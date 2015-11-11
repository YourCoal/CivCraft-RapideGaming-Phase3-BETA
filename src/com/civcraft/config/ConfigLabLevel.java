package com.civcraft.config;

import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.civcraft.main.CivLog;

public class ConfigLabLevel {
	public int level;	/* Current level number */
	public int amount; /* Number of redstone this mine consumes */
	public int count; /* Number of times that consumes must be met to level up */
	public double beakers; /* hammers generated each time hour */
	
	public static void loadConfig(FileConfiguration cfg, Map<Integer, ConfigLabLevel> levels) {
		levels.clear();
		List<Map<?, ?>> mine_levels = cfg.getMapList("lab_levels");
		for (Map<?, ?> level : mine_levels) {
			ConfigLabLevel mine_level = new ConfigLabLevel();
			mine_level.level = (Integer)level.get("level");
			mine_level.amount = (Integer)level.get("amount");
			mine_level.beakers = (Double)level.get("beakers");
			mine_level.count = (Integer)level.get("count"); 
			levels.put(mine_level.level, mine_level);
		}
		CivLog.info("Loaded "+levels.size()+" lab levels.");
	}
}