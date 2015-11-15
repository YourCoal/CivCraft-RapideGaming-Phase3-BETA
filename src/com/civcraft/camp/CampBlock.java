package com.civcraft.camp;

import com.civcraft.util.BlockCoord;

public class CampBlock {
	
	private BlockCoord coord;
	private Camp camp;
	private boolean friendlyBreakable = false;
	
	public CampBlock(BlockCoord coord, Camp camp) {
		this.coord = coord;
		this.camp = camp;
	}
	
	public CampBlock(BlockCoord coord, Camp camp, boolean friendlyBreakable) {
		this.coord = coord;
		this.camp = camp;
		this.friendlyBreakable = friendlyBreakable;
	}
	
	public BlockCoord getCoord() {
		return coord;
	}
	
	public void setCoord(BlockCoord coord) {
		this.coord = coord;
	}
	
	public Camp getCamp() {
		return camp;
	}
	
	public void setCamp(Camp camp) {
		this.camp = camp;
	}
	
	public int getX() {
		return this.coord.getX();
	}
	
	public int getY() {
		return this.coord.getY();
	}
	
	public int getZ() {
		return this.coord.getZ();
	}
	
	public String getWorldname() {
		return this.coord.getWorldname();
	}
	
	public boolean canBreak(String playerName) {
		if (this.friendlyBreakable == false) {
			return false;
		}
		
		if (camp.hasMember(playerName)) {
			return true;
		}
		return false;
	}
}
