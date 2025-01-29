package com.mod.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KiteCandles {
	
	@JsonProperty("candles")
	private List<String[]> candles;
	
	private List<Candle> processedCandles;

	public List<String[]> getCandles() {
		return candles;
	}

	public void setCandles(List<String[]> candles) {
		this.candles = candles;
	}
	
	public List<Candle> candleInformation(){
	
	String[] arrays = null;
	
			
		if(candles!=null && candles.size()>0 && processedCandles==null){
			processedCandles = new ArrayList<Candle>();
			Iterator<String[]> itr = candles.iterator();
			while(itr.hasNext()) {
				arrays = itr.next();
				processedCandles.add(new Candle(arrays[0], arrays[1], arrays[2], arrays[3], arrays[4]));
			}
			
			candles.clear();
			
		}
		
		return processedCandles;
		
	}
	

}
