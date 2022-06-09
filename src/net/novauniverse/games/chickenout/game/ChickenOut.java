package net.novauniverse.games.chickenout.game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.novauniverse.games.chickenout.NovaChickenOut;
import net.novauniverse.games.chickenout.game.config.ChickenOutConfig;
import net.novauniverse.games.chickenout.game.mobs.ChickenOutMobProvider;
import net.novauniverse.games.chickenout.game.mobs.ChickenOutMobRepo;
import net.novauniverse.games.chickenout.game.utils.WrappedChickenOutFeather;
import net.novauniverse.games.chickenout.game.utils.WrappedChickenOutMob;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentSound;
import net.zeeraa.novacore.spigot.abstraction.events.VersionIndependentPlayerPickUpItemEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameEndReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.MapGame;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerQuitEliminationAction;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import net.zeeraa.novacore.spigot.utils.LocationUtils;
import net.zeeraa.novacore.spigot.utils.PlayerUtils;
import net.zeeraa.novacore.spigot.utils.VectorArea;

public class ChickenOut extends MapGame implements Listener {
	private boolean started;
	private boolean ended;

	private Map<Team, Location> teamSpawnLocations;

	private List<WrappedChickenOutFeather> wrappedFeathers;
	private List<WrappedChickenOutMob> wrappedMobs;

	private static final int CHANCE_5_FEATHERS = 5;
	private static final int CHANCE_10_FEATHERS = 1;

	private static final double MOB_PLAYER_SPAWN_RADIUS = 10;

	private int roundTimeLeft;
	private int finalTimeLeft;

	private ChickenOutConfig config;

	private Task monitorTask;
	private Task particleTask;
	private Task roundTimer;
	private Task finalTimer;

	private Map<UUID, Integer> feathers;

	private int level;

	public ChickenOut(Plugin plugin) {
		super(plugin);

		started = false;
		ended = false;
		teamSpawnLocations = new HashMap<>();

		roundTimeLeft = 0;
		finalTimeLeft = 0;

		config = null;

		wrappedFeathers = new ArrayList<>();
		wrappedMobs = new ArrayList<>();

		feathers = new HashMap<UUID, Integer>();

		level = 1;

		finalTimer = new SimpleTask(plugin, new Runnable() {
			@Override
			public void run() {
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
					Bukkit.getServer().getOnlinePlayers().forEach(player -> {
						if (players.contains(player.getUniqueId())) {
							player.setGameMode(GameMode.SPECTATOR);
							player.getWorld().strikeLightning(player.getLocation());
						}
					});
					endGame(GameEndReason.TIME);
				}
			}
		}, 20L);

