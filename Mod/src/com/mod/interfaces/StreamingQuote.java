package com.mod.interfaces;

import java.math.BigDecimal;

public abstract class StreamingQuote {
	public String time;
	public String instrumentToken;
	
	public StreamingQuote() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor
	 * @param time
	 * @param instrumentToken
	 */
	public StreamingQuote(String time, String instrumentToken) {
		super();
		this.time = time;
		this.instrumentToken = instrumentToken;
	}
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getInstrumentToken() {
		return instrumentToken;
	}
	public void setInstrumentToken(String instrumentToken) {
		this.instrumentToken = instrumentToken;
	}
}
