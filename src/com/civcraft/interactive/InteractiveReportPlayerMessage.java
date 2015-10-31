package com.civcraft.interactive;

import org.bukkit.entity.Player;

import com.civcraft.exception.CivException;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;
import com.global.reports.ReportManager;
import com.global.reports.ReportManager.ReportType;

public class InteractiveReportPlayerMessage implements InteractiveResponse {

	ReportType type;
	String playerName;
	
	public InteractiveReportPlayerMessage(String playerName, ReportType type) {
		this.type = type;
		this.playerName = playerName;
	}
	
	@Override
	public void respond(String message, Resident resident) {
		Player player;
		try {
			player = CivGlobal.getPlayer(resident);
		} catch (CivException e) {
			return;
		}
		
		ReportManager.reportPlayer(playerName, type, message, resident.getName());
		CivMessage.sendSuccess(player, playerName+" was reported. Thank you.");
		resident.clearInteractiveMode();
	}

}
