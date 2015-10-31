package com.civcraft.interactive;

import org.bukkit.entity.Player;

import com.civcraft.exception.CivException;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;
import com.civcraft.util.CivColor;
import com.civcraft.util.TimeTools;
import com.global.perks.components.ChangeWeather;

public class InteractiveConfirmWeatherChange implements InteractiveResponse {

	ChangeWeather perk;
	public InteractiveConfirmWeatherChange(ChangeWeather perk) {
		this.perk = perk;
	}
	
	@Override
	public void respond(String message, Resident resident) {
		resident.clearInteractiveMode();
		
		if (message.equalsIgnoreCase("yes")) {
			Player player;
			try {
				player = CivGlobal.getPlayer(resident);
				player.getWorld().setStorm(false);
				player.getWorld().setThundering(false);
				player.getWorld().setWeatherDuration((int) TimeTools.toTicks(20*60));
				CivMessage.global(resident.getName()+" has used a "+CivColor.Yellow+"Weather Change"+CivColor.RESET+" token to change the weather to sunny!");
				perk.markAsUsed(resident);
			} catch (CivException e) {
			}
		} else {
			CivMessage.send(resident, "Weather Change cancelled.");
		}
		
	}

}
