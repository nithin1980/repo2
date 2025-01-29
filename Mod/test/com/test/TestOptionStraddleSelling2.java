package com.test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.mod.support.ApplicationHelper;
import com.mod.support.Candle;

public class TestOptionStraddleSelling2 {
	
	static List<KiteBNFSellingCandle> bnfdataList = new ArrayList<KiteBNFSellingCandle>();
	
	static final double range1=75.0;
	
	double pricePoint1 = 400;
	double pricePoint2 = 400;
	double pricePoint3 = 400;
	
	String namePricePoint1="PricePoint1";
	String namePricePoint2="PricePoint2";
	String namePricePoint3="PricePoint3";
	
	/**
	 * 430 points from first close gives a fab 27% failure
	 */
	
	@Test
	public void test1() {
		
		collectData();
		
		for(int k=0;k<25;k++) {
			
			pricePoint1=100+(5*k);
			pricePoint2=100+(5*k);
			pricePoint3=300+(5*k);
			System.out.println("Price Point 1:"+pricePoint1+","+pricePoint2+","+pricePoint3);
		
		double previousClose=0;
		double todayOpen=0;
		double todayFirstClose=0;
		
		KitePositionMetaData prevCloseMeta = new KitePositionMetaData(KitePositionMetaData.prevClose);
		KitePositionMetaData todayOpenMeta = new KitePositionMetaData(KitePositionMetaData.todayOpen);
		KitePositionMetaData todayFirstCloseMeta = new KitePositionMetaData(KitePositionMetaData.todayFirstClose);

		
		for(int i=1;i<bnfdataList.size();i++) {
			
			
			
			int size = bnfdataList.get(i-1).getCandles().size();
			previousClose =  bnfdataList.get(i-1).getCandles().get(size-1).getClose();
			
//			pricePoint1=previousClose*(0.0015+(0.0005*k));
//			pricePoint2=previousClose*0.0080;
			
			todayOpen = bnfdataList.get(i).getCandles().get(0).getOpen();
			todayFirstClose = bnfdataList.get(i).getCandles().get(0).getClose();
			
			Iterator<Candle> workingCandles = bnfdataList.get(i).getCandles().iterator();
			
			prevCloseMeta.clear();
			todayOpenMeta.clear();
			todayFirstCloseMeta.clear();
			String[] resultLog=new String[100];
			
			//ignore the first one, as it scchews the results..
			workingCandles.next();
			
			while(workingCandles.hasNext()) {
				Candle cd =  workingCandles.next();
				previousCloseProcess(cd, previousClose,prevCloseMeta,resultLog );
				todayOpenProcess(cd, todayOpen, todayOpenMeta, resultLog);
				todayFirstCloseProcess(cd, todayFirstClose, todayFirstCloseMeta, resultLog);
			}
			
			if(prevCloseMeta.checkPoistionTaken(namePricePoint1,pricePoint1)) {
				if(prevCloseMeta.checkCESLHit(namePricePoint1,pricePoint1) && prevCloseMeta.checkPESLHit(namePricePoint1,pricePoint1)) {
					resultLog[15]="true";
					prevCloseMeta.getPricePtcounter1().incrementOverall();
					prevCloseMeta.getPricePtcounter1().incrementFailure();
				}else {
					resultLog[15]="false";
					prevCloseMeta.getPricePtcounter1().incrementOverall();
				}
			}else {
				resultLog[15]="NO_POSITION";
			}
			
			if(prevCloseMeta.checkPoistionTaken(namePricePoint2,pricePoint2)) {
				if(prevCloseMeta.checkCESLHit(namePricePoint2,pricePoint2) && prevCloseMeta.checkPESLHit(namePricePoint2,pricePoint2)) {
					resultLog[16]="true";
					prevCloseMeta.getPricePtcounter2().incrementOverall();
					prevCloseMeta.getPricePtcounter2().incrementFailure();
				}else {
					resultLog[16]="false";
					prevCloseMeta.getPricePtcounter2().incrementOverall();
				}
			}else {
				resultLog[16]="NO_POSITION";
			}
			
			if(prevCloseMeta.checkPoistionTaken(namePricePoint3,pricePoint3)) {
				if(prevCloseMeta.checkCESLHit(namePricePoint3,pricePoint3) && prevCloseMeta.checkPESLHit(namePricePoint3,pricePoint3)) {
					resultLog[17]="true";
					prevCloseMeta.getPricePtcounter3().incrementOverall();
					prevCloseMeta.getPricePtcounter3().incrementFailure();

				}else {
					resultLog[17]="false";
					prevCloseMeta.getPricePtcounter3().incrementOverall();
				}
			}else {
				resultLog[17]="NO_POSITION";
			}
			

			
			if(todayOpenMeta.checkPoistionTaken(namePricePoint1,pricePoint1)) {
				if(todayOpenMeta.checkCESLHit(namePricePoint1,pricePoint1) && todayOpenMeta.checkPESLHit(namePricePoint1,pricePoint1)) {
					resultLog[33]="true";
					todayOpenMeta.getPricePtcounter1().incrementOverall();
					todayOpenMeta.getPricePtcounter1().incrementFailure();

				}else {
					resultLog[33]="false";
					todayOpenMeta.getPricePtcounter1().incrementOverall();
				}
			}else {
				resultLog[33]="NO_POSITION";
			}

			if(todayOpenMeta.checkPoistionTaken(namePricePoint2,pricePoint2)) {
				if(todayOpenMeta.checkCESLHit(namePricePoint2,pricePoint2) && todayOpenMeta.checkPESLHit(namePricePoint2,pricePoint2)) {
					resultLog[34]="true";
					todayOpenMeta.getPricePtcounter2().incrementOverall();
					todayOpenMeta.getPricePtcounter2().incrementFailure();

				}else {
					resultLog[34]="false";
					todayOpenMeta.getPricePtcounter2().incrementOverall();
				}
			}else {
				resultLog[34]="NO_POSITION";
			}
			
			if(todayOpenMeta.checkPoistionTaken(namePricePoint3,pricePoint3)) {
				if(todayOpenMeta.checkCESLHit(namePricePoint3,pricePoint3) && todayOpenMeta.checkPESLHit(namePricePoint3,pricePoint3)) {
					resultLog[35]="true";
					todayOpenMeta.getPricePtcounter3().incrementOverall();
					todayOpenMeta.getPricePtcounter3().incrementFailure();

				}else {
					resultLog[35]="false";
					todayOpenMeta.getPricePtcounter3().incrementOverall();

				}
			}else {
				resultLog[35]="NO_POSITION";
			}
			

			if(todayFirstCloseMeta.checkPoistionTaken(namePricePoint1,pricePoint1)) {
				if(todayFirstCloseMeta.checkCESLHit(namePricePoint1,pricePoint1) && todayFirstCloseMeta.checkPESLHit(namePricePoint1,pricePoint1)) {
					resultLog[51]="true";
					todayFirstCloseMeta.getPricePtcounter1().incrementOverall();
					todayFirstCloseMeta.getPricePtcounter1().incrementFailure();

				}else {
					resultLog[51]="false";
					todayFirstCloseMeta.getPricePtcounter1().incrementOverall();
				}
			}else {
				resultLog[51]="NO_POSITION";
			}

			if(todayFirstCloseMeta.checkPoistionTaken(namePricePoint2,pricePoint2)) {
				if(todayFirstCloseMeta.checkCESLHit(namePricePoint2,pricePoint2) && todayFirstCloseMeta.checkPESLHit(namePricePoint2,pricePoint2)) {
					resultLog[52]="true";
					todayFirstCloseMeta.getPricePtcounter2().incrementOverall();
					todayFirstCloseMeta.getPricePtcounter2().incrementFailure();

				}else {
					resultLog[52]="false";
					todayFirstCloseMeta.getPricePtcounter2().incrementOverall();
				}
			}else {
				resultLog[52]="NO_POSITION";
			}
			
			if(todayFirstCloseMeta.checkPoistionTaken(namePricePoint3,pricePoint3)) {
				if(todayFirstCloseMeta.checkCESLHit(namePricePoint3,pricePoint3) && todayFirstCloseMeta.checkPESLHit(namePricePoint3,pricePoint3)) {
					resultLog[53]="true";
					todayFirstCloseMeta.getPricePtcounter3().incrementOverall();
					todayFirstCloseMeta.getPricePtcounter3().incrementFailure();

				}else {
					resultLog[53]="false";
					todayFirstCloseMeta.getPricePtcounter3().incrementOverall();
				}
			}else {
				resultLog[53]="NO_POSITION";
			}
			


			StringBuilder resultLogString= new StringBuilder();
			
			for(int j=0;j<54;j++) {
				if(resultLog[j]==null || resultLog[j]=="") {
					resultLogString.append(",");
				}else {
					resultLogString.append(resultLog[j]+",");
				}
			}
			
			//System.out.println(resultLogString.toString());

			resultLog=new String[100];
			
		}
		
		
		System.out.println("Pre Close:"+prevCloseMeta.getPricePtcounter1().failurerate()+","
				+prevCloseMeta.getPricePtcounter2().failurerate()+","
				+prevCloseMeta.getPricePtcounter3().failurerate()+",");
		
		System.out.println("Today open:"+todayOpenMeta.getPricePtcounter1().failurerate()+","
				+todayOpenMeta.getPricePtcounter2().failurerate()+","
				+todayOpenMeta.getPricePtcounter3().failurerate()+",");

		System.out.println("Today First:"+todayFirstCloseMeta.getPricePtcounter1().failurerate()+","
				+todayFirstCloseMeta.getPricePtcounter2().failurerate()+","
				+todayFirstCloseMeta.getPricePtcounter3().failurerate()+",");
		
		System.out.println("----------------------------");
		
		
		
		}

	}
	
