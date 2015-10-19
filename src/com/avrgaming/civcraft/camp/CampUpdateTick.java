package com.avrgaming.civcraft.camp;

import com.avrgaming.civcraft.threading.CivAsyncTask;

public class CampUpdateTick extends CivAsyncTask {

	private Camp camp;
	
	public CampUpdateTick(Camp camp) {
		this.camp = camp;
	}
	
	@Override
	public void run() {
		if(camp.sifterLock.tryLock()) {
			try {
				if (camp.isSifterEnabled()) {
					camp.sifter.run(this);
				}
			} finally {
				camp.sifterLock.unlock();
			}
		}
		if(camp.cpLock.tryLock()) {
			try {
				if (camp.isCookingPotEnabled()) {
					camp.cp.run(this);
				}
			} finally {
				camp.cpLock.unlock();
			}
		}
	}
}
