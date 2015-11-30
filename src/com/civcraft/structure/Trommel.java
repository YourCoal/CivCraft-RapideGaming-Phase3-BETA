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

public class Trommel extends Structure {
	public static final int BRONZE = CivSettings.getIntegerStructure("trommel_global.bronze");
	public static final int STEEL = CivSettings.getIntegerStructure("trommel_global.steel");
	public static final int TITANIUM = CivSettings.getIntegerStructure("trommel_global.titanium");
	
	public static final int GRAVEL_MAX_RATE = CivSettings.getIntegerStructure("trommel_gravel.max");
	private static final double GRAVEL_IRON = CivSettings.getDoubleStructure("trommel_gravel.iron");
	private static final double GRAVEL_GOLD = CivSettings.getDoubleStructure("trommel_gravel.gold");
	private static final double GRAVEL_DIAMOND = CivSettings.getDoubleStructure("trommel_gravel.diamond");
	private static final double GRAVEL_EMERALD = CivSettings.getDoubleStructure("trommel_gravel.emerald");
	private static final double GRAVEL_REDSTONE = CivSettings.getDoubleStructure("trommel_gravel.redstone");
	
	public static final int DIRT_MAX_RATE = CivSettings.getIntegerStructure("trommel_dirt.max");
	private static final double DIRT_IRON = CivSettings.getDoubleStructure("trommel_dirt.iron");
	private static final double DIRT_GOLD = CivSettings.getDoubleStructure("trommel_dirt.gold");
	private static final double DIRT_DIAMOND = CivSettings.getDoubleStructure("trommel_dirt.diamond");
	private static final double DIRT_EMERALD = CivSettings.getDoubleStructure("trommel_dirt.emerald");
	private static final double DIRT_REDSTONE = CivSettings.getDoubleStructure("trommel_dirt.redstone");
	
	public static final int GRANITE_MAX_RATE = CivSettings.getIntegerStructure("trommel_granite.max");
	private static final double GRANITE_IRON = CivSettings.getDoubleStructure("trommel_granite.iron");
	private static final double GRANITE_GOLD = CivSettings.getDoubleStructure("trommel_granite.gold");
	private static final double GRANITE_DIAMOND = CivSettings.getDoubleStructure("trommel_granite.diamond");
	private static final double GRANITE_EMERALD = CivSettings.getDoubleStructure("trommel_granite.emerald");
	private static final double GRANITE_REDSTONE = CivSettings.getDoubleStructure("trommel_granite.redstone");
	
	public static final int DIORITE_MAX_RATE = CivSettings.getIntegerStructure("trommel_diorite.max");
	private static final double DIORITE_IRON = CivSettings.getDoubleStructure("trommel_diorite.iron");
	private static final double DIORITE_GOLD = CivSettings.getDoubleStructure("trommel_diorite.gold");
	private static final double DIORITE_DIAMOND = CivSettings.getDoubleStructure("trommel_diorite.diamond");
	private static final double DIORITE_EMERALD = CivSettings.getDoubleStructure("trommel_diorite.emerald");
	private static final double DIORITE_REDSTONE = CivSettings.getDoubleStructure("trommel_diorite.redstone");
	
	public static final int ANDESITE_MAX_RATE = CivSettings.getIntegerStructure("trommel_andesite.max");
	private static final double ANDESITE_IRON = CivSettings.getDoubleStructure("trommel_andesite.iron");
	private static final double ANDESITE_GOLD = CivSettings.getDoubleStructure("trommel_andesite.gold");
	private static final double ANDESITE_DIAMOND = CivSettings.getDoubleStructure("trommel_andesite.diamond");
	private static final double ANDESITE_EMERALD = CivSettings.getDoubleStructure("trommel_andesite.emerald");
	private static final double ANDESITE_REDSTONE = CivSettings.getDoubleStructure("trommel_andesite.redstone");
	
	private int level = 1;
	public int skippedCounter = 0;
	public ReentrantLock lock = new ReentrantLock();
	
	public enum Mineral {
		TITANIUM_ORE,
		BRONZE_ORE,
		STEEL_ORE,
		REDSTONE,
		EMERALD,
		DIAMOND,
		GOLD,
		IRON
	}
	
	protected Trommel(Location center, String id, Town town) throws CivException {
		super(center, id, town);	
	}
	
