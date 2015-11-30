package com.civcraft.event;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.civcraft.exception.InvalidConfiguration;
import com.civcraft.main.CivLog;
import com.civcraft.threading.TaskMaster;
import com.civcraft.threading.timers.CottageEventTimer;

public class HourlyTickEventMinute2 implements EventInterface {
	
	@Override
	public void process() {
		CivLog.info("TimerEvent: Hourly Cottage -------------------------------------");
		TaskMaster.asyncTask("CottageEventTimer", new CottageEventTimer(), 0);
		CivLog.info("TimerEvent: Hourly Cottage Finished -----------------------------");
	}
	
	@Override
	public Calendar getNextDate() throws InvalidConfiguration {
		SimpleDateFormat sdf = new SimpleDateFormat("M/dd h:mm:ss a z");
		Calendar cal = EventTimer.getCalendarInServerTimeZone();
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 1);
		cal.add(Calendar.SECOND, 3600);
		sdf.setTimeZone(cal.getTimeZone());
		return cal;
	}
}
