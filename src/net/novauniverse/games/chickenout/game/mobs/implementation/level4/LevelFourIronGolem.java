package net.novauniverse.games.chickenout.game.mobs.implementation.level4;

import net.novauniverse.games.chickenout.game.mobs.ChickenOutMobProvider;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LevelFourIronGolem implements ChickenOutMobProvider {
    @Override
    public int getLevel() {
        return 4;
    }

    @Override
    public Creature spawn(Location location, Player player) {
        Creature creature = (Creature) location.getWorld().spawnEntity(location, EntityType.IRON_GOLEM);

        creature.getEquipment().clear();

        creature.setCustomName("Iron Golem");
        creature.setCustomNameVisible(true);


        creature.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE,1,false,false));

        return creature;
    }
}