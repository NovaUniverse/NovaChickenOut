package net.novauniverse.games.chickenout.game.mobs.implementation.level1;

import net.novauniverse.games.chickenout.game.mobs.ChickenOutMobProvider;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class LevelOneZombie implements ChickenOutMobProvider {
    @Override
    public int getLevel() {
        return 1;
    }

    @Override
    public Creature spawn(Location location, Player player) {
        Creature creature = (Creature) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
        creature.getEquipment().clear();
        creature.setCustomName("Zombie");
        creature.setCustomNameVisible(true);
        ((Ageable) creature).setAdult();
        creature.getEquipment().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setAmount(1).setUnbreakable(true).build());
        return creature;
    }
}