package com.civcraft.command.debug;

import com.civcraft.camp.Camp;
import com.civcraft.command.CommandBase;
import com.civcraft.exception.CivException;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;
import com.civcraft.util.BlockCoord;

public class DebugCampCommand extends CommandBase {

	@Override
	public void init() {
		command = "/dbg test ";
		displayName = "Test Commands";
		
		commands.put("growth", "[name] - Shows a list of this player's camp growth spots.");
		
	}
	
	public void growth_cmd() throws CivException {
		Resident resident = getNamedResident(1);
		
		if (!resident.hasCamp()) {
			throw new CivException("This guy doesnt have a camp.");
		}
		
		Camp camp = resident.getCamp();
		
		CivMessage.sendHeading(sender, "Growth locations");
		
		String out = "";
		for (BlockCoord coord : camp.growthLocations) {
			boolean inGlobal = CivGlobal.vanillaGrowthLocations.contains(coord);
			out += coord.toString()+" in global:"+inGlobal;
		}
		
		CivMessage.send(sender, out);
		
	}

	@Override
	public void doDefaultAction() throws CivException {
		showHelp();
	}

	@Override
	public void showHelp() {
		showBasicHelp();
	}

	@Override
	public void permissionCheck() throws CivException {
		
	}

}
