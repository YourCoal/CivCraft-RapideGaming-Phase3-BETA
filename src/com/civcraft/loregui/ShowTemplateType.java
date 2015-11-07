package com.civcraft.loregui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.civcraft.lorestorage.LoreGuiItem;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivLog;
import com.civcraft.object.Resident;
import com.global.perks.Perk;

public class ShowTemplateType implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		// TODO Auto-generated method stub
		Resident resident = CivGlobal.getResident((Player)event.getWhoClicked());
		String perk_id = LoreGuiItem.getActionData(stack, "perk");
		Perk perk = resident.perks.get(perk_id);
		if (perk != null) {
			if (perk.getIdent().startsWith("template_test"))
			{
				resident.showTemplatePerks("test");
			}
//			else if (perk.getIdent().startsWith("template_aztec"))
//			{
//				resident.showTemplatePerks("aztec");
//			}
		} else {
			CivLog.error("Couldn't activate perk:"+perk_id+" cause it wasn't found in perks hashmap.");
		}
	}

}