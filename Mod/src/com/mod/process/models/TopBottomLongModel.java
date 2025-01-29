package com.mod.process.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mod.enums.EnumPositionType;
import com.mod.enums.EnumStratergyPositionType;
import com.mod.enums.EnumStratergyType;
import com.mod.interfaces.BusinessInterface;
import com.mod.interfaces.SystemInterface;
import com.mod.objects.DayCandles;
import com.mod.objects.GroupPosition;
import com.mod.objects.Position;
import com.mod.objects.EnumPositionStatus;
import com.mod.objects.PositionalData;
import com.mod.objects.StopLossType;
import com.mod.support.ApplicationHelper;
import com.mod.support.Candle;
import com.test.inf.TestKiteInterface;

import static com.mod.support.ApplicationHelper.percen;
import static com.mod.support.ApplicationHelper.LogLevel1;
import static com.mod.support.ApplicationHelper.LogLevel2;

public class TopBottomLongModel extends ProcessModelAbstract {

	/***
	 * *******************Backtradder back testing library.***********
	 * @param cacheService
	 */
	
	/**
	 * @TODO
	 * Position: MIS and CnC will create two seperate position.
	 * Stock positions then get moved in to Holdings.
	 * If you get both buy and sell data, then the position is closed.. verify that
	 */
	
	private boolean ignorePreviousDaySL;
	private static boolean processStopped = false;
	
	private static final TopBottomLongModel model = new TopBottomLongModel(CacheService.getInstance());

	private static Map<Long,String> log_text = new HashMap<Long, String>();
	
	private TopBottomLongModel(CacheService cacheService) {
		super();
		stockList = new ArrayList<String>();
		setCacheService(cacheService);
		if(modeConfig().getKeyValueConfigs().containsKey("stock_list")) {
			String[] stockListString = modeConfig().getKeyValueConfigs().get("stock_list").split("\\,");
			
			if(stockListString==null || stockListString.length==0) {
				throw new RuntimeException(modelid()+" Cache is not loaded yet");
			}
			stockList.addAll(Arrays.asList(stockListString));
		}
		
	}    
    
	public static TopBottomLongModel getInstance() {
		return model;
	}
	
	@Override
	public String modelid() {
		
		return "topbottom1";
	}
	
	//public static final double defaultSL=2.50;
	//public static final double profitZone=2.00;
	private static List<String> stockList;
	
	 
	public static void activateProcess() {
		processStopped = false;
	}
	
	public static void stopProcess() {
		processStopped = true;
	}
	
	/**
	 * TODO System sell point calculation...
	 */
	@Override
	public void processNow() {
		
		if(processStopped) {
			return;
		}

		if(LogLevel1) {
			System.out.println(modelid()+" Processing topbottom long model");
		}
		
		completedProcess=false;
		
		PositionalData position = null;
		
		if(CacheService.positionalData.size()>0) {
			
			Iterator<PositionalData> itr =  CacheService.positionalData.iterator();
			
			int index = 0;
			
			while(itr.hasNext()) {
				position = itr.next();
				if(stockList.contains(String.valueOf(position.getKey()+"0"))) {
					
					if(LogLevel2) {
						System.out.println(modelid()+" processing stock: "+position.getKey());
					}
					/** 
					 * send the List position as well, so that it can be replaced with updated data.
					 */
					subtask(position, index);
				}
				
				index++;
				
			}
			
		}else {
			throw new RuntimeException("TopBottomModel cannot be processed due to missing position");
		}
		
		
//		if(stockList.size()>0) {
//			
//			
//			
//			try {
//				Iterator<String> itr =  stockList.iterator();
//				while(itr.hasNext()) {
//					subtask(Double.valueOf(itr.next()));
//				}
//			} finally {
//				
//				completedProcess=true;
//			}
//			
//		}else {
//			throw new RuntimeException("TopBottomModel cannot be processed due to missing stock list");
//		}
		
		
		
		
		
		completedProcess=true;
	}
	
	
	
