package com.global.perks.components;

import org.bukkit.entity.Player;

import com.civcraft.exception.CivException;
import com.civcraft.interactive.InteractiveConfirmWeatherChange;
import com.civcraft.main.CivGlobal;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;
import com.civcraft.util.CivColor;

public class ChangeWeather extends PerkComponent {

	@Override
	public void onActivate(Resident resident) {
		Player player;
		try {
			player = CivGlobal.getPlayer(resident);
		} catch (CivException e) {
			return;
		}
		if (!player.getWorld().isThundering() && !player.getWorld().hasStorm()) {
			CivMessage.sendError(resident, "Weather is already sunny!");
			return;
		}
		
		CivMessage.sendHeading(resident, "Changing the Weather to Sunny");
		CivMessage.send(resident, CivColor.Green+"Are you sure you want the weather to be sunny?");
		CivMessage.send(resident, CivColor.LightGray+"If so type 'yes', type anything else to cancel.");
		resident.setInteractiveMode(new InteractiveConfirmWeatherChange(this));
	}
}
