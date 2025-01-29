package com.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OfficeTest {

	static Map<String, String> policyData = new HashMap<String, String>();
	static List<String> ref = new ArrayList<String>();
			
	public static void main(String[] args) {
		BufferedReader reader;
		
//		try {
//			reader = new BufferedReader(new FileReader("C:/office/policyfile.csv"));
//			String line = reader.readLine();
//
//			while (line != null) {
//				
//				pop(line);
//				// read next line
//				line = reader.readLine();
//				
//			}
//
//			reader.close();
//			
//			System.out.println(" Policy records:"+policyData.size());
//			
//			reader = new BufferedReader(new FileReader("C:/office/reference.csv"));
//			line = reader.readLine();
//			while (line != null) {
//				
//				ref.add(line);
//				line = reader.readLine();
//				
//			}
//
//			reader.close();
//			
//			System.out.println(" Reference records:"+ref.size());
//			
//			for(String refVal:ref) {
//				policyData.remove(refVal);
//			}
//			
//			System.out.println("Policy data post removal:"+policyData.size());
//
//			
//			FileWriter myWriter = new FileWriter("C:/office/output.csv");
//			
//			Set<String> policyKeys = policyData.keySet();
//			
//			for(String key:policyKeys) {
//				myWriter.write(policyData.get(key)+"\r\n");
//			}
//			
//		    
//		    myWriter.close();			
//			
//			
//			
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
		
		writeTest();
	}
	
	private static void writeTest() {
		try {
			FileWriter myWriter = new FileWriter("C:/office/output.csv");
			
			
			for(int i=0;i<4;i++) {
				myWriter.append(i+"\r\n");
			}
			
			
			myWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
	}
	
	private static void pop(String line) {
		
		String keyVal = line.split("\\;")[0];
		
		
		if(keyVal.startsWith("\"") && keyVal.endsWith("\"")) {
			String key = keyVal.split("\"")[1];
			policyData.put(key, line);
		}
	}

}