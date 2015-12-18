package com.civcraft.config;

import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.civcraft.main.CivLog;

public class ConfigTradeShipyardLevel {
	public int level;	/* Current level number */
	public int upgradeTrade; /* Number of items this consumes */
	public int maxTrade; /* Number of times that consumes must be met to level up */
	public int culture; /* culture generated each time hour */
	
	public static void loadConfig(FileConfiguration cfg, Map<Integer, ConfigTradeShipyardLevel> levels) {
		levels.clear();
		List<Map<?, ?>> trade_shipyard_levels = cfg.getMapList("trade_shipyard_levels");
		for (Map<?, ?> level : trade_shipyard_levels) {
			ConfigTradeShipyardLevel trade_shipyard_level = new ConfigTradeShipyardLevel();
			trade_shipyard_level.level = (Integer)level.get("level");
			trade_shipyard_level.culture = (Integer)level.get("culture");
			trade_shipyard_level.upgradeTrade = (Integer)level.get("upgradeTrade");
			trade_shipyard_level.maxTrade = (Integer)level.get("maxTrade"); 
			levels.put(trade_shipyard_level.level, trade_shipyard_level);
		}
		CivLog.info("Loaded "+levels.size()+" Trade-Shipyard Levels.");
	}
}