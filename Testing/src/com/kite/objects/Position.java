package com.kite.objects;

/**
 * 
 * @author nkumar
 * Holding information on the position
 * Like profit/loss for PE/CE trade.
 * This can be used to influence trade if the position is at loss.
 * 
 *
 */
public class Position {
	
	private double buy=0.0;
	private double sell=0.0;
	//private double profit=0.0;
	
	private double expense;
	
	private String name;
	
	private int size=150;
	
	private int buyRecord;
	private int sellRecord;
	
	private double reversePositionProfit;
	
	private double reversePositionHighProfit;
	
	
	
	
	private double highValue;
	private double lowValue;
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name+","+buy+","+sell+","+getProfit()+","+buyRecord+","+sellRecord+","+getHighValue()+","+getLowValue()+","+highProfit()+","+lowProfit()+"##";
		//return "{name:"+name+",buy:"+buy+",sell:"+sell+",profit:"+profit+",buyRecord:"+buyRecord+",sellRecord:"+sellRecord+"}";
	}
	
	public Position(double expense) {
		// TODO Auto-generated constructor stub
		setExpense(expense);
	}
	public Position(String name,double expense,double buy) {
		// TODO Auto-generated constructor stub
		setExpense(expense);
		setBuy(buy);
		setName(name);
	}
	public Position(String name,double expense,double buy,int size) {
		// TODO Auto-generated constructor stub
		setExpense(expense);
		setBuy(buy);
		setSize(size);
		setName(name);
	}
	
	public Position(Position localPosition) {
		// TODO Auto-generated constructor stub
		setName(localPosition.getName());
		setExpense(localPosition.getExpense());
		setBuy(localPosition.getBuy());
		setSize(localPosition.getSize());
		
	}
	
	public double getProfit() {
		double profit =0.0;
		
		if(buy>0.0 && sell>0.0){
			profit = sell-buy;
			profit = (profit*size)-expense;
		}
		return profit;
	}
	
	public boolean isProfitDecreasedEnough(double currentProfit){
		
		if(getReversePositionHighProfit()==0){
			return false;
		}
		
		if(getReversePositionHighProfit()<-150 && currentProfit<-150){
			return true;
		}
		
		double diff = ((getReversePositionHighProfit()-currentProfit)/getReversePositionHighProfit())*100;
		
		System.out.println("percentage decrease:"+diff+"---"+getReversePositionHighProfit()+"--"+currentProfit);
		if(diff>10){
			return true;
		}
		
		return false;
	}
	

	public double highProfit(){
		double profit =0.0;
		
		profit = getHighValue()-buy;
		profit = (profit*size)-expense;
		return profit;
		
	}
	public double lowProfit(){
		double profit =0.0;
		
		profit = getLowValue()-buy;
		profit = (profit*size)-expense;
		return profit;
		
	}
	public double getHighValue() {
		return highValue;
	}

	public void setHighValue(double highValue) {
		if(this.highValue==0){
			this.highValue = highValue;
		}
		if(highValue>this.highValue){
			this.highValue = highValue;
		}
	}

	public double getLowValue() {
		return lowValue;
	}

	public void setLowValue(double lowValue) {
		if(this.lowValue==0){
			this.lowValue=lowValue;
		}
		if(lowValue<this.lowValue){
			this.lowValue = lowValue;
		}
	}

	public double getReversePositionHighProfit() {
		return reversePositionHighProfit;
	}
	
	public void clearReversePositionHighProfit(){
		this.reversePositionHighProfit=0;
	}

	public void setReversePositionHighProfit(double reversePositionHighProfit) {
		if(this.reversePositionHighProfit==0){
			this.reversePositionHighProfit = reversePositionHighProfit;
		}
		if(reversePositionHighProfit>this.reversePositionHighProfit){
			this.reversePositionHighProfit = reversePositionHighProfit;
		}
	}

	public double getReversePositionProfit() {
		return reversePositionProfit;
	}

	public void clearReversePositionProfit() {
		this.reversePositionProfit=0;
	}
	public void setReversePositionProfit(double reversePositionProfit) {
		setReversePositionHighProfit(reversePositionProfit);
		this.reversePositionProfit = reversePositionProfit;
	}

	public int getBuyRecord() {
		return buyRecord;
	}

	public void setBuyRecord(int buyRecord) {
		this.buyRecord = buyRecord;
	}

	public int getSellRecord() {
		return sellRecord;
	}

	public void setSellRecord(int sellRecord) {
		this.sellRecord = sellRecord;
	}

	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
//	public void setProfit(double profit) {
//		this.profit = profit;
//	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getBuy() {
		return buy;
	}
	public void setBuy(double buy) {
		this.buy = buy;
	}
	public double getSell() {
		return sell;
	}
	public void setSell(double sell) {
		this.sell = sell;
	}
	public double getExpense() {
		return expense;
	}
	public void setExpense(double expense) {
		this.expense = expense;
	}
	
	

}
