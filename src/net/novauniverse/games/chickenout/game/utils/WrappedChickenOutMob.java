package net.novauniverse.games.chickenout.game.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;

public class WrappedChickenOutMob {
	private Creature entity;
	private UUID target;
	private int level;
	private int timeUntilRemoval;

	public WrappedChickenOutMob(Creature entity, UUID target, int level) {
		this.entity = entity;
		this.target = target;
		this.level = level;
		this.timeUntilRemoval = 60;
	}

	public void updateMobTarget() {
		if (entity.isDead()) {
			return;
		}

		Player player = Bukkit.getServer().getPlayer(target);
		if (player != null) {
			entity.setTarget(player);
		}
	}

	public Creature getEntity() {
		return entity;
	}

	public UUID getTarget() {
		return target;
	}

	public int getLevel() {
		return level;
	}

	public void decrementRemovalTimer() {
		timeUntilRemoval--;
	}

	public int getTimeUntilRemoval() {
		return timeUntilRemoval;
	}
}