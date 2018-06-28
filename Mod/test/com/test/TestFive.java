package com.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.NtpV3Packet;
import org.apache.commons.net.ntp.TimeInfo;

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
		
	try {
		//String TIME_SERVER = "nist1-ny.ustiming.org";
		String TIME_SERVER = "time-a-g.nist.gov";
		
		NTPUDPClient timeClient = new NTPUDPClient();
		InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
		TimeInfo timeInfo = timeClient.getTime(inetAddress);
		NtpV3Packet message = timeInfo.getMessage();
		long serverTime = message.getTransmitTimeStamp().getTime();
		Date time = new Date(serverTime);
		System.out.println("Time from " + TIME_SERVER + ": " + time);
	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
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
