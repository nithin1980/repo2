package com.mod;

import gnu.trove.list.TDoubleList;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import sun.security.util.PendingException;
import static org.junit.Assert.*;

import com.mod.datafeeder.DataFeed;
import com.mod.objects.Action;
import com.mod.objects.GroupPosition;
import com.mod.objects.Position;
import com.mod.objects.ScriptData;
import com.mod.objects.ValueTime;

public class ProcessingBlock {

	public static final String BUY_PE = "buy_pe";
	public static final String BUY_CE = "buy_ce";
	public static final String HOLD = "hold";
	public static final String REVERSE_TO_PE = "reverse_position_pe";
	public static final String REVERSE_TO_CE = "reverse_position_ce";
	public static final String HOLDING_PE = "hold_pe";
	public static final String HOLDING_CE = "hold_ce";
	public static final String HOLDING_NOTHING = "hold_nothing";
	
	public static final String INITIAL_POSITION = "INITIAL_POSITION";
	public static final String ENTERED_POSITION = "ENTERED_POSITION";

	
	
	public static final String RANGE_BOUND_PE = "range_bound_pe";
	public static final String RANGE_BOUND_CE = "range_bound_ce";
	
	
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
		
		
		List<List<String>> dataList = DataFeed.data();
		int size   = dataList.size();
		//Action action  = new Action(HOLDING_NOTHING);
		Action action  = new Action(HOLDING_CE);
		ScriptData script = new ScriptData();
		ScriptData peScript = new ScriptData();
		ScriptData ceScript = new ScriptData();
		List<String> values = null;
		
		CacheService.clearNifty();
		CacheService.dumpNifty();
		//ApplicationHelper.threadService.shutdown();
		
		DashBoard.positionMap.put("1", new GroupPosition());
		DashBoard.positionMap.put("2", new GroupPosition());
		
		GroupPosition pos = DashBoard.positionMap.get("1");
		Position newPosition = new Position("CE",70.0,217.5);
		newPosition.setBuyRecord(0);
		pos.getPePositions().add(newPosition);
		
		addReversePosition(newPosition, "PE",68);
		
		boolean sellDuringBuy = true;
		
		long t = System.currentTimeMillis();
		
