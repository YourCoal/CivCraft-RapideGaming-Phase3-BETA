package com.civcraft.randomevents.components;

import com.civcraft.config.CivSettings;
import com.civcraft.object.Resident;
import com.civcraft.randomevents.RandomEventComponent;
import com.global.perks.PlatinumManager;

public class GivePlatinum extends RandomEventComponent {

	@Override
	public void process() {
		for (Resident resident : this.getParentTown().getResidents()) {
			PlatinumManager.givePlatinumDaily(resident, 
					CivSettings.platinumRewards.get("randomEventSuccess").name,
					CivSettings.platinumRewards.get("randomEventSuccess").amount, 
					this.getString("message"));	
		}

	}

}
