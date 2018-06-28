package com.mod.process.models;

import java.util.List;

import com.mod.support.ApplicationHelper;

public class ProcessingBlock9 extends ProcessModelAbstract2 {

	@Override
	public String modelid() {
		// TODO Auto-generated method stub
		return "pmodel9";
	}
	
	public boolean alert1=false;
	public boolean alert2=false;
	public boolean alert3=false;	
	
	private double drawDown=10;
	private int expectedDrawDownBreach=4;
	private int drawDownBreachCount;
	private double entryPoint;
	
	
	@Override
	public void processNow() {
		// TODO Auto-generated method stub
		
		double high=0;
		double low=0;
		double close=0;
		double currentVal=getCacheService().PRICE_LIST.get(256265.0);
		double previousLow=CacheService.previousCandle.getLow();
		double previousHigh=CacheService.previousCandle.getHigh();
		double previousClose=0;
		
		/**
		 * This needs to change. Entry point to what the real value is *************Important*****************
		 */
		if(entryPoint==0){
			entryPoint=currentVal;
		}
		
		
		String overallState="open high -> went down- > now pull back to (resistance point)-> drawdown from day resistance";
		
		//List<GeneralObject> dt = ApplicationHelper.getNiftyData("04-12-2017");
		//List<String> dt1 = ApplicationHelper.getNiftyDataCSV("data");
		
		int count=0;
		
//			ApplicationHelper.bunchValues(currentVal);
//			/***
//			 * TRIGGER
//			 * ---------------------------
//			 */
//			if(count==60){
//				ApplicationHelper.timesUp(currentVal);
//				count=0;
//				if(alert1){
//					alert2=true;
//				}
//				alert1=false;
//				previousLow=CacheService.previousCandle.getLow();
//				previousHigh=CacheService.previousCandle.getHigh();
//			}
		
		
			/**
			 * --------------------------
			 */
			
			/**
			 * Different scenarios
			 * hitting resistance/support
			 * sidewards
			 * hovering...can go any direction
			 */
			
			//difference between the current low and previous low.
			//check to see if it really close.
			double diffLow = low-previousLow;
			
			//expected value after a time lag.
			double expectedValue=0;
			
			//the current low is less than previous low. Or current value is less than previous
			//low
			//current high is closer to previous low.
			//Important: Need an ability to wait....
			
			//this can trigger many times within a 60 sec window.
			//so need a way to let it know it has completed the minute cycle.
			//drawback, it might reduce the value....

			/*****
			 * ALL THE CONDITIONS MAY TRIGGER MULTIPLE TIMES WITHIN 60 SECONDS.
			 * 
			 * NEED TO HAVE ABILITY TO IGNORE LOTS OF THEM IF THEY DONT INDICATE TREND CHANGE
			 */
			/**
			 * The logic needs to ignore most of the price value
			 * and concentrate only if the current price drops from 
			 * the current low price. 
			 * 
			 * This might indicate a trend change from up to down. 
			 */
			if(close> 0 && low<previousLow){
				//***** This may not be required, as it is handled by current. Moreover this can happen only 60 sec closure
				System.out.println("broken.."+low+"---"+previousLow);
				//..breaking
				
//				if("first break...."){
//					//see if this is the first break. set it to alert.
//				}
//				if("high breaking as well?"){
//					
//				}
//				if(){
//					//hitting resistance point?
//				}
//				if(){
//					//large draw down, i.e. the current value has dropped suddenly.
//				}
				
			}
			//This can trigger many times within 60 seconds.
			else if( currentVal<previousLow){
				
				//if dropped for the first time, alert it
				if(!alert1){
					alert1=true;
					System.out.println("alerting.."+" "+currentVal+" "+previousLow);
					
					if(alert2){
						System.out.println("exit: "+currentVal+" "+previousLow);
						alert2=false;
					}
				}
				//once alerted ignore the rest
				
				
				// we may want to see if it breaks multiple times.....
				//if it is quite a difference, then we need there is a divergent.
			}
			//close should ideally be greater than previous high to indicate a up-trend.
			else if(close>0 && close < previousHigh){
				
			} 
			//this might indicate a trend change, as the current is lower than previous close
			else if(currentVal< previousClose){
				
			}//on close if the current high is less or equal than previous high, indicates a flatten trend and resistance.
			else if(close>0 && high <= previousHigh){
				
			}//trend change.
			else if(close>0 && close<previousLow){
				
			}
			
			if(close>0 && (alert1)){
				//there has been a breach and now need to see if the close is nearer to high or low 
				if(close<previousLow && ((close-low)<(high-close))){
					alert2=true;
					
					//once alert 2, then it needs to go in to confirmatio mode. Check the next candle is breaks the rule as well. 
				}
			}
			
			count++;	
		//for loop ends	
		
		
		

	}
	
	private void placeAlternatePath(double value){
		
	}



}
