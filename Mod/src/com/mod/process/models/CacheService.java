package com.mod.process.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mod.enums.EnumPositionType;
import com.mod.objects.PositionalData;
import com.mod.support.Candle;
import com.mod.support.CandleWrapper;
import com.mod.support.OpenHighLowSupport;
import com.mod.support.StockMetadataSupport;

public class CacheService{

	
	protected CacheService() {
		// TODO Auto-generated constructor stub
	}
	
	public static final Map<String, String> variables = new HashMap<String, String>();
	
	private static final CacheService singleton = new CacheService(); 
	public static final long BN_KEY=260105;
	public static final long NF_KEY=256265;
	
	static final String currentDate = getCurrentDate();
//	static final File dbfile = new File("C:/data/mapdb/report5.db");
//	static final DB reportdb = DBMaker.fileDB(dbfile).closeOnJvmShutdown().fileMmapEnableIfSupported().transactionEnable().make();
//	//static final DB reportdb = DBMaker.fileDB(dbfile).closeOnJvmShutdown().fileMmapEnableIfSupported().make();
//	
//	static final File cachefile = new File("C:/data/mapdb/cache5.db");
//	static final DB cachedb = DBMaker.fileDB(cachefile).closeOnJvmShutdown().fileMmapEnableIfSupported().transactionEnable().make();
//
//	static final File cachefile_1 = new File("C:/data/mapdb/cache7.db");
//	static final DB cachedb_1 = DBMaker.fileDB(cachefile_1).closeOnJvmShutdown().fileMmapEnableIfSupported().transactionEnable().allocateStartSize(40000).make();
//
//	static  File date_recording_db_file = new File("C:/data/mapdb/"+currentDate+"/"+currentDate+".db");
	
	//static final File date_recording_db_file = new File("C:/data/mapdb/"+currentDate+".db");
//	static DB date_recording_db = null;
//	
//	static boolean commited =  false;
//	
//	static HTreeMap<String, String> reporter = null;
//	
//	static TDoubleList niftytDoubleList  = new TDoubleArrayList();
//	static IndexTreeList<TDoubleList> scriplist = null;
	private static int niftyCount = 0;
	
	static final int holdingSize = 10;
//	private static TDoubleList[] dataArray = new TDoubleArrayList[holdingSize];
	
	public static Map<Long, Double> PRICE_LIST = new HashMap<Long, Double>();
	public static List<PositionalData> positionalData = new ArrayList<PositionalData>();
	public static Map<Long, CandleWrapper> candleData= new HashMap<Long, CandleWrapper>();
	public static Map<Long,OpenHighLowSupport> stockFutureData = new HashMap<Long, OpenHighLowSupport>();
	public static Map<Long, StockMetadataSupport> stockMetadata =  new HashMap<Long, StockMetadataSupport>();
	
	
	//public static LinkedList<Double> BB_Close_Records = new LinkedList<Double>();
	
	
	
	
	public  Integer[] findPositionsbyKey(long key, EnumPositionType positionType) {
		
		List<Integer> indexes = new ArrayList<Integer>();
		
		if(positionalData!=null & positionalData.size()>0) {
			for(int i=0;i<positionalData.size();i++) {
				if(key == positionalData.get(i).getKey() && positionType.equals(positionalData.get(i).getPositionType())) {
					indexes.add(i);
				}else if(key == positionalData.get(i).getKey() && positionType.equals(EnumPositionType.Both)) {
					indexes.add(i);
				}
				
				
				
			}
		}
		
		return indexes.toArray(new Integer[indexes.size()]);
	}
	
//	public static HTreeMap<Double,TDoubleList> optionsBackup = null;
//	private static TDoubleList optionCodes = new TDoubleArrayList();
//	private static int optionsBackupCount = 0;
//	private static int optionsListCount = 0;
	
	public static String niftyTrend="UNDEFINED";
	
	
	public static List<Candle> pastCandles = new ArrayList<Candle>();
	public static Candle previousCandle;
	public static Candle currentCandle=new Candle();
	
//	static{
//		
////		try {
//			date_recording_db_file.getParentFile().mkdirs();
//			//date_recording_db_file.createNewFile();
////		} catch (IOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//		date_recording_db = DBMaker.fileDB(date_recording_db_file).closeOnJvmShutdown().fileMmapEnableIfSupported().transactionEnable().allocateStartSize(40000).make();
//		
//		reporter = reportdb.hashMap("index_report",Serializer.STRING,Serializer.STRING).createOrOpen();
//		
//		scriplist =  (IndexTreeList<TDoubleList>)cachedb_1.indexTreeList("nifty",Serializer.JAVA).createOrOpen();
//		if(scriplist.size()==0){
//			scriplist.add(niftytDoubleList);
//			cachedb_1.commit();
//		}
//		niftytDoubleList = scriplist.get(0);
//		
//		//optionsBackup = (IndexTreeList<TDoubleList>)date_recording_db.indexTreeList("date_data_recorder",Serializer.JAVA).createOrOpen();
//		
//		optionsBackup = (HTreeMap<Double,TDoubleList>)date_recording_db.hashMap("date_data_recorder",Serializer.DOUBLE,Serializer.JAVA).createOrOpen();
//	}
	
