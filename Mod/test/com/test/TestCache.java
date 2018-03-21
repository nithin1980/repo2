package com.test;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;

import com.mod.process.models.CacheService;

import gnu.trove.list.TDoubleList;

public class TestCache {

	@Test
	public void testCache(){
		
		Calendar closing = Calendar.getInstance();
		closing.set(Calendar.HOUR, 10);
		closing.set(Calendar.MINUTE,00);
		closing.set(Calendar.SECOND,00);
		closing.set(Calendar.AM_PM, Calendar.AM);
		System.out.println(closing.getTime());
		
		
		String serverTime = "3:45:00";
		String[] times = serverTime.split("\\:");
		int hour = Integer.valueOf(times[0]);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, hour);
		cal.set(Calendar.MINUTE, Integer.valueOf(times[1]));
		cal.set(Calendar.SECOND, Integer.valueOf(times[2]));
		cal.set(Calendar.AM_PM, Calendar.AM);
		//cal.set(Calendar., value);
		System.out.println((closing.getTimeInMillis()-cal.getTimeInMillis())/1000);
		
		//cal.add(Calendar.MINUTE, 330);
		
		
		
		
		
		
	}
}
