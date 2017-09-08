package com.mod;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class ApplicationHelper {
	
	public static final ExecutorService threadService = Executors.newFixedThreadPool(5);
	
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

}
