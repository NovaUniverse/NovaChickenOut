package net.novauniverse.games.chickenout.game.event;

import java.util.UUID;

import org.bukkit.event.HandlerList;

public class ChickenOutPlayerPlacementEvent extends AbstractChickenOutPlacementEvent {
	private static final HandlerList HANDLERS_LIST = new HandlerList();
	
	private UUID uuid;
	
	public ChickenOutPlayerPlacementEvent(int score, int placement, UUID uuid) {
		super(score, placement);
		this.uuid = uuid;
	}
	
	public UUID getUuid() {
		return uuid;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}
}