package net.novauniverse.games.chickenout.game.mobs;

import org.bukkit.enchantments.Enchantment;

public class Enchant {
	private Enchantment enchantment;
	private int level = 0;

	public Enchant(Enchantment enchantment, int level) {
		this.enchantment = enchantment;
		this.level = level;
	}

	public Enchantment getEnchantment() {
		return enchantment;
	}

	public int getLevel() {
		return level;
	}
}