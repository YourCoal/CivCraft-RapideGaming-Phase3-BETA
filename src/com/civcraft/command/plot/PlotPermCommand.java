package com.civcraft.command.plot;

import org.bukkit.entity.Player;

import com.civcraft.command.CommandBase;
import com.civcraft.exception.CivException;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.TownChunk;
import com.civcraft.permission.PermissionNode;
import com.civcraft.util.CivColor;

public class PlotPermCommand extends CommandBase {

	@Override
	public void init() {
		command = "/plot perm";
		displayName = "Plot Perm";
		
		commands.put("set", "Sets a permission flag on or off.");
	}
	
	public void set_cmd() throws CivException {
		Player player = (Player)sender;
		
		TownChunk tc = CivGlobal.getTownChunk(player.getLocation());
		if (tc == null) {
			throw new CivException("Plot is not part of a town.");
		}
		
		if (args.length < 4) {
			showPermCmdHelp();
			throw new CivException("Incorrect number of arguments");	
		}
		
		PermissionNode node = null;
		switch(args[1].toLowerCase()) {
		case "build":
			node = tc.perms.build;
			break;
		case "destroy":
			node = tc.perms.destroy;
			break;
		case "interact":
			node = tc.perms.interact;
			break;
		case "itemuse":
			node = tc.perms.itemUse;
			break;
		case "reset":
			//TODO implement permissions reset.
			break;
		default:
			showPermCmdHelp();
			throw new CivException("Incorrect Command Arguments.");
		}
		
		if (node == null) {
			throw new CivException("Internal error, unknown permission node.");
		}
		
		boolean on;
		if (args[3].equalsIgnoreCase("on") || args[3].equalsIgnoreCase("yes") || args[3].equalsIgnoreCase("1")) {
			on = true;
		} else if (args[3].equalsIgnoreCase("off") || args[3].equalsIgnoreCase("no") || args[3].equalsIgnoreCase("0")) {
			on = false;
		} else {
			showPermCmdHelp();
			throw new CivException("Incorrect Command Arguments.");
		}
		
		switch(args[2].toLowerCase()) {
		case "owner":
			node.setPermitOwner(on);
			break;
		case "group":
			node.setPermitGroup(on);
			break;
		case "others":
			node.setPermitOthers(on);
		}
		
		tc.save();
		
		CivMessage.sendSuccess(sender, "Permission "+node.getType()+" changed to "+on+" for "+args[2]);
	}
	
	private void showPermCmdHelp() {
		CivMessage.send(sender, CivColor.LightGray+"/plot perm set <type> <groupType> [on|off] ");
		CivMessage.send(sender, CivColor.LightGray+"    types: [build|destroy|interact|itemuse|reset]");
		CivMessage.send(sender, CivColor.LightGray+"    groupType: [owner|group|others]");
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
		if (args.length != 0) {
			validPlotOwner();
		}
			
		return;
	}

}
