package com.global.perks.components;


import com.civcraft.interactive.InteractiveRenameCivOrTown;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;

public class RenameCivOrTown extends PerkComponent {

	@Override
	public void onActivate(Resident resident) {
		
		if (!resident.hasTown()) {
			CivMessage.sendError(resident, "You must be part of a civilization or town in order to rename it.");
			return;
		}
		
		resident.setInteractiveMode(new InteractiveRenameCivOrTown(resident, this));
	}
	
}
