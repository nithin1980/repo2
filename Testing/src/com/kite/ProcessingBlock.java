package com.kite;

import gnu.trove.list.TDoubleList;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

import com.kite.objects.Action;
import com.kite.objects.GroupPosition;
import com.kite.objects.Position;
import com.kite.objects.ScriptData;
import com.kite.objects.ValueTime;

public class ProcessingBlock {

	public static final String BUY_PE = "buy_pe";
	public static final String BUY_CE = "buy_ce";
	public static final String HOLD = "hold";
	public static final String REVERSE_TO_PE = "reverse_position_pe";
	public static final String REVERSE_TO_CE = "reverse_position_ce";
	public static final String HOLDING_PE = "hold_pe";
	public static final String HOLDING_CE = "hold_ce";
	public static final String HOLDING_NOTHING = "hold_nothing";
	
	public static final String HOLD_OPTION_DROP = "hold_option_drop";
	
	public static final String TOWARDS_HIGH = "towards_high";
	public static final String TOWARDS_LOW = "towards_low";
	public static final String TOWARDS_NEITHER = "neither";
	
	public static final int NEAR_MISS_COUNT = 3;
	
	
	private static final ScriptData PE = new ScriptData("12345",15.35);
	private static final ScriptData CE = new ScriptData("12345",100.45);
	
	
	private static boolean log = false;
	
	public static void main(String[] args) {
		System.out.println(isCloseTo(5.95,7.5,8.8));
		
	}
	
	
	
	@Test
	public void testPELogic(){
		
		
		List<List<String>> dataList = ApplicationHelper.getConfig();
		int size   = dataList.size();
		//Action action  = new Action(HOLDING_NOTHING);
		Action action  = new Action(HOLDING_PE);
		ScriptData script = new ScriptData();
		ScriptData peScript = new ScriptData();
		ScriptData ceScript = new ScriptData();
		List<String> values = null;
		
		CacheService.niftytDoubleList.clear();
		CacheService.dumpNifty();
		ApplicationHelper.threadService.shutdown();
		
		DashBoard.positionMap.put("1", new GroupPosition());
		
		GroupPosition pos = DashBoard.positionMap.get("1");
		Position newPosition = new Position("PE",70.0,77);
		newPosition.setBuyRecord(0);
		pos.getPePositions().add(newPosition);
		
		
		long t = System.currentTimeMillis();
		
		for(int i=0;i<size;i++){
			values = dataList.get(i);
			CacheService.addNifty(Double.valueOf(values.get(1)));
			if(i==6474){
				log=true;
			}
			if(i==8474){
				log=false;
			}
			
			peScript.setNewPrice(new ValueTime(values.get(0),Double.valueOf(values.get(2))));
			ceScript.setNewPrice(new ValueTime(values.get(0),Double.valueOf(values.get(3))));
			action = process(newPosition,script,values , action, peScript, ceScript);
			//assertEquals("Failed for record:"+i,values.get(4).trim(),action.getAction());
			values.add(action.getAction());
			
			if(BUY_PE.equals(action.getAction())){
				newPosition = new Position("PE",70.0,Double.valueOf(values.get(2)));
				newPosition.setBuyRecord(i);
				pos.getPePositions().add(newPosition);
				action.setAction(HOLDING_PE);
			}else if(BUY_CE.equals(action.getAction())){
				newPosition = new Position("CE",70.0,Double.valueOf(values.get(3)));
				newPosition.setBuyRecord(i);
				pos.getCePositions().add(newPosition);
				action.setAction(HOLDING_CE);
			}
			
			if(REVERSE_TO_PE.equals(action.getAction())){
				newPosition.setSell(Double.valueOf(values.get(3)));
				newPosition.setSellRecord(i);
			}else if(REVERSE_TO_CE.equals(action.getAction())){
				newPosition.setSell(Double.valueOf(values.get(2)));
				newPosition.setSellRecord(i);
			}
			
			if(log){
				System.out.println(values);
			}
			
		}
		
//		System.out.println(pos.total());
//		System.out.println(pos);
		
//		List<String> data = new ArrayList<String>();
//		data.add("123456");
//		data.add("20.5");
//		data.add("15.35");
//		data.add("2");
//		
//		
//		
//		process(script,data,action,null,null);
//		assertEquals(BUY_PE,action.getAction());
//		if(BUY_PE.equals(action.getAction())){
//			action.setAction(HOLDING_PE);
//		}else if(BUY_CE.equals(action.getAction())){
//			action.setAction(HOLDING_CE);
//		}
//		data.set(1, "20.6");
//		process(script,data,action,null,null);
//		assertEquals(HOLDING_PE,action.getAction());
//		
//		data.set(1, "20.8");
//		setPEPrice(new ValueTime("12348", 15.35));
//		process(script,data,action,null,null);
//		assertEquals(HOLDING_PE,action.getAction());
//		
//		data.set(1, "19.0");
//		process(script,data,action,null,null);
//		assertEquals(HOLDING_PE,action.getAction());
//		
//		data.set(1, "19.0");
//		process(script,data,action,null,null);
//		assertEquals(HOLDING_PE,action.getAction());
//		
//		data.set(1, "20.9");
//		process(script,data,action,null,null);
//		assertEquals(REVERSE_POSITION,action.getAction());
		
		System.out.println(System.currentTimeMillis()-t);
		
	}
	
