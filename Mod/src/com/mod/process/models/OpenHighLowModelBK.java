package com.mod.process.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.mod.enums.EnumPositionType;
import com.mod.enums.EnumStratergyType;
import com.mod.interfaces.SystemInterface;
import com.mod.interfaces.kite.LTPKiteAPIWebsocket;
import com.mod.objects.Position;
import com.mod.objects.PositionalData;
import com.mod.objects.StopLossType;
import com.mod.support.ApplicationHelper;
import com.mod.support.CandleSorter;
import com.mod.support.CandleWrapper;
import com.mod.support.OpenHighLowSupport;
import com.test.inf.TestKiteInterface;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Depth;
import com.zerodhatech.models.MarketDepth;
import com.zerodhatech.models.Quote;

public class OpenHighLowModelBK extends ProcessModelAbstract {
	
	static int count=0;
	
	
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

	/**
	 * TODO
	 * 1. once the stocks are recognised, unscribe from LTP and get full depth to take position.
	 * 2. compare stock & future and pick the ones where both are in the same direction. 
	 * 3. consider Intraday cost of 1/5 for most of the cost.
	 */
	
	public static long startbotTime = 0;
	static boolean positionTaken = false;
	static boolean positionPrinted=false;
	static long positionTime;
	public static List<Long> trackPrices;
	static ArrayList<Long> notRequired;
	private static final OpenHighLowModelBK model = new OpenHighLowModelBK(CacheService.getInstance());
	
	//private final SystemInterface kiteInterface = KiteInterface.getInstance();
	private final SystemInterface kiteInterface =  TestKiteInterface.getInstance();
	
    private static List<String> stockkeys;
    
    private double posCost=50000.00;
    
	private OpenHighLowModelBK(CacheService cacheService) {
		super();
		setCacheService(cacheService);
		
		stockkeys = new ArrayList<String>();
		sectorStockList = new HashMap<String, List<String>>();
		
		stockkeys.add("stock_list_cement");
		stockkeys.add("stock_list_finance");
		stockkeys.add("stock_list_pharma");
		stockkeys.add("stock_list_tech");
		stockkeys.add("stock_list_metal");
		stockkeys.add("stock_list_consumer");
		stockkeys.add("stock_list_auto");
		stockkeys.add("stock_list_energy");
		stockkeys.add("stock_list_realty");
		stockkeys.add("stock_list_power");
		stockkeys.add("stock_list_oil");
		
		
		
		for(int i=0;i<stockkeys.size();i++) {
			if(modeConfig().getKeyValueConfigs().containsKey(stockkeys.get(i))) {
				String[] stockListString = modeConfig().getKeyValueConfigs().get(stockkeys.get(i)).split("\\,");
				sectorStockList.put(stockkeys.get(i), Arrays.asList(stockListString));
				
			}
		}
		
		trackPrices  = new ArrayList<Long>();
		notRequired = new ArrayList<Long>();
		
	} 
	
	public static OpenHighLowModelBK getInstance() {
		return model;
	}

    
	@Override
	public String modelid() {
		
		return "openhl";
	}

	private static Map<String, List<String>> sectorStockList;

//	public DayCandles getCandles() {
//		return dayCandles;
//	}
	
	
	/**
	 * CAN DO ONLY ONE ASSET.. NEED to upgrade to manage multiple*****
	 */
	
