package com.civcraft.command.camp;

import com.civcraft.camp.Camp;
import com.civcraft.command.CommandBase;
import com.civcraft.config.CivSettings;
import com.civcraft.config.ConfigCampUpgrade;
import com.civcraft.exception.CivException;
import com.civcraft.main.CivMessage;
import com.civcraft.util.CivColor;

public class CampUpgradeCommand extends CommandBase {
	@Override
	public void init() {
		command = "/camp upgrade";
		displayName = "Camp Upgrade";
		
		
		commands.put("list", "shows available upgrades to purchase.");
		commands.put("purchased", "shows a list of purchased upgrades.");
		commands.put("buy", "[name] - buys upgrade for this camp.");
		
	}

	public void purchased_cmd() throws CivException {
		Camp camp = this.getCurrentCamp();
		CivMessage.sendHeading(sender, "Upgrades Purchased");

		String out = "";
		for (ConfigCampUpgrade upgrade : camp.getUpgrades()) {
			out += upgrade.name+", ";
		}
		
		CivMessage.send(sender, out);
	}
	
	private void list_upgrades(Camp camp) throws CivException {				
		for (ConfigCampUpgrade upgrade : CivSettings.campUpgrades.values()) {
			if (upgrade.isAvailable(camp)) {
				CivMessage.send(sender, upgrade.name+CivColor.LightGray+" Cost: "+CivColor.Yellow+upgrade.cost);
			}
		}
	}
	
	public void list_cmd() throws CivException {
		Camp camp = this.getCurrentCamp();

		CivMessage.sendHeading(sender, "Available Upgrades");	
		list_upgrades(camp);		
	}
	
	public void buy_cmd() throws CivException {
		Camp camp = this.getCurrentCamp();

		if (args.length < 2) {
			CivMessage.sendHeading(sender, "Available Upgrades");
			list_upgrades(camp);		
			CivMessage.send(sender, "Enter the name of the upgrade you wish to purchase.");
			return;
		}
				
		String combinedArgs = "";
		args = this.stripArgs(args, 1);
		for (String arg : args) {
			combinedArgs += arg + " ";
		}
		combinedArgs = combinedArgs.trim();
		
		ConfigCampUpgrade upgrade = CivSettings.getCampUpgradeByNameRegex(camp, combinedArgs);
		if (upgrade == null) {
			throw new CivException("No upgrade by the name of "+combinedArgs+" could be found.");
		}
		
		if (camp.hasUpgrade(upgrade.id)) {
			throw new CivException("You already have that upgrade.");
		}
		
		camp.purchaseUpgrade(upgrade);
		CivMessage.sendSuccess(sender, "Upgrade \""+upgrade.name+"\" purchased.");
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
		this.validCampOwner();
	}
}
