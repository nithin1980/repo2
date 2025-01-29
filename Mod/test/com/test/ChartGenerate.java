package com.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mod.interfaces.KitePositionQueryResponse;
import com.mod.objects.EnumPositionStatus;
import com.mod.objects.KitePositionDataLayer2;
import com.mod.objects.PositionalData;
import com.mod.process.models.CacheService;
import com.mod.support.ApplicationHelper;

public class ChartGenerate {
	
	/**
	 * 
	 * @param args
	 * 
	 * labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],
	 * data: [15, 21, 13, 5, 21, 23],
	 * const ctx2 = document.getElementById('myChart2').getContext('2d');
	 */

	public static void main(String[] args) {
		Scanner reader = null;
		List<String> lines = new ArrayList<String>();
		
		String txt = null;
		Map<String, List<String>> groupedData = new HashMap<String, List<String>>();
		try {
			File file = new File("C:/data/testdata.txt");
			
			reader = new Scanner(file);
			String prevTxt = null;
			while(reader.hasNextLine()) {
				txt = reader.nextLine();
				if(txt.contains("openhl") && txt.contains("CP") && txt.contains("ESL")) {
					if(!prevTxt.equals(txt)) {
						lines.add(txt);
					}
					
				}
				prevTxt = txt;
				
			}
			
			Iterator<String> itr = lines.iterator();
			
			 
			while(itr.hasNext()) {
				txt = itr.next();
				txt = txt.replace(":", ",");
				String[] dtArrs = txt.split("\\,");
				
				if(groupedData.get(dtArrs[1])==null) {
					groupedData.put(dtArrs[1], new ArrayList<String>());
					groupedData.get(dtArrs[1]).add(dtArrs[1]+","+dtArrs[5]+","+dtArrs[7]+","+dtArrs[8]+","+dtArrs[9]+","+dtArrs[10]+","+dtArrs[11]+","+dtArrs[12]);
					
				}else {
					groupedData.get(dtArrs[1]).add(dtArrs[1]+","+dtArrs[5]+","+dtArrs[7]+","+dtArrs[8]+","+dtArrs[9]+","+dtArrs[10]+","+dtArrs[11]+","+dtArrs[12]);
					
				}
				
			}
			
			Iterator<String> keys =  groupedData.keySet().iterator();
			List<StringBuilder> builders = new ArrayList<StringBuilder>();
			
			for(int i=0;i<700;i++) {
				builders.add(new StringBuilder());
			}
			
			
			while(keys.hasNext()) {
				txt =  keys.next();
				
				int size = groupedData.get(txt).size();
				for(int i=0;i<700;i++) {
					if(i>=size) {
						builders.get(i).append(",,,");
					}else {
						builders.get(i).append(groupedData.get(txt).get(i)+",");
					}
				}
				
			}

			Iterator<StringBuilder> itr2 =  builders.iterator();
			
//			while(itr2.hasNext()) {
//				System.out.println(itr2.next());
//			}


			
			reader.close();
			
			
			/***
			 * Reporting chart data
			 */
			
			
			file = new File("C:/Users/Vihaan/git/repo1/Mod/WebContent/reportingtemplate.txt");
			
			reader = new Scanner(file);
			StringBuilder reportTemplate = new StringBuilder();
			while(reader.hasNextLine()) {
				reportTemplate.append(reader.nextLine()+"\r");
				
			}
			
			StringBuilder copy = null;
			StringBuilder builder = new StringBuilder();
			StringBuilder builder2 = new StringBuilder();
			
			 keys =  groupedData.keySet().iterator();
			 
			 int objectId = 0;
			 
				while(keys.hasNext()) {
					 txt =  keys.next();
					
					 copy = new StringBuilder(reportTemplate);
					
					 replaceAll(copy, "#object_id#", ""+(objectId+1));
					 replaceAll(copy, "#stock_code#", txt);
					 objectId++;
					
					 Iterator<String> itr3 =  groupedData.get(txt).iterator();
					 StringBuilder col1 = new StringBuilder();
					 StringBuilder col2 = new StringBuilder();
					 StringBuilder col3 = new StringBuilder();
					 StringBuilder col4 = new StringBuilder();
					 StringBuilder col5 = new StringBuilder();
					 StringBuilder col6 = new StringBuilder();
					 StringBuilder col7 = new StringBuilder();
					 
					 int count=0;
					 while(itr3.hasNext()) {
						 String[] dtArrs2 = itr3.next().split("\\,"); 
						 col1.append(dtArrs2[1]+",");
						 col2.append(dtArrs2[2]+",");
						 col3.append(dtArrs2[3]+",");
						 col4.append(dtArrs2[4]+",");
						 col5.append(dtArrs2[5]+",");
						 col6.append(dtArrs2[6]+",");
						 col7.append(dtArrs2[7]+",");
						 
						 count++;
					 }
					 
					 replaceAll(copy, "#column1_data#", col1.toString());
					 replaceAll(copy, "#column2_data#", col2.toString());
					 replaceAll(copy, "#column3_data#", col3.toString());
					 replaceAll(copy, "#column4_data#", col4.toString());
					 replaceAll(copy, "#column5_data#", col5.toString());
					 replaceAll(copy, "#column6_data#", col6.toString());
					 replaceAll(copy, "#column7_data#", col7.toString());
					 
					 
					for(int j=0;j<count;j++) {
						builder.append("\'"+j+"\',");
					}
					replaceAll(copy, "#xrow_count#", builder.toString());
					
					builder= new StringBuilder();
					
					builder2.append(copy+"\r\r");

					
				}

			
			
			
			System.out.println(builder2);
//			
			
			
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally{
			
				if(reader!=null){
					reader.close();
				}
		}

	}
	
	public static void replaceAll(StringBuilder builder, String from, String to) {
	    int index = builder.indexOf(from);
	    while (index != -1) {
	        builder.replace(index, index + from.length(), to);
	        index += to.length(); // Move to the end of the replacement
	        index = builder.indexOf(from, index);
	    }
	}
}
