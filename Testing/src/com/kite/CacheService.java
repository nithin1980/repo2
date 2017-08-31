package com.kite;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.IndexTreeList;
import org.mapdb.Serializer;

public class CacheService {
	
	
	
	static final File dbfile = new File("C:/data/mapdb/report5.db");
	static final DB reportdb = DBMaker.fileDB(dbfile).closeOnJvmShutdown().fileMmapEnableIfSupported().transactionEnable().make();
	//static final DB reportdb = DBMaker.fileDB(dbfile).closeOnJvmShutdown().fileMmapEnableIfSupported().make();
	
	static final File cachefile = new File("C:/data/mapdb/cache5.db");
	static final DB cachedb = DBMaker.fileDB(cachefile).closeOnJvmShutdown().fileMmapEnableIfSupported().transactionEnable().make();

	static final File cachefile_1 = new File("C:/data/mapdb/cache7.db");
	static final DB cachedb_1 = DBMaker.fileDB(cachefile_1).closeOnJvmShutdown().fileMmapEnableIfSupported().transactionEnable().allocateStartSize(40000).make();
	
	static boolean commited =  false;
	
	static HTreeMap<String, String> reporter = null;
	
	static TDoubleList niftytDoubleList  = new TDoubleArrayList();
	static IndexTreeList<TDoubleList> scriplist = null;
	private static int niftyCount = 0;
	
	
	
	
	static{
		reporter = reportdb.hashMap("index_report",Serializer.STRING,Serializer.STRING).createOrOpen();
		
		scriplist =  (IndexTreeList<TDoubleList>)cachedb_1.indexTreeList("nifty",Serializer.JAVA).createOrOpen();
		if(scriplist.size()==0){
			scriplist.add(niftytDoubleList);
			cachedb_1.commit();
		}
		niftytDoubleList = scriplist.get(0);
		
		
	}
	
	public void addOptionCache(String groupName, double pePrice,double cePrice){
		
		IndexTreeList<TDoubleList> options = (IndexTreeList<TDoubleList>)cachedb_1.indexTreeList(groupName,Serializer.JAVA).createOrOpen();
		 TDoubleList peList  = new TDoubleArrayList();
		 TDoubleList ceList  = new TDoubleArrayList();
		
		if(options.size()==0){
			options.add(peList);
			options.add(ceList);
			cachedb_1.commit();
		}
		
		peList = options.get(0);
		ceList = options.get(1);
		
		peList.add(pePrice);
		ceList.add(cePrice);
		
		options.set(0, peList);
		options.set(1, ceList);
		
	}
	
	
	
	public static void clearNifty(){
		niftytDoubleList.clear();
		niftyCount=0;
	}
	
	public static int niftyCount(){
		return niftyCount;
	}
	
	public static void addNifty(double value){
		niftytDoubleList.add(value);
		niftyCount++;
	}
	
	public static void dumpNifty_sync(){
		scriplist.set(0, niftytDoubleList);
		if(!cachedb_1.isClosed()){
			cachedb_1.commit();
		}
	}
	
	
	public static void dumpNifty(){
		ApplicationHelper.threadService.execute(new Runnable() {
			@Override
			public void run() {
				scriplist.set(0, niftytDoubleList);
				if(!cachedb_1.isClosed()){
					cachedb_1.commit();
				}
			}
		});
	}
	
	public static void cacheScriptInsert(String cacheName,  String time,double value){
		System.out.println(1);
		//final IndexTreeList<TDoubleList> scriplist =  (IndexTreeList<TDoubleList>)cachedb_1.indexTreeList(cacheName,Serializer.JAVA).createOrOpen();
		System.out.println(2);
		if(scriplist.size()==0){
			System.out.println("creating new..");
			scriplist.add(new TDoubleArrayList());
			cachedb_1.commit();
			
		}
		System.out.println(3);
		TDoubleList data = scriplist.get(0);
		System.out.println(4);
		data.add(value);
		System.out.println(5);
		scriplist.set(0, data);
		System.out.println(6);
		
		if(time==null){
			time="NO_TIME_RECORDED";
		}
		//need to throw an event which will automatically trigger processing logic...
		
		final String finalTime = time;
		
//		if(value%1999==0){
//			ApplicationHelper.threadService.execute(new Runnable() {
//				@Override
//				public void run() {
//						cachedb_1.commit();
//						commited  =true;
//					
//					// TODO Auto-generated method stub
//					
////					IndexTreeList<String> list = cachedb_1.indexTreeList(cacheName+"_time",Serializer.STRING).createOrOpen();
////					list.add(finalTime);
////					cachedb_1.commit();
//				}
//			});
//		}
		
	}
	
