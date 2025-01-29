package com.mod.process.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONException;

import static com.mod.process.models.dashboard.BNFOptionSellingWithBuyDashboard.*;

import com.mod.enums.EnumPositionType;
import com.mod.enums.KiteDataConstant;
import com.mod.interfaces.BusinessInterface;
import com.mod.objects.PositionalData;
import com.mod.objects.StopLossPosition;
import com.mod.process.models.dashboard.BNFOptionSellingWithBuyDashboard;
import com.mod.support.ApplicationHelper;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Depth;
import com.zerodhatech.models.MarketDepth;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.Position;
import com.zerodhatech.models.Quote;

public class BNFOptionSellingWithBuy extends ProcessModelAbstract {

	
    static int counter=0;
	private static Map<Long, Double> PREVIOUS_lTP = new HashMap<Long, Double>(); 
    
	public static long startbotTime = 0;
	
	static List<Long> ceList = new ArrayList<Long>();
	static List<Long> peList = new ArrayList<Long>();
	
	public static List<Long> trackPrices = new ArrayList<Long>();
	
	static boolean sellPositionTaken=false;
    static boolean buyPositionTaken=false;
    
    static long ce_id=0;
    static long pe_id=0;
    static double profit=0.00;
    
    static final double priceLowerLimit=300.00;
    static final double priceHigherLimit=400.00;
    
    
    private static final int lotSize=15;
    
    private static final Double SL_BREAK_POINT =17.0;
    
        
    private static boolean orderCreationInProgress=false;
    
    private static boolean positionConfirmed=false;
    
    private static boolean checkingForPosition=false;
    
    private static boolean positionCheckComplete=false;
    
    
    public static double previousBNFClose=36650;
    
    private double priceLimit=230.00;
    
    private String ceLabel="";
    private String peLabel="";
    
    private static final long orderCheckingid=138258692;
    
    
    private static final BNFOptionSellingWithBuyDashboard dashboard = BNFOptionSellingWithBuyDashboard.getInstance();
    
    
    private static final BNFOptionSellingWithBuy instance = new BNFOptionSellingWithBuy(getCacheService());
    
	private BNFOptionSellingWithBuy(CacheService cacheService) {
		super();
		setCacheService(cacheService);

		List<String> ce_list =  modeConfig().getReferenceDataMap().get("ce_list");
		List<String> pe_list = modeConfig().getReferenceDataMap().get("pe_list");

		trackPrices.addAll(ce_list.stream().mapToLong(Long::parseLong).boxed().collect(Collectors.toList()));
		trackPrices.addAll(pe_list.stream().mapToLong(Long::parseLong).boxed().collect(Collectors.toList()));
		
	}    
	
	public static BNFOptionSellingWithBuy getInstance() {
		return instance;
	}
    
