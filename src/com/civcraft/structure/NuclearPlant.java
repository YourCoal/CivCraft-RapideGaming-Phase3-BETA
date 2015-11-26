package com.civcraft.structure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.Location;

import com.civcraft.config.CivSettings;
import com.civcraft.exception.CivException;
import com.civcraft.exception.InvalidConfiguration;
import com.civcraft.object.Buff;
import com.civcraft.object.Town;
import com.civcraft.util.BlockCoord;
import com.civcraft.util.SimpleBlock;

public class NuclearPlant extends Structure {
	/* Key:
	 * O = Ore
	 * I = Ingot
	 * B = Block */
	public static final int MAX_RATE = CivSettings.getIntegerStructure("nuclearplant.max");
	private static final double COALORE = CivSettings.getDoubleStructure("nuclearplant.coalO_rate");
	private static final double COALITEM = CivSettings.getDoubleStructure("nuclearplant.coalI_rate");
	private static final double COALBLOCK = CivSettings.getDoubleStructure("nuclearplant.coalB_rate");
	private static final double IRONORE = CivSettings.getDoubleStructure("nuclearplant.ironO_rate");
	private static final double IRONITEM = CivSettings.getDoubleStructure("nuclearplant.ironI_rate");
	private static final double IRONBLOCK = CivSettings.getDoubleStructure("nuclearplant.ironB_rate");
	private static final double GOLDORE = CivSettings.getDoubleStructure("nuclearplant.goldO_rate");
	private static final double GOLDITEM = CivSettings.getDoubleStructure("nuclearplant.goldI_rate");
	private static final double GOLDBLOCK = CivSettings.getDoubleStructure("nuclearplant.goldB_rate");
	private static final double REDSTONEORE = CivSettings.getDoubleStructure("nuclearplant.redO_rate");
	private static final double REDSTONEITEM = CivSettings.getDoubleStructure("nuclearplant.redI_rate");
	private static final double REDSTONEBLOCK = CivSettings.getDoubleStructure("nuclearplant.redB_rate");
	private static final double LAPISORE = CivSettings.getDoubleStructure("nuclearplant.lapO_rate");
	private static final double LAPISITEM = CivSettings.getDoubleStructure("nuclearplant.lapI_rate");
	private static final double LAPISBLOCK = CivSettings.getDoubleStructure("nuclearplant.lapB_rate");
	private static final double DIAMONDORE = CivSettings.getDoubleStructure("nuclearplant.diaO_rate");
	private static final double DIAMONDITEM = CivSettings.getDoubleStructure("nuclearplant.diaI_rate");
	private static final double DIAMONDBLOCK = CivSettings.getDoubleStructure("nuclearplant.diaB_rate");
	private static final double EMERALDORE = CivSettings.getDoubleStructure("nuclearplant.emdO_rate");
	private static final double EMERALDITEM = CivSettings.getDoubleStructure("nuclearplant.emdI_rate");
	private static final double EMERALDBLOCK = CivSettings.getDoubleStructure("nuclearplant.emdB_rate");
	
	private int level = 1;
	public int skippedCounter = 0;
	public ReentrantLock lock = new ReentrantLock();
	
	public enum PlantTypes {
		//Coal
		CO,
		CI,
		CB,
		//Iron
		IO,
		II,
		IB,
		//Gold
		GO,
		GI,
		GB,
		//Red
		RO,
		RI,
		RB,
		//Lap
		LO,
		LI,
		LB,
		//Dia
		DO,
		DI,
		DB,
		//Emd
		EO,
		EI,
		EB
	}
	
	protected NuclearPlant(Location center, String id, Town town) throws CivException {
		super(center, id, town);	
	}
	
	public NuclearPlant(ResultSet rs) throws SQLException, CivException {
		super(rs);
	}
	
	@Override
	public String getDynmapDescription() {
		return null;
	}
	
	@Override
	public String getMarkerIconName() {
		return null;
	}
	
	public double getGravelChance(PlantTypes pt) {
		double chance = 0;
		switch (pt) {
		case CO:
			chance = COALORE;
			break;
		case CI:
			chance = COALITEM;
			break;
		case CB:
			chance = COALBLOCK;
			break;
		case IO:
			chance = IRONORE;
			break;
		case II:
			chance = IRONITEM;
			break;
		case IB:
			chance = IRONBLOCK;
			break;
		case GO:
			chance = GOLDORE;
			break;
		case GI:
			chance = GOLDITEM;
			break;
		case GB:
			chance = GOLDBLOCK;
			break;
		case RO:
			chance = REDSTONEORE;
			break;
		case RI:
			chance = REDSTONEITEM;
			break;
		case RB:
			chance = REDSTONEBLOCK;
			break;
		case LO:
			chance = LAPISORE;
			break;
		case LI:
			chance = LAPISITEM;
			break;
		case LB:
			chance = LAPISBLOCK;
			break;
		case DO:
			chance = DIAMONDORE;
			break;
		case DI:
			chance = DIAMONDITEM;
			break;
		case DB:
			chance = DIAMONDBLOCK;
			break;
		case EO:
			chance = EMERALDORE;
			break;
		case EI:
			chance = EMERALDITEM;
			break;
		case EB:
			chance = EMERALDBLOCK;
			break;
		default:
			break;
		}
		return this.modifyChance(chance);
	}
	
	private double modifyChance(Double chance) {
		double increase = chance*this.getTown().getBuffManager().getEffectiveDouble(Buff.FUSION);
		chance += increase;
		try {
			if (this.getTown().getGovernment().id.equals("gov_communism")) {
				chance *= CivSettings.getDouble(CivSettings.structureConfig, "trommel.communism_rate");
			} else if (this.getTown().getGovernment().id.equals("gov_anarchy")){
				chance *= CivSettings.getDouble(CivSettings.structureConfig, "trommel.penalty_rate");
			}
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
		}
		return chance;
	}
	
	@Override
	public void onPostBuild(BlockCoord absCoord, SimpleBlock commandBlock) {
		this.level = getTown().saved_nuclear_plant_level;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
}
