package com.civcraft.command.market;

import com.civcraft.command.CommandBase;
import com.civcraft.exception.CivException;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Civilization;
import com.civcraft.object.Relation;
import com.civcraft.object.Town;
import com.civcraft.object.Relation.Status;
import com.civcraft.util.CivColor;
import com.civcraft.war.War;

public class MarketBuyCommand extends CommandBase {

	@Override
	public void init() {
		command = "/market buy";
		displayName = "Market Buy";
		
		commands.put("towns", "See what towns are for sale and buy them.");
		commands.put("civs", "See what civs are for sale and buy them.");
		
	}
	
	private void list_towns_for_sale(Civilization ourCiv) {
		
		CivMessage.sendHeading(sender, "Towns For Sale");
		for (Town town : CivGlobal.getTowns()) {
			if (!town.isCapitol()) {
				if (town.isForSale()) {
					CivMessage.send(sender, town.getName()+" - "+CivColor.Yellow+
							df.format(town.getForSalePrice())+" coins.");
				}
			}
		}
		
	}
	
	private void list_civs_for_sale(Civilization ourCiv) {
		
		CivMessage.sendHeading(sender, "Civs For Sale");
		for (Civilization civ : CivGlobal.getCivs()) {
				if (civ.isForSale()) {
					CivMessage.send(sender, civ.getName()+" - "+CivColor.Yellow+
							df.format(civ.getTotalSalePrice())+" coins.");
				}
		}
	}
	
	public void towns_cmd(Relation re) throws CivException {
		this.validLeaderDipAdvisor();
		Civilization senderCiv = this.getSenderCiv();
		
		if (args.length < 2) {
			list_towns_for_sale(senderCiv);
			CivMessage.send(sender, "Use /market buy towns [town-name] to buy this town.");
			return;
		}
		
		Town town = getNamedTown(1);
		
		if (senderCiv.isForSale()) {
			throw new CivException("Cannot buy a town when your civ is up for sale.");
		}
		
		if (town.getCiv() == senderCiv) {
			throw new CivException("Cannot buy a town already in your civ.");
		}
		
		if (town.isCapitol()) {
			throw new CivException("Cannot buy the capitol, you must buy the civilization instead.");
		}
		
		if (!town.isForSale()) {
			throw new CivException("Can only buy towns that are up for sale.");
		}
		
		if (War.isWarTime()) {
			throw new CivException("Can not buy towns during WarTime.");
		}
		
		if (War.isWithinWarDeclareDays() && re.getStatus() == Status.WAR) {
			throw new CivException("Can not buy towns within 3 days of WarTime when at war.");
		}
		
		senderCiv.buyTown(town);
		CivMessage.global("Town of "+town.getName()+" has been bought and is now part of "+senderCiv.getName());
		CivMessage.sendSuccess(sender, "Bought town "+args[1]);
	}
	
	
	public void civs_cmd(Relation re) throws CivException {
		this.validLeaderDipAdvisor();
		Civilization senderCiv = this.getSenderCiv();
		
		if (args.length < 2) {
			list_civs_for_sale(senderCiv);
			CivMessage.send(sender, "Use /market buy civs [civ-name] to buy this civ.");
			return;
		}
		
		Civilization civBought = getNamedCiv(1);
		
		if (senderCiv.isForSale()) {
			throw new CivException("Cannot buy a civ when your civ is up for sale.");
		}
		
		if (civBought == senderCiv) {
			throw new CivException("Cannot buy your own civ.");
		}
		
		if (!civBought.isForSale()) {
			throw new CivException("Can only buy civilizations that are up for sale.");
		}
		
		if (War.isWarTime()) {
			throw new CivException("Can not buy towns during WarTime.");
		}
		
		if (War.isWithinWarDeclareDays() && re.getStatus() == Status.WAR) {
			throw new CivException("Can not buy towns within 3 days of WarTime when at war.");
		}
		
		senderCiv.buyCiv(civBought);
		CivMessage.global(civBought.getName()+" has been bought by "+senderCiv.getName());
		CivMessage.sendSuccess(sender, "Bought civ "+args[1]);
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
