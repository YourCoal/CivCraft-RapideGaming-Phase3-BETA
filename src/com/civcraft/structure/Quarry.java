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

public class Quarry extends Structure {
	public static final int MAX = CivSettings.getIntegerStructure("quarry.max");
	private static final double COBBLESTONE_RATE = CivSettings.getDoubleStructure("quarry.cobblestone_rate");
	private static final double COAL_RATE = CivSettings.getDoubleStructure("quarry.coal_rate");
	private static final double IRON_RATE = CivSettings.getDoubleStructure("quarry.iron_rate");
	private static final double GOLD_RATE = CivSettings.getDoubleStructure("quarry.gold_rate");
	private static final double REDSTONE_RATE = CivSettings.getDoubleStructure("quarry.redstone_rate");
	private static final double DIAMOND_RATE = CivSettings.getDoubleStructure("quarry.diamond_rate");
	
	private int level = 1;
	public int skippedCounter = 0;
	public ReentrantLock lock = new ReentrantLock();
	
	public enum Ores {
		DIAMOND,
		REDSTONE,
		GOLD,
		IRON,
		COAL,
		COBBLESTONE
	}
	
	protected Quarry(Location center, String id, Town town) throws CivException {
		super(center, id, town);	
	}
	
	public Quarry(ResultSet rs) throws SQLException, CivException {
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
		return "construction";
	}

	public double getChance(Ores ore) {
		double chance = 0;
		switch (ore) {
		case DIAMOND:
			chance = DIAMOND_RATE;
			break;
		case REDSTONE:
			chance = REDSTONE_RATE;
			break;
		case GOLD:
			chance = GOLD_RATE;
			break;
		case IRON:
			chance = IRON_RATE;
			break;
		case COAL:
			chance = COAL_RATE;
			break;
		case COBBLESTONE:
			chance = COBBLESTONE_RATE;
			break;
		}
		return this.modifyChance(chance);
	}
	
	private double modifyChance(Double chance) {
		double increase = chance*this.getTown().getBuffManager().getEffectiveDouble(Buff.EXTRACTION);
		chance += increase;
		try {
			if (this.getTown().getGovernment().id.equals("gov_technocracy")) {
				chance *= CivSettings.getDouble(CivSettings.structureConfig, "quarry.technocracy_rate");
			} else if (this.getTown().getGovernment().id.equals("gov_theocracy") || this.getTown().getGovernment().id.equals("gov_monarchy")){
				chance *= CivSettings.getDouble(CivSettings.structureConfig, "quarry.penalty_rate");
			}
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
		}
		return chance;
	}
	
	@Override
	public void onPostBuild(BlockCoord absCoord, SimpleBlock commandBlock) {
		this.level = getTown().saved_quarry_level;
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
			sign.setText(("Tunnel Input")+(count+1));
			sign.update();
		}
		
		for (; count < getSigns().size(); count++) {
			StructureSign sign = getSignFromSpecialId(count);
			if (sign == null) {
				CivLog.error("sign from special id was null, id:"+count);
				return;
			}
			sign.setText("Tunnel Inactive");
			sign.update();
		}
		
	}
	
	@Override
	public void processSignAction(Player player, StructureSign sign, PlayerInteractEvent event) {
		int special_id = Integer.valueOf(sign.getAction());
		if (special_id < this.level) {
			CivMessage.send(player, CivColor.LightGreen+"Tunnel [%0] Active");

		} else {
			CivMessage.send(player, CivColor.Rose+"Tunnel [%0] Offline. Upgrade Quarry To Activate.");
		}
	}
}
