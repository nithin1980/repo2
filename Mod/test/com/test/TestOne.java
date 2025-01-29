package com.test;

import java.nio.ByteBuffer;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.mod.datafeeder.DataFeed;
import com.mod.interfaces.KiteGeneralWebSocketClient;
import com.mod.interfaces.KiteStockConverter;
import com.mod.objects.CacheMetaData;
import com.mod.objects.PositionalData;
import com.mod.process.models.CacheService;
import com.mod.support.ApplicationHelper;
import com.mod.support.ConfigData;
import com.mod.support.OpenHighLowSupport;
import com.mod.support.XMLParsing;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

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
		
		configData = XMLParsing.readAppConfig("C:/Users/nkumar/git/repo1/master/Mod/resource/pmodel6.config");
		ApplicationHelper.Application_Config_Cache.put("pmodel6", configData);

		configData = XMLParsing.readAppConfig("C:/Users/nkumar/git/repo1/master/Mod/resource/pmodel7.config");
		ApplicationHelper.Application_Config_Cache.put("pmodel7", configData);
		
		KiteStockConverter.build();
		
//		CacheService.clearDateDataRecord();
//		CacheService.addMetaDataToDateRecording("group1", metadata());
//		CacheService.initializeDataArray(initialSetup());
		/**
		 * Need to intialise the price backup thread as well.
		 */
		
//		CacheService.getMetaDataToDateRecording("group1");
		
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
		webSocketClient.connect();
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
		
//		CacheService.dumpDateRecording();
		/**
		 * Gives back the last 5 prices
		 */
		
		//TDoubleList d = CacheService.getItemsFromDateDataRecord_Test(12616706.0, 5);
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

	
	public static void main(String[] args) {
		double posCost=50000;
		
		List<Depths> depths = new ArrayList<Depths>();
		depths.add(new Depths(220.3, 200));
		depths.add(new Depths(221.3, 100));
		depths.add(new Depths(223.3, 300));
		depths.add(new Depths(224.3, 200));
		depths.add(new Depths(225.3, 400));
		depths.add(new Depths(226.3, 100));
		
		double price = 0.00;
		long qt = 0;
		
		double totalval = 0;
		long totalqt = 0;
		
		/**
		 * If overall is less the posCost, then keep taking it.
		 */
		
		for(int i=0;i<depths.size();i++) {
			System.out.println(totalqt+" -"+totalval);
			price = depths.get(i).getPrice();
			qt = depths.get(i).getQuantity();

			if(totalval<posCost) {
				
				boolean added =  false;

				if(((price*qt)+totalval) <=posCost) {
					
					totalval =  totalval+ (price*qt);
					totalqt = totalqt+qt;
					added =  true;
					
					//place order
					
				}
				
				/**
				 * If the present depth value + previous ones are greater than posCost
				 */
				if( ((price*qt)+totalval)>posCost && !added){
					
					double remaining = posCost-totalval;
					long pur = Double.valueOf(remaining).longValue()/Double.valueOf(price).longValue();
					totalqt = totalqt+pur;
					totalval =totalval+ (price*pur);
					
					//place order
					
				}
				
				
				

			}
			

		}
		
		

	}
	
	
	static void buySLCalculation(PositionalData position,double currentPrice,double buyPrice, double profitZone,double defaultSL) {
		
		double priceDiffPer = ApplicationHelper.percen(buyPrice,currentPrice);
		
		double currentSL = position.getExpectedSL(); //get this from the position api
		boolean changeSL=true;
		double newSL=0;
		
		
		if(priceDiffPer>=profitZone) {
			newSL = currentPrice *(1+(defaultSL/100)) ;
			
			if(newSL<=currentSL) {
				//no need to change the SL
				changeSL=true;
				position.setExpectedSL(newSL);
			}
			
			//System.out.println("Profit zone: CurrentSL="+currentSL+" buyprice:"+buyPrice+" currentprice:"+currentPrice+" newsl:"+newSL+" changing sl:"+changeSL);
			
		}
		
		else if(priceDiffPer<(-defaultSL)) {
			//newSL = buyPrice *(1-(defaultSL/100));
			//we wait for 15 mnts....
			changeSL=false;
			//--System.out.println(modelid()+" "+position.getKey()+" Below SL zone:");
			//System.out.println("Below SL zone: CurrentSL="+currentSL+" buyprice:"+buyPrice+" currentprice:"+currentPrice+" newsl:"+newSL);
		}
		
		System.out.println(position.getExpectedSL());
		
	}
	
}
