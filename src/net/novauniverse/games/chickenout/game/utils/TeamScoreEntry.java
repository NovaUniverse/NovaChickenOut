package net.novauniverse.games.chickenout.game.utils;

import java.util.UUID;
import java.util.Map.Entry;

public class TeamScoreEntry implements Entry<UUID, Integer> {
	private UUID teamUuid;
	private Integer score;

	public TeamScoreEntry(UUID teamUuid, Integer score) {
		this.teamUuid = teamUuid;
		this.score = score;
	}

	@Override
	public UUID getKey() {
		return teamUuid;
	}

	@Override
	public Integer getValue() {
		return score;
	}

	@Override
	public Integer setValue(Integer value) {
		return score;
	}
}