	private void previousCloseProcess(Candle cd,double previousClose,KitePositionMetaData prevCloseMeta,String[] resultLog) {
		
		
		
		if(prevCloseMeta.checkPoistionTaken(namePricePoint1,pricePoint1)) {
			
			if(cd.getHigh()>=(range1+prevCloseMeta.getPositionValue(namePricePoint1,pricePoint1)) 
					&& !prevCloseMeta.checkCESLHit(namePricePoint1,pricePoint1)) {
				resultLog[3]=cd.getTime();
				resultLog[4]=String.valueOf(cd.getHigh());
				prevCloseMeta.addCESLHit(namePricePoint1,pricePoint1);;
			}
			
			if(cd.getLow()<=(prevCloseMeta.getPositionValue(namePricePoint1,pricePoint1)-range1) 
					&& !prevCloseMeta.checkPESLHit(namePricePoint1,pricePoint1)) {
				resultLog[5]=cd.getTime();
				resultLog[6]=String.valueOf(cd.getLow());
				
				prevCloseMeta.addPESLHit(namePricePoint1,pricePoint1);;
			}
		}
		
		
		if(prevCloseMeta.checkPoistionTaken(namePricePoint2,pricePoint2)) {
			
			if(cd.getHigh()>=(range1+prevCloseMeta.getPositionValue(namePricePoint2,pricePoint2)) 
					&& !prevCloseMeta.checkCESLHit(namePricePoint2,pricePoint2)) {
				
				prevCloseMeta.addCESLHit(namePricePoint2,pricePoint2);;
				resultLog[7]=cd.getTime();
				resultLog[8]=String.valueOf(cd.getHigh());
			}
			
			if(cd.getLow()<=(prevCloseMeta.getPositionValue(namePricePoint2,pricePoint2)-range1) 
					&& !prevCloseMeta.checkPESLHit(namePricePoint2,pricePoint2)) {
				prevCloseMeta.addPESLHit(namePricePoint2,pricePoint2);
				resultLog[9]=cd.getTime();
				resultLog[10]=String.valueOf(cd.getLow());
				
			}
		}

		if(prevCloseMeta.checkPoistionTaken(namePricePoint3,pricePoint3)) {
			
			if(cd.getHigh()>=(range1+prevCloseMeta.getPositionValue(namePricePoint3,pricePoint3)) 
					&& !prevCloseMeta.checkCESLHit(namePricePoint3,pricePoint3)) {
				
				prevCloseMeta.addCESLHit(namePricePoint3,pricePoint3);;
				resultLog[11]=cd.getTime();
				resultLog[12]=String.valueOf(cd.getHigh());

			}
			
			if(cd.getLow()<=(prevCloseMeta.getPositionValue(namePricePoint3,pricePoint3)-range1) 
					&& !prevCloseMeta.checkPESLHit(namePricePoint3,pricePoint3)) {
				prevCloseMeta.addPESLHit(namePricePoint3,pricePoint3);;
				resultLog[13]=cd.getTime();
				resultLog[14]=String.valueOf(cd.getLow());

			}
		}
		
		/**
		 * Previous close
		 */
		if((cd.getHigh()>(previousClose+pricePoint1) || 
				cd.getLow()<(previousClose-pricePoint1)) && !prevCloseMeta.checkPoistionTaken(namePricePoint1,pricePoint1) ) {
			
			
			if(cd.getHigh()>(previousClose+pricePoint1) && cd.getHigh()<(previousClose+(pricePoint1+30)) ) {
				prevCloseMeta.addPositionTaken(namePricePoint1,pricePoint1);
				
				resultLog[0]=cd.getTime();
				prevCloseMeta.addPositionValue(namePricePoint1,pricePoint1, cd.getClose());
			}
			
			if(cd.getLow()<(previousClose-pricePoint1) && cd.getLow()>(previousClose-(pricePoint1+30))) {
				prevCloseMeta.addPositionTaken(namePricePoint1,pricePoint1);;
				
				resultLog[0]=cd.getTime();
				prevCloseMeta.addPositionValue(namePricePoint1,pricePoint1, cd.getClose());
			}
			
			
			
		}
		
		if((cd.getHigh()>(previousClose+pricePoint2) || 
				cd.getLow()<(previousClose-pricePoint2)) && !prevCloseMeta.checkPoistionTaken(namePricePoint2,pricePoint2) ) {
			
			prevCloseMeta.addPositionTaken(namePricePoint2,pricePoint2);;
			resultLog[1]=cd.getTime();
			prevCloseMeta.addPositionValue(namePricePoint2,pricePoint2, cd.getClose());
			
		}
		
		if((cd.getHigh()>(previousClose+pricePoint3) || 
				cd.getLow()<(previousClose-pricePoint3)) && !prevCloseMeta.checkPoistionTaken(namePricePoint3,pricePoint3) ) {
			
			prevCloseMeta.addPositionTaken(namePricePoint3,pricePoint3);;
			resultLog[2]=cd.getTime();
			
			prevCloseMeta.addPositionValue(namePricePoint3,pricePoint3, cd.getClose());
		}
		
	}

	
	