		for(int i=0;i<size;i++){
			values = dataList.get(i);
			CacheService.addNifty(Double.valueOf(values.get(1)));
			if(i==8471){
				log=false;
			}
			
			if(i==8690){
				log=false;
			}
			
			
			peScript.setNewPrice(new ValueTime(values.get(0),Double.valueOf(values.get(2))));
			ceScript.setNewPrice(new ValueTime(values.get(0),Double.valueOf(values.get(3))));
			action = process(newPosition,script,values , action, peScript, ceScript);
			values.add(action.getAction());
			

			double currentProfit = 0;
			
			if(i>1774 && i<3393){
				if(newPosition.getName().equals("PE")){
					System.out.println("current profit:"+getOptionProfit(newPosition, Double.valueOf(values.get(2)))+"  reverse profit:"+getOptionProfit(newPosition.getReversePosition(), Double.valueOf(values.get(3))));
				}else{
					System.out.println("current profit:"+getOptionProfit(newPosition, Double.valueOf(values.get(3)))+"  reverse profit:"+getOptionProfit(newPosition.getReversePosition(), Double.valueOf(values.get(2))));
				}
			}
			

				if(REVERSE_TO_PE.equals(action.getAction()) || HOLDING_CE.equals(action.getAction())){
					if(newPosition.getSell()==0){
						currentProfit = getOptionProfit(newPosition, Double.valueOf(values.get(3)));
						//System.out.println("current profit:"+currentProfit+"..."+i);
						if(currentProfit<Constants.LossLimit_1){
							newPosition.setSell(Double.valueOf(values.get(3)));
							newPosition.setSellRecord(i);
							
							
						}
						if(currentProfit>Constants.HighLimit_1){
							newPosition.setSell(Double.valueOf(values.get(3)));
							newPosition.setSellRecord(i);
						}
						
//						if(newPosition.highProfit()>250 && newPosition.percentageFromHighProfit(currentProfit)>15){
//							newPosition.setSell(Double.valueOf(values.get(3)));
//							newPosition.setSellRecord(i);
//						}
						
					}
				}
				
				if(REVERSE_TO_CE.equals(action.getAction()) || HOLDING_PE.equals(action.getAction())){
					if(newPosition.getSell()==0){
						currentProfit = getOptionProfit(newPosition, Double.valueOf(values.get(2)));
						
						if(currentProfit<Constants.LossLimit_1){
							newPosition.setSell(Double.valueOf(values.get(2)));
							newPosition.setSellRecord(i);
						}
						if(currentProfit>Constants.HighLimit_1){
							newPosition.setSell(Double.valueOf(values.get(2)));
							newPosition.setSellRecord(i);
						}
						
//						if(newPosition.highProfit()>250 && newPosition.percentageFromHighProfit(currentProfit)>15){
//							newPosition.setSell(Double.valueOf(values.get(2)));
//							newPosition.setSellRecord(i);
//						}
						
						
					}
				}
				
		

			
			if(sellDuringBuy){
				//selling during buy
				if(BUY_PE.equals(action.getAction())){
					if(newPosition.getSell()==0){
						newPosition.setSell(Double.valueOf(values.get(3)));
						newPosition.setSellRecord(i);
					}
					
					
					
				}else if(BUY_CE.equals(action.getAction())){
					if(newPosition.getSell()==0){
						newPosition.setSell(Double.valueOf(values.get(2)));
						newPosition.setSellRecord(i);
					}
				}
			}
			
			
			if(BUY_PE.equals(action.getAction())){
				newPosition = new Position("PE",70.0,Double.valueOf(values.get(2)));
				newPosition.setBuyRecord(i);
				pos.getPePositions().add(newPosition);
				action.setAction(HOLDING_PE);
				//action.setAction(INITIAL_POSITION);
				addReversePosition(newPosition, "CE", Double.valueOf(values.get(3)));
			}else if(BUY_CE.equals(action.getAction())){
				newPosition = new Position("CE",70.0,Double.valueOf(values.get(3)));
				newPosition.setBuyRecord(i);
				pos.getCePositions().add(newPosition);
				action.setAction(HOLDING_CE);
				//action.setAction(INITIAL_POSITION);
				addReversePosition(newPosition, "PE", Double.valueOf(values.get(2)));
			}
			
			/** Disabled for now.***/
//			if(INITIAL_POSITION.equals(action.getAction())){
//				
//				if("PE".equals(newPosition.getName())){
//					currentProfit = getOptionProfit(newPosition, peScript.getNewPrice().getValue());
//					
//					if(currentProfit<-300){
//						newPosition.setBuy(peScript.getNewPrice().getValue());
//						action.setAction(HOLDING_PE);
//					}
//				}
//				if("CE".equals(newPosition.getName())){
//					currentProfit = getOptionProfit(newPosition, ceScript.getNewPrice().getValue());
//					
//					if(currentProfit<-300){
//						newPosition.setBuy(ceScript.getNewPrice().getValue());
//						action.setAction(HOLDING_CE);
//					}
//				}
//				
//			}
			
			if(INITIAL_POSITION.equals(action.getAction())){
				
				double reverseProfit = 0.0;
				
				if("PE".equals(newPosition.getName())){
					currentProfit = getOptionProfit(newPosition, peScript.getNewPrice().getValue());
					reverseProfit = getOptionProfit(newPosition.getReversePosition(), ceScript.getNewPrice().getValue());
					if(currentProfit<0 && reverseProfit>0){
						newPosition.setBuy(peScript.getNewPrice().getValue());
						pos.getCePositions().get(pos.getCePositions().size()-1).setSell(ceScript.getNewPrice().getValue());
						pos.getCePositions().get(pos.getCePositions().size()-1).setSellRecord(i);
						action.setAction(HOLDING_PE);
					}
				}
				if("CE".equals(newPosition.getName())){
					currentProfit = getOptionProfit(newPosition, ceScript.getNewPrice().getValue());
					reverseProfit = getOptionProfit(newPosition.getReversePosition(), peScript.getNewPrice().getValue());
					if(currentProfit<0 && reverseProfit>0){
						newPosition.setBuy(ceScript.getNewPrice().getValue());
						pos.getPePositions().get(pos.getPePositions().size()-1).setSell(peScript.getNewPrice().getValue());
						pos.getPePositions().get(pos.getPePositions().size()-1).setSellRecord(i);
						action.setAction(HOLDING_CE);
					}
				}
				
			}
			
			if(!sellDuringBuy){
				//sell first
				if(REVERSE_TO_PE.equals(action.getAction())){
					if(newPosition.getSell()==0){
						newPosition.setSell(Double.valueOf(values.get(3)));
						newPosition.setSellRecord(i);
					}
				}else if(REVERSE_TO_CE.equals(action.getAction())){
					if(newPosition.getSell()==0){
						newPosition.setSell(Double.valueOf(values.get(2)));
						newPosition.setSellRecord(i);
					}
				}
			}
			
			if(log){
				//System.out.println(values);
			}
			
		}
		
