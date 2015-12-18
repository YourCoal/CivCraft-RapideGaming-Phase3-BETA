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
package com.civcraft.listener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.civcraft.config.CivSettings;
import com.civcraft.config.ConfigTechPotion;
import com.civcraft.exception.CivException;
import com.civcraft.items.units.UnitItemMaterial;
import com.civcraft.items.units.UnitMaterial;
import com.civcraft.lorestorage.LoreMaterial;
import com.civcraft.main.CivData;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivLog;
import com.civcraft.main.CivMessage;
import com.civcraft.mobs.timers.MobSpawnerTimer;
import com.civcraft.object.CultureChunk;
import com.civcraft.object.Resident;
import com.civcraft.road.Road;
import com.civcraft.structure.Capitol;
import com.civcraft.threading.TaskMaster;
import com.civcraft.threading.tasks.PlayerChunkNotifyAsyncTask;
import com.civcraft.threading.tasks.PlayerLoginAsyncTask;
import com.civcraft.threading.timers.PlayerLocationCacheUpdate;
import com.civcraft.util.BlockCoord;
import com.civcraft.util.ChunkCoord;
import com.civcraft.util.CivColor;
import com.civcraft.util.ItemManager;
import com.civcraft.war.War;
import com.civcraft.war.WarStats;

public class PlayerListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPickup(PlayerPickupItemEvent event) {
		
		String name;
		boolean rare = false;
		if (event.getItem().getItemStack().getItemMeta().hasDisplayName()) {
			name = event.getItem().getItemStack().getItemMeta().getDisplayName();
			rare = true;
		} else {
			name = event.getItem().getItemStack().getType().name().replace("_", " ").toLowerCase();
		}
		
		Resident resident = CivGlobal.getResident(event.getPlayer());
		if (resident.getItemMode().equals("all")) {
			CivMessage.send(event.getPlayer(), CivColor.LightGreen+"You've picked up "+CivColor.LightPurple+event.getItem().getItemStack().getAmount()+" "+name);
		} else if (resident.getItemMode().equals("rare") && rare) {
			CivMessage.send(event.getPlayer(), CivColor.LightGreen+"You've picked up "+CivColor.LightPurple+event.getItem().getItemStack().getAmount()+" "+name);
		}
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(PlayerLoginEvent event) {
		CivLog.info("Scheduling on player login task for player:"+event.getPlayer().getName());
		TaskMaster.asyncTask("onPlayerLogin-"+event.getPlayer().getName(), new PlayerLoginAsyncTask(event.getPlayer().getUniqueId()), 0);
		
		CivGlobal.playerFirstLoginMap.put(event.getPlayer().getName(), new Date());
		PlayerLocationCacheUpdate.playerQueue.add(event.getPlayer().getName());
		MobSpawnerTimer.playerQueue.add((event.getPlayer().getName()));
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
		if (event.getCause().equals(TeleportCause.COMMAND) ||
				event.getCause().equals(TeleportCause.PLUGIN)) {
			CivLog.info("[TELEPORT] "+event.getPlayer().getName()+" to:"+event.getTo().getBlockX()+","+event.getTo().getBlockY()+","+event.getTo().getBlockZ()+
					" from:"+event.getFrom().getBlockX()+","+event.getFrom().getBlockY()+","+event.getFrom().getBlockZ());
		}
	}
	
