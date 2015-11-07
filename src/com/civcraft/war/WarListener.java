package com.civcraft.war;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

import com.civcraft.config.CivSettings;
import com.civcraft.exception.InvalidConfiguration;
import com.civcraft.main.CivData;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.CultureChunk;
import com.civcraft.siege.iron.IronCannon;
import com.civcraft.util.ChunkCoord;
import com.civcraft.util.ItemManager;

public class WarListener implements Listener {

	ChunkCoord coord = new ChunkCoord();
	@EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (!War.isWarTime()) {
			return;
		}
		
		coord.setFromLocation(event.getBlock().getLocation());
		CultureChunk cc = CivGlobal.getCultureChunk(coord);
		
		if (cc == null) {
			return;
		}
		
		if (!cc.getCiv().getDiplomacyManager().isAtWar()) {
			return;
		}
				
		if (event.getBlock().getType().equals(Material.DIRT) || 
			event.getBlock().getType().equals(Material.GRASS) ||
			event.getBlock().getType().equals(Material.SAND) ||
			event.getBlock().getType().equals(Material.GRAVEL) ||
			event.getBlock().getType().equals(Material.TORCH) ||
			event.getBlock().getType().equals(Material.REDSTONE_TORCH_OFF) ||
			event.getBlock().getType().equals(Material.REDSTONE_TORCH_ON) ||
			event.getBlock().getType().equals(Material.REDSTONE) ||
			event.getBlock().getType().equals(Material.TNT) ||
			event.getBlock().getType().equals(Material.LADDER) ||
			event.getBlock().getType().equals(Material.VINE) ||
			!event.getBlock().getType().isSolid()) {
			return;
		}
		
		CivMessage.sendError(event.getPlayer(), "Must use TNT to break blocks in at-war civilization cultures during WarTime.");
		event.setCancelled(true);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (!War.isWarTime()) {
			return;
		}
		
		coord.setFromLocation(event.getBlock().getLocation());
		CultureChunk cc = CivGlobal.getCultureChunk(coord);
		
		if (cc == null) {
			return;
		}
		
		if (!cc.getCiv().getDiplomacyManager().isAtWar()) {
			return;
		}
				
		if (event.getBlock().getType().equals(Material.DIRT) || 
			event.getBlock().getType().equals(Material.GRASS) ||
			event.getBlock().getType().equals(Material.SAND) ||
			event.getBlock().getType().equals(Material.GRAVEL) ||
			event.getBlock().getType().equals(Material.TORCH) ||
			event.getBlock().getType().equals(Material.REDSTONE_TORCH_OFF) ||
			event.getBlock().getType().equals(Material.REDSTONE_TORCH_ON) ||
			event.getBlock().getType().equals(Material.REDSTONE) ||
			event.getBlock().getType().equals(Material.LADDER) ||
			event.getBlock().getType().equals(Material.VINE) ||
			event.getBlock().getType().equals(Material.TNT)) {
			
			if (event.getBlock().getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR) {
				return;
			}
			
			event.getBlock().getWorld().spawnFallingBlock(event.getBlock().getLocation(), event.getBlock().getType(), (byte) 0);
			event.getBlock().setType(Material.AIR);
			
			return;
		}
		
		CivMessage.sendError(event.getPlayer(), "Can only place grass, dirt, and TNT blocks in at-war civilization cultures during WarTime.");
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (!War.isWarTime()) {
			return;
		}
		
		if (event.getEntity() == null) {
			return;
		}
		
		if (event.getEntityType().equals(EntityType.UNKNOWN)) {
			return;
		}
		
		if (event.getEntityType().equals(EntityType.PRIMED_TNT) ||
				event.getEntityType().equals(EntityType.MINECART_TNT)) {
			
			int yield1;
			try {
				yield1 = CivSettings.getInteger(CivSettings.cannonConfig, "iron_cannon.yield");
			} catch (InvalidConfiguration e) {
				e.printStackTrace();
				return;
			}
		
			yield1 = yield1 / 2;
		
			for (int y = -yield1; y <= yield1; y++) {
				for (int x = -yield1; x <= yield1; x++) {
					for (int z = -yield1; z <= yield1; z++) {
						Location loc = event.getLocation().clone().add(new Vector(x,y,z));
					
						if (loc.distance(event.getLocation()) < yield1) {
							WarRegen.saveBlock(loc.getBlock(), IronCannon.RESTORE_NAME, false);
							ItemManager.setTypeIdAndData(loc.getBlock(), CivData.AIR, 0, false);
						}
					}	
				}
			}
			event.setCancelled(true);
		}
	}
}
