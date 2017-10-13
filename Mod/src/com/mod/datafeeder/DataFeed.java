package com.mod.datafeeder;

import java.util.Calendar;
import java.util.List;

import com.mod.process.models.ApplicationHelper;

public class DataFeed {

	public static Calendar START_TIME;
	public static long START_TIME_IN_NUMERIC=0;
	public static String START_TIME_STRING;
	
	static{
		START_TIME = Calendar.getInstance();
		START_TIME_IN_NUMERIC = START_TIME.getTimeInMillis();
		START_TIME_STRING = START_TIME.getTime().toString();
	}
	public DataFeed() {
	}
	
	public static void resetTime(){
		START_TIME = Calendar.getInstance();
		START_TIME_IN_NUMERIC = START_TIME.getTimeInMillis();
		START_TIME_STRING = START_TIME.getTime().toString();
	}
	
	
	public static List<List<String>> data(){
		return ApplicationHelper.getConfig();
	}
	
	public static long incrementTime(){
		return System.currentTimeMillis()-START_TIME_IN_NUMERIC;
	}
	
}
