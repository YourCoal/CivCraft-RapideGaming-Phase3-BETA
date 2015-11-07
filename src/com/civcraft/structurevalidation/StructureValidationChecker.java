package com.civcraft.structurevalidation;

import java.util.Iterator;
import java.util.Map.Entry;

import com.civcraft.exception.CivException;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivLog;
import com.civcraft.structure.Structure;
import com.civcraft.util.BlockCoord;
import com.civcraft.war.War;

public class StructureValidationChecker implements Runnable {

	@Override
	public void run() {
		Iterator<Entry<BlockCoord, Structure>> structIter = CivGlobal.getStructureIterator();
		while (structIter.hasNext()) {
			Structure struct = structIter.next().getValue();
			if (struct.getCiv().isAdminCiv()) {
				continue;
			}
			
			if (War.isWarTime()) {
				/* Don't do any work once it's war time. */
				break;
			}
			
			if (!struct.isActive()) {
				continue;
			}
			
			if (struct.isIgnoreFloating()) {
				continue;
			}
			
			try {
				CivLog.warning("Doing a structure validate...");
				struct.validate(null);
			} catch (CivException e) {
				e.printStackTrace();
			}
			
			synchronized (this) {
				try {
					this.wait(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
