package net.novauniverse.games.chickenout.game.mobs.implementation.level4;

import net.novauniverse.games.chickenout.game.mobs.ArmorMaterialType;
import net.novauniverse.games.chickenout.game.mobs.Enchant;
import net.novauniverse.games.chickenout.game.mobs.MobProviderWithRandomArmor;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class LevelFourBabyZombie extends MobProviderWithRandomArmor {
	@Override
	public int getLevel() {
		return 4;
	}

	@Override
	public Creature spawn(Location location, Player player) {
		Creature creature = (Creature) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
		creature.getEquipment().clear();
		creature.setCustomName("Baby Zombie");
		creature.setCustomNameVisible(true);
		((Ageable) creature).setBaby();
		ArrayList<Enchant> enchants = new ArrayList<>();
		enchants.add(new Enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1));

		setTimesYouCanRandomize(2);
		armorRandomizer(creature, ArmorMaterialType.IRON, enchants);

		creature.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, false, false));
		return creature;
	}
}