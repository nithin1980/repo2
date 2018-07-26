package com.mod.process.models;

import java.util.Iterator;
import java.util.List;

import com.mod.objects.GroupPosition;
import com.mod.objects.Position;

public class ProcessingBlock15 extends ProcessModelAbstract {

	
    private String openingState;
    
    private int counter;
    
	public ProcessingBlock15(CacheService cacheService) {
		super();
		setCacheService(cacheService);
	}    
    
	@Override
	public String modelid() {
		// TODO Auto-generated method stub
		return "pmodel15";
	}
	
	@Override
	public void processNow() {
		// TODO Auto-generated method stub

		//getCE_PRICE().getNewPrice()
		//getCE_PRICE().getCurrentPrice()
		
		completedProcess=false;
		
		try {
			if(counter==0) {
				System.out.println("Staring Model 15 counter");
			}
			if(counter<30) {
				counter++;
			}else {
				GroupPosition groupPosition = DashBoard.positionMap.get(modelid());
				
				Iterator<Position> itr = groupPosition.getCePositions().iterator();
				
				double ce_id=0;
				double pe_id=0;
				Position pos = null;
				Position reverse = null;
				double profit = 0;
				while(itr.hasNext()) {
					pos = itr.next();
					reverse = pos.getReversePosition();
					pos.setSell(getCacheService().PRICE_LIST.get(pos.getId()));
					reverse.setSell(getCacheService().PRICE_LIST.get(reverse.getId()));
					
					System.out.println(pos.getId()+","+reverse.getId()+","+pos.getBuy()+","+reverse.getBuy()+","+profit(pos, reverse)+","+profitPercen(pos, reverse));
					
					
				}
				
				counter=0;
				
			}
		} finally {
			// TODO Auto-generated catch block
			completedProcess=true;
		}
		
		completedProcess=true;
	}
	/**
	 * Check the opening value and see if 
	 * open is really high, flat or really low.
	 * @param current
	 */
	private double profit(Position pos,Position reverse) {
		double profit = pos.getProfit()+reverse.getProfit();
		return profit;
	}
	
	private double profitPercen(Position pos,Position reverse) {
		double totalCost = pos.cost()+reverse.cost();
		double profit = profit(pos,reverse);
		
		return (profit/totalCost)*100;
	}
//	private void stateCheck(){
//		
//		double bnifty = CacheService.PRICE_LIST.get(260105.0);
//		if(openingState==null){
//			if(bnifty-prev_nifty>0.5 || bnifty-prev_nifty<0.5 ){
//				double openPercentage = percen(bnifty, prev_nifty);
//				if(openPercentage>0.2){
//					openingState= ObservedState.UP_HIGH;
//				}else if(openPercentage>0 && openPercentage<=0.2){
//					openingState= ObservedState.UP_FLAT;
//				}else if(openPercentage<-0.2){
//					openingState= ObservedState.DOWN_LOW;
//				}else if(openPercentage<0 && openPercentage>=-0.2){
//					openingState= ObservedState.DOWN_FLAT;
//				}
//			}
//		}
//		
//		/**
//		 * Remove this..only for testing...should not be null during normal ops.
//		 */
//		openingState=null;
//	}
	private boolean entry(double prevDay,double prevVal,double currentVal){
		if(prevVal==0){
			prevVal=currentVal;
		}
		
		double diff = currentVal-prevVal;
		if(diff !=0 && diff>.1){
			if(percen(currentVal, prevVal)<-25){
				System.out.println("b loop:"+currentVal);
				return true;
			}
			
		}else if(percen(currentVal, prevVal)<-38){
			System.out.println("b:"+currentVal);
			return true;
		}
		
		return false;
	}
	
	private static double percen(double current,double prev){
		return ((current-prev)/prev)*100;
	}	
	

}