	@Override
	public void processNow() {
		
		if(startbotTime==0) {
			System.out.println(modelid()+ " Not ready: Processing high low");
			return;
		}
		
		if((System.currentTimeMillis()-startbotTime)<5000) {
			System.out.println(modelid()+ " Not time yet" + startbotTime+" current:"+System.currentTimeMillis());
			return;
		}
		
		if(positionTaken && positionTime==0) {
			positionTime =  System.currentTimeMillis();
			System.out.println(modelid()+" Time:"+new Date());
		}
		
		if(positionTaken ) {
						
			if(!positionPrinted) {
				getMatches();
				System.out.println(modelid()+" Position already taken "+CacheService.positionalData);
			}
			positionPrinted=true;
			//return;
		}
		
		completedProcess=false;
		
		count++;
		
		
		if(!positionTaken) {
			
			Iterator<String> stockKeys =  sectorStockList.keySet().iterator();
			String key =  null;
			String stock = null;
			
			List<String> depthList = new ArrayList<String>();
			
			List<CandleWrapper> changes = new ArrayList<CandleWrapper>();
			
			try {
				//finance
				while(stockKeys.hasNext()) {
					key = stockKeys.next();
					
					Iterator<String> stockList =  sectorStockList.get(key).iterator();
					
					
					int candleInThePositiveZone = 0;
					int candleInTheNegativeZone = 0;
					boolean choosen = false;
					while(stockList.hasNext()) {
						stock = stockList.next();
						
						CandleWrapper candle =  CacheService.candleData.get(Long.valueOf(stock));
						
						if(candle!=null) {

							if(candle.getSector()==null) {
								candle.setSector(key);
								CacheService.candleData.put(Long.valueOf(stock), candle);
							}
							
							if(candle.getKey()==0) {
								candle.setKey(Long.valueOf(stock));
								CacheService.candleData.put(Long.valueOf(stock), candle);
								
							}
							
							if(candle.getCandle().getChange()>=0.2 && candle.getCandle().isOpenEqualToLow()) {
								takePosPosition(candle,EnumStratergyType.OpenHighLow_1);
								candleInThePositiveZone++;
								choosen =  true;
								//System.out.println(modelid()+" Pos:"+candle.getKey());
							}
							
							if((candle.getCandle().getChange()<=(-0.2)) && candle.getCandle().isOpenEqualToHigh()) {
								takeNegPosition(candle,EnumStratergyType.OpenHighLow_1);
								candleInTheNegativeZone++;
								choosen =  true;
								//System.out.println(modelid()+" Neg:"+candle.getKey());
							}

							
							if(candle.getCandle().isOpenEqualToLow() ) {
								takePosPosition(candle, EnumStratergyType.OpenHighLow_2);
								depthList.add(stock);
								choosen =  true;
								
							}
							
							if(candle.getCandle().isOpenEqualToHigh()) {
								takeNegPosition(candle, EnumStratergyType.OpenHighLow_2);
								depthList.add(stock);
								choosen =  true;
								
							}
							
							if(!choosen) {
								System.out.println("Adding not chosen:"+stock);
								if(stock.contains(".")) {
									notRequired.add(Long.valueOf(stock.split("\\.")[0]));
								}
							}
							
							choosen=false;
							
							
						}
						
					}
					
					if(changes!=null && changes.size()>0) {
						System.out.println(modelid()+",Sector:"+key+",Positive Zone:"+candleInThePositiveZone+", Negative Zone:"+candleInTheNegativeZone);
						System.out.println("----------------------------------------------------------------------------------------");
						
					}
					candleInTheNegativeZone=0;
					candleInThePositiveZone=0;
					
					
					
				}
				
				
				if(depthList.size()>0) {
					Map<String, Quote> quote = ApplicationHelper.getKiteSDK().getQuote(depthList.toArray(new String[depthList.size()]));
					
					
					if(quote!=null && quote.size()>0) {
						Iterator<String> keys = quote.keySet().iterator();
						while(keys.hasNext()) {
							Quote lquote =  quote.get(keys.next());
							displayDepth(lquote.depth, lquote.instrumentToken);
							
						}
						
					}
				}
				
				//sortSector(changes);
			
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (KiteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		}
		

		if(positionTaken) {
			//SL tracking...
			
			Iterator<PositionalData> itr =  CacheService.positionalData.iterator();
			
			PositionalData psdata = null;
			
			while(itr.hasNext()) {
				psdata = itr.next();
				
				double currentP = CacheService.PRICE_LIST.get(psdata.getKey());
				
				if(EnumStratergyType.OpenHighLow_1.equals(psdata.getStratergyType()) || 
						EnumStratergyType.OpenHighLow_2.equals(psdata.getStratergyType())) {

					if(System.currentTimeMillis()-positionTime>120000) {
						psdata.checkProfit(currentP, true);
					}else {
						psdata.checkProfit(currentP, false);
					}

				}
			}
			
			
		}
		
		if(positionTaken) {
			
			trackSL();
		}
		
		
		if(notRequired.size()>0 && LTPKiteAPIWebsocket.tickerProvider!=null) {
			
			LTPKiteAPIWebsocket.tickerProvider.unsubscribe(notRequired);
			notRequired.clear();
			System.out.println(modelid()+ " Unsubscribed");
		}
		
		completedProcess=true;
	}
	
	
	private void displayDepth(MarketDepth marketDepth,long key) {
		
		List<Depth> depths = marketDepth.buy;
		
		if(depths!=null && depths.size()>0) {
			
			
			
			StringBuilder depthString = new StringBuilder(key+",buy,");
			for(int i=0;i<5;i++) {
				depthString.append(depths.get(i).getPrice()+":").append(depths.get(i).getQuantity()+",");
			}
			
			depthString.append(", sell,");
			depths = marketDepth.sell;
			for(int i=0;i<5;i++) {
				depthString.append(depths.get(i).getPrice()+":").append(depths.get(i).getQuantity()+",");
			}
			
			
			
			
			/**
			 * Now calculate position buy/sell size and cost
			 */

			Integer[] positionKeys =  CacheService.getInstance().findPositionsbyKey(key,EnumPositionType.Both);
			PositionalData psData = null;
			
			if(positionKeys!=null && positionKeys.length>0) {
				for(int i=0;i<positionKeys.length;i++) {
					psData = CacheService.positionalData.get(positionKeys[i]);
					
					if(EnumPositionType.Long.equals(psData.getPositionType())) {
						depths = marketDepth.sell;
					}

					if(EnumPositionType.Short.equals(psData.getPositionType())) {
						depths = marketDepth.buy;
					}
					
					psData = calculatePriceAndQuantity(psData, depths);
					
					
					
					CacheService.positionalData.set(positionKeys[i], psData);

				}
			}
			
			
			
		}
		
	}
	
	private PositionalData calculatePriceAndQuantity(PositionalData psData, List<Depth> depths) {
		
		double price = 0.00;
		int qt = 0;
		
		double totalval = 0;
		int totalqt = 0;
		
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
					
					/**
					 * @TODO PLACE order
					 */
					//place order
					
				}
				
				/**
				 * If the present depth value + previous ones are greater than posCost
				 */
				if( ((price*qt)+totalval)>posCost && !added){
					
					double remaining = posCost-totalval;
					int pur = Double.valueOf(remaining).intValue()/Double.valueOf(price).intValue();
					totalqt = totalqt+pur;
					totalval =totalval+ (price*pur);
					/**
					 * @TODO PLACE order
					 */
					
					//place order
					
				}
			}

		}
		
