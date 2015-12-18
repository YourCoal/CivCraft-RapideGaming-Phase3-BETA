package com.civcraft.structure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import com.civcraft.config.CivSettings;
import com.civcraft.exception.CivException;
import com.civcraft.exception.InvalidConfiguration;
import com.civcraft.main.CivLog;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Buff;
import com.civcraft.object.StructureSign;
import com.civcraft.object.Town;
import com.civcraft.util.BlockCoord;
import com.civcraft.util.CivColor;
import com.civcraft.util.SimpleBlock;

public class Fishery extends Structure {
	
	public static final int MAX_CHANCE = CivSettings.getIntegerStructure("fishery.tierMax");
	public static final double FISH_T0_RATE = CivSettings.getDoubleStructure("fishery.t0_rate"); //100%
	public static final double FISH_T1_RATE = CivSettings.getDoubleStructure("fishery.t1_rate"); //100%
	public static final double FISH_T2_RATE = CivSettings.getDoubleStructure("fishery.t2_rate"); //100%
	public static final double FISH_T3_RATE = CivSettings.getDoubleStructure("fishery.t3_rate"); //100%
	public static final double FISH_T4_RATE = CivSettings.getDoubleStructure("fishery.t4_rate"); //100%
	
	private int level = 1;
	public int skippedCounter = 0;
	public ReentrantLock lock = new ReentrantLock();
	
	protected Fishery(Location center, String id, Town town) throws CivException {
		super(center, id, town);	
	}
	
	public Fishery(ResultSet rs) throws SQLException, CivException {
		super(rs);
	}
	
	@Override
	public String getDynmapDescription() {
		String out = "<u><b>"+this.getDisplayName()+"</u></b><br/>";
		out += "Level: "+this.level;
		return out;
	}
	
	@Override
	public String getMarkerIconName() {
		return "cutlery";
	}
	
	public double getChance(double chance) {
		return this.modifyChance(chance);
	}
	
	private double modifyChance(Double chance) {
		double increase = chance*this.getTown().getBuffManager().getEffectiveDouble(Buff.FEEDING);
		chance += increase;
		try {
			if (this.getTown().getGovernment().id.equals("gov_tribalism") ||
					this.getTown().getGovernment().id.equals("gov_socialism")) {
				chance *= CivSettings.getDouble(CivSettings.structureConfig, "fishery.bonus_rate");
			} else if (this.getTown().getGovernment().id.equals("gov_bankocracy")){
				chance *= CivSettings.getDouble(CivSettings.structureConfig, "fishery.penalty_rate");
			}
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
		}
		return chance;
	}
	
	@Override
	public void onPostBuild(BlockCoord absCoord, SimpleBlock commandBlock) {
		this.level = getTown().saved_fishery_level;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
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
	
	@Override
	public void updateSignText() {
		int count = 0;
		for (count = 0; count < level; count++) {
			StructureSign sign = getSignFromSpecialId(count);
			if (sign == null) {
				CivLog.error("sign from special id was null, id:"+count);
				return;
			}
			sign.setText("Fishery\nPool\n"+(count+1));
			sign.update();
		}
		
		for (; count < getSigns().size(); count++) {
			StructureSign sign = getSignFromSpecialId(count);
			if (sign == null) {
				CivLog.error("sign from special id was null, id:"+count);
				return;
			}
			sign.setText("Pool Offline");
			sign.update();
		}
	}
	
	@Override
	public void processSignAction(Player player, StructureSign sign, PlayerInteractEvent event) {
		int special_id = Integer.valueOf(sign.getAction());
		if (special_id < this.level) {
			CivMessage.send(player, CivColor.LightGreen+"Fishery Pool "+(special_id+1)+" is active.");
		} else {
			CivMessage.send(player, CivColor.Rose+"Fishery Pool "+(special_id+1)+" is offline. Upgrade to activate.");
		}
	}
}