package com.civcraft.mobs;

import java.util.LinkedList;
import java.util.Random;

import moblib.mob.ICustomMob;
import moblib.moblib.MobLib;

import org.bukkit.Location;

import com.civcraft.exception.CivException;
import com.civcraft.util.CivColor;

public class MobSpawner {

	
	public static enum CustomMobLevel {
		LESSER(CivColor.LightGreen+"Lesser"),
		GREATER(CivColor.Yellow+"Greater"),
		ELITE(CivColor.LightPurple+"Elite"),
		BRUTAL(CivColor.Rose+"Brutal");
		
		private String name;
		
		private CustomMobLevel(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}	
	} 
		
	public static enum CustomMobType {
		YOBO("Yobo", "com.civcraft.mobs.Yobo"),
		YOBOBOSS("Yobo Boss", "com.civcraft.mobs.YoboBoss"),
		RUFFIAN("Ruffian", "com.civcraft.mobs.Ruffian"),
		BEHEMOTH("Behemoth", "com.civcraft.mobs.Behemoth"),
		SAVAGE("Cannibal", "com.civcraft.mobs.Savage"),
		ANGRYYOBO("Angry Yobo", "com.civcraft.mobs.AngryYobo");
		
		private String name;
		private String className; 
		
		private CustomMobType(String name, String className) {
			this.name = name;
			this.className = className;
		}
		
		public String getName() {
			return name;
		}
		
		public String getClassName() {
			return className;
		}

		public boolean equalsIgnoreCase(String string) {
			return false;
		}
	}
	
	public static void register() {
		Yobo.register();
		Behemoth.register();
		Savage.register();
		Ruffian.register();
	}
	
	public static void despawnAll() {
		for (CommonCustomMob mob : CommonCustomMob.customMobs.values()) {
			mob.entity.getBukkitEntity().remove();
		}
	}
	
	public static CommonCustomMob spawnCustomMob(CustomMobType type, CustomMobLevel level, Location loc) throws CivException {
		
		ICustomMob custom = MobLib.spawnCustom(type.className, loc);
		if (custom == null) {
			throw new CivException("Couldn't spawn custom mob type:"+type.toString());
		}
				
		custom.setData("type", type.toString().toUpperCase());
		custom.setData("level", level.toString().toUpperCase());
		
		custom.onCreate();
		custom.onCreateAttributes();
		
		CommonCustomMob common = (CommonCustomMob)custom;
		CommonCustomMob.customMobs.put(common.entity.getUniqueID(), common);
		return common;
	}

	public static void spawnRandomCustomMob(Location location) {
		LinkedList<TypeLevel> validMobs = CommonCustomMob.getValidMobsForBiome(location.getBlock().getBiome());
		if (validMobs.size() == 0) {
			return;
		}
		
		LinkedList<TypeLevel> removeUs = new LinkedList<TypeLevel>();
		for (TypeLevel v : validMobs) {
			if (CommonCustomMob.disabledMobs.contains(v.type.toString())) {
				removeUs.add(v);
			}
		}
		validMobs.removeAll(removeUs);
		
		Random random = new Random();
		int idx = random.nextInt(validMobs.size());
		
		CustomMobType type = validMobs.get(idx).type;
		CustomMobLevel level = validMobs.get(idx).level;
		
		try {
			spawnCustomMob(type, level, location);
		} catch (CivException e) {
			e.printStackTrace();
		}
	}
}
