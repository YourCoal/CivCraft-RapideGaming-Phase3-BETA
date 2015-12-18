package com.civcraft.structure;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.civcraft.components.AttributeBiomeRadiusPerLevel;
import com.civcraft.components.TradeShipyardLevelComponent;
import com.civcraft.components.TradeShipyardResults;
import com.civcraft.components.TradeShipyardLevelComponent.Result;
import com.civcraft.config.CivSettings;
import com.civcraft.config.ConfigMineLevel;
import com.civcraft.exception.CivException;
import com.civcraft.exception.CivTaskAbortException;
import com.civcraft.exception.InvalidConfiguration;
import com.civcraft.main.CivData;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Buff;
import com.civcraft.object.Town;
import com.civcraft.template.Template;
import com.civcraft.threading.CivAsyncTask;
import com.civcraft.threading.TaskMaster;
import com.civcraft.util.BlockCoord;
import com.civcraft.util.CivColor;
import com.civcraft.util.ItemManager;
import com.civcraft.util.MultiInventory;
import com.civcraft.util.SimpleBlock;
import com.civcraft.util.TimeTools;

public class Shipyard extends Structure {
	
	private int upgradeLevel = 1;
	private int tickLevel = 1;

	public HashSet<BlockCoord> goodsDepositPoints = new HashSet<BlockCoord>();
	public HashSet<BlockCoord> goodsWithdrawPoints = new HashSet<BlockCoord>();
	
	private TradeShipyardLevelComponent consumeComp = null;
	
	public static int WATER_LEVEL = 62;
	public static int TOLERANCE = 40;
	
	@Override
	protected void checkBlockPermissionsAndRestrictions(Player player, Block centerBlock, int regionX, int regionY, int regionZ, Location savedLocation) throws CivException {
		super.checkBlockPermissionsAndRestrictions(player, centerBlock, regionX, regionY, regionZ, savedLocation);
		if ((player.getLocation().getBlockY() - WATER_LEVEL) > TOLERANCE) {
			throw new CivException("You must be close to the water's surface to build this structure.");
		}
	}
	
	public Shipyard(ResultSet rs) throws SQLException, CivException {
		super(rs);
	}
	
	protected Shipyard(Location center, String id, Town town) throws CivException {
		super(center, id, town);
	}
	
	@Override
	public String getMarkerIconName() {
		return "anchor";
	}
	
	@Override
	public void loadSettings() {
		super.loadSettings();
	}
	
	public String getkey() {
		return getTown().getName()+"_"+this.getConfigId()+"_"+this.getCorner().toString(); 
	}
	
	public TradeShipyardLevelComponent getConsumeComponent() {
		if (consumeComp == null) {
			consumeComp = (TradeShipyardLevelComponent) this.getComponent(TradeShipyardLevelComponent.class.getSimpleName());
		}
		return consumeComp;
	}
	
	@Override 
	public void updateSignText() {
		reprocessCommandSigns();
	}
	
	public void reprocessCommandSigns() {		
		/* Load in the template. */
		//Template tpl = new Template();
		Template tpl;
		try {
			//tpl.load_template(this.getSavedTemplatePath());
			tpl = Template.getTemplate(this.getSavedTemplatePath(), null);
		} catch (IOException | CivException e) {
			e.printStackTrace();
			return;
		}
		class SyncTask implements Runnable {
			Template template;
			BlockCoord structCorner;
			
			public SyncTask(Template template, BlockCoord structCorner) {
				this.template = template;
				this.structCorner = structCorner;
			}
			
			@Override
			public void run() {
				
				processCommandSigns(template, structCorner);
			}
		}
		
		TaskMaster.syncTask(new SyncTask(tpl, corner), TimeTools.toTicks(1));

	}
	
