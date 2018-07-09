package com.mod.interfaces;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;













import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.mod.support.ApplicationHelper;
import com.mod.support.ConfigData;

public class KiteInterface implements SystemInterface {
	
	public ConfigData appConfig(){
		return ApplicationHelper.Application_Config_Cache.get("app");
	}
	
	@Override
	public void order(OrderInterfaceObject interfaceObject) {
		
		//KiteOrderObject kiteOrderObject = (KiteOrderObject)interfaceObject;
		Map<String, String> keyvalues = appConfig().getKeyValueConfigs();
		String url = keyvalues.get("kite_order_url");//  "https://kite.zerodha.com/api/orders";
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		
		//http://pp.axa-travel-insurance.com/AxaDE_B2B/login;jsessionid=FA1A27AD9305056ABB95647248D344C4.TI7A
		
		
		post.addHeader("Host", keyvalues.get("Host"));
		post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0");
		post.addHeader("Accept", "application/json, text/plain, */*");
		post.addHeader("Accept-Language", "en-US,en;q=0.5");
		post.addHeader("Accept-Encoding", "gzip, deflate, br");
		post.addHeader("Cache-Control", "no-cache");
		post.addHeader("Pragma", "no-cache");
		post.addHeader("If-Modified-Since", "0");
		post.addHeader("Content-Type", keyvalues.get("Content-Type"));
		post.addHeader("Referer", keyvalues.get("order_referrer"));
//		post.addHeader("Content-Length", "311");
//		
		post.addHeader("Cookie",keyvalues.get("Cookie"));
		post.addHeader("DNT", "1");
		post.addHeader("Connection", "keep-alive");
		
		String json="{\"exchange\":\"NFO\",\"tradingsymbol\":\"NIFTY17OCT10500CE\",\"transaction_type\":\"BUY\",\"order_type\":\"MARKET\",\"quantity\":\"75\",\"price\":\"0\",\"product\":\"NRML\",\"validity\":\"DAY\",\"disclosed_quantity\":\"0\",\"trigger_price\":\"0\",\"squareoff_value\":\"0\",\"squareoff\":\"0\",\"stoploss_value\":\"0\",\"stoploss\":\"0\",\"trailing_stoploss\":\"0\",\"variety\":\"amo\",\"client_id\":\"DV4051\"}";
		StringEntity requestEntity  =  new StringEntity(json,ContentType.APPLICATION_JSON);
		
//		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
//		urlParameters.add(new BasicNameValuePair("__fp", "h_HA5klazKw="));
//		urlParameters.add(new BasicNameValuePair("_sourcePage", "yfp1XQoFJkmHYzQ4nobxRuyKn_CNIWySzRlTyPTQI4Q="));
//		urlParameters.add(new BasicNameValuePair("login", "Einloggen"));
//		urlParameters.add(new BasicNameValuePair("password", "dertererer"));
//		urlParameters.add(new BasicNameValuePair("username", "test"));

		/*
		try {
			//post.setEntity(new UrlEncodedFormEntity(urlParameters));
			post.setEntity(requestEntity);
			long t = System.currentTimeMillis();
			HttpResponse response = client.execute(post);
			
			BufferedReader rd = new BufferedReader(
			        new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			System.out.println(System.currentTimeMillis()-t);
			System.out.println(result);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		//WebSocketClientExample.main(null);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public static void main(String[] args) {
		new KiteInterface().order(null);
	}
}
