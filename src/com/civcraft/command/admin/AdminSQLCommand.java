package com.civcraft.command.admin;

import java.sql.SQLException;

import com.civcraft.camp.Camp;
import com.civcraft.command.CommandBase;
import com.civcraft.exception.CivException;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Civilization;
import com.civcraft.object.Resident;
import com.civcraft.object.Town;

public class AdminSQLCommand extends CommandBase {

	@Override
	public void init() {
		command = "/ad sql";
		displayName = "Admin sql";
		
		commands.put("disbandciv", "[civ] - disbands this civilization");
		commands.put("disbandtown", "[town] - disbands this town");
		commands.put("disbandcamp", "[camp] - disbands this camp");
		commands.put("removeres", "Remove the resident from the MySQL Database 'RESIDENTS'.");
	}
	
	public void disbandciv_cmd() throws CivException {
		Civilization civ = getNamedCiv(1);
		CivMessage.sendCiv(civ, "Your civ is has disbanded by an admin!");
		try {
			civ.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		CivMessage.sendSuccess(sender, "Civ disbanded");
		CivMessage.sendSuccess(sender, "The Civilization "+civ.getName()+" has been disbanded by an admin.");
	}
	
	public void disbandtown_cmd() throws CivException {
		Town town = getNamedTown(1);
		if (town.isCapitol()) {
			throw new CivException("Cannot disband the capitol town, disband the civilization instead.");
		}
		
		CivMessage.sendTown(town, "Your town is has disbanded by an admin!");
		try {
			town.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		CivMessage.sendSuccess(sender, "Town disbanded");
		CivMessage.sendSuccess(sender, "The Town "+town.getName()+" has been disbanded by an admin.");
	}
	
	public void disbandcamp_cmd() throws CivException {
		Camp camp = getNamedCamp(1);
		CivMessage.sendCamp(camp, "Your camp is has disbanded by an admin!");
		try {
			camp.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		CivMessage.sendSuccess(sender, "Camp disbanded");
		CivMessage.sendSuccess(sender, "The Camp "+camp.getName()+" has been disbanded by an admin.");
	}
	
	public void removeres_cmd() throws CivException {
		Resident res = getNamedResident(1);
		try {
			res.delete();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new CivException(e.getMessage());
		}
		CivGlobal.removeResident(res);
		CivMessage.sendSuccess(sender, "Resident Removed");
		CivMessage.sendSuccess(sender, res.getName()+"'s CivCaft Game Data has been reset by an admin. Relog to become real.");
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
