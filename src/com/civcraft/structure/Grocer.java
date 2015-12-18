package com.civcraft.structure;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import com.civcraft.components.NonMemberFeeComponent;
import com.civcraft.config.CivSettings;
import com.civcraft.config.ConfigGrocerLevel;
import com.civcraft.exception.CivException;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivLog;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;
import com.civcraft.object.StructureSign;
import com.civcraft.object.Town;
import com.civcraft.util.CivColor;

public class Grocer extends Structure {
	
	private int level = 1;
	private NonMemberFeeComponent nonMemberFeeComponent; 
	
	protected Grocer(Location center, String id, Town town) throws CivException {
		super(center, id, town);
		nonMemberFeeComponent = new NonMemberFeeComponent(this);
		nonMemberFeeComponent.onSave();
	}
	
	public Grocer(ResultSet rs) throws SQLException, CivException {
		super(rs);
		nonMemberFeeComponent = new NonMemberFeeComponent(this);
		nonMemberFeeComponent.onLoad();
	}
	
	@Override
	public String getDynmapDescription() {
		String out = "<u><b>Grocer</u></b><br/>";
		for (int i = 0; i < level; i++) {
			ConfigGrocerLevel grocerlevel = CivSettings.grocerLevels.get(i+1);
			out += "<b>"+grocerlevel.itemName+"</b> Amount: "+grocerlevel.amount+ " Price: "+grocerlevel.price+" coins.<br/>";
		}
		return out;
	}
	
	@Override
	public String getMarkerIconName() {
		return "cutlery";
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public double getNonResidentFee() {
		return nonMemberFeeComponent.getFeeRate();
	}
	
	public void setNonResidentFee(double nonResidentFee) {
		this.nonMemberFeeComponent.setFeeRate(nonResidentFee);
	}
	
	private String getNonResidentFeeString() {
		return "Fee: "+((int)(getNonResidentFee()*100) + "%").toString();		
	}
	
	private StructureSign getSignFromSpecialId(int special_id) {
		for (StructureSign sign : getSigns()) {
			int id = Integer.valueOf(sign.getAction());
			if (id == special_id) {
				return sign;
			}
		}
		return null;
	}
	
	public void sign_buy_material(Player player, String itemName, int id, byte data, int amount, double price) {
		Resident resident;
		int payToTown = (int) Math.round(price*this.getNonResidentFee());
		try {
			resident = CivGlobal.getResident(player.getName());
			Town t = resident.getTown();
			if (t == this.getTown()) {
				// Pay no taxes! You're a member.
				resident.buyItem(itemName, id, data, price, amount);
				CivMessage.send(player, CivColor.LightGreen + "Bought "+amount+" "+itemName+" for "+ price+ " coins.");
				return;
			} else {
				// Pay non-resident taxes
				resident.buyItem(itemName, id, data, price + payToTown, amount);
				getTown().depositDirect(payToTown);
				CivMessage.send(player, CivColor.LightGreen + "Bought "+amount+" "+itemName+" for "+ price+ " coins.");
				CivMessage.send(player, CivColor.Yellow + "Paid "+ payToTown+" coins in non-resident taxes.");
			}
		} catch (CivException e) {
			CivMessage.send(player, CivColor.Rose + e.getMessage());
		}
		return;
	}
	
	@Override
	public void updateSignText() {
		int count = 0;
		for (count = 0; count < level; count++) {
			StructureSign sign = getSignFromSpecialId(count);
			if (sign == null) {
				CivLog.error("sign from special id was null, id:"+count);
				return;
			}
			ConfigGrocerLevel grocerlevel = CivSettings.grocerLevels.get(count+1);
			sign.setText("Buy\n"+grocerlevel.itemName+"\n"+
			"For "+grocerlevel.price+" Coins\n"+getNonResidentFeeString());
			sign.update();
		} for (; count < getSigns().size(); count++) {
			StructureSign sign = getSignFromSpecialId(count);
			if (sign == null) {
				CivLog.error("sign from special id was null, id:"+count);
				return;
			}
			sign.setText("Grocer Shelf\nEmpty");
			sign.update();
		}
	}
	
	@Override
	public void processSignAction(Player player, StructureSign sign, PlayerInteractEvent event) {
		int special_id = Integer.valueOf(sign.getAction());
		if (special_id < this.level) {
			ConfigGrocerLevel grocerlevel = CivSettings.grocerLevels.get(special_id+1);
			sign_buy_material(player, grocerlevel.itemName, grocerlevel.itemId, 
					(byte)grocerlevel.itemData, grocerlevel.amount, grocerlevel.price);
		} else {
			CivMessage.send(player, CivColor.Rose+"Grocer shelf empty, stock it using /town upgrade.");
		}
	}
}