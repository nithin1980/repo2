package com.kite.objects;

import java.util.List;

public class ScriptData {
	
	private double open;
	private double dayHigh;
	private double dayLow;
	private double dayPercentageIncr;
	
	private ValueTime dayHighestWithTime;
	private ValueTime dayLowestWithTime;
	
	private List<ValueTime> nearestHighs;
	
	private List<ValueTime> nearestLows;
	
	private ValueTime currentPrice;
	private ValueTime newPrice;
	
	private int reversePositionCount;
	
	private double reversePositionValue;
	
	/**
	 * This is keeping a count since the last reverse point.
	 * If there is a pullback AFTER A WHILE after the reverse value
	 * IT MEANS the current position is wrong. 
	 */
	private int countSinceLastReverse;
	
//	/**
//	 * This may be required...
//	 */
//	private String currentPriceTime;
	
	/**
	 * This is to store if one after the next price goes in one specific direction.
	 */
	private List<ValueTime> continousOneWay;
	
	private int uptrendCount;
	private int downTrendCount;
	
	/**
	 * Reference High & Reference low: this will be used to compare the current price to see 
	 * where it is going. It could day's high/low, list nearest high/low etc... 
	 * The current price action will MOVE within this high/low
	 */
	private ValueTime bracketHigh;
	private ValueTime bracketLow;
	
	/**
	 * Value where it is assumed that it exists high trend, if falls below this value
	 * value where it is assume that it has entered high , if it gains move this value.
	 */
	private ValueTime bracketHighLowerValue;
	private ValueTime bracketLowHigherValue;
	
	public ScriptData() {
		// TODO Auto-generated constructor stub
	}
	public ScriptData(String currentPriceTime,double newPrice) {
		// TODO Auto-generated constructor stub
		setCurrentPrice(new ValueTime(currentPriceTime, newPrice));
		setNewPrice(new ValueTime(currentPriceTime, newPrice));
		setOpen(newPrice);
	}
	
	public void increaseUptrend(){
		uptrendCount = uptrendCount++;
	}
	
	public void increaseDowntrend(){
		downTrendCount =downTrendCount++;
	}
	public void clearUptrendCount(){
		uptrendCount = 0;
	}
	public void clearDownTrendCount(){
		downTrendCount = 0;
	}
	public void calculateMargins(){
		double spread = getBracketHigh().getValue()-getBracketLow().getValue();
		
		spread = spread/4;
		
		setBracketHighLowerValue(new ValueTime(getBracketHigh().getTime(), getBracketHigh().getValue()-spread));
		setBracketLowHigherValue(new ValueTime(getBracketLow().getTime(), getBracketLow().getValue()+spread));
	}
	public ValueTime getBracketHighLowerValue() {
		return bracketHighLowerValue;
	}
	public void setBracketHighLowerValue(ValueTime bracketHighLowerValue) {
		this.bracketHighLowerValue = bracketHighLowerValue;
	}
	public ValueTime getBracketLowHigherValue() {
		return bracketLowHigherValue;
	}
	public void setBracketLowHigherValue(ValueTime bracketLowHigherValue) {
		this.bracketLowHigherValue = bracketLowHigherValue;
	}
	public int getUptrendCount() {
		return uptrendCount;
	}
	public void setUptrendCount(int uptrendCount) {
		this.uptrendCount = uptrendCount;
	}
	public int getDownTrendCount() {
		return downTrendCount;
	}
	public void setDownTrendCount(int downTrendCount) {
		this.downTrendCount = downTrendCount;
	}
	public boolean isNewPriceIncreased(){
		return getNewPrice().getValue()>getCurrentPrice().getValue();
	}
	
	public boolean isNewPriceSameAsCurrent(){
		return getNewPrice().getValue()==getCurrentPrice().getValue();
	}
	
	public double newPriceCurrentDifference(){
		return getNewPrice().getValue()-getCurrentPrice().getValue();
	}
	public double newPriceCurrentDifferencePercentage(){
		return ((getNewPrice().getValue()-getCurrentPrice().getValue())/getCurrentPrice().getValue())*100;
	}
	
