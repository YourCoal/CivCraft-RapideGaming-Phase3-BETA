package com.civcraft.siege.titanium;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.civcraft.exception.CivException;
import com.civcraft.main.CivMessage;
import com.civcraft.util.BlockCoord;

public class TitaniumCannonListener implements Listener {
	
	BlockCoord bcoord = new BlockCoord();
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {
		
		bcoord.setFromLocation(event.getBlock().getLocation());
		TitaniumCannon cannon = TitaniumCannon.cannonBlocks.get(bcoord);
		if (cannon != null) {
			cannon.onHit(event);
			event.setCancelled(true);
			return;
		}
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (!event.hasBlock()) {
			return;
		}
		
		try {
			bcoord.setFromLocation(event.getClickedBlock().getLocation());
			TitaniumCannon cannon = TitaniumCannon.fireSignLocations.get(bcoord);
			if (cannon != null) {
				cannon.processFire(event);
				event.setCancelled(true);
				return;
			}
			
			cannon = TitaniumCannon.angleSignLocations.get(bcoord);
			if (cannon != null) {
				cannon.processAngle(event);
				event.setCancelled(true);
				return;
			}
	
			cannon = TitaniumCannon.powerSignLocations.get(bcoord);
			if (cannon != null) {
				cannon.processPower(event);
				event.setCancelled(true);		
				return;
			}
		} catch (CivException e) {
			CivMessage.sendError(event.getPlayer(), e.getMessage());
			event.setCancelled(true);
		}

		
		
	}
}
