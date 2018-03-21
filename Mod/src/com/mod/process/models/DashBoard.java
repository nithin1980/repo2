package com.mod.process.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

import com.mod.interfaces.KiteGeneralWebSocketClient;
import com.mod.objects.GroupPosition;
import com.mod.support.ApplicationHelper;
import com.mod.support.ConfigData;
import com.mod.web.KiteProcess;


public class DashBoard {

	public double overall;
	
	public static Map<String, GroupPosition> positionMap = new HashMap<String, GroupPosition>();
	
	public static KiteGeneralWebSocketClient kiteWebSocketClient;
	
	public static final KiteProcess kiteProcess = new KiteProcess();
	
	public static int allowedSeconds;
	
	
	public DashBoard() {
		
	}

	public Map<String, GroupPosition> getPositionMap() {
		return positionMap;
	}
	
	public static void setKiteGenerlWebSocketClient(KiteGeneralWebSocketClient client){
		kiteWebSocketClient = client;
	}
	
}
