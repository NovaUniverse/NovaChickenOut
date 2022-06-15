package net.novauniverse.games.chickenout.game.event;

import org.bukkit.event.HandlerList;

import net.zeeraa.novacore.spigot.teams.Team;

public class ChickenOutTeamPlacementEvent extends AbstractChickenOutPlacementEvent {
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	private Team team;

	public ChickenOutTeamPlacementEvent(int score, int placement, Team team) {
		super(score, placement);
		this.team = team;
	}

	public Team getTeam() {
		return team;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}
}