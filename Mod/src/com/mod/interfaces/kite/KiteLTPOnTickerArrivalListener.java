package com.mod.interfaces.kite;

import static com.mod.process.models.CacheService.PRICE_LIST;
import static com.mod.process.models.CacheService.positionalData;
import static com.mod.support.ApplicationHelper.LogLevel2;

import java.util.ArrayList;
import java.util.Iterator;

import com.mod.objects.PositionalData;
import com.mod.process.models.BNFOptionSellingWithBuy;
import com.mod.process.models.CacheService;
import com.mod.process.models.OpenHighLowModel;
import com.mod.process.models.TopBottomLongModel;
import com.mod.support.Candle;
import com.mod.support.CandleWrapper;
import com.zerodhatech.models.Tick;
import com.zerodhatech.ticker.OnTicks;

public class KiteLTPOnTickerArrivalListener implements OnTicks {
	
	private static final OpenHighLowModel openHighModel  =  OpenHighLowModel.getInstance();
	
	private static final BNFOptionSellingWithBuy optionSellingWithBuy = BNFOptionSellingWithBuy.getInstance();
	
	static int count=0;

	@Override
	public void onTicks(ArrayList<Tick> ticks) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		
		count = count+1;
		
		boolean runHighLow=false;
		boolean runOptionSelling=false;
		
		if(ticks!=null && ticks.size()>0) {
			Tick individualTick = null;
			Iterator<Tick> itr = ticks.iterator();
			Long tickerId;
			
			
			CandleWrapper wrapper = null;
			
			try {
				while(itr.hasNext()) {
					individualTick = itr.next();
					tickerId = individualTick.getInstrumentToken();
				
					
					PRICE_LIST.put(tickerId, individualTick.getLastTradedPrice());
					
					
					if(optionSellingWithBuy.trackPrices!=null &&
							optionSellingWithBuy.trackPrices.contains(tickerId)) {
						
						runOptionSelling = true;
					}
					
					
					
					if(OpenHighLowModel.trackPrices!=null 
							&& OpenHighLowModel.trackPrices.contains(tickerId)) {
					//	System.out.println(tickerId+":"+individualTick.getLastTradedPrice());
						
						runHighLow=true;
						
						if(CacheService.candleData.containsKey(tickerId)) {
							wrapper = CacheService.candleData.get(tickerId);
							
							if(wrapper==null) {
								CandleWrapper wrapdata = new CandleWrapper(tickerId, new Candle(individualTick.getChange()));
								wrapdata.getCandle().setOpen(individualTick.getOpenPrice());
								wrapdata.getCandle().setHigh(individualTick.getHighPrice());
								wrapdata.getCandle().setLow(individualTick.getLowPrice());
								wrapdata.getCandle().setClose(individualTick.getClosePrice());
								CacheService.candleData.put(tickerId, wrapdata);
								
								
							}else {
								wrapper.getCandle().setChange(individualTick.getChange());
								if(wrapper.getCandle().getOpen()==0) {
									wrapper.getCandle().setOpen(individualTick.getOpenPrice());
								}
								wrapper.getCandle().setHigh(individualTick.getHighPrice());
								wrapper.getCandle().setLow(individualTick.getLowPrice());
								wrapper.getCandle().setClose(individualTick.getClosePrice());
								CacheService.candleData.put(tickerId, wrapper);
								
								
							}
						}else {
							CandleWrapper wrapdata = new CandleWrapper(tickerId, new Candle(individualTick.getChange()));
							wrapdata.getCandle().setOpen(individualTick.getOpenPrice());
							wrapdata.getCandle().setHigh(individualTick.getHighPrice());
							wrapdata.getCandle().setLow(individualTick.getLowPrice());
							wrapdata.getCandle().setClose(individualTick.getClosePrice());
							CacheService.candleData.put(tickerId, wrapdata);
							
							
						}
					}

					
					

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(OpenHighLowModel.startbotTime==0) {
				System.out.println("KiteLTP:count:"+count);
			}
			
			if(count>=3 && OpenHighLowModel.startbotTime==0) {
				System.out.println("Setting bot time:"+System.currentTimeMillis());
				OpenHighLowModel.startbotTime=System.currentTimeMillis();
			}

			if(count>=3 && optionSellingWithBuy.startbotTime==0) {
				System.out.println("Setting Buy bot time:"+System.currentTimeMillis());
				BNFOptionSellingWithBuy.startbotTime=System.currentTimeMillis();
			}
			
			System.out.println("-------------------------------");
			if(runHighLow) {
				openHighModel.processNow();
			}
			
			if(runOptionSelling) {
				//optionSellingWithBuy.processNow();
			}
			
			//bollingerBand1.processNow();
		}
		

			

	}

}
