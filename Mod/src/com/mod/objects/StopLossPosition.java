package com.mod.objects;

import com.mod.enums.EnumOrderStatus;

public class StopLossPosition {
	
	private long orderId;
	private int buyquantity;
	private int sellquantity;
	
	private double price;
	
	private String positionType;
	
	private StopLossType slreason;
	
	private EnumOrderStatus orderStatus;
	
	private PositionMetaData metadata;

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public int getBuyquantity() {
		return buyquantity;
	}

	public void setBuyquantity(int buyquantity) {
		this.buyquantity = buyquantity;
	}

	public int getSellquantity() {
		return sellquantity;
	}

	public void setSellquantity(int sellquantity) {
		this.sellquantity = sellquantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getPositionType() {
		return positionType;
	}

	public void setPositionType(String positionType) {
		this.positionType = positionType;
	}

	public StopLossType getSlreason() {
		return slreason;
	}

	public void setSlreason(StopLossType slreason) {
		this.slreason = slreason;
	}

	public EnumOrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(EnumOrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public PositionMetaData getMetadata() {
		return metadata;
	}

	public void setMetadata(PositionMetaData metadata) {
		this.metadata = metadata;
	}
	
	

}
