package com.kite;

public class Constants {
	
	public static int ReversePositionOppurtunityCount = 32;
	
	public static int MarginBreakup = 3;
	
	public static double BracketLowDifference = 12.0;
	
	public static final double LossLimit_1 = -1500;
	
	public static final double HighLimit_1 = 850;
	
	public static void main(String[] args) {
		
		ProcessingBlock processingBlock  = new ProcessingBlock();
		
		for(int i=5;i<11;i++){
			
			
			MarginBreakup=i;
			
			for(int j=10;j<21;j++){
				
				BracketLowDifference=j;
				
				for(int k=29;k<41;k++){
					
					
					ReversePositionOppurtunityCount=k;
					
					System.out.println("ReversePositionOppurtunityCount:"+ReversePositionOppurtunityCount+",MarginBreakup:"+MarginBreakup+",BracketLowDifference:"+BracketLowDifference);
					processingBlock.testPELogic();
					
//					try {
//						Thread.sleep(300);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					
					
				}
				
			}
		}
		
		ApplicationHelper.threadService.shutdown();
		
	}
}
