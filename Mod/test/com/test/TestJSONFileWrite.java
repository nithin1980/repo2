package com.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mod.support.ApplicationHelper;
import com.mod.support.Candle;
import com.mod.support.KiteCandleData;

public class TestJSONFileWrite {
	
	public static void main(String[] args) {
		Scanner reader = null;
	List<Candle> candles = new ArrayList<Candle>();
		
//		try {
//			File file = new File("C:/data/testdata1.txt");
//			
//			reader = new Scanner(file);
//			
//				String data = reader.nextLine();
//				KiteCandleData candleData =  ApplicationHelper.getObjectMapper().readValue(data, KiteCandleData.class);
//				
//			
//			
//			reader.close();
//			
////			File writefile = new File("C:/data/testdata1.txt");
////			
////			ApplicationHelper.getObjectMapper().writeValue(writefile, candleData);
//			
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (JsonParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally{
//			
//				if(reader!=null){
//					reader.close();
//				}
//		}
		

		Map<String, String> val = new HashMap<String, String>();
		
		val.put("some_1", "sometimes");
		val.put("some_2", "sometimes_2");
		
		try {
			String out = ApplicationHelper.getObjectMapper().writeValueAsString(val);
			HashMap readValue = ApplicationHelper.getObjectMapper().readValue(out, HashMap.class);
			HashMap<String, String> inp =  readValue;
			System.out.println(inp);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
