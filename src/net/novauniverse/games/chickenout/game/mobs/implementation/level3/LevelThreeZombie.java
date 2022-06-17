package net.novauniverse.games.chickenout.game.mobs.implementation.level3;

import net.novauniverse.games.chickenout.game.mobs.ArmorMaterialType;
import net.novauniverse.games.chickenout.game.mobs.MobProviderWithRandomArmor;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LevelThreeZombie extends MobProviderWithRandomArmor {
	@Override
	public int getLevel() {
		return 3;
	}

	@Override
	public Creature spawn(Location location, Player player) {
		Creature creature = (Creature) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);

		creature.getEquipment().clear();

		creature.setCustomName("Zombie");
		creature.setCustomNameVisible(true);
		((Ageable) creature).setAdult();

		setTimesYouCanRandomize(3);
		armorRandomizer(creature, ArmorMaterialType.IRON);
		ItemStack item = new ItemBuilder(Material.STONE_SWORD).setUnbreakable(true).setAmount(1).build();
		VersionIndependentUtils.get().setCreatureItemInMainHand(creature, item);

		creature.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));

		return creature;
	}
}
