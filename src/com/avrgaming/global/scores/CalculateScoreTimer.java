package com.avrgaming.global.scores;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

import com.avrgaming.civcraft.main.CivGlobal;
import com.avrgaming.civcraft.object.Civilization;
import com.avrgaming.civcraft.object.Town;
import com.avrgaming.civcraft.sessiondb.SessionEntry;
import com.avrgaming.civcraft.threading.CivAsyncTask;

public class CalculateScoreTimer extends CivAsyncTask {
	
	@Override
	public void run() {
		if (!CivGlobal.scoringEnabled) {
			return;
		}
		
		ArrayList<SessionEntry> entries = CivGlobal.getSessionDB().lookup("endgame:winningCiv");
		if (entries.size() != 0) {
			/* we have a winner, do not accumulate scores anymore. */
			return;
		}
		
		TreeMap<Integer, Civilization> civScores = new TreeMap<Integer, Civilization>();
		for (Civilization civ : CivGlobal.getCivs()) {
			if (civ.isAdminCiv()) {
				continue;
			}
			civScores.put(civ.getScore(), civ);
			
			try {
				ScoreManager.UpdateScore(civ, civ.getScore());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		TreeMap<Integer, Town> townScores = new TreeMap<Integer, Town>();	
		for (Town town : CivGlobal.getTowns()) {
			if (town.getCiv().isAdminCiv()) {
				continue;
			}
			try {
				townScores.put(town.getScore(), town);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				ScoreManager.UpdateScore(town, town.getScore());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		synchronized(CivGlobal.civilizationScores) {
			CivGlobal.civilizationScores = civScores;
		}
		
		synchronized(CivGlobal.townScores) {
			CivGlobal.townScores = townScores;
		}
	}
}
