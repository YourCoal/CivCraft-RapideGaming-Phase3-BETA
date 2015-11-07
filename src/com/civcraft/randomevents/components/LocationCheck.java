package com.civcraft.randomevents.components;

import java.util.List;

import com.civcraft.cache.PlayerLocationCache;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivLog;
import com.civcraft.object.Resident;
import com.civcraft.randomevents.RandomEventComponent;
import com.civcraft.util.BlockCoord;

public class LocationCheck extends RandomEventComponent {

	@Override
	public void process() {
	}
	
	public boolean onCheck() { 
		
		String varname = this.getString("varname");
		String locString = this.getParent().componentVars.get(varname);
		
		if (locString == null) {
			CivLog.warning("Couldn't get var name:"+varname+" for location check component.");
			return false;
		}
		
		BlockCoord bcoord = new BlockCoord(locString);
		double radiusSquared = 2500.0; /* 50 block radius */
		List<PlayerLocationCache> cache = PlayerLocationCache.getNearbyPlayers(bcoord, radiusSquared);
		
		for (PlayerLocationCache pc : cache) {
			Resident resident = CivGlobal.getResident(pc.getName());
			if (resident.getTown() == this.getParentTown()) {
				return true;
			}
		}
		
		return false; 
		
	}

}
