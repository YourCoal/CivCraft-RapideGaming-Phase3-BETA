package com.civcraft.loregui;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.civcraft.config.CivSettings;
import com.civcraft.config.ConfigBuildableInfo;
import com.civcraft.exception.CivException;
import com.civcraft.lorestorage.LoreGuiItem;
import com.civcraft.main.CivData;
import com.civcraft.main.CivGlobal;
import com.civcraft.object.Resident;
import com.civcraft.structure.Structure;
import com.civcraft.threading.TaskMaster;
import com.civcraft.tutorial.CivTutorial;
import com.civcraft.util.CivColor;
import com.civcraft.util.ItemManager;
import com.global.perks.Perk;

public class BuildChooseTemplate implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		Player player = (Player)event.getWhoClicked();
		Resident resident = CivGlobal.getResident(player);
		ConfigBuildableInfo sinfo = CivSettings.structures.get(LoreGuiItem.getActionData(stack, "info"));
		Structure struct;
		try {
			struct = Structure.newStructure(player.getLocation(), sinfo.id, resident.getTown());
		} catch (CivException e) {
			e.printStackTrace();
			return;
		}
		
		/* Look for any custom template perks and ask the player if they want to use them. */
		ArrayList<Perk> perkList = struct.getTown().getTemplatePerks(struct, resident, struct.info);		
		ArrayList<Perk> personalUnboundPerks = resident.getUnboundTemplatePerks(perkList, struct.info);
		//if (perkList.size() != 0 || personalUnboundPerks.size() != 0) {
			/* Store the pending buildable. */
		resident.pendingBuildable = struct;
		
		/* Build an inventory full of templates to select. */
		Inventory inv = Bukkit.getServer().createInventory(player, CivTutorial.MAX_CHEST_SIZE*9);
		ItemStack infoRec = LoreGuiItem.build("Default "+struct.getDisplayName(), 
				ItemManager.getId(Material.WRITTEN_BOOK), 
				0, CivColor.Gold+"<Click To Build>");
		infoRec = LoreGuiItem.setAction(infoRec, "BuildWithTemplate");
		inv.addItem(infoRec);
		
		for (Perk perk : perkList) {
			infoRec = LoreGuiItem.build(perk.getDisplayName(), 
					perk.configPerk.type_id, 
					perk.configPerk.data, CivColor.Gold+"<Click To Build>",
					CivColor.Gray+"Provided by: "+CivColor.LightBlue+perk.provider);
			infoRec = LoreGuiItem.setAction(infoRec, "BuildWithTemplate");
			infoRec = LoreGuiItem.setActionData(infoRec, "perk", perk.getIdent());
			inv.addItem(infoRec);
		}
		
		for (Perk perk : personalUnboundPerks) {
			infoRec = LoreGuiItem.build(perk.getDisplayName(), 
					CivData.BEDROCK, 
					perk.configPerk.data, CivColor.Gold+"<Click To Bind>",
					CivColor.Gray+"Unbound Temple",
					CivColor.Gray+"You own this template.",
					CivColor.Gray+"The town is missing it.",
					CivColor.Gray+"Click to bind to town first.",
					CivColor.Gray+"Then build again.");				
			infoRec = LoreGuiItem.setAction(infoRec, "ActivatePerk");
			infoRec = LoreGuiItem.setActionData(infoRec, "perk", perk.getIdent());
			
		}
		
		TaskMaster.syncTask(new OpenInventoryTask(player, inv));
		return;		
	}
}
