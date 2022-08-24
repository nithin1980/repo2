package com.mod.process.models;

import java.util.List;

import org.junit.Test;

import com.mod.interfaces.KiteStockConverter;
import com.mod.objects.Action;
import com.mod.objects.GroupPosition;
import com.mod.objects.Position;
import com.mod.objects.ScriptData;
import com.mod.objects.ValueTime;
import com.mod.order.OrderInfo;
import com.mod.support.ApplicationHelper;

import gnu.trove.list.TDoubleList;

public class ProcessingBlock5 extends ProcessModelAbstract {


	private static boolean log = false;
	
	public ProcessingBlock5(CacheService cacheService) {
		// TODO Auto-generated constructor stub
		
		super();
		setCacheService(cacheService);
		DashBoard.positionMap.put(modelid(), new GroupPosition());

	}
	
	@Override
	public String modelid() {
		// TODO Auto-generated method stub
		return "pmodel5";
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
		double currentPer = 0;
		double reversePer = 0;
		boolean highChanged = false;
		boolean lowChanged = false;
		
		//Initial position
		if(pos.isEmpty() && getCurrentPosition()==null){
			double positionid = Double.valueOf(modeConfig().getKeyValueConfigs().get("initial_position"));
			String positionName = KiteStockConverter.KITE_STOCK_LIST.get(positionid);
			
			//order the new position
			/**
			 * @TODO
			 * Need a way to find out the ordered price
			 */
			getOrderInterface().orderKiteOption(new OrderInfo());
			currentPrice = getCacheService().PRICE_LIST.get(positionid);
			updateScriptPrices();
			setCurrentPosition(new Position(positionName, getPositionExpense(), currentPrice));
			addReversePosition(getCurrentPosition());
			
			if(getCurrentPosition().isPEPosition()){
				setCurrentAction(new Action(HOLDING_PE));
			}else if(getCurrentPosition().isCEPosition()){
				setCurrentAction(new Action(HOLDING_CE));
			}
		}else{
			updateScriptPrices();
			
			
				if(HOLDING_CE.equals(getCurrentAction().getAction())){
					currentProfit = getOptionProfit(getCurrentPosition(), getCE_PRICE().getNewPrice().getValue());
					reverseProfit = getOptionProfit(getCurrentPosition().getReversePosition(), getPE_PRICE().getNewPrice().getValue());
					highChanged = getCurrentPosition().setHighValue(getCE_PRICE().getNewPrice().getValue());
	//				if(highChanged){
	//					getCurrentPosition().setHighValRecord(i);
	//				}
					lowChanged = getCurrentPosition().setLowValue(getCE_PRICE().getNewPrice().getValue());
	//				if(lowChanged){
	//					getCurrentPosition().setLowValRecord(i);
	//				}
					
					highChanged = getCurrentPosition().getReversePosition().setHighValue(getPE_PRICE().getNewPrice().getValue());
	//				if(highChanged){
	//					getCurrentPosition().getReversePosition().setHighValRecord(i);
	//				}
					lowChanged = getCurrentPosition().getReversePosition().setLowValue(getPE_PRICE().getNewPrice().getValue());
	//				if(lowChanged){
	//					getCurrentPosition().getReversePosition().setLowValRecord(i);
	//				}
					
					getCurrentPosition().addToRecord(currentProfit);
					getCurrentPosition().getReversePosition().addToRecord(reverseProfit);
					getCurrentPosition().setBracketHigh(new ValueTime("",currentProfit));
					getCurrentPosition().setBracketLow(new ValueTime("",currentProfit));
					
					//if((currentProfit>250 && getCurrentPosition().isProfitDecreasedEnough(i, 10, 15, currentProfit)) || Chart.DOWNTREND.equals(getCurrentPosition().trend())){
						if(currentProfit<-800 || (currentProfit>450 && (perCost(getPE_PRICE().getNewPrice().getValue(), getCurrentPosition().getReversePosition().getLowValue())>25)) || reverseProfit>300 || reverseProfit<-800){							
//						System.out.println(getCurrentPosition().trend());
						getCurrentPosition().setSell(getCE_PRICE().getNewPrice().getValue());
	//					getCurrentPosition().setSellRecord(i);
						getCurrentPosition().setSellRecord(saleRecord);
						pos.getCePositions().add(getCurrentPosition());
						
						setCurrentPosition(createPosition(getPE_PRICE()));
	//					getCurrentPosition().setBuyRecord(i);
						
						getCurrentAction().setAction(HOLDING_PE);
						addReversePosition(getCurrentPosition());
						System.out.println(count+" Sold CE Positions:"+pos);
						saleRecord++;
					}
			}else
			if(HOLDING_PE.equals(getCurrentAction().getAction())){
					currentProfit = getOptionProfit(getCurrentPosition(), getPE_PRICE().getNewPrice().getValue());
					reverseProfit = getOptionProfit(getCurrentPosition().getReversePosition(), getCE_PRICE().getNewPrice().getValue());
					highChanged = getCurrentPosition().setHighValue(getPE_PRICE().getNewPrice().getValue());
	
					//Store time record
	//				if(highChanged){
	//					getCurrentPosition().setHighValRecord(i);
	//				}
					lowChanged = getCurrentPosition().setLowValue(getPE_PRICE().getNewPrice().getValue());
	//				if(lowChanged){
	//					getCurrentPosition().setLowValRecord(i);
	//				}
					
					highChanged = getCurrentPosition().getReversePosition().setHighValue(getCE_PRICE().getNewPrice().getValue());
	//				if(highChanged){
	//					getCurrentPosition().getReversePosition().setHighValRecord(i);
	//				}
					lowChanged = getCurrentPosition().getReversePosition().setLowValue(getCE_PRICE().getNewPrice().getValue());
	//				if(lowChanged){
	//					getCurrentPosition().getReversePosition().setLowValRecord(i);
	//				}
	
					
					getCurrentPosition().addToRecord(currentProfit);
					getCurrentPosition().getReversePosition().addToRecord(reverseProfit);
	
					getCurrentPosition().setBracketHigh(new ValueTime("",currentProfit));
					getCurrentPosition().setBracketLow(new ValueTime("",currentProfit));
					
	//				if((currentProfit>250 && newPosition.isProfitDecreasedEnough(i, 10, 15, currentProfit)) || Chart.DOWNTREND.equals(newPosition.trend())){
					if(currentProfit<-800 || ((currentProfit>450) && (perCost(getCE_PRICE().getNewPrice().getValue(), getCurrentPosition().getReversePosition().getLowValue())>25)) || reverseProfit>300 || reverseProfit<-800){							
						/**
						 * @TODO need to remove the data in the position cache...
						 * use the cacheservice
						 */
//						System.out.println(getCurrentPosition().trend());
						getCurrentPosition().setSell(getPE_PRICE().getNewPrice().getValue());
						getCurrentPosition().setSellRecord(saleRecord);
						
						pos.getPePositions().add(getCurrentPosition());
	//					getCurrentPosition().setSellRecord(i);
						setCurrentPosition(createPosition(getCE_PRICE()));
	//					getCurrentPosition().setBuyRecord(i);
						
						getCurrentAction().setAction(HOLDING_CE);
						//action.setAction(INITIAL_POSITION);
						addReversePosition(getCurrentPosition());
						System.out.println(count+" Sold PE Positions:"+pos);
						saleRecord++;

						
					}
			}
			
			
		}
		completedProcess=true;
		count++;
		System.out.println("Position profit:"+pos.total()+" Current:"+currentProfit+" Reverse:"+reverseProfit);
		System.out.println("Processing Block5 completed in :"+(System.currentTimeMillis()-t)+"--"+"PE:"+getPE_PRICE().getNewPrice().getValue()+"--"+"CE:"+getCE_PRICE().getNewPrice().getValue());
	}

