package net.novauniverse.games.chickenout.game.mobs.implementation;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import net.novauniverse.games.chickenout.game.mobs.ChickenOutMobProvider;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class Level1Zombie implements ChickenOutMobProvider {
	// Providers can't have a constructor that accepts arguments since that messes
	// with the reflection part of the plugin

	@Override
	public int getLevel() {
		return 1;
	}

	@Override
	public Creature spawn(Location location, Player player) {
		Creature creature = (Creature) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
		// [LVL 1] gets added by the plugin later
		creature.setCustomName("Zombie");
		creature.setCustomNameVisible(true);
		((Ageable) creature).setAdult();
		creature.getEquipment().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setAmount(1).build());

		// sometimes the zombies spawn with items and we don't want that
		creature.getEquipment().setItemInHand(ItemBuilder.AIR);
		creature.getEquipment().setChestplate(ItemBuilder.AIR);
		creature.getEquipment().setLeggings(ItemBuilder.AIR);
		creature.getEquipment().setBoots(ItemBuilder.AIR);

		return creature;
	}
}