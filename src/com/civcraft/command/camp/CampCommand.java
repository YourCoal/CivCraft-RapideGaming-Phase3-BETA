package com.civcraft.command.camp;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.civcraft.camp.Camp;
import com.civcraft.command.CommandBase;
import com.civcraft.exception.CivException;
import com.civcraft.lorestorage.LoreCraftableMaterial;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;
import com.civcraft.questions.JoinCampResponse;
import com.civcraft.util.CivColor;

public class CampCommand extends CommandBase {
	public static final long INVITE_TIMEOUT = 30000; //30 seconds

	@Override
	public void init() {
		command = "/camp";
		displayName = "Camp";
		commands.put("undo", "Unbuilds the camp, issues a refund.");
		commands.put("add", "[name] - adds this player to our camp.");
		commands.put("remove", "[name] - removes this player from our camp.");
		commands.put("leave", "Leaves the current camp you're in.");
		commands.put("setowner", "[name] - Sets the camp's owner to the player name you give. They must be a current member.");
		commands.put("info", "Shows information about your current camp.");
		commands.put("disband", "Disbands this camp.");
		commands.put("upgrade", "Manage camp upgrades.");
		commands.put("refresh", "Refresh attatchable blocks (like ladders, doors, etc) in the camp.");
		commands.put("location", "Shows the location of your camp.");
	}
	
	public void location_cmd() throws CivException {
		Resident resident = getResident();
		if (!resident.hasCamp()) {
			throw new CivException("You are not currently in a camp.");
		}
		
		Camp camp = resident.getCamp();
		if (camp != null) {
			CivMessage.send(sender, "");
			CivMessage.send(sender, "");
			CivMessage.send(sender, CivColor.LightGreen+CivColor.BOLD+"Your Camp's Location: "+CivColor.LightPurple+camp.getCorner());
			CivMessage.send(sender, "");
			CivMessage.send(sender, "");
		}
	}
	
	public void refresh_cmd() throws CivException {
		Resident resident = getResident();
		if (!resident.hasCamp()) {
			throw new CivException("You are not currently in a camp.");
		}
		
		Camp camp = resident.getCamp();
		if (camp.getOwner() != resident) {
			throw new CivException("Only the owner of the camp can refresh it.");
		}
		
		if (camp.isDestroyed()) {
			throw new CivException("Your camp is destroyed and cannot be refreshed.");
		}
		try {
			camp.repairFromTemplate();
		} catch (IOException e) {
		} catch (CivException e) {
			e.printStackTrace();
		}
		camp.reprocessCommandSigns();
		CivMessage.send(sender, "Repaired the camp. Check your Chests/firepit/garden, items were ejected by the refresh.");
	}
	
	public void upgrade_cmd() {
		CampUpgradeCommand cmd = new CampUpgradeCommand();	
		cmd.onCommand(sender, null, "camp", this.stripArgs(args, 1));
	}
	
	public void info_cmd() throws CivException {
		Camp camp = this.getCurrentCamp();
		SimpleDateFormat sdf = new SimpleDateFormat("M/dd h:mm:ss a z");

		CivMessage.sendHeading(sender, "Camp "+camp.getName()+" Info");
		HashMap<String,String> info = new HashMap<String, String>();
		info.put("Owner", camp.getOwnerName());
		info.put("Members", ""+camp.getMembers().size());
		info.put("Next Raid", ""+sdf.format(camp.getNextRaidDate()));
		CivMessage.send(sender, this.makeInfoString(info, CivColor.Green, CivColor.LightGreen));
		
		info.clear();
		info.put("Hours of Fire Left", ""+camp.getFirepoints());
		info.put("Longhouse Level", ""+camp.getLonghouseLevel()+""+camp.getLonghouseCountString());
		CivMessage.send(sender, this.makeInfoString(info, CivColor.Green, CivColor.LightGreen));

		info.clear();
		info.put("Members", camp.getMembersString());
		CivMessage.send(sender, this.makeInfoString(info, CivColor.Green, CivColor.LightGreen));
	}
	
