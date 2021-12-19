package com.test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class TestSix {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Double> data =  cal();
		
		double price = 25.25*400;
		
		//0 to .5 = 15%
		//.5 to 1 = 25
		//1 to 1.5 = 30%
		//.5 to 2= 40
		// 2 to 2.5= 55
		//2.5 to 3 = 70
		//3  to 3.5 = 90
		// 3.5 to 4= 100%
		// 4 - 4.5 = 1.5
		// 4.5- 5= 200
		//5 to 5.5 = 2.5
		
		//>0 15%
		//<0 20%
		
		double pos=0;
		double neg=0;
		for(int i=0;i<data.size();i++) {
			if(data.get(i)>0) {
				pos = (price*.15)+pos;
			}
			if(data.get(i)<0) {
				neg= (price*.20)+neg;
			}
			
		}
		
		System.out.println(pos+"----"+neg+"----"+(pos-neg));
		System.out.println();
		
	}
	
	public static List<Double> cal() {
		Iterable<CSVRecord> records = null;
		Reader reader = null;
		
		
		List<Double> config = new ArrayList<>();;
		
		try {
			reader = new FileReader("C:/data/testdata.txt");
			records = CSVFormat.EXCEL.parse(reader);
			
			for(CSVRecord record:records){
				if(record.getRecordNumber()>1){
					
					int size = record.size();
					for(int i=0;i<size;i++){
						
						config.add(Double.valueOf(record.get(i)));
					}
					
					
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

		return config;
	}

}
