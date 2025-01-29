package com.test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import com.mod.process.models.BNFOptionSellingWithBuy;
import com.mod.process.models.CacheService;
import com.mod.process.models.dashboard.BNFOptionSellingWithBuyDashboard;
import com.mod.support.ApplicationHelper;
import com.mod.support.ConfigData;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.OrderParams;

public class TestBNFOptionSellingWithBuy {
	
	static List<String> ce_list;
	static List<String> pe_list;
	
	static BNFOptionSellingWithBuy batch;
	
	@Before
	public void before() {
		System.out.println("triggering");
		ApplicationHelper.botInitialSetup();

		ConfigData configData =  ApplicationHelper.Application_Config_Cache.get("bnfsellwithbuy");
		ce_list =  configData.getReferenceDataMap().get("ce_list");
		pe_list = configData.getReferenceDataMap().get("pe_list");
		
		batch = BNFOptionSellingWithBuy.getInstance();
		batch.previousBNFClose=36650;
		batch.startbotTime=System.currentTimeMillis();

	}
	
	@Test
	public void testStdFlow() {
		
		CacheService.getInstance().PRICE_LIST.put(new Long(138258692), 35.00);
		
		//TestKiteConnectMock.triggerPositionFailure=false;
		//TestKiteConnectMock.triggerFinalSLOrderFailure=true;
		
		for(int i=0;i<18;i++) {
			
			incrBN();
			batch.processNow();
		}
		
		/**
		 * Need asynch increment....
		 * to test the delay in order creation.
		 */
		
		/**
		 * Setting the right price for the limits.
		 * need a way to run it under test and in real.
		 */

		try {
			Thread.currentThread().sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		new Thread(
			
			new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					incrBN();
					incrCall();
					batch.processNow();
				}
			}
				
		).start();
		

		//------------------------------------------------------------
		try {
			Thread.currentThread().sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		try {
//			System.out.println("Positions:"+ApplicationHelper.getKiteSDK().getPositions().get("day"));
//		} catch (JSONException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (KiteException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
	
		new Thread(
			
			new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					decBN();
					incrPut();
					batch.processNow();
				}
			}
				
		).start();
		//---------------------------------------------------------------
		

		//------------------------------------------------------------
		try {
			Thread.currentThread().sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		new Thread(
			
			new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					incrBN();
					incrCall();
					batch.processNow();
				}
			}
				
		).start();
		//---------------------------------------------------------------
		
		
		
		for(int i=0;i<150;i++) {
			try {
				Thread.currentThread().sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			final int val=i;
			
			new Thread(
					
					new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(val%3==0) {
								decBN();
								incrPut();
							}else {
								incrBN();
								incrCall();								
							}

							batch.processNow();
						}
					}
						
				).start();			
			
			
		}
		
		//CacheService.positionalData.get(0)
		
		
		//System.out.println("Order Placed:"+TestKiteConnectMock.orderSystem);
		
		Iterator<Long> keys =  TestKiteConnectMock.orderSystem.keySet().iterator();
		
		while(keys.hasNext()) {
			Order params =  TestKiteConnectMock.orderSystem.get(keys.next());
			
			System.out.println("Orders:"+params.status+","+params.price+","+params.transactionType+","+params.quantity);
		}
		
		
		System.out.println(BNFOptionSellingWithBuyDashboard.getInstance().info);  
		
		//dump prices in to this list....
		
		
		/***
		 * VERIFY ORDER CREATION FAILURE. WHAT IF ONE OF THE ORDER FAILS, while others are sucessful.
		
		 */
		
		System.out.println(TestKiteConnectMock.positions.size()+":"+TestKiteConnectMock.orderSystem.size());
		
	}
	
	private void incrBN() {
		double previous= 36650;
		if( CacheService.PRICE_LIST.get(CacheService.BN_KEY) == null) {
			CacheService.PRICE_LIST.put(CacheService.BN_KEY, previous-30);
			previous = previous-30;
			//System.out.println(key+":"+CacheService.PRICE_LIST.get(key));
		}else {
			Double val = CacheService.PRICE_LIST.get(CacheService.BN_KEY);
			CacheService.PRICE_LIST.put(CacheService.BN_KEY, val+15);
		}
	}
	
	private void decBN() {
		double previous= 36650;
		if( CacheService.PRICE_LIST.get(CacheService.BN_KEY) == null) {
			CacheService.PRICE_LIST.put(CacheService.BN_KEY, previous+30);
			previous = previous-30;
			//System.out.println(key+":"+CacheService.PRICE_LIST.get(key));
		}else {
			Double val = CacheService.PRICE_LIST.get(CacheService.BN_KEY);
			CacheService.PRICE_LIST.put(CacheService.BN_KEY, val-15);
		}
		
	}
	
	private void incrCall() {
		
		double previous= 750.21;
		
		for(int i=ce_list.size()-1;i>=0;i--) {
			
			Long key = Long.valueOf(ce_list.get(i));
			
			if( CacheService.PRICE_LIST.get(key) == null) {
				CacheService.PRICE_LIST.put(key, previous-30);
				previous = previous-30;
				//System.out.println(key+":"+CacheService.PRICE_LIST.get(key));
			}else {
				Double val = CacheService.PRICE_LIST.get(key);
				CacheService.PRICE_LIST.put(key, val+4+i);
				//System.out.println(key+":"+CacheService.PRICE_LIST.get(key));
			}
			
		}

		//System.out.println(CacheService.PRICE_LIST);
		previous = 30.0;
		for(int i=0;i<pe_list.size();i++) {
			
			Long key = Long.valueOf(pe_list.get(i));
			
			if( CacheService.PRICE_LIST.get(key) == null) {
				CacheService.PRICE_LIST.put(key, previous+30);
				previous = previous+30;
			//	System.out.println(key+":"+CacheService.PRICE_LIST.get(key));
			}else {
				Double val = CacheService.PRICE_LIST.get(key);
				CacheService.PRICE_LIST.put(key, val-4-i);
			//	System.out.println(key+" "+i+":"+CacheService.PRICE_LIST.get(key));
			}
			
		}
		
		
	}
	
	private void incrPut() {

		double previous = 30.0;
		for(int i=0;i<pe_list.size();i++) {
			
			Long key = Long.valueOf(pe_list.get(i));
			
			if( CacheService.PRICE_LIST.get(key) == null) {
				CacheService.PRICE_LIST.put(key, previous+30);
				previous = previous+30;
			//	System.out.println(key+":"+CacheService.PRICE_LIST.get(key));
			}else {
				Double val = CacheService.PRICE_LIST.get(key);
				CacheService.PRICE_LIST.put(key, val+4+i);
			//	System.out.println(key+" "+i+":"+CacheService.PRICE_LIST.get(key));
			}
			
		}
		

		previous= 750.21;
		
		for(int i=ce_list.size()-1;i>=0;i--) {
			
			Long key = Long.valueOf(ce_list.get(i));
			
			if( CacheService.PRICE_LIST.get(key) == null) {
				CacheService.PRICE_LIST.put(key, previous-30);
				previous = previous-30;
			//	System.out.println(key+":"+CacheService.PRICE_LIST.get(key));
			}else {
				Double val = CacheService.PRICE_LIST.get(key);
				CacheService.PRICE_LIST.put(key, val-4-i);
			//	System.out.println(key+":"+CacheService.PRICE_LIST.get(key));
			}
			
		}
		

	}


}
