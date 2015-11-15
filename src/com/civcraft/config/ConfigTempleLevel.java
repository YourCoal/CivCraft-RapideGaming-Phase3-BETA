package com.civcraft.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.file.FileConfiguration;

import com.civcraft.main.CivLog;

public class ConfigTempleLevel {
	
	public int level; /* Current level */
	public Map<Integer, Integer> consumes; /* A map of block ID's and amounts required for this level to progress */
	public int count; /* Number of times that consumes must be met to level up */
	public double culture; /* Culture generated each time for the temple */
	
	public ConfigTempleLevel() {
	}
	
	public ConfigTempleLevel(ConfigTempleLevel currentlvl) {
		this.level = currentlvl.level;
		this.count = currentlvl.count;
		this.culture = currentlvl.culture;
		this.consumes = new HashMap<Integer, Integer>();
		for (Entry<Integer, Integer> entry : currentlvl.consumes.entrySet()) {
			this.consumes.put(entry.getKey(), entry.getValue());
		}
	}
	
	public static void loadConfig(FileConfiguration cfg, Map<Integer, ConfigTempleLevel> temple_levels) {
		temple_levels.clear();
		List<Map<?, ?>> temple_list = cfg.getMapList("temple_levels");
		Map<Integer, Integer> consumes_list = null;
		for (Map<?,?> cl : temple_list ) {
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
			ConfigTempleLevel templelevel = new ConfigTempleLevel();
			templelevel.level = (Integer)cl.get("level");
			templelevel.consumes = consumes_list;
			templelevel.count = (Integer)cl.get("count");
			templelevel.culture = (Double)cl.get("culture");
			temple_levels.put(templelevel.level, templelevel);
		}
		CivLog.info("Loaded "+temple_levels.size()+" temple levels.");		
	}
}