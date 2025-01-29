package com.mod.support;

public class PositionConfigData {
	
	private boolean populated;
	private double slPercen;
	private double slAbsolute;
	private double profitPercen;
	private double systemClosePoint;
	private String name;
	private double systemClosingPoint;
	private double hardslClosing;
	private double previousDaySL;
	
	
	public void clear() {
		setPopulated(false);
		setSlPercen(0.00);
		setSlAbsolute(0.00);
		setProfitPercen(0.00);
		setSystemClosePoint(0.00);
		setName(null);
		setHardslClosing(0.00);
		setSystemClosingPoint(0.00);
		setPreviousDaySL(0.00);
	}
	
	
	public double getSlPercen() {
		return slPercen;
	}
	public void setSlPercen(double slPercen) {
		this.slPercen = slPercen;
	}
	public double getSlAbsolute() {
		return slAbsolute;
	}
	public void setSlAbsolute(double slAbsolute) {
		this.slAbsolute = slAbsolute;
	}
	public double getProfitPercen() {
		return profitPercen;
	}
	public void setProfitPercen(double profitPercen) {
		this.profitPercen = profitPercen;
	}
	public boolean isPopulated() {
		return populated;
	}
	public void setPopulated(boolean populated) {
		this.populated = populated;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getSystemClosingPoint() {
		return systemClosingPoint;
	}
	public void setSystemClosingPoint(double systemClosingPoint) {
		this.systemClosingPoint = systemClosingPoint;
	}
	public double getSystemClosePoint() {
		return systemClosePoint;
	}
	public void setSystemClosePoint(double systemClosePoint) {
		this.systemClosePoint = systemClosePoint;
	}
	public double getHardslClosing() {
		return hardslClosing;
	}
	public void setHardslClosing(double hardslClosing) {
		this.hardslClosing = hardslClosing;
	}


	public double getPreviousDaySL() {
		return previousDaySL;
	}


	public void setPreviousDaySL(double previousDaySL) {
		this.previousDaySL = previousDaySL;
	}
	
	

}
