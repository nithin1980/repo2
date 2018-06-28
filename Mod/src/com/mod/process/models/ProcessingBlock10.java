package com.mod.process.models;

import org.apache.commons.net.ntp.NTPUDPClient;

import com.mod.objects.GroupPosition;
import com.mod.objects.Position;

public class ProcessingBlock10 extends ProcessModelAbstract {

	private double prev_pe=Double.valueOf(modeConfig().getKeyValueConfigs().get("prev_pe"));
	private double prev_ce=Double.valueOf(modeConfig().getKeyValueConfigs().get("prev_ce"));
    private double prev_nifty=Double.valueOf(modeConfig().getKeyValueConfigs().get("prev_nifty"));
	
    private String openingState;
    
	@Override
	public String modelid() {
		// TODO Auto-generated method stub
		return "pmodel10";
	}
	
	@Override
	public void processNow() {
		// TODO Auto-generated method stub

		//getCE_PRICE().getNewPrice()
		//getCE_PRICE().getCurrentPrice()
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
//						else{
//							System.out.println("reverse entry pe.. at:"+getPE_PRICE().getNewPrice().getValue());
//							getCurrentPosition().setReversePosition(new Position("PE", 100.00, getPE_PRICE().getNewPrice().getValue()));
//						}
					}
					
//					if(entry(prev_ce, getCE_PRICE().getCurrentPrice().getValue(), getCE_PRICE().getNewPrice().getValue())){
//						
//						
//						if(getCurrentPosition()==null){
//							System.out.println("entry ce.. at:"+getCE_PRICE().getNewPrice().getValue());
//							setCurrentPosition(new Position("CE", 100.00, getCE_PRICE().getNewPrice().getValue()));
//						}else{
//							System.out.println("reverse entry ce.. at:"+getCE_PRICE().getNewPrice().getValue());
//							getCurrentPosition().setReversePosition(new Position("CE", 100.00, getCE_PRICE().getNewPrice().getValue()));
//						}
//					}
					
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
