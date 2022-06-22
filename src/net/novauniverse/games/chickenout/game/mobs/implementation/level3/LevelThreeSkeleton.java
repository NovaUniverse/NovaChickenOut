package net.novauniverse.games.chickenout.game.mobs.implementation.level3;

import net.novauniverse.games.chickenout.game.mobs.ArmorMaterialType;
import net.novauniverse.games.chickenout.game.mobs.MobProviderWithRandomArmor;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LevelThreeSkeleton extends MobProviderWithRandomArmor {
	@Override
	public int getLevel() {
		return 3;
	}

	@Override
	public Creature spawn(Location location, Player player) {
		Creature creature = (Creature) location.getWorld().spawnEntity(location, EntityType.SKELETON);

		creature.getEquipment().clear();

		creature.setCustomName("Skeleton");
		creature.setCustomNameVisible(true);

		creature.getEquipment().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setAmount(1).setUnbreakable(true).build());
		setTimesYouCanRandomize(3);
		armorRandomizer(creature, ArmorMaterialType.IRON);
		ItemStack item = new ItemBuilder(Material.BOW).setUnbreakable(true).setAmount(1).addEnchant(Enchantment.ARROW_KNOCKBACK, 1).addEnchant(Enchantment.ARROW_DAMAGE, 1).build();
		VersionIndependentUtils.get().setCreatureItemInMainHand(creature, item);

		creature.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));

		return creature;
	}
}