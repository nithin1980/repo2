package com.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;

import com.mod.enums.KiteDataConstant;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Depth;
import com.zerodhatech.models.MarketDepth;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.OrderParams;
import com.zerodhatech.models.Position;
import com.zerodhatech.models.Quote;

public class TestKiteConnectMock extends KiteConnect{
	
	public static final Map<Long, Order> orderSystem = new HashMap<Long, Order>();
	
	public static final Map<String, List<Position>> positions = new HashMap<String, List<Position>>();
	
	
	public static boolean triggerFirstOrderError=false;
	
	/**
	 * Trigger failure of first SL
	 */
	public static boolean triggerFirstSLOrderFailure=false;
	
	
	/**
	 * Trigger failure of 3rd order
	 */
	public static boolean triggerThirdOrderFailure=false;
	
	/**
	 * Trigger final SL failure
	 */
			
	public static boolean triggerFinalSLOrderFailure=false;

	
	/**
	 * This will create position for CE, but will keep the remaining 3 orders open
	 */
	public static boolean triggerPositionFailure=false;

	
	public static boolean triggerPositionError=false;
	
	public static boolean triggerCancelOrderError=false;
	
	
	private static boolean SLFailureTriggered=false;
	
	
	private static boolean FinalSLFailureTriggered=false;
	
	private static boolean firstBuyAlreadyPlaced=false;

