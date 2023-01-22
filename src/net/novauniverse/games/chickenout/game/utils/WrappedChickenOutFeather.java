package net.novauniverse.games.chickenout.game.utils;

import java.awt.Color;

import org.bukkit.entity.Item;

import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.abstraction.particle.NovaDustOptions;

public class WrappedChickenOutFeather {
	private final Item item;
	private final Color color;
	private final NovaDustOptions particleColor;

	public WrappedChickenOutFeather(Item item, Color color) {
		this.item = item;
		this.color = color;
		this.particleColor = new NovaDustOptions(color);
	}

	public Item getItem() {
		return item;
	}

	public Color getColor() {
		return color;
	}

	public void showParticles() {
		if (!item.isDead()) {
			NovaCore.getInstance().getNovaParticleProvider().showRedstoneParticle(item.getLocation(), particleColor);
		}
	}
}