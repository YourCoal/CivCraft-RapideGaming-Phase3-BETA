package com.avrgaming.civcraft.threading.sync;

import java.util.Collection;

import org.bukkit.entity.Player;

import com.avrgaming.civcraft.exception.CivException;
import com.avrgaming.civcraft.main.CivGlobal;
import com.avrgaming.civcraft.object.Resident;

public class SyncUpdateTags implements Runnable {

	Collection<Resident> residentsToSendUpdate;
	String playerToUpdate;
	
	public SyncUpdateTags(String playerToUpdate, Collection<Resident> residentsToSendUpdate) {
		this.residentsToSendUpdate = residentsToSendUpdate;
		this.playerToUpdate = playerToUpdate;
	}

	@Override
	public void run() {
		try {
			Player player = CivGlobal.getPlayer(playerToUpdate);		
			for (Resident resident : residentsToSendUpdate) {
				try {
					Player resPlayer = CivGlobal.getPlayer(resident);
					if (player == resPlayer) {
						continue;
					}
				} catch (CivException e) {
				}
			}
		} catch (CivException e1) {
			return;
		}		
	}
}
