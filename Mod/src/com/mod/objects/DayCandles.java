package com.mod.objects;

import java.util.LinkedList;

import com.mod.support.Candle;

public class DayCandles {
	
	private double key;
	private Candle currentCandle;
	private Candle previousCandle;
	
	private boolean alertCandleAvailable;
	private String alertType;
	
	private double alertHigh;
	private double alertLow;
	
	private double upperBollingerBand;
	private double lowerBollingerBand;
	
	private LinkedList<Double> BB_Close_Records = new LinkedList<Double>();
	
	private boolean inPosition;
	
	private int candlesAfterAlert=0;
	
	private long candleStartTime;
	
	public DayCandles() {
		// TODO Auto-generated constructor stub
	}
	
	public DayCandles(Candle candle,String candleType) {
		// TODO Auto-generated constructor stub
		if("current".equalsIgnoreCase(candleType)) {
			setCurrentCandle(candle);
		}else {
			setPreviousCandle(candle);
		}
		
	}
	public DayCandles(Candle currentCandle,Candle previCandle ) {
		// TODO Auto-generated constructor stub
		setCurrentCandle(currentCandle);
		setPreviousCandle(previCandle);
	}
	public void setAlertToHigh() {
		this.alertType="High";
	}
	public void setAlertToLow() {
		this.alertType="Low";
	}
	
	
	
	

	public LinkedList<Double> getBB_Close_Records() {
		return BB_Close_Records;
	}

	public void setBB_Close_Records(LinkedList<Double> bB_Close_Records) {
		BB_Close_Records = bB_Close_Records;
	}

	public double getUpperBollingerBand() {
		return upperBollingerBand;
	}

	public void setUpperBollingerBand(double upperBollingerBand) {
		this.upperBollingerBand = upperBollingerBand;
	}

	public double getLowerBollingerBand() {
		return lowerBollingerBand;
	}

	public void setLowerBollingerBand(double lowerBollingerBand) {
		this.lowerBollingerBand = lowerBollingerBand;
	}

	public double getAlertHigh() {
		return alertHigh;
	}

	public void setAlertHigh(double alertHigh) {
		this.alertHigh = alertHigh;
	}

	public double getAlertLow() {
		return alertLow;
	}

	public void setAlertLow(double alertLow) {
		this.alertLow = alertLow;
	}

	public String alertCandleType() {
		return alertType;
	}
	public boolean isCurrentCandleEmpty() {
		if(currentCandle.getOpen()==0) {
			return true;
		}
		return false;
	}
	public boolean isPreviousCandleEmpty() {
		if(previousCandle.getOpen()==0) {
			return true;
		}
		return false;
	}

	public Candle getCurrentCandle() {
		return currentCandle;
	}
	public void setCurrentCandle(Candle currentCandle) {
		this.currentCandle = currentCandle;
	}
	public Candle getPreviousCandle() {
		return previousCandle;
	}
	public void setPreviousCandle(Candle previousCandle) {
		this.previousCandle = previousCandle;
	}

	public boolean isAlertCandleAvailable() {
		return alertCandleAvailable;
	}

	public void setAlertCandleAvailable(boolean alertCandleAvailable) {
		this.alertCandleAvailable = alertCandleAvailable;
	}

	public double getKey() {
		return key;
	}

	public void setKey(double key) {
		this.key = key;
	}

	public boolean isInPosition() {
		return inPosition;
	}

	public void setInPosition(boolean inPosition) {
		this.inPosition = inPosition;
	}

	public int getCandlesAfterAlert() {
		return candlesAfterAlert;
	}

	public void setCandlesAfterAlert(int candlesAfterAlert) {
		this.candlesAfterAlert = candlesAfterAlert;
	}

	public long getCandleStartTime() {
		return candleStartTime;
	}

	public void setCandleStartTime(long candleStartTime) {
		this.candleStartTime = candleStartTime;
	}
	
	
}
