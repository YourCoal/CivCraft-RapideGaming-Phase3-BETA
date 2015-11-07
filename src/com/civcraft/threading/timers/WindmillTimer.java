package com.civcraft.threading.timers;

import java.util.Iterator;
import java.util.Map.Entry;

import com.civcraft.main.CivGlobal;
import com.civcraft.structure.Structure;
import com.civcraft.structure.Windmill;
import com.civcraft.util.BlockCoord;
import com.civcraft.war.War;

public class WindmillTimer implements Runnable {

	@Override
	public void run() {
		if (War.isWarTime()) {
			return;
		}
		
		Iterator<Entry<BlockCoord, Structure>> iter = CivGlobal.getStructureIterator();
		while(iter.hasNext()) {
			Structure struct = iter.next().getValue();
			if (struct instanceof Windmill) {
				((Windmill)struct).processWindmill();
			}
		}
	}

}
