package com.mod.support;

public class GeneralObject {

	private String time;
	private String open;
	private String high;
	private String low;
	private String close;
	private String vol;
	
	public GeneralObject() {
		// TODO Auto-generated constructor stub
	}
	public GeneralObject(String time,String open,String high,String low,String close,String vol) {
		// TODO Auto-generated constructor stub
		setClose(close);
		setHigh(high);
		setLow(low);
		setOpen(open);
		setTime(time);
		setVol(vol);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getTime()+"-"+getOpen()+"-"+getHigh()+"-"+getLow()+"-"+getClose()+"-"+getVol();
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
	public String getOpen() {
		return open;
	}
	/**
	 * @param open the open to set
	 */
	public void setOpen(String open) {
		this.open = open;
	}
	/**
	 * @return the high
	 */
	public String getHigh() {
		return high;
	}
	/**
	 * @param high the high to set
	 */
	public void setHigh(String high) {
		this.high = high;
	}
	/**
	 * @return the low
	 */
	public String getLow() {
		return low;
	}
	/**
	 * @param low the low to set
	 */
	public void setLow(String low) {
		this.low = low;
	}
	/**
	 * @return the close
	 */
	public String getClose() {
		return close;
	}
	/**
	 * @param close the close to set
	 */
	public void setClose(String close) {
		this.close = close;
	}
	/**
	 * @return the vol
	 */
	public String getVol() {
		return vol;
	}
	/**
	 * @param vol the vol to set
	 */
	public void setVol(String vol) {
		this.vol = vol;
	}
	
	
}
