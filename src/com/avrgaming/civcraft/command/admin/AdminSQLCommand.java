package com.avrgaming.civcraft.command.admin;

import java.sql.SQLException;

import com.avrgaming.civcraft.camp.Camp;
import com.avrgaming.civcraft.command.CommandBase;
import com.avrgaming.civcraft.exception.CivException;
import com.avrgaming.civcraft.main.CivGlobal;
import com.avrgaming.civcraft.main.CivMessage;
import com.avrgaming.civcraft.object.Civilization;
import com.avrgaming.civcraft.object.Resident;
import com.avrgaming.civcraft.object.Town;

public class AdminSQLCommand extends CommandBase {

	@Override
	public void init() {
		command = "/ad SQL";
		displayName = "Admin SQL";
		
		commands.put("removeres", "[resident] - Remove this resident, removing their information forever.");
		commands.put("disbandcamp", "[camp] - Disband this camp, removing it forever.");
		commands.put("disbandtown", "[town] - Disband this town, removing it forever.");
		commands.put("disbandciv", "[civ] - Disband this civ, removing it forever.");
	}
	
	public void removeres_cmd() throws CivException {
		Resident resident = getNamedResident(1);
		try {
			resident.delete();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new CivException(e.getMessage());
		}
		CivGlobal.removeResident(resident);
		CivMessage.sendSuccess(sender, "Resident Removed");
	}
	
	public void destroycamp_cmd() throws CivException {
		Camp camp = getNamedCamp(1);		
		camp.destroy();
		CivMessage.sendSuccess(sender, "Camp Disbanded");
		CivMessage.sendCamp(camp, "Your camp is has disbanded by an admin!");
	}
	
	public void disbandtown_cmd() throws CivException {
		Town town = getNamedTown(1);
		if (town.isCapitol()) {
			throw new CivException("Cannot disband the capitol town! Disband the civilization instead.");
		}
		try {
			town.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		CivMessage.sendSuccess(sender, "Town Disbanded");
		CivMessage.sendTown(town, "Your town is has disbanded by an admin!");
	}
	
	public void disbandciv_cmd() throws CivException {
		Civilization civ = getNamedCiv(1);
		try {
			civ.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		CivMessage.sendSuccess(sender, "Civilization Disbanded");
		CivMessage.sendCiv(civ, "Your civilization is has disbanded by an admin!");
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