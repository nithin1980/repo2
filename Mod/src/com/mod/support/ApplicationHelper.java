package com.mod.support;

import static com.mod.support.ApplicationConstant.KITE_ACCESS_TOKEN;
import static com.mod.support.ApplicationConstant.KITE_API_KEY;
import static com.mod.support.ApplicationConstant.KITE_PUBLIC_TOKEN;
import static com.mod.support.ApplicationConstant.KITE_USER_ID;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mod.interfaces.IStreamingQuoteParser;
import com.mod.interfaces.KiteHoldingsDataLayer1;
import com.mod.interfaces.KiteHoldingsQueryResponse;
import com.mod.interfaces.KiteInterface;
import com.mod.interfaces.KitePositionQueryResponse;
import com.mod.interfaces.StreamingQuote;
import com.mod.interfaces.StreamingQuoteModeLtp;
import com.mod.interfaces.kite.LTPKiteAPIWebsocket;
import com.mod.objects.EnumPositionStatus;
import com.mod.objects.KitePositionDataLayer2;
import com.mod.objects.PositionalData;
import com.mod.process.models.CacheService;
import com.mod.process.models.ProcessModelAbstract;
import com.test.TestKiteConnectMock;
import com.test.TestKiteTickerMock;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.ticker.KiteTicker;

public class ApplicationHelper {
	
	public static final boolean LogLevel1=false;
	public static final boolean LogLevel2=false;
	
	public static final Map<String, ConfigData> Application_Config_Cache = new HashMap<String, ConfigData>(); 
	
	public static final ExecutorService threadService = Executors.newFixedThreadPool(15);
	
	private static long timer = 0;
	
	private static long count=0;
	
	public static boolean isTesting=true;
	
	static final Properties prop = new Properties();
	
