package com.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mod.support.ApplicationHelper;
import com.mod.support.GeneralJsonObject;
import com.mod.support.KiteCandleData;

public class TestSix {

	public static void main(String[] args) {
		
			TestSix.cal();
			//need an array.
			
		
	}
	
	public static void cal() {
		ApplicationHelper.getKiteProcessedCandles();		
	}

}
