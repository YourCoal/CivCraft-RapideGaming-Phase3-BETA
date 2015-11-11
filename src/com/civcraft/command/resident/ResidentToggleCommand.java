package com.civcraft.command.resident;

import com.civcraft.command.CommandBase;
import com.civcraft.exception.CivException;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;

public class ResidentToggleCommand extends CommandBase {

	@Override
	public void init() {
		command = "/resident toggle";
		displayName = "Resident Toggle";	
		
		commands.put("map", "Toggles a ASCII map which shows town locations of claimed town chunks.");
		commands.put("info", "Toggles a message displayed as you enter each culture chunk. Tells you what it would generate the town.");
		commands.put("showtown", "Toggles displaying of [Town] messages.");
		commands.put("showciv", "Toggles displaying of [Civ] messages.");
		commands.put("showscout", "Toggles displaying of scout tower messages.");
		commands.put("combatinfo", "Toggles displaying of combat information.");
		commands.put("itemdrops", "Toggles displaying of item drops.");
		
	}
	public void itemdrops_cmd() throws CivException {
		toggle();
	}
	
	public void map_cmd() throws CivException {
		toggle();
	}
	public void showtown_cmd() throws CivException {
		toggle();
	}
	
	public void showciv_cmd() throws CivException  {
		toggle();
	}
	
	public void showscout_cmd() throws CivException  {
		toggle();
	}
	
	public void info_cmd() throws CivException {
		toggle();
	}
	
	public void combatinfo_cmd() throws CivException {
		toggle();
	}
	
	private void toggle() throws CivException {
		Resident resident = getResident();
	
		boolean result;
		switch(args[0].toLowerCase()) {
		case "map":
			resident.setShowMap(!resident.isShowMap());
			result = resident.isShowMap();
			break;
		case "showtown":
			resident.setShowTown(!resident.isShowTown());
			result = resident.isShowTown();
			break;
		case "showciv":
			resident.setShowCiv(!resident.isShowCiv());
			result = resident.isShowCiv();
			break;
		case "showscout":
			resident.setShowScout(!resident.isShowScout());
			result = resident.isShowScout();
			break;
		case "info":
			resident.setShowInfo(!resident.isShowInfo());
			result = resident.isShowInfo();
			break;
		case "combatinfo":
			resident.setCombatInfo(!resident.isCombatInfo());
			result = resident.isCombatInfo();
			break;
		case "itemdrops":
			resident.toggleItemMode();
			return;
		default:
			throw new CivException("Unknown flag "+args[0]);
		}
		
		resident.save();
		CivMessage.sendSuccess(sender, "Toggled "+args[0]+" to "+result);
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
