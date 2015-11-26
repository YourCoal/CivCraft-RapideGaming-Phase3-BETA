/*************************************************************************
 * 
 * AVRGAMING LLC
 * __________________
 * 
 *  [2013] AVRGAMING LLC
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of AVRGAMING LLC and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to AVRGAMING LLC
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from AVRGAMING LLC.
 */
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
import com.civcraft.config.ConfigMineLevel;
import com.civcraft.exception.CivException;
import com.civcraft.exception.CivTaskAbortException;
import com.civcraft.exception.InvalidConfiguration;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Buff;
import com.civcraft.object.StructureChest;
import com.civcraft.object.Town;
import com.civcraft.threading.CivAsyncTask;
import com.civcraft.util.CivColor;
import com.civcraft.util.MultiInventory;

public class Mine extends Structure {

	private ConsumeLevelComponent consumeComp = null;
	
	protected Mine(Location center, String id, Town town) throws CivException {
		super(center, id, town);
	}

	public Mine(ResultSet rs) throws SQLException, CivException {
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
		return "hammer";
	}
	
	public ConsumeLevelComponent getConsumeComponent() {
		if (consumeComp == null) {
			consumeComp = (ConsumeLevelComponent) this.getComponent(ConsumeLevelComponent.class.getSimpleName());
		}
		return consumeComp;
	}
	
	public Result consume(CivAsyncTask task) throws InterruptedException {
		
		//Look for the mine's chest.
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
	
	public void process_mine(CivAsyncTask task) throws InterruptedException {	
		Result result = this.consume(task);
		switch (result) {
		case STARVE:
			CivMessage.sendTown(getTown(), CivColor.LightGreen+"A level "+getConsumeComponent().getLevel()+" mine's production "+
					CivColor.Rose+"fell. "+CivColor.LightGreen+getConsumeComponent().getCountString());
			break;
		case LEVELDOWN:
			CivMessage.sendTown(getTown(), CivColor.LightGreen+"A mine ran out of redstone and "+
					CivColor.Rose+"lost"+CivColor.LightGreen+" a level. It is now level "+getConsumeComponent().getLevel());
			break;
		case STAGNATE:
			CivMessage.sendTown(getTown(), CivColor.LightGreen+"A level "+
					getConsumeComponent().getLevel()+" mine "+CivColor.Yellow+"stagnated "+CivColor.LightGreen+getConsumeComponent().getCountString());
			break;
		case GROW:
			CivMessage.sendTown(getTown(), CivColor.LightGreen+"A level "+getConsumeComponent().getLevel()+" mine's production "+
					CivColor.Green+"rose. "+CivColor.LightGreen+getConsumeComponent().getCountString());
			break;
		case LEVELUP:
			CivMessage.sendTown(getTown(), CivColor.LightGreen+"A mine "+CivColor.Green+"gained"+CivColor.LightGreen+
					" a level. It is now level "+getConsumeComponent().getLevel());
			break;
		case MAXED:
			CivMessage.sendTown(getTown(), CivColor.LightGreen+"A level "+getConsumeComponent().getLevel()+" mine is "+
					CivColor.Green+"maxed. "+CivColor.LightGreen+getConsumeComponent().getCountString());
			break;
		default:
			break;
		}
	}

	public int getLevel() {
		return this.getConsumeComponent().getLevel();
	}
	
	public double getBonusHammers() {
		int level = getLevel();
		ConfigMineLevel lvl = CivSettings.mineLevels.get(level);
		return lvl.hammers;	
	}
	
	public double getHammersPerTile() {
		AttributeBiomeRadiusPerLevel attrBiome = (AttributeBiomeRadiusPerLevel)this.getComponent("AttributeBiomeRadiusPerLevel");
		double base = attrBiome.getBaseValue();
		double rate = 1;
		rate += this.getTown().getBuffManager().getEffectiveDouble(Buff.ADVANCED_MINING);
		
		if (this.getCiv().hasTechnology("tech_atom_developing")) {
			double bonus;
			try {
				bonus = CivSettings.getDouble(CivSettings.techsConfig, "atom_developing_mine_buff");
				rate *= bonus;
			} catch (InvalidConfiguration e) {
				e.printStackTrace();
			}
		}
		return (rate*base);
	}

	public int getCount() {
		return this.getConsumeComponent().getCount();
	}

	public int getMaxCount() {
		int level = getLevel();
		
		ConfigMineLevel lvl = CivSettings.mineLevels.get(level);
		return lvl.count;	
	}

	public Result getLastResult() {
		return this.getConsumeComponent().getLastResult();
	}
}