	public static CacheService getInstance(){
		return singleton;
	}
	
	public static double getBNCurrentPrice() {
		return PRICE_LIST.get(BN_KEY);
	}
	public static double getNFCurrentPrice() {
		return PRICE_LIST.get(NF_KEY);
	}
	
	/**----------------------------------------------------------------------------------------------------------
	 */
//	public static void addMetaDataToDateRecording(String groupName,CacheMetaData metadata){
//		date_recording_db.hashMap("metadata",Serializer.STRING,Serializer.JAVA).createOrOpen().put(groupName, metadata.getData());
//		date_recording_db.commit();
//	}
//	public static List<String> getMetaDataToDateRecording(String groupName){
//		HTreeMap<String, List<String>> metadata = date_recording_db.hashMap("metadata",Serializer.STRING,Serializer.JAVA).createOrOpen();
//		return metadata.get(groupName);
//	}
	
	/**
	 * 
	 * @param data
	 * This should run in a backup thread.
	 * 
	 */
//	public static void initializeDataArray(TDoubleList data){
//		/**
//		 * First element is always time
//		 */
//		optionsBackupCount = data.size();
//		TDoubleList dbackup = null;
//		
//		/**
//		 * Need to set the map size to prevent repeat size call.
//		 */
//		/**
//		 * Special identifier for time
//		 */
//		   optionsBackup.put(1000000.0,new TDoubleArrayList());
//			dbackup = new TDoubleArrayList();
//			dbackup.add(data.get(0));
//			optionCodes.add(data.get(0));
//			optionsBackup.put(data.get(0),dbackup);
//		
//			for(int i=1;i<optionsBackupCount;i++){
//				//optionsBackup.put(data.get(i),new TDoubleArrayList());
//				dbackup = new TDoubleArrayList();
//				dbackup.add(data.get(i));
//				optionCodes.add(data.get(i));
//				optionsBackup.put(data.get(i),dbackup);
//			}
//			optionsListCount=1;
//			date_recording_db.commit();
//	}
//	public static void addDateRecordingCache(){
//		/**
//		 * first one is time.
//		 */
//		
//		long t = System.currentTimeMillis();
//		
//		int size = optionCodes.size();
//		TDoubleList dbackup = optionsBackup.get(1000000.0);
//		dbackup.add(DataFeed.incrementTime());
//		optionsBackup.put(1000000.0, dbackup);
//		
//		
//		//double optionCode = 0;
//		for(int i=1;i<size;i++){
//			
//			if(PRICE_LIST.containsKey(optionCodes.get(i))){
//				dbackup = optionsBackup.get(optionCodes.get(i));
//				dbackup.add(PRICE_LIST.get(optionCodes.get(i)));
//				optionsBackup.put(optionCodes.get(i), dbackup);
//			}else{
//				throw new RuntimeException("Cannot find price from Price List for code:"+optionCodes.get(i));
//			}
//		}
//		
//		
//		optionsListCount++;
//		
//		
//	}
//	public static void updateNiftyTrend(double stockid){
//		TDoubleList list = null;
//		if(optionsListCount>60){
//			list = getItemsFromDateDataRecord(stockid, 60);
//		}
////		else if(optionsListCount>20){
////			list = getItemsFromDateDataRecord(stockid, 20);
////		}
//		
//		if(list!=null){
//			niftyTrend =  Chart.calculateTrend(list);
//		}
//		
//	}
//	public static void createCandles(){
//		//create candles accordingly......
//	}
//	public static void clearDateDataRecord(){
//		HTreeMap<Double,TDoubleList> options = (HTreeMap<Double,TDoubleList>)date_recording_db.hashMap("date_data_recorder",Serializer.DOUBLE,Serializer.JAVA).createOrOpen();
//		options.clear();
//		optionsBackup.clear();
//		optionsBackupCount=0;
//		optionsListCount=0;
//		optionCodes.clear();
//		date_recording_db.commit();
//	}
//	public static TDoubleList getItemsFromDateDataRecord(double stockid, int size){
//		//verify performance...
//		TDoubleList items = new TDoubleArrayList();
//
////		int index=-1;
////
////		/**
////		 * The first array is time
////		 */
////		for(int i=1;i<optionsBackupCount;i++){
////			if(optionsBackup.get(i)!=null && optionsBackup.get(i).get(0)==stockid){
////				index = i;
////			}
////		}
////		if(index<0){
////			throw new RuntimeException("Cannot locate stock for:"+stockid);
////		}
//		items.addAll(optionsBackup.get(stockid).subList(optionsListCount-size, optionsListCount));;
//		
//		return items;
//		
//	}
//	public static double getValueForIndex(double stockid,int index){
//		HTreeMap<Double,TDoubleList> options = (HTreeMap<Double,TDoubleList>)date_recording_db.hashMap("date_data_recorder",Serializer.DOUBLE,Serializer.JAVA).createOrOpen();
//		return options.get(stockid).get(index);
//	}
//	public static TDoubleList getItemsFromDateDataRecord_Test(double stockid, int size){
//		//verify performance...
//		
//		HTreeMap<Double,TDoubleList> options = (HTreeMap<Double,TDoubleList>)date_recording_db.hashMap("date_data_recorder",Serializer.DOUBLE,Serializer.JAVA).createOrOpen();
//		
//
//		optionsListCount = options.get(stockid).size();
//		TDoubleList items = new TDoubleArrayList();
//		
//		items.addAll(options.get(stockid).subList(optionsListCount-size, optionsListCount));;
//
//		return items;
//		
//	}
//	public static TDoubleList getItemsFromDateDataRecord_Test(double stockid, int startIndex,int endIndex){
//		//verify performance...
//		
//		HTreeMap<Double,TDoubleList> options = (HTreeMap<Double,TDoubleList>)date_recording_db.hashMap("date_data_recorder",Serializer.DOUBLE,Serializer.JAVA).createOrOpen();
//		
//		TDoubleList items = new TDoubleArrayList();
//		
//		items.addAll(options.get(stockid).subList(startIndex, endIndex));;
//
//		return items;
//		
//	}
//	
//	public static void dumpDateRecording(){
//		
////		IndexTreeList<TDoubleList> options = (IndexTreeList<TDoubleList>)date_recording_db.indexTreeList("date_data_recorder",Serializer.JAVA).createOrOpen();
////		int i=0;
////		while(dataArray[i]!=null){
////			options.set(i, dataArray[i]);
////			i++;
////		}
//		
//		if(!date_recording_db.isClosed()){
//			System.out.println("data saved..");
//			date_recording_db.commit();
//		}
//	}	
	private static String getCurrentDate(){
		Calendar calendar = Calendar.getInstance();
		String date = calendar.get(Calendar.DATE)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.YEAR);
		return date;
	}
