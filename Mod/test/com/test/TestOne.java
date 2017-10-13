package com.test;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.mod.datafeeder.DataFeed;
import com.mod.objects.CacheMetaData;
import com.mod.process.models.CacheService;

public class TestOne {
	
	@Test
	public void testCache1(){
		
		
		CacheService.clearDateDataRecord();
		CacheService.addMetaDataToDateRecording("group1", metadata());
		CacheService.addDateRecordingCache(initialSetup());
		
		CacheService.getMetaDataToDateRecording("group1");
		
		
		for(int i=0;i<10;i++){
			System.out.println(TestDataBuilder.option1.get(i).getLtp());
			CacheService.PRICE_LIST.put("12345", TestDataBuilder.option1.get(i).getLtp().doubleValue());
//			CacheService.PRICE_LIST.put("12346", TestDataBuilder.option2.get(i).getLtp().doubleValue());
//			CacheService.PRICE_LIST.put("12347", TestDataBuilder.option3.get(i).getLtp().doubleValue());
//			CacheService.PRICE_LIST.put("12348", TestDataBuilder.option4.get(i).getLtp().doubleValue());
			
			TDoubleList list = new TDoubleArrayList();
			list.add(Double.valueOf(DataFeed.incrementTime()));
			list.add(CacheService.PRICE_LIST.get("12345"));
//			list.add(CacheService.PRICE_LIST.get("12346"));
//			list.add(CacheService.PRICE_LIST.get("12347"));
//			list.add(CacheService.PRICE_LIST.get("12348"));
			
			CacheService.addDateRecordingCache(list);
			
			
		}
		CacheService.dumpDateRecording();
		/**
		 * Gives back the last 5 prices
		 */
		TDoubleList d = CacheService.getItemsFromDateDataRecord(12345, 5);
		//CacheService.addDateRecordingCache(data);
		System.out.println();
		
	}
	
	private TDoubleList initialSetup(){
		TDoubleList list = new TDoubleArrayList();
		list.add(Double.valueOf(DataFeed.incrementTime()));
		list.add(12345);
		list.add(12346);
		list.add(12347);
		list.add(12348);
		
		return list;
	}
	
	private CacheMetaData metadata(){
		List<String> metadata = new ArrayList<String>();
		metadata.add(String.valueOf(DataFeed.START_TIME_STRING));
		metadata.add("12345");
		metadata.add("12346");
		metadata.add("12347");
		metadata.add("12348");
		return new CacheMetaData(metadata);
	}

}
