package com.avrgaming.civcraft.structure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.avrgaming.civcraft.config.CivSettings;
import com.avrgaming.civcraft.exception.CivException;
import com.avrgaming.civcraft.exception.InvalidConfiguration;
import com.avrgaming.civcraft.object.Buff;
import com.avrgaming.civcraft.object.Town;

public class Shipyard extends Structure {
	private static final double SMALL_IMPORT = CivSettings.getDoubleStructure("shipyard.small_import");
	private static final double MEDIUM_IMPORT = CivSettings.getDoubleStructure("shipyard.medium_import");
	private static final double LARGE_IMPORT = CivSettings.getDoubleStructure("shipyard.large_import");
	private static final double MEGA_IMPORT = CivSettings.getDoubleStructure("shipyard.mega_import");
	private static final double SPECIAL_IMPORT_1 = CivSettings.getDoubleStructure("shipyard.special_import_1");
	private static final double SPECIAL_IMPORT_2 = CivSettings.getDoubleStructure("shipyard.special_import_2");
	
	public int skippedCounter = 0;
	public ReentrantLock lock = new ReentrantLock();
	
	public enum ShipyardStock {
		SMALL,
		MEDIUM,
		LARGE,
		MEGA,
		SPECIAL1,
		SPECIAL2
	}
	
	protected Shipyard(Location center, String id, Town town) throws CivException {
		super(center, id, town);	
	}
	
	public Shipyard(ResultSet rs) throws SQLException, CivException {
		super(rs);
	}
	
	@Override
	public String getDynmapDescription() {
		return null;
	}
	
	@Override
	public String getMarkerIconName() {
		return "anchor";
	}
	
	public double getImportSize(ShipyardStock ss) {
		double chance = 0;
		switch (ss) {
		case SMALL:
			chance = SMALL_IMPORT;
			break;
		case MEDIUM:
			chance = MEDIUM_IMPORT;
			break;
		case LARGE:
			chance = LARGE_IMPORT;
			break;
		case MEGA:
			chance = MEGA_IMPORT;
			break;
		case SPECIAL1:
			chance = SPECIAL_IMPORT_1;
			break;
		case SPECIAL2:
			chance = SPECIAL_IMPORT_2;
		}
		
		double increase = chance*this.getTown().getBuffManager().getEffectiveDouble(Buff.SHIPPING);
		chance += increase;
		
		try {
			if (this.getTown().getGovernment().id.equals("gov_republic")) {
				chance *= CivSettings.getDouble(CivSettings.structureConfig, "shipyard.republic_rate");
			} else if (this.getTown().getGovernment().id.equals("gov_theocracy")){
				chance *= CivSettings.getDouble(CivSettings.structureConfig, "shipyard.penalty_rate");
			}
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
		}
		return chance;
	}
	
	@Override
	protected Location repositionCenter(Location center, String dir, double x_size, double z_size) {
		Location loc = new Location(center.getWorld(), 
				center.getX(), center.getY(), center.getZ(), 
				center.getYaw(), center.getPitch());
		
		// Reposition tile improvements
		if (this.isTileImprovement()) {
			// just put the center at 0,0 of this chunk?
			loc = center.getChunk().getBlock(0, center.getBlockY(), 0).getLocation();
			//loc = center.getChunk().getBlock(arg0, arg1, arg2)
		} else {
			if (dir.equalsIgnoreCase("east")) {
				loc.setZ(loc.getZ() - (z_size / 2));
				loc.setX(loc.getX() + SHIFT_OUT);
			}
			else if (dir.equalsIgnoreCase("west")) {
				loc.setZ(loc.getZ() - (z_size / 2));
				loc.setX(loc.getX() - (SHIFT_OUT+x_size));
	
			}
			else if (dir.equalsIgnoreCase("north")) {
				loc.setX(loc.getX() - (x_size / 2));
				loc.setZ(loc.getZ() - (SHIFT_OUT+z_size));
			}
			else if (dir.equalsIgnoreCase("south")) {
				loc.setX(loc.getX() - (x_size / 2));
				loc.setZ(loc.getZ() + SHIFT_OUT);
	
			}
		}
		
		if (this.getTemplateYShift() != 0) {
			// Y-Shift based on the config, this allows templates to be built underground.
			loc.setY(WATER_LEVEL + this.getTemplateYShift());
		}
	
		return loc;
	}
	
	public static int WATER_LEVEL = 62;
	public static int TOLERANCE = 20;
	
	@Override
	protected void checkBlockPermissionsAndRestrictions(Player player, Block centerBlock, int regionX, int regionY, int regionZ, Location savedLocation) throws CivException {
		super.checkBlockPermissionsAndRestrictions(player, centerBlock, regionX, regionY, regionZ, savedLocation);
		if ((player.getLocation().getBlockY() - WATER_LEVEL) > TOLERANCE) {
			throw new CivException("You must be close to the water's surface to build this structure.");
		}
	}
}