package com.test;

import java.util.ArrayList;

import com.zerodhatech.ticker.KiteTicker;

public class TestKiteTickerMock extends KiteTicker{

	public TestKiteTickerMock() {
		// TODO Auto-generated constructor stub
		super("","");
		
	}
	
	
	public void unsubscribe(ArrayList<Long> arg0) {
		// TODO Auto-generated method stub
		System.out.println("TestKiteTickerMock:Unsubscribed: "+arg0);
		
	}
	
	
	public void subscribe(ArrayList<Long> arg0) {
		// TODO Auto-generated method stub
		System.out.println("TestKiteTickerMock:Subscribed: "+arg0);
		
	}
	
	
}
