package com.avrgaming.civcraft.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.file.FileConfiguration;

import com.avrgaming.civcraft.main.CivLog;

public class ConfigMonumentLevel {
	public int level;			/* Current level number */
	public Map<Integer, Integer> consumes; /* A map of block ID's and amounts required for this level to progress */
	public int count; /* Number of times that consumes must be met to level up */
	public double culture; /* Culture generated each time for the cottage */
	
	public ConfigMonumentLevel() {
	}
	
	public ConfigMonumentLevel(ConfigMonumentLevel currentlvl) {
		this.level = currentlvl.level;
		this.count = currentlvl.count;
		this.culture = currentlvl.culture;
		
		this.consumes = new HashMap<Integer, Integer>();
		for (Entry<Integer, Integer> entry : currentlvl.consumes.entrySet()) {
			this.consumes.put(entry.getKey(), entry.getValue());
		}
	}
	
	public static void loadConfig(FileConfiguration cfg, Map<Integer, ConfigMonumentLevel> monument_levels) {
		monument_levels.clear();
		List<Map<?, ?>> cottage_list = cfg.getMapList("monument_levels");
		Map<Integer, Integer> consumes_list = null;
		for (Map<?,?> cl : cottage_list ) {
			List<?> consumes = (List<?>)cl.get("consumes");
			if (consumes != null) {
				consumes_list = new HashMap<Integer, Integer>();
				for (int i = 0; i < consumes.size(); i++) {
					String line = (String) consumes.get(i);
					String split[];
					split = line.split(",");
					consumes_list.put(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
				}
			}
			
			ConfigMonumentLevel monumentlevel = new ConfigMonumentLevel();
			monumentlevel.level = (Integer)cl.get("level");
			monumentlevel.consumes = consumes_list;
			monumentlevel.count = (Integer)cl.get("count");
			monumentlevel.culture = (Double)cl.get("culture");
			monument_levels.put(monumentlevel.level, monumentlevel);
		}
		CivLog.info("Loaded "+monument_levels.size()+" monument levels.");		
	}
}
