package com.civcraft.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.civcraft.exception.CivException;
import com.civcraft.main.CivGlobal;
import com.civcraft.object.Resident;
import com.civcraft.util.CivColor;
import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Chatter.Result;
import com.dthielke.herochat.Herochat;

public class HeroChatListener implements Listener {
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChannelChatEvent(ChannelChatEvent event) {
		Resident resident = CivGlobal.getResident(event.getSender().getName());
		if (resident == null) {
			event.setResult(Result.FAIL);
			return;
		}
		
		if (!resident.isInteractiveMode()) {
			if (resident.isMuted()) {
				event.setResult(Result.MUTED);
				return;
			}
		}
		
		if (event.getChannel().getDistance() > 0) {
			for (String name : Resident.allchatters) {
				Player player;
				try {
					player = CivGlobal.getPlayer(name);
				} catch (CivException e) {
					continue;
				}
				
				Chatter you = Herochat.getChatterManager().getChatter(player);
				if (!event.getSender().isInRange(you, event.getChannel().getDistance())) {
					player.sendMessage(CivColor.White+event.getSender().getName()+"[Far]: "+event.getMessage());
				}
			}
		}
	}
}
