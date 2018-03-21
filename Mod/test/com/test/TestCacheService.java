package com.test;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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

import com.mod.datafeeder.DataFeed;
import com.mod.objects.CacheMetaData;
import com.mod.process.models.CacheService;
import com.mod.process.models.Chart;
import com.mod.support.ApplicationHelper;

public class TestCacheService extends CacheService{

	protected TestCacheService() {
		// TODO Auto-generated constructor stub
	}
	
	private static final TestCacheService singleton = new TestCacheService(); 	
	
	static final String currentDate = getCurrentDate();

	static  File date_recording_db_file = new File("C:/data/mapdb/"+currentDate+"-test/"+currentDate+"-test.db");
	
	//static final File date_recording_db_file = new File("C:/data/mapdb/"+currentDate+".db");
	static DB date_recording_db = null;
	
	static boolean commited =  false;
	
	static HTreeMap<String, String> reporter = null;
	
	static TDoubleList niftytDoubleList  = new TDoubleArrayList();
	static IndexTreeList<TDoubleList> scriplist = null;
	private static int niftyCount = 0;
	
	static final int holdingSize = 10;
	private static TDoubleList[] dataArray_test = new TDoubleArrayList[holdingSize];
	
	
	public static HTreeMap<Double,TDoubleList> optionsBackup_test = null;
	private static TDoubleList optionCodes = new TDoubleArrayList();
	private static int optionsBackup_testCount = 0;
	private static int optionsListCount_test = 0;
	
	static{
		
//		try {
			date_recording_db_file.getParentFile().mkdirs();
			//date_recording_db_file.createNewFile();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		date_recording_db = DBMaker.fileDB(date_recording_db_file).closeOnJvmShutdown().fileMmapEnableIfSupported().transactionEnable().allocateStartSize(40000).make();
		

		
		//optionsBackup_test = (IndexTreeList<TDoubleList>)date_recording_db.indexTreeList("date_data_recorder",Serializer.JAVA).createOrOpen();
		
		optionsBackup_test = (HTreeMap<Double,TDoubleList>)date_recording_db.hashMap("date_data_recorder",Serializer.DOUBLE,Serializer.JAVA).createOrOpen();
	}
	public static TestCacheService getInstance(){
		return singleton;
	}		
	/**----------------------------------------------------------------------------------------------------------
	 */
	public static void addMetaDataToDateRecording(String groupName,CacheMetaData metadata){
		date_recording_db.hashMap("metadata",Serializer.STRING,Serializer.JAVA).createOrOpen().put(groupName, metadata.getData());
		date_recording_db.commit();
	}
	public static List<String> getMetaDataToDateRecording(String groupName){
		HTreeMap<String, List<String>> metadata = date_recording_db.hashMap("metadata",Serializer.STRING,Serializer.JAVA).createOrOpen();
		return metadata.get(groupName);
	}
	
