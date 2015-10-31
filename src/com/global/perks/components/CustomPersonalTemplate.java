package com.global.perks.components;

import java.io.IOException;

import org.bukkit.entity.Player;

import com.civcraft.config.ConfigBuildableInfo;
import com.civcraft.exception.CivException;
import com.civcraft.main.CivMessage;
import com.civcraft.object.Resident;
import com.civcraft.template.Template;
import com.civcraft.util.CivColor;

public class CustomPersonalTemplate extends PerkComponent {
	
	@Override
	public void onActivate(Resident resident) {
		CivMessage.send(resident, CivColor.LightGreen+"No need to activate this perk. Always active =)");
	}
	

	public Template getTemplate(Player player, ConfigBuildableInfo info) {
		Template tpl = new Template();
		try {
			tpl.initTemplate(player.getLocation(), info, this.getString("theme"));
		} catch (CivException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tpl;
	}
}
