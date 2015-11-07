package com.civcraft.items.components;

import gpl.AttributeUtil;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.civcraft.exception.CivException;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;
import com.civcraft.siege.bronze.BronzeCannon;
import com.civcraft.util.CivColor;
import com.civcraft.war.War;

public class BuildBronzeCannon extends ItemComponent {

	public void onInteract(PlayerInteractEvent event) {
		try {
			if (!War.isWarTime()) {
				throw new CivException("Cannons can only be deployed during WarTime.");
			}
			
			Resident resident = CivGlobal.getResident(event.getPlayer());
			BronzeCannon.newCannon(resident);
			CivMessage.sendCiv(resident.getCiv(), "We've deployed a cannon at "+
				event.getPlayer().getLocation().getBlockX()+","+
				event.getPlayer().getLocation().getBlockY()+","+
				event.getPlayer().getLocation().getBlockZ());
			ItemStack newStack = new ItemStack(Material.AIR);
			event.getPlayer().setItemInHand(newStack);
		} catch (CivException e) {
			CivMessage.sendError(event.getPlayer(), e.getMessage());
		}
	}
	
	@Override
	public void onPrepareCreate(AttributeUtil attrUtil) {
		attrUtil.addLore(ChatColor.RESET+CivColor.Gold+"Deploys Bronze War Cannon");
		attrUtil.addLore(ChatColor.RESET+CivColor.Rose+"<Right Click To Use>");	
	}
}
