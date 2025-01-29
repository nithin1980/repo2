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
import com.mod.enums.KiteDataConstant;
import com.mod.interfaces.BusinessInterface;
import com.mod.interfaces.SystemInterface;
import com.mod.interfaces.kite.LTPKiteAPIWebsocket;
import com.mod.objects.Position;
import com.mod.objects.PositionMetaData;
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

public class OpenHighLowModel extends ProcessModelAbstract {
	
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
	private static boolean positionCheckDone=false;
	private static boolean checkingForPosition=false;
	private static final OpenHighLowModel model = new OpenHighLowModel(CacheService.getInstance());
	
	//private final SystemInterface kiteInterface = KiteInterface.getInstance();
	private final SystemInterface kiteInterface =  TestKiteInterface.getInstance();
	
    private static List<String> stockkeys;
    
    private double posCost=250000.00;
    
    private long profitMonitorTime=0;
    
    private double slippage=0.5;
    
	private OpenHighLowModel(CacheService cacheService) {
		super();
		setCacheService(cacheService);

		trackPrices  = new ArrayList<Long>();
		notRequired = new ArrayList<Long>();

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
				
				if(stockListString!=null && stockListString.length>0) {
					for(int j=0;j<stockListString.length;j++) {
						trackPrices.add(Long.valueOf(stockListString[j]));
					}
				}
				
				sectorStockList.put(stockkeys.get(i), Arrays.asList(stockListString));
				
			}
		}
		
		System.out.println(modelid()+" All keys loaded for Open High Low");
		
	} 
	
	public static OpenHighLowModel getInstance() {
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
			System.out.println(modelid()+ "Error: Not ready: Processing high low");
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
				profitMonitorTime = System.currentTimeMillis();
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
			
			trackPrices.clear();
			
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
								takePosPosition(candle,EnumStratergyType.OpenHighLow_1,key);
								candleInThePositiveZone++;
								choosen =  true;
								//System.out.println(modelid()+" Pos:"+candle.getKey());
							}
							
							if((candle.getCandle().getChange()<=(-0.2)) && candle.getCandle().isOpenEqualToHigh()) {
								takeNegPosition(candle,EnumStratergyType.OpenHighLow_1,key);
								candleInTheNegativeZone++;
								choosen =  true;
								//System.out.println(modelid()+" Neg:"+candle.getKey());
							}

							
							if(candle.getCandle().isOpenEqualToLow() ) {
								takePosPosition(candle, EnumStratergyType.OpenHighLow_2,key);
								depthList.add(stock);
								choosen =  true;
								
							}
							
							if(candle.getCandle().isOpenEqualToHigh()) {
								takeNegPosition(candle, EnumStratergyType.OpenHighLow_2,key);
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
		
		
		if(notRequired.size()>0 && ApplicationHelper.getKiteTicker()!=null) {
			
			ApplicationHelper.getKiteTicker().unsubscribe(notRequired);
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
			
			System.out.println(modelid()+","+key+","+depthString);
			
			depthString = new StringBuilder(", sell,");
			depths = marketDepth.sell;
			for(int i=0;i<5;i++) {
				depthString.append(depths.get(i).getPrice()+":").append(depths.get(i).getQuantity()+",");
			}
			
			System.out.println(modelid()+","+key+","+depthString);
			
			
			
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
					
					// order here...
					
					CacheService.positionalData.set(positionKeys[i], psData);

				}
			}
			
			
			
		}
		
	}
	
	
	/**
	 * Calculate price and quantity. Calculate slippage and decide if this needs to be 
	 * taken.
	 * @param psData
	 * @param depths
	 * @return
	 */
	private PositionalData calculatePriceAndQuantity(PositionalData psData, List<Depth> depths) {
		
		double price = 0.00;
		int qt = 0;
		
		double totalval = 0;
		int totalqt = 0;
		
		/**
		 * If overall is less the posCost, then keep taking it.
		 */
		
		for(int i=0;i<depths.size();i++) {
			
			price = depths.get(i).getPrice();
			qt = depths.get(i).getQuantity();

			if(totalval<posCost) {
				
				boolean added =  false;

				if(((price*qt)+totalval) <=posCost) {
					
					totalval =  totalval+ ((price*qt));
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
				if( (((price*qt))+totalval)>posCost && !added){
					
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
			System.out.println(modelid()+","+psData.getKey()+", changing buy price from:"+psData.getBuyPrice()+","+(totalval/totalqt));
			
			psData.getMetadata().setSlippage((totalval/totalqt)-psData.getBuyPrice());
			
			psData.setBuyQuantity(totalqt);
			psData.setBuyPrice(totalval/totalqt);
			
			BusinessInterface.asynchCreateRegularOrder(psData);
			
			System.out.println(modelid()+","+psData.getKey()+", display long:"+psData.getBuyPrice()+","+psData.getBuyQuantity());
			
		}

		if(EnumPositionType.Short.equals(psData.getPositionType())) {
			System.out.println(modelid()+","+psData.getKey()+", changing sell price from:"+psData.getSellPrice()+","+(totalval/totalqt));
			
			psData.getMetadata().setSlippage(psData.getSellPrice()-(totalval/totalqt));
			
			psData.setSellQuantity(totalqt);
			psData.setSellPrice(totalval/totalqt);
			
			BusinessInterface.asynchCreateRegularOrder(psData);
			System.out.println(modelid()+","+psData.getKey()+", display short:"+psData.getSellPrice()+","+psData.getSellQuantity());
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
							psdata2.getMetadata().setFutLotSize(support.getLotSize());
							psdata2.getMetadata().setFutId(support.getFuture());
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
		
		double newSL=0;
		
		PositionMetaData mtdata = position.getMetadata();
		
		/**
		 * If SL already hit don't change.
		 */
		if(currentSL!=0 && currentPrice!=0 && currentPrice<=currentSL) {
			position.setCurrentSL(position.getExpectedSL());
			position.setHardSLinPlce(true);
			additionalBuySL(position, currentPrice);
			System.out.println(modelid()+","+position.getKey()+",BP:"+position.getBuyPrice()+",CP:"+currentPrice+",ESL:"+position.getExpectedSL()
			+":"+mtdata.getOpenPriceSL()+":"+mtdata.getPost60secSL()+":"+mtdata.getReachedBrkEvenSL()
			+":"+mtdata.getFirstSL()+":"+mtdata.getSecondSL()+":"+mtdata.getThirdSL());
			return;
		}
		
		if(position.isHardSLinPlce()) {
			additionalBuySL(position, currentPrice);
			System.out.println(modelid()+","+position.getKey()+",BP:"+position.getBuyPrice()+",CP:"+currentPrice+",ESL:"+position.getExpectedSL()
			+":"+mtdata.getOpenPriceSL()+":"+mtdata.getPost60secSL()+":"+mtdata.getReachedBrkEvenSL()
			+":"+mtdata.getFirstSL()+":"+mtdata.getSecondSL()+":"+mtdata.getThirdSL());
			return;
		}
		
		double priceDiffPer = ApplicationHelper.percen(currentPrice, buyPrice);
		
		if(priceDiffPer>=profitZone) {
			newSL = currentPrice *(1-(defaultSL/100)) ;
			
			if(newSL>=currentSL) {
				//need to change the SL
				
				position.setExpectedSL(newSL);
			}
			
			//System.out.println("Profit zone: CurrentSL="+currentSL+" buyprice:"+buyPrice+" currentprice:"+currentPrice+" newsl:"+newSL+" changing sl:"+changeSL);
			
		}
		
		else if(priceDiffPer<(-defaultSL)) {
			//newSL = buyPrice *(1-(defaultSL/100));
			//we wait for 15 mnts....
			
			//--System.out.println(modelid()+" "+position.getKey()+" Below SL zone:");
			//System.out.println("Below SL zone: CurrentSL="+currentSL+" buyprice:"+buyPrice+" currentprice:"+currentPrice+" newsl:"+newSL);
		}
		
		additionalBuySL(position, currentPrice);
		
		System.out.println(modelid()+","+position.getKey()+",BP:"+position.getBuyPrice()+",CP:"+currentPrice+",ESL:"+position.getExpectedSL()
							+":"+mtdata.getOpenPriceSL()+":"+mtdata.getPost60secSL()+":"+mtdata.getReachedBrkEvenSL()
							+":"+mtdata.getFirstSL()+":"+mtdata.getSecondSL()+":"+mtdata.getThirdSL());
		
		//List<PositionalData> dt =  CacheService.positionalData;
		
		placeSLOrders(position, currentPrice, "BUY");
	}
	
	private void additionalBuySL(PositionalData position,double currentPrice) {

       
		double currentSL = position.getMetadata().getFirstSL(); 
		double newSL=0;
		double profitZone=0.1;
		double defaultSL=0.3;
		double priceDiffPer = ApplicationHelper.percen(currentPrice, position.getBuyPrice());
		if(currentSL==0) {
			position.getMetadata().setFirstSL(position.getBuyPrice());
		}
		
		if(priceDiffPer>=profitZone) {
			newSL = currentPrice *(1-(defaultSL/100)) ;
			if(newSL>=currentSL) {
				position.getMetadata().setFirstSL(newSL);
			}
		}
		
/**
 * 		second SL
 */
		currentSL = position.getMetadata().getSecondSL();
		newSL=0;
		profitZone=0.1;
		defaultSL=0.1;
		priceDiffPer = ApplicationHelper.percen(currentPrice, position.getBuyPrice());
		if(currentSL==0) {
			position.getMetadata().setSecondSL(position.getBuyPrice());
		}
		
		if(priceDiffPer>=profitZone) {
			newSL = currentPrice *(1-(defaultSL/100)) ;
			if(newSL>=currentSL) {
				position.getMetadata().setSecondSL(newSL);
			}
		}

		
/**
 * 		60 second SL
 */

		currentSL = position.getMetadata().getPost60secSL();
		if(currentSL==0) {
			position.getMetadata().setPost60secSL(position.getBuyPrice());
		}
		if((System.currentTimeMillis()-profitMonitorTime)>60000
				&& position.getMetadata().getPost60secSL()!=position.getBuyPrice()) {
			position.getMetadata().setPost60secSL(currentPrice);
		}

/**
 * Once in profit move to buy		
 */
		currentSL = position.getMetadata().getReachedBrkEvenSL();
		profitZone=100.00;
		priceDiffPer = currentPrice-position.getBuyPrice();
		if(currentSL==0) {
			position.getMetadata().setReachedBrkEvenSL(position.getExpectedSL());
		}
		if((priceDiffPer*position.getBuyQuantity())>=profitZone) {
			position.getMetadata().setReachedBrkEvenSL(position.getBuyPrice());
		}
		
		
		
	}
	/**
	 * ALWAYS CHECK WHILE SETTING SL. 
	 * IT SHOULD NOT set FREAK values.
	 * @param position
	 * @param currentPrice
	 * @param sellPrice
	 * @param profitZone
	 * @param defaultSL
	 */
	public void sellSLCalculation(PositionalData position,double currentPrice,double sellPrice, double profitZone,double defaultSL) {
		
		//price difference should be positive.
		
		double currentSL = position.getExpectedSL(); //get this from the position api
		boolean changeSL=true;
		double newSL=0;
		
		PositionMetaData mtdata = position.getMetadata();

		if(currentSL!=0 && currentPrice!=0 && currentPrice>=currentSL) {
			position.setCurrentSL(position.getExpectedSL());
			position.setHardSLinPlce(true);
			additionalSellSL(position, currentPrice);
			System.out.println(modelid()+","+position.getKey()+",BP:"+position.getBuyPrice()+",CP:"+currentPrice+",ESL:"+position.getExpectedSL()
			+":"+mtdata.getOpenPriceSL()+":"+mtdata.getPost60secSL()+":"+mtdata.getReachedBrkEvenSL()
			+":"+mtdata.getFirstSL()+":"+mtdata.getSecondSL()+":"+mtdata.getThirdSL());
			return;
		}
		
		if(position.isHardSLinPlce()) {
			additionalSellSL(position, currentPrice);
			System.out.println(modelid()+","+position.getKey()+",BP:"+position.getBuyPrice()+",CP:"+currentPrice+",ESL:"+position.getExpectedSL()
			+":"+mtdata.getOpenPriceSL()+":"+mtdata.getPost60secSL()+":"+mtdata.getReachedBrkEvenSL()
			+":"+mtdata.getFirstSL()+":"+mtdata.getSecondSL()+":"+mtdata.getThirdSL());
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
		
		additionalSellSL(position, currentPrice);
		
		System.out.println(modelid()+","+position.getKey()+",SP:"+position.getSellPrice()+",CP:"+currentPrice+",ESL:"+position.getExpectedSL()
		+":"+mtdata.getOpenPriceSL()+":"+mtdata.getPost60secSL()+":"+mtdata.getReachedBrkEvenSL()
		+":"+mtdata.getFirstSL()+":"+mtdata.getSecondSL()+":"+mtdata.getThirdSL());
		
		/**
		 * Failure os position check will cause order placement failure.
		 *  Place order for SL only if the current price is close.
		 */
		placeSLOrders(position, currentPrice, "SELL");
		

		
	}
	
	/**
	 * IF THERE IS A POSITION CHECK FAILURE>>> THIS WILL RETURN
	 * @param position
	 * @param currentPrice
	 * @param type
	 */
	private void placeSLOrders(PositionalData position,double currentPrice,String type) {
		//Update the SL order
		//if the current price is closer to the expected SL, then needs to set up the order
		
		if(checkingForPosition && !positionCheckDone) {
			return;
		}
		double slpriceDiffPer = 0.0;
		
		if("BUY".equals(type)) {
			slpriceDiffPer = ApplicationHelper.percen(currentPrice,position.getExpectedSL());
		}
		
		if("SELL".equals(type)) {
			slpriceDiffPer = ApplicationHelper.percen(position.getExpectedSL(),currentPrice);
		}
		
//		try {
//			Thread.currentThread().sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		long timePassedAfterOrder = System.currentTimeMillis()-position.getMetadata().getOrderTime();
		
		if(timePassedAfterOrder>10000 
				&& !position.getMetadata().isOrderSucess()) {
			System.out.println("Error: Cannot change SL as position order is not sucessfull:"+position.getKey());
		}

		if(position.getMetadata().isOrderSucess()) {
				
					if(!positionCheckDone) {
						checkingForPosition=true;
						boolean isProcessSucess = BusinessInterface.queryAndUpdateKitePositionalData();
						
						if(!isProcessSucess) {
							//TODO fill what to do if there is a failure.....
						}
						
						positionCheckDone = true;
					}
					
					if(slpriceDiffPer<0.2 && position.getCurrentSL()!=position.getExpectedSL()) {
						if(position.isHardSLinPlce()) {
							//edit
							BusinessInterface.updateSLKiteOrder(position);
							position.setHardSLinPlce(true);
						}else {
							//place order
							BusinessInterface.createSLKiteOrder(position);
							position.setHardSLinPlce(true);
						}
						
					}
				
			}else {
				System.out.println("Error: Cannot change SL as position order is not sucessfull:"+position.getKey());
			}
		
		
		
	}
	
	private void additionalSellSL(PositionalData position,double currentPrice) {

		double currentSL = position.getMetadata().getFirstSL(); 
		double newSL=0;
		double profitZone=0.1;
		double defaultSL=0.3;
		double priceDiffPer = ApplicationHelper.percen(position.getSellPrice(),currentPrice);
		if(currentSL==0) {
			position.getMetadata().setFirstSL(position.getSellPrice());
		}
		
		if(priceDiffPer>=profitZone) {
			newSL = currentPrice *(1+(defaultSL/100)) ;
			if(newSL<=currentSL) {
				position.getMetadata().setFirstSL(newSL);
			}
		}
		
/**
 * 		second SL
 */
		currentSL = position.getMetadata().getSecondSL();
		newSL=0;
		profitZone=0.1;
		defaultSL=0.1;
		priceDiffPer = ApplicationHelper.percen(position.getSellPrice(),currentPrice);
		if(currentSL==0) {
			position.getMetadata().setSecondSL(position.getSellPrice());
		}
		
		if(priceDiffPer>=profitZone) {
			newSL = currentPrice *(1+(defaultSL/100)) ;
			if(newSL<=currentSL) {
				position.getMetadata().setSecondSL(newSL);
			}
		}

		
/**
 * 		60 second SL
 */

		currentSL = position.getMetadata().getPost60secSL();
		if(currentSL==0) {
			position.getMetadata().setPost60secSL(position.getSellPrice());
		}
		if((System.currentTimeMillis()-profitMonitorTime)>60000 &&
				position.getMetadata().getPost60secSL()!=position.getSellPrice()) {
			position.getMetadata().setPost60secSL(currentPrice);
		}

/**
 * Once in profit move to sell point		
 */
		currentSL = position.getMetadata().getReachedBrkEvenSL();
		profitZone=100.00;
		priceDiffPer = position.getSellPrice()-currentPrice;
		if(currentSL==0) {
			position.getMetadata().setReachedBrkEvenSL(position.getExpectedSL());
		}
		if((priceDiffPer*position.getSellQuantity())>=profitZone) {
			position.getMetadata().setReachedBrkEvenSL(position.getSellPrice());
		}
		
		
	}
	
	private void takePosPosition(CandleWrapper candle, EnumStratergyType stratergyType, String sector) {
		
		PositionalData position = new PositionalData();
		position.setKey(candle.getKey());
		position.setStratergyType(stratergyType);
		position.setPositionType(EnumPositionType.Long);
		position.setBuyPrice(CacheService.PRICE_LIST.get(candle.getKey()));
		position.getMetadata().setSector(sector);
		position.setTradeType(KiteDataConstant.Product_mis);
		
		if(!CacheService.stockMetadata.containsKey(candle.getKey())){
			throw new RuntimeException("Symbol cannot be set in the positional data for:"+candle.getKey());
		}
		
		position.setTradingSymbol(CacheService.stockMetadata.get(candle.getKey()).getName());
		
		/**
		 * This is to prevent freak prices
		 */
		position.getMetadata().setOpenPriceSL(withinPercenLimits(position.getBuyPrice(),candle.getCandle().getOpen(),3.0,EnumPositionType.Long));
		
		position.setExpectedSL(candle.getCandle().getLow());
		
		CacheService.positionalData.add(position);
		
		positionTaken=true;
		trackPrices.add(position.getKey());
		
		System.out.println(modelid()+" Buy Position:"+position.getKey()+" Price:"+position.getBuyPrice()+" Strat:"+stratergyType);
		
	}
	
	private void takeNegPosition(CandleWrapper candle, EnumStratergyType stratergyType, String sector) {
		PositionalData position = new PositionalData();
		position.setKey(candle.getKey());
		position.setStratergyType(stratergyType);
		position.setPositionType(EnumPositionType.Short);
		position.setSellPrice(CacheService.PRICE_LIST.get(candle.getKey()));
		position.getMetadata().setSector(sector);
		position.setTradeType(KiteDataConstant.Product_mis);

		if(!CacheService.stockMetadata.containsKey(candle.getKey())){
			throw new RuntimeException("Symbol cannot be set in the positional data for:"+candle.getKey());
		}
		
		position.setTradingSymbol(CacheService.stockMetadata.get(candle.getKey()).getName());

		/**
		 * This is to prevent freak prices
		 */

		position.getMetadata().setOpenPriceSL(withinPercenLimits(position.getSellPrice(),candle.getCandle().getOpen(),3.0,EnumPositionType.Short));
		
		position.setExpectedSL(candle.getCandle().getHigh());
		
		CacheService.positionalData.add(position);
		
		positionTaken=true;
		trackPrices.add(position.getKey());
		System.out.println(modelid()+" Sell Position:"+position.getKey()+" Price:"+position.getSellPrice()+" Strat:"+stratergyType);
		
	}
	
	private double withinPercenLimits(double current,double valueToSet,double percen, EnumPositionType type) {
		
		double newVal=0.0;
		
		
		if(EnumPositionType.Long.equals(type)) {
			
			if(ApplicationHelper.percen(valueToSet, current)<-10) {
				System.out.println(modelid()+" The current price is really low:"+current);
				return valueToSet;
			}
			
			newVal = current*(1-percen);
			if(newVal<valueToSet) {
				return valueToSet;
			}else {
				return newVal;
			}
		}

		if(EnumPositionType.Short.equals(type)) {
			
			if(ApplicationHelper.percen(current,valueToSet)<-10) {
				System.out.println(modelid()+" The current price is really high:"+current);
				return valueToSet;
			}
			
			newVal = current*(1+percen);
			if(newVal>valueToSet) {
				return valueToSet;
			}else {
				return newVal;
			}
		}
		
		System.out.println(modelid()+" cannot determine the transaction type, setting valuetoset ");
		return valueToSet;
		
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
						takePosPosition(data.get(i),EnumStratergyType.OpenHighLow_2,"not set");
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
