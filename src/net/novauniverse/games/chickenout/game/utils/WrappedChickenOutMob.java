package net.novauniverse.games.chickenout.game.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;

import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.utils.XYLocation;

public class WrappedChickenOutMob {
	public static final int STUCK_THRESHOLD = 3;

	private Creature entity;
	private UUID target;
	private int level;
	private int timeUntilRemoval;
	private int stuckCounter;

	private XYLocation oldChunk;

	public WrappedChickenOutMob(Creature entity, UUID target, int level) {
		this.entity = entity;
		this.target = target;
		this.level = level;
		this.timeUntilRemoval = 60;
		this.stuckCounter = 0;

		this.oldChunk = getChunkLocation();
	}

	private XYLocation getChunkLocation() {
		return new XYLocation(entity.getLocation().getChunk().getX(), entity.getLocation().getChunk().getZ());
	}

	public void stuckCheck() {
		if (entity.isDead()) {
			return;
		}

		if (getChunkLocation() == oldChunk) {
			Log.trace("ChickenOut", "Stuck counter increased to " + stuckCounter + " for " + entity.getUniqueId());
			stuckCounter++;
			if (stuckCounter >= STUCK_THRESHOLD) {
				entity.remove();
			}
		} else {
			stuckCounter = 0;
			oldChunk = getChunkLocation();
		}
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