package net.novauniverse.games.chickenout.game.utils;

import java.awt.Color;

import org.bukkit.entity.Item;

import xyz.xenondevs.particle.ParticleEffect;

public class WrappedChickenOutFeather {
	private Item item;
	private Color color;

	public WrappedChickenOutFeather(Item item, Color color) {
		this.item = item;
		this.color = color;
	}

	public Item getItem() {
		return item;
	}
	
	public Color getColor() {
		return color;
	}

	public void showParticles() {
		if (!item.isDead()) {
			ParticleEffect.REDSTONE.display(item.getLocation().clone().add(0, 2, 0), color);
		}
	}
}