package com.civcraft.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;
import com.civcraft.threading.tasks.CivLeaderQuestionTask;
import com.civcraft.threading.tasks.PlayerQuestionTask;

public class AcceptCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		if (!(sender instanceof Player)) {
			CivMessage.sendError(sender, "Only a player can execute this command.");
			return false;
		}
		
		Player player = (Player)sender;
		
		PlayerQuestionTask task = (PlayerQuestionTask) CivGlobal.getQuestionTask(player.getName());
		if (task != null) {
			/* We have a question, and the answer was "Accepted" so notify the task. */
			synchronized(task) {
				task.setResponse("accept");
				task.notifyAll();
			}
			return true;
		}

		Resident resident = CivGlobal.getResident(player);
		if (resident.hasTown()) {
			if (resident.getCiv().getLeaderGroup().hasMember(resident)) {
				CivLeaderQuestionTask civTask = (CivLeaderQuestionTask) CivGlobal.getQuestionTask("civ:"+resident.getCiv().getName());
				
				synchronized(civTask) {
					civTask.setResponse("accept");
					civTask.setResponder(resident);
					civTask.notifyAll();
				}
				return true;
			}
		}
		

		CivMessage.sendError(sender, "No question to respond to.");
		return false;			
	}

}
