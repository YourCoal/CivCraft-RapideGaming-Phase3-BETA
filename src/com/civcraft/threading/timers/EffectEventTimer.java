package com.civcraft.threading.timers;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivLog;
import com.civcraft.main.CivMessage;
import com.civcraft.object.AttrSource;
import com.civcraft.object.Civilization;
import com.civcraft.object.Town;
import com.civcraft.structure.Lab;
import com.civcraft.structure.Mine;
import com.civcraft.structure.Structure;
import com.civcraft.structure.TownHall;
import com.civcraft.threading.CivAsyncTask;
import com.civcraft.util.BlockCoord;
import com.civcraft.util.CivColor;

public class EffectEventTimer extends CivAsyncTask {
	
	//public static Boolean running = false;
	
	public static ReentrantLock runningLock = new ReentrantLock();
	
	public EffectEventTimer() {
	}

	private void processTick() {
		/* Clear the last taxes so they don't accumulate. */
		for (Civilization civ : CivGlobal.getCivs()) {
			civ.lastTaxesPaidMap.clear();
		}
		
		//HashMap<Town, Integer> cultureGenerated = new HashMap<Town, Integer>();
		
		// Loop through each structure, if it has an update function call it in another async process
		Iterator<Entry<BlockCoord, Structure>> iter = CivGlobal.getStructureIterator();
		
		while(iter.hasNext()) {
			Structure struct = iter.next().getValue();
			TownHall townhall = struct.getTown().getTownHall();

			if (townhall == null) {
				continue;
			}

			if (!struct.isActive())
				continue;

			struct.onEffectEvent();

			if (struct.getEffectEvent() == null || struct.getEffectEvent().equals(""))
				continue;
			
			String[] split = struct.getEffectEvent().toLowerCase().split(":"); 
			switch (split[0]) {
			case "process_mine":
				if (struct instanceof Mine) {
					Mine mine = (Mine)struct;
					try {
						mine.process_mine(this);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			case "process_lab":
				if (struct instanceof Lab) {
					Lab lab = (Lab)struct;
					try {
						lab.process_lab(this);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			}
		}
		
		/* Process any hourly attributes for this town.
		 *  - Culture */
		for (Town town : CivGlobal.getTowns()) {
			double cultureGenerated;
			
			// highjack this loop to display town hall warning.
			TownHall townhall = town.getTownHall();
			if (townhall == null) {
				CivMessage.sendTown(town, CivColor.Yellow+"Your town does not have a town hall! Structures have no effect!");
				continue;
			}
							
			AttrSource cultureSources = town.getCulture();
			// Get amount generated after culture rate/bonus.
			cultureGenerated = cultureSources.total;
			cultureGenerated = Math.round(cultureGenerated);
			town.addAccumulatedCulture(cultureGenerated);
			cultureGenerated = Math.round(cultureGenerated);
			CivMessage.sendTown(town, CivColor.LightGreen+"Generated "+CivColor.LightPurple+cultureGenerated+CivColor.LightGreen+" culture.");
		}
		/* Checking for expired vassal states. */
		CivGlobal.checkForExpiredRelations();
	}
	
	@Override
	public void run() {
		
		if (runningLock.tryLock()) {
			try {
				processTick();
			} finally {
				runningLock.unlock();
			}
		} else {
			CivLog.error("COULDN'T GET LOCK FOR HOURLY TICK. LAST TICK STILL IN PROGRESS?");
		}		
	}
}
