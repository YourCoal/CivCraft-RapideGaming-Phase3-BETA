package com.civcraft.threading.timers;

import java.text.DecimalFormat;

import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Town;
import com.civcraft.object.TradeGood;
import com.civcraft.util.CivColor;

public class SyncTradeTimer implements Runnable {
	
	public SyncTradeTimer() {
	}
	
	public void processTownsTradePayments(Town town) {
		//goodies = town.getEffectiveBonusGoodies();
		//double payment = TradeGood.getTownTradePayment(town, goodies);
		double payment = TradeGood.getTownTradePayment(town);
		DecimalFormat df = new DecimalFormat();
		
		if (payment > 0.0) {
			double taxesPaid = payment*town.getDepositCiv().getIncomeTaxRate();
			if (taxesPaid > 0) {
				CivMessage.sendTown(town, CivColor.LightGreen+"Generated "+CivColor.Yellow+df.format(payment)+CivColor.LightGreen+" coins from trade."+
					CivColor.Yellow+" (Paid "+df.format(taxesPaid)+" in taxes to "+town.getDepositCiv().getName()+")");
			} else {
				CivMessage.sendTown(town, CivColor.LightGreen+"Generated "+CivColor.Yellow+df.format(payment)+CivColor.LightGreen+" coins from trade.");
			}
			town.getTreasury().deposit(payment - taxesPaid);
			town.getDepositCiv().taxPayment(town, taxesPaid);
		}
	}
	
	@Override
	public void run() {
		if (!CivGlobal.tradeEnabled) {
			return;
		}
		
		CivGlobal.checkForDuplicateGoodies();
		for (Town town : CivGlobal.getTowns()) {
			try {
				processTownsTradePayments(town);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
