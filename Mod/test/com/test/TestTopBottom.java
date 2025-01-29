package com.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mod.interfaces.BusinessInterface;
import com.mod.interfaces.KitePositionQueryResponse;
import com.mod.objects.KitePositionDataLayer2;
import com.mod.objects.EnumPositionStatus;
import com.mod.objects.PositionalData;
import com.mod.objects.StopLossType;
import com.mod.process.models.CacheService;
import com.mod.process.models.TopBottomLongModel;
import com.mod.support.ApplicationHelper;
import com.mod.support.Candle;
import com.mod.support.ConfigData;
import com.mod.support.KiteCandleData;
import com.mod.support.XMLParsing;
import com.test.inf.TestKiteInterface;

public class TestTopBottom {
	
	private static final double expectedSLPercen = 1-(2.5/100);
	
	@Before
	public void before() {
		System.out.println("triggering");
		ConfigData configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/app.config");
		ApplicationHelper.Application_Config_Cache.put("app", configData);

		configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/genwsclient.config");
		ApplicationHelper.Application_Config_Cache.put("mode1", configData);
		
		configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/pmodel5.config");
		ApplicationHelper.Application_Config_Cache.put("pmodel5", configData);
		
		configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/pmodel6.config");
		ApplicationHelper.Application_Config_Cache.put("pmodel6", configData);

		configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/topbottom1.config");
		ApplicationHelper.Application_Config_Cache.put("topbottom1", configData);
		
		configData = XMLParsing.readAppConfig("C:/Users/Vihaan/git/repo1/Mod/resource/openhl.config");
		ApplicationHelper.Application_Config_Cache.put("topbottom1", configData);
		
		//Set positional data.
		PositionalData data = new PositionalData();
		data.setBuyPrice(31000.00);
		data.setKey(CacheService.BN_KEY);
		//data.setTradingSymbol("NIFTY BANK");
		data.setStatus(EnumPositionStatus.InPoistionLong);
		data.setBuyQuantity(200);
		
		CacheService.positionalData.add(data);
	}

	@Test	
	public void testInitial() {
		
	}
	

}
