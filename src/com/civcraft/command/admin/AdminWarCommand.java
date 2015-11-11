package com.civcraft.command.admin;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.civcraft.command.CommandBase;
import com.civcraft.config.CivSettings;
import com.civcraft.exception.CivException;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;
import com.civcraft.threading.TaskMaster;
import com.civcraft.threading.tasks.PlayerKickBan;
import com.civcraft.war.War;

public class AdminWarCommand extends CommandBase {

	@Override
	public void init() {
		command = "/ad war";
		displayName = "Admin War";
		
		commands.put("start", "Turns on WarTime.");
		commands.put("stop", "Turns off WarTime.");
		commands.put("resetstart", "Resets the war start time to now.");
		//commands.put("setlastwar", "takes a date of the form: DAY:MONTH:YEAR:HOUR:MIN (24 hour time)");
		commands.put("onlywarriors", "Kicks everyone who is not at war from servers and only lets at war players in.");
	}
	
	public void onlywarriors_cmd() {
		
		War.setOnlyWarriors(!War.isOnlyWarriors());
		
		if (War.isOnlyWarriors()) {
		
			for (Player player : Bukkit.getOnlinePlayers()) {
				Resident resident = CivGlobal.getResident(player);
				
				if (player.isOp() || player.hasPermission(CivSettings.MINI_ADMIN)) {
					CivMessage.send(sender, "Skipping "+player.getName()+" since he is OP or mini admin.");
					continue;
				}
				
				if (resident == null || !resident.hasTown() || 
						!resident.getTown().getCiv().getDiplomacyManager().isAtWar()) {
					
					TaskMaster.syncTask(new PlayerKickBan(player.getName(), true, false, "Kicked: Only residents 'at war' can play right now."));
				}	
			}
			
			CivMessage.global("All players 'not at war' have been kicked and cannot rejoin.");
		} else {
			CivMessage.global("All players are now allowed to join again.");
		}
	}
	
	
//	public void setlastwar_cmd() throws CivException {
//		if (args.length < 2) {
//			throw new CivException("Enter a date like DAY:MONTH:YEAR:HOUR:MIN");
//		}
//		
//		String dateStr = args[1];
//		SimpleDateFormat parser = new SimpleDateFormat("d:M:y:H:m");
//		
//		Date lastwar;
//		try {
//			lastwar = parser.parse(dateStr);
//			War.setLastWarTime(lastwar);
//			CivMessage.sendSuccess(sender, "Set last war date");
//		} catch (ParseException e) {
//			throw new CivException("Couldnt parse "+args[1]+" into a date, use format: DAY:MONTH:YEAR:HOUR:MIN");
//		}
//		
//	}
	
	public void start_cmd() {
		
		War.setWarTime(true);
		CivMessage.sendSuccess(sender, "WarTime enabled.");
	}
	
	public void stop_cmd() {
		
		War.setWarTime(false);
		CivMessage.sendSuccess(sender, "WarTime disabled.");
	}

	@Override
	public void doDefaultAction() throws CivException {
		showHelp();
	}

	@Override
	public void showHelp() {
		showBasicHelp();
	}

	@Override
	public void permissionCheck() throws CivException {
		if (sender.isOp() == false) {
			throw new CivException("Only admins can use this command.");			
		}	
	}

}