		roundTimer = new SimpleTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (roundTimeLeft > 0) {
					roundTimeLeft--;
				} else {
					roundTimeLeft = config.getLevelTime();
					level++;
					// TODO: Level up message
					Bukkit.getServer().broadcastMessage(ChatColor.RED + "Monsters will now spawn at level " + level);
					if (level >= config.getMaxLevel()) {
						Task.tryStopTask(roundTimer);
						Task.tryStartTask(finalTimer);
					}
				}
			}
		}, 20L);

		monitorTask = new SimpleTask(plugin, new Runnable() {
			@Override
			public void run() {
				wrappedFeathers.removeIf(w -> w.getItem().isDead());
				wrappedMobs.removeIf(w -> w.getEntity().isDead());
				Bukkit.getServer().getOnlinePlayers().forEach(player -> {
					player.setSaturation(0);
					player.setFoodLevel(20);
				});
				wrappedMobs.forEach(wm -> wm.updateMobTarget());
				spawnFeathers();
				spawnMobs();

				if (!hasEnded()) {
					if (players.size() == 0) {
						endGame(GameEndReason.ALL_FINISHED);
					}
				}
			}
		}, 20L);

		particleTask = new SimpleTask(plugin, new Runnable() {
			@Override
			public void run() {
				wrappedFeathers.forEach(f -> f.showParticles());
			}
		}, 5L);
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

	public void spawnFeathers() {
		if (started) {
			int toSpawn = config.getTargetFeatherCount() - wrappedFeathers.size();
			if (toSpawn > 0) {
				Log.trace(getName(), "Spawing " + toSpawn + " feathers");
				for (int i = 0; i < toSpawn; i++) {
					VectorArea area = config.getFeatherSpawnAreas().get(getRandom().nextInt(config.getFeatherSpawnAreas().size()));
					Vector randLoc = area.getRandomVectorWithin(getRandom());

					Block block = LocationUtils.getHighestBlockAtLocation(randLoc.toLocation(getWorld())); // getWorld().getHighestBlockAt(randLoc.getBlockX(), randLoc.getBlockZ());
					if (block != null) {
						Location location = LocationUtils.centerLocation(block.getLocation());
						location.add(0, 5, 0);

						int count = 1;
						Color color = Color.WHITE;
						if (getRandom().nextInt(100) < CHANCE_5_FEATHERS) {
							count = 5;
							color = Color.GREEN;
						}
						if (getRandom().nextInt(100) < CHANCE_10_FEATHERS) {
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
				for (int i = 0; i < toSpawn; i++) {
					List<ChickenOutMobProvider> providers = ChickenOutMobRepo.getProviders().stream().filter(mob -> mob.getLevel() == level).collect(Collectors.toList());
					if (providers.size() == 0) {
						Log.warn("ChickenOut", "No mob providers for level " + level + " was found");
						break;
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

		ChickenOutConfig cfg = (ChickenOutConfig) this.getActiveMap().getMapData().getMapModule(ChickenOutConfig.class);
		if (cfg == null) {
			Log.fatal("ChickenOut", "The map " + this.getActiveMap().getMapData().getMapName() + " has no ChickenOut config map module");
			Bukkit.getServer().broadcastMessage(ChatColor.RED + "ChickenOut has run into an uncorrectable error and has to be ended");
			this.endGame(GameEndReason.ERROR);
			return;
		}
		this.config = cfg;

		List<Player> toTeleport = new ArrayList<Player>();

		Bukkit.getServer().getOnlinePlayers().forEach(player -> {
			if (players.contains(player.getUniqueId())) {
				toTeleport.add(player);
			} else {
				tpToSpectator(player);
			}
		});

		Collections.shuffle(toTeleport, getRandom());

		List<Location> toUse = new ArrayList<Location>();
		while (toTeleport.size() > 0) {
			if (toUse.size() == 0) {
				for (Location location : getActiveMap().getStarterLocations()) {
					toUse.add(location);
				}

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

		spawnFeathers();

		roundTimeLeft = config.getLevelTime();
		finalTimeLeft = config.getFinalRoundTime();

		Task.tryStartTask(monitorTask);
		Task.tryStartTask(particleTask);
		Task.tryStartTask(roundTimer);

		started = true;
		sendBeginEvent();
	}

	@Override
	public void onEnd(GameEndReason reason) {
		if (ended) {
			return;
		}

		Task.tryStopTask(monitorTask);
		Task.tryStopTask(particleTask);
		Task.tryStopTask(roundTimer);
		Task.tryStopTask(finalTimer);

		ended = true;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerPickupItem(VersionIndependentPlayerPickUpItemEvent e) {
		Player player = e.getPlayer();
		Log.trace("ChickenOut", "Pick up item " + e.getItem().getItemStack().getType().name());
		if (e.getItem().getItemStack().getType() == Material.FEATHER) {
			e.setCancelled(true);

			if (players.contains(player.getUniqueId())) {
				addFeathers(player, e.getItem().getItemStack().getAmount());
				VersionIndependentSound.ITEM_PICKUP.playAtLocation(player.getLocation());
				e.getItem().remove();
			}
		}
	}
}