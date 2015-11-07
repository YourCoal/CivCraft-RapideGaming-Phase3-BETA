package com.civcraft.threading.timers;

import java.util.Iterator;
import java.util.Map.Entry;

import com.civcraft.main.CivGlobal;
import com.civcraft.structure.Structure;
import com.civcraft.structure.wonders.Wonder;
import com.civcraft.util.BlockCoord;

public class RegenTimer implements Runnable {

	@Override
	public void run() {
		Iterator<Entry<BlockCoord, Structure>> iter = CivGlobal.getStructureIterator();
		
		while(iter.hasNext()) {
			Structure struct = iter.next().getValue();
			struct.processRegen();
		}
		
		for (Wonder wonder : CivGlobal.getWonders()) {
			wonder.processRegen();
		}
	}

}
