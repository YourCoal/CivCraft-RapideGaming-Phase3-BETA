package com.civcraft.threading.timers;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import com.civcraft.main.CivGlobal;
import com.civcraft.structure.Structure;
import com.civcraft.threading.CivAsyncTask;
import com.civcraft.threading.TaskMaster;
import com.civcraft.threading.tasks.QuarryAsyncTask;
import com.civcraft.util.BlockCoord;

public class RandomStructureTimer extends CivAsyncTask {
	
	public static ReentrantLock lock = new ReentrantLock();
	
	public RandomStructureTimer() {
	}
	
	@Override
	public void run() {		
		if (!lock.tryLock()) {
			return;
		}
		
		try {
			Iterator<Entry<BlockCoord, Structure>> iter = CivGlobal.getStructureIterator();
			while(iter.hasNext()) {
				Structure struct = iter.next().getValue();
				if (!struct.isActive())
					continue;
				try {
					if (struct.getUpdateEvent() != null && !struct.getUpdateEvent().equals("")) {
						if (struct.getUpdateEvent().equals("quarry_process")) {
							if (!CivGlobal.quarriesEnabled) {
								continue;
							}
							TaskMaster.asyncTask("quarry-"+struct.getCorner().toString(), new QuarryAsyncTask(struct), 0);
//						} else if (struct.getUpdateEvent().equals("fish_hatchery_process")) {
//							if (!CivGlobal.fisheryEnabled) {
//								continue;
//							}
//							TaskMaster.asyncTask("fishHatchery-"+struct.getCorner().toString(), new FisheryAsyncTask(struct), 0);
						}
					}
					struct.onUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} finally {
			lock.unlock();
		}
	}
}