	private void setModifiedMovementSpeed(Player player) {
		/* Change move speed based on armor. */
		double speed = CivSettings.normal_speed;
		Resident resident = CivGlobal.getResident(player);
		if (resident != null && resident.isOnRoad()) {	
			if (player.getVehicle() != null && player.getVehicle().getType().equals(EntityType.HORSE)) {
				Vector vec = player.getVehicle().getVelocity();
				double yComp = vec.getY();
				
				vec.multiply(Road.ROAD_HORSE_SPEED);
				vec.setY(yComp); /* Do not multiply y velocity. */
				
				player.getVehicle().setVelocity(vec);
			} else {
				speed *= Road.ROAD_PLAYER_SPEED;
			}
		}
		
		player.setWalkSpeed((float) Math.min(1.0f, speed));
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerMove(PlayerMoveEvent event) {
		/* Abort if we havn't really moved */
		if (event.getFrom().getBlockX() == event.getTo().getBlockX() && 
			event.getFrom().getBlockZ() == event.getTo().getBlockZ() && 
			event.getFrom().getBlockY() == event.getTo().getBlockY()) {
			return;
		}
		
		/* Test for enchants effecting movement. */
		/* TODO can speed be set once? If so we should only calculate speed change when our armor changes. */
		setModifiedMovementSpeed(event.getPlayer());
				
		ChunkCoord fromChunk = new ChunkCoord(event.getFrom());
		ChunkCoord toChunk = new ChunkCoord(event.getTo());
		
		// Haven't moved chunks.
		if (fromChunk.equals(toChunk)) {
			return;
		}
		
		TaskMaster.asyncTask(PlayerChunkNotifyAsyncTask.class.getSimpleName(), 
				new PlayerChunkNotifyAsyncTask(event.getFrom(), event.getTo(), event.getPlayer().getName()), 0);

	}
	
	UUID playerUUID;
	public void PlayerLoginAsyncTask(UUID playerUUID) {
		this.playerUUID = playerUUID;
	}
	public Player getPlayer() throws CivException {
		Player player = Bukkit.getPlayer(playerUUID);
		if (player == null) {
			throw new CivException("Player offline now. May have been kicked.");
		}
		return player;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) throws CivException {
		Player player = event.getPlayer();
		Resident resident = CivGlobal.getResident(player);
		if (resident == null || !resident.hasTown()) {
			return;
		}
		
		if (resident.getTown().getCiv().getDiplomacyManager().isAtWar() && War.isWarTime()) {
			Capitol capitol = resident.getCiv().getCapitolStructure();
			if (capitol != null) {
				BlockCoord respawn = capitol.getRandomRespawnPoint();
				if (respawn != null) {
					resident.setLastKilledTime(new Date());
					event.setRespawnLocation(respawn.getCenteredLocation());
					CivMessage.send(player, CivColor.LightGray+"You've respawned in the War Room since it's WarTime and you're at war.");
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Resident resident = CivGlobal.getResident(event.getPlayer());
		if (resident != null) {
			if (resident.previewUndo != null) {
				resident.previewUndo.clear();
			}
			resident.clearInteractiveMode();
		}
		MobSpawnerTimer.playerQueue.remove((event.getPlayer().getName()));
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			//Unit.removeUnit(((Player)event.getEntity()));
			
			ArrayList<ItemStack> stacksToRemove = new ArrayList<ItemStack>();
			for (ItemStack stack : event.getDrops()) {
				if (stack != null) {
					//CustomItemStack is = new CustomItemStack(stack);
					LoreMaterial material = LoreMaterial.getMaterial(stack);
					if (material != null) {
						material.onPlayerDeath(event, stack);
						if (material instanceof UnitMaterial) {
							stacksToRemove.add(stack);
							continue;
						}
						
						if (material instanceof UnitItemMaterial) {
							stacksToRemove.add(stack);
							continue;
						}
					}
				}
			}
			
			for (ItemStack stack : stacksToRemove) {
				event.getDrops().remove(stack);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (War.isWarTime()) {
			if (event.getEntity().getKiller() != null) {
				WarStats.incrementPlayerKills(event.getEntity().getKiller().getName());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST) 
	public void onPortalCreate(PortalCreateEvent event) {
		event.setCancelled(true);
	}
	
//	@EventHandler(priority = EventPriority.NORMAL)
//	public void OnCraftItemEvent(CraftItemEvent event) {
//		if (event.getInventory() == null) {
//			return;
//		}
//		
//		ItemStack resultStack = event.getInventory().getResult();
//		if (resultStack == null) {
//			return;
//		}
//		
//		if (CivSettings.techItems == null) {
//			CivLog.error("tech items null???");
//			return;
//		}
//
//		//XXX Replaced via materials system.
////		ConfigTechItem item = CivSettings.techItems.get(resultStack.getTypeId());
////		if (item != null) {
////			Resident resident = CivGlobal.getResident(event.getWhoClicked().getName());
////			if (resident != null && resident.hasTown()) {
////				if (resident.getCiv().hasTechnology(item.require_tech)) {
////					return;
////				}
////			}	
////			event.setCancelled(true);
////			CivMessage.sendError((Player)event.getWhoClicked(), "You do not have the required technology to craft a "+item.name);
////		}
//	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPortalEvent(PlayerPortalEvent event) {
		if(event.getCause().equals(TeleportCause.END_PORTAL)) {
			event.setCancelled(true);
			CivMessage.sendErrorNoRepeat(event.getPlayer(), "The End portal is disabled on this server.");
			return;
		}
		
		if (event.getCause().equals(TeleportCause.NETHER_PORTAL)) {
			event.setCancelled(true);
			CivMessage.sendErrorNoRepeat(event.getPlayer(), "The Nether is disabled on this server.");
			return;
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		// THIS EVENT IS NOT RUN IN OFFLINE MODE
	}

	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void OnPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
		Resident resident = CivGlobal.getResident(event.getPlayer());
	
		if (resident == null) {
			event.setCancelled(true);
			return;
		}

		ChunkCoord coord = new ChunkCoord(event.getBlockClicked().getLocation());
		CultureChunk cc = CivGlobal.getCultureChunk(coord);
		if (cc != null) {
			if (event.getBucket().equals(Material.LAVA_BUCKET) || 
					event.getBucket().equals(Material.LAVA)) {
				
				if (!resident.hasTown() || (resident.getTown().getCiv() != cc.getCiv())) {
					CivMessage.sendError(event.getPlayer(), "You cannot place lava inside another civ's culture.");
					event.setCancelled(true);
					return;
				}
				
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void OnBrewEvent(BrewEvent event) {
		/* Hardcoded disables based on ingredients used. */
		if (event.getContents().contains(Material.SPIDER_EYE) ||
			event.getContents().contains(Material.GOLDEN_CARROT) ||
			event.getContents().contains(Material.GHAST_TEAR) ||
			event.getContents().contains(Material.FERMENTED_SPIDER_EYE) ||
			event.getContents().contains(Material.BLAZE_POWDER) ||
			event.getContents().contains(Material.SULPHUR)) {
			event.setCancelled(true);
		}
		
		if (event.getContents().contains(Material.POTION)) {
			ItemStack potion = event.getContents().getItem(event.getContents().first(Material.POTION));
			
			if (potion.getDurability() == CivData.MUNDANE_POTION_DATA || 
				potion.getDurability() == CivData.MUNDANE_POTION_EXT_DATA ||
				potion.getDurability() == CivData.THICK_POTION_DATA) {
				event.setCancelled(true);
			}
		}
	}
	
	private boolean isPotionDisabled(PotionEffect type) {
		if (type.getType().equals(PotionEffectType.SPEED) ||
			type.getType().equals(PotionEffectType.FIRE_RESISTANCE) ||
			type.getType().equals(PotionEffectType.HEAL)) {
			return false;
		}
		
		return true;
	}
	
	@EventHandler(priority = EventPriority.LOW) 
	public void onPotionSplash(PotionSplashEvent event) {
		for (PotionEffect effect : event.getPotion().getEffects()) {
			if (isPotionDisabled(effect)) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW) 
	public void onConsume(PlayerItemConsumeEvent event) {
		if (ItemManager.getId(event.getItem()) == CivData.GOLDEN_APPLE) {
			CivMessage.sendError(event.getPlayer(), "You cannot use golden apples.");
			event.setCancelled(true);
			return;
		}
		
		if (event.getItem().getType().equals(Material.POTION)) {
			ConfigTechPotion pot = CivSettings.techPotions.get(Integer.valueOf(event.getItem().getDurability()));
			if (pot != null) {
				if (!pot.hasTechnology(event.getPlayer())) {
					CivMessage.sendError(event.getPlayer(), "You cannot use "+pot.name+" potions. You do not have the technology yet.");
					event.setCancelled(true);
					return;
				}
				if (pot.hasTechnology(event.getPlayer())) {
					event.setCancelled(false);
				}
			} else {
				CivMessage.sendError(event.getPlayer(), "You cannot use this type of potion.");
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryOpenEvent(InventoryOpenEvent event) {
		if (event.getInventory() instanceof DoubleChestInventory) {
			DoubleChestInventory doubleInv = (DoubleChestInventory)event.getInventory();
						
			Chest leftChest = (Chest)doubleInv.getHolder().getLeftSide();			
			/*Generate a new player 'switch' event for the left and right chests. */
			PlayerInteractEvent interactLeft = new PlayerInteractEvent((Player)event.getPlayer(), Action.RIGHT_CLICK_BLOCK, null, leftChest.getBlock(), null);
			BlockListener.OnPlayerSwitchEvent(interactLeft);
			
			if (interactLeft.isCancelled()) {
				event.setCancelled(true);
				return;
			}
			
			Chest rightChest = (Chest)doubleInv.getHolder().getRightSide();
			PlayerInteractEvent interactRight = new PlayerInteractEvent((Player)event.getPlayer(), Action.RIGHT_CLICK_BLOCK, null, rightChest.getBlock(), null);
			BlockListener.OnPlayerSwitchEvent(interactRight);
			
			if (interactRight.isCancelled()) {
				event.setCancelled(true);
				return;
			}			
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByEntityMonitor(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		Player attacker;
		Player defender;
		String damage;
		
		if (event.getEntity() instanceof Player) {
			defender = (Player)event.getEntity();
		} else {
			defender = null;
		}
		
		if (event.getDamager() instanceof Player) {
			attacker = (Player)event.getDamager();
		} else if (event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow)event.getDamager();
			if (arrow.getShooter() instanceof Player) {
				attacker = (Player)arrow.getShooter();
			} else {
				attacker = null;
			}
		} else {
			attacker = null;
		}
		
		if (attacker == null && defender == null) {
			return;
		}
		
		damage = new DecimalFormat("#.#").format(event.getDamage());
		
		if (defender != null) {
			Resident defenderResident = CivGlobal.getResident(defender);
			if (defenderResident.isCombatInfo()) {	
				if (attacker != null) {
					CivMessage.send(defender, CivColor.LightGray+"   [Combat] Took "+CivColor.Rose+damage+
							" damage "+CivColor.LightGray+" from "+CivColor.LightPurple+attacker.getName());				
				} else {
					String entityName = null;
					
					if (event.getDamager() instanceof LivingEntity) {
						entityName = ((LivingEntity)event.getDamager()).getCustomName();
					}
					
					if (entityName == null) {
						entityName = event.getDamager().getType().toString();
					}
					
					CivMessage.send(defender, CivColor.LightGray+"   [Combat] Took "+CivColor.Rose+damage+
							" damage "+CivColor.LightGray+" from a "+entityName);
				}
			}
		}
		
		if (attacker != null) {
			Resident attackerResident = CivGlobal.getResident(attacker);
			if (attackerResident.isCombatInfo()) {
				if (defender != null) {
					CivMessage.send(attacker, CivColor.LightGray+"   [Combat] Gave "+CivColor.LightGreen+damage+CivColor.LightGray+" damage to "+CivColor.LightPurple+defender.getName());
				} else {
					String entityName = null;
					
					if (event.getDamager() instanceof LivingEntity) {
						entityName = ((LivingEntity)event.getDamager()).getCustomName();
					}
					
					if (entityName == null) {
						entityName = event.getDamager().getType().toString();
					}
					
					CivMessage.send(attacker, CivColor.LightGray+"   [Combat] Gave "+CivColor.LightGreen+damage+CivColor.LightGray+" damage to a "+entityName);
				}
			}
		}
	}
}
