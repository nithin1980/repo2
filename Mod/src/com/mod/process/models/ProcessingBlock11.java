package com.mod.process.models;

import java.util.List;

import com.mod.objects.GroupPosition;
import com.mod.objects.Position;

public class ProcessingBlock11 extends ProcessModelAbstract {

	private double prev_pe=Double.valueOf(modeConfig().getKeyValueConfigs().get("prev_pe"));
	private double prev_ce=Double.valueOf(modeConfig().getKeyValueConfigs().get("prev_ce"));
    private double prev_nifty=Double.valueOf(modeConfig().getKeyValueConfigs().get("prev_nifty"));
	
    private String openingState;
    
	public ProcessingBlock11(CacheService cacheService) {
		super();
		setCacheService(cacheService);
	}    
    
	@Override
	public String modelid() {
		// TODO Auto-generated method stub
		return "pmodel11";
	}
	
	@Override
	public void processNow() {
		// TODO Auto-generated method stub

		//getCE_PRICE().getNewPrice()
		//getCE_PRICE().getCurrentPrice()
		
		completedProcess=false;
		
		GroupPosition groupPosition = DashBoard.positionMap.get(modelid());
		
		stateCheck();
		
		if(getCurrentPosition()==null && openingState!=null){
		    switch (openingState) {
				case ObservedState.UP_HIGH:
					//ce opens very high. may go down. objective catch at lower value.
					//pe low and may go down. 
					if(entry(prev_pe, getPE_PRICE().getCurrentPrice().getValue(), getPE_PRICE().getNewPrice().getValue())){
						if(getCurrentPosition()==null){
							System.out.println("entry pe.. at:"+getPE_PRICE().getNewPrice().getValue());
							groupPosition.getPePositions().add(new Position("PE", 100.00, getPE_PRICE().getNewPrice().getValue()));
						}
					}
					break;
				case ObservedState.DOWN_LOW:
					if(entry(prev_ce, getCE_PRICE().getCurrentPrice().getValue(), getCE_PRICE().getNewPrice().getValue())){
						if(getCurrentPosition()==null){
							System.out.println("entry ce.. at:"+getCE_PRICE().getNewPrice().getValue());
							groupPosition.getCePositions().add(new Position("CE", 100.00, getCE_PRICE().getNewPrice().getValue()));
						}
					}
					break;
				case ObservedState.UP_FLAT:
					groupPosition.getPePositions().add(new Position("PE", 100.00, getPE_PRICE().getNewPrice().getValue()));
					groupPosition.getCePositions().add(new Position("CE", 100.00, getCE_PRICE().getNewPrice().getValue()));
					break;
				case ObservedState.DOWN_FLAT:
					groupPosition.getPePositions().add(new Position("PE", 100.00, getPE_PRICE().getNewPrice().getValue()));
					groupPosition.getCePositions().add(new Position("CE", 100.00, getCE_PRICE().getNewPrice().getValue()));
					break;
	
				default:
					break;
			}	
//			if(entry(prevDay, prevVal, currentVal))
		}
		groupPosition.assignCESell(getCacheService().PRICE_LIST.get(getCE_PRICE().getId()));
		groupPosition.assignPESell(getCacheService().PRICE_LIST.get(getPE_PRICE().getId()));
		List<String> peInfo = groupPosition.peInfo();
		List<String> ceInfo = groupPosition.ceInfo();
		System.out.println("PE so far:"+peInfo.get(0)+","+peInfo.get(1)+","+peInfo.get(2));
		System.out.println("CE so far:"+ceInfo.get(0)+","+ceInfo.get(1)+","+ceInfo.get(2));
		System.out.println("Total:"+(Double.valueOf(peInfo.get(2))+Double.valueOf(ceInfo.get(2))));
		
		completedProcess=true;
	}
	/**
	 * Check the opening value and see if 
	 * open is really high, flat or really low.
	 * @param current
	 */
	private void stateCheck(){
		
		double bnifty = CacheService.PRICE_LIST.get(260105.0);
		if(openingState==null){
			if(bnifty-prev_nifty>0.5 || bnifty-prev_nifty<0.5 ){
				double openPercentage = percen(bnifty, prev_nifty);
				if(openPercentage>0.2){
					openingState= ObservedState.UP_HIGH;
				}else if(openPercentage>0 && openPercentage<=0.2){
					openingState= ObservedState.UP_FLAT;
				}else if(openPercentage<-0.2){
					openingState= ObservedState.DOWN_LOW;
				}else if(openPercentage<0 && openPercentage>=-0.2){
					openingState= ObservedState.DOWN_FLAT;
				}
			}
		}
		
		/**
		 * Remove this..only for testing...should not be null during normal ops.
		 */
		openingState=null;
	}
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
