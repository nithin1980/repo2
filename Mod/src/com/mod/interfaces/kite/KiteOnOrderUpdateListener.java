package com.mod.interfaces.kite;

import com.mod.enums.EnumPositionType;
import com.mod.enums.KiteDataConstant;
import com.mod.interfaces.KiteInterface;
import com.mod.interfaces.KitePositionQueryResponse;
import com.mod.interfaces.SystemInterface;
import com.mod.objects.KitePositionDataLayer2;
import com.mod.objects.EnumPositionStatus;
import com.mod.objects.PositionalData;
import com.mod.process.models.CacheService;
import com.zerodhatech.models.Order;
import com.zerodhatech.ticker.OnOrderUpdate;

public class KiteOnOrderUpdateListener implements OnOrderUpdate {
	
	private final SystemInterface kiteInterface =  KiteInterface.getInstance();

	@Override
	public void onOrderUpdate(Order arg0) {
		// TODO Auto-generated method stub
		
		if(arg0!=null && KiteDataConstant.OrderStatus_COMPLETE.equalsIgnoreCase(arg0.status) && arg0.tradingSymbol!=null) {
			
			System.out.println("Order Listener:Processing for completed order:"+arg0.tradingSymbol+" order id:"+arg0.orderId);

			KitePositionQueryResponse response = (KitePositionQueryResponse) kiteInterface.queryCurrentPosition(null);
			
			if("success".equals(response.getStatus())) {
				KitePositionDataLayer2[] layer2 = response.getData().getNet();
				
				if(layer2!=null && layer2.length!=0) {
					
					for(int i=0;i<layer2.length;i++) {
						
						if(!ifPresentUpdate(layer2[i],arg0)) {
							PositionalData data = new PositionalData();
							data.setTradingSymbol(layer2[i].getTradingsymbol());
							
							data.setBuyPrice(layer2[i].getBuy_price());
					 		data.setKey(Long.valueOf(layer2[i].getInstrument_token()));
					 		
							
							data.setCount(1);
							if(layer2[i].getBuy_quantity()!=0) {
								data.setBuyQuantity(layer2[i].getBuy_quantity());
								data.setStatus(EnumPositionStatus.InPoistionLong);
							}
							if(layer2[i].getSell_quantity()!=0) {
								data.setSellQuantity(layer2[i].getSell_quantity());
								data.setStatus(EnumPositionStatus.InPositionShort);
							}
							System.out.println("Order Listener:New position added:"+layer2[i].getTradingsymbol()+" key id:"+layer2[i].getInstrument_token());
							CacheService.positionalData.add(data);
						}
						
						
					}				
				}
			}

		}

	}
	
	private boolean ifPresentUpdate(KitePositionDataLayer2 layer2, Order order) {
		
		
		
		Integer[] pos = CacheService.getInstance().findPositionsbyKey(Long.valueOf(layer2.getInstrument_token()),EnumPositionType.Both);
		
		if(pos!=null && pos.length>0) {
			
			PositionalData data =  CacheService.positionalData.get(pos[0]);
			
			if(layer2.getTradingsymbol().equalsIgnoreCase(data.getTradingSymbol())) {
				
				
			 if(data.getStopLossPosition()!=null 
					 && data.getStopLossPosition().getOrderId()==Long.valueOf(order.orderId)) {
				  data.setStatus(EnumPositionStatus.PositionClosed_WithSell);
				  data.getStopLossPosition().setOrderId(0);
				  System.out.println("Order Listener:Stop loss order executed:"+data.getTradingSymbol()+" order id:"+order.orderId);
				}
				
				
				data.setBuyPrice(layer2.getBuy_price());
				/**
				 * TODO need to set the selling price
				 */
				
				/**
				 * If this is waiting for SL order to be executed, then set the id to zero, 
				 * so that it is not updated again.
				 */
//				if(data.getStopLossPosition()!=null 
//						&& data.getStopLossPosition().getOrderId()==Long.valueOf(order.orderId)) {
//					data.getStopLossPosition().setOrderId(0);
//				}
				
				if(layer2.getBuy_quantity()!=0) {
					data.setBuyQuantity(layer2.getBuy_quantity());
				}
				if(layer2.getSell_quantity()!=0) {
					data.setSellQuantity(layer2.getSell_quantity());
				}
				CacheService.positionalData.set(pos[0],data);
				System.out.println("Order Listener:Position updated:"+data.getTradingSymbol()+" key id:"+data.getKey());
				
				return true;
			}
			
		}

		return false;
		
	}

}
