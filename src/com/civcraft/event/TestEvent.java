package com.civcraft.event;

import java.util.Calendar;

import com.civcraft.exception.InvalidConfiguration;
import com.civcraft.main.CivMessage;

public class TestEvent implements EventInterface {

	@Override
	public void process() {
		CivMessage.global("This is a test event firing!");
	}

	@Override
	public Calendar getNextDate() throws InvalidConfiguration {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, 60);
		return cal;
	}
}
