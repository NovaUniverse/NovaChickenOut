package net.novauniverse.games.chickenout.game.mobs.implementation.level3;

import net.novauniverse.games.chickenout.game.mobs.ChickenOutMobProvider;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LevelThreeSpider implements ChickenOutMobProvider {
    @Override
    public int getLevel() {
        return 3;
    }

    @Override
    public Creature spawn(Location location, Player player) {
        Creature creature = (Creature) location.getWorld().spawnEntity(location, EntityType.SPIDER);

        creature.getEquipment().clear();

        creature.setCustomName("Spider");
        creature.setCustomNameVisible(true);


        creature.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE,1,false,false,false));

        return creature;
    }
}
