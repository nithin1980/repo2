package com.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.mod.objects.DayCandles;
import com.mod.process.models.BollingerBandModel1;
import com.mod.process.models.CacheService;
import com.mod.support.ApplicationHelper;
import com.mod.support.Candle;
import com.mod.support.ConfigData;
import com.mod.support.XMLParsing;

import static com.mod.process.models.CacheService.PRICE_LIST;

public class TestBollingerBandModel1 {

	
	@Before
	public void dataSetup() {
		
		ConfigData configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/app.config");
		ApplicationHelper.Application_Config_Cache.put("app", configData);

		configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/genwsclient.config");
		ApplicationHelper.Application_Config_Cache.put("mode1", configData);
		
		configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/pmodel5.config");
		ApplicationHelper.Application_Config_Cache.put("pmodel5", configData);
		
		configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/pmodel6.config");
		ApplicationHelper.Application_Config_Cache.put("pmodel6", configData);

		configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/bbmodel1.config");
		ApplicationHelper.Application_Config_Cache.put("bbmodel1", configData);
		
		Double[] pricelist = {36398.25,36379.1,36383.45,36345.9,36343.07,36347.18,36334.49,36335.29,36347.32,36371.37,36382.03,36360.48,36333.08,36307.02,36333.8,36340.06,36334.18,36320.97,36340.95,36324.05,36347.6};
		
		DayCandles trackingCandles = new DayCandles(new Candle(), new Candle());
		LinkedList<Double> BB_Close_Records  = new LinkedList<Double>();
		BB_Close_Records.addAll(Arrays.asList(pricelist));
		trackingCandles.setBB_Close_Records(BB_Close_Records);
		BollingerBandModel1.trackingCandles.clear();
		BollingerBandModel1.trackingCandles.put(CacheService.BN_KEY, trackingCandles);
		
		PRICE_LIST.clear();

		
		
	}
	
	public void bbCal() {
		double[] pricelist = {397.38,383.02,349.73,345.9,390.59,427.63,386.75,398.25,379.1,383.45,345.9,343.07,347.18,334.49,335.29,347.32,371.37,382.03,360.48,333.08,307.02,333.8,340.06,334.18,320.97,340.95,324.05,347.6};
		
		
		List<Double> dt = new ArrayList<Double>();
		for (int i=0;i<8;i++) {
			for(int j=i;j<(21+i);j++) {
				dt.add(pricelist[j]);
				
				
			}
			System.out.println(ApplicationHelper.bbHighCalculation(dt.toArray(new Double[dt.size()])));
			//System.out.println(dt);
			dt.clear();
		}
		LinkedList<Double> link = new LinkedList<Double>();
		link.pollFirst();
		link.addLast(23.45);
	}
	
