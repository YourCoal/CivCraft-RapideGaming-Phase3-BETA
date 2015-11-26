package com.civcraft.threading.tasks;

import java.util.ArrayList;

import org.bukkit.ChunkSnapshot;

import com.civcraft.main.CivGlobal;
import com.civcraft.structure.Windmill;
import com.civcraft.structure.farm.FarmChunk;
import com.civcraft.threading.TaskMaster;
import com.civcraft.util.ChunkCoord;

public class WindmillStartSyncTask implements Runnable {
	
	Windmill windmill;
	public WindmillStartSyncTask(Windmill windmill) {
		this.windmill = windmill;
	}
	
	@Override
	public void run() {
		ChunkCoord cc = new ChunkCoord(windmill.getCorner());
		ArrayList<ChunkSnapshot> snapshots = new ArrayList<ChunkSnapshot>();
		int[][] offset = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }, { 1, 1 }, {-1,-1 }, {-1, 1}, {1, -1} };
		for (int i = 0; i < 8; i++) {
			cc.setX(cc.getX() + offset[i][0]);
			cc.setZ(cc.getZ() + offset[i][1]);
			FarmChunk farmChunk = CivGlobal.getFarmChunk(cc);
			if (farmChunk != null) {
				snapshots.add(farmChunk.getChunk().getChunkSnapshot());
			}
			cc.setFromLocation(windmill.getCorner().getLocation());
		}
		
		if (snapshots.size() == 0) {
			return;
		}
		TaskMaster.asyncTask("", new WindmillPreProcessTask(windmill, snapshots), 0);	
	}
}
