package com.mod.interfaces;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;

import com.mod.enums.EnumOrderStatus;
import com.mod.enums.EnumPositionType;
import com.mod.enums.KiteDataConstant;
import com.mod.objects.KitePositionDataLayer2;
import com.mod.objects.PositionalData;
import com.mod.objects.StopLossPosition;
import com.mod.process.models.CacheService;
import com.test.inf.TestKiteInterface;

import static com.mod.support.ApplicationHelper.*;

public class BusinessInterface {
	
	private static final double buyOrderPlacingPercen= 90.00;
	private static final double sellOrderPlacingPercen= 110.00;
	
	private static final SystemInterface kiteInterface = KiteInterface.getInstance();
	//private static final SystemInterface kiteInterface =  TestKiteInterface.getInstance();
	
	//private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

//	static {
//		decimalFormat.setRoundingMode(RoundingMode.UP);
//	}
	
	
	
	public static boolean queryAndUpdateKitePositionalData() {
		KitePositionQueryObject queryPositionObject = new KitePositionQueryObject();
		
		KitePositionQueryResponse response =  (KitePositionQueryResponse)kiteInterface.queryCurrentPosition(queryPositionObject);
		
		if(response!=null && response.getData()!=null 
				&& response.getData().getDay()!=null 
				&& response.getData().getDay().length>0) {
			
			
			if(response.getStatus().contains("FAILED")) {
				return false;
			}
			
			KitePositionDataLayer2[] dataLy2 =  response.getData().getDay();
			for(int i=0;i<dataLy2.length;i++) {
				
				long instrKey = Long.valueOf(dataLy2[i].getInstrument_token());
				
				Integer[] pos = CacheService.getInstance().findPositionsbyKey(instrKey,EnumPositionType.Both);
				if(pos!=null & pos.length>0) {
					PositionalData positionData =  CacheService.positionalData.get(pos[0]);
					if(positionData.getBuyPrice()!=0) {
						positionData.setBuyQuantity(dataLy2[i].getBuy_quantity());
						positionData.setBuyPrice(dataLy2[i].getBuy_price());
						System.out.println("Updating buy quantity & price from position:"+positionData.getKey());
					}
					
					if(positionData.getSellPrice()!=0) {
						positionData.setSellQuantity(dataLy2[i].getSell_quantity());
						positionData.setSellPrice(dataLy2[i].getSell_price());
						System.out.println("Updating sell quantity & price from position:"+positionData.getKey());
					}
					
				}
				
				
				
			}
		}
		
		
		return true;
		
	}
	
