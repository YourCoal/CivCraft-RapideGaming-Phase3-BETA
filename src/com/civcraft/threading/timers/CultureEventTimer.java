package com.civcraft.threading.timers;

import java.util.concurrent.locks.ReentrantLock;

import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivLog;
import com.civcraft.main.CivMessage;
import com.civcraft.object.AttrSource;
import com.civcraft.object.Town;
import com.civcraft.structure.TownHall;
import com.civcraft.threading.CivAsyncTask;
import com.civcraft.util.CivColor;

public class CultureEventTimer extends CivAsyncTask {
	
	//public static Boolean running = false;
	
	public static ReentrantLock runningLock = new ReentrantLock();
	
	public CultureEventTimer() {
	}

	private void processTick() {
		for (Town town : CivGlobal.getTowns()) {
			double cultureGenerated;
			TownHall townhall = town.getTownHall();
			if (townhall == null) {
				CivMessage.sendTown(town, CivColor.Yellow+"Your town does not have a town hall! Structures have no effect!");
				continue;
			}
			AttrSource cultureSources = town.getCulture();
			cultureGenerated = cultureSources.total;
			cultureGenerated = Math.round(cultureGenerated);
			town.addAccumulatedCulture(cultureGenerated);
			cultureGenerated = Math.round(cultureGenerated);
			CivMessage.sendTown(town, CivColor.LightGreen+"Generated a total of "+CivColor.LightPurple+cultureGenerated+CivColor.LightGreen+" culture.");
		}
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
