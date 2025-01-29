package com.mod.support;

import org.apache.commons.csv.CSVRecord;

public class StockMetadataSupport {
	
	private String name;
	private String lotSize;
	private String futureName;
	
	
	public StockMetadataSupport(CSVRecord record) {
		// TODO Auto-generated constructor stub
		
		setName(record.get(1));
		setLotSize(record.get(2));
		setFutureName(record.get(3));
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLotSize() {
		return lotSize;
	}
	public void setLotSize(String lotSize) {
		this.lotSize = lotSize;
	}
	public String getFutureName() {
		return futureName;
	}
	public void setFutureName(String futureName) {
		this.futureName = futureName;
	}
	
	

}
