package com.mod.interfaces;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mod.support.ApplicationHelper;
import com.mod.support.ConfigData;

public class KiteStockConverter {
	
	public static final Map<Double, String> KITE_STOCK_LIST = new HashMap<Double, String>();
	public static final Map<Double, String> BN_PE_LIST = new HashMap<Double, String>();
	public static final Map<Double, String> BN_CE_LIST = new HashMap<Double, String>();
	
	
	private static ConfigData appConfig(){
		return ApplicationHelper.Application_Config_Cache.get("app");
	}
	static{
		buildKite();
	}
	
	public KiteStockConverter() {
		// TODO Auto-generated constructor stub
	}
	
	private static void buildKite(){
		List<String> data = appConfig().getReferenceDataMap().get("kite_token_list");
		int size = data.size();
		String[] values = null;
		for(int i=0;i<size;i++){
			values = values(data.get(i));
			KITE_STOCK_LIST.put(Double.valueOf(values[0]), values[1]);
			if(values[1].contains("BANKNIFTY") && values[1].contains("CE")){
				BN_CE_LIST.put(Double.valueOf(values[0]), values[1]);
			}
			if(values[1].contains("BANKNIFTY") && values[1].contains("PE")){
				BN_PE_LIST.put(Double.valueOf(values[0]), values[1]);
			}
		}
	}
	
	private static String[] values(String data){
		return data.split("\\,");
	}

	public static void build(){
		
	}
}