	public TestKiteConnectMock(String apiKey) {
		super(apiKey);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Order cancelOrder(String orderId, String variety) throws KiteException, JSONException, IOException {
		// TODO Auto-generated method stub
		
		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Order order =   orderSystem.get(Long.valueOf(orderId));
		order.status="cancelled";
		orderSystem.put(Long.valueOf(orderId), order);
		
		System.out.println("Order cancelled for:"+orderId+" "+order.tradingSymbol);
		
		return order;
	}
	
	@Override
	public List<Order> getOrders() throws KiteException, JSONException, IOException {
		// TODO Auto-generated method stub

		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<Order> orders = new ArrayList<Order>();
		
		if(orderSystem!=null && orderSystem.size()>0) {
			Iterator<Order> itr = orderSystem.values().iterator();
			
			while(itr.hasNext()) {
				orders.add(itr.next());
			}
		}
		
		
		
		return orders;
	}

	
	@Override
	public Map<String, List<Position>> getPositions() throws KiteException, JSONException, IOException {
		// TODO Auto-generated method stub
		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return positions;
	}
	
	@Override
	public Order placeOrder(OrderParams orderParams, String variety) throws KiteException, JSONException, IOException {
		// TODO Auto-generated method stub
		
		//Current status of the order. 
		//Most common values or COMPLETE, REJECTED, CANCELLED, and OPEN. There may be other values as well
		
		if(triggerFirstOrderError 
				&& !"BLS".equalsIgnoreCase(orderParams.tradingsymbol)
				) {
			throw new KiteException("Connection failure: triggerFirstOrderError");
		}
		
		if(triggerFirstSLOrderFailure && "buy".equalsIgnoreCase(orderParams.transactionType) && !SLFailureTriggered
				&& !"BLS".equalsIgnoreCase(orderParams.tradingsymbol)
				) {
			SLFailureTriggered=true;
			throw new KiteException("Connection failure: triggerFirstSLOrderFailure");
		}
		
		if(triggerThirdOrderFailure && "BANKNIFTY23APR37000PE".equalsIgnoreCase(orderParams.tradingsymbol)) {
			throw new KiteException("Connection failure: triggerThirdOrderFailure");
		}
		
		
		
		
		try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/** 
		 * Trigger final SL failure..
		 */
		if(triggerFinalSLOrderFailure && !FinalSLFailureTriggered && firstBuyAlreadyPlaced && "buy".equalsIgnoreCase(orderParams.transactionType)) {
			FinalSLFailureTriggered=true;
			throw new KiteException("Connection failure:FinalSLFailureTriggered");
		}
		
		if("buy".equalsIgnoreCase(orderParams.transactionType) && !"BLS".equalsIgnoreCase(orderParams.tradingsymbol)) {
			firstBuyAlreadyPlaced=true;
		}


		long newId = new Random().nextLong();
		
		if(newId<0) {
			newId=newId*-1;
		}
		
		Order order = new Order();
		
		List<Position> dayPos = positions.get("day");
		
		if(dayPos==null) {
			positions.put("day", new ArrayList<Position>());
			dayPos = positions.get("day");
		}
		
		if(triggerPositionFailure) {
		
			if("BANKNIFTY23APR36900CE".equalsIgnoreCase(orderParams.tradingsymbol) 
					&& "SELL".equals(orderParams.transactionType)) {
				
				order.status=KiteDataConstant.OrderStatus_COMPLETE;
				
				
				Position pos = new Position();
				pos.instrumentToken="9081346";
				pos.sellQuantity=15;
				pos.sellPrice=330.34;
				pos.tradingSymbol="BANKNIFTY23APR36900CE";
				
				dayPos.add(pos);
				
				
				positions.put("day", dayPos);
				
				System.out.println("TestKiteTickerMock:Position added for: "+pos.tradingSymbol);
				
			}else {
				order.status=KiteDataConstant.OrderStatus_OPEN;
			}
		
		}else {
			
			if("SELL".equalsIgnoreCase(orderParams.transactionType)) {
				
				order.status=KiteDataConstant.OrderStatus_COMPLETE;
				
				
				Position pos = new Position();
				
				if("BANKNIFTY23APR36900CE".equalsIgnoreCase(orderParams.tradingsymbol)) {
					pos.instrumentToken="9081346";
				}
				
				if("BANKNIFTY23APR37300PE".equalsIgnoreCase(orderParams.tradingsymbol)) {
					pos.instrumentToken="13625602";
				}
				
				
				//pos.instrumentToken=orderParams.
				pos.sellQuantity=15;
				pos.sellPrice=orderParams.price;
				pos.tradingSymbol=orderParams.tradingsymbol;
				
				
				dayPos.add(pos);
				
				System.out.println("testkitemock: adding pos:"+orderParams.tradingsymbol);
				
				positions.put("day", dayPos);

				
			}
			if("BUY".equalsIgnoreCase(orderParams.transactionType)) {
				
				order.status=KiteDataConstant.OrderStatus_OPEN;
				
				
//				Position pos = new Position();
//				
//				if("BANKNIFTY23APR36900CE".equalsIgnoreCase(orderParams.tradingsymbol)) {
//					pos.instrumentToken="9081346";
//				}
//				
//				if("BANKNIFTY23APR37000PE".equalsIgnoreCase(orderParams.tradingsymbol)) {
//					pos.instrumentToken="13625602";
//				}
//				
//				
//				//pos.instrumentToken=orderParams.
//				pos.sellQuantity=15;
//				pos.sellPrice=orderParams.price;
//				pos.tradingSymbol=orderParams.tradingsymbol;
//				
//				
//				dayPos.add(pos);
//				
//				
//				positions.put("day", dayPos);

				
			}
			
			
		}
		
		
		order.orderId=String.valueOf(newId);
		order.price=String.valueOf(orderParams.price);
		order.product=orderParams.product;
		order.tradingSymbol=orderParams.tradingsymbol;
		
		
		orderSystem.put(newId, order);
		
		if("BLS".equalsIgnoreCase(orderParams.tradingsymbol)) {
			
			Position pos = new Position();
			pos.instrumentToken="138258692";
			pos.buyQuantity=1;
			pos.sellPrice=orderParams.price;
			pos.tradingSymbol=orderParams.tradingsymbol;
			
			dayPos.add(pos);

			
			positions.put("day", dayPos);
		}
		
		System.out.println("TestKiteConnectMock:Positions"+positions);
		
		return order;
	}
	
	@Override
	public Order modifyOrder(String orderId, OrderParams orderParams, String variety)
			throws KiteException, JSONException, IOException {
		// TODO Auto-generated method stub
		return new Order();
	}
	
	@Override
	public Map<String, Quote> getQuote(String[] instruments) throws KiteException, JSONException, IOException {
		// TODO Auto-generated method stub
		
		Map<String, Quote> quotes = new HashMap<String, Quote>();
		
		Quote quote = new Quote();
	
		MarketDepth marketDepth = new MarketDepth();
		marketDepth.buy = new ArrayList<Depth>();
		marketDepth.sell = new ArrayList<Depth>();
		
		addBuys_415745(marketDepth);
		addSells_415745(marketDepth);
		quote.depth = marketDepth;
		
		
		quote.instrumentToken=415745;
		quotes.put("415745", quote);
		

		quote = new Quote();
		
		marketDepth = new MarketDepth();
		marketDepth.buy = new ArrayList<Depth>();
		marketDepth.sell = new ArrayList<Depth>();
		
		addBuys_10677506(marketDepth);
		addSells_10677506(marketDepth);
		quote.depth = marketDepth;
		
		
		quote.instrumentToken=10677506;
		quotes.put("10677506", quote);		
		

		//---------------------------------------------------------------
		quote = new Quote();
		
		marketDepth = new MarketDepth();
		marketDepth.buy = new ArrayList<Depth>();
		marketDepth.sell = new ArrayList<Depth>();
		
		addBuys_10681602(marketDepth);
		addSells_10681602(marketDepth);
		quote.depth = marketDepth;
		
		
		quote.instrumentToken=10681602;
		quotes.put("10681602", quote);		


		//----------------------------------------------------------------
		
		quote = new Quote();
		
		marketDepth = new MarketDepth();
		marketDepth.buy = new ArrayList<Depth>();
		marketDepth.sell = new ArrayList<Depth>();
		
		addBuys_9081346(marketDepth);
		addSells_9081346(marketDepth);
		quote.depth = marketDepth;
		
		
		quote.instrumentToken=9081346;
		quotes.put("9081346", quote);
		

		//---------------------------------------------------------------
		quote = new Quote();
		
		marketDepth = new MarketDepth();
		marketDepth.buy = new ArrayList<Depth>();
		marketDepth.sell = new ArrayList<Depth>();
		
		addBuys_13625602(marketDepth);
		addSells_13625602(marketDepth);
		quote.depth = marketDepth;
		
		
		quote.instrumentToken=13625602;
		quotes.put("13625602", quote);		
		
		
		
		
		return quotes;
	}


	
	private void addBuys_13625602(MarketDepth marketDepth) {
		Depth depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.2);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);
		
		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.3);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.4);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.5);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.6);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);
		
	

	}
	
	private void addSells_13625602(MarketDepth marketDepth) {
		Depth depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.20);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);
		
		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.00);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(299.00);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(298);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(297);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);
		
	}
	
	private void addBuys_9081346(MarketDepth marketDepth) {
		Depth depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.30);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);
		
		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.50);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.90);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(301.5);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(302.00);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);
		
	

	}
	
	private void addSells_9081346(MarketDepth marketDepth) {
		Depth depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(21.3);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);
		
		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(21.2);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(21.1);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(21.0);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(20.9);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);
		
	}
	
	private void addBuys_10681602(MarketDepth marketDepth) {
		Depth depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.2);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);
		
		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.3);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.4);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.5);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.6);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);
		
	

	}
	
	private void addSells_10681602(MarketDepth marketDepth) {
		Depth depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.20);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);
		
		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.00);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(299.00);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(298);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(297);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);
		
	}
	
	
	private void addBuys_10677506(MarketDepth marketDepth) {
		Depth depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.2);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);
		
		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.3);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.4);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.5);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.6);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);
		
	

	}
	
	private void addSells_10677506(MarketDepth marketDepth) {
		Depth depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.20);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);
		
		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.00);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(299.00);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(298);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(297);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);
		
	}
	
	
	private void addBuys_415745(MarketDepth marketDepth) {
		Depth depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.30);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);
		
		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.50);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(300.90);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(301.5);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(302.00);
		depth.setQuantity(100);
		marketDepth.buy.add(depth);
		
	

	}
	
	private void addSells_415745(MarketDepth marketDepth) {
		Depth depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(21.3);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);
		
		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(21.2);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(21.1);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(21.0);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);

		depth = new Depth();
		depth.setOrders(10);
		depth.setPrice(20.9);
		depth.setQuantity(100);
		marketDepth.sell.add(depth);
		
	}
}
