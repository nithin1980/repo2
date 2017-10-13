package com.mod.interfaces;

import java.util.HashMap;
import java.util.Map;

public class KiteStockConverter {
	
	public static final Map<String, String> KITE_STOCK_LIST = new HashMap<String, String>();
	
	public KiteStockConverter() {
		// TODO Auto-generated constructor stub
		
		buildKite();
	}
	
	private void buildKite(){
		KITE_STOCK_LIST.put("408065", "NSE:infy");
	}

}