	private static void setPEPrice(ValueTime newPrice){
		getPE().setCurrentPrice(getPE().getNewPrice());
		getPE().setNewPrice(newPrice);
	}
	private static void setCEPrice(ValueTime newPrice){
		getCE().setCurrentPrice(getCE().getNewPrice());
		getCE().setNewPrice(newPrice);
	}
	
	private static ScriptData getPE(){
		return PE;
	}
	private static ScriptData getCE(){
		return CE;
	}
	
	private static void processDirectionalCount(ScriptData script,boolean priceIncreased,boolean noChange){
		if(priceIncreased){
			if(script.getDownTrendCount()>0){
				script.clearDownTrendCount();
			}
			script.increaseUptrend();
		}
		
		if(!priceIncreased && !noChange){
			if(script.getUptrendCount()>0){
				script.clearUptrendCount();
			}
			script.increaseDowntrend();
		}
		if(!noChange){
			if(script.getUptrendCount()>0){
				script.increaseUptrend();
			}else{
				script.increaseDowntrend();
			}
		}
	}
	/**
	 * 
	 * @param script
	 * @param newPriceValue
	 * @param action
	 * @return
	 * 
	 * Uptrend: the lowest in a block should be higher than the lowest in the previous block.
	 * Check varition on PE/CE if it reduces... it indicates people feel there is a correction.
	 * 
	 * 
	 * I need a high sustain and a low sustain. 
	 * if the number stays above lowest high/support, then it is staying high, it the number stays within high low then it going lower./resistance
	 * 
	 * 
	 * Global pattern watch which tells me if going down/reversal(down-reversal/up-reversal)/up. once change is reversal, continue reversal is confirmed change.
	 * 
	 * The current value up/down from opening price
	 * 
	 * Last 5-10 records/from a point, were they closer to bracket high or bracket low? IMPORTANT WILL TELL THE PUSH AND WHAT PEOPLE THINK. IT COULD INDICATE RESISTANCE/SUPPORT.. SO WATTCH OUT
	 * 
	 * If the bracket high is broken, then bracket low should increase as well. similarly, when bracket low breaks, braket high decrease
	 * question is by how much. 
	 * 
	 * Check change in PE/CE price which might indicate sentiments.
	 * 
	 */
	private static Action process(Position currentPosition,ScriptData script,List<String> data,Action action,ScriptData peScript,ScriptData ceScript){
		
		StringBuilder logBuilder = new StringBuilder();
		
		ValueTime newPriceValue = new ValueTime(data.get(0), Double.valueOf(data.get(1)));
		ValueTime peNewPrice = new ValueTime(data.get(0), Double.valueOf(data.get(2)));
		ValueTime ceNewPrice = new ValueTime(data.get(0), Double.valueOf(data.get(3)));
		
		
		if(script.getNewPrice()==null){
			script.setNewPrice(newPriceValue);
		}
		
		script.setCurrentPrice(script.getNewPrice());
		script.setNewPrice(newPriceValue);
		double newPrice = newPriceValue.getValue();
		String time = newPriceValue.getTime();
		
		boolean priceIncreased = script.isNewPriceIncreased();
		boolean same = script.isNewPriceSameAsCurrent();
		
		
		/**TODO Apply this  trend to the logic...
		 * Is it trending up or down...
		 */
		String trend = "TREND_NOT_AVAILABLE";
		
		/**
		 * TODO
		 * This needs to be stored... calling hits performance.
		 */
		int niftyRecordSize = CacheService.niftyCount();

		/**
		 * Is current price higher than last 5 records low.
		 * Is current price lower than last 5 records high.
		 */
		boolean lowerThanHighest = false;
		boolean higherThanLowest = false;
		TDoubleList queryList = null;
		
		if(niftyRecordSize>10){
			queryList = CacheService.getItemsFromNiftyCache(10);
			trend = Chart.calculateTrend(queryList);
			
			lowerThanHighest = Chart.isLowerThanHighest(newPrice, queryList.subList(5, 10));
			higherThanLowest = Chart.isHigherThanLowest(newPrice, queryList.subList(5, 10));
		}else if(niftyRecordSize>4){
			queryList = CacheService.getItemsFromNiftyCache(4);
			trend = Chart.calculateTrend(queryList);
			
			lowerThanHighest = Chart.isLowerThanHighest(newPrice, queryList);
			higherThanLowest = Chart.isHigherThanLowest(newPrice, queryList);
		}
		
		logBuilder.append("trend:"+trend+",lowerThanHighest:"+lowerThanHighest+",higherThanLowest:"+higherThanLowest);
		/**
		 * 
		 */
		
		
		/**
		 * calculate if there has been continous increase towards one direction.
		 */
		processDirectionalCount(script, priceIncreased, same);
		
		if(script.getBracketHigh()==null && script.getBracketLow()==null){
			script.setBracketHigh(new ValueTime(newPriceValue.getTime(), newPriceValue.getValue()));
			script.setBracketLow(new ValueTime(newPriceValue.getTime(), newPriceValue.getValue()-4));
		}
		
		if(script.getBracketHigh()!=null && script.getBracketLow()!=null){
			script.calculateMargins();
		}
		
		boolean priceTowardsHigh = false;
		boolean priceTowardsLow = false;
		if(TOWARDS_HIGH.equals(isCloseTo(script.getBracketLow().getValue(),newPrice,script.getBracketHigh().getValue()))){
			priceTowardsHigh = true;
		}else if(TOWARDS_LOW.equals(isCloseTo(script.getBracketLow().getValue(),newPrice,script.getBracketHigh().getValue()))){
			priceTowardsLow = true;
		}

		logBuilder.append(",priceTowardsHigh:"+priceTowardsHigh+",priceTowardsLow:"+priceTowardsLow);
		
		
		
		String response = null;
		/**
		 * Current action 
		 */
		String holding  = action.getAction();

		if(same && !HOLDING_NOTHING.equals(holding)){
//			if(HOLDING_PE.equals(holding)){
//				response = HOLDING_PE;
//			}else if(HOLDING_CE.equals(holding)){
//				response = HOLDING_CE;
//			}else{
//				response = HOLD;
//			}
			response = holding;
		}
		
		
		/**
		 * If it is reverse pe/ce, then i need to store the reverse price
		 * if it reverse pe, then i can only buy if next price is higher/low than the reverse point!
		 * if this does not work then use the margins
		 */
		
		if(REVERSE_TO_PE.equals(holding)){
			if(newPrice<=script.getReversePositionValue()){
				response = BUY_PE;
			}
		}else if(REVERSE_TO_CE.equals(holding)){
			if(newPrice>=script.getReversePositionValue()){
				response = BUY_CE;
			}
		}
		logBuilder.append(",script.getReversePositionValue():"+script.getReversePositionValue());
		/**
		 * If the current value is back to the reversing value... it means 
		 * there has been a pull back from the reverse.
		 */

		
		
		
		if(!priceIncreased && HOLDING_NOTHING.equals(holding)){
			response = BUY_PE;
			//tactical high
			script.setBracketLow(new ValueTime(time, newPrice));
			script.setBracketHigh(new ValueTime(time,script.getCurrentPrice().getValue()));
		}else if(priceIncreased && HOLDING_NOTHING.equals(holding)){
			response = BUY_CE;
			//tactical low
			script.setBracketHigh(new ValueTime(time, newPrice));
			script.setBracketLow(new ValueTime(time, script.getCurrentPrice().getValue()));
		}
		
//		double profit = 0;
//		if(HOLDING_PE.equals(holding)){
//			currentPosition.setSell(peNewPrice.getValue());
//			if(currentPosition.getProfit()<-350){
//				response = REVERSE_TO_CE;
//			}
//			currentPosition.setSell(0.00);
//		}else if(HOLDING_CE.equals(holding)){
//			currentPosition.setSell(ceNewPrice.getValue());
//			if(currentPosition.getProfit()<-350){
//				response = REVERSE_TO_PE;
//			}
//			currentPosition.setSell(0.00);
//		}
		
		
		ValueTime bracketHigh = script.getBracketHigh();
		ValueTime bracketLow = script.getBracketLow();
	
		double newOptionPriceDifference= getPE().newPriceCurrentDifference();
		double scriptPriceDifference = script.newPriceCurrentDifference();
		
		
		logBuilder.append(",brackethigh:"+script.getBracketHigh().getValue()+",brackethighlow:"+script.getBracketHighLowerValue().getValue()+",bracketlowhigh:"
							+script.getBracketLowHigherValue().getValue()+",bracketlow:"+script.getBracketLow().getValue()+",reversePositionCount:"+script.getReversePositionCount());
		
		
		/**
		 * IMPLEMENT*****************************************
		 * last 5+5 records increasing
		*	current price is nearer to high & greater than last 5 record low.
		*   and ideally current price greater than previous price
			*************************************88
		 */
		
		/**
		 * PE Logic
		 */
		if(!priceIncreased && !same && HOLDING_PE.equals(holding)){
			if(bracketLow!=null){
				if(newPrice<bracketLow.getValue()){
					script.setBracketLow(new ValueTime(time, newPrice));
					response =  HOLDING_PE;
				}
			}
			
			response = HOLDING_PE;
			/**
			 * if the option price drops.
			 */
			if(newOptionPriceDifference<0){
				response = HOLD_OPTION_DROP;
			}
		}else if(priceIncreased && HOLDING_PE.equals(holding)){
			
			//int highNearVisits = bracketHigh.getNumberOfNearVisits();
			
			if(bracketHigh!=null && newPrice>bracketHigh.getValue()){
				script.setBracketHigh(new ValueTime(time,newPrice));
				bracketHigh = script.getBracketHigh();
				script.calculateMargins();
			}
//			else if(bracketHigh!=null){
//				bracketHigh.setNumberOfNearVisits(bracketHigh.getNumberOfNearVisits()+1);
//			}
			
			/**
			 * If greater than higher low then reverse position
			 * 
			 * Needs to hold it for 2 times before changing... 
			 */
			if((priceTowardsHigh && higherThanLowest) || newPriceValue.getValue()>=script.getBracketHighLowerValue().getValue()){
				if(script.getReversePositionCount()>=2){
					//reverse only if the buy and sell price are not equal.
					if(currentPosition.getBuy()!=peNewPrice.getValue()){
						//System.out.println("Reverse CE 1");
						response = REVERSE_TO_CE;
						script.setReversePositionValue(newPrice);
						script.setCountSinceLastReverse(0);
					}
				}else{
					script.setReversePositionCount(script.getReversePositionCount()+1);
					response = HOLDING_PE;
				}
				
			}else{
				script.setReversePositionCount(0);
			}
//			if(highNearVisits>1 
//					&& TOWARDS_HIGH.equals(isCloseTo(bracketLow.getValue(),newPrice,bracketHigh.getValue()))){
//				response = REVERSE_POSITION;
//			}
			if(bracketLow!=null && !REVERSE_TO_CE.equals(response)){
//				if(bracketLow.getNumberOfNearVisits()< NEAR_MISS_COUNT){
//					bracketLow.setNumberOfNearVisits(bracketLow.getNumberOfNearVisits()+1);
//					response =  HOLDING_PE;
//					
//					if(newOptionPriceDifference<0){
//						if(currentPosition.getBuy()!=peNewPrice.getValue()){
//							System.out.println("Reverse CE 2");
//							response = REVERSE_TO_CE;
//							script.setReversePositionValue(newPrice);
//						}
//					}
//				}
				
//				else if(bracketLow.getNumberOfNearVisits()==NEAR_MISS_COUNT){
//					// 3 near miss means it is changing trend..
//					if(currentPosition.getBuy()!=peNewPrice.getValue()){
//						bracketLow.setNumberOfNearVisits(0);
//						System.out.println(3);
//						response=REVERSE_TO_CE;
//						script.setReversePositionValue(newPrice);
//					}
//				}
			}
		}
		
		/**
		 * CE Logic
		 */
		
		newOptionPriceDifference = getCE().newPriceCurrentDifference();
		
		if(!priceIncreased && !same && HOLDING_CE.equals(holding)){
			
			int lowNearVisits = bracketLow.getNumberOfNearVisits();
			
			if(bracketLow!=null && newPrice<bracketLow.getValue()){
				script.setBracketLow(new ValueTime(time,newPrice));
				bracketLow = script.getBracketLow();
				script.calculateMargins();
			}
			
//			else if(bracketLow!=null){
//				bracketLow.setNumberOfNearVisits(bracketLow.getNumberOfNearVisits()+1);
//			}
			
//			if(lowNearVisits>1 
//					&& TOWARDS_LOW.equals(isCloseTo(bracketLow.getValue(),newPrice,bracketHigh.getValue()))){
//				response = REVERSE_POSITION;
//			}
			if((priceTowardsLow && lowerThanHighest) || newPriceValue.getValue()<=script.getBracketLowHigherValue().getValue()){
				if(script.getReversePositionCount()>=2){
					if(currentPosition.getBuy()!=ceNewPrice.getValue()){
						//System.out.println("Reverse PE 1");
						response = REVERSE_TO_PE;
						script.setReversePositionValue(newPrice);
						script.setCountSinceLastReverse(0);
					}
				}else{
					script.setReversePositionCount(script.getReversePositionCount()+1);
					response = HOLDING_CE;
				}
			}else{
				script.setReversePositionCount(0);
			}
			
			if(bracketHigh!=null && !REVERSE_TO_PE.equals(response)){
//				if(bracketHigh.getNumberOfNearVisits()< NEAR_MISS_COUNT){
//					bracketHigh.setNumberOfNearVisits(bracketHigh.getNumberOfNearVisits()+1);
//					response =  HOLDING_CE;
//					
//					if(newOptionPriceDifference<0){
//						if(currentPosition.getBuy()!=ceNewPrice.getValue()){
//							System.out.println("Reverse PE 2");
//							response = REVERSE_TO_PE;
//							script.setReversePositionValue(newPrice);
//						}
//					}					
//				}
				
//				else if(bracketHigh.getNumberOfNearVisits()==NEAR_MISS_COUNT){
//					// 3 near miss means it is changing trend..
//					if(currentPosition.getBuy()!=ceNewPrice.getValue()){
//						bracketHigh.setNumberOfNearVisits(0);
//						response=REVERSE_TO_PE;
//						script.setReversePositionValue(newPrice);
//					}
//				}
			}
		}else if(priceIncreased && HOLDING_CE.equals(holding)){
			if(bracketHigh!=null){
				if(newPrice>bracketHigh.getValue()){
					script.setBracketHigh(new ValueTime(time, newPrice));
					response =  HOLDING_CE;
				}
				
				response = HOLDING_CE;
				if(newOptionPriceDifference<0){
					response = HOLD_OPTION_DROP;
				}				
			}
		}
		/**
		 * TODO
		 * there is an area after reverse to figure out when to buy the reverse position...
		 * 
		 */
		if(log){
			System.out.println(logBuilder.toString());
		}
		if(response==null){
			//System.out.println("No response generated.. so holding:"+action.getAction());
			response = action.getAction();
			//throw new RuntimeException("No response generated");
		}
		
		
		action.setAction(response);
		return action;
		
	}
	
	private static String isCloseTo(double low,double value,double high){
		double differenceFromHigh = high-value;
		double differenceFromLow = value-low;
		
		if(differenceFromHigh>differenceFromLow){
			return "towards_low";
		}
		
		if(differenceFromHigh<differenceFromLow){
			return "towards_high";
		}
		
		return "neither";
	}
	
}
