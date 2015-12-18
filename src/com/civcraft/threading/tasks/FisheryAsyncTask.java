package com.civcraft.threading.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.block.Biome;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.civcraft.exception.CivTaskAbortException;
import com.civcraft.lorestorage.LoreMaterial;
import com.civcraft.main.CivData;
import com.civcraft.main.CivLog;
import com.civcraft.object.StructureChest;
import com.civcraft.structure.Fishery;
import com.civcraft.structure.Structure;
import com.civcraft.threading.CivAsyncTask;
import com.civcraft.threading.sync.request.UpdateInventoryRequest.Action;
import com.civcraft.util.ItemManager;
import com.civcraft.util.MultiInventory;

public class FisheryAsyncTask extends CivAsyncTask {

	Fishery fishHatchery;
	
	public static HashSet<String> debugTowns = new HashSet<String>();

	public static void debug(Fishery fishHatchery, String msg) {
		if (debugTowns.contains(fishHatchery.getTown().getName())) {
			CivLog.warning("FisheryDebug:"+fishHatchery.getTown().getName()+":"+msg);
		}
	}	
	
	public FisheryAsyncTask(Structure fishHatchery) {
		this.fishHatchery = (Fishery)fishHatchery;
	}
	
	public void processFisheryUpdate() {
		if (!fishHatchery.isActive()) {
			debug(fishHatchery, "Fishery inactive...");
			return;
		}
		
		debug(fishHatchery, "Processing Fishery...");
		ArrayList<StructureChest> sources = fishHatchery.getAllChestsById(0);
		sources.addAll(fishHatchery.getAllChestsById(1));
		sources.addAll(fishHatchery.getAllChestsById(2));
		sources.addAll(fishHatchery.getAllChestsById(3));
		ArrayList<StructureChest> destinations = fishHatchery.getAllChestsById(4);
		if (sources.size() != 4 || destinations.size() != 2) {
			CivLog.error("Bad chests for fish ery in town:"+fishHatchery.getTown().getName()+" sources:"+sources.size()+" dests:"+destinations.size());
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
				//this.syncLoadChunk(src.getCoord().getWorldname(), src.getCoord().getX(), src.getCoord().getZ());				
				Inventory tmp;
				try {
					tmp = this.getChestInventory(src.getCoord().getWorldname(), src.getCoord().getX(), src.getCoord().getY(), src.getCoord().getZ(), false);
				} catch (CivTaskAbortException e) {
					//e.printStackTrace();
					CivLog.warning("Fish ery:"+e.getMessage());
					return;
				}
				
				if (tmp == null) {
					fishHatchery.skippedCounter++;
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
				//this.syncLoadChunk(dst.getCoord().getWorldname(), dst.getCoord().getX(), dst.getCoord().getZ());
				Inventory tmp;
				try {
					tmp = this.getChestInventory(dst.getCoord().getWorldname(), dst.getCoord().getX(), dst.getCoord().getY(), dst.getCoord().getZ(), false);
				} catch (CivTaskAbortException e) {
					//e.printStackTrace();
					CivLog.warning("Fish ery:"+e.getMessage());
					return;
				}
				
				if (tmp == null) {
					fishHatchery.skippedCounter++;
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
		
		debug(fishHatchery, "Processing Fish ery:"+fishHatchery.skippedCounter+1);
		ItemStack[] contents0 = source_inv0.getContents();
		ItemStack[] contents1 = source_inv1.getContents();
		ItemStack[] contents2 = source_inv2.getContents();
		ItemStack[] contents3 = source_inv3.getContents();
		for (int i = 0; i < fishHatchery.skippedCounter+1; i++) {
			for(ItemStack stack : contents0) {
				if (stack == null) {
					continue;
				}
				
				if (ItemManager.getId(stack) == CivData.FISHING_ROD) {
					try {
						short damage = ItemManager.getData(stack);
						this.updateInventory(Action.REMOVE, source_inv0, ItemManager.createItemStack(CivData.FISHING_ROD, 1, damage));
						damage++;
						if (damage < 64) {
						this.updateInventory(Action.ADD, source_inv0, ItemManager.createItemStack(CivData.FISHING_ROD, 1, damage));
						}
					} catch (InterruptedException e) {
						return;
					}
					
					ItemStack newItem;
					newItem = this.getFishForBiome();
					try {
						debug(fishHatchery, "Updating inventory:"+newItem);
						this.updateInventory(Action.ADD, dest_inv, newItem);
					} catch (InterruptedException e) {
						return;
					}
					break;
				}
			}
			
			if (this.fishHatchery.getLevel() >= 2) {
				for (ItemStack stack : contents1) {
					if (stack == null) {
						continue;
					}
					
					if (ItemManager.getId(stack) == CivData.FISHING_ROD) {
						try {
							short damage = ItemManager.getData(stack);
							this.updateInventory(Action.REMOVE, source_inv1, ItemManager.createItemStack(CivData.FISHING_ROD, 1, damage));
							damage++;
							if (damage < 64) {
							this.updateInventory(Action.ADD, source_inv1, ItemManager.createItemStack(CivData.FISHING_ROD, 1, damage));
							}
						} catch (InterruptedException e) {
							return;
						}
						
						ItemStack newItem;
						newItem = this.getFishForBiome();
						try {
							debug(fishHatchery, "Updating inventory:"+newItem);
							this.updateInventory(Action.ADD, dest_inv, newItem);
						} catch (InterruptedException e) {
							return;
						}
						break;
					}
				}
			}
			
			if (this.fishHatchery.getLevel() >= 3) {
				for (ItemStack stack : contents2) {
					if (stack == null) {
						continue;
					}
					
					if (ItemManager.getId(stack) == CivData.FISHING_ROD) {
						try {
							short damage = ItemManager.getData(stack);
							this.updateInventory(Action.REMOVE, source_inv2, ItemManager.createItemStack(CivData.FISHING_ROD, 1, damage));
							damage++;
							if (damage < 64) {
							this.updateInventory(Action.ADD, source_inv2, ItemManager.createItemStack(CivData.FISHING_ROD, 1, damage));
							}
						} catch (InterruptedException e) {
							return;
						}
						
						ItemStack newItem;
						newItem = this.getFishForBiome();
						try {
							debug(fishHatchery, "Updating inventory:"+newItem);
							this.updateInventory(Action.ADD, dest_inv, newItem);
						} catch (InterruptedException e) {
							return;
						}
						break;
					}
				}
			}
			
			if (this.fishHatchery.getLevel() >= 4) {
				for (ItemStack stack : contents3) {
					if (stack == null) {
						continue;
					}
					
					if (ItemManager.getId(stack) == CivData.FISHING_ROD) {
						try {
							short damage = ItemManager.getData(stack);
							this.updateInventory(Action.REMOVE, source_inv3, ItemManager.createItemStack(CivData.FISHING_ROD, 1, damage));
							damage++;
							if (damage < 64) {
							this.updateInventory(Action.ADD, source_inv3, ItemManager.createItemStack(CivData.FISHING_ROD, 1, damage));
							}
						} catch (InterruptedException e) {
							return;
						}
						
						ItemStack newItem;
						newItem = this.getFishForBiome();
						try {
							debug(fishHatchery, "Updating inventory:"+newItem);
							this.updateInventory(Action.ADD, dest_inv, newItem);
						} catch (InterruptedException e) {
							return;
						}
						break;
					}
				}
			}
		}	
		fishHatchery.skippedCounter = 0;
	}
	
	private int getBiome() {
		Biome biome = this.fishHatchery.getCorner().getBlock().getBiome();
		if (biome.equals(Biome.BIRCH_FOREST_HILLS) ||
			biome.equals(Biome.BIRCH_FOREST_MOUNTAINS) ||
			biome.equals(Biome.BIRCH_FOREST_HILLS_MOUNTAINS) ||
			biome.equals(Biome.COLD_TAIGA_HILLS) ||
			biome.equals(Biome.COLD_TAIGA_MOUNTAINS) ||
			biome.equals(Biome.EXTREME_HILLS_MOUNTAINS) ||
			biome.equals(Biome.EXTREME_HILLS_PLUS) ||
			biome.equals(Biome.EXTREME_HILLS_PLUS_MOUNTAINS) ||
			biome.equals(Biome.ICE_MOUNTAINS) ||
			biome.equals(Biome.JUNGLE_EDGE_MOUNTAINS) ||
			biome.equals(Biome.JUNGLE_HILLS) ||
			biome.equals(Biome.JUNGLE_MOUNTAINS) ||
			biome.equals(Biome.MESA_PLATEAU_FOREST_MOUNTAINS) ||
			biome.equals(Biome.MESA_PLATEAU_MOUNTAINS) ||
			biome.equals(Biome.ROOFED_FOREST_MOUNTAINS) ||
			biome.equals(Biome.SAVANNA_MOUNTAINS) ||
			biome.equals(Biome.SAVANNA_PLATEAU_MOUNTAINS) ||
			biome.equals(Biome.SMALL_MOUNTAINS) ||
			biome.equals(Biome.SWAMPLAND_MOUNTAINS) ||
			biome.equals(Biome.TAIGA_MOUNTAINS)) {
			return 1;
		} else if (biome.equals(Biome.BIRCH_FOREST) ||
			biome.equals(Biome.EXTREME_HILLS) ||
			biome.equals(Biome.FOREST) ||
			biome.equals(Biome.FOREST_HILLS) ||
			biome.equals(Biome.ICE_PLAINS) ||
			biome.equals(Biome.ICE_PLAINS_SPIKES) ||
			biome.equals(Biome.JUNGLE) ||
			biome.equals(Biome.JUNGLE_EDGE) ||
			biome.equals(Biome.MEGA_SPRUCE_TAIGA) ||
			biome.equals(Biome.MEGA_SPRUCE_TAIGA_HILLS) ||
			biome.equals(Biome.MEGA_TAIGA) ||
			biome.equals(Biome.MEGA_TAIGA_HILLS) ||
			biome.equals(Biome.FLOWER_FOREST) ||
			biome.equals(Biome.MESA) ||
			biome.equals(Biome.MESA_BRYCE) ||
			biome.equals(Biome.MESA_PLATEAU) ||
			biome.equals(Biome.MESA_PLATEAU_FOREST) ||
			biome.equals(Biome.ROOFED_FOREST) ||
			biome.equals(Biome.SAVANNA) ||
			biome.equals(Biome.SAVANNA_PLATEAU) ||
			biome.equals(Biome.SUNFLOWER_PLAINS) ||
			biome.equals(Biome.TAIGA) ||
			biome.equals(Biome.TAIGA_HILLS)) {
			return 2;
		} else if (biome.equals(Biome.BEACH) ||
			biome.equals(Biome.COLD_BEACH) ||
			biome.equals(Biome.COLD_TAIGA) ||
			biome.equals(Biome.DEEP_OCEAN) ||
			biome.equals(Biome.DESERT) ||
			biome.equals(Biome.DESERT_HILLS) ||
			biome.equals(Biome.DESERT_MOUNTAINS) ||
			biome.equals(Biome.FROZEN_OCEAN) ||
			biome.equals(Biome.FROZEN_RIVER) ||
			biome.equals(Biome.MUSHROOM_ISLAND) ||
			biome.equals(Biome.MUSHROOM_SHORE) ||
			biome.equals(Biome.OCEAN) ||
			biome.equals(Biome.PLAINS) ||
			biome.equals(Biome.RIVER) ||
			biome.equals(Biome.STONE_BEACH) ||
			biome.equals(Biome.SWAMPLAND) ) {
			return 3;
		} else {
			return 0;
		}
	}
	
	private ItemStack getFishForBiome() {
		Random rand = new Random();
		int maxRand = Fishery.MAX_CHANCE;
		int baseRand = rand.nextInt(maxRand);
		ItemStack newItem = null;
		int fisheryLevel = this.fishHatchery.getLevel();
		int fishTier;
		if (baseRand < ((int)((fishHatchery.getChance(Fishery.FISH_T4_RATE))*maxRand)) && fisheryLevel >= 4) {
			fishTier = 4;
		} else if (baseRand < ((int)((fishHatchery.getChance(Fishery.FISH_T3_RATE))*maxRand)) && fisheryLevel >= 3) {
			fishTier = 3;
		} else if (baseRand < ((int)((fishHatchery.getChance(Fishery.FISH_T2_RATE))*maxRand)) && fisheryLevel >= 2) {
			fishTier = 2;
		} else if (baseRand < ((int)((fishHatchery.getChance(Fishery.FISH_T1_RATE))*maxRand))) {
			fishTier = 1;
		} else {
			fishTier = 0;
		}
		
		int biome = getBiome();
		int randMax = 100;
		int biomeRand = rand.nextInt(randMax);
		switch (fishTier) {
			case 0: //Fish Tier 0
				if (biomeRand >= 95) { 
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish0_4"));
				} else if (biomeRand > 85) {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish0_3"));
				} else if (biomeRand > 75) {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish0_2"));
				} else if (biomeRand > 50) {
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish0_1"));
				} else {
					int junkRand = rand.nextInt(randMax);
					if (junkRand > 90) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:blue_corral"));
					} else if (junkRand > 90) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:red_corral"));
					} else if (junkRand > 90) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:green_corral"));
					} else if (junkRand > 90) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:blue_corral"));
					} else if (junkRand > 75) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:seaweed"));
					} else if (junkRand > 75) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:algae"));
					} else if (junkRand > 60) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:salt_rocks"));
					} else if (junkRand > 60) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:clean_rocks"));
					} else {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:refined_sugar"));
					}
				}
				break;
			case 1: //Fish Tier 1
				switch (biome) {
				
				case 0: //Not ranked
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish0_1"));
					break;
					
				case 1: //Mountains
					if (biomeRand < 80) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish1_1"));
					} else {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish1_1"));
					}
					break;
					
				case 2: //Flatter Lands
					if (biomeRand < 85) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish1_2"));
					} else {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish1_3"));
					}
					break;
					
				case 3: // Oceans, Mushroom, Swamps, Ice
					if (biomeRand < 90) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish1_3"));
					} else {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish1_4"));
					}
					break;
				}
				break;
				
			case 2: //Fish Tier 2
				switch (biome) {
				
				case 0: //Not ranked
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish0_1"));
					break;
					
				case 1: //Mountains
					if (biomeRand < 80) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish2_1"));
					} else {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish2_1"));
					}
					break;
					
				case 2: //Flatter Lands
					if (biomeRand < 85) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish2_2"));
					} else {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish2_3"));
					}
					break;
					
				case 3: // Oceans, Mushroom, Swamps, Ice
					if (biomeRand < 90) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish2_3"));
					} else {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish2_4"));
					}
					break;
				}
				break;
				
			case 3: //Fish Tier 3
				switch (biome) {
				
				case 0: //Not ranked
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish0_1"));
					break;
					
				case 1: //Mountains
					if (biomeRand < 80) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish3_1"));
					} else {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish3_1"));
					}
					break;
					
				case 2: //Flatter Lands
					if (biomeRand < 85) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish3_2"));
					} else {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish3_3"));
					}
					break;
					
				case 3: // Oceans, Mushroom, Swamps, Ice
					if (biomeRand < 90) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish3_3"));
					} else {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish3_4"));
					}
					break;
				}
				break;
				
			case 4: //Fish Tier 4
				switch (biome) {
				
				case 0: //Not ranked
					newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish0_1"));
					break;
					
				case 1: //Mountains
					if (biomeRand < 80) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish4_1"));
					} else {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish4_1"));
					}
					break;
					
				case 2: //Flatter Lands
					if (biomeRand < 85) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish4_2"));
					} else {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish4_3"));
					}
					break;
					
				case 3: // Oceans, Mushroom, Swamps, Ice
					if (biomeRand < 90) {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish4_3"));
					} else {
						newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish4_4"));
					}
					break;
				}
				break;
		}
		
		if (newItem == null) {
			CivLog.debug("Fishery: newItem was null");
			newItem = LoreMaterial.spawn(LoreMaterial.materialMap.get("civ:fish0_1"));
		}
		return newItem;
	}
	
	@Override
	public void run() {
		if (this.fishHatchery.lock.tryLock()) {
			try {
				try {
					processFisheryUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} finally {
				this.fishHatchery.lock.unlock();
			}
		} else {
			debug(this.fishHatchery, "Failed to get lock while trying to start task, aborting.");
		}
	}
}