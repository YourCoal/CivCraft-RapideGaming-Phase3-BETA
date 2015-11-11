package com.civcraft.command.market;

import com.civcraft.command.CommandBase;
import com.civcraft.exception.CivException;

public class MarketCommand extends CommandBase {

	@Override
	public void init() {
		command = "/market";
		displayName = "Market";	
				
		commands.put("buy", "Buy things from the market, see whats for sale.");

	}

	public void buy_cmd() {
		MarketBuyCommand cmd = new MarketBuyCommand();	
		cmd.onCommand(sender, null, "buy", this.stripArgs(args, 1));
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