	public void remove_cmd() throws CivException {
		this.validCampOwner();
		Camp camp = getCurrentCamp();
		Resident resident = getNamedResident(1);
		
		if (!resident.hasCamp() || resident.getCamp() != camp) {
			throw new CivException(resident.getName()+" does not belong to this camp.");
		}
		
		if (resident.getCamp().getOwner() == resident) {
			throw new CivException("Cannot remove the owner of the camp from his own camp!");
		}
		
		camp.removeMember(resident);
		CivMessage.sendSuccess(sender, "Removed "+resident.getName()+" from this camp.");
	}
	
	public void add_cmd() throws CivException {
		this.validCampOwner();
		Camp camp = this.getCurrentCamp();
		Resident resident = getNamedResident(1);
		Player player = getPlayer();
		
		if (resident.hasCamp()) {
			throw new CivException("This resident already belongs to a camp.");
		}
		
		if (resident.hasTown()) {
			throw new CivException("This resident belongs to a town and cannot join a camp.");
		}
		
		JoinCampResponse join = new JoinCampResponse();
		join.camp = camp;
		join.resident = resident;
		join.sender = player;
		
		CivGlobal.questionPlayer(player, CivGlobal.getPlayer(resident), 
				"Would you like to join the camp owned by "+player.getName()+"?",
				INVITE_TIMEOUT, join);
		
		CivMessage.sendSuccess(player, "Invited "+resident.getName()+" to our camp.");
	}
	
	public void setowner_cmd() throws CivException {
		this.validCampOwner();
		Camp camp = getCurrentCamp();
		Resident newLeader = getNamedResident(1);
		
		if (!camp.hasMember(newLeader.getName())) {
			throw new CivException(newLeader.getName()+" is not a member of the camp and cannot be set as the owner.");
		}
		
		camp.setOwner(newLeader);
		camp.save();
		
		Player player = CivGlobal.getPlayer(newLeader);
		CivMessage.sendSuccess(player, "You are now the proud owner of the camp you're in.");
		CivMessage.sendSuccess(sender, "Transfered camp ownership to "+newLeader.getName());
		
	}
	
	public void leave_cmd() throws CivException {
		Resident resident = getResident();
		
		if (!resident.hasCamp()) {
			throw new CivException("You are not currently in a camp.");
		}
		
		Camp camp = resident.getCamp();
		if (camp.getOwner() == resident) {
			throw new CivException("The owner of the camp cannot leave it. Try /camp setowner to give it to someone else or use /camp disband to abondon the camp.");
		}
		
		camp.removeMember(resident);
		camp.save();
		CivMessage.sendSuccess(sender, "You've left camp "+camp.getName());
	}
	
	public void new_cmd() throws CivException {

	}
	
	public void disband_cmd() throws CivException {
		Resident resident = getResident();
		this.validCampOwner();
		Camp camp = this.getCurrentCamp();
		
		if (!resident.hasCamp()) {
			throw new CivException("You are not part of a camp.");
		}

		camp.disband();
		CivMessage.sendSuccess(sender, "Camp disbanded.");
	}
	
	public void undo_cmd() throws CivException {
		Resident resident = getResident();
		
		if (!resident.hasCamp()) {
			throw new CivException("You are not part of a camp.");
		}
		
		Camp camp = resident.getCamp();
		if (camp.getOwner() != resident) {
			throw new CivException("Only the camp owner "+camp.getOwner().getName()+" can disband this camp.");
		}
		
		if (!camp.isUndoable()) {
			throw new CivException("This camp can no longer be unbuilt. Use /camp disband instead.");
		}
		
		LoreCraftableMaterial campMat = LoreCraftableMaterial.getCraftMaterialFromId("mat_found_camp");
		if (campMat == null) {
			throw new CivException("Cannot undo camp. Internal error. Contact an admin.");
		}
		
		ItemStack newStack = LoreCraftableMaterial.spawn(campMat);
		Player player = CivGlobal.getPlayer(resident);
		HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(newStack);
		for (ItemStack stack : leftovers.values()) {
			player.getWorld().dropItem(player.getLocation(), stack);
			CivMessage.send(player, CivColor.LightGray+"Your camp item was dropped on the ground because your inventory was full.");
		}
		
		camp.undo();
		CivMessage.sendSuccess(sender, "Unbuilt camp. You were refunded your Camp.");
		
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
	}

}
