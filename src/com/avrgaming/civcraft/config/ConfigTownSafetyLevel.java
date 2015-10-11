package com.avrgaming.civcraft.config;

import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.avrgaming.civcraft.main.CivLog;

public class ConfigTownSafetyLevel {
	public int level;
	public double safety;
	
	public static void loadConfig(FileConfiguration cfg, Map<Integer, ConfigTownSafetyLevel> town_happiness_levels) {
		town_happiness_levels.clear();
		List<Map<?, ?>> list = cfg.getMapList("safety.town_levels");
		for (Map<?,?> cl : list ) {
			
			ConfigTownSafetyLevel safe_level = new ConfigTownSafetyLevel();
			safe_level.level = (Integer)cl.get("level");
			safe_level.safety = (Double)cl.get("safety");
			town_happiness_levels.put(safe_level.level, safe_level);
		}
		CivLog.info("Loaded "+town_happiness_levels.size()+" Town Safety levels.");		
	}
}
