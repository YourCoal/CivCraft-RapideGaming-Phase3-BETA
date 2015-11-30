package com.civcraft.event;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.civcraft.exception.InvalidConfiguration;
import com.civcraft.main.CivLog;
import com.civcraft.threading.TaskMaster;
import com.civcraft.threading.timers.CultureEventTimer;

public class HourlyTickEventMinute5 implements EventInterface {
	
	@Override
	public void process() {
		CivLog.info("TimerEvent: Hourly Culture -------------------------------------");
		TaskMaster.asyncTask("CultureEventTimer", new CultureEventTimer(), 0);
		CivLog.info("TimerEvent: Hourly Culture Finished -----------------------------");
	}
	
	@Override
	public Calendar getNextDate() throws InvalidConfiguration {
		SimpleDateFormat sdf = new SimpleDateFormat("M/dd h:mm:ss a z");
		Calendar cal = EventTimer.getCalendarInServerTimeZone();
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 4);
		cal.add(Calendar.SECOND, 3600);
		sdf.setTimeZone(cal.getTimeZone());
		return cal;
	}
}
