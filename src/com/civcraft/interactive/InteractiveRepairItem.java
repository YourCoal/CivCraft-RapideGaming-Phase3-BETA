package com.civcraft.interactive;

import org.bukkit.entity.Player;

import com.civcraft.exception.CivException;
import com.civcraft.lorestorage.LoreCraftableMaterial;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;
import com.civcraft.structure.Barracks;
import com.civcraft.util.CivColor;

public class InteractiveRepairItem implements InteractiveResponse {

	double cost;
	String playerName;
	LoreCraftableMaterial craftMat;
	
	public InteractiveRepairItem(double cost, String playerName, LoreCraftableMaterial craftMat) {
		this.cost = cost;
		this.playerName = playerName;
		this.craftMat = craftMat;
	}
	
	public void displayMessage() {
		Player player;
		try {
			player = CivGlobal.getPlayer(playerName);
		} catch (CivException e) {
			return;
		}
		
		CivMessage.sendHeading(player, "Repair!");
		CivMessage.send(player, CivColor.LightGreen+CivColor.BOLD+"Hello there! Would you like to repair your "+craftMat.getName()+"?");
		CivMessage.send(player, CivColor.LightGreen+CivColor.BOLD+"Looks like we can get you fixed up for "+CivColor.Yellow+CivColor.BOLD+cost+" coins.");
		CivMessage.send(player, CivColor.LightGreen+CivColor.BOLD+"If that's ok, please type 'yes'. Type anything else to cancel.");
		
	}
	
	
	@Override
	public void respond(String message, Resident resident) {
		resident.clearInteractiveMode();

		if (!message.equalsIgnoreCase("yes")) {
			CivMessage.send(resident, CivColor.LightGray+"Repair cancelled.");
			return;
		}
		
		Barracks.repairItemInHand(cost, resident.getName(), craftMat);
	}

}