	private void processCommandSigns(Template tpl, BlockCoord corner) {
		for (BlockCoord relativeCoord : tpl.commandBlockRelativeLocations) {
			SimpleBlock sb = tpl.blocks[relativeCoord.getX()][relativeCoord.getY()][relativeCoord.getZ()];
			BlockCoord absCoord = new BlockCoord(corner.getBlock().getRelative(relativeCoord.getX(), relativeCoord.getY(), relativeCoord.getZ()));
			switch (sb.command) {
			
			case "/incoming": {
				Integer ID = Integer.valueOf(sb.keyvalues.get("id"));
				if (this.getUpgradeLvl() >= ID+1) {
					this.goodsWithdrawPoints.add(absCoord);
					ItemManager.setTypeId(absCoord.getBlock(), ItemManager.getId(Material.CHEST));
					byte data3 = CivData.convertSignDataToChestData((byte)sb.getData());
					ItemManager.setData(absCoord.getBlock(), data3);
				} else {
					ItemManager.setTypeId(absCoord.getBlock(), ItemManager.getId(Material.AIR));
					ItemManager.setData(absCoord.getBlock(), sb.getData());
				}
				this.addStructureBlock(absCoord, false);
				break;
			}
			case "/inSign": {
				Integer ID = Integer.valueOf(sb.keyvalues.get("id"));
				if (this.getUpgradeLvl() >= ID+1) {
					ItemManager.setTypeId(absCoord.getBlock(), ItemManager.getId(Material.WALL_SIGN));
					ItemManager.setData(absCoord.getBlock(), sb.getData());
					Sign sign = (Sign)absCoord.getBlock().getState();
					sign.setLine(0, "Incoming");
					sign.setLine(1, ""+(ID+1));
					sign.setLine(2, "");
					sign.setLine(3, "");
					sign.update();
				} else {
					ItemManager.setTypeId(absCoord.getBlock(), ItemManager.getId(Material.WALL_SIGN));
					ItemManager.setData(absCoord.getBlock(), sb.getData());
					Sign sign = (Sign)absCoord.getBlock().getState();
					sign.setLine(0, "Incoming");
					sign.setLine(1, "Unavailable");
					sign.setLine(2, "");
					sign.setLine(3, "");
					sign.update();
				}
				this.addStructureBlock(absCoord, false);
				break;
			}
			case "/outgoing": {
				Integer ID = Integer.valueOf(sb.keyvalues.get("id"));
				if (this.getLevel() >= (ID*2)+1) {
					this.goodsDepositPoints.add(absCoord);
					ItemManager.setTypeId(absCoord.getBlock(), ItemManager.getId(Material.CHEST));
					byte data3 = CivData.convertSignDataToChestData((byte)sb.getData());
					ItemManager.setData(absCoord.getBlock(), data3);
					this.addStructureBlock(absCoord, false);
				} else {
					ItemManager.setTypeId(absCoord.getBlock(), ItemManager.getId(Material.AIR));
					ItemManager.setData(absCoord.getBlock(), sb.getData());
				}
				break;
			}
			case "/outSign": {
				Integer ID = Integer.valueOf(sb.keyvalues.get("id"));
				if (this.getLevel() >= (ID*2)+1) {
					ItemManager.setTypeId(absCoord.getBlock(), ItemManager.getId(Material.WALL_SIGN));
					ItemManager.setData(absCoord.getBlock(), sb.getData());
					Sign sign = (Sign)absCoord.getBlock().getState();
					sign.setLine(0, "Outgoing");
					sign.setLine(1, ""+(ID+1));
					sign.setLine(2, "");
					sign.setLine(3, "");
					sign.update();
				} else {
					ItemManager.setTypeId(absCoord.getBlock(), ItemManager.getId(Material.WALL_SIGN));
					ItemManager.setData(absCoord.getBlock(), sb.getData());
					Sign sign = (Sign)absCoord.getBlock().getState();
					sign.setLine(0, "Outgoing");
					sign.setLine(1, "Unavailable");
					sign.setLine(2, "Level "+(ID*2)+1);
					sign.setLine(3, "Required");
					sign.update();
				}
				this.addStructureBlock(absCoord, false);
				break;
			}
			case "/in": {
				Integer ID = Integer.valueOf(sb.keyvalues.get("id"));
				if (ID == 0) {
					ItemManager.setTypeId(absCoord.getBlock(), ItemManager.getId(Material.WALL_SIGN));
					ItemManager.setData(absCoord.getBlock(), sb.getData());
					Sign sign = (Sign)absCoord.getBlock().getState();
					sign.setLine(0, "Incoming");
					sign.setLine(1, "1");
					sign.setLine(2, "2");
					sign.setLine(3, "");
					sign.update();
				} else {
					ItemManager.setTypeId(absCoord.getBlock(), ItemManager.getId(Material.WALL_SIGN));
					ItemManager.setData(absCoord.getBlock(), sb.getData());
					Sign sign = (Sign)absCoord.getBlock().getState();
					sign.setLine(0, "Incoming");
					sign.setLine(1, "3");
					sign.setLine(2, "4");
					sign.setLine(3, "");
					sign.update();
				}
				this.addStructureBlock(absCoord, false);
				break;
			}
			default: {
				/* Unrecognized command... treat as a literal sign. */
				ItemManager.setTypeId(absCoord.getBlock(), ItemManager.getId(Material.WALL_SIGN));
				ItemManager.setData(absCoord.getBlock(), sb.getData());
				Sign sign = (Sign)absCoord.getBlock().getState();
				sign.setLine(0, sb.message[0]);
				sign.setLine(1, sb.message[1]);
				sign.setLine(2, sb.message[2]);
				sign.setLine(3, sb.message[3]);
				sign.update();
				this.addStructureBlock(absCoord, false);
				break;
				}
			}
		}
	}
	
