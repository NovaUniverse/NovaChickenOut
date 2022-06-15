package net.novauniverse.games.chickenout.game.event;

import org.bukkit.event.Event;

public abstract class AbstractChickenOutPlacementEvent extends Event {
	private int score;
	private int placement;

	public AbstractChickenOutPlacementEvent(int score, int placement) {
		this.score = score;
		this.placement = placement;
	}

	public int getScore() {
		return score;
	}

	public int getPlacement() {
		return placement;
	}
}