	public static PositionalData asynchCreateRegularOrder(final PositionalData position) {
		
		final KiteCreateOrderRequest request =  new KiteCreateOrderRequest();
		
		String actionType = null;
		
		String price = null;

		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		
		try {
		
				
				/**
				 * For same of testing, reducing the price by 10%
				 * 
				 * **************SETTING PRICE AT 90%**************************
				 */

		
				if(position.getBuyQuantity()!=0) {
					request.setQuantity(position.getBuyQuantity());
					actionType = KiteDataConstant.Transaction_buy;
					decimalFormat.setRoundingMode(RoundingMode.UP);
					/**
					 * NEED TO SET A HIGHER PRICES, SO THAT I COLLECTS ALL OF THE CURRENT PRICES THAT IS AVAILABLE.
					 */
					price = decimalFormat.format(givePercenValue(position.getBuyPrice(),buyOrderPlacingPercen));
				}else {
					request.setQuantity(position.getSellQuantity());
					actionType = KiteDataConstant.Transaction_sell;
					decimalFormat.setRoundingMode(RoundingMode.DOWN);
					price = decimalFormat.format(givePercenValue(position.getSellPrice(),sellOrderPlacingPercen));
				}
				
				request.setTradingSymbol(position.getTradingSymbol());
				request.setExchange(KiteDataConstant.Exchange_NSE);
				request.setVariety(KiteDataConstant.Variety_Regular);
				
				
				request.setTransactionType(actionType);
				request.setOrderType(position.getOrderType());
				
				
				request.setValidity(KiteDataConstant.Validity_day);
				//SL can be only be set for MIS
				request.setProduct(position.getTradeType());
				

		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("The price is not formattable for create order:"+position.getKey()+":"+position.getExpectedSL());
		}
		
		request.setPrice(price);
		
		System.out.println("BusinessInterface: order created at price:"+price+" with type:"+actionType+" time:"+new Date());
		
		

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				position.getMetadata().setOrderPlaced(true);
				
					KiteCreateOrderResponse response = (KiteCreateOrderResponse)kiteInterface.createOrder(request);

					if(KiteDataConstant.OrderStatus_COMPLETE.equalsIgnoreCase(response.getStatus()) || KiteDataConstant.OrderStatus_OPEN.equalsIgnoreCase(response.getStatus())) {
						position.getMetadata().setOrderId(response.getData().getOrderId());
						position.getMetadata().setOrderSucess(true);

					}else {
						
						position.getMetadata().setOrderSucess(false);
						System.out.println("asynchCreateRegularOrder:Error: Bad response recieved for key:"+position.getKey()+" "+position.getConfigData().getName()+ " status:"+response.getStatus());
						
					}
					
					
					
					
					
					
					
				if(position.getMetadata().isUpdatePostOrder()) {	
					updatePos(position);				
				}
			}
		}).start();
		
		
		return position;
	}
	
	
	public static PositionalData synchronousCreateRegularOrder(final PositionalData position) {
		
		final KiteCreateOrderRequest request =  new KiteCreateOrderRequest();
		
		String actionType = null;
		
		String price = null;

		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		
		try {
		
				
				/**
				 * For same of testing, reducing the price by 10%
				 * 
				 * **************SETTING PRICE AT 90%**************************
				 */
			
			/**
			 * If selling, I need to sell at lower price to trigger the first one. or sell at the second depth, if not enough present on the first one
			 * But for testing i need to sell at higher price, to prevent the trigger.
			 * 
			 * If buying, I need to buy at higher price to trigger the first one. or buy at the second depth, if not enough present on the first one
			 * But for testing i need to buy at lower price, to prevent the trigger.
			 * 
			 * 
			 */
			

		
				if(position.getBuyQuantity()!=0) {
					request.setQuantity(position.getBuyQuantity());
					actionType = KiteDataConstant.Transaction_buy;
					decimalFormat.setRoundingMode(RoundingMode.DOWN);
					/**
					 * NEED TO SET A HIGHER PRICES, SO THAT I COLLECTS ALL OF THE CURRENT PRICES THAT IS AVAILABLE.
					 */
					price = decimalFormat.format(givePercenValue(position.getBuyPrice(),buyOrderPlacingPercen));
				}else {
					request.setQuantity(position.getSellQuantity());
					actionType = KiteDataConstant.Transaction_sell;
					decimalFormat.setRoundingMode(RoundingMode.UP);
					price = decimalFormat.format(givePercenValue(position.getSellPrice(),sellOrderPlacingPercen));
				}
				
				request.setTradingSymbol(position.getTradingSymbol());
				request.setExchange(KiteDataConstant.Exchange_NSE);
				request.setVariety(KiteDataConstant.Variety_Regular);
				
				
				request.setTransactionType(actionType);
				request.setOrderType(position.getOrderType());
				request.setValidity(KiteDataConstant.Validity_day);
				//SL can be only be set for MIS
				request.setProduct(position.getTradeType());
				

		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("The price is not formattable for create order:"+position.getKey()+":"+position.getExpectedSL());
		}
		
		request.setPrice(price);
		
		System.out.println("BusinessInterface: order created at price:"+price+" with type:"+actionType+" time:"+new Date());
		

		position.getMetadata().setOrderPlaced(true);
		
		KiteCreateOrderResponse response = (KiteCreateOrderResponse)kiteInterface.createOrder(request);

		
		if(KiteDataConstant.OrderStatus_COMPLETE.equalsIgnoreCase(response.getStatus()) || KiteDataConstant.OrderStatus_OPEN.equalsIgnoreCase(response.getStatus())) {
			position.getMetadata().setOrderId(response.getData().getOrderId());
			position.getMetadata().setOrderSucess(true);
			
		}else {
			position.getMetadata().setOrderSucess(false);
			System.out.println("synchronousCreateRegularOrder:Error: Bad response recieved for key:"+position.getKey()+" "+position.getTradingSymbol()+ " status:"+response.getStatus());
		}
		
		if(position.getMetadata().isUpdatePostOrder()) {
			updatePos(position);
		}
						


		
		
		return position;
	}
	
	
	public static PositionalData createSLKiteOrder(final PositionalData position) {
		
		synchronousCreateRegularOrder(position);
		
		if(!position.getMetadata().isOrderSucess()) {
			throw new RuntimeException("Could not place main synch order(with SL) for: "+position.getKey()+" name:"+position.getTradingSymbol()+ " ignoring the SL entry");
		}
		
		
		
		final KiteCreateOrderRequest request =  new KiteCreateOrderRequest();
		
		position.getMetadata().setOrderPlaced(false);
		position.getMetadata().setOrderSucess(false);
		
		
		String actionType = null;
		
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		String price = null;
		
		
		
		try {
			
			/**
			 * For same of testing, reducing the price by 10%
			 * 
			 * **************SETTING PRICE AT 90%**************************
			 */
			
			/**
			 * If selling, I need to sell at lower price to trigger the first one. or sell at the second depth, if not enough present on the first one
			 * But for testing i need to sell at higher price, to prevent the trigger.
			 * 
			 * If buying, I need to buy at higher price to trigger the first one. or buy at the second depth, if not enough present on the first one
			 * But for testing i need to buy at lower price, to prevent the trigger.
			 * 
			 * 
			 */
			
			
			if(position.getBuyQuantity()!=0) {
				request.setQuantity(position.getBuyQuantity());
				actionType = KiteDataConstant.Transaction_sell;
				decimalFormat.setRoundingMode(RoundingMode.UP);
				
				price = decimalFormat.format(givePercenValue(position.getExpectedSL(),sellOrderPlacingPercen));

			}else {
				request.setQuantity(position.getSellQuantity());
				actionType = KiteDataConstant.Transaction_buy;
				decimalFormat.setRoundingMode(RoundingMode.DOWN);
				
				price = decimalFormat.format(givePercenValue(position.getExpectedSL(),buyOrderPlacingPercen));

			}
			

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("The expected SL price is not formattable for create order:"+position.getKey()+":"+position.getExpectedSL());
		}

		
		
		
		
		request.setTradingSymbol(position.getTradingSymbol());
		request.setExchange(KiteDataConstant.Exchange_NSE);
		request.setVariety(KiteDataConstant.Variety_Regular);
		
		
		request.setTransactionType(actionType);
		request.setOrderType(position.getOrderType());
		request.setValidity(KiteDataConstant.Validity_day);
		//SL can be only be set for MIS
		request.setProduct(position.getTradeType());
		
		
		if(KiteDataConstant.Transaction_sell.equals(actionType)) {
			decimalFormat.setRoundingMode(RoundingMode.UP);
		}else {
			decimalFormat.setRoundingMode(RoundingMode.DOWN);
		}
		
		
		request.setPrice(price);
		
		System.out.println("BusinessInterface: SL order created at price:"+price+" with type:"+actionType+" time:"+new Date());
		
		
		try {
			KiteCreateOrderResponse response = (KiteCreateOrderResponse)kiteInterface.createOrder(request);

			if(KiteDataConstant.OrderStatus_COMPLETE.equalsIgnoreCase(response.getStatus()) || KiteDataConstant.OrderStatus_OPEN.equalsIgnoreCase(response.getStatus())) {
				
				
				
				StopLossPosition slPosition = new StopLossPosition();
				slPosition.setOrderId(response.getData().getOrderId());
				if(KiteDataConstant.Transaction_buy.equals(request.getTransactionType())) {
					slPosition.setBuyquantity(request.getQuantity());
				}else {
					slPosition.setSellquantity(request.getQuantity());
				}
				slPosition.setPositionType(request.getTransactionType());
				slPosition.setPrice(Double.valueOf(request.getPrice()));
				slPosition.setSlreason(position.getStopLossType());
				slPosition.setOrderStatus(EnumOrderStatus.New);
				
				position.setStopLossPosition(slPosition);
				position.getMetadata().setOrderSucess(true);
				
				
			}else {
				
				position.getMetadata().setOrderSucess(false);
				System.out.println("BI:createSLKiteOrder: Error: Bad response recieved for key:"+position.getKey()+" "+position.getConfigData().getName()+" status:"+response.getStatus());
				
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			position.getMetadata().setOrderSucess(false);
			updatePos(position);
			
			throw new RuntimeException("Could not place SL order for: "+position.getKey()+" name:"+position.getTradingSymbol());
		} 
	
		updatePos(position);

		
		
		
		
		return position;
		
	}
	
	public static PositionalData updateSLKiteOrder(final PositionalData position) {
		
		final KiteUpdateOrderRequest request = new KiteUpdateOrderRequest();
		
		String actionType=null;
		
		
		if(position.getStopLossPosition()==null || position.getStopLossPosition().getOrderId()==0) {
			throw new RuntimeException(position.getTradingSymbol()+" doesn't have a stop loss position to be updated."+position.getStopLossPosition());
		}
		
		
		StopLossPosition slPosition = position.getStopLossPosition();
		
		request.setExchange(KiteDataConstant.Exchange_NSE);
		request.setOrderId(slPosition.getOrderId());
		
		
		String price = null;
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		
		if(KiteDataConstant.Transaction_sell.equals(actionType)) {
			decimalFormat.setRoundingMode(RoundingMode.UP);
		}else {
			decimalFormat.setRoundingMode(RoundingMode.DOWN);
		}
		
		try {
			if(slPosition.getBuyquantity()!=0) {
				request.setQuantity(slPosition.getBuyquantity());
				actionType = KiteDataConstant.Transaction_buy;
				price = decimalFormat.format(givePercenValue(position.getExpectedSL(),buyOrderPlacingPercen));
			}else {
				request.setQuantity(slPosition.getSellquantity());
				actionType = KiteDataConstant.Transaction_sell;
				price = decimalFormat.format(givePercenValue(position.getExpectedSL(),sellOrderPlacingPercen));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("The expected SL price is not formattable for update order:"+position.getKey()+":"+position.getExpectedSL());
		}
		
		request.setTradingSymbol(position.getTradingSymbol());
		request.setTransactionType(actionType);
		request.setVariety(KiteDataConstant.Variety_Regular);
		request.setOrderType(position.getOrderType());
		request.setValidity(KiteDataConstant.Validity_day);
		request.setProduct(position.getTradeType());
		//request.setSlTriggerPrice(null);
		

		
		
		request.setPrice(price);
		
		System.out.println("order updated at price:"+price+" with type:"+actionType+" time:"+new Date());
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub

					
					position.getMetadata().setOrderPlaced(true);
					KiteUpdateOrderRespone response =  (KiteUpdateOrderRespone)kiteInterface.updateOrder(request);
					
					if(KiteDataConstant.OrderStatus_COMPLETE.equals(response.getStatus()) || KiteDataConstant.OrderStatus_OPEN.equals(response.getStatus())) {
						
						position.getStopLossPosition().setSlreason(position.getStopLossType());
						position.getStopLossPosition().setPrice(position.getExpectedSL());
						position.getStopLossPosition().setOrderStatus(EnumOrderStatus.New);
						position.getMetadata().setOrderSucess(true);
						position.getStopLossPosition().setOrderId(response.getData().getOrderId());
						
						
					}else {
						position.getMetadata().setOrderSucess(false);
						
						System.out.println("updateSLKiteOrder:Error: Bad response recieved for order:"+position.getStopLossPosition().getOrderId()
								+" key:"+position.getKey()+" "+position.getConfigData().getName()+" status:"+response.getStatus());
						
					}
					
					updatePos(position);
					
					
			}
		}).start();
		
		
		return position;
		
	}
	
	
	private static void updatePos(final PositionalData position) {
		Integer[] indexs = CacheService.getInstance().findPositionsbyKey(position.getKey(),EnumPositionType.Both);
		CacheService.getInstance().positionalData.set(indexs[0],position);
		System.out.println("BusinessInterface:Updated positional data for:"+position.getKey());
		
	}
}
