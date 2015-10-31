package com.civcraft.interactive;

import com.civcraft.camp.WarCamp;
import com.civcraft.config.ConfigBuildableInfo;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;

public class InteractiveWarCampFound implements InteractiveResponse {

	ConfigBuildableInfo info;
	
	public InteractiveWarCampFound(ConfigBuildableInfo info) {
		this.info = info;
	}
	
	@Override
	public void respond(String message, Resident resident) {
		resident.clearInteractiveMode();

		if (!message.equalsIgnoreCase("yes")) {
			CivMessage.send(resident, "War Camp creation cancelled.");
			return;
		}
		
		WarCamp.newCamp(resident, info);
	}

}
