package com.civcraft.threading.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.civcraft.exception.CivTaskAbortException;
import com.civcraft.lorestorage.LoreMaterial;
import com.civcraft.main.CivData;
import com.civcraft.main.CivLog;
import com.civcraft.object.StructureChest;
import com.civcraft.structure.Structure;
import com.civcraft.structure.Trommel;
import com.civcraft.structure.Trommel.Mineral;
import com.civcraft.threading.CivAsyncTask;
import com.civcraft.threading.sync.request.UpdateInventoryRequest.Action;
import com.civcraft.util.ItemManager;
import com.civcraft.util.MultiInventory;

public class TrommelAsyncTask extends CivAsyncTask {

	Trommel trommel;
	private static final int GRAVEL_RATE = 1;
	
	public static HashSet<String> debugTowns = new HashSet<String>();

	public static void debug(Trommel trommel, String msg) {
		if (debugTowns.contains(trommel.getTown().getName())) {
			CivLog.warning("TrommelDebug:"+trommel.getTown().getName()+":"+msg);
		}
	}	
	
	public TrommelAsyncTask(Structure trommel) {
		this.trommel = (Trommel)trommel;
	}
	
	public void processTrommelUpdate() {
		if (!trommel.isActive()) {
			debug(trommel, "trommel inactive...");
			return;
		}
		
		debug(trommel, "Processing trommel...");
		// Grab each CivChest object we'll require.
		ArrayList<StructureChest> sources = trommel.getAllChestsById(1);
		ArrayList<StructureChest> destinations = trommel.getAllChestsById(2);
		
		if (sources.size() != 2 || destinations.size() != 2) {
			CivLog.error("Bad chests for trommel in town:"+trommel.getTown().getName()+" sources:"+sources.size()+" dests:"+destinations.size());
			return;
		}
		
		// Make sure the chunk is loaded before continuing. Also, add get chest and add it to inventory.
		MultiInventory source_inv = new MultiInventory();
		MultiInventory dest_inv = new MultiInventory();

		try {
			for (StructureChest src : sources) {
				//this.syncLoadChunk(src.getCoord().getWorldname(), src.getCoord().getX(), src.getCoord().getZ());				
				Inventory tmp;
				try {
					tmp = this.getChestInventory(src.getCoord().getWorldname(), src.getCoord().getX(), src.getCoord().getY(), src.getCoord().getZ(), false);
				} catch (CivTaskAbortException e) {
					//e.printStackTrace();
					CivLog.warning("Trommel:"+e.getMessage());
					return;
				}
				if (tmp == null) {
					trommel.skippedCounter++;
					return;
				}
				source_inv.addInventory(tmp);
			}
			
			boolean full = true;
			for (StructureChest dst : destinations) {
				//this.syncLoadChunk(dst.getCoord().getWorldname(), dst.getCoord().getX(), dst.getCoord().getZ());
				Inventory tmp;
				try {
					tmp = this.getChestInventory(dst.getCoord().getWorldname(), dst.getCoord().getX(), dst.getCoord().getY(), dst.getCoord().getZ(), false);
				} catch (CivTaskAbortException e) {
					//e.printStackTrace();
					CivLog.warning("Trommel:"+e.getMessage());
					return;
				}
				if (tmp == null) {
					trommel.skippedCounter++;
					return;
				}
				dest_inv.addInventory(tmp);
				
				for (ItemStack stack : tmp.getContents()) {
					if (stack == null) {
						full = false;
						break;
					}
				}
			}
			
			if (full) {
				/* Trommel destination chest is full, stop processing. */
				return;
			}
		} catch (InterruptedException e) {
			return;
		}
		
		debug(trommel, "Processing trommel:"+trommel.skippedCounter+1);
		ItemStack[] contents = source_inv.getContents();
		for (int i = 0; i < trommel.skippedCounter+1; i++) {
		
			for(ItemStack stack : contents) {
				if (stack == null) {
					continue;
				}
				
				if (ItemManager.getId(stack) == CivData.COBBLESTONE) {
					try {
						this.updateInventory(Action.REMOVE, source_inv, ItemManager.createItemStack(CivData.COBBLESTONE, 1));
					} catch (InterruptedException e) {
						return;
					}
					
					Random rand = new Random();
					int randMax = Trommel.GRAVEL_MAX_RATE;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((trommel.getGravelChance(Mineral.EMERALD))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.EMERALD, 1);
					} else if (rand1 < ((int)((trommel.getGravelChance(Mineral.DIAMOND))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.DIAMOND, 1);
					} else if (rand1 < ((int)((trommel.getGravelChance(Mineral.REDSTONE))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.REDSTONE_DUST, 2);
					} else if (rand1 < ((int)((trommel.getGravelChance(Mineral.GOLD))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, 1);
					} else if (rand1 < ((int)((trommel.getGravelChance(Mineral.IRON))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
					} else if (rand1 < ((int)((trommel.getGravelChance(Mineral.BRONZE_ORE))*randMax))) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:bronze_ore"));
					} else if (rand1 < ((int)((trommel.getGravelChance(Mineral.STEEL_ORE))*randMax))) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:steel_ore"));
					} else if (rand1 < ((int)((trommel.getGravelChance(Mineral.TITANIUM_ORE))*randMax))) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:titanium_ore"));
					} else {
						newItem = ItemManager.createItemStack(CivData.GRAVEL, (Integer)GRAVEL_RATE);
					}
					try {
						debug(trommel, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
				
//				if (ItemManager.getId(stack) == CivData.STONE) {
//					if (this.trommel.getLevel() >= 2 && ItemManager.getData(stack) == 
//							ItemManager.getData(ItemManager.getMaterialData(CivData.STONE, CivData.GRANITE))) {
//						try {
//							this.updateInventory(Action.REMOVE, source_inv, ItemManager.createItemStack(CivData.STONE, 1, (short) CivData.GRANITE));
//						} catch (InterruptedException e) {
//							return;
//						}
//						
//						Random rand = new Random();
//						int randMax = Trommel.GRANITE_MAX_RATE;
//						int rand1 = rand.nextInt(randMax);
//						ItemStack newItem;
//						if (rand1 < ((int)((trommel.getGraniteChance(Mineral.EMERALD))*randMax))) {
//							newItem = ItemManager.createItemStack(CivData.EMERALD, 1);
//						} else if (rand1 < ((int)((trommel.getGraniteChance(Mineral.DIAMOND))*randMax))) {
//							newItem = ItemManager.createItemStack(CivData.DIAMOND, 1);
//						} else if (rand1 < ((int)((trommel.getGraniteChance(Mineral.REDSTONE))*randMax))) {
//							newItem = ItemManager.createItemStack(CivData.REDSTONE_DUST, 2);
//						} else if (rand1 < ((int)((trommel.getGraniteChance(Mineral.GOLD))*randMax))) {
//							newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, 1);
//						} else if (rand1 < ((int)((trommel.getGraniteChance(Mineral.IRON))*randMax))) {
//							newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
//						} else if (rand1 < ((int)((trommel.getGraniteChance(Mineral.BRONZE_ORE))*randMax))) {
//							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:bronze_ore"));
//						} else if (rand1 < ((int)((trommel.getGraniteChance(Mineral.STEEL_ORE))*randMax))) {
//							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:steel_ore"));
//						} else if (rand1 < ((int)((trommel.getGraniteChance(Mineral.TITANIUM_ORE))*randMax))) {
//							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:titanium_ore"));
//						} else {
//							newItem = ItemManager.createItemStack(CivData.GRAVEL, (Integer)GRAVEL_RATE);
//						}
//						try {
//							debug(trommel, "Updating inventory:"+newItem);
//							this.updateInventory(Action.ADD, dest_inv, newItem);
//						} catch (InterruptedException e) {
//							return;
//						}
//						break;
//					}
//					
//					
//					if (this.trommel.getLevel() >= 3 && ItemManager.getData(stack) == 
//							ItemManager.getData(ItemManager.getMaterialData(CivData.STONE, CivData.DIORITE))) {
//						try {
//							this.updateInventory(Action.REMOVE, source_inv, ItemManager.createItemStack(CivData.STONE, 1, (short) CivData.DIORITE));
//						} catch (InterruptedException e) {
//							return;
//						}
//						
//						Random rand = new Random();
//						int randMax = Trommel.DIORITE_MAX_RATE;
//						int rand1 = rand.nextInt(randMax);
//						ItemStack newItem;
//						if (rand1 < ((int)((trommel.getDioriteChance(Mineral.EMERALD))*randMax))) {
//							newItem = ItemManager.createItemStack(CivData.EMERALD, 1);
//						} else if (rand1 < ((int)((trommel.getDioriteChance(Mineral.DIAMOND))*randMax))) {
//							newItem = ItemManager.createItemStack(CivData.DIAMOND, 1);
//						} else if (rand1 < ((int)((trommel.getDioriteChance(Mineral.REDSTONE))*randMax))) {
//							newItem = ItemManager.createItemStack(CivData.REDSTONE_DUST, 2);
//						} else if (rand1 < ((int)((trommel.getDioriteChance(Mineral.GOLD))*randMax))) {
//							newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, 1);
//						} else if (rand1 < ((int)((trommel.getDioriteChance(Mineral.IRON))*randMax))) {
//							newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
//						} else if (rand1 < ((int)((trommel.getDioriteChance(Mineral.BRONZE_ORE))*randMax))) {
//							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:bronze_ore"));
//						} else if (rand1 < ((int)((trommel.getDioriteChance(Mineral.STEEL_ORE))*randMax))) {
//							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:steel_ore"));
//						} else if (rand1 < ((int)((trommel.getDioriteChance(Mineral.TITANIUM_ORE))*randMax))) {
//							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:titanium_ore"));
//						} else {
//							newItem = ItemManager.createItemStack(CivData.GRAVEL, (Integer)GRAVEL_RATE);
//						}
//						try {
//							debug(trommel, "Updating inventory:"+newItem);
//							this.updateInventory(Action.ADD, dest_inv, newItem);
//						} catch (InterruptedException e) {
//							return;
//						}
//						break;
//					}
//					
//					
//					if (this.trommel.getLevel() >= 4 && ItemManager.getData(stack) == 
//							ItemManager.getData(ItemManager.getMaterialData(CivData.STONE, CivData.ANDESITE))) {
//						try {
//							this.updateInventory(Action.REMOVE, source_inv, ItemManager.createItemStack(CivData.STONE, 1, (short) CivData.ANDESITE));
//						} catch (InterruptedException e) {
//							return;
//						}
//						
//						Random rand = new Random();
//						int randMax = Trommel.ANDESITE_MAX_RATE;
//						int rand1 = rand.nextInt(randMax);
//						ItemStack newItem;
//						if (rand1 < ((int)((trommel.getAndesiteChance(Mineral.EMERALD))*randMax))) {
//							newItem = ItemManager.createItemStack(CivData.EMERALD, 1);
//						} else if (rand1 < ((int)((trommel.getAndesiteChance(Mineral.DIAMOND))*randMax))) {
//							newItem = ItemManager.createItemStack(CivData.DIAMOND, 1);
//						} else if (rand1 < ((int)((trommel.getAndesiteChance(Mineral.REDSTONE))*randMax))) {
//						newItem = ItemManager.createItemStack(CivData.REDSTONE_DUST, 2);
//						} else if (rand1 < ((int)((trommel.getAndesiteChance(Mineral.GOLD))*randMax))) {
//							newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, 1);
//						} else if (rand1 < ((int)((trommel.getAndesiteChance(Mineral.IRON))*randMax))) {
//							newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
//						} else if (rand1 < ((int)((trommel.getAndesiteChance(Mineral.BRONZE_ORE))*randMax))) {
//							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:bronze_ore"));
//						} else if (rand1 < ((int)((trommel.getAndesiteChance(Mineral.STEEL_ORE))*randMax))) {
//							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:steel_ore"));
//						} else if (rand1 < ((int)((trommel.getAndesiteChance(Mineral.TITANIUM_ORE))*randMax))) {
//							newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:titanium_ore"));
//						} else {
//							newItem = ItemManager.createItemStack(CivData.GRAVEL, (Integer)GRAVEL_RATE);
//						}
//						try {
//							debug(trommel, "Updating inventory:"+newItem);
//							this.updateInventory(Action.ADD, dest_inv, newItem);
//						} catch (InterruptedException e) {
//							return;
//						}
//						break;
//					}
//				}
			}	
		}
		trommel.skippedCounter = 0;
	}
	
	@Override
	public void run() {
		if (this.trommel.lock.tryLock()) {
			try {
				try {
					processTrommelUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} finally {
				this.trommel.lock.unlock();
			}
		} else {
			debug(this.trommel, "Failed to get lock while trying to start task, aborting.");
		}
	}
}