//	/**----------------------------------------------------------------------------------------------------------
//	 */
//	
//	
//	
//	
//	
//	public void addOptionCache(String groupName, double pePrice,double cePrice){
//		
//		IndexTreeList<TDoubleList> options = (IndexTreeList<TDoubleList>)cachedb_1.indexTreeList(groupName,Serializer.JAVA).createOrOpen();
//		 TDoubleList peList  = new TDoubleArrayList();
//		 TDoubleList ceList  = new TDoubleArrayList();
//		
//		if(options.size()==0){
//			options.add(peList);
//			options.add(ceList);
//			cachedb_1.commit();
//		}
//		
//		peList = options.get(0);
//		ceList = options.get(1);
//		
//		peList.add(pePrice);
//		ceList.add(cePrice);
//		
//		options.set(0, peList);
//		options.set(1, ceList);
//		
//	}
	
	
	
//	public static void clearNifty(){
//		niftytDoubleList.clear();
//		niftyCount=0;
//	}
//	
//	public static int niftyCount(){
//		return niftyCount;
//	}
//	
//	public static void addNifty(double value){
//		niftytDoubleList.add(value);
//		niftyCount++;
//	}
	
//	public static void dumpNifty_sync(){
//		scriplist.set(0, niftytDoubleList);
//		if(!cachedb_1.isClosed()){
//			cachedb_1.commit();
//		}
//	}
//	
//	
//	public static void dumpNifty(){
//		ApplicationHelper.threadService.execute(new Runnable() {
//			@Override
//			public void run() {
//				scriplist.set(0, niftytDoubleList);
//				if(!cachedb_1.isClosed()){
//					cachedb_1.commit();
//				}
//			}
//		});
//	}
//	
//	public static void cacheScriptInsert(String cacheName,  String time,double value){
//		System.out.println(1);
//		//final IndexTreeList<TDoubleList> scriplist =  (IndexTreeList<TDoubleList>)cachedb_1.indexTreeList(cacheName,Serializer.JAVA).createOrOpen();
//		System.out.println(2);
//		if(scriplist.size()==0){
//			System.out.println("creating new..");
//			scriplist.add(new TDoubleArrayList());
//			cachedb_1.commit();
//			
//		}
//		System.out.println(3);
//		TDoubleList data = scriplist.get(0);
//		System.out.println(4);
//		data.add(value);
//		System.out.println(5);
//		scriplist.set(0, data);
//		System.out.println(6);
//		
//		if(time==null){
//			time="NO_TIME_RECORDED";
//		}
//		//need to throw an event which will automatically trigger processing logic...
//		
//		final String finalTime = time;
//		
////		if(value%1999==0){
////			ApplicationHelper.threadService.execute(new Runnable() {
////				@Override
////				public void run() {
////						cachedb_1.commit();
////						commited  =true;
////					
////					// TODO Auto-generated method stub
////					
//////					IndexTreeList<String> list = cachedb_1.indexTreeList(cacheName+"_time",Serializer.STRING).createOrOpen();
//////					list.add(finalTime);
//////					cachedb_1.commit();
////				}
////			});
////		}
//		
//	}
//	
//	public static void write(String key,String value){
//		reporter.put(key, value);
//		reportdb.commit();
//		
//	}
	
