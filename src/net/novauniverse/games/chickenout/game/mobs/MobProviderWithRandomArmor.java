package net.novauniverse.games.chickenout.game.mobs;

import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.RandomGenerator;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Creature;

import java.util.ArrayList;

public abstract class MobProviderWithRandomArmor implements ChickenOutMobProvider {
	private int timesYouCanRandomize = 0;

	public void setTimesYouCanRandomize(int timesYouCanRandomize) {
		this.timesYouCanRandomize = timesYouCanRandomize;
	}

	public int getTimesYouCanRandomize() {
		return timesYouCanRandomize;
	}

	public void armorRandomizer(Creature creature, ArmorMaterialType type) {
		this.armorRandomizer(creature, type, new ArrayList<Enchant>());
	}

	public void armorRandomizer(Creature creature, ArmorMaterialType type, ArrayList<Enchant> enchantments) {
		boolean hasHelmet = false;
		boolean hasChestplate = false;
		boolean hasLeggings = false;
		boolean hasBoots = false;

		for (int i = 0; i < timesYouCanRandomize; i++) {
			try {
				int random = RandomGenerator.generate(1, 4);
				if (random == 1) {
					if (!hasHelmet) {
						ItemBuilder item = new ItemBuilder(Material.valueOf(type + "_HELMET")).setUnbreakable(true);

						if (!(enchantments.isEmpty() || enchantments == null)) {
							enchantments.forEach((enchant -> item.addEnchant(enchant.getEnchantment(), enchant.getLevel(), false)));
						}

						creature.getEquipment().setHelmet(item.build());
						hasHelmet = true;
					}
				} else if (random == 2) {
					if (!hasChestplate) {
						ItemBuilder item = new ItemBuilder(Material.valueOf(type + "_CHESTPLATE")).setUnbreakable(true);

						if (!(enchantments.isEmpty() || enchantments == null))
							enchantments.forEach((enchant -> item.addEnchant(enchant.getEnchantment(), enchant.getLevel(), false)));

						creature.getEquipment().setHelmet(item.build());
						hasChestplate = true;

					}
				} else if (random == 3) {
					if (!hasLeggings) {
						ItemBuilder item = new ItemBuilder(Material.valueOf(type + "_LEGGINGS")).setUnbreakable(true);

						if (!(enchantments.isEmpty() || enchantments == null)) {
							enchantments.forEach((enchant -> item.addEnchant(enchant.getEnchantment(), enchant.getLevel(), false)));
						}

						creature.getEquipment().setHelmet(item.build());
						hasLeggings = true;

					}
				} else if (random == 4) {
					if (!hasBoots) {
						ItemBuilder item = new ItemBuilder(Material.valueOf(type + "_BOOTS")).setUnbreakable(true);

						if (!(enchantments.isEmpty() || enchantments == null)) {
							enchantments.forEach((enchant -> item.addEnchant(enchant.getEnchantment(), enchant.getLevel(), false)));
						}

						creature.getEquipment().setHelmet(item.build());
						hasBoots = true;
					}
				}
			} catch (Exception e) {
				Log.error("MobProviderWithRandomArmor", "An exception was thrown while generating armor: " + e.getClass().getName() + " " + e.getMessage());
			}
		}
	}
}
