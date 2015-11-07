package com.civcraft.config;

import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.civcraft.main.CivLog;

public class ConfigHappinessState {
	public int level;
	public String name;
	public String color;
	public double amount;
	public double coin_rate;
	public double hammer_rate;
	public double beaker_rate;
	public double growth_rate;
	//public double culture_rate;
	
	public static void loadConfig(FileConfiguration cfg, Map<Integer, ConfigHappinessState> happiness_states) {
		happiness_states.clear();
		List<Map<?, ?>> list = cfg.getMapList("happiness.states");
		for (Map<?,?> cl : list) {
			
			ConfigHappinessState happy_level = new ConfigHappinessState();
			happy_level.level = (Integer)cl.get("level");
			happy_level.name = (String)cl.get("name");
			happy_level.color = (String)cl.get("color");
			happy_level.amount = (Double)cl.get("amount");
			happy_level.coin_rate = (Double)cl.get("coin_rate");
			happy_level.hammer_rate = (Double)cl.get("hammer_rate");
			happy_level.beaker_rate = (Double)cl.get("beaker_rate");
			happy_level.growth_rate = (Double)cl.get("growth_rate");
			//happy_level.culture_rate = (Double)cl.get("culture_rate");
			happiness_states.put(happy_level.level, happy_level);
		}
		CivLog.info("Loaded "+happiness_states.size()+" Happiness States.");		
	}
}