	@Override
	public String modelid() {
		// TODO Auto-generated method stub
		return "bnfsellwithbuy";
	}
	
	
	@Override
	public void processNow() {
		// TODO Auto-generated method stub
		
		//LocalTime current =  LocalTime.now();
		
		/**
		 * 
		 * How to handle changes to strike prices, when trying to place order?
		 * Need to process this only if the BN prices changes. ignore the strike prices...
		 * 
		 * also if the order creation is going on, it needs to go back.
		 * 
		 */
		
		if(orderCreationInProgress) {
			System.out.println("Order CIP.. GB");
			return;
		}
		
		if(checkingForPosition) {
			System.out.println("Position CIP.. GB");
			return;
			
		}
		
		if(positionCheckComplete &&  !positionConfirmed) {
			System.out.println("Position NC.. GB");
			return;
		}

		if(startbotTime==0) {
			System.out.println(modelid()+ " Not ready: Processing bnf");
			return;
		}
		
//		if((System.currentTimeMillis()-startbotTime)<5000) {
//			System.out.println(modelid()+ " Not time yet" + startbotTime+" current:"+System.currentTimeMillis());
//			return;
//		}
		
		
		double BN_price = CacheService.PRICE_LIST.get(CacheService.BN_KEY);
		
		
		if(BN_price>(previousBNFClose-priceLimit) && BN_price<(previousBNFClose+priceLimit) && !sellPositionTaken ) {
			System.out.println("Price not in position yet");
			return;
		}

		
		try {
			

			
			if(!sellPositionTaken) {
				
				processSellPositionNotTaken();
			}
			
			
			if(sellPositionTaken && !buyPositionTaken && positionConfirmed) {
				
				
				try {
				
				Integer[] positionKeys =  CacheService.getInstance().findPositionsbyKey(ce_id, EnumPositionType.Short);
				System.out.println("buypos:"+CacheService.getInstance().PRICE_LIST.get(ce_id)+"  "+ce_id+" "+pe_id+" "+CacheService.positionalData+" "+buyPositionTaken);
				PositionalData psData = CacheService.getInstance().positionalData.get(positionKeys[0]);

				
					
					//System.out.println(modelid()+"--CE:"+ApplicationHelper.percen(CacheService.PRICE_LIST.get(ce_id), psData.getSellPrice()));
				
				
				if(ApplicationHelper.percen(CacheService.PRICE_LIST.get(ce_id), psData.getSellPrice())>=SL_BREAK_POINT) {
					System.out.println(modelid()+" *********CE SL hit for*********:"+ce_id+" "+ApplicationHelper.percen(CacheService.PRICE_LIST.get(ce_id), psData.getSellPrice()));
					
					/**
					 * TODO this should be calculated correctly as I may not get the current price and 
					 * a limit claculated to prevent market price.
					 */
					psData.setExpectedSL(CacheService.PRICE_LIST.get(ce_id));
					
					/**
					 * This should be pre-created
					 */
					//BusinessInterface.createSLKiteOrder(psData);
					
					psData.getMetadata().setSlippage(psData.getSellPrice()-CacheService.PRICE_LIST.get(ce_id));
					psData.setSellPrice(0);
					psData.setBuyPrice(CacheService.PRICE_LIST.get(ce_id));
					psData.setPositionType(EnumPositionType.Long);
					
					CacheService.positionalData.set(positionKeys[0], psData);
					buyPositionTaken=true;
					System.out.println(modelid()+" -- Placing Buy position for CE:" + psData.getBuyPrice());
					
				}
				
				
				positionKeys =  CacheService.getInstance().findPositionsbyKey(pe_id, EnumPositionType.Short);
				
				psData = CacheService.positionalData.get(positionKeys[0]);

				System.out.println(modelid()+"--PE:"+ApplicationHelper.percen(CacheService.PRICE_LIST.get(pe_id), psData.getSellPrice()));

				
				if(ApplicationHelper.percen(CacheService.PRICE_LIST.get(pe_id), psData.getSellPrice())>=SL_BREAK_POINT) {
					System.out.println(modelid()+"*******PE SL hit for:*********"+pe_id+" "+ApplicationHelper.percen(CacheService.PRICE_LIST.get(pe_id), psData.getSellPrice())+","+CacheService.PRICE_LIST.get(pe_id)+","+psData.getSellPrice());
					
					psData.setExpectedSL(CacheService.PRICE_LIST.get(pe_id));
					
					//BusinessInterface.createSLKiteOrder(psData);
					
					psData.getMetadata().setSlippage(psData.getSellPrice()-CacheService.PRICE_LIST.get(pe_id));
					psData.setSellPrice(0);
					psData.setBuyPrice(CacheService.PRICE_LIST.get(pe_id));
					psData.setPositionType(EnumPositionType.Long);
					CacheService.positionalData.set(positionKeys[0], psData);
					buyPositionTaken=true;
					System.out.println(modelid()+" -- Placing Buy position for PE:" + psData.getBuyPrice());
					
				}
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			if(buyPositionTaken) {
				processBuyPositionTaken();
			}
			
			if(sellPositionTaken) {
				processsellPositionTaken();
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

		completedProcess=true;
	}
	
	
	private void closeAllOrdersAndPositions(List<Position> toBeClosed, List<String> orderIds) {
		

		
		if(toBeClosed==null) {
			return;
		}
		System.out.println("********************ZERODHA POSITION NOT CONFIRMED*******************");
		
		
		/**
		 * CANCELL ALL ORDERS
		 */
		
		try {
			
			
			if(orderIds!=null & orderIds.size()>0) {
				System.out.println("****Cancelling all orders****");
				String orderStr="";
				for(int i=0;i<orderIds.size();i++) {
					ApplicationHelper.getKiteSDK().cancelOrder(orderIds.get(i), KiteDataConstant.Variety_Regular);
					orderStr = orderStr+orderIds.get(i)+"-";
				}
				info.put(cancelledOrders, orderStr);
				info.put(cancelledAllOrders, true_str);
				
				
			}
			
			
			if(toBeClosed!=null && toBeClosed.size()>0) {
				
				System.out.println("****Closing all positions****");
			
				Iterator<Position> itr2 = toBeClosed.iterator();
				
				
				while(itr2.hasNext()) {
					
					Position pos = itr2.next();
					PositionalData positionalData = new PositionalData();
					positionalData.setTradingSymbol(pos.tradingSymbol);
					positionalData.setKey(Long.valueOf(pos.instrumentToken));
					
					double tobeClosedPrice = CacheService.PRICE_LIST.get(Long.valueOf(pos.instrumentToken));
					tobeClosedPrice = tobeClosedPrice+2.0;
					
					positionalData.setBuyPrice(tobeClosedPrice);
					positionalData.setBuyQuantity(lotSize);
					positionalData.setOrderType(KiteDataConstant.Order_Type_Limit);
					
					positionalData.setKey(Long.valueOf(pos.instrumentToken));
					positionalData.setTradeType(KiteDataConstant.Product_mis);
					
					
					
					BusinessInterface.synchronousCreateRegularOrder(positionalData);
					
					System.out.println("cancelling...."+pos.tradingSymbol);
					
					if(info.get(cancelledPosition)!=null) {
						info.put(cancelledPosition, info.get(cancelledPosition)+"--"+positionalData.getTradingSymbol());
					}else {
						info.put(cancelledPosition, positionalData.getTradingSymbol());
					}
					
					
				
				
				}
				info.put(allfailurePositionClosed, true_str);
				
			}
			
			//toBeClosed.instrumentToken
			
			
			//ApplicationHelper.getKiteSDK().
			
			/**
			 * CLOSE ALL POSITIONS
			 */
			
			
		} catch (KiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
	}
	
	private void processSellPositionNotTaken() {


		List<String> ce_list =  modeConfig().getReferenceDataMap().get("ce_list");
		List<String> pe_list = modeConfig().getReferenceDataMap().get("pe_list");
		
		
		List<Long> ce_pick=new ArrayList<Long>();
		List<Long> pe_pick=new ArrayList<Long>();
		
		ArrayList<Long> notRequired;
		notRequired = new ArrayList<Long>();
		try {
			
			
			Iterator<String> itr = ce_list.iterator();
			Long key = null;
			Double price = 0.00;
			while(itr.hasNext()) {
				
				key = Long.valueOf(itr.next());
				price = CacheService.PRICE_LIST.get(key);
				if(price>=priceLowerLimit && price<=priceHigherLimit) {
					ce_pick.add(key);
				}else {
					notRequired.add(key);
				}
			}
			
			
			itr = pe_list.iterator();
			
			while(itr.hasNext()) {
				
				key = Long.valueOf(itr.next());
				price = CacheService.PRICE_LIST.get(key);
				
				
				
				if(price>=priceLowerLimit && price<=priceHigherLimit) {
					
					pe_pick.add(key);
				}else {
					notRequired.add(key);
				}
			}
			
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			
		}
		
		System.out.println(modelid()+"--ce pick:"+ce_pick+"---pe pick:"+pe_pick);
		
		//unscriscibe... in another thread.
		/**
		 * Make sure this doesn't unscribe the instrutments which are required in other strategy.************
		 */
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(notRequired.size()>0 && ApplicationHelper.getKiteTicker()!=null) {
					
					ApplicationHelper.getKiteTicker().unsubscribe(notRequired);
					notRequired.clear();
					System.out.println(modelid()+ " Unsubscribed");
					info.put(tickerUnsubcribed, true_str);
				}
				
			}
		}).start();

		
		
		
		if(ce_pick.size()==0) {
			throw new RuntimeException("No ce position picked.....");
		}
		if(pe_pick.size()==0) {
			throw new RuntimeException("No pe position picked.....");
		}
		
		double diff=0.00;
		double diffStr=0.00;
		
		orderCreationInProgress=true;
		
		boolean orderState = checkOrderingState();
		
		/**
		 * If order place check fails, then exit...
		 */
		if(!orderState) {
			orderCreationInProgress=true;
			return;
		}
		
		//orderCreationInProgress=false;
		
		
		
		for(int i=0;i<ce_pick.size();i++) {
			if(ce_id==0) {
				ce_id=ce_pick.get(i);
			}
			
			
			for(int j=0;j<pe_pick.size();j++) {
				if(pe_id==0) {
					pe_id=pe_pick.get(j);
				}
				
				diff = CacheService.PRICE_LIST.get(ce_pick.get(i))-CacheService.PRICE_LIST.get(pe_pick.get(j));
				
				if(diff<0) {
					diff=diff*-1;
				}
				
				if(diffStr==0) {
					diffStr = diff;
				}
				
				if(diff<diffStr) {
					ce_id=ce_pick.get(i);
					pe_id=pe_pick.get(j);	
					diffStr=diff;
				}
				
				

			}
		}
		
		System.out.println(modelid()+"--ce id:"+ce_id+" "+CacheService.PRICE_LIST.get(ce_id)+" "+"--pe_id:"+pe_id+" "+CacheService.PRICE_LIST.get(pe_id)+" ");
		
		
		PositionalData cePosition = new PositionalData();
		cePosition.setPositionType(EnumPositionType.Short);
		cePosition.setKey(ce_id);
		cePosition.setTradingSymbol(CacheService.stockMetadata.get(ce_id).getName());
		ceLabel=cePosition.getTradingSymbol();
		cePosition.setOrderType(KiteDataConstant.Order_Type_Limit);
		cePosition.setTradeType(KiteDataConstant.Product_mis);
	
		CacheService.positionalData.add(cePosition);
		info.put(CE_Name, cePosition.getTradingSymbol()+"-"+cePosition.getKey());
		
		calculatePriceAndQuantity(cePosition);
		
		
		PositionalData pePosition = new PositionalData();
		pePosition.setPositionType(EnumPositionType.Short);
		pePosition.setKey(pe_id);
		pePosition.setTradingSymbol(CacheService.stockMetadata.get(pe_id).getName());
		peLabel=pePosition.getTradingSymbol();
		pePosition.setTradeType(KiteDataConstant.Product_mis);
		pePosition.setOrderType(KiteDataConstant.Order_Type_Limit);
		CacheService.positionalData.add(pePosition);
		info.put(PE_Name, pePosition.getTradingSymbol()+"-"+pePosition.getKey());
		
		calculatePriceAndQuantity(pePosition);
		
		
		String[] depthList = new String[2];
		depthList[0]= String.valueOf(ce_id);
		depthList[1]= String.valueOf(pe_id);
		try {
			Map<String, Quote> quote = ApplicationHelper.getKiteSDK().getQuote(depthList); 
			
			System.out.println("quote:"+quote);
			
			info.put(FIRST_DEPTH_QUERY, true_str);
			
			if(quote!=null && quote.size()>0) {
				Iterator<String> keys = quote.keySet().iterator();
				while(keys.hasNext()) {
					Quote lquote =  quote.get(keys.next());
					if(lquote.instrumentToken==ce_id || lquote.instrumentToken==pe_id) {
						//displayDepth(lquote.depth, lquote.instrumentToken);
					}
					
					
				}
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			info.put(FIRST_DEPTH_QUERY, false_str);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			info.put(FIRST_DEPTH_QUERY, false_str);
			e.printStackTrace();
		} catch (KiteException e) {
			// TODO Auto-generated catch block
			info.put(FIRST_DEPTH_QUERY, false_str);
			e.printStackTrace();
		}
		
		
		if(dashboard.booleanValue(ce_sellAndSLPlaced) 
				&& dashboard.booleanValue(pe_sellAndSLPlaced)) {
			info.put(both_sellAndSLPlaced, true_str);
		}else {
			info.put(both_sellAndSLPlaced, false_str);
		}
		
		
		sellPositionTaken=true;
		
		
		
		
		
		if(sellPositionTaken & !positionConfirmed) {
			
			int localCount=0;
			
			System.out.println("Checking for confirmed position...");
			
			checkingForPosition=true;
			
			boolean positionFailed=false;
			
			List<Position> toBeClosed = new ArrayList<Position>();
			
			List<String> orderIds= new ArrayList<String>();
			
			try {
				List<Position> zerodhaPositions =  ApplicationHelper.getKiteSDK().getPositions().get("day");
				
				
				
				if(zerodhaPositions!=null && zerodhaPositions.size()>0) {
					
					Iterator<Position> itr =  zerodhaPositions.iterator();
					
					
					
					while(itr.hasNext()) {
						Position pst =  itr.next();
						
						
						
						if(ceLabel.equalsIgnoreCase(pst.tradingSymbol) || peLabel.equalsIgnoreCase(pst.tradingSymbol)) {
							localCount++;
							
							toBeClosed.add(pst);
						}
					}
					
				}
				
				
				
				List<Order> orders =   ApplicationHelper.getKiteSDK().getOrders();
				
				if(orders!=null & orders.size()>0) {
					Iterator<Order> itr =  orders.iterator();
					Order order = null;
					
					while(itr.hasNext()) {
						order = itr.next();
						
						if((KiteDataConstant.OrderStatus_OPEN.equalsIgnoreCase(order.status) || KiteDataConstant.OrderStatus_REJECTED.equalsIgnoreCase(order.status)) 
								&& (ceLabel.equalsIgnoreCase(order.tradingSymbol) || peLabel.equalsIgnoreCase(order.tradingSymbol))
						  ) {
							
							orderIds.add(order.orderId);
							
						}
					}
				}

				
				
			} catch (KiteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(toBeClosed.size()<2 || orderIds.size()<2 ) {
				positionFailed=true;
				closeAllOrdersAndPositions(toBeClosed,orderIds);
				
			}
			
			
			
			if(!positionFailed) {
				positionConfirmed=true;
			}
			
			positionCheckComplete=true;
			
			

			
		}

		
		
			
	}
	
	
	private boolean checkOrderingState() {
		
		PositionalData psData = new PositionalData();
		psData.setPositionType(EnumPositionType.Long);
		
		psData.setKey(orderCheckingid);
		psData.setTradingSymbol(CacheService.stockMetadata.get(orderCheckingid).getName());
		
		psData.setTradeType(KiteDataConstant.Product_cnc);
		psData.setOrderType(KiteDataConstant.Order_Type_Market);
		
		//psData.setSellPrice(currentPrice);
		psData.setBuyQuantity(1);
		psData.setBuyPrice(CacheService.PRICE_LIST.get(orderCheckingid));
		psData.getMetadata().setUpdatePostOrder(false);
		
		boolean positionCheck=false;
		
		
		try {
			BusinessInterface.synchronousCreateRegularOrder(psData);
			
			List<Position> zerodhaPositions =  ApplicationHelper.getKiteSDK().getPositions().get("day");
			
			if(zerodhaPositions!=null && zerodhaPositions.size()>0) {
				
				Iterator<Position> itr =  zerodhaPositions.iterator();
				
				while(itr.hasNext()) {
					Position pst =  itr.next();
					if(psData.getTradingSymbol().equalsIgnoreCase(pst.tradingSymbol)) {
						positionCheck=true;
						
					}
				}
				
			}		
			
			
		} catch (KiteException e) {
			// TODO Auto-generated catch block
			info.put(orderCheckStatus, false_str);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			info.put(orderCheckStatus, false_str);
			e.printStackTrace();
			return false;
		}
		
		
		if(!positionCheck) {
			info.put(orderCheckStatus, false_str);
			return false;
		}
		
		info.put(orderCheckStatus, true_str);
		
		return true;
	}
	
	
	private void processBuyPositionTaken() {
		
		Integer[] positionKeys =  CacheService.getInstance().findPositionsbyKey(ce_id, EnumPositionType.Long);
		
		if(positionKeys!=null && positionKeys.length>0) {
			PositionalData psData = CacheService.positionalData.get(positionKeys[0]);
			
			if(EnumPositionType.Long.equals(psData.getPositionType())) {
				profit = CacheService.PRICE_LIST.get(ce_id)-psData.getBuyPrice();
				System.out.println(modelid()+" ce position:"+ce_id+":long profit:"+profit+","+psData.getMetadata().getSlippage()+","+psData.getBuyPrice());
				
			}
		}
		
		positionKeys =  CacheService.getInstance().findPositionsbyKey(pe_id, EnumPositionType.Long);
		
		if(positionKeys!=null && positionKeys.length>0) {
			PositionalData psData = CacheService.positionalData.get(positionKeys[0]);
			
			if(EnumPositionType.Long.equals(psData.getPositionType())) {
				profit = CacheService.PRICE_LIST.get(pe_id)-psData.getBuyPrice();
				System.out.println(modelid()+" pe position:"+pe_id+":long profit:"+profit+","+psData.getMetadata().getSlippage()+","+psData.getBuyPrice()+","+CacheService.PRICE_LIST.get(psData.getKey()));
			}
		}

	}
	
	private void processsellPositionTaken() {
		  double localProfit = 0.0;
		  
			Integer[] positionKeys =  CacheService.getInstance().findPositionsbyKey(ce_id, EnumPositionType.Short);
			
			if(positionKeys!=null && positionKeys.length>0) {
				PositionalData psData = CacheService.positionalData.get(positionKeys[0]);
				
				if(EnumPositionType.Short.equals(psData.getPositionType())) {
					localProfit = psData.getSellPrice()-CacheService.PRICE_LIST.get(ce_id);
					System.out.println(modelid()+" ce position:"+ce_id+":Short profit:"+localProfit+","+psData.getSellPrice()+","+CacheService.PRICE_LIST.get(psData.getKey()));
					
				}
			}
			
			positionKeys =  CacheService.getInstance().findPositionsbyKey(pe_id, EnumPositionType.Short);
			if(positionKeys!=null && positionKeys.length>0) {
				
				PositionalData psData = CacheService.positionalData.get(positionKeys[0]);
				
				if(EnumPositionType.Short.equals(psData.getPositionType())) {
					localProfit = psData.getSellPrice()-CacheService.PRICE_LIST.get(pe_id);
					System.out.println(modelid()+" pe position:"+pe_id+":Short profit:"+localProfit);
				}
			}

	}
	
	private void displayDepth(MarketDepth marketDepth,long key) {
		
		
		
		List<Depth> depths = marketDepth.buy;
		
		if(depths!=null && depths.size()>0) {
			
			
			
			StringBuilder depthString = new StringBuilder(key+",depth buy,");
			for(int i=0;i<5;i++) {
				depthString.append(depths.get(i).getPrice()+":").append(depths.get(i).getQuantity()+",");
			}
			
			System.out.println(modelid()+","+key+","+depthString);
			
			depthString = new StringBuilder(",depth sell,");
			depths = marketDepth.sell;
			for(int i=0;i<5;i++) {
				depthString.append(depths.get(i).getPrice()+":").append(depths.get(i).getQuantity()+",");
			}
			
			System.out.println(modelid()+","+key+","+depthString);
			
			
			
			/**
			 * Now calculate position buy/sell size and cost
			 */

			Integer[] positionKeys =  CacheService.getInstance().findPositionsbyKey(key, EnumPositionType.Short);
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
					
					psData = calculatePriceAndQuantity(psData);
					
					
					
					CacheService.positionalData.set(positionKeys[i], psData);

				}
			}
			
		}
		
	}
	
	private PositionalData calculatePriceAndQuantity(PositionalData psData) {
		String optionType="";
		if(psData.getTradingSymbol().contains("CE")) {
			optionType="CE";
		}else {
			optionType="PE";
		}
		
		if(info.containsKey(ce_sellAndSLPlaced) && !dashboard.booleanValue(ce_sellAndSLPlaced)) {
			orderCreationInProgress=false;
			return psData;
		}
		
		if(info.containsKey(pe_sellAndSLPlaced) && !dashboard.booleanValue(pe_sellAndSLPlaced)) {
			orderCreationInProgress=false;
			return psData;
		}
		
		if(EnumPositionType.Short.equals(psData.getPositionType())) {
			double currentPrice = CacheService.PRICE_LIST.get(psData.getKey());
			
			/**
			 * @TODO The price needs to be modified.
			 */
			psData.setSellPrice(currentPrice);
			psData.setSellQuantity(lotSize);
			psData.setExpectedSL(psData.getSellPrice()*((100+SL_BREAK_POINT))/100);
			//BusinessInterface.createRegularOrder(psData);
			System.out.println(modelid()+","+psData.getKey()+", display short:"+psData.getSellPrice()+","+psData.getSellQuantity());
			psData.getMetadata().setUpdatePostOrder(true);
			
			/**
			 * Setting & ordering stop loss position
			 */

			
			orderCreationInProgress=true;
			
			try {
				psData =  BusinessInterface.createSLKiteOrder(psData);
				
				if(!psData.getMetadata().isOrderSucess()) {
					System.out.println("BNFOptions:order failure:"+psData.getTradingSymbol());
					if("CE".equalsIgnoreCase(optionType)) {
						info.put(ce_sellAndSLPlaced, false_str);	
					}else {
						info.put(pe_sellAndSLPlaced, false_str);
					}
				}else {
					if("CE".equalsIgnoreCase(optionType)) {
						info.put(ce_sellAndSLPlaced, true_str);	
					}else {
						info.put(pe_sellAndSLPlaced, true_str);
					}
				}
				
				
				
				
				
				

			} catch (RuntimeException e) {
				e.printStackTrace();
				
				
				if("CE".equalsIgnoreCase(optionType)) {
					info.put(ce_sellAndSLPlaced, false_str);	
				}else {
					info.put(pe_sellAndSLPlaced, false_str);
				}
				
				System.out.println("exception while creating order....");
			}
			
			
			if(info.containsKey(ce_sellAndSLPlaced) 
					&& info.containsKey(pe_sellAndSLPlaced)) {
				orderCreationInProgress=false;
			}
			
			
			
			
			
			//psData.getStopLossPosition().setPrice(stopLoss.getBuyPrice());

			
		}
		
		if(EnumPositionType.Long.equals(psData.getPositionType())) {
			
			double currentPrice = CacheService.PRICE_LIST.get(psData.getKey());
			
			psData.setBuyPrice(currentPrice);
			psData.setBuyQuantity(lotSize);
			
			psData.setExpectedSL(psData.getBuyPrice()*((100-SL_BREAK_POINT))/100);
			System.out.println(modelid()+","+psData.getKey()+", display long:"+psData.getBuyPrice()+","+psData.getBuyQuantity());
			
			
			/**
			 * Setting & ordering stop loss position
			 */
			psData.setStopLossPosition(new StopLossPosition());
			psData.getMetadata().setUpdatePostOrder(true);
			
			orderCreationInProgress=true;
			BusinessInterface.createSLKiteOrder(psData);
			orderCreationInProgress=false;
			
			info.put(buyAndSLPlaced, true_str);
			
			//psData.getStopLossPosition().setPrice(stopLoss.getBuyPrice());

		}
		
		return psData;

	}
	
	private void printPrices() {
		
		Iterator<Long> itr = getCacheService().PRICE_LIST.keySet().iterator();
		Long key =  null;
		while(itr.hasNext()) {
			key = itr.next();
			System.out.println("R_C,"+key.doubleValue()+","+getCacheService().PRICE_LIST.get(key).doubleValue());
			PREVIOUS_lTP.put(key, getCacheService().PRICE_LIST.get(key));
		}
		
		
	}
	
	

}
