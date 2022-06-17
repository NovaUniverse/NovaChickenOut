package net.novauniverse.games.chickenout.game.mobs.implementation.level2;

import net.novauniverse.games.chickenout.game.mobs.ChickenOutMobProvider;
import net.novauniverse.games.chickenout.game.mobs.MobProviderWithRandomArmor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LevelTwoSpider implements ChickenOutMobProvider {

    @Override
    public int getLevel() {
        return 2;
    }

    @Override
    public Creature spawn(Location location, Player player) {
        Creature creature = (Creature) location.getWorld().spawnEntity(location, EntityType.SPIDER);

        creature.getEquipment().clear();

        creature.setCustomName("Spider");
        creature.setCustomNameVisible(true);


        creature.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE,0,false,false,false));

        return creature;
    }
}