	/**
	 * 
	 * @param data
	 * This should run in a backup thread.
	 * 
	 */
	public static void initializeDataArray(TDoubleList data){
		/**
		 * First element is always time
		 */
		optionsBackup_testCount = data.size();
		TDoubleList dbackup = null;
		
		/**
		 * Need to set the map size to prevent repeat size call.
		 */
		/**
		 * Special identifier for time
		 */
		   optionsBackup_test.put(1000000.0,new TDoubleArrayList());
			dbackup = new TDoubleArrayList();
			dbackup.add(data.get(0));
			optionCodes.add(data.get(0));
			optionsBackup_test.put(data.get(0),dbackup);
		
			for(int i=1;i<optionsBackup_testCount;i++){
				//optionsBackup_test.put(data.get(i),new TDoubleArrayList());
				dbackup = new TDoubleArrayList();
				dbackup.add(data.get(i));
				optionCodes.add(data.get(i));
				optionsBackup_test.put(data.get(i),dbackup);
			}
			optionsListCount_test=1;
			date_recording_db.commit();
	}
	public static void addDateRecordingCache(){
		/**
		 * first one is time.
		 */
		
		long t = System.currentTimeMillis();
		
		int size = optionCodes.size();
		TDoubleList dbackup = optionsBackup_test.get(1000000.0);
		dbackup.add(DataFeed.incrementTime());
		optionsBackup_test.put(1000000.0, dbackup);
		
		
		//double optionCode = 0;
		for(int i=1;i<size;i++){
			
			if(PRICE_LIST.containsKey(optionCodes.get(i))){
				dbackup = optionsBackup_test.get(optionCodes.get(i));
				dbackup.add(PRICE_LIST.get(optionCodes.get(i)));
				optionsBackup_test.put(optionCodes.get(i), dbackup);
			}else{
				throw new RuntimeException("Cannot find price from Price List for code:"+optionCodes.get(i));
			}
		}
		
		
		optionsListCount_test++;
		
		
	}
	public static void clearDateDataRecord(){
		HTreeMap<Double,TDoubleList> options = (HTreeMap<Double,TDoubleList>)date_recording_db.hashMap("date_data_recorder",Serializer.DOUBLE,Serializer.JAVA).createOrOpen();
		options.clear();
		optionsBackup_test.clear();
		optionsBackup_testCount=0;
		optionsListCount_test=0;
		optionCodes.clear();
		date_recording_db.commit();
	}
	public static TDoubleList getItemsFromDateDataRecord(double stockid, int size){
		//verify performance...
		TDoubleList items = new TDoubleArrayList();

//		int index=-1;
//
//		/**
//		 * The first array is time
//		 */
//		for(int i=1;i<optionsBackup_testCount;i++){
//			if(optionsBackup_test.get(i)!=null && optionsBackup_test.get(i).get(0)==stockid){
//				index = i;
//			}
//		}
//		if(index<0){
//			throw new RuntimeException("Cannot locate stock for:"+stockid);
//		}
		items.addAll(optionsBackup_test.get(stockid).subList(optionsListCount_test-size, optionsListCount_test));;
		
		return items;
		
	}
	public static void updateNiftyTrend(double stockid){
		TDoubleList list = null;
		if(optionsListCount_test>60){
			list = getItemsFromDateDataRecord(stockid, 60);
		}
		
		if(list!=null){
			niftyTrend =  Chart.calculateTrend(list);
		}
		
	}	
	public static TDoubleList getItemsFromDateDataRecord_Test(double stockid, int size){
		//verify performance...
		
		HTreeMap<Double,TDoubleList> options = (HTreeMap<Double,TDoubleList>)date_recording_db.hashMap("date_data_recorder",Serializer.DOUBLE,Serializer.JAVA).createOrOpen();
		
		
		
		TDoubleList items = new TDoubleArrayList();
		TDoubleList data = options.get(stockid);
		items.addAll(options.get(stockid).subList(optionsListCount_test-size, optionsListCount_test));;

		return items;
		
	}
	
	public static void dumpDateRecording(){
		
//		IndexTreeList<TDoubleList> options = (IndexTreeList<TDoubleList>)date_recording_db.indexTreeList("date_data_recorder",Serializer.JAVA).createOrOpen();
//		int i=0;
//		while(dataArray[i]!=null){
//			options.set(i, dataArray[i]);
//			i++;
//		}
		
		if(!date_recording_db.isClosed()){
			date_recording_db.commit();
		}
	}	
	private static String getCurrentDate(){
		Calendar calendar = Calendar.getInstance();
		String date = calendar.get(Calendar.DATE)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.YEAR);
		return date;
	}
	/**----------------------------------------------------------------------------------------------------------
	 */
	
	
	
	
	
	
	
	
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
	
	


	public static TDoubleList getItemsFromNiftyCache(int size){
		//verify performance...
		TDoubleList items = new TDoubleArrayList();
		
		int listSize = niftytDoubleList.size();
		items.addAll(niftytDoubleList.subList(listSize-size, listSize));;
		
		return items;
		
	}
	
	

	public static void main(String[] args) {
		
		
		long t = System.currentTimeMillis();
		
		HTreeMap<Double,TDoubleList> options = (HTreeMap<Double,TDoubleList>)date_recording_db.hashMap("date_data_recorder",Serializer.DOUBLE,Serializer.JAVA).createOrOpen();
		TDoubleList slist = options.get(12616706.0);
		
		System.out.println();
		
		//HTreeMap<String, String> map = reportdb.hashMap("index_report",Serializer.STRING,Serializer.STRING).open();
//		reporter.clear();
//		reportdb.commit();
//		StringBuilder builder;
//		
//		try {
//			FileWriter writer = new FileWriter(new File("C:/data/reports/report_18_10_2017.txt"));
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
