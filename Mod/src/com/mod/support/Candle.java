package com.mod.support;

public class Candle{

	
	private String time;
	private double open;
	private double high;
	private double low;
	private double close;
	private double vol;
	private double change;
	
	private String state;
	
	public Candle() {
		// TODO Auto-generated constructor stub
	}
	

	public Candle(String time,String open, String high, String low, String close) {
		// TODO Auto-generated constructor stub
		this.setClose(Double.valueOf(close));
		this.setHigh(Double.valueOf(high));
		this.setLow(Double.valueOf(low));
		this.setOpen(Double.valueOf(open));
		this.setTime(time);
	}

	public Candle(double change) {
		setChange(change);
		
	}
	public Candle(Candle candle) {
		// TODO Auto-generated constructor stub
		this.setClose(candle.getClose());
		this.setHigh(candle.getHigh());
		this.setLow(candle.getLow());
		this.setOpen(candle.getOpen());
		this.setState(candle.getState());
		if(candle.getTime()!=null) {
			this.setTime(candle.getTime().split("\\+")[0]);
		}
		
		this.setVol(candle.getVol());
	}
	
	public void populateWithAnotherCandle(Candle candle) {
		// TODO Auto-generated constructor stub
		this.setClose(candle.getClose());
		this.setHigh(candle.getHigh());
		this.setLow(candle.getLow());
		this.setOpen(candle.getOpen());
		this.setState(candle.getState());
		this.setTime(candle.getTime());
		this.setVol(candle.getVol());
	}
	
	public void setAllWithSinglePrice(double price) {
		setOpen(price);
		setHigh(price);
		setLow(price);
		setClose(price);
		
	}
	
	public void reset(){
		setClose(0);
		setHigh(0);
		setLow(0);
		setOpen(0);
		setTime("0");
		setVol(0);
		setChange(0);
		setState(null);
	}
	

	/**
	 * Need to cater to the fact that open, low & high can all be the same. Indicates no price movement.
	 * @return
	 */
	public boolean isOpenEqualToLow() {
		
		if(getLow()!=0 && getHigh()!=0 
				&& getLow()==getHigh()) {
			return false;
		}
		
		if(getOpen()!=0 && getLow()!=0 
				&& getOpen()==getLow()) {
			return true;
		}
		
		return false;
	}

	/**
	 * Need to cater to the fact that open, low & high can all be the same. Indicates no price movement.
	 * @return
	 */
	
	public boolean isOpenEqualToHigh() {
		if(getLow()!=0 && getHigh()!=0 
				&& getLow()==getHigh()) {
			return false;
		}
		
		if(getOpen()!=0 && getHigh()!=0 
				&& getOpen()==getHigh()) {
			return true;
		}
		
		return false;
		
	}
	
	public double bwHighLow() {
		return (getHigh()+getLow())/2;
	}
	public boolean isBetweenHighLow(double expectedValue) {
		
		if(expectedValue<=getHigh() && expectedValue>=getLow()) {
			return true;
		}
		
		return false;
		
	}
	
	public void populate(double value){
		if(value>getHigh()){
			setHigh(value);
		}
		
		if(getLow()==0 || value<getLow()){
			setLow(value);
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Time:"+time+ " High:"+getHigh()+" Low:"+getLow()+" Close:"+getClose()+" Change:"+getChange();
	}
	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * @return the open
	 */
	public double getOpen() {
		return open;
	}

	/**
	 * @param open the open to set
	 */
	public void setOpen(double open) {
		this.open = open;
	}

	/**
	 * @return the high
	 */
	public double getHigh() {
		return high;
	}

	/**
	 * @param high the high to set
	 */
	public void setHigh(double high) {
		this.high = high;
	}

	/**
	 * @return the low
	 */
	public double getLow() {
		return low;
	}

	/**
	 * @param low the low to set
	 */
	public void setLow(double low) {
		this.low = low;
	}

	/**
	 * @return the close
	 */
	public double getClose() {
		return close;
	}

	/**
	 * @param close the close to set
	 */
	public void setClose(double close) {
		this.close = close;
	}

	/**
	 * @return the vol
	 */
	public double getVol() {
		return vol;
	}

	/**
	 * @param vol the vol to set
	 */
	public void setVol(double vol) {
		this.vol = vol;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}


	public double getChange() {
		return change;
	}


	public void setChange(double change) {
		this.change = change;
	}
	
	
}