	private void updateScriptPrices(){
		getPE_PRICE().setNewPrice(new ValueTime("",getCacheService().PRICE_LIST.get(getPE_PRICE().getId())));
		getCE_PRICE().setNewPrice(new ValueTime("",getCacheService().PRICE_LIST.get(getCE_PRICE().getId())));
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
	
	/**
	 *  Total time in seconds: 23400
	 */
	
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
		
//		getCacheService().clearNifty();;
//		getCacheService().dumpNifty();
		//ApplicationHelper.threadService.shutdown();
		
		DashBoard.positionMap.put("1", new GroupPosition());
		DashBoard.positionMap.put("2", new GroupPosition());
		
		GroupPosition pos = DashBoard.positionMap.get("1");
		Position newPosition = new Position("PE",70.0,68);
		newPosition.setBuyRecord(0);
		pos.getPePositions().add(newPosition);
		
		addReversePosition(newPosition, "CE",217);
		
		boolean sellDuringBuy = true;
		
		long t = System.currentTimeMillis();
		
		for(int i=0;i<size;i++){
			values = dataList.get(i);
//			getCacheService().addNifty(Double.valueOf(values.get(1)));
			if(i==8471){
				log=false;
			}
			
			if(i==8690){
				log=false;
			}
			
			
			peScript.setNewPrice(new ValueTime(values.get(0),Double.valueOf(values.get(2))));
			ceScript.setNewPrice(new ValueTime(values.get(0),Double.valueOf(values.get(3))));
			//action = process(newPosition,script,values , action, peScript, ceScript);
			values.add(action.getAction());

			double currentProfit = 0;
			double reverseProfit = 0;
			double currentPer = 0;
			double reversePer = 0;
			boolean highChanged = false;
			boolean lowChanged = false;
			if(i>6000 && i<7700){
				if(newPosition.getName().equals("PE")){
					currentProfit = getOptionProfit(newPosition, Double.valueOf(values.get(2)));
					reverseProfit = getOptionProfit(newPosition.getReversePosition(), Double.valueOf(values.get(3)));
					currentPer = perCost(currentProfit, newPosition.cost());
					reversePer = perCost(reverseProfit, newPosition.getReversePosition().cost());
					
					System.out.println("current :"+currentProfit+"("+currentPer +")"+"  reverse :"+reverseProfit+"("+reversePer +")"+" diff:"+(currentProfit+reverseProfit));
				}else{
					currentProfit = getOptionProfit(newPosition, Double.valueOf(values.get(3)));
					reverseProfit = getOptionProfit(newPosition.getReversePosition(), Double.valueOf(values.get(2)));
					
					currentPer = perCost(currentProfit, newPosition.cost());
					reversePer = perCost(reverseProfit, newPosition.getReversePosition().cost());
					
					System.out.println("current :"+currentProfit+"("+currentPer +")"+"  reverse :"+reverseProfit+"("+reversePer +")"+" diff:"+(currentProfit+reverseProfit));
				}
			}
			
			
				if(HOLDING_CE.equals(action.getAction())){
						currentProfit = getOptionProfit(newPosition, ceScript.getNewPrice().getValue());
						reverseProfit = getOptionProfit(newPosition.getReversePosition(), peScript.getNewPrice().getValue());
						highChanged = newPosition.setHighValue(ceScript.getNewPrice().getValue());
						if(highChanged){
							newPosition.setHighValRecord(i);
						}
						lowChanged = newPosition.setLowValue(ceScript.getNewPrice().getValue());
						if(lowChanged){
							newPosition.setLowValRecord(i);
						}
						
						highChanged = newPosition.getReversePosition().setHighValue(peScript.getNewPrice().getValue());
						if(highChanged){
							newPosition.getReversePosition().setHighValRecord(i);
						}
						lowChanged = newPosition.getReversePosition().setLowValue(peScript.getNewPrice().getValue());
						if(lowChanged){
							newPosition.getReversePosition().setLowValRecord(i);
						}
						
						newPosition.addToRecord(currentProfit);
						newPosition.getReversePosition().addToRecord(reverseProfit);
						//System.out.println("current profit:"+currentProfit+"..."+i);
//						if(currentProfit<Constants.LossLimit_1){
//							newPosition.setSell(Double.valueOf(values.get(3)));
//							newPosition.setSellRecord(i);
//							
//							
//						}
						newPosition.setBracketHigh(new ValueTime("",currentProfit));
						newPosition.setBracketLow(new ValueTime("",currentProfit));
						
						//if((currentProfit>250 && newPosition.isProfitDecreasedEnough(i, 10, 15, currentProfit)) || Chart.DOWNTREND.equals(newPosition.trend())){
							if((currentProfit>250 && (perCost(peScript.getNewPrice().getValue(), newPosition.getReversePosition().getLowValue())>25)) || reverseProfit>300){							
							System.out.println(newPosition.trend());
							newPosition.setSell(Double.valueOf(values.get(3)));
							newPosition.setSellRecord(i);
							
							newPosition = new Position("PE",70.0,Double.valueOf(values.get(2)));
							newPosition.setBuyRecord(i);
							pos.getPePositions().add(newPosition);
							action.setAction(HOLDING_PE);
							addReversePosition(newPosition, "CE", Double.valueOf(values.get(3)));
							
						}
						
						
//						if(currentProfit>500){
//							
//							newPosition.setSell(Double.valueOf(values.get(3)));
//							newPosition.setSellRecord(i);
//							newPosition = new Position("PE",70.0,Double.valueOf(values.get(2)));
//							newPosition.setBuyRecord(i);
//							pos.getPePositions().add(newPosition);
//							action.setAction(HOLDING_PE);
//							addReversePosition(newPosition, "CE", Double.valueOf(values.get(3)));
//							
//						}
						
//						if(newPosition.highProfit()>250 && newPosition.percentageFromHighProfit(currentProfit)>15){
//							newPosition.setSell(Double.valueOf(values.get(3)));
//							newPosition.setSellRecord(i);
//						}
				}
				
				if(HOLDING_PE.equals(action.getAction())){
						currentProfit = getOptionProfit(newPosition, peScript.getNewPrice().getValue());
						reverseProfit = getOptionProfit(newPosition.getReversePosition(), ceScript.getNewPrice().getValue());
						highChanged = newPosition.setHighValue(peScript.getNewPrice().getValue());
						if(highChanged){
							newPosition.setHighValRecord(i);
						}
						lowChanged = newPosition.setLowValue(peScript.getNewPrice().getValue());
						if(lowChanged){
							newPosition.setLowValRecord(i);
						}
						
						highChanged = newPosition.getReversePosition().setHighValue(ceScript.getNewPrice().getValue());
						if(highChanged){
							newPosition.getReversePosition().setHighValRecord(i);
						}
						lowChanged = newPosition.getReversePosition().setLowValue(ceScript.getNewPrice().getValue());
						if(lowChanged){
							newPosition.getReversePosition().setLowValRecord(i);
						}

						
						newPosition.addToRecord(currentProfit);
						newPosition.getReversePosition().addToRecord(reverseProfit);

						newPosition.setBracketHigh(new ValueTime("",currentProfit));
						newPosition.setBracketLow(new ValueTime("",currentProfit));
						
//						if((currentProfit>250 && newPosition.isProfitDecreasedEnough(i, 10, 15, currentProfit)) || Chart.DOWNTREND.equals(newPosition.trend())){
							if(((currentProfit>250) && (perCost(ceScript.getNewPrice().getValue(), newPosition.getReversePosition().getLowValue())>25)) || reverseProfit>300){							
							
							System.out.println(newPosition.trend());
							newPosition.setSell(Double.valueOf(values.get(2)));
							newPosition.setSellRecord(i);
							newPosition = new Position("CE",70.0,Double.valueOf(values.get(3)));
							newPosition.setBuyRecord(i);
							pos.getCePositions().add(newPosition);
							action.setAction(HOLDING_CE);
							//action.setAction(INITIAL_POSITION);
							addReversePosition(newPosition, "PE", Double.valueOf(values.get(2)));
						}

						
//						if(currentProfit<Constants.LossLimit_1){
//							newPosition.setSell(Double.valueOf(values.get(2)));
//							newPosition.setSellRecord(i);
//						}
//						if(currentProfit>500){
//							System.out.println("selling");
//							newPosition.setSell(Double.valueOf(values.get(2)));
//							newPosition.setSellRecord(i);
//							newPosition = new Position("CE",70.0,Double.valueOf(values.get(3)));
//							newPosition.setBuyRecord(i);
//							pos.getCePositions().add(newPosition);
//							action.setAction(HOLDING_CE);
//							//action.setAction(INITIAL_POSITION);
//							addReversePosition(newPosition, "PE", Double.valueOf(values.get(2)));
//							
//						}
						
//						if(newPosition.highProfit()>250 && newPosition.percentageFromHighProfit(currentProfit)>15){
//							newPosition.setSell(Double.valueOf(values.get(2)));
//							newPosition.setSellRecord(i);
//						}
						
						
					
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
//		int niftyRecordSize = getCacheService().niftyCount();
		
		
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
		
//		if(niftyRecordSize>10){
//			queryList = getCacheService().getItemsFromNiftyCache(10);
//			trend = Chart.calculateTrend(queryList);
//			
//			lowerThanHighest = Chart.isLowerThanHighest(newPrice, queryList.subList(5, 10));
//			higherThanLowest = Chart.isHigherThanLowest(newPrice, queryList.subList(5, 10));
//		}else if(niftyRecordSize>4){
//			queryList = getCacheService().getItemsFromNiftyCache(4);
//			trend = Chart.calculateTrend(queryList);
//			
//			lowerThanHighest = Chart.isLowerThanHighest(newPrice, queryList);
//			higherThanLowest = Chart.isHigherThanLowest(newPrice, queryList);
//		}
		
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
