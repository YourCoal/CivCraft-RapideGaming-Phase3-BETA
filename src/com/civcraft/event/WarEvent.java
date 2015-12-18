package com.civcraft.event;

import java.util.Calendar;

import com.civcraft.config.CivSettings;
import com.civcraft.exception.InvalidConfiguration;
import com.civcraft.main.CivLog;
import com.civcraft.threading.TaskMaster;
import com.civcraft.threading.timers.WarEndCheckTask;
import com.civcraft.util.TimeTools;
import com.civcraft.war.War;

public class WarEvent implements EventInterface {

	@Override
	public void process() {
		CivLog.info("TimerEvent: WarEvent -------------------------------------");
		try {
			War.setWarTime(true);
		} catch (Exception e) {
			CivLog.error("WarStartException:"+e.getMessage());
			e.printStackTrace();
		}
		// Start repeating task waiting for war time to end.
		TaskMaster.syncTask(new WarEndCheckTask(), TimeTools.toTicks(1));
	}
	
	@Override
	public Calendar getNextDate() throws InvalidConfiguration {
		Calendar cal = EventTimer.getCalendarInServerTimeZone();
		int dayOfWeek = CivSettings.getInteger(CivSettings.warConfig, "war.time_day");
		int hourOfWar = CivSettings.getInteger(CivSettings.warConfig, "war.time_hour");
		cal.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		cal.set(Calendar.HOUR_OF_DAY, hourOfWar);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		Calendar now = Calendar.getInstance();
		if (now.after(cal)) {
			cal.add(Calendar.WEEK_OF_MONTH, 1);
			cal.set(Calendar.DAY_OF_WEEK, dayOfWeek);
			cal.set(Calendar.HOUR_OF_DAY, hourOfWar);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
		}
		return cal;
	}
}
