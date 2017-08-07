package com.kite.objects;

public class ValueTime {
	
	private String time;
	private double value;
	
	/**
	 * Percentage gain/loss since last value.
	 */
	private double perComparedToPreviousValue;

	/**
	 * value gain/loss since last value.
	 */
	private double valueComparedToPreviousValue;
	
	/**
	 * count of number of times the price hit near high/low. This can tell me 
	 * if there is a resistance/support based on the number of times it has tried to breach the value.
	 */
	private int numberOfNearVisits=0;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getTime()+"--"+getValue()+"--"+getPerComparedToPreviousValue()+"--"+getValueComparedToPreviousValue()+"--"+numberOfNearVisits;
	}
	
	public ValueTime() {
		// TODO Auto-generated constructor stub
	}
	
	public ValueTime(String time,double value) {
		// TODO Auto-generated constructor stub
		setTime(time);
		setValue(value);
	}
	
	public String howLongSinceThePrice(){
		/**
		 *  price holding time: how long the current value holds. 
		 *  indicating support or resistance.
		 */
		return null;
	}
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}

	public double getPerComparedToPreviousValue() {
		return perComparedToPreviousValue;
	}

	public void setPerComparedToPreviousValue(double perComparedToPreviousValue) {
		this.perComparedToPreviousValue = perComparedToPreviousValue;
	}

	public double getValueComparedToPreviousValue() {
		return valueComparedToPreviousValue;
	}

	public void setValueComparedToPreviousValue(double valueComparedToPreviousValue) {
		this.valueComparedToPreviousValue = valueComparedToPreviousValue;
	}

	public int getNumberOfNearVisits() {
		return numberOfNearVisits;
	}

	public void setNumberOfNearVisits(int numberOfNearVisits) {
		this.numberOfNearVisits = numberOfNearVisits;
	}
	
	
	

}
