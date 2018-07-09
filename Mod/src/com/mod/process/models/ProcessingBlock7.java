package com.mod.process.models;

import com.mod.interfaces.KiteStockConverter;
import com.mod.objects.Action;
import com.mod.objects.GroupPosition;
import com.mod.objects.Position;
import com.mod.objects.ScriptData;
import com.mod.objects.ValueTime;

public class ProcessingBlock7 extends ProcessModelAbstract {


	private static boolean log = false;
	
	private int positionTime=0;
	
	private double triggerValue;
	
	private Position realPosition;
	
	public ProcessingBlock7(CacheService cacheService) {
		// TODO Auto-generated constructor stub
		
		super();
		setCacheService(cacheService);
		DashBoard.positionMap.put(modelid(), new GroupPosition());

	}
	
	@Override
	public String modelid() {
		// TODO Auto-generated method stub
		return "pmodel7";
	}
	
	public void close(){
		if(HOLDING_CE.equals(getCurrentAction().getAction())){
			getCurrentPosition().setSell(getCE_PRICE().getNewPrice().getValue());
			getCurrentPosition().setSellRecord(saleRecord);
			DashBoard.positionMap.get(modelid()).getCePositions().add(getCurrentPosition());
		}else if(HOLDING_PE.equals(getCurrentAction().getAction())){
			getCurrentPosition().setSell(getPE_PRICE().getNewPrice().getValue());
			getCurrentPosition().setSellRecord(saleRecord);
			DashBoard.positionMap.get(modelid()).getCePositions().add(getCurrentPosition());
		}
	}
	
	
	@Override
	public void processNow() {
		// TODO Auto-generated method stub
		
		long t = System.currentTimeMillis();
		
		completedProcess=false;
		GroupPosition pos = DashBoard.positionMap.get(modelid());
		double currentPrice = 0;
		
		double currentProfit = 0;
		double reverseProfit = 0;
		double currentPer=0;
		double reversePer=0;
		
		updateScriptPrices();
		if(positionTime>0){
			positionTime++;
		}
		double niftydiff = getNIFTY().newPriceDiffFromCurrent();
		
		String trend = getCacheService().niftyTrend;
		System.out.println("trend:"+trend);
		if(getCurrentPosition()==null){
			if(niftydiff>=2.5 && Chart.DOWNTREND.equals(trend)){
				System.out.println("-----up-------"+getNIFTY().getNewPrice().getValue()+"------------");
				double positionid = getCE_PRICE().getId();
				String positionName = KiteStockConverter.KITE_STOCK_LIST.get(positionid);
				currentPrice = getCacheService().PRICE_LIST.get(positionid);
				
				setCurrentPosition(new Position(positionName, getPositionExpense(), currentPrice));
				addReversePosition(getCurrentPosition());
				
				positionTime++;
				
				
			}
			else if(niftydiff<=-2.5 && Chart.UPTREND.equals(trend)){
				System.out.println("---down---------"+getNIFTY().getNewPrice().getValue()+"------------");
				double positionid = getPE_PRICE().getId();
				String positionName = KiteStockConverter.KITE_STOCK_LIST.get(positionid);
				currentPrice = getCacheService().PRICE_LIST.get(positionid);
				
				setCurrentPosition(new Position(positionName, getPositionExpense(), currentPrice));
				addReversePosition(getCurrentPosition());
				positionTime++;
				
			}
			
			if(getCurrentPosition()!=null){
				if(getCurrentPosition().isPEPosition()){
					setCurrentAction(new Action(HOLDING_PE));
				}else if(getCurrentPosition().isCEPosition()){
					setCurrentAction(new Action(HOLDING_CE));
				}				
			}

		}else{
			if(HOLDING_CE.equals(getCurrentAction().getAction())){
				currentProfit = getOptionProfit(getCurrentPosition(), getCE_PRICE().getNewPrice().getValue());
				reverseProfit = getOptionProfit(getCurrentPosition().getReversePosition(), getPE_PRICE().getNewPrice().getValue());
				currentPer = perCost(currentProfit, getCurrentPosition().cost());
				reversePer = perCost(reverseProfit, getCurrentPosition().cost());
				getCurrentPosition().setHighValue(getCE_PRICE().getNewPrice().getValue());
				getCurrentPosition().setLowValue(getCE_PRICE().getNewPrice().getValue());
				/**
				 * This works fine with large amount like 60k
				 */
				
				if(reversePer>1.5){
					getCurrentPosition().setBuy(getCE_PRICE().getNewPrice().getValue());
					getCurrentPosition().resetHigh(getCurrentPosition().getBuy());
					addReversePosition(getCurrentPosition());
				}
				
				if(getCurrentPosition().getHighValue()>202){
					System.out.println("203 now....");
				}
//				if(currentPer>2.5 && getCurrentPosition().percentageFromHighProfit(currentProfit)>25){
//					System.out.println("High:"+getCurrentPosition().percentageFromHighProfit(currentProfit)+"--"+getCurrentPosition().getHighValue());
//					getCurrentPosition().setSell(getCE_PRICE().getNewPrice().getValue());
//					getCurrentPosition().setSellRecord(saleRecord);
//					pos.getCePositions().add(getCurrentPosition());
//					System.out.println(count+" Sold CE Positions:"+pos);
//					saleRecord++;
//					
//					setCurrentPosition(null);
//					positionTime=0;
//					System.out.println("Position profit:"+pos.total()+" Current:"+currentProfit+" Reverse:"+reverseProfit);
//				}
				
				System.out.println("Holding CE:Position profit:"+pos.total()+" Current:"+currentProfit+" Reverse:"+reverseProfit+"--"+getCE_PRICE().getNewPrice().getValue()+"---C+R Profit:"+(currentProfit+reverseProfit));
				
			}
			else if(HOLDING_PE.equals(getCurrentAction().getAction())){
				currentProfit = getOptionProfit(getCurrentPosition(), getPE_PRICE().getNewPrice().getValue());
				reverseProfit = getOptionProfit(getCurrentPosition().getReversePosition(), getCE_PRICE().getNewPrice().getValue());
				currentPer = perCost(currentProfit, getCurrentPosition().cost());
				reversePer = perCost(reverseProfit, getCurrentPosition().cost());
				getCurrentPosition().setHighValue(getPE_PRICE().getNewPrice().getValue());
				getCurrentPosition().setLowValue(getPE_PRICE().getNewPrice().getValue());
				
				/**
				 * This works fine with large amount like 60k
				 */
				
				if(reversePer>1.5){
					getCurrentPosition().setBuy(getPE_PRICE().getNewPrice().getValue());
					getCurrentPosition().resetHigh(getCurrentPosition().getBuy());
					addReversePosition(getCurrentPosition());
				}				
				if(getCurrentPosition().getHighValue()>202){
					if(getCurrentPosition().percentageFromHighProfit(currentProfit)>580){
						getCurrentPosition().percentageFromHighProfit(currentProfit);
					}
					System.out.println(" now...."+getCurrentPosition().percentageFromHighProfit(currentProfit)+"--"+getCurrentPosition().getBuy()+"--"+getCurrentPosition().getHighValue()+"--"+getPE_PRICE().getNewPrice().getValue());
				}
				
//				if(currentPer>2.5 && getCurrentPosition().percentageFromHighProfit(currentProfit)>25 ){
//					System.out.println("High:"+getCurrentPosition().percentageFromHighProfit(currentProfit)+"--"+getCurrentPosition().getHighValue());
//					getCurrentPosition().setSell(getPE_PRICE().getNewPrice().getValue());
//					getCurrentPosition().setSellRecord(saleRecord);
//					pos.getCePositions().add(getCurrentPosition());
//					System.out.println(count+" Sold PE Positions:"+pos);
//					saleRecord++;
//					
//					setCurrentPosition(null);
//					positionTime=0;
//					System.out.println("Position profit:"+pos.total()+" Current:"+currentProfit+" Reverse:"+reverseProfit);
//				}
				
				System.out.println("Holding PE:Position profit:"+pos.total()+" Current:"+currentProfit+" Reverse:"+reverseProfit+"--"+getPE_PRICE().getNewPrice().getValue()+"---C+R Profit:"+(currentProfit+reverseProfit));
				
			}
				
		}
		
		completedProcess=true;
		count++;
		//System.out.println("Position profit:"+pos.total()+" Current:"+currentProfit+" Reverse:"+reverseProfit);
		//System.out.println("Processing Block5 completed in :"+(System.currentTimeMillis()-t)+"--"+"PE:"+getPE_PRICE().getNewPrice().getValue()+"--"+"CE:"+getCE_PRICE().getNewPrice().getValue());
	}

	private void updateScriptPrices(){
		getPE_PRICE().setNewPrice(new ValueTime("",getCacheService().PRICE_LIST.get(getPE_PRICE().getId())));
		getCE_PRICE().setNewPrice(new ValueTime("",getCacheService().PRICE_LIST.get(getCE_PRICE().getId())));
		getNIFTY().setNewPrice(new ValueTime("",getCacheService().PRICE_LIST.get(getNIFTY().getId())));
	}
	
	private void addReversePosition(Position currentPosition){
		if(currentPosition.isPEPosition()){
			currentPosition.setReversePosition(createPosition(getCE_PRICE()));
		}else if (currentPosition.isCEPosition()){
			currentPosition.setReversePosition(createPosition(getPE_PRICE()));
		}
	}
	private Position createPosition(ScriptData scriptData){
		String positionName = null;
		positionName = KiteStockConverter.KITE_STOCK_LIST.get(scriptData.getId());
		return new Position(positionName, getPositionExpense(), scriptData.getNewPrice().getValue());
	}
	
	
	private double perCost(double profit,double cost){
		return (profit/cost)*100;
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
