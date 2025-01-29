package com.mod.objects;

public class PositionMetaData {

	private double slippage;
	
	private double highestProfit;
	
	private double highestLoss;
	
	private long futId;
	private long futLotSize;
	
	private String sector;
	
	private long orderTime;
	
	private boolean inPositionNow;
	
	private boolean updatePostOrder;
	
	
	
	//************************************
	/**
	 * once it reaches the breakeven point
	 */
	private double reachedBrkEvenSL;
	/**
	 *  SL if profit doesn't come in 60 second. till then buy price
	 */
	private double post60secSL;
	
	
	private double openPriceSL;
	
	private double firstSL;
	private double secondSL;
	private double thirdSL;
	private double frthSL;
	
	
	
	//*************************************
	
	private double profit;
	
	
	private boolean takePosition;
	private boolean orderPlaced;
	private boolean orderSucess;
	
	private long orderId;
	

	public long getFutId() {
		return futId;
	}

	public double getFirstSL() {
		return firstSL;
	}

	public void setFirstSL(double firstSL) {
		this.firstSL = firstSL;
	}

	public double getSecondSL() {
		return secondSL;
	}

	public void setSecondSL(double secondSL) {
		this.secondSL = secondSL;
	}

	public double getThirdSL() {
		return thirdSL;
	}

	public void setThirdSL(double thirdSL) {
		this.thirdSL = thirdSL;
	}

	public double getFrthSL() {
		return frthSL;
	}

	public void setFrthSL(double frthSL) {
		this.frthSL = frthSL;
	}

	public double getReachedBrkEvenSL() {
		return reachedBrkEvenSL;
	}

	public void setReachedBrkEvenSL(double reachedBrkEvenSL) {
		this.reachedBrkEvenSL = reachedBrkEvenSL;
	}

	public double getPost60secSL() {
		return post60secSL;
	}

	public void setPost60secSL(double post60secSL) {
		this.post60secSL = post60secSL;
	}

	public double getOpenPriceSL() {
		return openPriceSL;
	}

	public void setOpenPriceSL(double openPriceSL) {
		this.openPriceSL = openPriceSL;
	}

	public void setFutId(long futId) {
		this.futId = futId;
	}

	public long getFutLotSize() {
		return futLotSize;
	}

	public void setFutLotSize(long futLotSize) {
		this.futLotSize = futLotSize;
	}

	public double getSlippage() {
		return slippage;
	}

	public void setSlippage(double slippage) {
		this.slippage = slippage;
	}

	public double getHighestProfit() {
		return highestProfit;
	}

	public void setHighestProfit(double highestProfit) {
		this.highestProfit = highestProfit;
	}

	public double getHighestLoss() {
		return highestLoss;
	}

	public void setHighestLoss(double highestLoss) {
		this.highestLoss = highestLoss;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public boolean isTakePosition() {
		return takePosition;
	}

	public void setTakePosition(boolean takePosition) {
		this.takePosition = takePosition;
	}

	public boolean isOrderSucess() {
		return orderSucess;
	}

	public void setOrderSucess(boolean orderSucess) {
		this.orderSucess = orderSucess;
	}

	public boolean isOrderPlaced() {
		return orderPlaced;
	}

	public void setOrderPlaced(boolean orderPlaced) {
		this.orderPlaced = orderPlaced;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public long getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(long orderTime) {
		this.orderTime = orderTime;
	}

	public boolean isInPositionNow() {
		return inPositionNow;
	}

	public void setInPositionNow(boolean inPositionNow) {
		this.inPositionNow = inPositionNow;
	}

	public boolean isUpdatePostOrder() {
		return updatePostOrder;
	}

	public void setUpdatePostOrder(boolean updatePostOrder) {
		this.updatePostOrder = updatePostOrder;
	}
	
	
}
