package com.mod.support;

public class CandleWrapper {
	
	public CandleWrapper() {
		// TODO Auto-generated constructor stub
	}
	
	public CandleWrapper(long key, Candle candle) {
		// TODO Auto-generated constructor stub
		setKey(key);
		setCandle(candle);
	}
	
	private Candle candle;
	
	private String sector;
	
	private long key;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return key+":"+candle.toString();
	}

	public Candle getCandle() {
		return candle;
	}

	public void setCandle(Candle candle) {
		this.candle = candle;
	}

	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}
	
	

}