	public double getOpen() {
		return open;
	}
	public void setOpen(double open) {
		this.open = open;
	}
	public double getDayHigh() {
		return dayHigh;
	}
	public void setDayHigh(double dayHigh) {
		this.dayHigh = dayHigh;
	}
	public double getDayLow() {
		return dayLow;
	}
	public void setDayLow(double dayLow) {
		this.dayLow = dayLow;
	}
	public double getDayPercentageIncr() {
		return dayPercentageIncr;
	}
	public void setDayPercentageIncr(double dayPercentageIncr) {
		this.dayPercentageIncr = dayPercentageIncr;
	}
	public ValueTime getDayHighestWithTime() {
		return dayHighestWithTime;
	}
	public void setDayHighestWithTime(ValueTime dayHighestWithTime) {
		this.dayHighestWithTime = dayHighestWithTime;
	}
	public ValueTime getDayLowestWithTime() {
		return dayLowestWithTime;
	}
	public void setDayLowestWithTime(ValueTime dayLowestWithTime) {
		this.dayLowestWithTime = dayLowestWithTime;
	}
	public List<ValueTime> getNearestHighs() {
		return nearestHighs;
	}
	public void setNearestHighs(List<ValueTime> nearestHighs) {
		this.nearestHighs = nearestHighs;
	}
	public List<ValueTime> getNearestLows() {
		return nearestLows;
	}
	public void setNearestLows(List<ValueTime> nearestLows) {
		this.nearestLows = nearestLows;
	}
	
	public ValueTime getCurrentPrice() {
		return currentPrice;
	}
	public void setCurrentPrice(ValueTime currentPrice) {
		this.currentPrice = currentPrice;
	}
	public ValueTime getNewPrice() {
		return newPrice;
	}
	public void setNewPrice(ValueTime newPrice) {
		this.newPrice = newPrice;
	}
	public List<ValueTime> getContinousOneWay() {
		return continousOneWay;
	}
	public void setContinousOneWay(List<ValueTime> continousOneWay) {
		this.continousOneWay = continousOneWay;
	}
	public ValueTime getBracketHigh() {
		return bracketHigh;
	}
	public void setBracketHigh(ValueTime bracketHigh) {
		if(getBracketLow()==null){
			this.bracketHigh = bracketHigh;
			if(getBracketLow()!=null){
				calculateMargins();
			}
		}
		
		if(getBracketHigh()!=null){
			double diff = bracketHigh.getValue()-getBracketHigh().getValue();
			this.bracketHigh = bracketHigh;
			if(getBracketLow()!=null){
				getBracketLow().setValue(getBracketLow().getValue()+diff);
				calculateMargins();
			}
		}
	}
	public ValueTime getBracketLow() {
		return bracketLow;
	}
	public void setBracketLow(ValueTime bracketLow) {
		if(getBracketLow()==null){
			this.bracketLow = bracketLow;
			if(getBracketHigh()!=null){
				calculateMargins();
			}
		}
		if(getBracketLow()!=null){
			double diff = getBracketLow().getValue()-bracketLow.getValue();
			this.bracketLow = bracketLow;
			if(getBracketHigh()!=null){
				getBracketHigh().setValue(getBracketHigh().getValue()-diff);
				calculateMargins();
			}
		}
	}
	public int getReversePositionCount() {
		return reversePositionCount;
	}
	public void setReversePositionCount(int reversePositionCount) {
		this.reversePositionCount = reversePositionCount;
	}
	public double getReversePositionValue() {
		return reversePositionValue;
	}
	public void setReversePositionValue(double reversePositionValue) {
		this.reversePositionValue = reversePositionValue;
	}
	public int getCountSinceLastReverse() {
		return countSinceLastReverse;
	}
	public void setCountSinceLastReverse(int countSinceLastReverse) {
		this.countSinceLastReverse = countSinceLastReverse;
	}
	

	
}
