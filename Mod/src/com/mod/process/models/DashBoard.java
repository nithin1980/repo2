package com.mod.process.models;

import java.util.HashMap;
import java.util.Map;

import com.mod.interfaces.KiteGeneralWebSocketClient;
import com.mod.objects.GroupPosition;
import com.mod.web.KiteProcess;


public class DashBoard {

	public double overall;
	
	public static Map<String, GroupPosition> positionMap = new HashMap<String, GroupPosition>();
	
	public static KiteGeneralWebSocketClient kiteWebSocketClient;
	
	public static final KiteProcess kiteProcess = new KiteProcess();
	
	public static int allowedSeconds;
	
	public static String checkedTime; 
	
	
	public DashBoard() {
		
	}

	public Map<String, GroupPosition> getPositionMap() {
		return positionMap;
	}
	
	public static void setKiteGenerlWebSocketClient(KiteGeneralWebSocketClient client){
		kiteWebSocketClient = client;
	}
	
}
