package com.avrgaming.civcraft.structure;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Location;

import com.avrgaming.civcraft.exception.CivException;
import com.avrgaming.civcraft.object.Town;

public class Granary extends Structure {
	
	protected Granary(Location center, String id, Town town)
			throws CivException {
		super(center, id, town);
	}
	
	public Granary(ResultSet rs) throws SQLException, CivException {
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
}