	public static void write(String key,String value){
		reporter.put(key, value);
		reportdb.commit();
		
	}
	
	public static void writeToCache(Double value, String cacheName){
		cachedb.indexTreeList(cacheName,Serializer.DOUBLE).createOrOpen().add(value);
		cachedb.commit();
		
	}
	
	public static void closeCache(){
		cachedb.close();
	}
	
	public static Double getLastItemFromCache(String cacheName){
		IndexTreeList<Double> list = cachedb.indexTreeList(cacheName,Serializer.DOUBLE).createOrOpen();
		
		return list.get(list.size()-1); 
	}


	public static TDoubleList getItemsFromNiftyCache(int size){
		//verify performance...
		TDoubleList items = new TDoubleArrayList();
		
		int listSize = niftytDoubleList.size();
		items.addAll(niftytDoubleList.subList(listSize-size, listSize));;
		
		return items;
		
	}
	
	public static TDoubleList[] getItemsFromOptionCache(String groupName,int size){
		//verify performance...
		TDoubleList peList = new TDoubleArrayList();
		TDoubleList ceList = new TDoubleArrayList();
		
		
		IndexTreeList<TDoubleList> options = (IndexTreeList<TDoubleList>)cachedb_1.indexTreeList(groupName,Serializer.JAVA).createOrOpen();
		
		
		int peSize = niftytDoubleList.size();
		
		peList.addAll(options.get(0).subList(peSize-size, peSize));
		ceList.addAll(options.get(1).subList(peSize-size, peSize));
		
		TDoubleList[] arrays = new TDoubleList[2];
		arrays[0] = peList;
		arrays[1] = ceList;
		
		return arrays;
		
	}
	
	
	/**
	 * The objects are referenced so dont modify the return directly
	 * ***WARNING****************************************
	 * @param size
	 * @param cacheName
	 * @return
	 */
	public static List<Double> getItemsFromCache(int size,String cacheName){
		IndexTreeList<Double> list = cachedb.indexTreeList(cacheName,Serializer.DOUBLE).createOrOpen();
		int listSize = list.getSize();
		/**
		 * Sublist copy is much faster than while or for loop
		 */
		//new TDoubleArrayList().get
		//List<Double> data = new ArrayList<Double>(list.subList(listSize-size, listSize));
		List<Double> data = list.subList(listSize-size, listSize);
		return data;
		
	}

	public static void clearCache(String cacheName){
		cachedb.indexTreeList(cacheName,Serializer.DOUBLE).createOrOpen().clear();
		cachedb.commit();
	}
	

	public static void main(String[] args) {
		
		
		long t = System.currentTimeMillis();
		
		
		//HTreeMap<String, String> map = reportdb.hashMap("index_report",Serializer.STRING,Serializer.STRING).open();
//		reporter.clear();
//		reportdb.commit();
//		StringBuilder builder;
//		
//		try {
//			FileWriter writer = new FileWriter(new File("C:/data/reports/report_25_08_2017.txt"));
//			builder = new StringBuilder();
//			//builder.append(reporter.toString());
//			String data = null;
//			//int i=1;
//			int size = reporter.size();
//			for(int i=1;i<size+1;i++){
//				data = reporter.get(String.valueOf(i));
//				builder.append(data+"--");
//			}
//			writer.write(builder.toString());
//			writer.close();
//			//writer.append(builder.toString());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
}
