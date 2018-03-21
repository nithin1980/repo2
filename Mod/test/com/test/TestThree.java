package com.test;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.mod.datafeeder.DataFeed;
import com.mod.interfaces.KiteStockConverter;
import com.mod.objects.CacheMetaData;
import com.mod.process.models.CacheService;
import com.mod.process.models.DashBoard;
import com.mod.process.models.ProcessingBlock6;
import com.mod.process.models.ProcessingBlock7;
import com.mod.process.models.ProcessingBlock8;
import com.mod.support.ApplicationHelper;
import com.mod.support.ConfigData;
import com.mod.support.XMLParsing;

public class TestThree {
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
		
		configData = XMLParsing.readAppConfig("C:/Users/nkumar/git/repo1/master/Mod/resource/pmodel6.config");
		ApplicationHelper.Application_Config_Cache.put("pmodel6", configData);

		configData = XMLParsing.readAppConfig("C:/Users/nkumar/git/repo1/master/Mod/resource/pmodel7.config");
		ApplicationHelper.Application_Config_Cache.put("pmodel7", configData);
		
		KiteStockConverter.build();
		
		
		
		TestCacheService.clearDateDataRecord();
		TestCacheService.addMetaDataToDateRecording("group1", metadata());
		TestCacheService.initializeDataArray(initialSetup());
		/**
		 * Need to intialise the price backup thread as well.
		 */
		
		TestCacheService.getMetaDataToDateRecording("group1");
		
		long t = System.currentTimeMillis();
		ProcessingBlock8 block8 = new ProcessingBlock8(TestCacheService.getInstance());
		for(int i=1;i<4711;i++){
			
			TestCacheService.PRICE_LIST.put(256265.0, CacheService.getValueForIndex(256265.0, i));
			System.out.println(CacheService.getValueForIndex(256265.0, i));
			TestCacheService.PRICE_LIST.put(10377730.0, CacheService.getValueForIndex(10377730.0, i));
			TestCacheService.PRICE_LIST.put(10118146.0, CacheService.getValueForIndex(10118146.0, i));
			
			TestCacheService.addDateRecordingCache();
			TestCacheService.updateNiftyTrend(256265.0);
			
			if(block8.completedProcess){
				block8.processNow();
			}
			
			/**
			 * This should read from the latest prices....
			 */
		}
		//System.out.println("1a--"+(System.currentTimeMillis()-t));
//		KiteGeneralWebSocketClient webSocketClient = new KiteGeneralWebSocketClient(latch);
//		webSocketClient.connect();
		
		
//		try {
//			for(int i=0;i<35;i++){
//				webSocketClient.onMessage(message(), null);
//				
//				Thread.sleep(300);
//			}
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		//TestCacheService.dumpDateRecording();
		/**
		 * Gives back the last 5 prices
		 */
		
		//TDoubleList d = TestCacheService.getItemsFromDateDataRecord_Test(12616706.0, 5);
		//TestCacheService.addDateRecordingCache(data);
		block8.close();
		System.out.println(DashBoard.positionMap.get("pmodel8"));
		System.out.println(DashBoard.positionMap.get("pmodel8").total());
//		try {
//			latch.await();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		
	}
	
	private ByteBuffer message(){
		
		byte[] d = new byte[]{0, 3, 0, 28, 0, 3, -23, 9, 0, 15, 123, 127, 0, 15, -107, 11, 0, 15, 114, 111, 0, 15, -108, 107, 0, 15, -108, -99, -1, -1, -1, -63, 0, 44, 0, -64, -113, 2, 0, 0, 39, 116, 0, 0, 0, 75, 0, 0, 30, 1, 0, 110, -87, 51, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 124, 0, 0, 41, -12, 0, 0, 19, -105, 0, 0, 16, -2, 0, 44, 0, -64, -124, 2, 0, 0, 55, 25, 0, 0, 0, 75, 0, 0, 67, -70, 0, 5, -72, 66, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 91, -62, 0, 0, 91, -62, 0, 0, 54, -45, 0, 0, 94, 111};
		ByteBuffer byteBuffer = ByteBuffer.wrap(modify(d));
		return byteBuffer;
	}
	private ByteBuffer message2(){
		
		byte[] d = new byte[]{0, 3, 0, 28,0, 3, -23, 9,0, 15, 121, 120, 0, 15, -107, 11, 0, 15, 114, 111, 0, 15, -108, 107, 0, 15, -108, -99, -1, -1, -1, -63,0, 44, 0, -64, -113, 2, 0, 0, 29, 36, 0, 0, 0, 75, 0, 0, 56, 1, 0, 110, -117, 51, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 21, 124, 0, 0, 41, -12, 0, 0, 19, -105, 0, 0, 116, -2, 0, 44, 0, -64, -104, 2, 0, 0, 55, 25, 0, 0, 0, 75, 0, 0, 67, -70, 0, 5, -72, 66, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 91, -62, 0, 0, 101, -62, 0, 0, 54, -45, 0, 0, 94, 35};

		ByteBuffer byteBuffer = ByteBuffer.wrap(d);
		return byteBuffer;
	}
	
	private byte[] modify(byte[] data){
		
		ThreadLocalRandom random = ThreadLocalRandom.current();
		//nifty
		data[10]=Byte.valueOf(String.valueOf(random.nextInt(120, 126)));
		data[11]=Byte.valueOf(String.valueOf(random.nextInt(90, 127)));
		//data[11] = 110;
		
		
		//pe
		data[40]=Byte.valueOf(String.valueOf(random.nextInt(27, 31)));
		data[41]=Byte.valueOf(String.valueOf(random.nextInt(15, 46)));;
		
		//ce
		data[86]=Byte.valueOf(String.valueOf(random.nextInt(53, 57)));;
		data[87]=Byte.valueOf(String.valueOf(random.nextInt(10, 35)));
		
		return data;
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