		System.out.println(pos.total());
		System.out.println("PE sale:"+pos.getPePositions().size()+", CE sale:"+pos.getCePositions().size());
		System.out.println(pos);
		
//		System.out.println(System.currentTimeMillis()-t);
		
	}
	
	private static void addReversePosition(Position position,String name,double price){
		position.setReversePosition(new Position(name, 70.0, price));
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
		 * Set the high/low profit of position
		 */
		if("PE".equals(currentPosition.getName())){
			currentPosition.setHighValue(peNewPrice.getValue());
			currentPosition.setLowValue(peNewPrice.getValue());
		}
		if("CE".equals(currentPosition.getName())){
			currentPosition.setHighValue(ceNewPrice.getValue());
			currentPosition.setLowValue(ceNewPrice.getValue());
		}

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
		
		logBuilder.append("nifty:"+newPrice+","+peNewPrice.getValue()+","+ceNewPrice.getValue()+",trend:"+trend+",lowerThanHighest:"+lowerThanHighest+",higherThanLowest:"+higherThanLowest);
		
		if(script.getBracketHigh()==null && script.getBracketLow()==null){
			script.setBracketHigh(new ValueTime(newPriceValue.getTime(), newPriceValue.getValue()));
			script.setBracketLow(new ValueTime(newPriceValue.getTime(), newPriceValue.getValue()-Constants.BracketLowDifference));
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
			response = holding;
		}
		

		/**
		 * If the current value is back to the reversing value... it means 
		 * there has been a pull back from the reverse.
		 */
//		if(!priceIncreased && HOLDING_NOTHING.equals(holding) && Chart.DOWNTREND.equals(trend)){
//			response = BUY_PE;
//			//tactical high
//			script.setBracketHigh(new ValueTime(time,newPrice));
//			script.setBracketLow(new ValueTime(time, newPrice-3.8));
//			script.calculateMargins();
//		}else if(priceIncreased && HOLDING_NOTHING.equals(holding) && Chart.UPTREND.equals(trend)){
//			response = BUY_CE;
//			//tactical low
//			script.setBracketHigh(new ValueTime(time,newPrice));
//			script.setBracketLow(new ValueTime(time, newPrice-3.8));
//			script.calculateMargins();
//		}
		
		ValueTime bracketHigh = script.getBracketHigh();
		ValueTime bracketLow = script.getBracketLow();

		

		
		
		response = handleReverseCondition(script, holding, newPrice, response,currentPosition,peNewPrice,ceNewPrice);
		
		logBuilder.append(",script.getReversePositionValue():"+script.getReversePositionValue());
		

		
		
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
		
		logBuilder.append(",brackethigh:"+script.getBracketHigh().getValue()+",brackethighlow:"+script.getBracketHighLowerValue().getValue()+",bracketlowhigh:"
							+script.getBracketLowHigherValue().getValue()+",bracketlow:"+script.getBracketLow().getValue()+",reversePositionOppurtunityCount:"+script.getReversePositionOppurtunityCount());
		
		
		response = reversing(script, holding, priceTowardsHigh, higherThanLowest, newPrice, currentPosition, peNewPrice, ceNewPrice, response, same, priceTowardsLow, lowerThanHighest, priceIncreased);
		
		/**
		 * Do this at the end... to prevent it from self chasing a high/low...
		 */
		if(newPrice>bracketHigh.getValue()){
			script.setBracketHigh(new ValueTime(time,newPrice));
			bracketHigh = script.getBracketHigh();
		}
		
		if(newPrice<bracketLow.getValue()){
			script.setBracketLow(new ValueTime(time,newPrice));			
			bracketLow =  script.getBracketLow();
		}
		
		script.calculateMargins();
		
		/**
		 * TODO
		 * there is an area after reverse to figure out when to buy the reverse position...
		 * 
		 */
		if(response==null){
			//System.out.println("No response generated.. so holding:"+action.getAction());
			response = action.getAction();
			//throw new RuntimeException("No response generated");
		}
		logBuilder.append(","+response);
		if(log){
			System.out.println(logBuilder.toString());
		}
		
		
		action.setAction(response);
		return action;
		
	}
	
	private static String reversing(ScriptData script,String holding,boolean priceTowardsHigh,boolean higherThanLowest,double newPrice,Position currentPosition,ValueTime peNewPrice,ValueTime ceNewPrice,String response,
			boolean same,boolean priceTowardsLow,boolean lowerThanHighest,boolean priceIncreased ){
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
		if(HOLDING_PE.equals(holding)){
			/**
			 * If greater than higher low then reverse position
			 * 
			 * Needs to hold it for 2 times before changing... 
			 */
			if((priceTowardsHigh && higherThanLowest) || newPrice>=script.getBracketHighLowerValue().getValue()){
				if(script.getReversePositionOppurtunityCount()>=Constants.ReversePositionOppurtunityCount){
					//reverse only if the buy and sell price are not equal.
					if(currentPosition.getBuy()!=peNewPrice.getValue()){
						//System.out.println("Reverse CE 1");
						response = REVERSE_TO_CE;
						script.setReversePositionValue(newPrice);
						script.setCountSinceLastReverse(0);
						
						if(currentPosition.getReversePositionProfit()==0){
							currentPosition.setReversePositionProfit(getOptionProfit(currentPosition, peNewPrice.getValue()));
							//System.out.println("Setting pe profit:"+getOptionProfit(currentPosition, peNewPrice.getValue()));
						}
					}
				}else{
					script.setReversePositionOppurtunityCount(script.getReversePositionOppurtunityCount()+1);
					response = HOLDING_PE;
				}
				
			}else{
				script.setReversePositionOppurtunityCount(0);
			}
		}
		/**
		 * CE Logic
		 */
		
		if(!same && HOLDING_CE.equals(holding)){
		//if(!priceIncreased && !same && HOLDING_CE.equals(holding)){
			
			if((priceTowardsLow && lowerThanHighest) || newPrice<=script.getBracketLowHigherValue().getValue()){
				if(script.getReversePositionOppurtunityCount()>=Constants.ReversePositionOppurtunityCount){
					if(currentPosition.getBuy()!=ceNewPrice.getValue()){
						//System.out.println("Reverse PE 1");
						response = REVERSE_TO_PE;
						script.setReversePositionValue(newPrice);
						script.setCountSinceLastReverse(0);
						
						if(currentPosition.getReversePositionProfit()==0){
							currentPosition.setReversePositionProfit(getOptionProfit(currentPosition, ceNewPrice.getValue()));
							//System.out.println("Setting ce profit:"+getOptionProfit(currentPosition,  ceNewPrice.getValue()));
						}
						
					}
				}else{
					script.setReversePositionOppurtunityCount(script.getReversePositionOppurtunityCount()+1);
					response = HOLDING_CE;
				}
			}else{
				script.setReversePositionOppurtunityCount(0);
			}
			
		}
		else if(priceIncreased && HOLDING_CE.equals(holding)){
				response = HOLDING_CE;
		}
		
		return response;
	}
	private static double getOptionProfit(Position currentPosition, double value){
		
		Position localPosition =  new Position(currentPosition);
		
		localPosition.setSell(value);
		double profit = localPosition.getProfit();
		return profit;
	}
	private static String handleReverseCondition(ScriptData script,String holding,double newPrice,String response,Position currentPosition,ValueTime peNewPrice,ValueTime ceNewPrice){
		/**
		 * If it is reverse pe/ce, then i need to store the reverse price
		 * if it reverse pe, then i can only buy if next price is higher/low than the reverse point!
		 * 
		 * If it does not reach the reverse value, then go back to hold after 4 counts...
		 * 
		 * if this does not work then use the margins
		 */
		
		double currentProfit = 0;
		
		if(REVERSE_TO_PE.equals(holding)){
			
			currentProfit = getOptionProfit(currentPosition, ceNewPrice.getValue());
			
			if(newPrice<=script.getReversePositionValue()){
				response = BUY_PE;
				script.setCountSinceLastReverse(0);
				currentPosition.clearReversePositionProfit();
				currentPosition.clearReversePositionHighProfit();
			}
			/**
			 * Either going up or range bound.
			 * 
			 */
			else if(script.getCountSinceLastReverse()==10){
				
				
				//check if the price is near reverse value or near bracket high.
				//System.out.println("checking ce profit now");
				if(currentPosition.isProfitDecreasedEnough(currentProfit)){
					response = BUY_PE;
					currentPosition.clearReversePositionProfit();
					currentPosition.clearReversePositionHighProfit();
				}else{
					response  =  HOLDING_CE;
					
					script.setCountSinceLastReverse(0);
					
					//System.out.println("current:"+currentProfit+",stored:"+currentPosition.getReversePositionHighProfit()+",current ce:"+ceNewPrice.getValue());
					
				}
				
				
				
//				String result = isCloseTo(script.getReversePositionValue(),newPrice,bracketHigh.getValue());
//				if(TOWARDS_HIGH.equals(result)){
//					//System.out.println("range bounded... pe--towards high");
//					response = BUY_CE;
//				}else{
//					//System.out.println("range bounded... pe--towards low");
//					response = BUY_PE;
//				}
			}
			
//			else if(script.getCountSinceLastReverse()==20){
//				System.out.println(" range bounded.for 20 records...check if worth switching to PE???");
//				//check the option price to see which way the wind is blowing...
//				//has it increased..  indicating uptrend.
//				//has it gone down indicating downtrend...
//				response = REVERSE_TO_PE;
//				//change the reverse price and set the count to zero?
//				script.setReversePositionValue(newPrice);
//				script.setCountSinceLastReverse(0);
//			}
			else{
				script.setCountSinceLastReverse(script.getCountSinceLastReverse()+1);
				currentPosition.setReversePositionHighProfit(currentProfit);
			}
		}else if(REVERSE_TO_CE.equals(holding)){
			
			currentProfit = getOptionProfit(currentPosition, peNewPrice.getValue());
			
			if(newPrice>=script.getReversePositionValue()){
				response = BUY_CE;
				script.setCountSinceLastReverse(0);
				currentPosition.clearReversePositionProfit();
				currentPosition.clearReversePositionHighProfit();
			}
			else if(script.getCountSinceLastReverse()==10){
				
				//System.out.println("checking pe profit now");
				if(currentPosition.isProfitDecreasedEnough(currentProfit)){
					response = BUY_CE;
					currentPosition.clearReversePositionProfit();
					currentPosition.clearReversePositionHighProfit();
				}else{
					response  =  HOLDING_PE;
					script.setCountSinceLastReverse(0);
					//System.out.println("current:"+currentProfit+",stored:"+currentPosition.getReversePositionHighProfit()+",curren pe:"+peNewPrice.getValue());
					
				}
				
				
				
//				String result = isCloseTo(script.getReversePositionValue(),newPrice,bracketHigh.getValue());
//				if(TOWARDS_LOW.equals(result)){
//					//System.out.println("range bounded... ce--towards low");
//					response = BUY_PE;
//				}else{
//					//System.out.println("range bounded... ce--towards high");
//					response = BUY_CE;
//				}
			}
			
//			else if(script.getCountSinceLastReverse()==20){
//				System.out.println(" range bounded.for 20 records...check if worth switching to CE???");
//				response = REVERSE_TO_CE;
//				script.setReversePositionValue(newPrice);
//				script.setCountSinceLastReverse(0);
//				
//			}
			else{
				script.setCountSinceLastReverse(script.getCountSinceLastReverse()+1);
				currentPosition.setReversePositionHighProfit(currentProfit);
			}
		}
		
		return response;
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
