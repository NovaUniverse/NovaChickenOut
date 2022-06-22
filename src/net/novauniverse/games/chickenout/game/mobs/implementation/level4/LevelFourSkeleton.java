package net.novauniverse.games.chickenout.game.mobs.implementation.level4;

import net.novauniverse.games.chickenout.game.mobs.ArmorMaterialType;
import net.novauniverse.games.chickenout.game.mobs.Enchant;
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

import java.util.ArrayList;

public class LevelFourSkeleton extends MobProviderWithRandomArmor {
	@Override
	public int getLevel() {
		return 4;
	}

	@Override
	public Creature spawn(Location location, Player player) {
		Creature creature = (Creature) location.getWorld().spawnEntity(location, EntityType.SKELETON);

		creature.getEquipment().clear();

		creature.setCustomName("Skeleton");
		creature.setCustomNameVisible(true);
		ArrayList<Enchant> enchants = new ArrayList<>();
		enchants.add(new Enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1));

		creature.getEquipment().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setAmount(1).setUnbreakable(true).build());
		setTimesYouCanRandomize(4);
		armorRandomizer(creature, ArmorMaterialType.IRON, enchants);
		ItemStack item = new ItemBuilder(Material.BOW).setUnbreakable(true).setAmount(1).addEnchant(Enchantment.ARROW_FIRE, 1).build();
		VersionIndependentUtils.get().setCreatureItemInMainHand(creature, item);

		creature.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));
		return creature;
	}
}
