package com.avrgaming.civcraft.threading.timers;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import com.avrgaming.civcraft.camp.Camp;
import com.avrgaming.civcraft.camp.CampUpdateTick;
import com.avrgaming.civcraft.main.CivGlobal;
import com.avrgaming.civcraft.structure.Structure;
import com.avrgaming.civcraft.structure.wonders.Wonder;
import com.avrgaming.civcraft.threading.CivAsyncTask;
import com.avrgaming.civcraft.threading.TaskMaster;
import com.avrgaming.civcraft.threading.tasks.ShipyardAsyncTask;
import com.avrgaming.civcraft.util.BlockCoord;

public class ShipyardFishTimer extends CivAsyncTask {
		
	public static ReentrantLock lock = new ReentrantLock();
	
	public ShipyardFishTimer() {
	}
	
	@Override
	public void run() {		
		if (!lock.tryLock()) {
			return;
		}
		
		try {
			// Loop through each structure, if it has an update function call it in another async process
			Iterator<Entry<BlockCoord, Structure>> iter = CivGlobal.getStructureIterator();
			while(iter.hasNext()) {
				Structure struct = iter.next().getValue();
				
				if (!struct.isActive())
					continue;
				
				try {
					if (struct.getUpdateEvent() != null && !struct.getUpdateEvent().equals("")) {
						if (struct.getUpdateEvent().equals("shipyard_trade")) {
							if (!CivGlobal.shippingEnabled) {
								continue;
							}
							TaskMaster.asyncTask("shipyard-"+struct.getCorner().toString(), new ShipyardAsyncTask(struct), 0);
						}
					}
					struct.onUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			for (Wonder wonder : CivGlobal.getWonders()) {
				wonder.onUpdate();
			}
			
			for (Camp camp : CivGlobal.getCamps()) {
				if (!camp.sifterLock.isLocked()) {
					TaskMaster.asyncTask(new CampUpdateTick(camp), 0);
				}
			}
		} finally {
			lock.unlock();
		}
	}
}