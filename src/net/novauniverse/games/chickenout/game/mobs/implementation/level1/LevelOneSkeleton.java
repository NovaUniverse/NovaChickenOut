package net.novauniverse.games.chickenout.game.mobs.implementation.level1;

import net.novauniverse.games.chickenout.game.mobs.ChickenOutMobProvider;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class LevelOneSkeleton implements ChickenOutMobProvider {

    @Override
    public int getLevel() {
        return 1;
    }

    @Override
    public Creature spawn(Location location, Player player) {
        Creature creature = (Creature) location.getWorld().spawnEntity(location, EntityType.SKELETON);
        creature.getEquipment().clear();
        creature.setCustomName("Skeleton");
        creature.setCustomNameVisible(true);
        creature.getEquipment().setItemInMainHand(new ItemBuilder(Material.BOW).setUnbreakable(true).setAmount(1).build());
        return creature;
    }
}
