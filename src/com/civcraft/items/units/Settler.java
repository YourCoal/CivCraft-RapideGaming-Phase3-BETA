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
package com.civcraft.items.units;

import gpl.AttributeUtil;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.civcraft.config.CivSettings;
import com.civcraft.config.ConfigBuildableInfo;
import com.civcraft.config.ConfigUnit;
import com.civcraft.exception.CivException;
import com.civcraft.exception.InvalidConfiguration;
import com.civcraft.interactive.InteractiveTownName;
import com.civcraft.lorestorage.LoreMaterial;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;
import com.civcraft.object.Town;
import com.civcraft.structure.Buildable;
import com.civcraft.structure.TownHall;
import com.civcraft.util.BlockCoord;
import com.civcraft.util.CallbackInterface;
import com.civcraft.util.CivColor;

public class Settler extends UnitMaterial implements CallbackInterface {

	public Settler(String id, ConfigUnit configUnit) {
		super(id,configUnit);
	}
	
	public static void spawn(Inventory inv, Town town) throws CivException {

		ItemStack is = LoreMaterial.spawn(Unit.SETTLER_UNIT);
		
		UnitMaterial.setOwningTown(town, is);
		
		AttributeUtil attrs = new AttributeUtil(is);
		attrs.addLore(CivColor.Rose+"Only Usable In Civ: "+CivColor.LightBlue+town.getCiv().getName());
		attrs.addLore(CivColor.Gold+"Right Click To Found Town");
		attrs.addEnhancement("LoreEnhancementSoulBound", null, null);
		attrs.addLore(CivColor.Gold+"Soulbound");
		
		attrs.setCivCraftProperty("owner_civ_id", ""+town.getCiv().getId());
		is = attrs.getStack();
		
		
		if (!Unit.addItemNoStack(inv, is)) {
			throw new CivException("Cannot make "+Unit.SETTLER_UNIT.getUnit().name+". Barracks chest is full! Make Room!");
		}

	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onInteract(PlayerInteractEvent event) {
		event.setCancelled(true);
		Player player = event.getPlayer();
		player.updateInventory();
		Resident resident = CivGlobal.getResident(player);
		
		if (resident == null || !resident.hasTown()) {
			CivMessage.sendError(player, "You are not part of a civilization.");
			return;
		}
		
		AttributeUtil attrs = new AttributeUtil(event.getItem());
		String ownerIdString = attrs.getCivCraftProperty("owner_civ_id");
		if (ownerIdString == null) {
			CivMessage.sendError(player, "Cannot find owner civilization ID. This settler is broken. Report this to an admin.");
			return;
		}
		
		int civ_id = Integer.valueOf(ownerIdString);
		if (civ_id != resident.getCiv().getId()) {
			CivMessage.sendError(player, "You cannot use this settler unit. Your civilization is not the owner.");
			return;
		}
		
		double minDistance;
		try {
			minDistance = CivSettings.getDouble(CivSettings.townConfig, "town.min_town_distance");
		} catch (InvalidConfiguration e) {
			CivMessage.sendError(player, "Internal configuration error.");
			e.printStackTrace();
			return;
		}
		
		for (Town town : CivGlobal.getTowns()) {
			TownHall townhall = town.getTownHall();
			if (townhall == null) {
				continue;
			}
			
			double dist = townhall.getCenterLocation().distance(new BlockCoord(event.getPlayer().getLocation()));
			if (dist < minDistance) {
				DecimalFormat df = new DecimalFormat();
				CivMessage.sendError(player, "Cannot build town here. Too close to the town of "+town.getName()+". Distance is "+df.format(dist)+" and needs to be "+minDistance);
				return;
			}
		}
		
		
		/*
		 * Build a preview for the Capitol structure.
		 */
		CivMessage.send(player, CivColor.LightGreen+CivColor.BOLD+"Checking structure position...Please wait.");
		ConfigBuildableInfo info = CivSettings.structures.get("s_townhall");
		try {
			Buildable.buildVerifyStatic(player, info, player.getLocation(), this);
		} catch (CivException e) {
			CivMessage.sendError(player, e.getMessage());
		}	
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
		resident.desiredTownLocation = player.getLocation();
		
		CivMessage.sendHeading(player, "Founding A New Town");
		CivMessage.send(player, CivColor.LightGreen+"This looks like a good place to settle!");
		CivMessage.send(player, " ");
		CivMessage.send(player, CivColor.LightGreen+ChatColor.BOLD+"What shall your new Town be called?");
		CivMessage.send(player, CivColor.LightGray+"(To cancel, type 'cancel')");
		
		resident.setInteractiveMode(new InteractiveTownName());
	}
	
}