	public TradeShipyardResults consume(CivAsyncTask task) throws InterruptedException {
		TradeShipyardResults tradeResult;
		//Look for the TradeShip chests.
		if (this.goodsDepositPoints.size() == 0 || this.goodsWithdrawPoints.size() == 0) {
			tradeResult = new TradeShipyardResults();
			tradeResult.setResult(Result.STAGNATE);
			return tradeResult;
		}
		
		MultiInventory mInv = new MultiInventory();
		for (BlockCoord bcoord : this.goodsDepositPoints) {
			task.syncLoadChunk(bcoord.getWorldname(), bcoord.getX(), bcoord.getZ());
			Inventory tmp;
			try {
				tmp = task.getChestInventory(bcoord.getWorldname(), bcoord.getX(), bcoord.getY(), bcoord.getZ(), true);
			} catch (CivTaskAbortException e) {
				tradeResult = new TradeShipyardResults();
				tradeResult.setResult(Result.STAGNATE);
				return tradeResult;
			}
			mInv.addInventory(tmp);
		}
		
		if (mInv.getInventoryCount() == 0) {
			tradeResult = new TradeShipyardResults();
			tradeResult.setResult(Result.STAGNATE);
			return tradeResult;
		}
		getConsumeComponent().setSource(mInv);
		getConsumeComponent().setConsumeRate(1.0);
		tradeResult = getConsumeComponent().processConsumption(this.getUpgradeLvl()-1);
		getConsumeComponent().onSave();		
		return tradeResult;
	}
	
