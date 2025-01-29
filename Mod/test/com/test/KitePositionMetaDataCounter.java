package com.test;

public class KitePositionMetaDataCounter {
	
	private double failure;
	private double overall;
	
	public double failurerate() {
		System.out.println("fail:"+failure+","+overall+","+(overall-(failure*2)));
		return (failure/overall)*100;
	}
	
	public void incrementFailure() {
		failure=failure+1;
	}
	
	public void incrementOverall() {
		overall=overall+1;
	}

	public double getFailure() {
		return failure;
	}

	public void setFailure(double failure) {
		this.failure = failure;
	}

	public double getOverall() {
		return overall;
	}

	public void setOverall(double overall) {
		this.overall = overall;
	}

	
	

}