//	public static void writeToCache(Double value, String cacheName){
//		cachedb.indexTreeList(cacheName,Serializer.DOUBLE).createOrOpen().add(value);
//		cachedb.commit();
//		
//	}
//	
//	public static void closeCache(){
//		cachedb.close();
//	}
//	
//	public static Double getLastItemFromCache(String cacheName){
//		IndexTreeList<Double> list = cachedb.indexTreeList(cacheName,Serializer.DOUBLE).createOrOpen();
//		
//		return list.get(list.size()-1); 
//	}
//
//
//	public static TDoubleList getItemsFromNiftyCache(int size){
//		//verify performance...
//		TDoubleList items = new TDoubleArrayList();
//		
//		int listSize = niftytDoubleList.size();
//		items.addAll(niftytDoubleList.subList(listSize-size, listSize));;
//		
//		return items;
//		
//	}
//	
//	public static TDoubleList[] getItemsFromOptionCache(String groupName,int size){
//		//verify performance...
//		TDoubleList peList = new TDoubleArrayList();
//		TDoubleList ceList = new TDoubleArrayList();
//		
//		
//		IndexTreeList<TDoubleList> options = (IndexTreeList<TDoubleList>)cachedb_1.indexTreeList(groupName,Serializer.JAVA).createOrOpen();
//		
//		
//		int peSize = niftytDoubleList.size();
//		
//		peList.addAll(options.get(0).subList(peSize-size, peSize));
//		ceList.addAll(options.get(1).subList(peSize-size, peSize));
//		
//		TDoubleList[] arrays = new TDoubleList[2];
//		arrays[0] = peList;
//		arrays[1] = ceList;
//		
//		return arrays;
//		
//	}
//	
//	
//	/**
//	 * The objects are referenced so dont modify the return directly
//	 * ***WARNING****************************************
//	 * @param size
//	 * @param cacheName
//	 * @return
//	 */
//	public static List<Double> getItemsFromCache(int size,String cacheName){
//		IndexTreeList<Double> list = cachedb.indexTreeList(cacheName,Serializer.DOUBLE).createOrOpen();
//		int listSize = list.getSize();
//		/**
//		 * Sublist copy is much faster than while or for loop
//		 */
//		//new TDoubleArrayList().get
//		//List<Double> data = new ArrayList<Double>(list.subList(listSize-size, listSize));
//		List<Double> data = list.subList(listSize-size, listSize);
//		return data;
//		
//	}
//
//	public static void clearCache(String cacheName){
//		cachedb.indexTreeList(cacheName,Serializer.DOUBLE).createOrOpen().clear();
//		cachedb.commit();
//	}
//	
//
//	public static void main(String[] args) {
//		
//		
//		long t = System.currentTimeMillis();
//		
//		HTreeMap<Double,TDoubleList> options = (HTreeMap<Double,TDoubleList>)date_recording_db.hashMap("date_data_recorder",Serializer.DOUBLE,Serializer.JAVA).createOrOpen();
//		TDoubleList slist = options.get(12616706.0);
//		
//		System.out.println();
//		
//		//HTreeMap<String, String> map = reportdb.hashMap("index_report",Serializer.STRING,Serializer.STRING).open();
////		reporter.clear();
////		reportdb.commit();
////		StringBuilder builder;
////		
////		try {
////			FileWriter writer = new FileWriter(new File("C:/data/reports/report_18_10_2017.txt"));
////			builder = new StringBuilder();
////			//builder.append(reporter.toString());
////			String data = null;
////			//int i=1;
////			int size = reporter.size();
////			for(int i=1;i<size+1;i++){
////				data = reporter.get(String.valueOf(i));
////				builder.append(data+"--");
////			}
////			writer.write(builder.toString());
////			writer.close();
////			//writer.append(builder.toString());
////		} catch (IOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//		
//	}
}
