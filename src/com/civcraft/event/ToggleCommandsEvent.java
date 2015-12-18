package com.civcraft.event;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

import org.bukkit.Bukkit;

import com.civcraft.config.CivSettings;
import com.civcraft.exception.InvalidConfiguration;
import com.civcraft.main.CivLog;
import com.civcraft.main.CivMessage;
import com.civcraft.util.CivColor;

public class ToggleCommandsEvent implements EventInterface {

	@Override
	public void process() {
		CivLog.info("TimerEvent: DisableTeleportEvent -------------------------------------");
		disableTeleport();
	}

	@Override
	public Calendar getNextDate() throws InvalidConfiguration {
		Calendar cal = EventTimer.getCalendarInServerTimeZone();
		int dayOfWeek = CivSettings.getInteger(CivSettings.warConfig, "war.disable_tp_time_day");
		int hourBeforeWar = CivSettings.getInteger(CivSettings.warConfig, "war.disable_tp_time_hour");
		int halfHourBeforeWar = CivSettings.getInteger(CivSettings.warConfig, "war.disable_tp_time_halfhour");
		cal.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		cal.set(Calendar.HOUR_OF_DAY, hourBeforeWar);
		cal.set(Calendar.MINUTE, halfHourBeforeWar);
		cal.set(Calendar.SECOND, 0);
		Calendar now = Calendar.getInstance();
		if (now.after(cal)) {
			cal.add(Calendar.WEEK_OF_MONTH, 1);
			cal.set(Calendar.DAY_OF_WEEK, dayOfWeek);
			cal.set(Calendar.HOUR_OF_DAY, hourBeforeWar);
			cal.set(Calendar.MINUTE, halfHourBeforeWar);
			cal.set(Calendar.SECOND, 0);
		}
		return cal;
	}
	
	public static void disableTeleport() {
		File file = new File("TPDisable.yml");
		if (!file.exists()) {
			CivLog.warning("No TPDisable.yml to run commands from");
			return;
		}
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			try {
				CivMessage.globalHeading(CivColor.BOLD+"Some Commands have just been disabled");
				CivMessage.globalHeading(CivColor.BOLD+"until after WarTime.");
				while ((line = br.readLine()) != null) {
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), line);
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public static void enableTeleport() {
		File file = new File("TPEnable.yml");
		if (!file.exists()) {
			CivLog.warning("No TPEnable.yml to run commands from");
			return;
		}
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			try {
				CivMessage.globalHeading(CivColor.BOLD+"Teleportation is now enabled.");
				while ((line = br.readLine()) != null) {
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), line);
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
	}
}