	static {
		try {
			prop.load(ApplicationHelper.class.getResourceAsStream("app.properties"));
			System.out.println("Properties loaded:"+prop.containsKey("driver.location"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public ApplicationHelper() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static KiteConnect getKiteSDK() {
		if(isTesting) {
			return new TestKiteConnectMock("");
		}
		return LTPKiteAPIWebsocket.getSDK();
	}
	public static KiteTicker getKiteTicker() {
		if(isTesting) {
			return new TestKiteTickerMock();
		}
		return LTPKiteAPIWebsocket.tickerProvider;
	}
	
	
	public static void botInitialSetup() {
		
//		try {
//			System.setOut(new PrintStream(new File("C:/data/eclipse_log.txt")));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		CacheService.variables.put(KITE_API_KEY, "4rytp6t5efi3t5ux");
		CacheService.variables.put(KITE_USER_ID, "IXW424");
		CacheService.variables.put(KITE_ACCESS_TOKEN, "QPawU6vtUg40ipl5TniB4luHTGKVATMy");
		CacheService.variables.put(KITE_PUBLIC_TOKEN, "1Lx0MWJMertdttdZnuvZuipsAxkWb2tv");
		
		KiteInterface kiteInterface = KiteInterface.getInstance();
		
		ConfigData configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/app.config");
		ApplicationHelper.Application_Config_Cache.put("app", configData);

		configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/genwsclient.config");
		ApplicationHelper.Application_Config_Cache.put("mode1", configData);
		

		configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/topbottom1.config");
		ApplicationHelper.Application_Config_Cache.put("topbottom1", configData);

		configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/bbmodel1.config");
		ApplicationHelper.Application_Config_Cache.put("bbmodel1", configData);

		configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/openhl.config");
		ApplicationHelper.Application_Config_Cache.put("openhl", configData);

		configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/bnfsellwithbuy.config");
		ApplicationHelper.Application_Config_Cache.put("bnfsellwithbuy", configData);

		List<OpenHighLowSupport> openHighLowdata =  ApplicationHelper.getStockFutureData("C:/Users/Vihaan/git/repo1/Mod/resource/stockfuturedata.config");
		if(openHighLowdata!=null && openHighLowdata.size()>0) {
			Iterator<OpenHighLowSupport> itr =  openHighLowdata.iterator();
			OpenHighLowSupport support = null;
			while(itr.hasNext()) {
				support = itr.next();
				CacheService.stockFutureData.put(support.getStock(), support);
			}
		}
		/**
		 * Get metadata of the stocks/instruments
		 */
		CacheService.stockMetadata = getStockMetaData("C:/Users/Vihaan/git/repo1/Mod/resource/metadata.config");
		
		
		
		
		//Topbottom logic
//		KitePositionQueryResponse response = (KitePositionQueryResponse) kiteInterface.queryCurrentPosition(null);
//		
//		if("success".equals(response.getStatus())) {
//			KitePositionDataLayer2[] layer2 = response.getData().getNet();
//			
//			if(layer2!=null && layer2.length>0) {
//	
//				for(int i=0;i<layer2.length;i++) {
//					PositionalData data = new PositionalData();
//					data.setTradingSymbol(layer2[i].getTradingsymbol());
//					data.setBuyPrice(layer2[i].getBuy_price());
//			 		data.setKey(layer2[i].getInstrument_token());
//			 		data.setTradeType(layer2[i].getProduct());
//					
//					data.setCount(1);
//					
//					/****
//					 * This doesn't indicate what type it is..
//					 * there could be multiple buy and sell on the position..
//					 */
//					if(layer2[i].getBuy_quantity()!=0) {
//						data.setBuyQuantity(layer2[i].getBuy_quantity());
//						data.setStatus(EnumPositionStatus.InPoistionLong);
//					}
//					if(layer2[i].getSell_quantity()!=0) {
//						data.setSellQuantity(layer2[i].getSell_quantity());
//						data.setStatus(EnumPositionStatus.InPositionShort);
//					}
//					
//					System.out.println("KiteAPIWebSocket:added position:"+layer2[i].getTradingsymbol()+" status:"+data.getStatus());
//					CacheService.positionalData.add(data);
//					
//				}				
//				
//				
//			}else {
//				System.out.println("KiteAPIWebsocket: Positional data"+layer2);
//			}
//			
//
//			
//			
//			
//		}else {
//			throw new RuntimeException("KiteAPIWebSocket: Could not get positional information:"+response.getStatus());
//		}
//		
//		/**
//		 * ONLY STOCKS COME IN TO THE HOLDINGS..
//		 */
//		
//		KiteHoldingsQueryResponse holdingsResponse = (KiteHoldingsQueryResponse) kiteInterface.queryCurrentHoldings(null);
//		if("success".equals(holdingsResponse.getStatus())) {
//			KiteHoldingsDataLayer1[] layer1 = holdingsResponse.getData();
//			
//			if(layer1!=null && layer1.length>0) {
//				for(int i=0;i<layer1.length;i++) {
//					PositionalData data = new PositionalData();
//					data.setTradingSymbol(layer1[i].getTradingSymbol());
//					data.setBuyPrice(layer1[i].getAverage_price());
//			 		data.setKey(layer1[i].getInstrument_token());
//			 		data.setTradeType(layer1[i].getProduct());
//			 		data.setHoldings(true);
//					
//					data.setCount(1);
//					
//					/****
//					 * This doesn't indicate what type it is..
//					 * there could be multiple buy and sell on the position..
//					 */
//					if(layer1[i].getQuantity()!=0 || layer1[i].getT1_quantity()!=0) {
//						if(layer1[i].getT1_quantity()==0) {
//							data.setBuyQuantity(layer1[i].getQuantity());
//						}else {
//							data.setBuyQuantity(layer1[i].getT1_quantity());
//						}
//						
//						data.setStatus(EnumPositionStatus.InPoistionLong);
//					}
//					
//					System.out.println("KiteAPIWebSocket:added holdings:"+layer1[i].getTradingSymbol()+" status:"+data.getStatus());
//					CacheService.positionalData.add(data);
//				
//				
//				}
//			
//				
//			}
//			
//			
//			
//		}else {
//			throw new RuntimeException("KiteAPIWebSocket: Could not get positional information:"+response.getStatus());
//		}		
		
		
	}
	
	public static String getKiteKey(String identifier) {
		/**
		 * ToDO
		 */
		
		List<String> strikePriceKeys =  appConfig().getReferenceDataMap().get("strike_price_keys");
		
		Iterator<String> itr =  strikePriceKeys.iterator();
		String val = null;
		while(itr.hasNext()) {
			val = itr.next();
			
			if(val.contains(identifier)) {
				return val.split("\\,")[1];
			}
		}
		
		throw new RuntimeException("Could not find key for strike price:"+identifier);
	}
	
	public static int getStrikePrice(double indexKey,double currentPrice, String positionType) {
		
		if(indexKey!=CacheService.BN_KEY && indexKey!=CacheService.NF_KEY) {
			throw new RuntimeException("Can get strikeprice for only NF & BNF");
		}
		
		long sPrice = 0;
		
		if("buy".equalsIgnoreCase(positionType)) {
			 sPrice = (Math.round((currentPrice-100))/100)*100;
		}
		
		if("sell".equalsIgnoreCase(positionType)) {
			sPrice = (Math.round((currentPrice+100))/100)*100;
		}
		
		
		return (int)sPrice;
		
		
		
		
	}
	
	/**
	 * 
	 * @param priceList
	 * @return
	 */
	public static double bbHighCalculation(Double[] priceList) {
		
		double sum = 0.0;
        int length = priceList.length;

        for(double num : priceList) {
            sum += num;
        }

        double avg = sum/length;
        
        
        double stdDev = stdDeviation(priceList);
        
        return avg+(2*stdDev);
		
	}
	public static double bbLowCalculation(Double[] priceList) {
		
		double sum = 0.0;
        int length = priceList.length;

        for(double num : priceList) {
            sum += num;
        }

        double avg = sum/length;
        
        double stdDev = stdDeviation(priceList);
        
        return avg-(2*stdDev);
		
	}
	
	public static double stdDeviation(Double[] numArray) {
		
		
		double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.length;

        for(double num : numArray) {
            sum += num;
        }

        double mean = sum/length;

        for(double num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation/length);
		
	}
	
	public static double positionVal(String modelKey){
		return Double.valueOf(modeConfig(modelKey).getKeyValueConfigs().get("position_val"));
	}
	public static int lotsize(String modelKey){
		return Integer.valueOf(modeConfig(modelKey).getKeyValueConfigs().get("lot_size"));
	}
	public static int calculateSize(double position_val,double cost,int lot_size ){
		int size = (int)(position_val/cost);
		
		if(((size+10)/lot_size)-(size/lot_size)==1){
			return (size+10)/lot_size;
		}else{
			return size/lot_size;
		}
		
	}
	
	
	public static double[] getPriceRange(String model) {
		ConfigData configData = XMLParsing.readAppConfig(ApplicationHelper.getProperty("config.location")+model+".config");
		
		double[] ranges = new double[4];
		String val = configData.getKeyValueConfigs().get("ce_range");
		String[] vals = val.split("\\,");
		ranges[0] = Double.valueOf(vals[0]);
		ranges[1] = Double.valueOf(vals[1]);
		
		val = configData.getKeyValueConfigs().get("pe_range");
		vals = val.split("\\,");
		ranges[2] = Double.valueOf(vals[0]);
		ranges[3] = Double.valueOf(vals[1]);
		
		Application_Config_Cache.put(model, configData);
		
		return ranges;
	}
	public static void placeConfig(String model) {
		ConfigData configData = XMLParsing.readAppConfig(ApplicationHelper.getProperty("config.location")+model+".config");
		Application_Config_Cache.put(model, configData);
		
	}
	public static String getProperty(String key) {
		return (String)prop.get(key);
	}
	
	public static ConfigData appConfig(){
		return ApplicationHelper.Application_Config_Cache.get("app");
	}
	public static ConfigData modeConfig(String modelId){
		return ApplicationHelper.Application_Config_Cache.get(modelId);
	}
	public static long getPositionId(String modelID,String key){
		String val = modeConfig(modelID).getKeyValueConfigs().get(key);
		if(val==null){
			throw new RuntimeException("Position must be set up:"+key);
		}
		return Long.valueOf(val);
		
	}	
	
	public static void bunchValues(double value){
		
		if(timer==0){
			timer = System.currentTimeMillis();
			CacheService.currentCandle.reset();
		}
		
		CacheService.currentCandle.populate(value);
		
		if((System.currentTimeMillis()-timer)>60000){
			timesUp(value);
		}
	}
	
	public static void timesUp(double value){
		timer=0;
		CacheService.currentCandle.setClose(value);
		CacheService.pastCandles.add(CacheService.currentCandle);
		CacheService.previousCandle = new Candle(CacheService.currentCandle);
		CacheService.currentCandle = new Candle();
	}
	
	
	public static ObjectMapper getObjectMapper(){
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		return mapper;		
	}	

    public static String[] ltpStrings(){
		
		String[] values =subscribeValues();
		
		String ltpMode = Application_Config_Cache.get("app").getKeyValueConfigs().get("stream_mode_ltp");
		
		String[] ltpStrings = new String[values.length];
		
		for(int i=0;i<values.length;i++){
			ltpStrings[i] = new StringBuilder(ltpMode).toString().replace("value", values[i]);
		}
		
		return ltpStrings;

    }	
	
    public static String[] subscribeValues(){
		StringBuilder subscribe = new StringBuilder(Application_Config_Cache.get("app").getKeyValueConfigs().get("subscribe_string"));
		
		subscribe.delete(0, 22);
		subscribe.delete(subscribe.length()-2, subscribe.length());
		
		String[] values =subscribe.toString().split("\\,");
    	return values;
    }
    
    public static List<Candle> getKiteProcessedCandles(){
		Scanner reader = null;
	List<Candle> candles = new ArrayList<Candle>();
		
		try {
			File file = new File("C:/data/testdata.txt");
			
			reader = new Scanner(file);
			
			StringBuilder data = new StringBuilder();
			
			while(reader.hasNextLine()) {
				data = data.append(reader.nextLine());
			}
			
			KiteCandleData candleData =  getObjectMapper().readValue(data.toString(), KiteCandleData.class);
			
			candles.addAll(candleData.getData().candleInformation());
			
			reader.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			
				if(reader!=null){
					reader.close();
				}
		}

		return candles;
    	
    }

    public static List<Candle> getKiteProcessedCandles(String startDate,String endDate){
    	
    	Iterator<Candle> itr = getKiteProcessedCandles().iterator();
    	
    	List<Candle> subSet = new ArrayList<Candle>();
    	
    	Candle candle = null;
    	
    	boolean started = false;
    	boolean finished = false;
    	
    	while(itr.hasNext()) {
    		
    		candle = itr.next();

    		if(endDate.equalsIgnoreCase(candle.getTime()) && !finished) {
    			subSet.add(candle);
    			finished=true;
    		}

    		if(started && !finished) {
    			subSet.add(candle);
    		}

    		if(startDate.equalsIgnoreCase(candle.getTime()) && !started) {
    			subSet.add(candle);
    			started=true;
    		}
    		
    		
    		
    	}
    	
    	return subSet;
    	
    }

    
    
//    public static double trailingSL(double currentPrice, double positionPrice, double currentSL, String positionType) {
//    	
//    	TrailingSL trailingSL = new TrailingSL();
//    	
//    	if("BUY".equalsIgnoreCase(positionType)) {
//    		if(percen(currentPrice,positionPrice)>=3){
//    			trailingSL.setSlPrice(currentPrice*.97);//-- new SL
//    		}else {
//    			trailingSL.setSlPrice(positionPrice*.97);//other wise default first SL
//    		}
//    		
//    	
//    	}
//    	if("SELL".equalsIgnoreCase(positionType)) {
//    		if(percen(positionPrice,currentPrice)>=3){
//    			trailingSL.setSlPrice(currentPrice*1.03);
//    		}else {
//    			trailingSL.setSlPrice(positionPrice*1.03);
//    		}
//    	}
//
//    	
//    }

    public static double givePercenValue(double val, double percenReq) {
    	return val*(percenReq/100);
    }
	public static double percen(double current,double prev){
		
		if(prev<=0) {
			throw new RuntimeException(" Previous value is zero or null:"+prev);
		}
		return ((current-prev)/prev)*100;
	}	
    
    public static List<GeneralObject> getNiftyData(String date){
		
		GeneralJsonObject jsonObject = null;
		
		BufferedReader reader =  null;
		
		List<String> data = new ArrayList<String>();
		
		List<GeneralObject> objects = new ArrayList<GeneralObject>();
		
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("C:/data/mapdb/databackup/nifty/"+date+".txt")),"UTF-8"));
			//reader = new BufferedReader(new FileReader(fileNamewithFullPath+"list/Product-Type.csv"));
			int i=1;
			String str = null;
			while((str=reader.readLine())!=null){
				if(str.length()>3 && !str.contains("#comment") && i>2){
					data.add(str.trim().replace(",", "").replace("[\"", "").replace("\"", "").replace("]", ""));
				}
				i++;
			}
			str=null;
			reader.close();
			
			
			
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			
		}
		int size = data.size();
		for(int i=0;i<size;i+=6){
			objects.add(new GeneralObject(data.get(i),data.get(i+1), data.get(i+2), data.get(i+3), data.get(i+4), data.get(i+5)));
		}
		
		data.clear();
		
		return objects;
    }
    public static List<String> getNiftyDataCSV(String date){
		
		
		BufferedReader reader =  null;
		
		List<String> data = new ArrayList<String>();
		
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("C:/data/reports/"+date+".txt")),"UTF-8"));
			int i=1;
			String str = null;
			while((str=reader.readLine())!=null){
				if(str.length()>3 && !str.contains("#comment")){
					data.add(str);
				}
			}
			str=null;
			reader.close();
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			
		}
		
		return data;
    }    
    public static List<GeneralObject> getCE_PEData(String type,String strike,String date){
		
		BufferedReader reader =  null;
		
		List<String> data = new ArrayList<String>();
		
		List<GeneralObject> objects = new ArrayList<GeneralObject>();
		
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("C:/data/mapdb/databackup/"+type+"/"+strike+"_"+date+".txt")),"UTF-8"));
			//reader = new BufferedReader(new FileReader(fileNamewithFullPath+"list/Product-Type.csv"));
			int i=1;
			String str = null;
			while((str=reader.readLine())!=null){
				if(str.length()>3 && !str.contains("#comment") && i>2){
					data.add(str.trim().replace(",", "").replace("[\"", "").replace("\"", "").replace("]", ""));
				}
				i++;
			}
			str=null;
			reader.close();
			
			
			
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			
		}
		int size = data.size();
		for(int i=0;i<size;i+=6){
			objects.add(new GeneralObject(data.get(i),data.get(i+1), data.get(i+2), data.get(i+3), data.get(i+4), data.get(i+5)));
		}
		
		data.clear();
		
		return objects;
    }
    

   public static Map<Long,StockMetadataSupport> getStockMetaData(String location) {
		Iterable<CSVRecord> records = null;
		Reader reader = null;
		
		Map<Long,StockMetadataSupport> configs = new HashMap<Long, StockMetadataSupport>();
		
		try {
			reader = new FileReader(location);
			records = CSVFormat.EXCEL.parse(reader);
			
			for(CSVRecord record:records){
				if(record.getRecordNumber()>1 && !record.get(0).contains("instrument")){
					configs.put(Long.valueOf(record.get(0)),new StockMetadataSupport(record));
				}
				
			}
			
			reader.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				if(reader!=null){
					reader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return configs;

   }

	public static List<OpenHighLowSupport> getStockFutureData(String location){
		Iterable<CSVRecord> records = null;
		Reader reader = null;
		
		List<OpenHighLowSupport> configs = new ArrayList<OpenHighLowSupport>();
		
		try {
			reader = new FileReader(location);
			records = CSVFormat.EXCEL.parse(reader);
			
			for(CSVRecord record:records){
				if(record.getRecordNumber()>1){
					configs.add(new OpenHighLowSupport(record.get(0),record.get(1),record.get(2)));
				}
				
			}
			
			reader.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				if(reader!=null){
					reader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return configs;
		
	}
    
    
	public static List<List<String>> getConfig(){
		Iterable<CSVRecord> records = null;
		Reader reader = null;
		
		List<List<String>> configs = new ArrayList<List<String>>();
		List<String> config = null;
		
		try {
			reader = new FileReader("C:/data/testdata.txt");
			records = CSVFormat.EXCEL.parse(reader);
			
			for(CSVRecord record:records){
				if(record.getRecordNumber()>1){
					config = new ArrayList<>();
					int size = record.size();
					for(int i=0;i<size;i++){
						config.add(record.get(i));
					}
					
					configs.add(config);
				}
				
			}
			
			reader.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				if(reader!=null){
					reader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return configs;
		
	}
	
	public static List<StreamingQuote> parseBuffer(ByteBuffer buffer, String time,List<ProcessModelAbstract> processingModels,IStreamingQuoteParser parser){
		// start parse Buffer array
		
		List<StreamingQuote> quotes = new ArrayList<StreamingQuote>();
		boolean validData = false;
		int start = buffer.position();
		int buffLen = buffer.capacity();
		if (buffLen == 1) {
			// HeartBeat
//			if(ZStreamingConfig.isHeartBitMsgPrintable()){
//				System.out.println("StreamingQuoteParserThread.parseBuffer(): WS HEARTBIT Byte");
//			}
		} else {
			// num of Packets
			int numPackets = buffer.getShort();
			if (numPackets == 0) {
				// Invalid Case: Zero Num of Packets - ignore
				System.out.println(
						"StreamingQuoteParserThread.parseBuffer(): ERROR: WS Byte numPackets is 0 in WS message, Ignoring !!!");
			} else {
				start += 2;
				//System.out.println("numPackets: " + numPackets);
				
				for (int i = 0; i < numPackets; i++) {
					// each packet
					//System.out.println("packet num: " + i);
					int numBytes = buffer.getShort();
					if (numBytes != 0) {
						// Valid Number of Bytes
						start += 2;
						
						// packet structure
						byte[] pkt = new byte[numBytes];
						buffer.get(pkt, 0, numBytes);
						ByteBuffer pktBuffer = ByteBuffer.wrap(pkt);
						if (pktBuffer != null) {
							//parse quote packet buffer
							parseQuotePktBuffer(pktBuffer, time,quotes,parser);
							//put the time and collection of data here
							//CacheService.addDateRecordingCache(data);
							start += numBytes;
							validData = true;
						} else {
							// Invalid Case: ByteBuffer could not wrap the bytes
							// - ignore
							System.out.println(
									"StreamingQuoteParserThread.parseBuffer(): ERROR: pktBuffer is null in WS message, Ignoring !!!");
						}
					} else {
						// Invalid Case: Zero Num of Bytes in packet - ignore
						System.out.println("StreamingQuoteParserThread.parseBuffer(): ERROR: numBytes is 0 in WS message packet[" + i + "], Ignoring !!!");
					}
				}
			}
		}
		
		/**
		 * This should move to a batch process of reading the price
		 */
		if(validData){
		//	CacheService.addDateRecordingCache();
			
			threadService.execute(new Runnable() {
				
				@Override
				public void run() {
//					CacheService.updateNiftyTrend(256265.0);
//					CacheService.createCandles();
				}
			});
			//Should be asynchronous
			int modelSize = processingModels.size();
			
			for(int i=0;i<modelSize;i++){
				/**
				 * @TODO This should be asynchrous
				 */
				if(processingModels!=null && 
						processingModels.get(i)!=null && 
						processingModels.get(i).completedProcess){
					processingModels.get(i).processNow();
				}
				
			}
		}else{
			System.out.println("heart beat data...");
		}
		 
		return quotes;
	}    
	
	private static void parseQuotePktBuffer(ByteBuffer pktBuffer, String time,List<StreamingQuote> quotes,IStreamingQuoteParser parser){
		StreamingQuote streamingQuote = null;
		
		if(parser != null){
			streamingQuote = parser.parse(pktBuffer,time );
			
			if(streamingQuote!=null){
				quotes.add(streamingQuote);
				
				StreamingQuoteModeLtp ltpObject = (StreamingQuoteModeLtp)streamingQuote;
				//System.out.println(ltpObject);
				CacheService.PRICE_LIST.put(Long.valueOf(ltpObject.getInstrumentToken()), ltpObject.getLtp().doubleValue());
				/**
				 * Need meta data in place before it is triggered.
				 */
				//System.out.println(count++ +","+ltpObject.getInstrumentToken()+","+ltpObject.getLtp().doubleValue());
				
			}
			
		}
		
		
	}
	
	public static void main(String[] args) {
		System.out.println(new Date(System.currentTimeMillis()).toString());
		
		System.out.println(new Date(System.currentTimeMillis()-(46400*1000)).toString());
		
	}

}
