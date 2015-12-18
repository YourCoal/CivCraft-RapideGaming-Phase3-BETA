package com.civcraft.threading.timers;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import com.civcraft.exception.InvalidConfiguration;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivLog;
import com.civcraft.object.Civilization;
import com.civcraft.structure.Shipyard;
import com.civcraft.structure.Structure;
import com.civcraft.structure.TownHall;
import com.civcraft.threading.CivAsyncTask;
import com.civcraft.util.BlockCoord;

public class TradingEventTimer extends CivAsyncTask {
	
	public static ReentrantLock runningLock = new ReentrantLock();
	
	public TradingEventTimer() {
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
			case "process_shipyard_trade":
				if (struct instanceof Shipyard) {
					Shipyard shipyard = (Shipyard)struct;
					try {
						shipyard.process_shipyard_trade(this);
					} catch (InterruptedException | InvalidConfiguration e) {
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