	public Trommel(ResultSet rs) throws SQLException, CivException {
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
		return "minecart";
	}
	
	public double getGravelChance(Mineral mineral) {
		double chance = 0;
		switch (mineral) {
		case EMERALD:
			chance = GRAVEL_EMERALD;
			break;
		case DIAMOND:
			chance = GRAVEL_DIAMOND;
			break;
		case GOLD:
			chance = GRAVEL_GOLD;
			break;
		case IRON:
			chance = GRAVEL_IRON;
			break;
		case REDSTONE:
			chance = GRAVEL_REDSTONE;
		case BRONZE_ORE:
			chance = BRONZE;
			break;
		case STEEL_ORE:
			chance = STEEL;
			break;
		case TITANIUM_ORE:
			chance = TITANIUM;
			break;
		default:
			break;
		}
		return this.modifyChance(chance);
	}
	
	public double getDirtChance(Mineral mineral) {
		double chance = 0;
		switch (mineral) {
		case EMERALD:
			chance = DIRT_EMERALD;
			break;
		case DIAMOND:
			chance = DIRT_DIAMOND;
			break;
		case GOLD:
			chance = DIRT_GOLD;
			break;
		case IRON:
			chance = DIRT_IRON;
			break;
		case REDSTONE:
			chance = DIRT_REDSTONE;
			break;
		case BRONZE_ORE:
			chance = BRONZE;
			break;
		case STEEL_ORE:
			chance = STEEL;
			break;
		case TITANIUM_ORE:
			chance = TITANIUM;
			break;
		}
		return this.modifyChance(chance);
	}
	
	public double getGraniteChance(Mineral mineral) {
		double chance = 0;
		switch (mineral) {
		case EMERALD:
			chance = GRANITE_EMERALD;
			break;
		case DIAMOND:
			chance = GRANITE_DIAMOND;
			break;
		case GOLD:
			chance = GRANITE_GOLD;
			break;
		case IRON:
			chance = GRANITE_IRON;
			break;
		case REDSTONE:
			chance = GRANITE_REDSTONE;
			break;
		case BRONZE_ORE:
			chance = BRONZE;
			break;
		case STEEL_ORE:
			chance = STEEL;
			break;
		case TITANIUM_ORE:
			chance = TITANIUM;
			break;
		}
		return this.modifyChance(chance);
	}
	
	public double getDioriteChance(Mineral mineral) {
		double chance = 0;
		switch (mineral) {
		case EMERALD:
			chance = DIORITE_EMERALD;
			break;
		case DIAMOND:
			chance = DIORITE_DIAMOND;
			break;
		case GOLD:
			chance = DIORITE_GOLD;
			break;
		case IRON:
			chance = DIORITE_IRON;
			break;
		case REDSTONE:
			chance = DIORITE_REDSTONE;
			break;
		case BRONZE_ORE:
			chance = BRONZE;
			break;
		case STEEL_ORE:
			chance = STEEL;
			break;
		case TITANIUM_ORE:
			chance = TITANIUM;
			break;
		}
		return this.modifyChance(chance);
	}
	
	public double getAndesiteChance(Mineral mineral) {
		double chance = 0;
		switch (mineral) {
		case EMERALD:
			chance = ANDESITE_EMERALD;
			break;
		case DIAMOND:
			chance = ANDESITE_DIAMOND;
			break;
		case GOLD:
			chance = ANDESITE_GOLD;
			break;
		case IRON:
			chance = ANDESITE_IRON;
			break;
		case REDSTONE:
			chance = ANDESITE_REDSTONE;
			break;
		case BRONZE_ORE:
			chance = BRONZE;
			break;
		case STEEL_ORE:
			chance = STEEL;
			break;
		case TITANIUM_ORE:
			chance = TITANIUM;
			break;
		}
		return this.modifyChance(chance);
	}
	
	private double modifyChance(Double chance) {
		double increase = chance*this.getTown().getBuffManager().getEffectiveDouble(Buff.EXTRACTION);
		chance += increase;
		try {
			if (this.getTown().getGovernment().id.equals("gov_technocracy")) {
				chance *= CivSettings.getDouble(CivSettings.structureConfig, "trommel.technocracy_rate");
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
		this.level = getTown().saved_trommel_level;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
}