	private void todayOpenProcess(Candle cd,double todayOpen,KitePositionMetaData todayOpenMeta,String[] resultLog) {
		
		
		if(todayOpenMeta.checkPoistionTaken(namePricePoint1,pricePoint1)) {
			
			if(cd.getHigh()>=(range1+todayOpenMeta.getPositionValue(namePricePoint1,pricePoint1)) 
					&& !todayOpenMeta.checkCESLHit(namePricePoint1,pricePoint1)) {
				resultLog[21]=cd.getTime();
				resultLog[22]=String.valueOf(cd.getHigh());
				todayOpenMeta.addCESLHit(namePricePoint1,pricePoint1);
			}
			
			if(cd.getLow()<=(todayOpenMeta.getPositionValue(namePricePoint1,pricePoint1)-range1) 
					&& !todayOpenMeta.checkPESLHit(namePricePoint1,pricePoint1)) {
				resultLog[23]=cd.getTime();
				resultLog[24]=String.valueOf(cd.getLow());
				
				todayOpenMeta.addPESLHit(namePricePoint1,pricePoint1);
			}
		}
		
		
		if(todayOpenMeta.checkPoistionTaken(namePricePoint2,pricePoint2)) {
			
			if(cd.getHigh()>=(range1+todayOpenMeta.getPositionValue(namePricePoint2,pricePoint2)) 
					&& !todayOpenMeta.checkCESLHit(namePricePoint2,pricePoint2)) {
				
				todayOpenMeta.addCESLHit(namePricePoint2,pricePoint2);
				resultLog[25]=cd.getTime();
				resultLog[26]=String.valueOf(cd.getHigh());
				
			}
			
			if(cd.getLow()<=(todayOpenMeta.getPositionValue(namePricePoint2,pricePoint2)-range1) 
					&& !todayOpenMeta.checkPESLHit(namePricePoint2,pricePoint2)) {
				todayOpenMeta.addPESLHit(namePricePoint2,pricePoint2);
				resultLog[27]=cd.getTime();
				resultLog[28]=String.valueOf(cd.getLow());

			}
		}

		if(todayOpenMeta.checkPoistionTaken(namePricePoint3,pricePoint3)) {
			
			if(cd.getHigh()>=(range1+todayOpenMeta.getPositionValue(namePricePoint3,pricePoint3)) 
					&& !todayOpenMeta.checkCESLHit(namePricePoint3,pricePoint3)) {
				
				todayOpenMeta.addCESLHit(namePricePoint3,pricePoint3);
				resultLog[29]=cd.getTime();
				resultLog[30]=String.valueOf(cd.getHigh());

			}
			
			if(cd.getLow()<=(todayOpenMeta.getPositionValue(namePricePoint3,pricePoint3)-range1) 
					&& !todayOpenMeta.checkPESLHit(namePricePoint3,pricePoint3)) {
				todayOpenMeta.addPESLHit(namePricePoint3,pricePoint3);
				resultLog[31]=cd.getTime();
				resultLog[32]=String.valueOf(cd.getLow());

			}
		}
		
		/**
		 * Previous close
		 */
		if((cd.getHigh()>(todayOpen+pricePoint1) || 
				cd.getLow()<(todayOpen-pricePoint1)) && !todayOpenMeta.checkPoistionTaken(namePricePoint1,pricePoint1) ) {

			if(cd.getHigh()>(todayOpen+pricePoint1) && cd.getHigh()<(todayOpen+(pricePoint1+30)) ) {
				todayOpenMeta.addPositionTaken(namePricePoint1,pricePoint1);
				
				resultLog[18]=cd.getTime();
				todayOpenMeta.addPositionValue(namePricePoint1,pricePoint1, cd.getClose());
			}
			
			if(cd.getLow()<(todayOpen-pricePoint1) && cd.getLow()>(todayOpen-(pricePoint1+30))) {
				todayOpenMeta.addPositionTaken(namePricePoint1,pricePoint1);
				
				resultLog[18]=cd.getTime();
				todayOpenMeta.addPositionValue(namePricePoint1,pricePoint1, cd.getClose());
				
			}
			

			
		}
		
		if((cd.getHigh()>(todayOpen+pricePoint2) || 
				cd.getLow()<(todayOpen-pricePoint2)) && !todayOpenMeta.checkPoistionTaken(namePricePoint2,pricePoint2) ) {
			
			todayOpenMeta.addPositionTaken(namePricePoint2,pricePoint2);
			resultLog[19]=cd.getTime();
			todayOpenMeta.addPositionValue(namePricePoint2,pricePoint2, cd.getClose());
		}
		
		if((cd.getHigh()>(todayOpen+pricePoint3) || 
				cd.getLow()<(todayOpen-pricePoint3)) && !todayOpenMeta.checkPoistionTaken(namePricePoint3,pricePoint3) ) {
			
			todayOpenMeta.addPositionTaken(namePricePoint3,pricePoint3);
			resultLog[20]=cd.getTime();
			
			todayOpenMeta.addPositionValue(namePricePoint3,pricePoint3, cd.getClose());
		}
		
	}
	
	
	private void todayFirstCloseProcess(Candle cd,double firstClose,KitePositionMetaData todayFirstCloseMeta,String[] resultLog) {
		
		
		if(todayFirstCloseMeta.checkPoistionTaken(namePricePoint1,pricePoint1)) {
			
			if(cd.getHigh()>=(range1+todayFirstCloseMeta.getPositionValue(namePricePoint1,pricePoint1)) 
					&& !todayFirstCloseMeta.checkCESLHit(namePricePoint1,pricePoint1)) {
				resultLog[39]=cd.getTime();
				resultLog[40]=String.valueOf(cd.getHigh());
				todayFirstCloseMeta.addCESLHit(namePricePoint1,pricePoint1);
			}
			
			if(cd.getLow()<=(todayFirstCloseMeta.getPositionValue(namePricePoint1,pricePoint1)-range1) 
					&& !todayFirstCloseMeta.checkPESLHit(namePricePoint1,pricePoint1)) {
				resultLog[41]=cd.getTime();
				resultLog[42]=String.valueOf(cd.getLow());
				
				todayFirstCloseMeta.addPESLHit(namePricePoint1,pricePoint1);
			}
		}
		
		
		if(todayFirstCloseMeta.checkPoistionTaken(namePricePoint2,pricePoint2)) {
			
			if(cd.getHigh()>=(range1+todayFirstCloseMeta.getPositionValue(namePricePoint2,pricePoint2)) 
					&& !todayFirstCloseMeta.checkCESLHit(namePricePoint2,pricePoint2)) {
				
				todayFirstCloseMeta.addCESLHit(namePricePoint2,pricePoint2);
				resultLog[43]=cd.getTime();
				resultLog[44]=String.valueOf(cd.getHigh());
				
			}
			
			if(cd.getLow()<=(todayFirstCloseMeta.getPositionValue(namePricePoint2,pricePoint2)-range1) 
					&& !todayFirstCloseMeta.checkPESLHit(namePricePoint2,pricePoint2)) {
				todayFirstCloseMeta.addPESLHit(namePricePoint2,pricePoint2);
				resultLog[45]=cd.getTime();
				resultLog[46]=String.valueOf(cd.getLow());

			}
		}

		if(todayFirstCloseMeta.checkPoistionTaken(namePricePoint3,pricePoint3)) {
			
			if(cd.getHigh()>=(range1+todayFirstCloseMeta.getPositionValue(namePricePoint3,pricePoint3)) 
					&& !todayFirstCloseMeta.checkCESLHit(namePricePoint3,pricePoint3)) {
				
				todayFirstCloseMeta.addCESLHit(namePricePoint3,pricePoint3);
				resultLog[47]=cd.getTime();
				resultLog[48]=String.valueOf(cd.getHigh());

			}
			
			if(cd.getLow()<=(todayFirstCloseMeta.getPositionValue(namePricePoint3,pricePoint3)-range1) 
					&& !todayFirstCloseMeta.checkPESLHit(namePricePoint3,pricePoint3)) {
				todayFirstCloseMeta.addPESLHit(namePricePoint3,pricePoint3);
				resultLog[49]=cd.getTime();
				resultLog[50]=String.valueOf(cd.getLow());

			}
		}
		
		/**
		 * Previous close
		 */
		if((cd.getHigh()>(firstClose+pricePoint1) || 
				cd.getLow()<(firstClose-pricePoint1)) && !todayFirstCloseMeta.checkPoistionTaken(namePricePoint1,pricePoint1) ) {
			
			if(cd.getHigh()>(firstClose+pricePoint1) && cd.getHigh()<(firstClose+(pricePoint1+30)) ) {
				todayFirstCloseMeta.addPositionTaken(namePricePoint1,pricePoint1);
				
				resultLog[36]=cd.getTime();
				todayFirstCloseMeta.addPositionValue(namePricePoint1,pricePoint1, cd.getClose());
			}
			
			if(cd.getLow()<(firstClose-pricePoint1) && cd.getLow()>(firstClose-(pricePoint1-30))) {
				todayFirstCloseMeta.addPositionTaken(namePricePoint1,pricePoint1);
				
				resultLog[36]=cd.getTime();
				todayFirstCloseMeta.addPositionValue(namePricePoint1,pricePoint1, cd.getClose());
				
			}
			
			
		}
		
		if((cd.getHigh()>(firstClose+pricePoint2) || 
				cd.getLow()<(firstClose-pricePoint2)) && !todayFirstCloseMeta.checkPoistionTaken(namePricePoint2,pricePoint2) ) {
			
			todayFirstCloseMeta.addPositionTaken(namePricePoint2,pricePoint2);
			resultLog[37]=cd.getTime();
			todayFirstCloseMeta.addPositionValue(namePricePoint2,pricePoint2, cd.getClose());
		}
		
		if((cd.getHigh()>(firstClose+pricePoint3) || 
				cd.getLow()<(firstClose-pricePoint3)) && !todayFirstCloseMeta.checkPoistionTaken(namePricePoint3,pricePoint3) ) {
			
			todayFirstCloseMeta.addPositionTaken(namePricePoint3,pricePoint3);
			resultLog[38]=cd.getTime();
			
			todayFirstCloseMeta.addPositionValue(namePricePoint3,pricePoint3, cd.getClose());
		}
		
	}
	
	
	
