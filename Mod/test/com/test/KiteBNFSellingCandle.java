package com.test;

import java.util.List;

import com.mod.support.Candle;

public class KiteBNFSellingCandle {
	
	
	private String time;
	
	private List<Candle> candles;
	
	public KiteBNFSellingCandle() {
		// TODO Auto-generated constructor stub
	}
	
	public KiteBNFSellingCandle(String time, List<Candle> candles) {
		// TODO Auto-generated constructor stub
		setTime(time);
		setCandles(candles);
	}
	

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public List<Candle> getCandles() {
		return candles;
	}

	public void setCandles(List<Candle> candles) {
		this.candles = candles;
	}
	
	

}
