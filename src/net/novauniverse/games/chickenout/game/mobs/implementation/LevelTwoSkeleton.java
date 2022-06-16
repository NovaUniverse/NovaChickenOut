package net.novauniverse.games.chickenout.game.mobs.implementation;

import net.novauniverse.games.chickenout.game.mobs.ChickenOutMobProvider;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class LevelTwoSkeleton implements ChickenOutMobProvider {
    @Override
    public int getLevel() {
        return 2;
    }

    @Override
    public Creature spawn(Location location, Player player) {
        Creature creature = (Creature) location.getWorld().spawnEntity(location, EntityType.SKELETON);

        creature.getEquipment().clear();

        creature.setCustomName("Skeleton");
        creature.setCustomNameVisible(true);
        creature.getEquipment().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setAmount(1).setUnbreakable(true).build());
        creature.getEquipment().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).setAmount(1).setUnbreakable(true).build());
        creature.getEquipment().setItemInMainHand(new ItemBuilder(Material.BOW).setAmount(1).setUnbreakable(true).build());
        return creature;
    }
}
