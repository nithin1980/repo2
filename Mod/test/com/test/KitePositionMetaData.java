package com.test;

import java.util.HashMap;
import java.util.Map;

public class KitePositionMetaData {

	
	public static final String prevClose = "prevClose";
	public static final String todayOpen = "prevClose";
	public static final String todayFirstClose = "prevClose";
	
	
	private String type;
	
	
	private Map<String, Boolean> positionTakenCheck = new HashMap<String, Boolean>();
	private Map<String, Boolean> ceSLHit = new HashMap<String, Boolean>();
	private Map<String, Boolean> peSLHit = new HashMap<String, Boolean>();
	private Map<String, Double> positionTakenValue = new HashMap<String, Double>();
	
	private KitePositionMetaDataCounter pricePtcounter1 = new KitePositionMetaDataCounter();
	private KitePositionMetaDataCounter pricePtcounter2 = new KitePositionMetaDataCounter();
	private KitePositionMetaDataCounter pricePtcounter3 = new KitePositionMetaDataCounter();

	
	public KitePositionMetaData(String type) {
		// TODO Auto-generated constructor stub
		setType(type);
		
	}
	
	public void clear() {
		positionTakenCheck.clear();
		ceSLHit.clear();
		peSLHit.clear();
		positionTakenCheck.clear();
	}
	
	public void calculatePointsGain(double currentPrice, String pricePointName, double pricePoint ) {
		//double 
	}
	
	public void addPositionValue(String pricePointName, double pricePoint, double value) {
		positionTakenValue.put(pricePointName+"_"+String.valueOf(pricePoint), value);
	}
	
	public double getPositionValue(String pricePointName, double pricePoint) {
		return positionTakenValue.get(pricePointName+"_"+String.valueOf(pricePoint));
	}
	
	public void addPositionTaken(String pricePointName, double pricePoint) {
		positionTakenCheck.put(pricePointName+"_"+String.valueOf(pricePoint), Boolean.TRUE);
	}
	
	public boolean checkPoistionTaken(String pricePointName, double pricePoint) {
		if(positionTakenCheck.containsKey(pricePointName+"_"+String.valueOf(pricePoint))) {
			return true;
		}
		
		return false;
	}
	
	public void addCESLHit(String pricePointName, double pricePoint) {
		ceSLHit.put(pricePointName+"_"+String.valueOf(pricePoint), Boolean.TRUE);
	}
	
	public boolean checkCESLHit(String pricePointName, double pricePoint) {
		if(ceSLHit.containsKey(pricePointName+"_"+String.valueOf(pricePoint))) {
			return true;
		}
		
		return false;
	}
	public void addPESLHit(String pricePointName, double pricePoint) {
		peSLHit.put(pricePointName+"_"+String.valueOf(pricePoint), Boolean.TRUE);
	}
	
	public boolean checkPESLHit(String pricePointName, double pricePoint) {
		if(peSLHit.containsKey(pricePointName+"_"+String.valueOf(pricePoint))) {
			return true;
		}
		
		return false;
	}


	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}



	public KitePositionMetaDataCounter getPricePtcounter1() {
		return pricePtcounter1;
	}


	public void setPricePtcounter1(KitePositionMetaDataCounter pricePtcounter1) {
		this.pricePtcounter1 = pricePtcounter1;
	}


	public KitePositionMetaDataCounter getPricePtcounter2() {
		return pricePtcounter2;
	}


	public void setPricePtcounter2(KitePositionMetaDataCounter pricePtcounter2) {
		this.pricePtcounter2 = pricePtcounter2;
	}


	public KitePositionMetaDataCounter getPricePtcounter3() {
		return pricePtcounter3;
	}


	public void setPricePtcounter3(KitePositionMetaDataCounter pricePtcounter3) {
		this.pricePtcounter3 = pricePtcounter3;
	}
	
	
	
	
	
	
	
}
