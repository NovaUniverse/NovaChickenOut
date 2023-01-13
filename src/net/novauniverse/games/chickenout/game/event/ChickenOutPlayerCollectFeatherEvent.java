package net.novauniverse.games.chickenout.game.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChickenOutPlayerCollectFeatherEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	private Player player;
	private int amount;
	private boolean disablePickupSound;

	public ChickenOutPlayerCollectFeatherEvent(Player player, int amount) {
		this.player = player;
		this.amount = amount;
		this.disablePickupSound = false;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public boolean isDisablePickupSound() {
		return disablePickupSound;
	}

	public void setDisablePickupSound(boolean disablePickupSound) {
		this.disablePickupSound = disablePickupSound;
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