package com.civcraft.command.admin;

import java.util.HashMap;
import java.util.LinkedList;

import net.minecraft.server.v1_8_R3.EntityCreature;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.civcraft.command.CommandBase;
import com.civcraft.exception.CivException;
import com.civcraft.main.CivMessage;
import com.civcraft.mobs.CommonCustomMob;
import com.civcraft.mobs.MobSpawner.CustomMobType;
import com.civcraft.mobs.timers.MobSpawnerTimer;
import com.civcraft.util.CivColor;
import com.civcraft.util.EntityProximity;

public class AdminMobCommand extends CommandBase {

	@Override
	public void init() {
		command = "/ad mob";
		displayName = "Admin Mob";		
		
		commands.put("count", "Shows mob totals globally");
		commands.put("disable", "[name] Disables this mob from spawning");
		commands.put("enable", "[name] Enables this mob to spawn.");
		commands.put("killall", "[name] Removes all of these mobs from the game instantly.");
	}
	
	public void killall_cmd() throws CivException {
		Player player = getPlayer();
		String name = getNamedString(1, "Enter a mob name");
		
		LinkedList<CommonCustomMob> removeUs = new LinkedList<CommonCustomMob>();
		for (CommonCustomMob mob :CommonCustomMob.customMobs.values()) {
			if (mob.getType().toString().equalsIgnoreCase(name)) {
				removeUs.add(mob);
			}
		}
		
		int count = 0;
		for (CommonCustomMob mob : removeUs) {
			CommonCustomMob.customMobs.remove(mob.entity.getUniqueID());
			mob.entity.getBukkitEntity().remove();
			count++;
		}
		CivMessage.sendSuccess(player, "Removed "+count+ " mobs of type "+name);
	}

	public void count_cmd() throws CivException {
		Player player = getPlayer();
		
		HashMap<String, Integer> amounts = new HashMap<String, Integer>();
		int total = CommonCustomMob.customMobs.size();
		for (CommonCustomMob mob : CommonCustomMob.customMobs.values()) {
			Integer count = amounts.get(mob.getClass().getSimpleName());
			if (count == null) {
				count = 0;
			}
			
			amounts.put(mob.getClass().getSimpleName(), count+1);
		}
		
		CivMessage.sendHeading(player, "Custom Mob Counts");
		CivMessage.send(player, CivColor.LightGray+"Red mobs are over their count limit for this area and should no longer spawn.");
		for (String mob : amounts.keySet()) {
			int count = amounts.get(mob);
			
			LinkedList<Entity> entities = EntityProximity.getNearbyEntities(null, player.getLocation(), MobSpawnerTimer.MOB_AREA, EntityCreature.class);
			if (entities.size() > MobSpawnerTimer.MOB_AREA_LIMIT) {
				CivMessage.send(player, CivColor.Red+mob+": "+CivColor.Rose+count);
			} else {
				CivMessage.send(player, CivColor.Green+mob+": "+CivColor.LightGreen+count);
			}
			
		}
		CivMessage.send(player, CivColor.Green+"Total Mobs:"+CivColor.LightGreen+total);
	}
	
	public void disable_cmd() throws CivException {
		Player player = getPlayer();
		String name = getNamedString(1, "Enter a mob name");
		
		switch (name.toLowerCase()) {
		case "behemoth":
			CommonCustomMob.disabledMobs.add(CustomMobType.BEHEMOTH.toString());
			break;
		case "ferosse":
			CommonCustomMob.disabledMobs.add(CustomMobType.FEROSSE.toString());
			break;
		case "glacial brute":
			CommonCustomMob.disabledMobs.add(CustomMobType.GLACIAL_BRUTE.toString());
			break;
		case "woodland sentinel":
			CommonCustomMob.disabledMobs.add(CustomMobType.WOODLAND_SENTINEL.toString());
			break;
		case "timberland ranger":
			CommonCustomMob.disabledMobs.add(CustomMobType.TIMBERLAND_RANGER.toString());
			break;
		case "floral_gatherer":
			CommonCustomMob.disabledMobs.add(CustomMobType.FLORAL_GATHERER.toString());
			break;
		case "inferi":
			CommonCustomMob.disabledMobs.add(CustomMobType.INFERI.toString());
			break;
		case "torrid ogre":
			CommonCustomMob.disabledMobs.add(CustomMobType.TORRID_OGRE.toString());
			break;
		default:
			throw new CivException("Invalid mob. Make sure it is spelled right!");
		}
		CivMessage.sendSuccess(player, "Disabled "+name);
	}
	
	public void enable_cmd() throws CivException {
		Player player = getPlayer();
		String name = getNamedString(1, "Enter a mob name");
		
		switch (name.toLowerCase()) {
		case "behemoth":
			CommonCustomMob.disabledMobs.add(CustomMobType.BEHEMOTH.toString());
			break;
		case "ferosse":
			CommonCustomMob.disabledMobs.add(CustomMobType.FEROSSE.toString());
			break;
		case "glacial brute":
			CommonCustomMob.disabledMobs.add(CustomMobType.GLACIAL_BRUTE.toString());
			break;
		case "woodland sentinel":
			CommonCustomMob.disabledMobs.add(CustomMobType.WOODLAND_SENTINEL.toString());
			break;
		case "timberland ranger":
			CommonCustomMob.disabledMobs.add(CustomMobType.TIMBERLAND_RANGER.toString());
			break;
		case "floral_gatherer":
			CommonCustomMob.disabledMobs.add(CustomMobType.FLORAL_GATHERER.toString());
			break;
		case "inferi":
			CommonCustomMob.disabledMobs.add(CustomMobType.INFERI.toString());
			break;
		case "torrid ogre":
			CommonCustomMob.disabledMobs.add(CustomMobType.TORRID_OGRE.toString());
			break;
		default:
			throw new CivException("Invalid mob. Make sure it is spelled right!");
		}
		
		CivMessage.sendSuccess(player, "Enabled "+name);
	}
	@Override
	public void doDefaultAction() throws CivException {
		showHelp();
	}

	@Override
	public void showHelp() {
		showBasicHelp();
	}

	@Override
	public void permissionCheck() throws CivException {
	}

}
