package net.novauniverse.games.chickenout.game.mobs.implementation.level4.irongolem;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;

public interface ChickenOutIronGolemSpawner {
	public Creature spawnGolem(Location location, Player player);
}