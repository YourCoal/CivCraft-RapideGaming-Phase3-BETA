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

import org.bukkit.entity.Player;

import com.civcraft.exception.CivException;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;
import com.civcraft.threading.TaskMaster;
import com.civcraft.threading.tasks.FoundCivSync;
import com.civcraft.util.CivColor;

public class InteractiveConfirmCivCreation implements InteractiveResponse {

	@Override
	public void respond(String message, Resident resident) {
		
		Player player;
		try {
			player = CivGlobal.getPlayer(resident);
		} catch (CivException e) {
			return;
		}

		resident.clearInteractiveMode();

		if (!message.equalsIgnoreCase("yes")) {
			CivMessage.send(player, "Civilization creation cancelled.");
			return;
		}
		
		if (resident.desiredCapitolName == null || resident.desiredCivName == null) {
			CivMessage.send(player, CivColor.Rose+"Internal Error Creating Civ... =(");
			return;
		}
		
		TaskMaster.syncTask(new FoundCivSync(resident));

	}

}
