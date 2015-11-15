package com.civcraft.threading.timers;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivLog;
import com.civcraft.object.Civilization;
import com.civcraft.structure.Lab;
import com.civcraft.structure.Structure;
import com.civcraft.structure.TownHall;
import com.civcraft.threading.CivAsyncTask;
import com.civcraft.util.BlockCoord;

public class LabEventTimer extends CivAsyncTask {
	
	public static ReentrantLock runningLock = new ReentrantLock();
	
	public LabEventTimer() {
	}
	
	private void processTick() {
		for (Civilization civ : CivGlobal.getCivs()) {
			civ.lastTaxesPaidMap.clear();
		}
		
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
