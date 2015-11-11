package com.civcraft.command.admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.civcraft.command.CommandBase;
import com.civcraft.event.EventTimer;
import com.civcraft.exception.CivException;
import com.civcraft.exception.InvalidConfiguration;
import com.civcraft.main.CivMessage;

public class AdminTimerCommand extends CommandBase {

	@Override
	public void init() {
		command = "/ad timer";
		displayName = "Admin Timer";
		
		commands.put("set", "[name] [date] DAY:MONTH:YEAR:HOUR:MIN (24 hour time)");
		commands.put("run", "[name] Runs this timer event.");		
	}

	
	public void run_cmd() throws CivException {
		if (args.length < 2) {
			throw new CivException("Enter a timer name");
		}
		
		EventTimer timer = EventTimer.timers.get(args[1]);
		if (timer == null) {
			throw new CivException("No timer named "+args[1]);
		}
		
		Calendar next;
		try {
			next = timer.getEventFunction().getNextDate();
		} catch (InvalidConfiguration e) {
			e.printStackTrace();
			throw new CivException("Invalid configuration, cant run timer.");
		}

		timer.getEventFunction().process();
		timer.setLast(EventTimer.getCalendarInServerTimeZone());
		timer.setNext(next);
		timer.save();
		
		CivMessage.sendSuccess(sender, "Timer Ran");
	}
	
	public void set_cmd() throws CivException {
		if (args.length < 3) {
			throw new CivException("Enter a timer name and date like DAY:MONTH:YEAR:HOUR:MIN");
		}
		
		String timerName = args[1];
		EventTimer timer = EventTimer.timers.get(timerName);
		if (timer == null) {
			throw new CivException("No timer called: "+args[1]);
		}
		
		String dateStr = args[2];
		SimpleDateFormat parser = new SimpleDateFormat("d:M:y:H:m");
		
		Calendar next = EventTimer.getCalendarInServerTimeZone();
		try {
			next.setTime(parser.parse(dateStr));
			timer.setNext(next);
			timer.save();
			CivMessage.sendSuccess(sender, "Set timer "+timer.getName()+" to "+parser.format(next.getTime()));
		} catch (ParseException e) {
			throw new CivException("Couldnt parse "+args[2]+" into a date, use format: DAY:MONTH:YEAR:HOUR:MIN");
		}
		
	}
	
	@Override
	public void doDefaultAction() throws CivException {
		showHelp();		
	}

	@Override
	public void showHelp() {
		showBasicHelp();
	}

	@Override
	public void permissionCheck() throws CivException {
		
	}

}
