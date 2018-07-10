package com.mod.support;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mod.interfaces.IStreamingQuoteParser;
import com.mod.interfaces.StreamingQuote;
import com.mod.interfaces.StreamingQuoteModeLtp;
import com.mod.process.models.CacheService;
import com.mod.process.models.ProcessModelAbstract;

public class ApplicationHelper {
	
	public static final Map<String, ConfigData> Application_Config_Cache = new HashMap<String, ConfigData>(); 
	
	public static final ExecutorService threadService = Executors.newFixedThreadPool(15);
	
	private static long timer = 0;
	
	private static long count=0;
	
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
		
	public static String getProperty(String key) {
		return (String)prop.get(key);
	}
	
	public static ConfigData appConfig(){
		return ApplicationHelper.Application_Config_Cache.get("app");
	}
	public static ConfigData modeConfig(String modelId){
		return ApplicationHelper.Application_Config_Cache.get(modelId);
	}
	public static double getPositionId(String modelID,String key){
		String val = modeConfig(modelID).getKeyValueConfigs().get(key);
		if(val==null){
			throw new RuntimeException("Position must be set up:"+key);
		}
		return Double.valueOf(val);
		
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
			CacheService.addDateRecordingCache();
			
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
				CacheService.PRICE_LIST.put(Double.valueOf(ltpObject.getInstrumentToken()), ltpObject.getLtp().doubleValue());
				/**
				 * Need meta data in place before it is triggered.
				 */
				//System.out.println(count++ +","+ltpObject.getInstrumentToken()+","+ltpObject.getLtp().doubleValue());
				
			}
			
		}
		
		
	}
	
	public static void main(String[] args) {
		List<GeneralObject> object = getNiftyData("04-12-2017");
		List<GeneralObject> PEobject = getCE_PEData("PE", "10000", "04-12-2017");
		List<GeneralObject> CEobject = getCE_PEData("CE", "10200", "04-12-2017");
		
		double support=2.0;
		double resistance=3.0;
		
		System.out.println();
	}

}
