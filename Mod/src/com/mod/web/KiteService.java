package com.mod.web;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.mod.interfaces.KiteStockConverter;
import com.mod.objects.GroupPosition;
import com.mod.objects.Position;
import com.mod.order.Order;
import com.mod.process.models.CacheService;
import com.mod.process.models.DashBoard;
import com.mod.support.ApplicationHelper;
import com.sun.javafx.css.CalculatedValue;

public class KiteService {

	private static final Order orderInterface = new Order();
	
	private static double positionVal(String modelKey){
		return Double.valueOf(ApplicationHelper.modeConfig(modelKey).getKeyValueConfigs().get("position_val"));
	}
	private static int lotsize(String modelKey){
		return Integer.valueOf(ApplicationHelper.modeConfig(modelKey).getKeyValueConfigs().get("lot_size"));
	}
	private static int calculateSize(double position_val,double cost,int lot_size ){
		int size = (int)(position_val/cost);
		
		if(((size+10)/lot_size)-(size/lot_size)==1){
			return (size+10)/lot_size;
		}else{
			return size/lot_size;
		}
		
	}
	public static void orderPE() throws RuntimeException{
		GroupPosition groupPosition = DashBoard.positionMap.get("pmodel10");
		
		double position_val = positionVal("pmodel10");
		int lot_size = lotsize("pmodel10");
		double pe_id = ApplicationHelper.getPositionId("pmodel10", "pe_id");
		double cost = CacheService.PRICE_LIST.get(pe_id);
		
		int size = calculateSize(position_val, cost, lot_size); 
				
				//(int)(Double.valueOf(position_val)/(cost*lot_size));
		size=size*lot_size;
		
		groupPosition.getPePositions().add(new Position("PE", 100.00,cost,size));
		
		orderInterface.orderKiteOption(null);
	}
	
	public static void orderCE() throws RuntimeException{
		GroupPosition groupPosition = DashBoard.positionMap.get("pmodel10");

		double position_val = positionVal("pmodel10");
		int lot_size = lotsize("pmodel10");
		
		double ce_id = ApplicationHelper.getPositionId("pmodel10", "ce_id");
		double cost = CacheService.PRICE_LIST.get(ce_id);

		int size =calculateSize(position_val, cost, lot_size); 
				//(int)(Double.valueOf(position_val)/(cost*lot_size));
		size=size*lot_size;
		
		groupPosition.getCePositions().add(new Position("CE", 100.00,cost,size));
		
		orderInterface.orderKiteOption(null);
	}
	public static void orderBoth() throws RuntimeException{
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				orderPE();
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				orderCE();
			}
		}).start();
	}
	
	public static void orderEquals() throws RuntimeException{
		GroupPosition groupPosition = DashBoard.positionMap.get("pmodel11");

		double position_val = positionVal("pmodel11");
		int lot_size = lotsize("pmodel11");
		
		double ce_id = ApplicationHelper.getPositionId("pmodel11", "ce_id");
		double pe_id = ApplicationHelper.getPositionId("pmodel11", "pe_id");
		
		double minPrice = 35.00;
		double maxPrice= 50.00;
		Iterator<Double> keys  = KiteStockConverter.BN_CE_LIST.keySet().iterator();
		double currentCost = 0;
		Double key = null;
		while(keys.hasNext()){
			key = keys.next();
			if(CacheService.PRICE_LIST.get(key)>minPrice && CacheService.PRICE_LIST.get(key)<maxPrice ){
				currentCost = CacheService.PRICE_LIST.get(key);
			}
		}
		
		double cost = CacheService.PRICE_LIST.get(ce_id);

		int size =calculateSize(position_val, cost, lot_size); 
				//(int)(Double.valueOf(position_val)/(cost*lot_size));
		size=size*lot_size;
		
		groupPosition.getCePositions().add(new Position("CE", 100.00,cost,size));
		
		orderInterface.orderKiteOption(null);
	}
	
	
}