	@Test
	public void testProcessLongPosition() {
		//fail("Not yet implemented");
		
		BollingerBandModel1 model1 = new BollingerBandModel1(CacheService.getInstance());
		
		DayCandles daycandles =  model1.trackingCandles.get(CacheService.BN_KEY);
		
		PRICE_LIST.clear();
		
		for(int i=0;i<30;i++) {
			
			
			try {
				System.out.println("waiting.."+i);
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			if(i==5) {
				assertEquals(Double.valueOf(36402.2), Double.valueOf(daycandles.getPreviousCandle().getLow()));
			}
			if(i==9) {
				assertEquals(Double.valueOf(36399.2), Double.valueOf(daycandles.getPreviousCandle().getLow()));
			}
			if(i==13) {
				assertEquals(Double.valueOf(36395.2), Double.valueOf(daycandles.getPreviousCandle().getLow()));
			}
			if(i==17) {
				assertEquals(Double.valueOf(36407.2), Double.valueOf(daycandles.getPreviousCandle().getLow()));
			}
			if(i==21) {
				assertEquals(Double.valueOf(36411.2), Double.valueOf(daycandles.getPreviousCandle().getLow()));
			}
			if(i==25) {
				assertEquals(Double.valueOf(36415.2), Double.valueOf(daycandles.getPreviousCandle().getLow()));
			}
			if(i==29) {
				assertEquals(Double.valueOf(36423.2), Double.valueOf(daycandles.getPreviousCandle().getLow()));
			}
			
			if(i<4) {
				PRICE_LIST.put(CacheService.BN_KEY, 36402.20+i);
			}else if(i>5 && i<12) {
				PRICE_LIST.put(CacheService.BN_KEY, 36406.20-i);
			}else if(i>12 && i<25) {
				PRICE_LIST.put(CacheService.BN_KEY, 36394.20+i);
			}else if(i>=25) {
				PRICE_LIST.put(CacheService.BN_KEY, 36450.20-i);
			}
			
			PRICE_LIST.put(new Long(276269), 130.20+i);
			
			if(i>19 & i<28) {
				PRICE_LIST.put(new Long(276269), 169.20-i);
			}

			
			
			model1.processNow();
			
			
		}
		
	}
	
	@Test
	public void testProcessShortPosition() {
		//fail("Not yet implemented");
		
		BollingerBandModel1 model1 = new BollingerBandModel1(CacheService.getInstance());
		
		PRICE_LIST.clear();
		
		for(int i=0;i<30;i++) {
			
			
			try {
				System.out.println("waiting.."+i);
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			if(i<=4) {
				PRICE_LIST.put(CacheService.BN_KEY, 36302.20-i);
			}else if(i>=5 && i<12) {
				PRICE_LIST.put(CacheService.BN_KEY, 36298.20+i);
			}else if(i>12) {
				PRICE_LIST.put(CacheService.BN_KEY, 36302.20-i);
			}
			
			
			PRICE_LIST.put(new Long(276269), 110.20-i);
			
			if(i>19 & i<28) {
				PRICE_LIST.put(new Long(276269), 71.20+i);
			}

			
			model1.processNow();
			
			
		}
		
	}
	@Test
	public void testAlertCandleSet_ThenPriceDrops() {

		BollingerBandModel1 model1 = new BollingerBandModel1(CacheService.getInstance());
		
		DayCandles daycandles =  model1.trackingCandles.get(CacheService.BN_KEY);
		
		for(int i=0;i<40;i++) {
			
					
			try {
				System.out.println("waiting.."+i);
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//System.out.println("day canclde low:"+daycandles.getPreviousCandle().getLow());
			if(i==5) {
				
				assertEquals(Double.valueOf(36402.2), Double.valueOf(daycandles.getPreviousCandle().getLow()));
			}
//			if(i==9) {
//				assertEquals(Double.valueOf(36399.2), Double.valueOf(daycandles.getPreviousCandle().getLow()));
//			}
//			if(i==13) {
//				assertEquals(Double.valueOf(36395.2), Double.valueOf(daycandles.getPreviousCandle().getLow()));
//			}
//			if(i==17) {
//				assertEquals(Double.valueOf(36407.2), Double.valueOf(daycandles.getPreviousCandle().getLow()));
//			}
//			if(i==21) {
//				assertEquals(Double.valueOf(36411.2), Double.valueOf(daycandles.getPreviousCandle().getLow()));
//			}
//			if(i==25) {
//				assertEquals(Double.valueOf(36415.2), Double.valueOf(daycandles.getPreviousCandle().getLow()));
//			}
//			if(i==29) {
//				assertEquals(Double.valueOf(36423.2), Double.valueOf(daycandles.getPreviousCandle().getLow()));
//			}
			
			if(i<4) {
				PRICE_LIST.put(CacheService.BN_KEY, 36402.20+i);
			}else if(i>5 && i<12) {
				PRICE_LIST.put(CacheService.BN_KEY, 36406.20-i);
			}else if(i>12 && i<25) {
				PRICE_LIST.put(CacheService.BN_KEY, 36394.20-i);
			}else if(i>=25) {
				PRICE_LIST.put(CacheService.BN_KEY, 36450.20-i);
			}
			
			PRICE_LIST.put(new Long(276269), 130.20+i);
			
			if(i>19 & i<28) {
				PRICE_LIST.put(new Long(276269), 169.20-i);
			}
			
			model1.processNow();
			
		}
		
		
	}
	
	@Test
	public void testMulitplSL_NOTDONE() {
		
	}
	
	@Test
	public void testSLTrailing_For_ShortPosition_NOTDONE() {
		
	}
	@Test
	public void testSLPreviousCandle_Low() {
		
		BollingerBandModel1 model1 = new BollingerBandModel1(CacheService.getInstance());
		
		DayCandles daycandles =  model1.trackingCandles.get(CacheService.BN_KEY);
		
		for(int i=0;i<40;i++) {
			
					
			try {
				System.out.println("waiting.."+i);
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//System.out.println("day canclde low:"+daycandles.getPreviousCandle().getLow());
			if(i==5) {
				
				assertEquals(Double.valueOf(36402.2), Double.valueOf(daycandles.getPreviousCandle().getLow()));
			}
			if(i==9) {
				assertEquals(Double.valueOf(36399.2), Double.valueOf(daycandles.getPreviousCandle().getLow()));
			}
			if(i==13) {
				assertEquals(Double.valueOf(36395.2), Double.valueOf(daycandles.getPreviousCandle().getLow()));
			}
			if(i==17) {
				assertEquals(Double.valueOf(36407.2), Double.valueOf(daycandles.getPreviousCandle().getLow()));
			}
			if(i==21) {
				assertEquals(Double.valueOf(36411.2), Double.valueOf(daycandles.getPreviousCandle().getLow()));
			}
			if(i==25) {
				assertEquals(Double.valueOf(36415.2), Double.valueOf(daycandles.getPreviousCandle().getLow()));
			}
			if(i==29) {
				assertEquals(Double.valueOf(36423.2), Double.valueOf(daycandles.getPreviousCandle().getLow()));
			}
			
			if(i<4) {
				PRICE_LIST.put(CacheService.BN_KEY, 36402.20+i);
			}else if(i>5 && i<12) {
				PRICE_LIST.put(CacheService.BN_KEY, 36406.20-i);
			}else if(i>12 && i<25) {
				PRICE_LIST.put(CacheService.BN_KEY, 36394.20+i);
			}else if(i>=25) {
				PRICE_LIST.put(CacheService.BN_KEY, 36450.20-i);
			}
			
			PRICE_LIST.put(new Long(276269), 130.20+i);
			
			if(i>19 & i<28) {
				PRICE_LIST.put(new Long(276269), 169.20-i);
			}
			
			model1.processNow();
			
			
			
			
		}
		
	}
	

	@Test
	public void testLargeAlertCandle_NOTDONE() {
		//we need to be careful with large alert candle, as any position entry 
		// after a large move tends to go down..
	}
	
	@Test
	public void testRemovePositionAfterSL_NOTDONE() {
		
	}
	@Test
	public void testPostSLHit_NOTDONE() {
		
	}
	
	public static void main(String[] args) {
		Double val =  new Double(23.45);
		System.out.println(Long.valueOf(val.toString()));
	}

}
