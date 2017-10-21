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
import com.mod.interfaces.KiteStockConverter;
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
		
		
		ConfigData configData = XMLParsing.readAppConfig("C:/Users/nkumar/git/repo1/master/Mod/resource/app.config");
		ApplicationHelper.Application_Config_Cache.put("app", configData);

		configData = XMLParsing.readAppConfig("C:/Users/nkumar/git/repo1/master/Mod/resource/genwsclient.config");
		ApplicationHelper.Application_Config_Cache.put("mode1", configData);
		
		configData = XMLParsing.readAppConfig("C:/Users/nkumar/git/repo1/master/Mod/resource/pmodel5.config");
		ApplicationHelper.Application_Config_Cache.put("pmodel5", configData);
		
		KiteStockConverter.build();
		
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
		KiteGeneralWebSocketClient webSocketClient = new KiteGeneralWebSocketClient(latch);
		//webSocketClient.connect();
		webSocketClient.onMessage(message2(), null);
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
		
		byte[] d = new byte[]{0, 3, 0, 28, 0, 3, -23, 9, 0, 15, 123, 127, 0, 15, -107, 11, 0, 15, 114, 111, 0, 15, -108, 107, 0, 15, -108, -99, -1, -1, -1, -63, 0, 44, 0, -64, -113, 2, 0, 0, 39, 116, 0, 0, 0, 75, 0, 0, 30, 1, 0, 110, -87, 51, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 124, 0, 0, 41, -12, 0, 0, 19, -105, 0, 0, 16, -2, 0, 44, 0, -64, -124, 2, 0, 0, 55, 25, 0, 0, 0, 75, 0, 0, 67, -70, 0, 5, -72, 66, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 91, -62, 0, 0, 91, -62, 0, 0, 54, -45, 0, 0, 94, 111};
		ByteBuffer byteBuffer = ByteBuffer.wrap(d);
		return byteBuffer;
	}
	private ByteBuffer message2(){
		
		byte[] d = new byte[]{0, 3, 0, 28, 0, 3, -23, 9, 0, 15, 123, 127, 0, 15, -107, 11, 0, 25, 11, 101, -107, 15, -108, 117, 0, 15, -108, -99, -1, -1, -1, -63, 0, 44, 0, -64, -113, 2, 0, 0, 29, 36, 0, 0, 0, 75, 0, 0, 56, 1, 0, 110, -117, 51, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 124, 0, 0, 41, -12, 0, 0, 19, -105, 0, 0, 116, -2, 0, 44, 0, -64, -104, 2, 0, 0, 55, 25, 0, 0, 0, 75, 0, 0, 67, -70, 0, 5, -72, 66, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 91, -62, 0, 0, 101, -62, 0, 0, 54, -45, 0, 0, 94, 35};

		ByteBuffer byteBuffer = ByteBuffer.wrap(d);
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
