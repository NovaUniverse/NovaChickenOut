package net.novauniverse.games.chickenout.game.mobs.implementation.level2;

import net.novauniverse.games.chickenout.game.mobs.ArmorMaterialType;
import net.novauniverse.games.chickenout.game.mobs.MobProviderWithRandomArmor;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LevelTwoZombie extends MobProviderWithRandomArmor {
    @Override
    public int getLevel() {
        return 2;
    }

    @Override
    public Creature spawn(Location location, Player player) {
        Creature creature = (Creature) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);

        creature.getEquipment().clear();

        creature.setCustomName("Zombie");
        creature.setCustomNameVisible(true);
        ((Ageable) creature).setAdult();

        setTimesYouCanRandomize(2);
        armorRandomizer(creature, ArmorMaterialType.CHAINMAIL);
        creature.getEquipment().setItemInMainHand(new ItemBuilder(Material.WOODEN_SWORD).setUnbreakable(true).setAmount(1).build());

        creature.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE,0,false,false,false));

        return creature;
    }
}