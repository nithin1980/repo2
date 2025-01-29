package com.mod.support;

public class OpenHighLowSupport {
	
	private long future;
	private long stock;
	private int lotSize;
	
	public OpenHighLowSupport() {
		// TODO Auto-generated constructor stub
	}
	
	public OpenHighLowSupport(String future,String stock,String lotSize) {
		// TODO Auto-generated constructor stub
		
		setFuture(Long.valueOf(future));
		setStock(Long.valueOf(stock));
		setLotSize(Integer.valueOf(lotSize));
	}
	
	
	
	public long getFuture() {
		return future;
	}
	public void setFuture(long future) {
		this.future = future;
	}
	public long getStock() {
		return stock;
	}
	public void setStock(long stock) {
		this.stock = stock;
	}
	public int getLotSize() {
		return lotSize;
	}
	public void setLotSize(int lotSize) {
		this.lotSize = lotSize;
	}
	
	

}
