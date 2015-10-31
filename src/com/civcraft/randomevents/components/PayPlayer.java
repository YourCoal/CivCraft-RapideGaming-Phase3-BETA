package com.civcraft.randomevents.components;


import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivLog;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;
import com.civcraft.randomevents.RandomEventComponent;

public class PayPlayer extends RandomEventComponent {

	@Override
	public void process() {
		String playerName = this.getParent().componentVars.get(getString("playername_var"));
		if (playerName == null) {
			CivLog.warning("No playername var for pay player.");
			return;
		}

		Resident resident = CivGlobal.getResident(playerName);
		double coins = this.getDouble("amount");
		resident.getTreasury().deposit(coins);
		CivMessage.send(resident, "You've recieved "+coins+" coins!");	
	}

}
