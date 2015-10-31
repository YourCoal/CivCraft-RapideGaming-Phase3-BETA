package com.civcraft.structure;

import java.util.List;

import com.civcraft.util.BlockCoord;

public interface RespawnLocationHolder {

	public String getRespawnName();
	public List<BlockCoord> getRespawnPoints();
	public BlockCoord getRandomRevivePoint();
	
}
