package com.civcraft.camp;

import com.civcraft.main.CivGlobal;

public class CampHourlyTick implements Runnable {
	
	@Override
	public void run() {
		for (Camp camp : CivGlobal.getCamps()) {
			try {
				camp.processFirepoints();
				if (camp.isLonghouseEnabled()) {
					camp.processLonghouse();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
