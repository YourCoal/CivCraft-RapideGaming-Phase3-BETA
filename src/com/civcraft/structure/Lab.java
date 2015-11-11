package com.civcraft.structure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

import com.civcraft.components.AttributeBiomeRadiusPerLevel;
import com.civcraft.components.ConsumeLevelComponent;
import com.civcraft.components.ConsumeLevelComponent.Result;
import com.civcraft.config.CivSettings;
import com.civcraft.config.ConfigLabLevel;
import com.civcraft.exception.CivException;
import com.civcraft.exception.CivTaskAbortException;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Buff;
import com.civcraft.object.StructureChest;
import com.civcraft.object.Town;
import com.civcraft.threading.CivAsyncTask;
import com.civcraft.util.CivColor;
import com.civcraft.util.MultiInventory;

public class Lab extends Structure {

	private ConsumeLevelComponent consumeComp = null;
	
	protected Lab(Location center, String id, Town town) throws CivException {
		super(center, id, town);
	}

	public Lab(ResultSet rs) throws SQLException, CivException {
		super(rs);
	}
	
	@Override
	public void loadSettings() {
		super.loadSettings();
	}
	
	public String getkey() {
		return getTown().getName()+"_"+this.getConfigId()+"_"+this.getCorner().toString(); 
	}
		
	@Override
	public String getDynmapDescription() {
		return null;
	}
	
	@Override
	public String getMarkerIconName() {
		return "ruby";
	}
	
	public ConsumeLevelComponent getConsumeComponent() {
		if (consumeComp == null) {
			consumeComp = (ConsumeLevelComponent) this.getComponent(ConsumeLevelComponent.class.getSimpleName());
		}
		return consumeComp;
	}
	
	public Result consume(CivAsyncTask task) throws InterruptedException {
		
		//Look for the lab's chest.
		if (this.getChests().size() == 0)
			return Result.STAGNATE;	

		MultiInventory multiInv = new MultiInventory();
		
		ArrayList<StructureChest> chests = this.getAllChestsById(0);
		
		// Make sure the chest is loaded and add it to the multi inv.
		for (StructureChest c : chests) {
			task.syncLoadChunk(c.getCoord().getWorldname(), c.getCoord().getX(), c.getCoord().getZ());
			Inventory tmp;
			try {
				tmp = task.getChestInventory(c.getCoord().getWorldname(), c.getCoord().getX(), c.getCoord().getY(), c.getCoord().getZ(), true);
			} catch (CivTaskAbortException e) {
				return Result.STAGNATE;
			}
			multiInv.addInventory(tmp);
		}
		getConsumeComponent().setSource(multiInv);
		getConsumeComponent().setConsumeRate(1.0);
		Result result = getConsumeComponent().processConsumption();
		getConsumeComponent().onSave();		
		return result;
	}
	
	public void process_lab(CivAsyncTask task) throws InterruptedException {	
		Result result = this.consume(task);
		switch (result) {
		case STARVE:
			CivMessage.sendTown(getTown(), CivColor.LightGreen+"A level "+getConsumeComponent().getLevel()+" lab's production "+
					CivColor.Rose+"fell. "+CivColor.LightGreen+getConsumeComponent().getCountString());
			break;
		case LEVELDOWN:
			CivMessage.sendTown(getTown(), CivColor.LightGreen+"A lab ran out of redstone and "+
					CivColor.Rose+"lost"+CivColor.LightGreen+" a level. It is now level "+getConsumeComponent().getLevel());
			break;
		case STAGNATE:
			CivMessage.sendTown(getTown(), CivColor.LightGreen+"A level "+
					getConsumeComponent().getLevel()+" lab "+CivColor.Yellow+"stagnated "+CivColor.LightGreen+getConsumeComponent().getCountString());
			break;
		case GROW:
			CivMessage.sendTown(getTown(), CivColor.LightGreen+"A level "+getConsumeComponent().getLevel()+" lab's production "+
					CivColor.Green+"rose. "+CivColor.LightGreen+getConsumeComponent().getCountString());
			break;
		case LEVELUP:
			CivMessage.sendTown(getTown(), CivColor.LightGreen+"A lab "+CivColor.Green+"gained"+CivColor.LightGreen+
					" a level. It is now level "+getConsumeComponent().getLevel());
			break;
		case MAXED:
			CivMessage.sendTown(getTown(), CivColor.LightGreen+"A level "+getConsumeComponent().getLevel()+" lab is "+
					CivColor.Green+"maxed. "+CivColor.LightGreen+getConsumeComponent().getCountString());
			break;
		default:
			break;
		}
	}
	
	public int getLevel() {
		return this.getConsumeComponent().getLevel();
	}
	
	public double getBeakersPerTile() {
		AttributeBiomeRadiusPerLevel attrBiome = (AttributeBiomeRadiusPerLevel)this.getComponent("AttributeBiomeRadiusPerLevel");
		double base = attrBiome.getBaseValue();
		double rate = 1;
		rate += this.getTown().getBuffManager().getEffectiveDouble(Buff.ADVANCED_TOOLING);
		return (rate*base);
	}
	
	public int getCount() {
		return this.getConsumeComponent().getCount();
	}
	
	public int getMaxCount() {
		int level = getLevel();
		
		ConfigLabLevel lvl = CivSettings.labLevels.get(level);
		return lvl.count;
	}
	
	public Result getLastResult() {
		return this.getConsumeComponent().getLastResult();
	}
}