	private void subtask(PositionalData position, int index) {
		//PositionalData position = CacheService.positionalData.get(positionid);
		
		if(position==null) {
			System.out.println(modelid()+" "+ position.getKey()+": No position for this identifier ");
			return;
			
		}
		
		checkforConfig(position);
		
		try {
			
			double currentPrice = getCacheService().PRICE_LIST.get(position.getKey());
			
			double buyPrice = position.getBuyPrice();
			double priceDiffPer = percen(currentPrice, buyPrice);
			double dayOpen = position.getDayCandle().getOpen();
			double dayLow = position.getDayCandle().getLow();
			
			double systemSellPoint=position.getSystemClosingPoint();
			
			double defaultSL = position.getConfigData().getSlPercen();
			double profitZone = position.getConfigData().getProfitPercen();
			
			if(position.getCurrentSL()>currentPrice 
					|| EnumPositionStatus.PositionClosed_WithBuy.equals(position.getStatus()) 
					|| EnumPositionStatus.PositionClosed_WithSell.equals(position.getStatus())) {
				if(!EnumPositionStatus.PositionClosed_WithBuy.equals(position.getStatus()) &&
						!EnumPositionStatus.PositionClosed_WithSell.equals(position.getStatus())) {
					position.setStatus(EnumPositionStatus.PositionClosed_WithBuy);
				}
				
				System.out.println(modelid()+" "+position.getKey()+" Position is already closed. SL hit. Current SL:"+position.getCurrentSL()+" price:"+currentPrice);
				CacheService.positionalData.set(index,position);
				return;
			}
			
			
			
			//has it opened below the expected SL? Then we need to change the SL to the 15 mnt candle low
			
			if(!position.isGap15mntcheckDone()) {
				position = mnt15GapCheck(position, dayOpen, dayLow,defaultSL,currentPrice);
				if(position.getExpectedSL()==0) {
					slCalculation(position, priceDiffPer, currentPrice, buyPrice,profitZone,defaultSL);
				}
			}else 
			if(position.isGap15mntcheckDone()) {
				slCalculation(position, priceDiffPer, currentPrice, buyPrice,profitZone,defaultSL);
			}

			if(log_text.get(position.getKey())==null) {
				log_text.put(position.getKey(),modelid()+","+position.getKey()+" ,ESL,"+position.getExpectedSL()+", BP:,"+buyPrice+", CP:,"+currentPrice);
			}
			
			String lc_log = modelid()+","+position.getKey()+" ,ESL,"+position.getExpectedSL()+", BP:,"+buyPrice+", CP:,"+currentPrice;
			
			if(!lc_log.equals(log_text.get(position.getKey()))) {
				System.out.println(lc_log);
				log_text.put(position.getKey(), lc_log);
			}
			
			
			//System.out.println("*******************************************");

			CacheService.positionalData.set(index,position);
			
			checkForCurrentPriceClosingonSL(currentPrice,position);
			
			//check if the current price is greater or less than 2.5% of the buy price. if yes, then start trailing.
			//if the current price is less than 2.5%, then we stick with 
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		finally {
			
			completedProcess=true;
		}
		
	}
	
	private void checkforConfig(PositionalData position) {
		if(!position.getConfigData().isPopulated()) {
			
			/****
			 * TODO
			 * Double to String conversion issue around extra 0
			 */
			if(modeConfig().getReferenceDataMap().containsKey(String.valueOf(position.getKey())+"0")) {
					
				
				List<String> values =  modeConfig().getReferenceDataMap().get(String.valueOf(position.getKey())+"0");
				position.getConfigData().setName(values.get(0));
				
				position.getConfigData().setSlPercen(Double.valueOf(values.get(1).split("\\:")[1]));
				
				position.getConfigData().setSlAbsolute(Double.valueOf(values.get(2).split("\\:")[1]));
				position.getConfigData().setProfitPercen(Double.valueOf(values.get(3).split("\\:")[1]));
				
				String type = values.get(4).split("\\:")[1];
				
				if("BUY".equals(type)) {
					position.setStratergyPositionType(EnumStratergyPositionType.POSITION_BUY);
				}else {
					position.setStratergyPositionType(EnumStratergyPositionType.POSITION_SELL);
				}
				
				position.getConfigData().setHardslClosing(Double.valueOf(values.get(5).split("\\:")[1]));
				position.getConfigData().setPreviousDaySL(Double.valueOf(values.get(6).split("\\:")[1]));                    
				
				
				
				
				position.setStratergyType(EnumStratergyType.TopBottom);
				
				position.getConfigData().setPopulated(true);
				
				
			
			}else {
				System.out.println(modelid()+" "+"Error: *******Missing configuration for:"+position.getKey());
			}
			
		}
	}
	
	private void checkForCurrentPriceClosingonSL(double currentPrice,PositionalData position) {
		
		double hardslLimit = position.getConfigData().getHardslClosing();
		if(currentPrice>position.getExpectedSL()) {
			if(percen(currentPrice, position.getExpectedSL())<=hardslLimit) {
				if(position.getCurrentSL()<position.getExpectedSL()) {
					/**
					 * TODO
					 * Hard SL should always be a .5 or whole number
					 */
					position.setCurrentSL(position.getExpectedSL());
					placeOrder(position);
					System.out.println(modelid()+" "+position.getKey()+"................Setting hard SL...................!,"+position.getExpectedSL());
					
				}
			}
		}
		
		if(currentPrice<position.getExpectedSL()) {
			if(!EnumPositionStatus.PositionClosed_WithBuy.equals(position.getStatus()) &&
					!EnumPositionStatus.PositionClosed_WithSell.equals(position.getStatus())) {
				
				
				//need to close the position...
				System.out.println(modelid()+" A freak spike where the curren price is less than SL. Need to way to manage this:CP:"+currentPrice+" ESL:"+position.getExpectedSL());
				
			}
		}
	}
	
	
	private void placeOrder(PositionalData position) {
		
		/**
		 * @TODO Order creation should be in another thread, so as not block processing
		 */
		if(position.isHardSLinPlce()) {
			//modify SL
			position = BusinessInterface.updateSLKiteOrder(position);
			
		}else {
			position = BusinessInterface.createSLKiteOrder(position);
		}
		position.setStopLossType(StopLossType.Regular);
		position.setHardSLinPlce(true);
	}
	
	public PositionalData mnt15GapCheck(PositionalData position, double dayOpen, double dayLow,double defaultSL,double currentPrice) {
		
		if(position.isGap15mntcheckDone()) {
			return position;
		}
		System.out.println(modelid()+" "+position.getKey()+" Doing 15 mnt check");
		
		if(!StopLossType.Mnt15_High.equals(position.getStopLossType()) || 
				!StopLossType.Mnt15_Low.equals(position.getStopLossType())) {
			
			if(position.doesPriceBreaksSL(dayOpen, defaultSL,0, EnumPositionType.Long)) {
				position.setExpectedSL(dayLow);
				position.setStopLossType(StopLossType.Mnt15_Low);
				
				// if the current price is below the 15mnt low, we exit. Call the SL order modification now.
				/******
				 * This should be set after the order modification has processed:
				 * TODO
				 */
				
				//position.setCurrentSL(position.getExpectedSL());
				
				System.out.println(modelid()+" "+position.getKey()+"-- Setting Day Low as SL"+position.getExpectedSL());
				
				//-----------------------------------------------------------------------
				
				
			}else {
				
				if(position.getConfigData().getPreviousDaySL()!=0 && !ignorePreviousDaySL) {
					System.out.println(modelid()+" "+position.getKey()+"Setting Previous Day SL:"+position.getConfigData().getPreviousDaySL());
					position.setExpectedSL(position.getConfigData().getPreviousDaySL());
				}
			}
			
			
		}
		
		position.setGap15mntcheckDone(true);
		
		return position;
		
	}
	
	
	public void slCalculation(PositionalData position,double priceDiffPer,double currentPrice,double buyPrice, double profitZone,double defaultSL) {
		
		
		double currentSL = position.getExpectedSL(); //get this from the position api
		boolean changeSL=true;
		double newSL=0;
		
		
		if(priceDiffPer>=profitZone) {
			newSL = currentPrice *(1-(defaultSL/100)) ;
			
			if(newSL>=currentSL) {
				//no need to change the SL
				changeSL=true;
				position.setExpectedSL(newSL);
			}
			
			//System.out.println("Profit zone: CurrentSL="+currentSL+" buyprice:"+buyPrice+" currentprice:"+currentPrice+" newsl:"+newSL+" changing sl:"+changeSL);
			
		}else if(priceDiffPer<profitZone && priceDiffPer>=0) {
			newSL = buyPrice *(1-(defaultSL/100));
			if(newSL>=currentSL) {
				changeSL = true;
				position.setExpectedSL(newSL);
			}
			//--System.out.println(modelid()+" "+position.getKey()+" B/w 0 to default zone");
			//System.out.println("B/w 0 to default zone: CurrentSL="+currentSL+" buyprice:"+buyPrice+" currentprice:"+currentPrice+" newsl:"+newSL);
		}else if(priceDiffPer<0 && priceDiffPer>=(-defaultSL)) {
			newSL = buyPrice *(1-(defaultSL/100));
			if(newSL>=currentSL) {
				changeSL = true;
				position.setExpectedSL(newSL);
			}
			//--System.out.println(modelid()+" "+position.getKey()+" B/w low SL to 0 zone");
			//System.out.println("B/w low SL to 0 zone: CurrentSL="+currentSL+" buyprice:"+buyPrice+" currentprice:"+currentPrice+" newsl:"+newSL);
		}
		else if(priceDiffPer<(-defaultSL)) {
			//newSL = buyPrice *(1-(defaultSL/100));
			//we wait for 15 mnts....
			changeSL=false;
			//--System.out.println(modelid()+" "+position.getKey()+" Below SL zone:");
			//System.out.println("Below SL zone: CurrentSL="+currentSL+" buyprice:"+buyPrice+" currentprice:"+currentPrice+" newsl:"+newSL);
		}
//		else if(currentPrice<systemSellPoint) {
//			//newSL = buyPrice *(1-(defaultSL/100));
//			//we wait for 15 mnts....
//			changeSL=false;
//			System.out.println("Below System sell point......");
//		}
		
	}

	public boolean isIgnorePreviousDaySL() {
		return ignorePreviousDaySL;
	}

	public void setIgnorePreviousDaySL(boolean ignorePreviousDaySL) {
		this.ignorePreviousDaySL = ignorePreviousDaySL;
	}

}
