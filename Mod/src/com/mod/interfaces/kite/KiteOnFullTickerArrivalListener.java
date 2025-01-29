package com.mod.interfaces.kite;

import java.util.ArrayList;
import java.util.Iterator;

import com.mod.enums.EnumPositionType;
import com.mod.objects.PositionalData;
import com.mod.process.models.BollingerBandModel1;
import com.mod.process.models.CacheService;
import com.mod.process.models.TopBottomLongModel;
import com.mod.support.ApplicationHelper;
import com.zerodhatech.models.Tick;
import com.zerodhatech.ticker.OnTicks;

import static com.mod.process.models.CacheService.PRICE_LIST;
import static com.mod.process.models.CacheService.positionalData;

import static com.mod.support.ApplicationHelper.LogLevel1;
import static com.mod.support.ApplicationHelper.LogLevel2;

public class KiteOnFullTickerArrivalListener implements OnTicks {
	
	private static final TopBottomLongModel topBottommodel =  TopBottomLongModel.getInstance();
	private static final BollingerBandModel1 bollingerBand1 =  new BollingerBandModel1(CacheService.getInstance());

	@Override
	public void onTicks(ArrayList<Tick> ticks) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		
		
		
		if(ticks!=null && ticks.size()>0) {
			Tick individualTick = null;
			Iterator<Tick> itr = ticks.iterator();
			Long tickerId;
			
			PositionalData position = null;
			
			while(itr.hasNext()) {
				individualTick = itr.next();
				tickerId = individualTick.getInstrumentToken();
				
				PRICE_LIST.put(tickerId, individualTick.getLastTradedPrice());
				
				
				
				
				try {
					
					Integer[] pos = CacheService.getInstance().findPositionsbyKey(tickerId,EnumPositionType.Both);
					if(pos!=null && pos.length>0) {
						
						for(int i=0;i<pos.length;i++) {
							if(LogLevel2) {
								System.out.println("Getting data for ticker--:"+individualTick.getInstrumentToken()+ " "+individualTick.getHighPrice()+" "+individualTick.getLowPrice());
							}
							
							position = positionalData.get(pos[i]);
							position.getDayCandle().setOpen(individualTick.getOpenPrice());
							position.getDayCandle().setHigh(individualTick.getHighPrice());
							position.getDayCandle().setLow(individualTick.getLowPrice());
							positionalData.set(pos[i], position);
						}
						
						
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			topBottommodel.processNow();
			//bollingerBand1.processNow();
		}
		

	}

}
