package com.mod.support;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.mod.datafeeder.DataFeed;
import com.mod.interfaces.IStreamingQuoteParser;
import com.mod.interfaces.KiteStockConverter;
import com.mod.interfaces.StreamingQuote;
import com.mod.interfaces.StreamingQuoteModeFull;
import com.mod.interfaces.StreamingQuoteModeLtp;
import com.mod.interfaces.StreamingQuoteParserModeFull;
import com.mod.process.models.CacheService;
import com.mod.process.models.ProcessModelAbstract;
import com.mod.process.models.ProcessingBlock;

public class ApplicationHelper {
	
	public static final Map<String, ConfigData> Application_Config_Cache = new HashMap<String, ConfigData>(); 
	
	public static final ExecutorService threadService = Executors.newFixedThreadPool(15);
	

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
			//Should be asynchronous
			int modelSize = processingModels.size();
			
			for(int i=0;i<modelSize;i++){
				/**
				 * @TODO This should be asynchrous
				 */
				if(processingModels.get(i).completedProcess){
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
				
				
			}
			
		}
		
		
	}	

}
