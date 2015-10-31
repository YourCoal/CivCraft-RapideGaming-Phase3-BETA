package com.avrgaming.civcraft.threading.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.avrgaming.civcraft.exception.CivTaskAbortException;
import com.avrgaming.civcraft.lorestorage.LoreMaterial;
import com.avrgaming.civcraft.main.CivData;
import com.avrgaming.civcraft.main.CivLog;
import com.avrgaming.civcraft.object.StructureChest;
import com.avrgaming.civcraft.structure.Structure;
import com.avrgaming.civcraft.structure.Shipyard;
import com.avrgaming.civcraft.structure.Shipyard.ShipyardStock;
import com.avrgaming.civcraft.threading.CivAsyncTask;
import com.avrgaming.civcraft.threading.sync.request.UpdateInventoryRequest.Action;
import com.avrgaming.civcraft.util.ItemManager;
import com.avrgaming.civcraft.util.MultiInventory;

public class ShipyardAsyncTask extends CivAsyncTask {
	
	Shipyard sy;
	
	public static HashSet<String> debugTowns = new HashSet<String>();
	public static void debug(Shipyard mobGrinder, String msg) {
		if (debugTowns.contains(mobGrinder.getTown().getName())) {
			CivLog.warning("ShipyardDebug:"+mobGrinder.getTown().getName()+":"+msg);
		}
	}	
	
	public ShipyardAsyncTask(Structure shipyard) {
		this.sy = (Shipyard)shipyard;
	}
	
	public void processShipyardUpdate() {
		if (!sy.isActive()) {
			debug(sy, "Shipyard inactive...");
			return;
		}
		
		debug(sy, "Processing Shipyard...");
		// Grab each CivChest object we'll require.
		ArrayList<StructureChest> sources = sy.getAllChestsById(0);
		ArrayList<StructureChest> destinations = sy.getAllChestsById(1);
		
		if (sources.size() != 2 || destinations.size() != 2) {
			CivLog.error("Bad chests for Shipyard in town:"+sy.getTown().getName()+" sources:"+sources.size()+" dests:"+destinations.size());
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
					CivLog.warning("Shipyard:"+e.getMessage());
					return;
				}
				if (tmp == null) {
					sy.skippedCounter++;
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
					CivLog.warning("Shipyard:"+e.getMessage());
					return;
				}
				if (tmp == null) {
					sy.skippedCounter++;
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
				return;
			}
		} catch (InterruptedException e) {
			return;
		}
		
		debug(sy, "Processing mobGrinder:"+sy.skippedCounter+1);
		ItemStack[] contents = source_inv.getContents();
		for (int i = 0; i < sy.skippedCounter+1; i++) {
		
			for(ItemStack stack : contents) {
				if (stack == null) {
					continue;
				}
				
				String itemID = LoreMaterial.getMaterial(stack).getId();
				try {
					ItemStack newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get(itemID));
					this.updateInventory(Action.REMOVE, source_inv, newItem);
				} catch (InterruptedException e) {
					return;
				}

				Random rand = new Random();
				int rand1 = rand.nextInt(10000);
				ArrayList<ItemStack> newItems = new ArrayList<ItemStack>();
				if (itemID.contains("civ:tier1_export_crate")) {	
					if (rand1 < ((int)((sy.getImportSize(ShipyardStock.MEGA))))) {
						//newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish_data")));
						newItems.add(ItemManager.createItemStack(CivData.FISH, 256));
					} else if (rand1 < ((int)((sy.getImportSize(ShipyardStock.LARGE))))) {
						//newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish_data")));
						newItems.add(ItemManager.createItemStack(CivData.FISH, 128));
					} else if (rand1 < ((int)((sy.getImportSize(ShipyardStock.MEDIUM))))) {
						//newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish_data")));
						newItems.add(ItemManager.createItemStack(CivData.FISH, 64));
					} else if (rand1 < ((int)((sy.getImportSize(ShipyardStock.SMALL))))) {
						//newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish_data")));
						newItems.add(ItemManager.createItemStack(CivData.FISH, 32));
					} else {
						//newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish_data")));
						newItems.add(ItemManager.createItemStack(CivData.FISH, 16));
					}
					
					//Try to add the new item to the dest chest, if we cant, oh well.
					try {
						for (ItemStack item : newItems)
						{
							debug(sy, "Updating inventory:"+item);
							this.updateInventory(Action.ADD, dest_inv, item);
						}
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
				
				else if (itemID.contains("civ:tier2_export_crate")) {	
					if (rand1 < ((int)((sy.getImportSize(ShipyardStock.MEGA))*4))) {
						//newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish_data")));
						newItems.add(ItemManager.createItemStack(CivData.FISH, 256));
					} else if (rand1 < ((int)((sy.getImportSize(ShipyardStock.LARGE))*4))) {
						//newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish_data")));
						newItems.add(ItemManager.createItemStack(CivData.FISH, 128));
					} else if (rand1 < ((int)((sy.getImportSize(ShipyardStock.MEDIUM))*4))) {
						//newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish_data")));
						newItems.add(ItemManager.createItemStack(CivData.FISH, 64));
					} else if (rand1 < ((int)((sy.getImportSize(ShipyardStock.SMALL))*4))) {
						//newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish_data")));
						newItems.add(ItemManager.createItemStack(CivData.FISH, 32));
					} else {
						//newItems.add(LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish_data")));
						newItems.add(ItemManager.createItemStack(CivData.FISH, 16));
					}
					
					//Try to add the new item to the dest chest, if we cant, oh well.
					try {
						for (ItemStack item : newItems)
						{
							debug(sy, "Updating inventory:"+item);
							this.updateInventory(Action.ADD, dest_inv, item);
						}
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}	
		}
		sy.skippedCounter = 0;
	}
	
	@Override
	public void run() {
		if (this.sy.lock.tryLock()) {
			try {
				try {
					processShipyardUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} finally {
				this.sy.lock.unlock();
			}
		} else {
			debug(this.sy, "Failed to get lock while trying to start task, aborting.");
		}
	}
}