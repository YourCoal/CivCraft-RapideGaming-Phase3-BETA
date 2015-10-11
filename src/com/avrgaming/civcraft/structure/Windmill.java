package com.avrgaming.civcraft.structure;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Location;

import com.avrgaming.civcraft.exception.CivException;
import com.avrgaming.civcraft.object.Town;
import com.avrgaming.civcraft.threading.TaskMaster;
import com.avrgaming.civcraft.threading.tasks.WindmillStartSyncTask;

public class Windmill extends Structure {
	
	public enum CropType {
		WHEAT,
		CARROTS,
		POTATOES
	}
	
	public Windmill(ResultSet rs) throws SQLException, CivException {
		super(rs);
	}
	
	public Windmill(Location center, String id, Town town)
			throws CivException {
		super(center, id, town);
	}
	
	@Override
	public void onEffectEvent() {
	}
	
	public void processWindmill() {
		/* Fire a sync task to perform this. */
		TaskMaster.syncTask(new WindmillStartSyncTask(this), 0);
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
