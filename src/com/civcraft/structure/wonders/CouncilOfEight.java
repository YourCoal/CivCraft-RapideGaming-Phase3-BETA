package com.civcraft.structure.wonders;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Location;

import com.civcraft.exception.CivException;
import com.civcraft.object.Town;

public class CouncilOfEight extends Wonder {

	public CouncilOfEight(ResultSet rs) throws SQLException, CivException {
		super(rs);
	}

	public CouncilOfEight(Location center, String id, Town town) throws CivException {
		super(center, id, town);
	}

	@Override
	protected void removeBuffs() {
	}

	@Override
	protected void addBuffs() {		
	}

}
