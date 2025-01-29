package com.mod.process.models.dashboard;

import java.util.HashMap;
import java.util.Map;

public class BNFOptionSellingWithBuyDashboard {
//	public static final Map<String, Boolean> Steps = new HashMap<String, Boolean>();
	
	public static final Map<String, String> info = new HashMap<String, String>();
	
	private static final BNFOptionSellingWithBuyDashboard instance = new BNFOptionSellingWithBuyDashboard();
	
	
	public static String true_str = "true";
	public static String false_str = "false";
	
	public static String tickerUnsubcribed = "tickerUnsubcribed";
	public static String ce_sellAndSLPlaced = "ce_sellAndSLPlaced";
	public static String pe_sellAndSLPlaced = "pe_sellAndSLPlaced";
	public static String both_sellAndSLPlaced = "both_sellAndSLPlaced";
	public static String buyAndSLPlaced = "buyAndSLPlaced";
	public static String cancelledAllOrders = "cancelledAllOrders";
	public static String cancelledOrders = "cancelledOrders";
	public static String allfailurePositionClosed = "allfailurePositionClosed";
	public static String cancelledPosition = "cancelledPosition";
	public static String orderCheckStatus = "orderCheckStatus";
	public static String FIRST_DEPTH_QUERY = "FIRST_DEPTH_QUERY";
	
	public static String CE_Name="CE_Name";
	public static String PE_Name="PE_Name";
	
	
	
	
	private BNFOptionSellingWithBuyDashboard() {
		// TODO Auto-generated constructor stub
	}
	
	public static BNFOptionSellingWithBuyDashboard getInstance() {
		return instance;
	}
	
	
	public boolean booleanValue(String key) {
		
		if(info.containsKey(key)) {
			
			String val = info.get(key);
			
			if(true_str.equalsIgnoreCase(val)) {
				return true;
			}else {
				return false;
			}
			
		} 
		
		return false;
		
	}
	
}
