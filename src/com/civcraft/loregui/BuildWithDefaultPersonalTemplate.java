package com.civcraft.loregui;

import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.civcraft.config.ConfigBuildableInfo;
import com.civcraft.exception.CivException;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;
import com.civcraft.structure.Buildable;
import com.civcraft.structurevalidation.StructureValidator;
import com.civcraft.template.Template;
import com.civcraft.template.Template.TemplateType;
import com.civcraft.threading.TaskMaster;

public class BuildWithDefaultPersonalTemplate implements GuiAction {

	@Override
	public void performAction(InventoryClickEvent event, ItemStack stack) {
		Player player = (Player)event.getWhoClicked();
		Resident resident = CivGlobal.getResident(player);
		ConfigBuildableInfo info = resident.pendingBuildableInfo;
		
		try {
			String path = Template.getTemplateFilePath(info.template_base_name, Template.getDirection(player.getLocation()), TemplateType.STRUCTURE, "default");
			Template tpl;
			try {
				//tpl.load_template(path);
				tpl = Template.getTemplate(path, player.getLocation());
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			Location centerLoc = Buildable.repositionCenterStatic(player.getLocation(), info, Template.getDirection(player.getLocation()), (double)tpl.size_x, (double)tpl.size_z);	
			//Buildable.validate(player, null, tpl, centerLoc, resident.pendingCallback);
			TaskMaster.asyncTask(new StructureValidator(player, tpl.getFilepath(), centerLoc, resident.pendingCallback), 0);
			player.closeInventory();

		} catch (CivException e) {
			CivMessage.sendError(player, e.getMessage());
		}		
	}

}
