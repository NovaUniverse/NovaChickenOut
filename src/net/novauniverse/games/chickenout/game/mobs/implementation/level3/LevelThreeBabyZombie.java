package net.novauniverse.games.chickenout.game.mobs.implementation.level3;

import net.novauniverse.games.chickenout.game.mobs.ArmorMaterialType;
import net.novauniverse.games.chickenout.game.mobs.MobProviderWithRandomArmor;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LevelThreeBabyZombie extends MobProviderWithRandomArmor {
	@Override
	public int getLevel() {
		return 3;
	}

	@Override
	public Creature spawn(Location location, Player player) {
		Creature creature = (Creature) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
		creature.getEquipment().clear();
		creature.setCustomName("Baby Zombie");
		creature.setCustomNameVisible(true);
		((Ageable) creature).setBaby();

		setTimesYouCanRandomize(1);
		armorRandomizer(creature, ArmorMaterialType.IRON);

		creature.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));
		return creature;
	}
}