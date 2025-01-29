package com.mod.process.models;

import static com.mod.support.ApplicationHelper.LogLevel2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.mod.enums.EnumMinuteType;
import com.mod.enums.EnumPositionType;
import com.mod.enums.EnumStratergyPositionType;
import com.mod.enums.EnumStratergyType;
import com.mod.interfaces.KiteInterface;
import com.mod.interfaces.SystemInterface;
import com.mod.interfaces.kite.KiteAPIWebsocket;
import com.mod.objects.DayCandles;
import com.mod.objects.Position;
import com.mod.objects.EnumPositionStatus;
import com.mod.objects.PositionalData;
import com.mod.objects.StopLossType;
import com.mod.support.ApplicationHelper;
import com.mod.support.Candle;
import com.test.inf.TestKiteInterface;
import com.zerodhatech.ticker.KiteTicker;

public class BollingerBandModel1 extends ProcessModelAbstract {
	
	
	
	/** per month return
	 * GRB	5.57
		ML	4.5
		BNF Straddle	3.95
		RSI	2.6
		BSSR	2.6
		GRBO	1.5
		NSS	1
		MSS 1%	0.83
		BTS	2.014

	 */

	
	//private final SystemInterface kiteInterface = KiteInterface.getInstance();
	private final SystemInterface kiteInterface =  TestKiteInterface.getInstance();
	
//    private static double upperBollingerBand=0;
//    private static double lowerBollingerBand=0;
    
    
    public static final Map<Long,DayCandles> trackingCandles = new HashMap<Long, DayCandles>();
    
    
    
    //for test
    private static final long CANDLE_TIME_IN_MILLISECONDS=12*1000;
    //private static final long CANDLE_TIME_IN_MILLISECONDS=300*1000;
    
	public BollingerBandModel1(CacheService cacheService) {
		super();
		setCacheService(cacheService);
		if(modeConfig().getKeyValueConfigs().containsKey("stock_list")) {
			String[] stockListString = modeConfig().getKeyValueConfigs().get("stock_list").split("\\,");
			stockList.addAll(Arrays.asList(stockListString));
		}
		
		
	}    
    
	@Override
	public String modelid() {
		
		return "bbmodel1";
	}

	private static final List<String> stockList = new ArrayList<String>();

//	public DayCandles getCandles() {
//		return dayCandles;
//	}
	
	
	/**
	 * CAN DO ONLY ONE ASSET.. NEED to upgrade to manage multiple*****
	 */
	
