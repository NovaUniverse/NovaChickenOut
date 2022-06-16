package net.novauniverse.games.chickenout.game.mobs;

import net.zeeraa.novacore.commons.utils.RandomGenerator;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Creature;


import java.util.ArrayList;

public abstract class MobProviderWithRandomArmor implements ChickenOutMobProvider {

    boolean hasHelmet = false;
    boolean hasChestplate = false;
    boolean hasLeggings = false;
    boolean hasBoots = false;
    int timesYouCanRandomize = 0;
    int timesRandomized = 0;
    public void setTimesYouCanRandomize(int timesYouCanRandomize) {
        this.timesYouCanRandomize = timesYouCanRandomize;
    }
    public int getTimesYouCanRandomize() {
        return timesYouCanRandomize;
    }
    public int getTimesRandomized() {
        return timesRandomized;
    }
    public void setTimesRandomized(int timesRandomized) {
        this.timesRandomized = timesRandomized;
    }
    public void armorRandomizer(Creature creature, ArmorMaterialType type) {
        armorRandomizer(creature,type, new ArrayList<Enchant>());
    }
    public void armorRandomizer(Creature creature, ArmorMaterialType type, ArrayList<Enchant> enchantments) {
        int random = RandomGenerator.generate(1,4);
        if (getTimesRandomized() == getTimesYouCanRandomize())
             return;

        if (random == 1) {
            if (!hasHelmet) {
                ItemBuilder item = new ItemBuilder(Material.valueOf(type + "_HELMET")).setUnbreakable(true);

                if (!(enchantments.isEmpty() || enchantments == null))
                    enchantments.forEach((enchant -> item.addEnchant(enchant.getEnchantment(),enchant.getLevel(),false)));

                creature.getEquipment().setHelmet(item.build());
                setTimesRandomized(getTimesRandomized() + 1);
                hasBoots = true;



            }
        } else if (random == 2) {
            if (!hasChestplate) {
                ItemBuilder item = new ItemBuilder(Material.valueOf(type + "_CHESTPLATE")).setUnbreakable(true);

                if (!(enchantments.isEmpty() || enchantments == null))
                    enchantments.forEach((enchant -> item.addEnchant(enchant.getEnchantment(),enchant.getLevel(),false)));

                creature.getEquipment().setHelmet(item.build());
                setTimesRandomized(getTimesRandomized() + 1);
                hasChestplate = true;

            }
        }else if (random == 3) {
            if (!hasLeggings) {
                ItemBuilder item = new ItemBuilder(Material.valueOf(type + "_LEGGINGS")).setUnbreakable(true);

                if (!(enchantments.isEmpty() || enchantments == null))
                    enchantments.forEach((enchant -> item.addEnchant(enchant.getEnchantment(),enchant.getLevel(),false)));

                creature.getEquipment().setHelmet(item.build());
                setTimesRandomized(getTimesRandomized() + 1);
                hasLeggings = true;

            }
        } else if (random == 4) {
            if (!hasBoots) {
                ItemBuilder item = new ItemBuilder(Material.valueOf(type + "_BOOTS")).setUnbreakable(true);

                if (!(enchantments.isEmpty() || enchantments == null))
                    enchantments.forEach((enchant -> item.addEnchant(enchant.getEnchantment(),enchant.getLevel(),false)));

                creature.getEquipment().setHelmet(item.build());
                setTimesRandomized(getTimesRandomized() + 1);
                hasBoots = true;

            }
        }
        armorRandomizer(creature, type);
    }
}
