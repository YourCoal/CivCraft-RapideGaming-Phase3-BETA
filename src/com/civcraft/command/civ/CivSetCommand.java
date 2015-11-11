package com.civcraft.command.civ;

import com.civcraft.command.CommandBase;
import com.civcraft.exception.CivException;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Civilization;
import com.civcraft.util.DecimalHelper;

public class CivSetCommand extends CommandBase {

	@Override
	public void init() {
		command = "/civ set";
		displayName = "Civ Set";
		
		commands.put("taxes", "[percent] Sets the income tax for this civ.");
		commands.put("science", "[percent] Sets the amount of taxes that get converted into beakers.");
		commands.put("color", "(value) shows you the current civ's color. If value is specified, changes your color.");

	}

	
	private double vaildatePercentage(String arg) throws CivException {
		try { 
			
			arg = arg.replace("%", "");
			
			Integer amount = Integer.valueOf(arg);
			
			if (amount < 0 || amount > 100) {
				throw new CivException ("You must set a percentage between 0% and 100%");
			}
			
			return ((double)amount/100);
			
		} catch (NumberFormatException e) {
			throw new CivException(arg+" is not a number.");
		}
				
	}
	
	public void taxes_cmd() throws CivException {
		Civilization civ = getSenderCiv();
		
		if (args.length < 2) {
			CivMessage.send(sender, "Current income percentage:"+civ.getIncomeTaxRateString());
			return;
		}
		
		double newPercentage = vaildatePercentage(args[1]);
		
		if (newPercentage > civ.getGovernment().maximum_tax_rate) {
			throw new CivException("Cannot set your tax rate higher than your government's maximum("+
					DecimalHelper.formatPercentage(civ.getGovernment().maximum_tax_rate)+")");
		}
		
		civ.setIncomeTaxRate(newPercentage);
		
		civ.save();
		
		CivMessage.sendSuccess(sender, "Set income rate to "+args[1]+" percent.");
	}
	
	public void science_cmd() throws CivException {
	Civilization civ = getSenderCiv();
		
		if (args.length < 2) {
			CivMessage.send(sender, "Current science percentage:"+civ.getSciencePercentage());
			return;
		}
		
		double newPercentage = vaildatePercentage(args[1]);
		
		civ.setSciencePercentage(newPercentage);		
		civ.save();
		
		CivMessage.sendSuccess(sender, "Set science rate to "+args[1]+" percent.");	
	}
	

	public void color_cmd() throws CivException {
		Civilization civ = getSenderCiv();
		
		if (args.length < 2) {
			CivMessage.sendSuccess(sender, "Civ color is: "+Integer.toHexString(civ.getColor()));
			return;
		}
	
		try {		
			
			int color = Integer.parseInt(args[1], 16);
			if (color > Civilization.HEX_COLOR_MAX) {
				throw new CivException("Invalid color, out of range.");
			}
			if (color == 0xFF0000) {
				throw new CivException("Invalid color, this color is reserved for town borders.");
			}
			
			civ.setColor(color);
			civ.save();
			CivMessage.sendSuccess(sender, "Set civ color to "+Integer.toHexString(color));
		} catch (NumberFormatException e) {
			throw new CivException(args[1]+" is an invalid color.");
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
		this.validLeaderAdvisor();
	}

}
