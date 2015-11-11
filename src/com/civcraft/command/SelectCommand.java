package com.civcraft.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.threading.tasks.PlayerQuestionTask;
import com.civcraft.threading.tasks.TemplateSelectQuestionTask;

public class SelectCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			CivMessage.sendError(sender, "Only a player can execute this command.");
			return false;
		}
		
		
		if (args.length < 1) {
			CivMessage.sendError(sender, "Enter a number.");
			return false;
		}
		
		Player player = (Player)sender;
		
		PlayerQuestionTask task = (PlayerQuestionTask) CivGlobal.getQuestionTask(player.getName());
		if (task == null) {
			CivMessage.sendError(sender, "No question to respond to.");
			return false;
		}
		
		if (!(task instanceof TemplateSelectQuestionTask)) {
			CivMessage.sendError(sender, "Cannot respond to the current question.");
			return false;
		}
		
		/* We have a question, and the answer was "Accepted" so notify the task. */
		synchronized(task) {
			task.setResponse(args[0]);
			task.notifyAll();
		}
				
		return true;
	}

}
