package com.avrgaming.civcraft.threading.timers;

import java.util.ArrayList;

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.main.CivGlobal;
import com.avrgaming.civcraft.main.CivMessage;
import com.avrgaming.civcraft.object.Civilization;
import com.avrgaming.civcraft.sessiondb.SessionEntry;

public class ChangeGovernmentTimer implements Runnable {

	@Override
	public void run() {
		for (Civilization civ : CivGlobal.getCivs()) {
			if (civ.getGovernment().id.equalsIgnoreCase("gov_anarchy")) {
				String key = "changegov_"+civ.getId();
				ArrayList<SessionEntry> entries;
				
				entries = CivGlobal.getSessionDB().lookup(key);
				if (entries == null || entries.size() < 1) {
					civ.setGovernment("gov_tribalism");
					return;
				}
				
				SessionEntry se = entries.get(0);
				int duration = 3600;
				if (CivGlobal.testFileFlag("debug")) {
					duration = 1;
				}
			
				if (CivGlobal.hasTimeElapsed(se, (Integer)CivSettings.getIntegerGovernment("anarchy_duration")*duration)) {
					civ.setGovernment(se.value);
					CivMessage.global(civ.getName()+" has emerged from anarchy and has adopted "+CivSettings.governments.get(se.value).displayName);
					CivGlobal.getSessionDB().delete_all(key);
					civ.save();
				} 
			}
		}		
	}
}
