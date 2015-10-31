package com.civcraft.loregui;


import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.civcraft.lorestorage.LoreGuiItem;
import com.civcraft.main.CivGlobal;
import com.civcraft.object.Resident;

public class ShowPerkPage implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		
		Resident resident = CivGlobal.getResident((Player)event.getWhoClicked());
		resident.showPerkPage(Integer.valueOf(LoreGuiItem.getActionData(stack, "page")));				
	}

}
