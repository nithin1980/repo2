package com.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.mod.support.ApplicationHelper;
import com.mod.support.Candle;
import com.mod.support.GeneralObject;

public class TestFour {
	
	@Before
	public void setup(){
		Candle can = new Candle();
		can.setHigh(9864.5);
		can.setLow(9863.9);
		pastCandles.add(can);
		previousCandle = can;
	}
	/**
	 * 1.current high
		2.current val
		3.current low
		4.current close
		
		c.h > p.high, p.close, p.low
		c.val> ideally p.high(but alteast higher once, atleast equal once) , ideally p.close but not necess, always p.low
		c.low> p.low, nice if p.high, nice if p.close if the previous was a positive close
		c.close> p.low, nice if p.high, majority p.close( okay if less than p.close if it closer to close),
	 */

	/**
	 * This is to store the past 4-5 candles and record if they passed the trend or failed.
	 * Will tell me if it is passing through the trend.
	 */
	public boolean alert1=false;
	public boolean alert2=false;
	public boolean alert3=false;
	
	@Test
	public void test2(){
		
	}
	
	//@Test
	public void test1(){
		double high=0;
		double low=0;
		double close=0;
		double currentVal=0;
		double previousLow=previousCandle.getLow();
		double previousHigh=previousCandle.getHigh();
		double previousClose=0;
		
		
		
		String overallState="open high -> went down- > now pull back to (resistance point)-> drawdown from day resistance";
		
		//List<GeneralObject> dt = ApplicationHelper.getNiftyData("04-12-2017");
		List<String> dt1 = ApplicationHelper.getNiftyDataCSV("data");
		
		int count=0;
		for(int i=1197;i<6000;i++){
			currentVal = Double.valueOf(dt1.get(i));
			
			bunch(currentVal);
			/***
			 * TRIGGER
			 * ---------------------------
			 */
			if(count==60){
				timesUp(currentVal);
				count=0;
				if(alert1){
					alert2=true;
				}
				alert1=false;
				previousLow=previousCandle.getLow();
				previousHigh=previousCandle.getHigh();
			}
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
						System.out.println("exit:"+i+" "+currentVal+" "+previousLow);
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
		
		
	}
	
	private long timer = 0;
	private static Candle currentCandle=new Candle();
	private List<Candle> pastCandles = new ArrayList<Candle>();
	
	private Candle previousCandle;
	
	private void bunch(double value){
		
		if(timer==0){
			timer = System.currentTimeMillis();
			currentCandle.reset();
		}
		
		currentCandle.populate(value);
		
		if((System.currentTimeMillis()-timer)>60000){
			timesUp(value);
		}
	}
	
	private void timesUp(double value){
		timer=0;
		currentCandle.setClose(value);
		pastCandles.add(currentCandle);
		previousCandle = new Candle(currentCandle);
		currentCandle = new Candle();
	}

}
