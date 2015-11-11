package com.civcraft.config;

import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.civcraft.main.CivLog;

public class ConfigCultureBiomeInfo {
	public String name;
	public double coins;
	public double growth;
	public double hammers;
	public double beakers;
	public double happiness;
	
	public static void loadConfig(FileConfiguration cfg, Map<String, ConfigCultureBiomeInfo> culture_biomes) {
		culture_biomes.clear();
		List<Map<?, ?>> list = cfg.getMapList("culture_biomes");
		for (Map<?,?> cl : list) {
			
			ConfigCultureBiomeInfo biome = new ConfigCultureBiomeInfo();
			biome.name = (String)cl.get("name");
			biome.coins = (Double)cl.get("coins");
			biome.growth = (Double)cl.get("growth");
			biome.hammers = (Double)cl.get("hammers");
			biome.beakers = (Double)cl.get("beakers");
			biome.happiness = (Double)cl.get("happiness");

			culture_biomes.put(biome.name, biome);
		}
		CivLog.info("Loaded "+culture_biomes.size()+" Culture Biomes.");		
	}
}