	@Override
	public void processNow() {
		

		completedProcess=false;
		
		
		
		PositionalData position = null;
		
		Iterator<String> itr2 =  stockList.iterator();
		
		/**
		 * Run BB scans on BNF & NF
		 */
		
		while(itr2.hasNext()) {
			Long keyVal = Long.valueOf(itr2.next());
			if(keyVal==CacheService.BN_KEY || keyVal==CacheService.NF_KEY) {
				subProcess(keyVal);
			}
		}
		
		/**
		 * Run process for positional data..
		 */
		
		if(CacheService.positionalData.size()>0) {
			
			Iterator<PositionalData> itr =  CacheService.positionalData.iterator();
			
			
			int index = 0;			
			
			
			/**
			 * Trail SL for positions
			 */
			
			while(itr.hasNext()) {
				position = itr.next();
				if(EnumStratergyType.BoliingerBand5Mnts.equals(position.getStratergyType())) {
					
					if(LogLevel2) {
						System.out.println(modelid()+" processing stock: "+position.getKey());
					}
					//System.out.println("processing stock: "+position.getKey());
					//track 5 mnts for positional candles
					subProcess(position.getKey());
					/** 
					 * send the List position as well, so that it can be replaced with updated data.
					 * 
					 * WITHOUT THE LATEST POSITION DATA cannot track..
					 */
					trailSL(position, index);
				}
				
				index++;
				
			}
			
		}
		
		
		
		
		/**
		 * if position taken, it needs to be SL trailed.....**********************
		 */
		
		
		
		
		
		try {
			
			
			
		} finally {
			
			completedProcess=true;
		}
		
		completedProcess=true;
	}
	
	
	private void trailSL(PositionalData position, int index) {
		
		if(position==null) {
			System.out.println(modelid()+ ":Position is null");
			return;
			
		}
		
		/**
		 * NO SL tracking for BN & NF position..
		 */
		if(CacheService.BN_KEY == position.getKey() 
				|| CacheService.NF_KEY == position.getKey()) {
			return;
		}
		
		if(EnumPositionStatus.PositionClosed_WithBuy.equals(position.getStatus()) 
				|| EnumPositionStatus.PositionClosed_WithSell.equals(position.getStatus())) {
			System.out.println(modelid()+":"+position.getKey()+": *********Position is already closed, not tracking******** ");
			return;
			
		}
		
		DayCandles dayCandles = trackingCandles.get(position.getKey());
		
		checkforConfig(position);
		
		try {
			System.out.println("trailing..."+position.getKey());
			double currentPrice = CacheService.PRICE_LIST.get(position.getKey());
			
			double buyPrice = position.getBuyPrice();
//			double priceDiffPer = percen(currentPrice, buyPrice);
//			
//			double defaultSL = position.getConfigData().getSlPercen();
//			double profitZone = position.getConfigData().getProfitPercen();
			
		
			if(EnumPositionType.Long.equals(position.getPositionType())
					&& position.getCurrentSL()!=0
					&& position.getCurrentSL()>=currentPrice) {
				position.setStatus(EnumPositionStatus.PositionClosed_WithBuy);
				System.out.println(modelid()+":"+position.getKey()+" Position is already closed. SL hit");
				CacheService.positionalData.set(index,position);
				return;
			}
			if(EnumPositionType.Short.equals(position.getPositionType())
					&& position.getCurrentSL()!=0
					&& position.getCurrentSL()<=currentPrice) {
				position.setStatus(EnumPositionStatus.PositionClosed_WithSell);
				System.out.println(modelid()+":"+position.getKey()+" Position is already closed. SL hit");
				CacheService.positionalData.set(index,position);
				return;
			}
			
			
			
			
			/**
			 * Need to decide if SL trailing calculation needs to be done. 
			 */
			//slCalculation(position, priceDiffPer, currentPrice, buyPrice,profitZone,defaultSL);
			
			System.out.println(modelid()+","+position.getPositionType() +" "+dayCandles.getPreviousCandle().getLow()+" ");
			
			boolean hardSL =  false;
			
			if(EnumPositionType.Long.name().equals(position.getPositionType()) &&
					position.getExpectedSL()<dayCandles.getPreviousCandle().getLow() &&
					currentPrice > dayCandles.getPreviousCandle().getLow()) {
				
				hardSL = checkForCurrentPriceClosingonSL(currentPrice,position);
				
				if(!hardSL) {
					position.setExpectedSL(dayCandles.getPreviousCandle().getLow());
				}
				
			}

			if(EnumPositionType.Short.name().equals(position.getPositionType()) &&
					position.getExpectedSL()>dayCandles.getPreviousCandle().getHigh() &&
					currentPrice < dayCandles.getPreviousCandle().getHigh()) {
				
				hardSL = checkForCurrentPriceClosingonSL(currentPrice,position);
				
				if(!hardSL) {
					position.setExpectedSL(dayCandles.getPreviousCandle().getHigh());
				}
				
			}

			System.out.println(modelid()+":"+"R,"+position.getKey()+" ESL,"+position.getExpectedSL()+", BP:,"+buyPrice+", CP:,"+currentPrice);
			//System.out.println("*******************************************");

			CacheService.positionalData.set(index,position);
			
			
			
			//check if the current price is greater or less than 2.5% of the buy price. if yes, then start trailing.
			//if the current price is less than 2.5%, then we stick with 
			
			trackingCandles.put(position.getKey(), dayCandles);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		finally {
			
			//completedProcess=true;
		}
		
	}

	private boolean checkForCurrentPriceClosingonSL(double currentPrice,PositionalData position) {
		EnumPositionType positionType = position.getPositionType();
		
		System.out.println(modelid()+":"+"Position type:"+positionType);
		
		if(EnumPositionType.Long.equals(positionType) && currentPrice>position.getExpectedSL()) {
			if(percen(currentPrice, position.getExpectedSL())<=1) {
				//if(position.getCurrentSL()<position.getExpectedSL()) 
				{
					/**
					 * TODO
					 * Hard SL should always be a .5 or whole number
					 */
					if(position.getCurrentSL()>0 
							&& position.getCurrentSL()<position.getExpectedSL()) {
						position.setCurrentSL(position.getExpectedSL());
						position.setStopLossType(StopLossType.Regular);
						System.out.println(modelid()+":"+position.getKey()+"................Setting hard SL........for Buy...........!,"+position.getExpectedSL());
						
					}else if(position.getCurrentSL()==0) {
						position.setCurrentSL(position.getExpectedSL());
						position.setStopLossType(StopLossType.Regular);
						System.out.println(modelid()+":"+position.getKey()+"................Setting hard SL........for Buy...........!,"+position.getExpectedSL());
						
					}
					
				}
			}
		}
		
		if(EnumPositionType.Short.equals(positionType) && currentPrice<position.getExpectedSL()) {
			if(percen(position.getExpectedSL(),currentPrice)<=1) {
				//if(position.getCurrentSL()>position.getExpectedSL()) 
				{
					/**
					 * TODO
					 * Hard SL should always be a .5 or whole number
					 */
					if(position.getCurrentSL()>0 
							&& position.getCurrentSL()>position.getExpectedSL()) {
						position.setCurrentSL(position.getExpectedSL());
						position.setStopLossType(StopLossType.Regular);
						System.out.println(modelid()+":"+position.getKey()+"................Setting hard SL........for Sell...........!,"+position.getExpectedSL());
						
						return true;
						
					}else if(position.getCurrentSL()==0) {
						position.setCurrentSL(position.getExpectedSL());
						position.setStopLossType(StopLossType.Regular);
						System.out.println(modelid()+":"+position.getKey()+"................Setting hard SL........for Sell...........!,"+position.getExpectedSL());
						
						return true;
					}
					
				}
			}
		}
		
		return false;
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
			System.out.println(modelid()+":"+position.getKey()+" B/w 0 to default zone");
			//System.out.println("B/w 0 to default zone: CurrentSL="+currentSL+" buyprice:"+buyPrice+" currentprice:"+currentPrice+" newsl:"+newSL);
		}else if(priceDiffPer<0 && priceDiffPer>=(-defaultSL)) {
			newSL = buyPrice *(1-(defaultSL/100));
			if(newSL>=currentSL) {
				changeSL = true;
				position.setExpectedSL(newSL);
			}
			System.out.println(modelid()+":"+position.getKey()+" B/w low SL to 0 zone");
			//System.out.println("B/w low SL to 0 zone: CurrentSL="+currentSL+" buyprice:"+buyPrice+" currentprice:"+currentPrice+" newsl:"+newSL);
		}
		else if(priceDiffPer<(-defaultSL)) {
			//newSL = buyPrice *(1-(defaultSL/100));
			//we wait for 15 mnts....
			changeSL=false;
			System.out.println(modelid()+":"+position.getKey()+" Below SL zone:");
			//System.out.println("Below SL zone: CurrentSL="+currentSL+" buyprice:"+buyPrice+" currentprice:"+currentPrice+" newsl:"+newSL);
		}
//		else if(currentPrice<systemSellPoint) {
//			//newSL = buyPrice *(1-(defaultSL/100));
//			//we wait for 15 mnts....
//			changeSL=false;
//			System.out.println("Below System sell point......");
//		}
		
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
					position.setStratergyPositionType(EnumStratergyPositionType.POSITION_BUY);   									;
				}else {
					position.setStratergyPositionType(EnumStratergyPositionType.POSITION_SELL);
				}
				
//				position.getConfigData().setHardslClosing(Double.valueOf(values.get(5).split("\\:")[1]));
//				position.getConfigData().setPreviousDaySL(Double.valueOf(values.get(6).split("\\:")[1]));                    

				
				position.getConfigData().setPopulated(true);
				
				
			
			}else {
				System.out.println(modelid()+":"+"Error: *******Missing configuration for:"+position.getKey());
			}
			
		}
	}
	
	
	private void subProcess(long stockKey) {
		
		boolean tobeTracked= false;
		double entryPrice=0;
		
		if(stockKey==CacheService.BN_KEY || stockKey==CacheService.NF_KEY) {
			tobeTracked=true;
		}
		
		double currentPrice = CacheService.PRICE_LIST.get(stockKey);
		
		DayCandles dayCandles =  trackingCandles.get(stockKey);
		
		
		
		if(dayCandles==null) {
			trackingCandles.put(stockKey, new DayCandles(new Candle(), new Candle()));
			dayCandles = trackingCandles.get(stockKey);
			dayCandles.setUpperBollingerBand(currentPrice);
			dayCandles.setLowerBollingerBand(currentPrice);
		}
		if(dayCandles.getUpperBollingerBand()==0) {
			dayCandles.setUpperBollingerBand(currentPrice);
			dayCandles.setLowerBollingerBand(currentPrice);
		}
		
		//System.out.println("Alert candle:"+dayCandles.isAlertCandleAvailable()+" "+stockKey);
		
		Integer[] posKey = CacheService.getInstance().findPositionsbyKey(stockKey,EnumPositionType.Both);
		
		long candleStartTime = dayCandles.getCandleStartTime();
		
		if(candleStartTime==0) {
			dayCandles.setCandleStartTime(System.currentTimeMillis());
			candleStartTime = dayCandles.getCandleStartTime();
		}
		
		
		
		if((System.currentTimeMillis()-candleStartTime)<CANDLE_TIME_IN_MILLISECONDS) {
			//System.out.println("Collecting candles Current Price:"+currentPrice+" "+stockKey);
			
			//waiting for the 5 mnts to get over..
			if(dayCandles.isCurrentCandleEmpty()) {
				dayCandles.getCurrentCandle().setAllWithSinglePrice(currentPrice);  
			}else if(currentPrice>dayCandles.getCurrentCandle().getHigh()) {
				dayCandles.getCurrentCandle().setHigh(currentPrice);
			}else if(currentPrice<dayCandles.getCurrentCandle().getLow()) {
				dayCandles.getCurrentCandle().setLow(currentPrice);
			}
			System.out.println(modelid()+":"+"low---"+dayCandles.getCurrentCandle().getLow()+" --key:"+stockKey);
		}else if((System.currentTimeMillis()-candleStartTime)>=CANDLE_TIME_IN_MILLISECONDS) {
			//set the close price after 5 mnts.
			
			System.out.println("Times up.....");
			
			dayCandles.getCurrentCandle().setClose(currentPrice);
			dayCandles.getPreviousCandle().populateWithAnotherCandle(dayCandles.getCurrentCandle());
			
			System.out.println(modelid()+":"+"Previous candle low:"+dayCandles.getPreviousCandle().getLow()+" "+stockKey);
			
			//System.out.println("Previous candle high:"+dayCandles.getPreviousCandle().getHigh() +"Previous candle low:"+dayCandles.getPreviousCandle().getLow());
			
			dayCandles.setCandleStartTime(System.currentTimeMillis());
			
			
			/**
			 * Check to see the alert candle is hanging around for long.
			 */
			
			if(tobeTracked && dayCandles.isAlertCandleAvailable() && !dayCandles.isInPosition()) {
				if(dayCandles.getCandlesAfterAlert()==3) {
					dayCandles.setCandlesAfterAlert(0);
					
					dayCandles.setAlertCandleAvailable(false);
					System.out.println(modelid()+":"+"Removing alert candle after 2 candles, as no position found for:"+stockKey);
				}else {
					dayCandles.setCandlesAfterAlert(dayCandles.getCandlesAfterAlert()+1);
					System.out.println(modelid()+":"+"Increase alert count:"+dayCandles.getCandlesAfterAlert());
				}
			}

			
			//for upper band....
			if(!dayCandles.isInPosition() && tobeTracked &&  currentPrice>dayCandles.getUpperBollingerBand()) {
				
				//******do not take alert position if there is one already!******
				//if true then the previous candle becomes the alert candle...
				if(!dayCandles.isAlertCandleAvailable()) {
					//System.out.println("closing is greater than BB: Candle High:"+dayCandles.getCurrentCandle().getHigh());
					dayCandles.setAlertCandleAvailable(true);
					dayCandles.setAlertToHigh();
					dayCandles.setAlertHigh(dayCandles.getCurrentCandle().getHigh());
					entryPrice = dayCandles.getPreviousCandle().getHigh();
					dayCandles.setCandlesAfterAlert(dayCandles.getCandlesAfterAlert()+1);
					
					
					System.out.println(modelid()+":"+"upper band..setting alert candle:stockkey:"+stockKey+" high:"+entryPrice);
					
				}
				
			}
			if(!dayCandles.isInPosition() && tobeTracked && currentPrice<dayCandles.getLowerBollingerBand()) {
				
				if(!dayCandles.isAlertCandleAvailable()) {
					//if true then the previous candle becomes the alert candle...
					//System.out.println("closing is lower than BB: Candle Low:"+dayCandles.getCurrentCandle().getLow());
					dayCandles.setAlertCandleAvailable(true);
					dayCandles.setAlertToLow();
					dayCandles.setAlertLow(dayCandles.getCurrentCandle().getLow());
					entryPrice = dayCandles.getPreviousCandle().getLow();
					dayCandles.setCandlesAfterAlert(dayCandles.getCandlesAfterAlert()+1);
					System.out.println(modelid()+":"+"lower band..setting alert candle:stockkey:"+stockKey+" low:"+entryPrice);
				}
			}
			
			dayCandles.getCurrentCandle().reset();
			
			
			
			//add the close to the cache to calcualte new BB
			if(tobeTracked) {
				LinkedList<Double> BB_Close_Records = dayCandles.getBB_Close_Records();
				BB_Close_Records.pollFirst();
				BB_Close_Records.addLast(currentPrice);
				dayCandles.setUpperBollingerBand(ApplicationHelper.bbHighCalculation(BB_Close_Records.toArray(new Double[BB_Close_Records.size()])));
				
				dayCandles.setLowerBollingerBand(ApplicationHelper.bbLowCalculation(BB_Close_Records.toArray(new Double[BB_Close_Records.size()])));
				
				
				System.out.println(modelid()+":"+"BB Upper band:"+dayCandles.getUpperBollingerBand());
				System.out.println(modelid()+":"+"BB Lower band:"+dayCandles.getLowerBollingerBand());
			}
			
			//System.out.println("Current price:"+currentPrice);
			
			
//			if(upperBollingerBand==0) {
//				upperBollingerBand =  ApplicationHelper.bbHighCalculation(BB_Close_Records.toArray(new Double[BB_Close_Records.size()]));
//			}
//			
//			if(lowerBollingerBand==0) {
//				lowerBollingerBand = ApplicationHelper.bbLowCalculation(BB_Close_Records.toArray(new Double[BB_Close_Records.size()]));
//			}
			

			

		}
		
		/**
		 * If already in position, we don't need to add it.
		 */
		if(posKey!=null && posKey.length>0) {
			if(LogLevel2) {
				System.out.println(modelid()+":"+"Not processing, as the asset is already in position:"+stockKey);
			}
			return;
		}

		int sPrice = 0;
		
		//if alert candle is available
		if(dayCandles.isAlertCandleAvailable()) {
			if(currentPrice>dayCandles.getAlertHigh() 
					&& "High".equalsIgnoreCase(dayCandles.alertCandleType())) {
				
				sPrice = ApplicationHelper.getStrikePrice(stockKey, currentPrice, "buy");
				
				List<Candle> dt = kiteInterface.getCandleData(ApplicationHelper.getKiteKey(String.valueOf(sPrice)),EnumMinuteType.Fiveminute);
				
				int index = (dt.size()-1) -(dayCandles.getCandlesAfterAlert()-1);
				
				PositionalData position = new PositionalData();
				position.setKey(Long.valueOf(ApplicationHelper.getKiteKey(String.valueOf(sPrice))+".00"));
				position.setStratergyType(EnumStratergyType.BoliingerBand5Mnts);
				position.setPositionType(EnumPositionType.Long);
				position.setExpectedSL(dt.get(index).getLow());
				CacheService.positionalData.add(position);
				dayCandles.setInPosition(true);
				dayCandles.setAlertCandleAvailable(false);
				System.out.println(modelid()+":"+"*******Taking long position at*******:Strike Price:"+sPrice+" SL:"+position.getExpectedSL()+ " key:"+stockKey);
				
				subscribeToStrikePrice(position.getKey());
				
				/** TODO NEED TO GET THE POSITION BUY PRICE....
				 * This should be the strike price premium
				 */
				position.setBuyPrice(135.12);
				
				/**
				 * Set data for candle tracking
				 */
			}
			if(currentPrice<dayCandles.getAlertLow() 
					&& "Low".equalsIgnoreCase(dayCandles.alertCandleType())) {
				
				sPrice = ApplicationHelper.getStrikePrice(stockKey, currentPrice, "sell");
				
				List<Candle> dt = kiteInterface.getCandleData(ApplicationHelper.getKiteKey(String.valueOf(sPrice)),EnumMinuteType.Fiveminute);
				
				int index = (dt.size()-1) -(dayCandles.getCandlesAfterAlert()-1);
				
				PositionalData position = new PositionalData();
				position.setKey(Long.valueOf(ApplicationHelper.getKiteKey(String.valueOf(sPrice))+".00"));
				position.setStratergyType(EnumStratergyType.BoliingerBand5Mnts);
				position.setPositionType(EnumPositionType.Short);
				
				position.setExpectedSL(dt.get(index).getHigh());
				CacheService.positionalData.add(position);

				dayCandles.setInPosition(true);
				dayCandles.setAlertCandleAvailable(false);

				System.out.println(modelid()+":"+"******Taking short position at*******:Strike Price:"+sPrice+" SL:"+position.getExpectedSL());
				subscribeToStrikePrice(position.getKey());
				
				/***
				 * TODO this should be Strike price position premium
				 */
				position.setBuyPrice(135.12);
			}
			

		}
		

		//System.out.println("stockkey:"+stockKey+"Low--"+dayCandles.getCurrentCandle().getLow());
		trackingCandles.put(stockKey, dayCandles);
		//System.out.println("Alert candle:"+dayCandles.isAlertCandleAvailable()+" "+stockKey);
	}
	
	
	public void subscribeToStrikePrice(double key) {
		ArrayList<Long> args = new ArrayList<Long>();
		
		String kiteKey = String.valueOf(key).split("\\.")[0];
		
	
		args.add(Long.valueOf(kiteKey));
		
		
		KiteAPIWebsocket.tickerProvider.setMode(args, KiteTicker.modeFull);
		KiteAPIWebsocket.tickerProvider.subscribe(args);
		
		System.out.println(modelid()+":"+"Subscribed to :"+key);

	}
	
	/**
	 * Check the opening value and see if 
	 * open is really high, flat or really low.
	 * @param current
	 */
	private double profit(Position pos,Position reverse) {
		double profit = pos.getProfit()+reverse.getProfit();
		return profit;
	}
	
	private double profitPercen(Position pos,Position reverse) {
		double totalCost = pos.cost()+reverse.cost();
		double profit = profit(pos,reverse);
		
		return (profit/totalCost)*100;
	}
	
	private static double percen(double current,double prev){
		return ((current-prev)/prev)*100;
	}	
	

}
