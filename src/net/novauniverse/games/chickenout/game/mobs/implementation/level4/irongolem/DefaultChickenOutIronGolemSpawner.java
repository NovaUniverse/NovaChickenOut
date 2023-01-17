package net.novauniverse.games.chickenout.game.mobs.implementation.level4.irongolem;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class DefaultChickenOutIronGolemSpawner implements ChickenOutIronGolemSpawner {
	@Override
	public Creature spawnGolem(Location location, Player player) {
		return (Creature) location.getWorld().spawnEntity(location, EntityType.IRON_GOLEM);
	}
}