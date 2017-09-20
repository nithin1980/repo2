package com.mod.interfaces;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;










import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public class KiteInterface implements SystemInterface {

	@Override
	public void order(OrderInterfaceObject interfaceObject) {
		
		//KiteOrderObject kiteOrderObject = (KiteOrderObject)interfaceObject;
		String url = "https://kite.zerodha.com/api/marketwatch";
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet post = new HttpGet(url);
		
		//http://pp.axa-travel-insurance.com/AxaDE_B2B/login;jsessionid=FA1A27AD9305056ABB95647248D344C4.TI7A
		
		
//		post.addHeader("Host", "kite.zerodha.com");
//		post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0");
//		post.addHeader("Accept", "application/json, text/plain, */*");
//		post.addHeader("Accept-Language", "en-US,en;q=0.5");
//		post.addHeader("Accept-Encoding", "gzip, deflate, br");
//		post.addHeader("Cache-Control", "no-cache");
//		post.addHeader("Pragma", "no-cache");
//		post.addHeader("If-Modified-Since", "0");
//		post.addHeader("Content-Type", "application/json;charset=utf-8");
//		post.addHeader("Referer", "https://kite.zerodha.com/orderbook/");
//		post.addHeader("Content-Length", "311");
//		
//		post.addHeader("Cookie", "lang=en; __cfduid=d742ee155b21feaf4026d5014e34706541499707836; session=41fd7883-1343-4740-9ff6-ac5b3b0904a8");
//		post.addHeader("DNT", "1");
//		post.addHeader("Connection", "keep-alive");
		
		post.addHeader("Host", "kite.zerodha.com");
		post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
		post.addHeader("Accept", "application/json, text/plain, */*");
		post.addHeader("Accept-Language", "en-US,en;q=0.8");
		post.addHeader("Accept-Encoding", "gzip, deflate, br");
		//post.addHeader("Content-Type", "application/x-www-form-urlencoded");
		post.addHeader("Referer", "https://kite.zerodha.com/dashboard/?login=true");
		//this is a problem, it does not let to set the content length.
		//post.addHeader("Content-Length", "128");
		
		post.addHeader("Cookie", "__cfduid=d07a94ca71e72f44476c348fd001ecd381499431753; lang=en; session=102fb428-2a55-4883-bfc9-a36dd51f0c0b");
		post.addHeader("If-Modified-Since", "0");
		post.addHeader("Connection", "keep-alive");
		post.addHeader("Cache-Control", "no-cache");
		post.addHeader("Pragma", "no-cache");
		
		
//		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
//		urlParameters.add(new BasicNameValuePair("__fp", "h_HA5klazKw="));
//		urlParameters.add(new BasicNameValuePair("_sourcePage", "yfp1XQoFJkmHYzQ4nobxRuyKn_CNIWySzRlTyPTQI4Q="));
//		urlParameters.add(new BasicNameValuePair("login", "Einloggen"));
//		urlParameters.add(new BasicNameValuePair("password", "dertererer"));
//		urlParameters.add(new BasicNameValuePair("username", "test"));
		
		try {
			//post.setEntity(new UrlEncodedFormEntity(urlParameters));
			long t = System.currentTimeMillis();
			HttpResponse response = client.execute(post);
			
//			BufferedReader rd = new BufferedReader(
//			        new InputStreamReader(response.getEntity().getContent()));
//
//			StringBuffer result = new StringBuffer();
//			String line = "";
//			while ((line = rd.readLine()) != null) {
//				result.append(line);
//			}
//			System.out.println(System.currentTimeMillis()-t);
//			System.out.println(result);
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
		
		WebSocketClientExample.main(null);
		
		
	}
	public static void main(String[] args) {
		new KiteInterface().order(null);
	}
}
