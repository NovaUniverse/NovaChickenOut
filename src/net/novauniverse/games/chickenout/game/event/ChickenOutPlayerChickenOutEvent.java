package net.novauniverse.games.chickenout.game.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChickenOutPlayerChickenOutEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	private Player player;
	private int feathers;

	public ChickenOutPlayerChickenOutEvent(Player player, int feathers) {
		this.player = player;
		this.feathers = feathers;
	}

	public int getFeathers() {
		return feathers;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}
}