	private void collectData() {

		Iterator<Candle> candles =  ApplicationHelper.getKiteProcessedCandles("2023-07-13T09:15:00+0530","2023-07-14T15:29:00+0530").iterator();
		
		
		
		KiteBNFSellingCandle bnfData = new KiteBNFSellingCandle();
		
		Candle fileCandle = null;
		List<Candle> newCandleList =  new ArrayList<Candle>();
		int dayOfYear = 0;
		
		while(candles.hasNext()) {
			
			fileCandle = candles.next();
			
			LocalDateTime inputDate = LocalDateTime.parse(fileCandle.getTime().split("\\+")[0]);
			
			if(dayOfYear==0) {
				dayOfYear = inputDate.getDayOfYear();
			}
			
			if(dayOfYear!=inputDate.getDayOfYear() || !candles.hasNext()) {
				
				bnfData.setCandles(newCandleList);
				bnfdataList.add(bnfData);
				bnfData = new KiteBNFSellingCandle();
				newCandleList =  new ArrayList<Candle>();
				dayOfYear=inputDate.getDayOfYear();
			}
			
			if(bnfData.getTime()==null) {
				bnfData.setTime(fileCandle.getTime().split("\\+")[0]);
				
			}
			
			Candle  newCandle = new Candle(fileCandle);
			newCandleList.add(newCandle);
			
			
			
		}
		
		
	}

}
