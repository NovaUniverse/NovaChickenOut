package net.novauniverse.games.chickenout.game.mobs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.bukkit.plugin.Plugin;

import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.utils.ClassFinder;

public class ChickenOutMobRepo {
	private static List<ChickenOutMobProvider> providers = new ArrayList<>();

	public static List<ChickenOutMobProvider> getProviders() {
		return providers;
	}

	public static void clearProviders() {
		providers.clear();
	}

	public static void addProvider(ChickenOutMobProvider provider) {
		providers.add(provider);
	}

	public static void scanForProviders(Plugin plugin, String packagee) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Log.info("ChickenOutMobRepo", "Scanning for ChickenOutMobProvider in package " + packagee + " of plugin " + plugin.getName());

		Set<Class<?>> classes = ClassFinder.getClasses(FileUtils.toFile(plugin.getClass().getProtectionDomain().getCodeSource().getLocation()), packagee);
		for (Class<?> clazz : classes) {
			if (clazz.isInterface()) {
				continue;
			}

			if (clazz.isAnonymousClass()) {
				continue;
			}

			if (Modifier.isAbstract(clazz.getModifiers())) {
				continue;
			}

			if (ChickenOutMobProvider.class.isAssignableFrom(clazz)) {
				if (providers.stream().anyMatch(c -> c.getClass().getName().equalsIgnoreCase(clazz.getName()))) {
					Log.info("ChickenOutMobRepo", "Ignoring class " + clazz.getName() + "since its already loaded");
					continue;
				}
				ChickenOutMobProvider provider = (ChickenOutMobProvider) clazz.getConstructor().newInstance();
				providers.add(provider);
				Log.info("ChickenOutMobRepo", "Added level " + provider.getLevel() + " provider " + provider.getClass().getName());
			}
		}
	}
}