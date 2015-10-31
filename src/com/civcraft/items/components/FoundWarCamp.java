package com.civcraft.items.components;

import gpl.AttributeUtil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.civcraft.config.CivSettings;
import com.civcraft.config.ConfigBuildableInfo;
import com.civcraft.exception.CivException;
import com.civcraft.exception.InvalidConfiguration;
import com.civcraft.interactive.InteractiveWarCampFound;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;
import com.civcraft.structure.Buildable;
import com.civcraft.threading.TaskMaster;
import com.civcraft.util.CallbackInterface;
import com.civcraft.util.CivColor;
import com.civcraft.war.War;

public class FoundWarCamp extends ItemComponent implements CallbackInterface {
	
	public static ConfigBuildableInfo info = new ConfigBuildableInfo();
	static {
		info.id = "warcamp";
		info.displayName = "War Camp";
		info.ignore_floating = false;
		info.template_base_name = "warcamp";
		info.tile_improvement = false;
		info.templateYShift = -1;
		info.max_hitpoints = 100;
	}
	
	@Override
	public void onPrepareCreate(AttributeUtil attrUtil) {
		attrUtil.addLore(ChatColor.RESET+CivColor.Gold+"Deploys War Camp");
		attrUtil.addLore(ChatColor.RESET+CivColor.Rose+"<Right Click To Use>");		
	}
	
	public void foundCamp(Player player) throws CivException {
		Resident resident = CivGlobal.getResident(player);
		
		if (!resident.hasTown()) {
			throw new CivException("You must be part of a civilization to found a war camp.");
		}
		
		if (!resident.getCiv().getLeaderGroup().hasMember(resident) &&
			!resident.getCiv().getAdviserGroup().hasMember(resident)) {
			throw new CivException("You must be a leader or adviser of the civilization to found a war camp.");
		}
		
		if (!War.isWarTime()) {
			throw new CivException("War Camps can only be built during WarTime.");
		}
		
		/*
		 * Build a preview for the Capitol structure.
		 */
		CivMessage.send(player, CivColor.LightGreen+CivColor.BOLD+"Checking structure position...Please wait.");

		
		Buildable.buildVerifyStatic(player, info, player.getLocation(), this);
	}
	
	public void onInteract(PlayerInteractEvent event) {
		
		event.setCancelled(true);
		if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) &&
				!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		
		try {
			foundCamp(event.getPlayer());
		} catch (CivException e) {
			CivMessage.sendError(event.getPlayer(), e.getMessage());
		}
		
		class SyncTask implements Runnable {
			String name;
				
			public SyncTask(String name) {
				this.name = name;
			}
			
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				Player player;
				try {
					player = CivGlobal.getPlayer(name);
				} catch (CivException e) {
					return;
				}
				player.updateInventory();
			}
		}
		TaskMaster.syncTask(new SyncTask(event.getPlayer().getName()));
		
		return;
		
	}

	@Override
	public void execute(String playerName) {
		Player player;
		try {
			player = CivGlobal.getPlayer(playerName);
		} catch (CivException e) {
			return;
		}
		Resident resident = CivGlobal.getResident(playerName);
		int warTimeout;
		try {
			warTimeout = CivSettings.getInteger(CivSettings.warConfig, "warcamp.rebuild_timeout");
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
			return;
		}
		
		CivMessage.sendHeading(player, "Ready for War! War Camp.");
		CivMessage.send(player, CivColor.LightGreen+"Lets get down to buisness. ");
		CivMessage.send(player, CivColor.LightGreen+"   -Your Civilization will be able to spawn here.");
		CivMessage.send(player, CivColor.LightGreen+"   -Cannot be rebuilt for at least "+warTimeout+" mins.");
		CivMessage.send(player, " ");
		CivMessage.send(player, CivColor.LightGreen+ChatColor.BOLD+"Do you want to place the War Camp here?");
		CivMessage.send(player, CivColor.LightGray+"(To accept, type 'yes')");
		
		resident.setInteractiveMode(new InteractiveWarCampFound(info));
	}
}
