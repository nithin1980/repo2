package com.mod.process.models;

import static com.mod.support.ApplicationHelper.getObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.mod.interfaces.KiteGeneralWebSocketClient;
import com.mod.objects.GroupPosition;
import com.mod.support.KiteCandleData;
import com.mod.web.KiteProcess;
import com.mod.web.UIRequestData;


public class DashBoard {

	public double overall;
	
	public static Map<String, GroupPosition> positionMap = new HashMap<String, GroupPosition>();
	
	public static KiteGeneralWebSocketClient kiteWebSocketClient;
	
	public static final KiteProcess kiteProcess = new KiteProcess();
	
	public static int allowedSeconds;
	
	public static String checkedTime; 
	
	public static long lastRecordTime=0;
	
	
	public static void enableStatusCheck() {
		System.out.println("Enable status check..");
		ScheduledExecutorService threadService = Executors.newSingleThreadScheduledExecutor();
		Runnable command = new Runnable() {
			@Override
			public void run() {
				 
				if(lastRecordTime>0 && ((System.currentTimeMillis()-lastRecordTime)>29000)){
					System.out.println("Connection check failed. Re-connecting...");
					if(kiteWebSocketClient!=null){
						kiteWebSocketClient.connect();
					}
				}
			}
		};
		threadService.scheduleAtFixedRate(command, 5, 15, TimeUnit.SECONDS);
		
	}

	public Map<String, GroupPosition> getPositionMap() {
		return positionMap;
	}
	
	public static void setKiteGenerlWebSocketClient(KiteGeneralWebSocketClient client){
		kiteWebSocketClient = client;
	}
	
	public static void parse_and_ProcessUIData(String data) {
		HashMap<String, String> requestData = null;
		try {
			requestData =  getObjectMapper().readValue(data, HashMap.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(requestData);
		
		
		
		
	}
	
	
	
}
