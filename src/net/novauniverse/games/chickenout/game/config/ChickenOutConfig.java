package net.novauniverse.games.chickenout.game.config;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import net.zeeraa.novacore.spigot.gameengine.module.modules.game.map.mapmodule.MapModule;
import net.zeeraa.novacore.spigot.utils.VectorArea;

public class ChickenOutConfig extends MapModule {
	private List<VectorArea> mobSpawnAreas;
	private List<VectorArea> featherSpawnAreas;

	private int targetFeatherCount;
	private int targetMobCount;

	public ChickenOutConfig(JSONObject json) {
		super(json);

		mobSpawnAreas = new ArrayList<VectorArea>();
		JSONArray mobAreasJSON = json.getJSONArray("mob_spawn_areas");
		for (int i = 0; i < mobAreasJSON.length(); i++) {
			mobSpawnAreas.add(VectorArea.fromJSON(mobAreasJSON.getJSONObject(i)));
		}

		featherSpawnAreas = new ArrayList<VectorArea>();
		JSONArray featherAreasJSON = json.getJSONArray("feather_spawn_areas");
		for (int i = 0; i < mobAreasJSON.length(); i++) {
			featherSpawnAreas.add(VectorArea.fromJSON(featherAreasJSON.getJSONObject(i)));
		}

		targetFeatherCount = json.getInt("target_feather_count");
		targetMobCount = json.getInt("target_mob_count");
	}

	public List<VectorArea> getMobSpawnAreas() {
		return mobSpawnAreas;
	}

	public List<VectorArea> getFeatherSpawnAreas() {
		return featherSpawnAreas;
	}

	public int getTargetFeatherCount() {
		return targetFeatherCount;
	}

	public int getTargetMobCount() {
		return targetMobCount;
	}
}