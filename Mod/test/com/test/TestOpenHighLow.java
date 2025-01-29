package com.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mod.interfaces.KitePositionQueryResponse;
import com.mod.objects.EnumPositionStatus;
import com.mod.objects.KitePositionDataLayer2;
import com.mod.objects.PositionalData;
import com.mod.process.models.CacheService;
import com.mod.process.models.OpenHighLowModel;
import com.mod.support.ApplicationHelper;
import com.mod.support.Candle;
import com.mod.support.CandleWrapper;
import com.mod.support.ConfigData;
import com.mod.support.OpenHighLowSupport;
import com.mod.support.XMLParsing;

public class TestOpenHighLow {
	
	private static final double expectedSLPercen = 1-(2.5/100);
	
	@Before
	public void before() {
		System.out.println("triggering");
//		ConfigData configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/app.config");
//		ApplicationHelper.Application_Config_Cache.put("app", configData);
//
//		configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/genwsclient.config");
//		ApplicationHelper.Application_Config_Cache.put("mode1", configData);
//		
//		configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/pmodel5.config");
//		ApplicationHelper.Application_Config_Cache.put("pmodel5", configData);
//		
//		configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/pmodel6.config");
//		ApplicationHelper.Application_Config_Cache.put("pmodel6", configData);
//
//		configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/topbottom1.config");
//		ApplicationHelper.Application_Config_Cache.put("topbottom1", configData);
//		
//		configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/openhl.config");
//		ApplicationHelper.Application_Config_Cache.put("openhl", configData);
//		
//		List<OpenHighLowSupport> data =  ApplicationHelper.getStockFutureData("C:/Users/Vihaan/git/repo1/Mod/resource/stockfuturedata.config");
//		if(data!=null && data.size()>0) {
//			Iterator<OpenHighLowSupport> itr =  data.iterator();
//			OpenHighLowSupport support = null;
//			while(itr.hasNext()) {
//				support = itr.next();
//				CacheService.stockFutureData.put(support.getStock(), support);
//			}
//		}
		
		ApplicationHelper.botInitialSetup();
		
	}
	
	@Test
	public void testSomething() {
		OpenHighLowModel model = OpenHighLowModel.getInstance();
		model.startbotTime=System.currentTimeMillis();
		
		try {
			Thread.sleep(7000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		CandleWrapper wrapper = new CandleWrapper(415745, new Candle(null, "21", "21", "19", "20.5"));
		wrapper.getCandle().setChange(-0.3);
		
		CacheService.candleData.put(Long.valueOf(415745), wrapper);
		CacheService.PRICE_LIST.put(Long.valueOf(415745), 20.20);
		
		
		CandleWrapper wrapper2 = new CandleWrapper(2977281, new Candle(null, "20", "21", "20", "20.5"));
		CacheService.candleData.put(Long.valueOf(2977281), wrapper2);
		CacheService.PRICE_LIST.put(Long.valueOf(2977281), 20.20);
		
		wrapper2 = new CandleWrapper(21099266, new Candle(null, "20", "20", "15", "20.5"));
		CacheService.PRICE_LIST.put(Long.valueOf(21099266), 20.30);
		model.processNow();

		CacheService.PRICE_LIST.put(Long.valueOf(2977281), 20.30);
		CacheService.PRICE_LIST.put(Long.valueOf(415745), 20.10);
		model.processNow();
		
		CacheService.PRICE_LIST.put(Long.valueOf(2977281), 20.40);
		CacheService.PRICE_LIST.put(Long.valueOf(415745), 20.00);
		model.processNow();

		CacheService.PRICE_LIST.put(Long.valueOf(2977281), 20.50);
		CacheService.PRICE_LIST.put(Long.valueOf(415745), 20.50);
		model.processNow();

		CacheService.PRICE_LIST.put(Long.valueOf(2977281), 20.60);
		CacheService.PRICE_LIST.put(Long.valueOf(415745), 20.70);
		model.processNow();
		
		
		
	}
	/**
	 * Reads from a file.....!
	 */
	public void resetData() {
		CacheService.positionalData.clear();
		
		Scanner reader = null;
	    List<Candle> candles = new ArrayList<Candle>();
		StringBuilder builder = new StringBuilder();
		try {
			File file = new File("C:/data/testdata.txt");
			
			reader = new Scanner(file);
			
			while(reader.hasNextLine()) {
				builder = builder.append(reader.nextLine());
			}
			
			KitePositionQueryResponse response =  ApplicationHelper.getObjectMapper().readValue(builder.toString(), KitePositionQueryResponse.class);
			
			
			if("success".equals(response.getStatus())) {
				KitePositionDataLayer2[] layer2 = response.getData().getNet();
				
				if(layer2==null || layer2.length==0) {
					throw new RuntimeException("No current position has been taken.Cannot run the TopBottom Model");
				}
				
				for(int i=0;i<layer2.length;i++) {
					PositionalData data = new PositionalData();
					data.setTradingSymbol(layer2[i].getTradingsymbol());
					data.setBuyPrice(layer2[i].getBuy_price());
					data.setBuyQuantity(layer2[i].getBuy_quantity());
					data.setKey(Long.valueOf(layer2[i].getInstrument_token()));
					data.setStatus(EnumPositionStatus.InPoistionLong);
					data.setCount(1);
					
					CacheService.positionalData.add(data);
				}
				
				System.out.println();
				
			}else {
				throw new RuntimeException("Could not get positional information:"+response.getStatus());
			}

			
			reader.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			
				if(reader!=null){
					reader.close();
				}
		}
		
		
	}


}
