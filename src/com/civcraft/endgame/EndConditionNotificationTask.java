package com.civcraft.endgame;

import java.util.ArrayList;

import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Civilization;
import com.civcraft.sessiondb.SessionEntry;
import com.civcraft.util.CivColor;

public class EndConditionNotificationTask implements Runnable {

	@Override
	public void run() {
		
		for (EndGameCondition endCond : EndGameCondition.endConditions) {
			ArrayList<SessionEntry> entries = CivGlobal.getSessionDB().lookup(endCond.getSessionKey());
			if (entries.size() == 0) {
				continue;
			}
			
			for (SessionEntry entry : entries) {
				Civilization civ = EndGameCondition.getCivFromSessionData(entry.value);
				Integer daysLeft = endCond.getDaysToHold() - endCond.getDaysHeldFromSessionData(entry.value);
				CivMessage.global(CivColor.LightBlue+CivColor.BOLD+civ.getName()+CivColor.White+" is "+
				CivColor.Yellow+CivColor.BOLD+daysLeft+CivColor.White+" days away from a "+CivColor.LightPurple+CivColor.BOLD+endCond.getVictoryName()+
				CivColor.White+" victory! Capture their capital to prevent it!");
			}
		}
		
	}

}
