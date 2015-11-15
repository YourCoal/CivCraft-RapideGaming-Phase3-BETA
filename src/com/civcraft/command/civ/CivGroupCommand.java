package com.civcraft.command.civ;

import org.bukkit.entity.Player;

import com.civcraft.command.CommandBase;
import com.civcraft.exception.CivException;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Civilization;
import com.civcraft.object.Resident;
import com.civcraft.permission.PermissionGroup;
import com.civcraft.util.CivColor;

public class CivGroupCommand extends CommandBase {

	@Override
	public void init() {
		command = "/civ group";
		displayName = "Civ Group";
		
		commands.put("add", "[name] [leaders|dipadvisers|econadvisers] - Adds a member to the leader or an adviser group.");
		commands.put("remove", "[name] [leaders|dipadvisers|econadvisers] - Removes a member to the leader or an adviser group.");
		commands.put("info", "[leaders|dipadvisers|econadvisers] - Lists members of the leader or an adviser group.");
		
	}

	public void remove_cmd() throws CivException {
		Civilization civ = getSenderCiv();
		Resident resident = getResident();
		Resident oldMember = getNamedResident(1);
		String groupName = getNamedString(2, "Enter a group name");
	
		PermissionGroup grp = null;
		if (groupName.equalsIgnoreCase("leaders")) {
			grp = civ.getLeaderGroup();
		} else if (groupName.equalsIgnoreCase("dipadvisers")) {
			grp = civ.getDipAdviserGroup();
		} else if (groupName.equalsIgnoreCase("econadvisers")) {
			grp = civ.getEconAdviserGroup();
		} else {
			throw new CivException("Could not find group "+groupName);
		}
		
		if (grp == civ.getLeaderGroup() && !grp.hasMember(resident)) {
			throw new CivException("Only Leaders can remove members to the Leaders group.");
		}
		
		if (!grp.hasMember(oldMember)) {
			throw new CivException(oldMember.getName()+" is not a member of this group.");
		}
		
		if (grp == civ.getLeaderGroup() && resident == oldMember) {
			throw new CivException("You cannot removed yourself from the leaders group.");
		}
		
		grp.removeMember(oldMember);	
		grp.save();
		CivMessage.sendSuccess(sender, "Removed "+oldMember.getName()+" from group "+groupName);	
		try {
			Player newPlayer = CivGlobal.getPlayer(oldMember);
			CivMessage.send(newPlayer, CivColor.Rose+"You were removed from the "+groupName+" group in civ "+civ.getName());
		} catch (CivException e) {
			/* player not online. forget the exception*/
		}
	}
	
	public void add_cmd() throws CivException {
		Civilization civ = getSenderCiv();
		Resident resident = getResident();
		Resident newMember = getNamedResident(1);
		String groupName = getNamedString(2, "Enter a group name");
		
		PermissionGroup grp = null;
		if (groupName.equalsIgnoreCase("leaders")) {
			grp = civ.getLeaderGroup();
		} else if (groupName.equalsIgnoreCase("dipadvisers")) {
			grp = civ.getDipAdviserGroup();
		} else if (groupName.equalsIgnoreCase("econadvisers")) {
			grp = civ.getEconAdviserGroup();
		} else {
			throw new CivException("Could not find group "+groupName);
		}
		
		if (grp == civ.getLeaderGroup() && !grp.hasMember(resident)) {
			throw new CivException("Only Leaders can add members to the leaders group.");
		}
		
		if (newMember.getCiv() != civ) {
			throw new CivException("Cannot add non-civ members to leaders or adviser groups.");
		}
		
		grp.addMember(newMember);
		grp.save();
		
		CivMessage.sendSuccess(sender, "Added "+newMember.getName()+" to group "+groupName);

		try {
			Player newPlayer = CivGlobal.getPlayer(newMember);
			CivMessage.sendSuccess(newPlayer, "You were added to the "+groupName+" group in civ "+civ.getName());
		} catch (CivException e) {
			/* player not online. forget the exception*/
		}
	}
	
	
	public void info_cmd() throws CivException {
		Civilization civ = getSenderCiv();
		
		if (args.length > 1) {
			PermissionGroup grp = null;
			if (args[1].equalsIgnoreCase("leaders")) {
				grp = civ.getLeaderGroup();
			} else if (args[1].equalsIgnoreCase("dipadvisers")) {
				grp = civ.getDipAdviserGroup();
			} else if (args[1].equalsIgnoreCase("econadvisers")) {
				grp = civ.getEconAdviserGroup();
			} else {
				throw new CivException("Could not find group "+args[1]);
			}
			
			CivMessage.sendHeading(sender, "Group:"+args[1]);
			
			String residents = "";
			for (Resident res : grp.getMemberList()) {
				residents += res.getName() + " ";
			}
			CivMessage.send(sender, residents);
			
		} else {
			CivMessage.sendHeading(sender, "Civ Group Information");
			PermissionGroup grp = civ.getLeaderGroup();
			CivMessage.send(sender, grp.getName()+CivColor.LightGray+" ("+grp.getMemberCount()+" members)");	
			grp = civ.getDipAdviserGroup();
			CivMessage.send(sender, grp.getName()+CivColor.LightGray+" ("+grp.getMemberCount()+" members)");
			grp = civ.getEconAdviserGroup();
			CivMessage.send(sender, grp.getName()+CivColor.LightGray+" ("+grp.getMemberCount()+" members)");
		}
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
		this.validLeaderDipEconAdvisor();
	}
}
