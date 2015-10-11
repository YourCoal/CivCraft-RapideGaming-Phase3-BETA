package com.avrgaming.civcraft.config;

import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.avrgaming.civcraft.main.CivLog;

public class ConfigSafetyState {
	public int level;
	public String name;
	public String color;
	public double amount;
	public double coin_rate;
	public double growth_rate;
	
	public static void loadConfig(FileConfiguration cfg, Map<Integer, ConfigSafetyState> safety_states) {
		safety_states.clear();
		List<Map<?, ?>> list = cfg.getMapList("safety.states");
		for (Map<?,?> cl : list) {
			
			ConfigSafetyState safe_level = new ConfigSafetyState();
			safe_level.level = (Integer)cl.get("level");
			safe_level.name = (String)cl.get("name");
			safe_level.color = (String)cl.get("color");
			safe_level.amount = (Double)cl.get("amount");
			safe_level.coin_rate = (Double)cl.get("coin_rate");
			safe_level.growth_rate = (Double)cl.get("growth_rate");
			safety_states.put(safe_level.level, safe_level);
		}
		CivLog.info("Loaded "+safety_states.size()+" Safety States.");		
	}
}
