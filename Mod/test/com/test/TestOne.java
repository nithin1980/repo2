package com.test;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.catalina.tribes.io.DirectByteArrayOutputStream;
import org.junit.Test;

import com.mod.datafeeder.DataFeed;
import com.mod.interfaces.KiteGeneralWebSocketClient;
import com.mod.objects.CacheMetaData;
import com.mod.process.models.CacheService;
import com.mod.support.ApplicationHelper;
import com.mod.support.ConfigData;
import com.mod.support.XMLParsing;

public class TestOne {
	private static CountDownLatch latch;
	
	@Test
	public void testCache1(){
		
		
		
		latch = new CountDownLatch(1);
		KiteGeneralWebSocketClient webSocketClient = new KiteGeneralWebSocketClient(latch);
		
		ConfigData configData = XMLParsing.readAppConfig("C:/Users/nkumar/git/repo1/master/Mod/resource/app.config");
		ApplicationHelper.Application_Config_Cache.put("app", configData);

		configData = XMLParsing.readAppConfig("C:/Users/nkumar/git/repo1/master/Mod/resource/genwsclient.config");
		ApplicationHelper.Application_Config_Cache.put("mode1", configData);
		
		
		
		
		CacheService.clearDateDataRecord();
		CacheService.addMetaDataToDateRecording("group1", metadata());
		CacheService.initializeDataArray(initialSetup());
		/**
		 * Need to intialise the price backup thread as well.
		 */
		
		CacheService.getMetaDataToDateRecording("group1");
		
		long t = System.currentTimeMillis();
//		for(int i=0;i<10;i++){
//			
//			CacheService.PRICE_LIST.put(12345.0, TestDataBuilder.option1.get(i).getLtp().doubleValue());
//			CacheService.PRICE_LIST.put(12346.0, TestDataBuilder.option2.get(i).getLtp().doubleValue());
//			CacheService.PRICE_LIST.put(12347.0, TestDataBuilder.option3.get(i).getLtp().doubleValue());
//			CacheService.PRICE_LIST.put(12348.0, TestDataBuilder.option4.get(i).getLtp().doubleValue());
			
			
			/**
			 * This should read from the latest prices....
			 */
			
			
			
			
			
//		}
		//System.out.println("1a--"+(System.currentTimeMillis()-t));
		webSocketClient.connect();
		
		//CacheService.dumpDateRecording();
		/**
		 * Gives back the last 5 prices
		 */
		
		//TDoubleList d = CacheService.getItemsFromDateDataRecord_Test(12345, 5);
		//CacheService.addDateRecordingCache(data);
		
		
		System.out.println();
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	private ByteBuffer message(){
		ByteBuffer byteBuffer = ByteBuffer.allocate(1);
		byteBuffer.put(0,(byte)0xFF);
		return byteBuffer;
	}
	
	private TDoubleList initialSetup(){
		TDoubleList list = new TDoubleArrayList();
		list.add(Double.valueOf(DataFeed.incrementTime()));
		
		String[] values = ApplicationHelper.subscribeValues();
		for(int i=0;i<values.length;i++){
			list.add(Double.valueOf(values[i]));
		}
		
		return list;
	}
	
	private CacheMetaData metadata(){
		List<String> metadata = new ArrayList<String>();
		metadata.add(String.valueOf(DataFeed.START_TIME_STRING));
		return new CacheMetaData(metadata);
	}

}
