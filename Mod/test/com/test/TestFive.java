package com.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.NtpV3Packet;
import org.apache.commons.net.ntp.TimeInfo;

import com.mod.objects.GroupPosition;
import com.mod.objects.Position;
import com.mod.process.models.CacheService;
import com.mod.process.models.DashBoard;
import com.mod.support.ApplicationHelper;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

public class TestFive {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		double[] vals = {42.65, 39.8, 39.8, 39.8, 32.4, 32.05, 30.55, 30.75, 29.2, 26.7, 27.4, 25.95, 26.25, 24.95, 24.2, 24.95, 24.2, 24.9, 28.7, 26.6, 25.6, 27.05, 27.5, 27.55, 27.65, 28.0, 28.25, 28.7, 28.55, 28.05, 27.7, 28};
//
//		double prevClose=41.0;
//		double cur=0;
//		double preVal=0;
//		
//		for(int i=0;i<vals.length;i++){
//			//System.out.println(percen(cur, prevClose));
//			cur=vals[i];
//			if(i==0){
//				preVal=cur;
//			}else{
//				preVal=vals[i-1];
//			}
//			double diff = cur-preVal;
//			if(diff !=0 && diff>.1){
//				if(percen(cur, prevClose)<-25){
//					System.out.println("b loop:"+cur);
//				}
//				
//			}else if(percen(cur, prevClose)<-38){
//				System.out.println("b:"+cur);
//			}
//
//		}
		
//	}	
		ApplicationHelper.placeConfig("pmodel11");
		double position_val = positionVal("pmodel11");
		int lot_size = lotsize("pmodel11");
		
		double[] ranges = ApplicationHelper.getPriceRange("pmodel11");
		System.out.println();
		double minPrice = ranges[2];
		double maxPrice= ranges[3];
		
		GroupPosition groupPosition = new GroupPosition();
		
		double cost = 39;
		Double key = null;
		int size=0;
		if(cost>minPrice && cost<maxPrice ){
			//cost = CacheService.PRICE_LIST.get(key);
			size =calculateSize(position_val, cost, lot_size);
			size=size*lot_size;
			groupPosition.getCePositions().add(new Position("CE", 100.00,cost,size));
		//	ApplicationHelper.modeConfig("pmodel11").getKeyValueConfigs().put("ce_id", String.valueOf(key.doubleValue()));
			
		}
		
	}
	private static double positionVal(String modelKey){
		return Double.valueOf(ApplicationHelper.modeConfig(modelKey).getKeyValueConfigs().get("position_val"));
	}
	private static int lotsize(String modelKey){
		return Integer.valueOf(ApplicationHelper.modeConfig(modelKey).getKeyValueConfigs().get("lot_size"));
	}
	private static int calculateSize(double position_val,double cost,int lot_size ){
		int size = (int)(position_val/cost);
		
		if(((size+10)/lot_size)-(size/lot_size)==1){
			return (size+10)/lot_size;
		}else{
			return size/lot_size;
		}
		
	}
	
	private void first(){
		//track first mnt and hold off for the next opp.
	}
	private void track(){
		TDoubleList pe = new TDoubleArrayList();
		int pe_size=0;
		double[] vals = {42.65, 39.8, 39.8, 39.8, 32.4, 32.05, 30.55, 30.75, 29.2, 26.7, 27.4, 25.95, 26.25, 24.95, 24.2, 24.95, 24.2, 24.9, 28.7, 26.6, 25.6, 27.05, 27.5, 27.55, 27.65, 28.0, 28.25, 28.7, 28.55, 28.05, 27.7, 28};
		
		for(int i=0;i<20;i++){
			pe.add(vals[i]);
			pe_size++;
			
			
			if(pe_size==60){
				pe.clear();
			}
		}
		
		
		
	}
	
	private static double percen(double current,double prev){
		return ((current-prev)/prev)*100;
	}

}
