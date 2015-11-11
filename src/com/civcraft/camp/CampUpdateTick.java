package com.civcraft.camp;

import com.civcraft.threading.CivAsyncTask;

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
		
	}

}