	public void process_shipyard_trade(CivAsyncTask task) throws InterruptedException, InvalidConfiguration {	
		TradeShipyardResults tradeResult = this.consume(task);
		Result result = tradeResult.getResult();
		switch (result) {
		case STAGNATE:
			CivMessage.sendTown(getTown(), CivColor.LightGreen+"A level "+
					getConsumeComponent().getLevel()+" Trade Ship "+CivColor.Yellow+"stagnated "+CivColor.LightGreen+getConsumeComponent().getCountString());
			break;
		case GROW:
			CivMessage.sendTown(getTown(), CivColor.LightGreen+"A level "+getConsumeComponent().getLevel()+" Trade Ship's process "+
					CivColor.Green+"gained. "+CivColor.LightGreen+getConsumeComponent().getCountString());
			break;
		case LEVELUP:
			CivMessage.sendTown(getTown(), CivColor.LightGreen+"A Trade Ship "+CivColor.Green+"gained"+CivColor.LightGreen+
					" a level. It is now level "+getConsumeComponent().getLevel());
			break;
		case MAXED:
			CivMessage.sendTown(getTown(), CivColor.LightGreen+"A level "+getConsumeComponent().getLevel()+" Trade Ship is "+
					CivColor.Green+"maxed. "+CivColor.LightGreen+getConsumeComponent().getCountString());
			break;
		default:
			break;
		}
		
		if (tradeResult.getCulture() >= 1) {
			int total_culture = (int)Math.round(tradeResult.getCulture());
			this.getTown().addAccumulatedCulture(total_culture);
			this.getTown().save();
		}
		
		if (tradeResult.getMoney() >= 1) {
			double total_coins = tradeResult.getMoney();
			//if (this.getTown().getBuffManager().hasBuff("buff_ingermanland_trade_ship_income")) {
			//	total_coins *= this.getTown().getBuffManager().getEffectiveDouble("buff_ingermanland_trade_ship_income");
			//}
			
			double taxesPaid = total_coins*this.getTown().getDepositCiv().getIncomeTaxRate();
			if (total_coins >= 1) {
				CivMessage.sendTown(getTown(), CivColor.LightGreen+"A Trade Ship Generated "+Math.round(total_coins)+" coins of income and "+tradeResult.getCulture()
						+" culture produced from a total of "+tradeResult.getConsumed()+" units of cargo.");
			}
			
			if (taxesPaid > 0) {
				CivMessage.sendTown(this.getTown(), CivColor.Yellow+" (Paid "+taxesPaid+" in taxes to "+this.getTown().getDepositCiv().getName()+")");;
			}
			this.getTown().getTreasury().deposit(total_coins - taxesPaid);
			this.getTown().getDepositCiv().taxPayment(this.getTown(), taxesPaid);
		}
		
		if (tradeResult.getReturnCargo().size() >= 1) {
			MultiInventory multiInv = new MultiInventory();
			
			for (BlockCoord bcoord : this.goodsWithdrawPoints) {
				task.syncLoadChunk(bcoord.getWorldname(), bcoord.getX(), bcoord.getZ());
				Inventory tmp;
				try {
					tmp = task.getChestInventory(bcoord.getWorldname(), bcoord.getX(), bcoord.getY(), bcoord.getZ(), true);
					multiInv.addInventory(tmp);
				} catch (CivTaskAbortException e) {
					e.printStackTrace();
				}
			}
			
			for (ItemStack item :tradeResult.getReturnCargo()) {
				multiInv.addItem(item);
			}
			//CivMessage.sendTown(getTown(), CivColor.LightGreen+"It also brought back something specail! Check your Trade Ship's incoming cargo hold.");
		}
	}
	
	public void onPostBuild(BlockCoord absCoord, SimpleBlock commandBlock) {
		this.upgradeLevel = getTown().saved_trade_shipyard_upgrade_level;
		this.reprocessCommandSigns();
	}
	
	public int getUpgradeLvl() {
		return upgradeLevel;
	}
	
	public void setUpgradeLvl(int level) {
		this.upgradeLevel = level;
		this.reprocessCommandSigns();
	}
	
	public int getLevel() {
		try {
			return this.getConsumeComponent().getLevel();
		} catch (Exception e) {
			return tickLevel;
		}
	}
	
	public double getHammersPerTile() {
		AttributeBiomeRadiusPerLevel attrBiome = (AttributeBiomeRadiusPerLevel)this.getComponent("AttributeBiomeBase");
		double base = attrBiome.getBaseValue();
		
		double rate = 1;
		rate += this.getTown().getBuffManager().getEffectiveDouble(Buff.ADVANCED_WATER_TRADING);
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
	
	@Override
	public void delete() throws SQLException {
		super.delete();
		if (getConsumeComponent() != null) {
			getConsumeComponent().onDelete();
		}
	}
	
	public void onDestroy() {
		super.onDestroy();
		getConsumeComponent().setLevel(1);
		getConsumeComponent().setCount(0);
		getConsumeComponent().onSave();
	}
}