		if(totalval<posCost) {
			System.out.println(modelid()+","+psData.getKey()+", Not enough volume:"+totalval+" ---"+totalqt);
		}
		
		if(EnumPositionType.Long.equals(psData.getPositionType())) {
			psData.setBuyQuantity(totalqt);
			psData.setBuyPrice(totalval/totalqt);
			
		}

		if(EnumPositionType.Short.equals(psData.getPositionType())) {
			psData.setSellQuantity(totalqt);
			psData.setSellPrice(totalval/totalqt);
		}
		
		return psData;
		
	}
	
	private void getMatches() {

		Iterator<PositionalData> itr =  CacheService.positionalData.iterator();
		
		Iterator<PositionalData> itr2 =  CacheService.positionalData.iterator();
		
		PositionalData psdata = null;
		PositionalData psdata2 = null;
		
		while(itr.hasNext()) {
			psdata = itr.next();
			
			
			
			if(EnumStratergyType.OpenHighLow_1.equals(psdata.getStratergyType()) || 
					EnumStratergyType.OpenHighLow_2.equals(psdata.getStratergyType())) {
				
				OpenHighLowSupport support =  CacheService.stockFutureData.get(psdata.getKey());
				
				if(support!=null) {
					while(itr2.hasNext()) {
						psdata2 = itr2.next();
						if(psdata2!=null && psdata2.getKey()==support.getStock()) {
							psdata2.setBuyQuantity(support.getLotSize());
							psdata2.setTradingSymbol(String.valueOf(support.getFuture()));
						}
					}
					itr2 =  CacheService.positionalData.iterator();
					
					 
				}
				
			}
		}
		
	}
	
	private void trackSL() {

		Iterator<PositionalData> itr =  CacheService.positionalData.iterator();
		
		PositionalData psdata = null;
		
		
		while(itr.hasNext()) {
			psdata = itr.next();
			
			double currentP = CacheService.PRICE_LIST.get(psdata.getKey());
			
			if(EnumStratergyType.OpenHighLow_1.equals(psdata.getStratergyType()) || 
					EnumStratergyType.OpenHighLow_2.equals(psdata.getStratergyType())) {
				
				if(psdata.getBuyPrice()!=0) {
					
					buySLCalculation(psdata, currentP, psdata.getBuyPrice(), 0.20, 0.20);
				}
				
				if(psdata.getSellPrice()!=0) {
					
					sellSLCalculation(psdata, currentP, psdata.getSellPrice(), 0.20, 0.20);
				}

			}
		}

	}
	
	/***
	 * 
	 * @TODO CHECK TRailing SL logic between when the current price is between the buy price & profit. The SL should remain as it is.
	 * @param position
	 * @param priceDiffPer
	 * @param currentPrice
	 * @param buyPrice
	 * @param profitZone
	 * @param defaultSL
	 */
	
	public void buySLCalculation(PositionalData position,double currentPrice,double buyPrice, double profitZone,double defaultSL) {
		
		
		double currentSL = position.getExpectedSL(); //get this from the position api
		boolean changeSL=true;
		double newSL=0;
		/**
		 * If SL already hit don't change.
		 */
		if(currentSL!=0 && currentPrice!=0 && currentPrice<=currentSL) {
			return;
		}
		
		double priceDiffPer = ApplicationHelper.percen(currentPrice, buyPrice);
		
		if(priceDiffPer>=profitZone) {
			newSL = currentPrice *(1-(defaultSL/100)) ;
			
			if(newSL>=currentSL) {
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
		
		System.out.println(modelid()+","+position.getKey()+",BP:"+position.getBuyPrice()+",CP:"+currentPrice+",ESL:"+position.getExpectedSL());
		
	}
	
	public void sellSLCalculation(PositionalData position,double currentPrice,double sellPrice, double profitZone,double defaultSL) {
		
		//price difference should be positive.
		
		double currentSL = position.getExpectedSL(); //get this from the position api
		boolean changeSL=true;
		double newSL=0;
		
		
		/**
		 * If SL already hit don't change.
		 */
		if(currentSL!=0 && currentPrice!=0 && currentPrice>=currentSL) {
			return;
		}

		
		double priceDiffPer = ApplicationHelper.percen(sellPrice,currentPrice);
		
		if(priceDiffPer>=profitZone) {
			newSL = currentPrice *(1+(defaultSL/100)) ;
			
			if(newSL<=currentSL) {
				
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
		
		System.out.println(modelid()+","+position.getKey()+",BP:"+position.getSellPrice()+",CP:"+currentPrice+",ESL:"+position.getExpectedSL());
	}	
	
	private void takePosPosition(CandleWrapper candle, EnumStratergyType stratergyType) {
		
		PositionalData position = new PositionalData();
		position.setKey(candle.getKey());
		position.setStratergyType(stratergyType);
		position.setPositionType(EnumPositionType.Long);
		position.setBuyPrice(CacheService.PRICE_LIST.get(candle.getKey()));
		
		position.setExpectedSL(candle.getCandle().getLow());
		CacheService.positionalData.add(position);
		
		positionTaken=true;
		trackPrices.add(position.getKey());
		
		System.out.println(modelid()+" Buy Position:"+position.getKey()+" Price:"+position.getBuyPrice()+" Strat:"+stratergyType);
		
	}
	
	private void takeNegPosition(CandleWrapper candle, EnumStratergyType stratergyType) {
		PositionalData position = new PositionalData();
		position.setKey(candle.getKey());
		position.setStratergyType(stratergyType);
		position.setPositionType(EnumPositionType.Short);
		position.setSellPrice(CacheService.PRICE_LIST.get(candle.getKey()));
		
		position.setExpectedSL(candle.getCandle().getHigh());
		
		CacheService.positionalData.add(position);
		
		positionTaken=true;
		trackPrices.add(position.getKey());
		System.out.println(modelid()+" Sell Position:"+position.getKey()+" Price:"+position.getSellPrice()+" Strat:"+stratergyType);
		
	}
	
	public void sortSector(List<CandleWrapper> data) {
		System.out.println(modelid()+" Sector sorting...");
		Collections.sort(data, new CandleSorter());
		
		Map<String, Integer> track = new HashMap<String, Integer>();
		String key = null;
		Integer count = 0;
		
		if(data!=null && data.size()>0) {

			for(int i=0;i<10;i++) {
				key = data.get(i).getSector();
				if(track.containsKey(key)) {
					count = track.get(key);
					count = count+1;
					track.put(key, count);
				}else {
					track.put(key, 1);
				}
				
				if(i<3) {
					if(data.get(i).getCandle().isOpenEqualToLow()) {
						takePosPosition(data.get(i),EnumStratergyType.OpenHighLow_2);
					}
				}
				

			}
			
			System.out.println(modelid()+":"+data);
			System.out.println(modelid()+" Ranking:"+track);
			System.out.println("----------------------------------------------------------------------------------------");
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
