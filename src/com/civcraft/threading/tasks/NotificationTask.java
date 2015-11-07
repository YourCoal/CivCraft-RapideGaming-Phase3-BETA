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
package com.civcraft.threading.tasks;

import org.bukkit.entity.Player;

import com.civcraft.exception.CivException;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;

public class NotificationTask implements Runnable {
	//private Server server;
	String message;
	String playerName;
	
	public NotificationTask(String playerName, String msg) {
		message = msg;
		this.playerName = playerName;
	}

	@Override
	public void run() {
		try {
			Player player = CivGlobal.getPlayer(playerName);
			CivMessage.send(player, message);
		} catch (CivException e) {
			//Player not online
		}
		
	}
}