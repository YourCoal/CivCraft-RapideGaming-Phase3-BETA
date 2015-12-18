package com.civcraft.config;

import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.civcraft.main.CivLog;

public class ConfigPastureLevel {
	public int level;
	public String itemName;
	public int itemId;
	public int itemData;
	public int amount;
	public double price;
	
	public static void loadConfig(FileConfiguration cfg, Map<Integer, ConfigPastureLevel> levels) {
		levels.clear();
		List<Map<?, ?>> pasture_levels = cfg.getMapList("pasture_levels");
		for (Map<?, ?> level : pasture_levels) {
			ConfigPastureLevel pasture_level = new ConfigPastureLevel();
			pasture_level.level = (Integer)level.get("level");
			pasture_level.itemName = (String)level.get("itemName");
			pasture_level.itemId = (Integer)level.get("itemId");
			pasture_level.itemData = (Integer)level.get("itemData");
			pasture_level.amount = (Integer)level.get("amount");
			pasture_level.price = (Double)level.get("price");
			levels.put(pasture_level.level, pasture_level);
		}
		CivLog.info("Loaded "+levels.size()+" Pasture Levels.");
	}
}
