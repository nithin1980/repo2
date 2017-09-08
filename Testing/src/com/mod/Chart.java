package com.mod;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import com.tictactec.ta.lib.test.InputData;

public class Chart {
	
	public static final String UPTREND = "Uptrend";
	public static final String DOWNTREND = "Downtrend";
	public static final String SIDETREND = "Sidetrend";
	
	public static void main(String[] args) {
		Double[] d = {21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0,21.23,23.45,45.0};
		Queue<Double> data = new LinkedList<Double>();
		data.addAll(Arrays.asList(d));
		long t = System.currentTimeMillis();
		
		List<Double> dd = CacheService.getItemsFromCache(100, "9700CE");
		System.out.println("dd"+dd);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				CacheService.writeToCache(719.1, "9700CE");
				
			}
		}).start();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		findHighLows(dd);
		
//		System.out.println("dd"+dd);
//		TDoubleList tDoubleList = new TDoubleArrayList();
//		List<Double> dd2 = new ArrayList<Double>();
//		System.out.println(System.currentTimeMillis()-t);
//		dd2.addAll(dd);
//		System.out.println(System.currentTimeMillis()-t);
//		tDoubleList.addAll(dd);
//		System.out.println(System.currentTimeMillis()-t);
//		System.out.println("tDoubleList"+tDoubleList);
//		System.out.println("dd"+dd);
//		List<Double> dd1 = CacheService.getItemsFromCache(5, "9700CE");
//		System.out.println("dd1"+dd1);
//		System.out.println(System.currentTimeMillis()-t);
//		
//		CacheService.closeCache();
		
		/**
		 * Chart pattern
		 */
//		double[] open = {21,22,22,21,22,22,21,22,22,21,22,22};
//		double[] high = {23,24,25,23,24,25,23,24,25,23,24,25};
//		double[] low =  {20,19,20,20,19,20,20,19,20,20,19,20};
//		double[] close = {22,23,21,22,23,21,22,23,21,22,23,21};
//		
//		double[] outIndex = new double[12];
//		int[] outIndex2 = new int[12];
//		
//		
//		//Double currentPoint = data.get(index)
//		MInteger outBeg = new MInteger();
//		MInteger outNbEl = new MInteger();
//		RetCode retCode = null;
//		//retCode = new Core().sma(0,11,close,5,outBeg,outNbEl,outIndex);
//		retCode = new Core().cdlHignWave(0,11, open,high, low, close, outBeg, outNbEl, outIndex2);
//		retCode = new Core().plusDM(0,11, high, low,5, outBeg, outNbEl, outIndex);
//		System.out.println();
		
	}
	
	@Test
	public void testTrend(){
		long t = System.currentTimeMillis();
		List<List<String>> configs  = ApplicationHelper.getConfig();
		List<Double> data = new ArrayList<Double>();
		int configSize = configs.size();
		for(int i=0;i<configSize;i++){
			data.add(Double.valueOf(configs.get(i).get(0)));
		}
		
		List<List<Double>> splitData = new ArrayList<List<Double>>();
		
		for(int i=0;i<2100;i+=5){
			splitData.add(new ArrayList<Double>(data.subList(i, i+5)));
			
		}
		
		int splitDataSize = splitData.size();
		String firstResult = null;
		String secondResult = null;
		for(int i=0;i<splitDataSize-1;i++){
			firstResult =  findHighLows(splitData.get(i));
			secondResult = findHighLows(splitData.get(i+1));
			trend(firstResult, secondResult);
			
		}
		
		System.out.println(System.currentTimeMillis()-t);
	}
	
	/**
	 * 
	 * @param list
	 * @return
	 */
	public static String calculateTrend(TDoubleList list){
		int size = list.size();
		int half = size/2;
		
		TDoubleList firstHalf = new TDoubleArrayList();
		firstHalf.addAll(list.subList(0, half));
		
		TDoubleList secondHalf = new TDoubleArrayList();
		secondHalf.addAll(list.subList(half, size));
		
		String first = findHighLows(firstHalf);
		String second = findHighLows(secondHalf);
		
		return trend(first, second);
	}
	
	private static String trend(String first,String second){
		String[] firstSplit = first.split("\\,");
		String[] secondSplit = second.split("\\,");
		
		double firstHigh = Double.valueOf(firstSplit[1]);
		double firstLow = Double.valueOf(firstSplit[0]);
		double secondHigh = Double.valueOf(secondSplit[1]);
		double secondLow = Double.valueOf(secondSplit[0]);
		
		if(secondHigh>=firstHigh && secondLow>=firstLow){
			//System.out.println("Uptrend");
			return UPTREND;
		}
		else if(secondHigh<=firstHigh && secondLow<=firstLow){
			//System.out.println("Downtrend");
			//System.out.println("Downtrend or side: second high:"+secondHigh+" first high::"+firstHigh+" second low:"+secondLow+" first low:"+firstLow);
			return DOWNTREND;
		}else{
			//System.out.println("Sidetrend");
			return SIDETREND;
		}
		
	}
	
	public static boolean isLowerThanHighest(double value,TDoubleList list){
		String result = findHighLows(list);
		String[] resultSplit= result.split("\\,");
		double firstHigh = Double.valueOf(resultSplit[1]);
		
		
		if(value<firstHigh){
			return true;
		}
		
		return false;
		
	}
	public static boolean isHigherThanLowest(double value,TDoubleList list){
		String result = findHighLows(list);
		String[] resultSplit= result.split("\\,");
		double firstLow = Double.valueOf(resultSplit[0]);
		
		if(value>firstLow){
			return true;
		}
		
		return false;
		
	}
	
	public static String findHighLows(List<Double> data){
		TDoubleList list = new TDoubleArrayList();
		list.addAll(data);
		list.sort();
		int size = list.size();
		return list.get(0)+","+list.get(size-1);
		
	}

	public static String findHighLows(TDoubleList list){
		list.sort();
		int size = list.size();
		return list.get(0)+","+list.get(size-1);
		
	}
	
	

}
