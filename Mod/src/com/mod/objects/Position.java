package com.mod.objects;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

import com.mod.process.models.Chart;
import com.mod.process.models.Constants;

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
	
	public static final String INITIAL_POSITION = "INITIAL_POSITION";
	public static final String ENTERED_POSITION = "ENTERED_POSITION";
//	public static final String POSITIVE = "ENTERED_POSITION";
//	public static final String ENTERED_POSITION = "ENTERED_POSITION";
	
	
	
	private double buy=0.0;
	private double sell=0.0;
	//private double profit=0.0;
	
	private double expense;
	
	private String name;
	
	private int size=0;
	
	private int buyRecord;
	private int sellRecord;
	
	private double reversePositionProfit;
	
	private double reversePositionHighProfit;
	
	
	private String status;
	
	
	private double highValue;
	private double lowValue;
	
	private int highValRecord;
	private int lowValRecord;
	
	private Position reversePosition;
	
	
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
	
	
	private int reversePositionOppurtunityCount;
	
	private TDoubleList cache  = new TDoubleArrayList();
	private int cachesize; 
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name+","+buy+","+sell+","+getProfit()+","+buyRecord+","+sellRecord+","+getHighValue()+","+getLowValue()+","+highProfit()+","+lowProfit()+","+cost()+","+profitPercentage()+"##";
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
	public void addToRecord(double value){
		cache.add(value);
		cachesize++;
	}
	public void clearRecords(){
		cache.clear();
		cachesize=0;
	}
	public TDoubleList getItemsFromRecords(int recordNeeded){
		TDoubleList items = new TDoubleArrayList();
		
		int listSize = cache.size();
		items.addAll(cache.subList(listSize-recordNeeded, listSize));;
		
		return items;
		
	}
	public boolean isProfitDecreasedEnough(int currentrecord, int recordnumber, double percentReduction, double currentProfit){
		double diff = percentageFromHighProfit(currentProfit);
		
		if(diff>percentReduction && (currentrecord-highValRecord)>recordnumber){
			return true;
		}
		
		return false;
	}
	public String trend(){
		if(cachesize<11){
			return "TREND_NOT_AVAILABLE";
		}
		TDoubleList list = getItemsFromRecords(10);
		String trend = Chart.calculateTrend(list);
		
		return trend;
	}
	public void calculateMargins(){
		double spread = getBracketHigh().getValue()-getBracketLow().getValue();
		
		spread = spread/Constants.MarginBreakup;
		
		setBracketHighLowerValue(new ValueTime(getBracketHigh().getTime(), getBracketHigh().getValue()-spread));
		setBracketLowHigherValue(new ValueTime(getBracketLow().getTime(), getBracketLow().getValue()+spread));
	}
	public int size(){
		if(size==0){
			return (int) (20000/buy);
		}
		
		return size;
	}
	public double cost(){
		return buy*size();
	}
	
	public double profitPercentage(){
		return (getProfit()/cost())*100;
	}
	
	public double getProfit() {
		double profit =0.0;
		
		if(buy>0.0 && sell>0.0){
			profit = sell-buy;
			profit = (profit*size())-expense;
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
		
//		System.out.println("percentage decrease:"+diff+"---"+getReversePositionHighProfit()+"--"+currentProfit);
		if(diff>10){
			return true;
		}
		
		return false;
	}
	public double percentgeProfit(double currentProfit){
		double diff = (currentProfit/cost())*100;
		
		return diff;

	}
	public double percentageFromHighProfit(double currentProfit){
		double diff = ((highProfit()-currentProfit)/highProfit())*100;
		
		return diff;
	}

	public double highProfit(){
		double profit =0.0;
		
		profit = getHighValue()-buy;
		profit = (profit*size())-expense;
		return profit;
		
	}
	public double lowProfit(){
		double profit =0.0;
		
		profit = getLowValue()-buy;
		profit = (profit*size())-expense;
		return profit;
		
	}
	public double getHighValue() {
		return highValue;
	}

	
	public ValueTime getBracketHigh() {
		return bracketHigh;
	}

	public void setBracketHigh(ValueTime bracketHigh) {
		if(getBracketHigh()==null){
			
			if(getBracketLow()!=null){
				if(getBracketLow().getValue()>bracketHigh.getValue()){
					throw new RuntimeException("Bracket high should be greater than bracket low");
				}
			}
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
			
			if(getBracketHigh()!=null){
				if(getBracketHigh().getValue()<bracketLow.getValue()){
					throw new RuntimeException("Bracket low should be lower than bracket low");
				}
			}
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

	public boolean setHighValue(double highValue) {
		if(this.highValue==0){
			this.highValue = highValue;
			return true;
		}
		if(highValue>this.highValue){
			this.highValue = highValue;
			return true;
		}
		
		return false;
	}

	public double getLowValue() {
		return lowValue;
	}

	public boolean setLowValue(double lowValue) {
		if(this.lowValue==0){
			this.lowValue=lowValue;
			return true;
		}
		if(lowValue<this.lowValue){
			this.lowValue = lowValue;
			return true;
		}
		return false;
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
		return size();
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

	public Position getReversePosition() {
		return reversePosition;
	}

	public void setReversePosition(Position reversePosition) {
		this.reversePosition = reversePosition;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getReversePositionOppurtunityCount() {
		return reversePositionOppurtunityCount;
	}

	public void setReversePositionOppurtunityCount(
			int reversePositionOppurtunityCount) {
		this.reversePositionOppurtunityCount = reversePositionOppurtunityCount;
	}

	public int getLowValRecord() {
		return lowValRecord;
	}

	public void setLowValRecord(int lowValRecord) {
		this.lowValRecord = lowValRecord;
	}

	public int getHighValRecord() {
		return highValRecord;
	}

	public void setHighValRecord(int highValRecord) {
		this.highValRecord = highValRecord;
	}
	
	

}
