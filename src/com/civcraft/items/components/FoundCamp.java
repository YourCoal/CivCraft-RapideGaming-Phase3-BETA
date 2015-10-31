/*************************************************************************
 * 
 * AVRGAMING LLC
 * __________________
 * 
 *  [2013] AVRGAMING LLC
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of AVRGAMING LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to AVRGAMING LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from AVRGAMING LLC.
 */
package com.civcraft.items.components;

import gpl.AttributeUtil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.civcraft.config.ConfigBuildableInfo;
import com.civcraft.exception.CivException;
import com.civcraft.interactive.InteractiveCampName;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;
import com.civcraft.structure.Buildable;
import com.civcraft.threading.TaskMaster;
import com.civcraft.util.CallbackInterface;
import com.civcraft.util.CivColor;

public class FoundCamp extends ItemComponent implements CallbackInterface {

	@Override
	public void onPrepareCreate(AttributeUtil attrUtil) {
		attrUtil.addLore(ChatColor.RESET+CivColor.Gold+"Starts a Camp");
		attrUtil.addLore(ChatColor.RESET+CivColor.Rose+"<Right Click To Use>");
		attrUtil.addEnhancement("LoreEnhancementSoulBound", null, null);
		attrUtil.addLore(CivColor.Gold+"Soulbound");
	}
	
	public void foundCamp(Player player) throws CivException {
		Resident resident = CivGlobal.getResident(player);
		
		if (resident.hasTown()) {
			throw new CivException("You cannot found a camp when you're a member of a town.");
		}
		
		if (resident.hasCamp()) {
			throw new CivException("You cannot found a camp when you're a member of another camp.");
		}
			
		/*
		 * Build a preview for the Capitol structure.
		 */
		CivMessage.send(player, CivColor.LightGreen+CivColor.BOLD+"Checking structure position...Please wait.");
		ConfigBuildableInfo info = new ConfigBuildableInfo();
		info.id = "camp";
		info.displayName = "Camp";
		info.ignore_floating = false;
		info.template_base_name = "camp";
		info.tile_improvement = false;
		info.templateYShift = -1;
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
		CivMessage.sendHeading(player, "Setting up Camp!");
		CivMessage.send(player, CivColor.LightGreen+"You and your small band of travelers need a place to sleep for the night.");
		CivMessage.send(player, " ");
		CivMessage.send(player, CivColor.LightGreen+ChatColor.BOLD+"What shall your new camp be called?");
		CivMessage.send(player, CivColor.LightGray+"(To cancel, type 'cancel')");
		resident.setInteractiveMode(new InteractiveCampName());
	}
}
