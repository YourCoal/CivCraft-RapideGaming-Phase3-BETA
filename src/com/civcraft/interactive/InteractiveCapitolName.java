/*************************************************************************
 * 
 * AVRGAMING LLC
 * __________________
 * 
 *  [2013] AVRGAMING LLC
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of AVRGAMING LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to AVRGAMING LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from AVRGAMING LLC.
 */
package com.civcraft.interactive;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.civcraft.command.town.TownCommand;
import com.civcraft.exception.CivException;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;
import com.civcraft.threading.TaskMaster;
import com.civcraft.util.CivColor;

public class InteractiveCapitolName implements InteractiveResponse {

	@Override
	public void respond(String message, Resident resident) {
		
		Player player;
		try {
			player = CivGlobal.getPlayer(resident);
		} catch (CivException e) {
			return;
		}

		if (message.equalsIgnoreCase("cancel")) {
			CivMessage.send(player, "Civilization creation cancelled.");
			resident.clearInteractiveMode();
			return;
		}
		
		if (!StringUtils.isAlpha(message)) {
			CivMessage.send(player, CivColor.Rose+ChatColor.BOLD+"Town names must only contain letters(A-Z). Enter another name.");
			return;
		}
		
		message = message.replace(" ", "_");
		message = message.replace("\"", "");
		message = message.replace("\'", "");
		
		resident.desiredCapitolName = message;
		CivMessage.send(player, CivColor.LightGreen+"The Civilization of "+CivColor.Yellow+resident.desiredCivName+
				CivColor.LightGreen+"! And its capitol will be "+CivColor.Yellow+resident.desiredCapitolName+CivColor.LightGreen+"!");
		CivMessage.sendHeading(player, "Survey Results");
		
		class SyncTask implements Runnable {
			String playerName;
			
			
			public SyncTask(String name) {
				this.playerName = name;
			}

			@Override
			public void run() {
				Player player;
				try {
					player = CivGlobal.getPlayer(playerName);
				} catch (CivException e) {
					return;
				}
				
				Resident resident = CivGlobal.getResident(playerName);
				if (resident == null) {
					return;
				}
				
				CivMessage.send(player, TownCommand.survey(player.getLocation()));
				CivMessage.send(player, "");
				CivMessage.send(player, CivColor.LightGreen+ChatColor.BOLD+"Are you sure? Type 'yes' and I will create this Civilization. Type anything else, and I will forget the whole thing.");
				resident.setInteractiveMode(new InteractiveConfirmCivCreation());				
			}
		
		}

		TaskMaster.syncTask(new SyncTask(resident.getName())); 

		return;
	}

}
