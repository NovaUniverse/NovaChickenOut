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
	
	private VectorArea chickenOutArea;

	private int targetFeatherCount;
	private int targetMobCount;
	
	private int levelTime;
	private int maxLevel;

	private int finalRoundTime;
	
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
		
		chickenOutArea = VectorArea.fromJSON(json.getJSONObject("chicken_out_area"));

		targetFeatherCount = json.getInt("target_feather_count");
		targetMobCount = json.getInt("target_mob_count");
		
		levelTime = json.getInt("level_time");
		maxLevel = json.getInt("max_level");
		
		finalRoundTime = json.getInt("final_round_time");
	}

	public List<VectorArea> getMobSpawnAreas() {
		return mobSpawnAreas;
	}

	public List<VectorArea> getFeatherSpawnAreas() {
		return featherSpawnAreas;
	}

	public VectorArea getChickenOutArea() {
		return chickenOutArea;
	}
	
	public int getTargetFeatherCount() {
		return targetFeatherCount;
	}

	public int getTargetMobCount() {
		return targetMobCount;
	}
	
	public int getFinalRoundTime() {
		return finalRoundTime;
	}
	
	public int getLevelTime() {
		return levelTime;
	}
	
	public int getMaxLevel() {
		return maxLevel;
	}
}