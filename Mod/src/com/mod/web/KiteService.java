package com.mod.web;

import com.mod.objects.GroupPosition;
import com.mod.objects.Position;
import com.mod.order.Order;
import com.mod.process.models.CacheService;
import com.mod.process.models.DashBoard;
import com.mod.support.ApplicationHelper;

public class KiteService {

	private static final Order orderInterface = new Order();
	
	public static void orderPE() throws RuntimeException{
		GroupPosition groupPosition = DashBoard.positionMap.get("pmodel10");
		String position_size = ApplicationHelper.modeConfig("pmodel10").getKeyValueConfigs().get("position_size");
		
		double pe_id = ApplicationHelper.getPositionId("pmodel10", "pe_id");
		groupPosition.getPePositions().add(new Position("PE", 100.00,CacheService.PRICE_LIST.get(pe_id)));
		orderInterface.orderKiteOption(null);
	}
	public static void orderCE() throws RuntimeException{
		GroupPosition groupPosition = DashBoard.positionMap.get("pmodel10");
		String position_size = ApplicationHelper.modeConfig("pmodel10").getKeyValueConfigs().get("position_size");
		
		double ce_id = ApplicationHelper.getPositionId("pmodel10", "ce_id");
		groupPosition.getCePositions().add(new Position("CE", 100.00,CacheService.PRICE_LIST.get(ce_id)));
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
	
}
