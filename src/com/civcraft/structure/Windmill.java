package com.civcraft.structure;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Location;

import com.civcraft.exception.CivException;
import com.civcraft.object.Town;
import com.civcraft.threading.TaskMaster;
import com.civcraft.threading.tasks.WindmillStartSyncTask;

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
		TaskMaster.syncTask(new WindmillStartSyncTask(this), 0);
	}
}
