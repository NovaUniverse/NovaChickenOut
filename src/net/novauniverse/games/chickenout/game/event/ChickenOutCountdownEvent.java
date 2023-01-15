package net.novauniverse.games.chickenout.game.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChickenOutCountdownEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	private int timeLeft;
	private boolean disableCountdownSoundEffect;

	public ChickenOutCountdownEvent(int timeLeft) {
		this.timeLeft = timeLeft;
		this.disableCountdownSoundEffect = false;
	}

	public int getTimeLeft() {
		return timeLeft;
	}

	public boolean isDisableCountdownSoundEffect() {
		return disableCountdownSoundEffect;
	}

	public void setDisableCountdownSoundEffect(boolean disableCountdownSoundEffect) {
		this.disableCountdownSoundEffect = disableCountdownSoundEffect;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}
}