package net.novauniverse.games.chickenout.game;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.games.chickenout.NovaChickenOut;
import net.novauniverse.games.chickenout.game.config.ChickenOutConfig;
import net.novauniverse.games.chickenout.game.event.*;
import net.novauniverse.games.chickenout.game.mobs.ChickenOutMobProvider;
import net.novauniverse.games.chickenout.game.mobs.ChickenOutMobRepo;
import net.novauniverse.games.chickenout.game.utils.TeamScoreEntry;
import net.novauniverse.games.chickenout.game.utils.WrappedChickenOutFeather;
import net.novauniverse.games.chickenout.game.utils.WrappedChickenOutMob;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.Callback;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentMaterial;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentSound;
import net.zeeraa.novacore.spigot.abstraction.events.VersionIndependentPlayerAchievementAwardedEvent;
import net.zeeraa.novacore.spigot.abstraction.events.VersionIndependentPlayerPickUpItemEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameEndReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.MapGame;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerQuitEliminationAction;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.compass.CompassTracker;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;
import net.zeeraa.novacore.spigot.utils.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.awt.Color;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class ChickenOut extends MapGame implements Listener {
	private boolean started;
	private boolean ended;

	private Map<Team, Location> teamSpawnLocations;

	private List<WrappedChickenOutFeather> wrappedFeathers;
	private List<WrappedChickenOutMob> wrappedMobs;

	private List<Location> lightningLocations;

	public static final int CHANCE_5_FEATHERS = 5;
	public static final int CHANCE_10_FEATHERS = 1;

	public static final double MOB_PLAYER_SPAWN_RADIUS = 16;

	public static final int START_COUNTDOWN = 10;

	private Map<Team, Integer> teamFinalScore;
	private Map<UUID, Integer> playerFinalScore;

	private List<UUID> fullPlayerList;

	private int roundTimeLeft;
	private int finalTimeLeft;

	private ChickenOutConfig config;

	private Task monitorTask;
	private Task particleTask;
	private Task roundTimer;
	private Task finalTimer;
	private Task stuckCheckTimer;
	private Task chickenOutTask;
	private Task speedFixTask;
	private Task actionBarTask;

	private Task beginCountdown;

	private int beginCountdownValue;
	private boolean countdownOver;

	private ChickenOutCountdownType countdownType;

	private List<Callback> timerDecrementCallbacks;
	private List<Callback> levelChangeCallbacks;

	private Map<UUID, Integer> feathers;

	private int level;

	public ChickenOut(Plugin plugin) {
		super(plugin);

		started = false;
		ended = false;
		teamSpawnLocations = new HashMap<>();

		roundTimeLeft = 0;
		finalTimeLeft = 0;

		countdownOver = false;
		beginCountdownValue = START_COUNTDOWN;

		config = null;

		wrappedFeathers = new ArrayList<>();
		wrappedMobs = new ArrayList<>();

		lightningLocations = new ArrayList<>();

		timerDecrementCallbacks = new ArrayList<>();
		levelChangeCallbacks = new ArrayList<>();

		feathers = new HashMap<>();

		teamFinalScore = new HashMap<>();
		playerFinalScore = new HashMap<>();

		fullPlayerList = new ArrayList<>();

		level = 1;

		finalTimer = new SimpleTask(plugin, () -> {
			countdownType = ChickenOutCountdownType.FINAL;
			if (finalTimeLeft > 0) {
				finalTimeLeft--;

				if (finalTimeLeft == 30 || finalTimeLeft == 60) {
					Bukkit.getServer().broadcastMessage(ChatColor.RED + "" + finalTimeLeft + " seconds left " + TextUtils.ICON_WARNING);
					Bukkit.getServer().getOnlinePlayers().forEach(player -> {
						VersionIndependentSound.NOTE_PLING.play(player);
						VersionIndependentUtils.get().sendTitle(player, "", ChatColor.RED + TextUtils.ICON_WARNING + " " + finalTimeLeft + " seconds left " + TextUtils.ICON_WARNING, 0, 40, 10);
					});
				}

				if (finalTimeLeft <= 10) {
					Bukkit.getServer().getOnlinePlayers().forEach(player -> {
						VersionIndependentSound.NOTE_PLING.play(player);
						VersionIndependentUtils.get().sendTitle(player, "", ChatColor.RED + TextUtils.ICON_WARNING + " " + finalTimeLeft + " second" + (finalTimeLeft == 1 ? "" : "s") + " left " + TextUtils.ICON_WARNING, 0, 20, 10);
					});
				}
			} else {
				Task.tryStopTask(finalTimer);
				Bukkit.getServer().getOnlinePlayers().stream().filter(player -> players.contains(player.getUniqueId())).forEach(player -> {
					player.setGameMode(GameMode.SPECTATOR);
					player.getWorld().strikeLightning(player.getLocation());
				});
				endGame(GameEndReason.TIME);
			}

			timerDecrementCallbacks.forEach(callback -> callback.execute());

			if (hasActiveMap()) {
				Bukkit.getServer().getOnlinePlayers().forEach(player -> player.setCompassTarget(config.getChickenOutAreaCenter()));
			}
		}, 20L);

		chickenOutTask = new SimpleTask(plugin, new Runnable() {
			@Override
			public void run() {
				// Remove mobs in chicken out area
				wrappedMobs.stream().filter(m -> config.getChickenOutArea().isInside(m.getEntity().getLocation().toVector())).forEach(m -> m.getEntity().remove());

				// Remove if player is eliminated
				wrappedMobs.stream().filter(m -> !players.contains(m.getTarget())).forEach(m -> m.getEntity().remove());

				Bukkit.getServer().getOnlinePlayers().stream().filter(player -> players.contains(player.getUniqueId())).filter(player -> config.getChickenOutArea().isInside(player.getLocation().toVector())).forEach(player -> {
					int score = getPlayerFeathers(player);
					if (TeamManager.hasTeamManager()) {
						Team team = TeamManager.getTeamManager().getPlayerTeam(player);
						if (team != null) {
							if (teamFinalScore.containsKey(team)) {
								score += teamFinalScore.get(team);
							}
							teamFinalScore.put(team, score);
						}
					} else {
						playerFinalScore.put(player.getUniqueId(), score);
					}

					feathers.remove(player.getUniqueId());

					net.md_5.bungee.api.ChatColor color = net.md_5.bungee.api.ChatColor.AQUA;
					if (TeamManager.hasTeamManager()) {
						Team team = TeamManager.getTeamManager().getPlayerTeam(player);
						if (team != null) {
							color = team.getTeamColor();
						}
					}

					Bukkit.getServer().broadcastMessage(color + "" + ChatColor.BOLD + player.getName() + ChatColor.GOLD + ChatColor.BOLD + " chickened out");
					player.setGameMode(GameMode.SPECTATOR);
					tpToSpectator(player);
					VersionIndependentSound.WITHER_HURT.play(player);
					Firework firework = (Firework) world.spawnEntity(config.getChickenOutAreaCenter(), EntityType.FIREWORK);
					FireworkMeta meta = firework.getFireworkMeta();
					meta.setPower(1);
					meta.addEffect(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(org.bukkit.Color.WHITE).build());
					firework.setFireworkMeta(meta);
					players.remove(player.getUniqueId());

					Event event = new ChickenOutPlayerChickenOutEvent(player, score);
					Bukkit.getServer().getPluginManager().callEvent(event);

					// eliminatePlayer(player, null, PlayerEliminationReason.OTHER);
				});
			}
		}, 2L);

		speedFixTask = new SimpleTask(getPlugin(), new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.getServer().getOnlinePlayers().stream().filter(player -> player.hasPotionEffect(PotionEffectType.SPEED)).forEach(player -> player.removePotionEffect(PotionEffectType.SPEED));
			}
		}, 1L);
		levelChangeCallbacks.add(() -> {
			Bukkit.getPluginManager().callEvent(new ChickenOutPhaseChangeEvent(level));
			roundTimeLeft = config.getLevelTime();
			Bukkit.getServer().getOnlinePlayers().forEach(player -> VersionIndependentUtils.get().sendTitle(player, ChatColor.RED + "Level " + level, "", 10, 40, 10));
			Bukkit.getServer().broadcastMessage(ChatColor.RED + "Monsters will now spawn at level " + level);
			if (level >= config.getMaxLevel()) {
				Task.tryStopTask(roundTimer);
				Task.tryStartTask(finalTimer);
			}
		});
		roundTimer = new SimpleTask(plugin, () -> {
			countdownType = ChickenOutCountdownType.ROUND;
			if (roundTimeLeft > 0) {
				roundTimeLeft--;
			} else {
				incrementLevel();
			}

			// Handle mob removal time
			wrappedMobs.stream().filter(w -> w.getLevel() != level).forEach(WrappedChickenOutMob::decrementRemovalTimer);
			wrappedMobs.stream().filter(w -> w.getTimeUntilRemoval() <= 0).forEach(w -> w.getEntity().remove());

			// Callbacks
			timerDecrementCallbacks.forEach(Callback::execute);
		}, 20L);

		monitorTask = new SimpleTask(plugin, () -> {
			// Compass target
			Bukkit.getServer().getOnlinePlayers().stream().filter(p -> p.getWorld() == getWorld()).forEach(player -> player.setCompassTarget(config.getChickenOutAreaCenter()));

			// Remove dead entity wrappers
			wrappedFeathers.removeIf(w -> w.getItem().isDead());
			wrappedMobs.removeIf(w -> w.getEntity().isDead());

			// Player food
			Bukkit.getServer().getOnlinePlayers().forEach(player -> {
				player.setSaturation(0);
				player.setFoodLevel(20);
			});

			// Update targets
			wrappedMobs.forEach(wm -> wm.updateMobTarget());

			// Spawn feathers and mobs
			if (countdownOver) {
				spawnFeathers();
				spawnMobs();
			}

			// End game
			if (!hasEnded()) {
				if (players.size() == 0) {
					endGame(GameEndReason.ALL_FINISHED);
				}
			}
		}, 20L);

		stuckCheckTimer = new SimpleTask(plugin, new Runnable() {
			@Override
			public void run() {
				wrappedMobs.forEach(mob -> mob.stuckCheck());
			}
		}, 200L);

		particleTask = new SimpleTask(plugin, new Runnable() {
			@Override
			public void run() {
				wrappedFeathers.forEach(f -> f.showParticles());
			}
		}, 5L);

		beginCountdown = new SimpleTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (beginCountdownValue > 0) {
					VersionIndependentSound.NOTE_PLING.broadcast(1.0F, 1.0F);
					Bukkit.getServer().getOnlinePlayers().forEach(player -> VersionIndependentUtils.get().sendTitle(player, ChatColor.AQUA + "" + beginCountdownValue, "", 0, 20, 0));
					beginCountdownValue--;
				} else {
					VersionIndependentSound.NOTE_PLING.broadcast(1.0F, 1.25F);
					Bukkit.getServer().getOnlinePlayers().forEach(player -> VersionIndependentUtils.get().sendTitle(player, ChatColor.AQUA + "GO", "", 0, 20, 5));
					sendBeginEvent();
					countdownOver = true;
					Task.tryStopTask(beginCountdown);
				}
			}
		}, 20L);

		actionBarTask = new SimpleTask(plugin, new Runnable() {
			@Override
			public void run() {
				Bukkit.getServer().getOnlinePlayers().stream().filter(player -> feathers.containsKey(player.getUniqueId()) && player.getGameMode() != GameMode.SPECTATOR).forEach(player -> {
					int featherCount = feathers.get(player.getUniqueId());
					VersionIndependentUtils.get().sendActionBarMessage(player, ChatColor.GREEN + "" + featherCount + " feather" + (featherCount == 1 ? "" : "s") + " collected");
				});
			}
		}, 10L);
	}

	public ChickenOutCountdownType getCountdownType() {
		return countdownType;
	}

	public void addTimerDecrementCallback(Callback callback) {
		timerDecrementCallbacks.add(callback);
	}

	public void addLevelChangeCallback(Callback callback) {
		levelChangeCallbacks.add(callback);
	}

	public int getRoundTimeLeft() {
		return roundTimeLeft;
	}

	public int getFinalTimeLeft() {
		return finalTimeLeft;
	}

	public boolean isRoundTimerRunning() {
		return roundTimer.isRunning();
	}

	public boolean isFinalTimerRunning() {
		return finalTimer.isRunning();
	}

	public int getLevel() {
		return level;
	}

	public void incrementLevel() {
		setLevel(level + 1);
	}



	public void setLevel(int level) {
		this.level = level;
		levelChangeCallbacks.forEach(Callback::execute);
	}

	public int getFinalPlayerScore(UUID uuid) {
		if (playerFinalScore.containsKey(uuid)) {
			return playerFinalScore.get(uuid);
		}
		return 0;
	}

	public int getFinalTeamScore(Team team) {
		if (teamFinalScore.containsKey(team)) {
			return teamFinalScore.get(team);
		}
		return 0;
	}

	public int getFinalScoreForDisplay(UUID uuid) {
		if (TeamManager.hasTeamManager()) {
			Team team = TeamManager.getTeamManager().getPlayerTeam(uuid);
			if (team != null) {
				return getFinalTeamScore(team);
			}
		} else {
			return getFinalPlayerScore(uuid);
		}

		return 0;
	}

	public void spawnFeathers() {
		if (started) {
			int toSpawn = config.getTargetFeatherCount() - wrappedFeathers.size();
			if (toSpawn > 0) {
				// Log.trace(getName(), "Spawing " + toSpawn + " feathers");
				for (int i = 0; i < toSpawn; i++) {
					VectorArea area = config.getFeatherSpawnAreas().get(getRandom().nextInt(config.getFeatherSpawnAreas().size()));
					Vector randLoc = area.getRandomVectorWithin(getRandom());

					Block block = LocationUtils.getHighestBlockAtLocation(randLoc.toLocation(getWorld())); // getWorld().getHighestBlockAt(randLoc.getBlockX(), randLoc.getBlockZ());
					if (block != null) {
						Location location = LocationUtils.centerLocation(block.getLocation());
						location.add(0, 5, 0);

						int count = 1;
						int r = getRandom().nextInt(100);
						Color color = Color.WHITE;
						if (r < CHANCE_5_FEATHERS) {
							count = 5;
							color = Color.GREEN;
						}
						if (r < CHANCE_10_FEATHERS) {
							count = 10;
							color = Color.RED;
						}

						Item feather = getWorld().dropItem(location, new ItemBuilder(Material.FEATHER).setAmount(count).build());
						wrappedFeathers.add(new WrappedChickenOutFeather(feather, color));
					}
				}
			}
		}
	}

	public void spawnMobs() {
		Bukkit.getServer().getOnlinePlayers().forEach(player -> {
			if (isPlayerInGame(player)) {
				int existing = wrappedMobs.stream().filter(mob -> mob.getTarget().toString().equalsIgnoreCase(player.getUniqueId().toString())).toArray().length;
				int toSpawn = config.getTargetMobCount() - existing;
				if (toSpawn > 0) {
					List<ChickenOutMobProvider> providers = ChickenOutMobRepo.getProviders().stream().filter(mob -> mob.getLevel() == level).collect(Collectors.toList());
					if (providers.size() == 0) {
						Log.warn("ChickenOut", "No mob providers for level " + level + " was found");
						return;
					}

					Location originPoint = player.getLocation();

					Vector rotation = new Vector((random.nextDouble() * 2.0) - 1.0, 0, (random.nextDouble() * 2.0) - 1.0);

					Location randomLocation = originPoint.add(rotation.normalize().multiply(MOB_PLAYER_SPAWN_RADIUS));
					// LocationUtils.getRandomLocationWithRadiusFromCenter(player.getLocation(),
					// MOB_PLAYER_SPAWN_RADIUS, getRandom(), false, MOB_PLAYER_SPAWN_RADIUS);
					int y = LocationUtils.getHighestYAtLocation(randomLocation);
					randomLocation.setY(y + 3);

					ChickenOutMobProvider provider = providers.get(getRandom().nextInt(providers.size()));
					Creature creature = provider.spawn(randomLocation, player);
					creature.setCustomName("[LVL " + provider.getLevel() + "] " + creature.getCustomName());
					WrappedChickenOutMob wrappedMob = new WrappedChickenOutMob(creature, player.getUniqueId(), provider.getLevel());
					wrappedMob.updateMobTarget();
					wrappedMobs.add(wrappedMob);
				}
			}
		});
	}

	public int getPlayerFeathers(Player player) {
		if (feathers.containsKey(player.getUniqueId())) {
			return feathers.get(player.getUniqueId());
		}
		return 0;
	}

	public void addFeathers(Player player, int feathers) {
		Integer newValue = feathers;
		if (this.feathers.containsKey(player.getUniqueId())) {
			newValue += this.feathers.get(player.getUniqueId());
		}
		this.feathers.put(player.getUniqueId(), newValue);
	}

	@Override
	public String getName() {
		return "chickenout";
	}

	@Override
	public String getDisplayName() {
		return "Chicken Out";
	}

	@Override
	public PlayerQuitEliminationAction getPlayerQuitEliminationAction() {
		return PlayerQuitEliminationAction.DELAYED;
	}

	@Override
	public boolean eliminatePlayerOnDeath(Player player) {
		return true;
	}

	@Override
	public boolean isPVPEnabled() {
		return false;
	}

	@Override
	public boolean autoEndGame() {
		return false;
	}

	@Override
	public boolean hasStarted() {
		return started;
	}

	@Override
	public boolean hasEnded() {
		return ended;
	}

	@Override
	public boolean isFriendlyFireAllowed() {
		return false;
	}

	@Override
	public boolean canAttack(LivingEntity attacker, LivingEntity target) {
		return true;
	}

	public void tpToSpectator(Player player) {
		NovaCore.getInstance().getVersionIndependentUtils().resetEntityMaxHealth(player);
		player.setHealth(20);
		player.setGameMode(GameMode.SPECTATOR);
		player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 0, false, false));
		if (hasActiveMap()) {
			player.teleport(getActiveMap().getSpectatorLocation());
		}
	}

	/**
	 * Teleport a player to a provided start location
	 * 
	 * @param player   {@link Player} to teleport
	 * @param location {@link Location} to teleport the player to
	 */
	protected void tpToArena(Player player, Location location) {
		player.teleport(location.getWorld().getSpawnLocation());
		PlayerUtils.clearPlayerInventory(player);
		PlayerUtils.clearPotionEffects(player);
		PlayerUtils.resetPlayerXP(player);
		player.setHealth(player.getMaxHealth());
		player.setSaturation(20);
		player.setFoodLevel(20);
		player.setGameMode(GameMode.SURVIVAL);
		player.teleport(location);

		Team team = null;
		if (TeamManager.hasTeamManager()) {
			team = TeamManager.getTeamManager().getPlayerTeam(player);
		}

		ItemBuilder swordBuilder = new ItemBuilder(VersionIndependentMaterial.WOODEN_SWORD);
		swordBuilder.setUnbreakable(true);

		ItemBuilder compassBuilder = new ItemBuilder(Material.COMPASS);
		compassBuilder.setName(ChatColor.GOLD + "Stash feathers");

		ItemBuilder chestplateBuilder = new ItemBuilder(Material.LEATHER_CHESTPLATE);
		chestplateBuilder.setUnbreakable(true);
		if (team != null) {
			chestplateBuilder.setLeatherArmorColor(ChatColorRGBMapper.chatColorToRGBColorData(team.getTeamColor()).toBukkitColor());
		}

		player.getInventory().addItem(swordBuilder.build());
		player.getInventory().addItem(compassBuilder.build());
		player.getInventory().setChestplate(chestplateBuilder.build());

		new BukkitRunnable() {
			@Override
			public void run() {
				player.teleport(location);
			}
		}.runTaskLater(NovaChickenOut.getInstance(), 10L);
	}

	@Override
	public void onStart() {
		if (started) {
			return;
		}

		ModuleManager.disable(CompassTracker.class);

		ChickenOutConfig cfg = (ChickenOutConfig) this.getActiveMap().getMapData().getMapModule(ChickenOutConfig.class);
		if (cfg == null) {
			Log.fatal("ChickenOut", "The map " + this.getActiveMap().getMapData().getMapName() + " has no ChickenOut config map module");
			Bukkit.getServer().broadcastMessage(ChatColor.RED + "ChickenOut has run into an uncorrectable error and has to be ended");
			this.endGame(GameEndReason.ERROR);
			return;
		}
		this.config = cfg;

		fullPlayerList = new ArrayList<>(players);

		List<Player> toTeleport = new ArrayList<Player>();

		Bukkit.getServer().getOnlinePlayers().forEach(player -> {
			if (players.contains(player.getUniqueId())) {
				toTeleport.add(player);
			} else {
				tpToSpectator(player);
			}
		});

		Collections.shuffle(toTeleport, getRandom());

		List<Location> toUse = new ArrayList<>();
		while (toTeleport.size() > 0) {
			if (toUse.size() == 0) {
				toUse.addAll(getActiveMap().getStarterLocations());

				Collections.shuffle(toUse, getRandom());
			}

			if (toUse.size() == 0) {
				// Could not load spawn locations. break out to prevent server from crashing
				Log.fatal("ChickenOut", "The map " + this.getActiveMap().getMapData().getMapName() + " has no spawn locations. Ending game to prevent crash");
				Bukkit.getServer().broadcastMessage(ChatColor.RED + "ChickenOut has run into an uncorrectable error and has to be ended");
				this.endGame(GameEndReason.ERROR);
				return;
			}

			Player player = toTeleport.remove(0);

			Location location = null;
			if (TeamManager.hasTeamManager()) {
				Team team = TeamManager.getTeamManager().getPlayerTeam(player);
				if (team != null) {
					if (teamSpawnLocations.containsKey(team)) {
						location = teamSpawnLocations.get(team);
					} else {
						location = toUse.remove(0);
						teamSpawnLocations.put(team, location);
					}
				}
			}

			if (location == null) {
				location = toUse.remove(0);
			}

			tpToArena(player, location);
		}

		Bukkit.getServer().getWorlds().forEach(world -> {
			VersionIndependentUtils.get().setGameRule(world, "announceAdvancements", "false");
			VersionIndependentUtils.get().setGameRule(world, "keepInventory", "true");
		});

		roundTimeLeft = config.getLevelTime();
		finalTimeLeft = config.getFinalRoundTime();

		Task.tryStartTask(monitorTask);
		Task.tryStartTask(particleTask);
		Task.tryStartTask(roundTimer);
		Task.tryStartTask(chickenOutTask);
		Task.tryStartTask(stuckCheckTimer);
		Task.tryStartTask(speedFixTask);
		Task.tryStartTask(actionBarTask);

		started = true;

		Task.tryStartTask(beginCountdown);

		Bukkit.getServer().getOnlinePlayers().forEach(player -> {
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, (START_COUNTDOWN * 20) + 40, 0));
		});

		players.forEach(uuid -> feathers.put(uuid, 0));

		getWorld().setTime(6000);
		VersionIndependentUtils.get().setGameRule(getWorld(), "doDaylightCycle", "false");

		getConfig().getLightningLocations().forEach(l -> lightningLocations.add(l.toBukkitLocation(getWorld())));
	}

	public ChickenOutConfig getConfig() {
		return config;
	}

	@Override
	public void onEnd(GameEndReason reason) {
		if (ended) {
			return;
		}

		// We use for loops here since we need to index value
		if (TeamManager.hasTeamManager()) {
			List<Entry<Team, Integer>> entries = new ArrayList<>(teamFinalScore.entrySet());
			entries.sort(Entry.comparingByValue());
			Collections.reverse(entries);

			for (int i = 0; i < entries.size(); i++) {
				Entry<Team, Integer> entry = entries.get(i);
				AbstractChickenOutPlacementEvent event = new ChickenOutTeamPlacementEvent(entry.getValue(), i + 1, entry.getKey());
				Log.trace("ChickenOut", "Calling ChickenOutTeamPlacementEvent");
				Bukkit.getServer().getPluginManager().callEvent(event);
			}
		} else {
			List<Entry<UUID, Integer>> entries = new ArrayList<>(playerFinalScore.entrySet());
			entries.sort(Entry.comparingByValue());
			Collections.reverse(entries);

			for (int i = 0; i < entries.size(); i++) {
				Entry<UUID, Integer> entry = entries.get(i);
				AbstractChickenOutPlacementEvent event = new ChickenOutPlayerPlacementEvent(entry.getValue(), i + 1, entry.getKey());
				Log.trace("ChickenOut", "Calling ChickenOutPlayerPlacementEvent");
				Bukkit.getServer().getPluginManager().callEvent(event);
			}
		}

		try {
			List<Entry<UUID, Integer>> list;
			if (TeamManager.hasTeamManager()) {
				list = new ArrayList<>();
				teamFinalScore.forEach((team, score) -> {
					list.add(new TeamScoreEntry(team.getTeamUuid(), score));
				});
			} else {
				list = new ArrayList<>(playerFinalScore.entrySet());
			}

			// No placement if you dont get any score
			list.removeIf(e -> e.getValue() == 0);

			list.sort(Entry.comparingByValue());
			Collections.reverse(list);

			int maxEntries = 5;
			int max = (list.size() > maxEntries) ? maxEntries : list.size();
			Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "-- Top " + max + (TeamManager.hasTeamManager() ? " teams" : " players") + " --");
			for (int i = 0; i < max; i++) {
				Entry<UUID, Integer> entry = list.get(i);
				String name = "MissingNo";
				ChatColor color = ChatColor.AQUA;
				if (TeamManager.hasTeamManager()) {
					Team team = TeamManager.getTeamManager().getTeamByTeamUUID(entry.getKey());
					name = team.getDisplayName();
					color = team.getTeamColor();
				} else {
					OfflinePlayer player = Bukkit.getServer().getOfflinePlayer(entry.getKey());
					name = player.getName();
				}
				Bukkit.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + TextUtils.ordinal(i + 1) + " place: " + color + ChatColor.BOLD + name + ChatColor.AQUA + ChatColor.BOLD + " with " + entry.getValue() + " feathers collected");
			}
		} catch (Exception e) {
			Bukkit.getServer().broadcastMessage(org.bukkit.ChatColor.DARK_RED + "An error occured while displaying the top list. Dont worry, your score will still be applied correctly");
			Log.error("ChickenOut", "Failed to display final score. " + e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
		}

		Task.tryStopTask(monitorTask);
		Task.tryStopTask(particleTask);
		Task.tryStopTask(roundTimer);
		Task.tryStopTask(finalTimer);
		Task.tryStopTask(chickenOutTask);
		Task.tryStopTask(stuckCheckTimer);
		Task.tryStopTask(speedFixTask);
		Task.tryStopTask(actionBarTask);



		getActiveMap().getStarterLocations().forEach(location -> {
			Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
			FireworkMeta fwm = fw.getFireworkMeta();

			fwm.setPower(2);
			fwm.addEffect(RandomFireworkEffect.randomFireworkEffect());

			if (random.nextBoolean()) {
				fwm.addEffect(RandomFireworkEffect.randomFireworkEffect());
			}

			fw.setFireworkMeta(fwm);
		});

		Bukkit.getServer().getOnlinePlayers().forEach(player -> {
			VersionIndependentUtils.get().resetEntityMaxHealth(player);
			player.setFoodLevel(20);
			PlayerUtils.clearPlayerInventory(player);
			PlayerUtils.resetPlayerXP(player);
			player.setGameMode(GameMode.SPECTATOR);
		});

		ended = true;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(PlayerRespawnEvent e) {
		if (hasStarted()) {
			if (!players.contains(e.getPlayer().getUniqueId())) {
				new BukkitRunnable() {
					@Override
					public void run() {
						tpToSpectator(e.getPlayer());
					}
				}.runTaskLater(getPlugin(), 2L);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent e) {
		if (wrappedMobs.stream().anyMatch(w -> w.getEntity().getUniqueId().toString().equalsIgnoreCase(e.getEntity().getUniqueId().toString()))) {
			e.setDroppedExp(0);
			e.getDrops().clear();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent e) {
		e.setKeepInventory(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			if (hasActiveMap()) {
				if (started) {
					if (config.getChickenOutArea().isInside(e.getEntity().getLocation().toVector())) {
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent e) {
		if (started && !ended && !countdownOver) {
			Location to = e.getFrom().clone();

			to.setYaw(e.getTo().getYaw());
			to.setPitch(e.getTo().getPitch());

			e.setTo(to);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent e) {
		e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Firework) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerPickupItem(VersionIndependentPlayerPickUpItemEvent e) {
		Player player = e.getPlayer();
		// Log.trace("ChickenOut", "Pick up item " +
		// e.getItem().getItemStack().getType().name());
		if (e.getItem().getItemStack().getType() == Material.FEATHER) {
			e.setCancelled(true);

			if (players.contains(player.getUniqueId())) {
				addFeathers(player, e.getItem().getItemStack().getAmount());
				VersionIndependentSound.ITEM_PICKUP.playAtLocation(player.getLocation());
				e.getItem().remove();
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onVersionIndependentPlayerAchievementAwarded(VersionIndependentPlayerAchievementAwardedEvent e) {
		e.setCancelled(true);
	}

	public List<UUID> getAllParticipatingPlayers() {
		return fullPlayerList;
	}
}