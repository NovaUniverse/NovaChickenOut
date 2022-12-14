package net.novauniverse.games.chickenout;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONException;
import org.json.JSONObject;

import net.novauniverse.games.chickenout.game.ChickenOut;
import net.novauniverse.games.chickenout.game.config.ChickenOutConfig;
import net.novauniverse.games.chickenout.game.mobs.ChickenOutMobRepo;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.debug.DebugCommandRegistrator;
import net.zeeraa.novacore.spigot.debug.DebugTrigger;
import net.zeeraa.novacore.spigot.gameengine.NovaCoreGameEngine;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.map.mapmodule.MapModuleManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.mapselector.selectors.guivoteselector.GUIMapVote;
import net.zeeraa.novacore.spigot.gameengine.module.modules.gamelobby.GameLobby;
import net.zeeraa.novacore.spigot.module.ModuleManager;

public class NovaChickenOut extends JavaPlugin implements Listener {
	private static NovaChickenOut instance;

	private ChickenOut game;

	private boolean allowBarChange;

	public void allowBarChange(boolean val) {
		allowBarChange = val;
	}

	public boolean canBarChange() {
		return allowBarChange;
	}


	public static NovaChickenOut getInstance() {
		return instance;
	}

	public ChickenOut getGame() {
		return game;
	}

	@Override
	public void onEnable() {
		NovaChickenOut.instance = this;
		saveDefaultConfig();

		// Create files and folders
		File mapFolder = new File(this.getDataFolder().getPath() + File.separator + "Maps");
		File worldFolder = new File(this.getDataFolder().getPath() + File.separator + "Worlds");

		if (NovaCoreGameEngine.getInstance().getRequestedGameDataDirectory() != null) {
			mapFolder = new File(NovaCoreGameEngine.getInstance().getRequestedGameDataDirectory().getAbsolutePath() + File.separator + getName() + File.separator + "Maps");
			worldFolder = new File(NovaCoreGameEngine.getInstance().getRequestedGameDataDirectory().getAbsolutePath() + File.separator + getName() + File.separator + "Worlds");
		}

		File mapOverrides = new File(this.getDataFolder().getPath() + File.separator + "map_overrides.json");
		if (mapOverrides.exists()) {
			Log.info(getName(), "Trying to read map overrides file");
			try {
				JSONObject mapFiles = JSONFileUtils.readJSONObjectFromFile(mapOverrides);

				boolean relative = mapFiles.getBoolean("relative");

				mapFolder = new File((relative ? this.getDataFolder().getPath() + File.separator : "") + mapFiles.getString("maps_folder"));
				worldFolder = new File((relative ? this.getDataFolder().getPath() + File.separator : "") + mapFiles.getString("worlds_folder"));

				Log.info(getName(), "New paths:");
				Log.info(getName(), "Map folder: " + mapFolder.getAbsolutePath());
				Log.info(getName(), "World folder: " + worldFolder.getAbsolutePath());
			} catch (JSONException | IOException e) {
				e.printStackTrace();
				Log.error(getName(), "Failed to read map overrides from file " + mapOverrides.getAbsolutePath());
			}
		}

		try {
			FileUtils.forceMkdir(getDataFolder());
			FileUtils.forceMkdir(mapFolder);
			FileUtils.forceMkdir(worldFolder);
		} catch (IOException e) {
			e.printStackTrace();
			Log.fatal(getName(), "Failed to setup data directory");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		try {
			ChickenOutMobRepo.scanForProviders(this, this.getClass().getPackage().getName());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			Log.fatal(getName(), "Failed to load mob providers. " + e.getClass().getName() + " " + e.getMessage());
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		// Enable required modules
		ModuleManager.enable(GameManager.class);
		ModuleManager.enable(GameLobby.class);

		MapModuleManager.addMapModule("chickenout.config", ChickenOutConfig.class);

		// Init game and maps
		this.game = new ChickenOut(this);

		GameManager.getInstance().loadGame(game);

		GUIMapVote mapSelector = new GUIMapVote();

		GameManager.getInstance().setMapSelector(mapSelector);

		// Register events
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getServer().getPluginManager().registerEvents(mapSelector, this);

		// Read maps
		Log.info(getName(), "Loading maps from " + mapFolder.getPath());
		GameManager.getInstance().readMapsFromFolder(mapFolder, worldFolder);

		new BukkitRunnable() {
			@Override
			public void run() {
				Log.info("ChickenOut", ChickenOutMobRepo.getProviders().size() + " custom mobs loaded");
			}
		}.runTaskLater(this, 20L);

		DebugCommandRegistrator.getInstance().addDebugTrigger(new DebugTrigger() {
			@Override
			public void onExecute(CommandSender sender, String commandLabel, String[] args) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.RED + "Missing arg: level");
				} else {
					try {
						int level = Integer.parseInt(args[0]);
						game.setLevel(level);
						sender.sendMessage(ChatColor.GREEN + "Ok");
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Invalid level");
					}
				}
			}

			@Override
			public PermissionDefault getPermissionDefault() {
				return PermissionDefault.OP;
			}

			@Override
			public String getPermission() {
				return "novauniverse.debug.chickenout.setlevel";
			}

			@Override
			public String getName() {
				return "setlevel";
			}

			@Override
			public AllowedSenders getAllowedSenders() {
				return AllowedSenders.ALL;
			}
		});
	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll((Plugin) this);
	}

	@EventHandler
	public void onHungerLoss(FoodLevelChangeEvent e) {
			e.setCancelled(!allowBarChange);
	}

}