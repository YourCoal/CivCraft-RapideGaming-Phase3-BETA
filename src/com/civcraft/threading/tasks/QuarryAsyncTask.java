package com.civcraft.threading.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.civcraft.exception.CivTaskAbortException;
import com.civcraft.main.CivData;
import com.civcraft.main.CivLog;
import com.civcraft.object.StructureChest;
import com.civcraft.structure.Quarry;
import com.civcraft.structure.Quarry.Ores;
import com.civcraft.structure.Structure;
import com.civcraft.threading.CivAsyncTask;
import com.civcraft.threading.sync.request.UpdateInventoryRequest.Action;
import com.civcraft.util.ItemManager;
import com.civcraft.util.MultiInventory;

public class QuarryAsyncTask extends CivAsyncTask {

	Quarry quarry;
	
	public static HashSet<String> debugTowns = new HashSet<String>();

	public static void debug(Quarry quarry, String msg) {
		if (debugTowns.contains(quarry.getTown().getName())) {
			CivLog.warning("QuarryDebug:"+quarry.getTown().getName()+":"+msg);
		}
	}	
	
	public QuarryAsyncTask(Structure fishHatchery) {
		this.quarry = (Quarry)fishHatchery;
	}
	
	public void processFisheryUpdate() {
		if (!quarry.isActive()) {
			debug(quarry, "Quarry inactive...");
			return;
		}
		
		debug(quarry, "Processing Quarry...");
		// Grab each CivChest object we'll require.
		ArrayList<StructureChest> sources = quarry.getAllChestsById(0);
		sources.addAll(quarry.getAllChestsById(1));
		sources.addAll(quarry.getAllChestsById(2));
		sources.addAll(quarry.getAllChestsById(3));
		ArrayList<StructureChest> destinations = quarry.getAllChestsById(4);
		
		if (sources.size() != 4 || destinations.size() != 2) {
			CivLog.error("Bad chests for quarry in town:"+quarry.getTown().getName()+" sources:"+sources.size()+" dests:"+destinations.size());
			return;
		}
		
		// Make sure the chunk is loaded before continuing. Also, add get chest and add it to inventory.
		MultiInventory source_inv0 = new MultiInventory();
		MultiInventory source_inv1 = new MultiInventory();
		MultiInventory source_inv2 = new MultiInventory();
		MultiInventory source_inv3 = new MultiInventory();
		MultiInventory dest_inv = new MultiInventory();

		try {
			for (StructureChest src : sources) {			
				Inventory tmp;
				try {
					tmp = this.getChestInventory(src.getCoord().getWorldname(), src.getCoord().getX(), src.getCoord().getY(), src.getCoord().getZ(), false);
				} catch (CivTaskAbortException e) {
					CivLog.warning("Quarry:"+e.getMessage());
					return;
				}
				if (tmp == null) {
					quarry.skippedCounter++;
					return;
				}
				switch(src.getChestId()){
				case 0: source_inv0.addInventory(tmp);
			 	break;
				case 1: source_inv1.addInventory(tmp);
			 	break;
				case 2: source_inv2.addInventory(tmp);
			 	break;
				case 3: source_inv3.addInventory(tmp);
			 	break;
				}
			}
			
			boolean full = true;
			for (StructureChest dst : destinations) {
				Inventory tmp;
				try {
					tmp = this.getChestInventory(dst.getCoord().getWorldname(), dst.getCoord().getX(), dst.getCoord().getY(), dst.getCoord().getZ(), false);
				} catch (CivTaskAbortException e) {
					CivLog.warning("Quarry:"+e.getMessage());
					return;
				}
				if (tmp == null) {
					quarry.skippedCounter++;
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
				/* Quarry destination chest is full, stop processing. */
				return;
			}
		} catch (InterruptedException e) {
			return;
		}
		debug(quarry, "Processing Quarry:"+quarry.skippedCounter+1);
		ItemStack[] contents0 = source_inv0.getContents();
		ItemStack[] contents1 = source_inv1.getContents();
		ItemStack[] contents2 = source_inv2.getContents();
		ItemStack[] contents3 = source_inv3.getContents();
		for (int i = 0; i < quarry.skippedCounter+1; i++) {
			
			//XXX Chest 1
			for (ItemStack stack : contents0) {
				if (stack == null) {
					continue;
				}
				
				if (ItemManager.getId(stack) == CivData.WOOD_PICKAXE) {
					try {
						short damage = ItemManager.getData(stack);
						this.updateInventory(Action.REMOVE, source_inv0, ItemManager.createItemStack(CivData.WOOD_PICKAXE, 1, damage));
						damage++;
						if (damage < 59) {
						this.updateInventory(Action.ADD, source_inv0, ItemManager.createItemStack(CivData.WOOD_PICKAXE, 1, damage));
						}
					} catch (InterruptedException e) {
						return;
					}
					
					Random rand = new Random();
					int randMax = Quarry.MAX;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((quarry.getChance(Ores.IRON))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COAL)*2)*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COAL, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COBBLESTONE))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, 1);
					} else {
						newItem = getJunk();
					}
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
			for (ItemStack stack : contents0) {
				if (stack == null) {
					continue;
				}
				
				if (this.quarry.getLevel() >= 2 && ItemManager.getId(stack) == CivData.STONE_PICKAXE) {
					try {
						short damage = ItemManager.getData(stack);
						this.updateInventory(Action.REMOVE, source_inv0, ItemManager.createItemStack(CivData.STONE_PICKAXE, 1, damage));
						damage++;
						if (damage < 131) {
						this.updateInventory(Action.ADD, source_inv0, ItemManager.createItemStack(CivData.STONE_PICKAXE, 1, damage));
						}
					} catch (InterruptedException e) {
						return;
					}
					
					Random rand = new Random();
					int randMax = Quarry.MAX;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((quarry.getChance(Ores.GOLD)/2)*randMax))) {
					newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, 1);
					}else if (rand1 < ((int)((quarry.getChance(Ores.IRON)*2)*randMax))) {
					newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COAL))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COAL, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COBBLESTONE))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, 1);
					} else {
						newItem = getJunk();
					}
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
			for (ItemStack stack : contents0) {
				if (stack == null) {
					continue;
				}
				
				if (this.quarry.getLevel() >= 3 && ItemManager.getId(stack) == CivData.IRON_PICKAXE) {
					try {
						short damage = ItemManager.getData(stack);
						this.updateInventory(Action.REMOVE, source_inv0, ItemManager.createItemStack(CivData.IRON_PICKAXE, 1, damage));
						damage++;
						if (damage < 250) {
						this.updateInventory(Action.ADD, source_inv0, ItemManager.createItemStack(CivData.IRON_PICKAXE, 1, damage));
						}
					} catch (InterruptedException e) {
						return;
					}
					
					Random rand = new Random();
					int randMax = Quarry.MAX;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((quarry.getChance(Ores.REDSTONE))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.REDSTONE_DUST, 2);
					} else if (rand1 < ((int)((quarry.getChance(Ores.GOLD))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, 1);
					}else if (rand1 < ((int)((quarry.getChance(Ores.IRON))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COBBLESTONE))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, 1);
					} else {
						newItem = getJunk();
					}
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
			for (ItemStack stack : contents0) {
				if (stack == null) {
					continue;
				}
				
				if (ItemManager.getId(stack) == CivData.GOLD_PICKAXE) {
					try {
						short damage = ItemManager.getData(stack);
						this.updateInventory(Action.REMOVE, source_inv0, ItemManager.createItemStack(CivData.GOLD_PICKAXE, 1, damage));
						damage++;
						if (damage < 32) {
						this.updateInventory(Action.ADD, source_inv0, ItemManager.createItemStack(CivData.GOLD_PICKAXE, 1, damage));
						}
					} catch (InterruptedException e) {
						return;
					}
					
					Random rand = new Random();
					int randMax = Quarry.MAX;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((quarry.getChance(Ores.REDSTONE))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.REDSTONE_DUST, 4);
					}else if (rand1 < ((int)((quarry.getChance(Ores.IRON)/2)*randMax))) {
					newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COBBLESTONE)*4)*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, 1);
					} else {
						newItem = getJunk();
					}
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
			for (ItemStack stack : contents0) {
				if (stack == null) {
					continue;
				}
				
				if (this.quarry.getLevel() >= 4 && ItemManager.getId(stack) == CivData.DIAMOND_PICKAXE) {
					try {
						short damage = ItemManager.getData(stack);
						this.updateInventory(Action.REMOVE, source_inv0, ItemManager.createItemStack(CivData.DIAMOND_PICKAXE, 1, damage));
						damage++;
						if (damage < 1561) {
						this.updateInventory(Action.ADD, source_inv0, ItemManager.createItemStack(CivData.DIAMOND_PICKAXE, 1, damage));
						}
					} catch (InterruptedException e) {
						return;
					}
					
					Random rand = new Random();
					int randMax = Quarry.MAX;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((quarry.getChance(Ores.DIAMOND))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.DIAMOND, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.REDSTONE))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.REDSTONE_DUST, 2);
					} else if (rand1 < ((int)((quarry.getChance(Ores.GOLD)*2)*randMax))) {
					newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COBBLESTONE))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, 1);
					} else {
						newItem = getJunk();
					}
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
			
			//XXX Chest 2
			for (ItemStack stack : contents1) {
				if (stack == null) {
					continue;
				}
				
				if (this.quarry.getLevel() >= 2 && ItemManager.getId(stack) == CivData.WOOD_PICKAXE) {
					try {
						short damage = ItemManager.getData(stack);
						this.updateInventory(Action.REMOVE, source_inv1, ItemManager.createItemStack(CivData.WOOD_PICKAXE, 1, damage));
						damage++;
						if (damage < 59) {
						this.updateInventory(Action.ADD, source_inv1, ItemManager.createItemStack(CivData.WOOD_PICKAXE, 1, damage));
						}
					} catch (InterruptedException e) {
						return;
					}
					
					Random rand = new Random();
					int randMax = Quarry.MAX;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((quarry.getChance(Ores.IRON))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COAL)*2)*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COAL, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COBBLESTONE))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, 1);
					} else {
						newItem = getJunk();
					}
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
			for (ItemStack stack : contents1) {
				if (stack == null) {
					continue;
				}
				
				if (this.quarry.getLevel() >= 2 && ItemManager.getId(stack) == CivData.STONE_PICKAXE) {
					try {
						short damage = ItemManager.getData(stack);
						this.updateInventory(Action.REMOVE, source_inv1, ItemManager.createItemStack(CivData.STONE_PICKAXE, 1, damage));
						damage++;
						if (damage < 131) {
						this.updateInventory(Action.ADD, source_inv1, ItemManager.createItemStack(CivData.STONE_PICKAXE, 1, damage));
						}
					} catch (InterruptedException e) {
						return;
					}
					
					Random rand = new Random();
					int randMax = Quarry.MAX;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((quarry.getChance(Ores.GOLD)/2)*randMax))) {
					newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, 1);
					}else if (rand1 < ((int)((quarry.getChance(Ores.IRON)*2)*randMax))) {
					newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COAL))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COAL, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COBBLESTONE))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, 1);
					} else {
						newItem = getJunk();
					}
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
			for (ItemStack stack : contents1) {
				if (stack == null) {
					continue;
				}
				
				if (this.quarry.getLevel() >= 3 && ItemManager.getId(stack) == CivData.IRON_PICKAXE) {
					try {
						short damage = ItemManager.getData(stack);
						this.updateInventory(Action.REMOVE, source_inv1, ItemManager.createItemStack(CivData.IRON_PICKAXE, 1, damage));
						damage++;
						if (damage < 250) {
						this.updateInventory(Action.ADD, source_inv1, ItemManager.createItemStack(CivData.IRON_PICKAXE, 1, damage));
						}
					} catch (InterruptedException e) {
						return;
					}
					
					Random rand = new Random();
					int randMax = Quarry.MAX;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((quarry.getChance(Ores.REDSTONE))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.REDSTONE_DUST, 2);
					} else if (rand1 < ((int)((quarry.getChance(Ores.GOLD))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, 1);
					}else if (rand1 < ((int)((quarry.getChance(Ores.IRON))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COBBLESTONE))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, 1);
					} else {
						newItem = getJunk();
					}
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
			for (ItemStack stack : contents1) {
				if (stack == null) {
					continue;
				}
				
				if (this.quarry.getLevel() >= 2 && ItemManager.getId(stack) == CivData.GOLD_PICKAXE) {
					try {
						short damage = ItemManager.getData(stack);
						this.updateInventory(Action.REMOVE, source_inv1, ItemManager.createItemStack(CivData.GOLD_PICKAXE, 1, damage));
						damage++;
						if (damage < 32) {
						this.updateInventory(Action.ADD, source_inv1, ItemManager.createItemStack(CivData.GOLD_PICKAXE, 1, damage));
						}
					} catch (InterruptedException e) {
						return;
					}
					
					Random rand = new Random();
					int randMax = Quarry.MAX;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((quarry.getChance(Ores.REDSTONE))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.REDSTONE_DUST, 4);
					}else if (rand1 < ((int)((quarry.getChance(Ores.IRON)/2)*randMax))) {
					newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COBBLESTONE)*4)*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, 1);
					} else {
						newItem = getJunk();
					}
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
			for (ItemStack stack : contents1) {
				if (stack == null) {
					continue;
				}
				
				if (this.quarry.getLevel() >= 4 && ItemManager.getId(stack) == CivData.DIAMOND_PICKAXE) {
					try {
						short damage = ItemManager.getData(stack);
						this.updateInventory(Action.REMOVE, source_inv1, ItemManager.createItemStack(CivData.DIAMOND_PICKAXE, 1, damage));
						damage++;
						if (damage < 1561) {
						this.updateInventory(Action.ADD, source_inv1, ItemManager.createItemStack(CivData.DIAMOND_PICKAXE, 1, damage));
						}
					} catch (InterruptedException e) {
						return;
					}
					
					Random rand = new Random();
					int randMax = Quarry.MAX;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((quarry.getChance(Ores.DIAMOND))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.DIAMOND, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.REDSTONE))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.REDSTONE_DUST, 2);
					} else if (rand1 < ((int)((quarry.getChance(Ores.GOLD)*2)*randMax))) {
					newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COBBLESTONE))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, 1);
					} else {
						newItem = getJunk();
					}
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
			
			//XXX Chest 3
			for (ItemStack stack : contents2) {
				if (stack == null) {
					continue;
				}
				
				if (this.quarry.getLevel() >= 3 && ItemManager.getId(stack) == CivData.WOOD_PICKAXE) {
					try {
						short damage = ItemManager.getData(stack);
						this.updateInventory(Action.REMOVE, source_inv2, ItemManager.createItemStack(CivData.WOOD_PICKAXE, 1, damage));
						damage++;
						if (damage < 59) {
						this.updateInventory(Action.ADD, source_inv2, ItemManager.createItemStack(CivData.WOOD_PICKAXE, 1, damage));
						}
					} catch (InterruptedException e) {
						return;
					}
					
					Random rand = new Random();
					int randMax = Quarry.MAX;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((quarry.getChance(Ores.IRON))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COAL)*2)*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COAL, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COBBLESTONE))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, 1);
					} else {
						newItem = getJunk();
					}
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
			for (ItemStack stack : contents2) {
				if (stack == null) {
					continue;
				}
				
				if (this.quarry.getLevel() >= 3 && ItemManager.getId(stack) == CivData.STONE_PICKAXE) {
					try {
						short damage = ItemManager.getData(stack);
						this.updateInventory(Action.REMOVE, source_inv2, ItemManager.createItemStack(CivData.STONE_PICKAXE, 1, damage));
						damage++;
						if (damage < 131) {
						this.updateInventory(Action.ADD, source_inv2, ItemManager.createItemStack(CivData.STONE_PICKAXE, 1, damage));
						}
					} catch (InterruptedException e) {
						return;
					}
					
					Random rand = new Random();
					int randMax = Quarry.MAX;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((quarry.getChance(Ores.GOLD)/2)*randMax))) {
					newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, 1);
					}else if (rand1 < ((int)((quarry.getChance(Ores.IRON)*2)*randMax))) {
					newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COAL))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COAL, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COBBLESTONE))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, 1);
					} else {
						newItem = getJunk();
					}
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
			for (ItemStack stack : contents2) {
				if (stack == null) {
					continue;
				}
				
				if (this.quarry.getLevel() >= 3 && ItemManager.getId(stack) == CivData.IRON_PICKAXE) {
					try {
						short damage = ItemManager.getData(stack);
						this.updateInventory(Action.REMOVE, source_inv2, ItemManager.createItemStack(CivData.IRON_PICKAXE, 1, damage));
						damage++;
						if (damage < 250) {
						this.updateInventory(Action.ADD, source_inv2, ItemManager.createItemStack(CivData.IRON_PICKAXE, 1, damage));
						}
					} catch (InterruptedException e) {
						return;
					}
					
					Random rand = new Random();
					int randMax = Quarry.MAX;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((quarry.getChance(Ores.REDSTONE))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.REDSTONE_DUST, 2);
					} else if (rand1 < ((int)((quarry.getChance(Ores.GOLD))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, 1);
					}else if (rand1 < ((int)((quarry.getChance(Ores.IRON))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COBBLESTONE))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, 1);
					} else {
						newItem = getJunk();
					}
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
			for (ItemStack stack : contents2) {
				if (stack == null) {
					continue;
				}
				
				if (this.quarry.getLevel() >= 3 && ItemManager.getId(stack) == CivData.GOLD_PICKAXE) {
					try {
						short damage = ItemManager.getData(stack);
						this.updateInventory(Action.REMOVE, source_inv2, ItemManager.createItemStack(CivData.GOLD_PICKAXE, 1, damage));
						damage++;
						if (damage < 32) {
						this.updateInventory(Action.ADD, source_inv2, ItemManager.createItemStack(CivData.GOLD_PICKAXE, 1, damage));
						}
					} catch (InterruptedException e) {
						return;
					}
					
					Random rand = new Random();
					int randMax = Quarry.MAX;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((quarry.getChance(Ores.REDSTONE))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.REDSTONE_DUST, 4);
					}else if (rand1 < ((int)((quarry.getChance(Ores.IRON)/2)*randMax))) {
					newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COBBLESTONE)*4)*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, 1);
					} else {
						newItem = getJunk();
					}
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
			for (ItemStack stack : contents2) {
				if (stack == null) {
					continue;
				}
				
				if (this.quarry.getLevel() >= 4 && ItemManager.getId(stack) == CivData.DIAMOND_PICKAXE) {
					try {
						short damage = ItemManager.getData(stack);
						this.updateInventory(Action.REMOVE, source_inv2, ItemManager.createItemStack(CivData.DIAMOND_PICKAXE, 1, damage));
						damage++;
						if (damage < 1561) {
						this.updateInventory(Action.ADD, source_inv2, ItemManager.createItemStack(CivData.DIAMOND_PICKAXE, 1, damage));
						}
					} catch (InterruptedException e) {
						return;
					}
					
					Random rand = new Random();
					int randMax = Quarry.MAX;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((quarry.getChance(Ores.DIAMOND))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.DIAMOND, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.REDSTONE))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.REDSTONE_DUST, 2);
					} else if (rand1 < ((int)((quarry.getChance(Ores.GOLD)*2)*randMax))) {
					newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COBBLESTONE))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, 1);
					} else {
						newItem = getJunk();
					}
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
			
			//XXX Chest 4
			for (ItemStack stack : contents3) {
				if (stack == null) {
					continue;
				}
				
				if (this.quarry.getLevel() >= 4 && ItemManager.getId(stack) == CivData.WOOD_PICKAXE) {
					try {
						short damage = ItemManager.getData(stack);
						this.updateInventory(Action.REMOVE, source_inv3, ItemManager.createItemStack(CivData.WOOD_PICKAXE, 1, damage));
						damage++;
						if (damage < 59) {
						this.updateInventory(Action.ADD, source_inv3, ItemManager.createItemStack(CivData.WOOD_PICKAXE, 1, damage));
						}
					} catch (InterruptedException e) {
						return;
					}
					
					Random rand = new Random();
					int randMax = Quarry.MAX;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((quarry.getChance(Ores.IRON))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COAL)*2)*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COAL, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COBBLESTONE))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, 1);
					} else {
						newItem = getJunk();
					}
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
			for (ItemStack stack : contents3) {
				if (stack == null) {
					continue;
				}
				
				if (this.quarry.getLevel() >= 4 && ItemManager.getId(stack) == CivData.STONE_PICKAXE) {
					try {
						short damage = ItemManager.getData(stack);
						this.updateInventory(Action.REMOVE, source_inv3, ItemManager.createItemStack(CivData.STONE_PICKAXE, 1, damage));
						damage++;
						if (damage < 131) {
						this.updateInventory(Action.ADD, source_inv3, ItemManager.createItemStack(CivData.STONE_PICKAXE, 1, damage));
						}
					} catch (InterruptedException e) {
						return;
					}
					
					Random rand = new Random();
					int randMax = Quarry.MAX;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((quarry.getChance(Ores.GOLD)/2)*randMax))) {
					newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, 1);
					}else if (rand1 < ((int)((quarry.getChance(Ores.IRON)*2)*randMax))) {
					newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COAL))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COAL, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COBBLESTONE))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, 1);
					} else {
						newItem = getJunk();
					}
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
			for (ItemStack stack : contents3) {
				if (stack == null) {
					continue;
				}
				
				if (this.quarry.getLevel() >= 4 && ItemManager.getId(stack) == CivData.IRON_PICKAXE) {
					try {
						short damage = ItemManager.getData(stack);
						this.updateInventory(Action.REMOVE, source_inv3, ItemManager.createItemStack(CivData.IRON_PICKAXE, 1, damage));
						damage++;
						if (damage < 250) {
						this.updateInventory(Action.ADD, source_inv3, ItemManager.createItemStack(CivData.IRON_PICKAXE, 1, damage));
						}
					} catch (InterruptedException e) {
						return;
					}
					
					Random rand = new Random();
					int randMax = Quarry.MAX;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((quarry.getChance(Ores.REDSTONE))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.REDSTONE_DUST, 2);
					} else if (rand1 < ((int)((quarry.getChance(Ores.GOLD))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, 1);
					}else if (rand1 < ((int)((quarry.getChance(Ores.IRON))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COBBLESTONE))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, 1);
					} else {
						newItem = getJunk();
					}
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
			for (ItemStack stack : contents3) {
				if (stack == null) {
					continue;
				}
				
				if (this.quarry.getLevel() >= 4 && ItemManager.getId(stack) == CivData.GOLD_PICKAXE) {
					try {
						short damage = ItemManager.getData(stack);
						this.updateInventory(Action.REMOVE, source_inv3, ItemManager.createItemStack(CivData.GOLD_PICKAXE, 1, damage));
						damage++;
						if (damage < 32) {
						this.updateInventory(Action.ADD, source_inv3, ItemManager.createItemStack(CivData.GOLD_PICKAXE, 1, damage));
						}
					} catch (InterruptedException e) {
						return;
					}
					
					Random rand = new Random();
					int randMax = Quarry.MAX;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((quarry.getChance(Ores.REDSTONE))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.REDSTONE_DUST, 4);
					}else if (rand1 < ((int)((quarry.getChance(Ores.IRON)/2)*randMax))) {
					newItem = ItemManager.createItemStack(CivData.IRON_INGOT, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COBBLESTONE)*4)*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, 1);
					} else {
						newItem = getJunk();
					}
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
			for (ItemStack stack : contents3) {
				if (stack == null) {
					continue;
				}
				
				if (this.quarry.getLevel() >= 4 && ItemManager.getId(stack) == CivData.DIAMOND_PICKAXE) {
					try {
						short damage = ItemManager.getData(stack);
						this.updateInventory(Action.REMOVE, source_inv3, ItemManager.createItemStack(CivData.DIAMOND_PICKAXE, 1, damage));
						damage++;
						if (damage < 1561) {
						this.updateInventory(Action.ADD, source_inv3, ItemManager.createItemStack(CivData.DIAMOND_PICKAXE, 1, damage));
						}
					} catch (InterruptedException e) {
						return;
					}
					
					Random rand = new Random();
					int randMax = Quarry.MAX;
					int rand1 = rand.nextInt(randMax);
					ItemStack newItem;
					if (rand1 < ((int)((quarry.getChance(Ores.DIAMOND))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.DIAMOND, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.REDSTONE))*randMax))) {
					newItem = ItemManager.createItemStack(CivData.REDSTONE_DUST, 2);
					} else if (rand1 < ((int)((quarry.getChance(Ores.GOLD)*2)*randMax))) {
					newItem = ItemManager.createItemStack(CivData.GOLD_INGOT, 1);
					} else if (rand1 < ((int)((quarry.getChance(Ores.COBBLESTONE))*randMax))) {
						newItem = ItemManager.createItemStack(CivData.COBBLESTONE, 1);
					} else {
						newItem = getJunk();
					}
					try {
						debug(quarry, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
		}	
	quarry.skippedCounter = 0;
	}
	
	private ItemStack getJunk() {
		int randMax = 10;
		Random rand = new Random();
		int rand2 = rand.nextInt(randMax);
		if (rand2 < (2)) {
			return ItemManager.createItemStack(CivData.DIRT, 1); //, (short) CivData.PODZOL);
//		} else if (rand2 < (5)) {
//			return ItemManager.createItemStack(CivData.DIRT, 1, (short) CivData.COARSE_DIRT);
		} else {
			return ItemManager.createItemStack(CivData.DIRT, 1);
		}
	}
	
//	private ItemStack getOther() {
//		int randMax = Quarry.MAX_CHANCE;
//		Random rand = new Random();
//		int rand2 = rand.nextInt(randMax);
//		if (rand2 < (randMax/8)) {
//			return ItemManager.createItemStack(CivData.STONE, 1, (short) CivData.ANDESITE);
//		} else if (rand2 < (randMax/5)) {
//			return ItemManager.createItemStack(CivData.STONE, 1, (short) CivData.DIORITE);
//		} else {
//			return ItemManager.createItemStack(CivData.STONE, 1, (short) CivData.GRANITE);
//		}
//	}
	
	@Override
	public void run() {
		if (this.quarry.lock.tryLock()) {
			try {
				try {
					processFisheryUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} finally {
				this.quarry.lock.unlock();
			}
		} else {
			debug(this.quarry, "Failed to get lock while trying to start task, aborting.");
		}
	}
}