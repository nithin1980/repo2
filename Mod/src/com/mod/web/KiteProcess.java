package com.mod.web;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mod.datafeeder.DataFeed;
import com.mod.interfaces.KiteGeneralWebSocketClient;
import com.mod.interfaces.KiteStockConverter;
import com.mod.objects.CacheMetaData;
import com.mod.process.models.CacheService;
import com.mod.process.models.DashBoard;
import com.mod.support.ApplicationHelper;
import com.mod.support.ConfigData;
import com.mod.support.GeneralJsonObject;
import com.mod.support.XMLParsing;

public class KiteProcess {
	
	private String websocketKey;
	
	private String destinationUrl;
	
	public void startProcess(){
		
		getkeys();
		
		ConfigData configData = XMLParsing.readAppConfig("C:/Users/nkumar/git/repo1/master/Mod/resource/app.config");

		configData.getKeyValueConfigs().put("Sec-WebSocket-Key",websocketKey);
		configData.getKeyValueConfigs().put("destination_url",destinationUrl);
		
		System.out.println("websocket key:"+configData.getKeyValueConfigs().get("Sec-WebSocket-Key"));
		System.out.println("destination url:"+configData.getKeyValueConfigs().get("destination_url"));
		
		ApplicationHelper.Application_Config_Cache.put("app", configData);

		configData = XMLParsing.readAppConfig("C:/Users/nkumar/git/repo1/master/Mod/resource/genwsclient.config");
		ApplicationHelper.Application_Config_Cache.put("mode1", configData);
		
		configData = XMLParsing.readAppConfig("C:/Users/nkumar/git/repo1/master/Mod/resource/pmodel5.config");
		ApplicationHelper.Application_Config_Cache.put("pmodel5", configData);
		
		configData = XMLParsing.readAppConfig("C:/Users/nkumar/git/repo1/master/Mod/resource/pmodel6.config");
		ApplicationHelper.Application_Config_Cache.put("pmodel6", configData);

		configData = XMLParsing.readAppConfig("C:/Users/nkumar/git/repo1/master/Mod/resource/pmodel7.config");
		ApplicationHelper.Application_Config_Cache.put("pmodel7", configData);
		
		KiteStockConverter.build();
		
		CacheService.clearDateDataRecord();
		CacheService.addMetaDataToDateRecording("group1", metadata());
		CacheService.initializeDataArray(initialSetup());
		
		setApplicableTime();
		
		DashBoard.setKiteGenerlWebSocketClient(new KiteGeneralWebSocketClient());
		DashBoard.kiteWebSocketClient.connect();
		
	}
	public void dumpData(){
		System.out.println("Saving data....");
		CacheService.dumpDateRecording();
		System.out.println(DashBoard.positionMap);
	}
	public void stopProcess(){
		DashBoard.kiteWebSocketClient.closeSession();
	}
	private TDoubleList initialSetup(){
		TDoubleList list = new TDoubleArrayList();
		list.add(Double.valueOf(DataFeed.incrementTime()));
		
		String[] values = ApplicationHelper.subscribeValues();
		for(int i=0;i<values.length;i++){
			list.add(Double.valueOf(values[i]));
		}
		
		return list;
	}
	
	private CacheMetaData metadata(){
		List<String> metadata = new ArrayList<String>();
		metadata.add(String.valueOf(DataFeed.START_TIME_STRING));
		return new CacheMetaData(metadata);
	}
	
	private void setApplicableTime(){
		String url =  appConfig().getKeyValueConfigs().get("time_url");
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(url);
		
		try {
			HttpResponse response = client.execute(get);
			
			BufferedReader rd = new BufferedReader(
			        new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			GeneralJsonObject jsonObject = ApplicationHelper.getObjectMapper().readValue(result.toString(), GeneralJsonObject.class);
			
			System.out.println(jsonObject.getDateTime_24HR());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Not able to get time"+e);
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Not able to get time"+e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Not able to get time"+e);
		}		
	}
	private static ConfigData appConfig(){
		return ApplicationHelper.Application_Config_Cache.get("app");
	}
	
	private void getkeys(){
		WebDriver driver =  null;
		Map<String,String> windowHandles = new HashMap<String, String>();
		
		System.setProperty("webdriver.chrome.driver", "C:/Users/nkumar/git/repo1/master/Mod/WebContent/WEB-INF/lib/chromedriver.exe");
	    DesiredCapabilities capabilities = new DesiredCapabilities();
		
	    ChromeOptions options = new ChromeOptions();
	    options.addArguments("start-maximized");
	    capabilities.setCapability(ChromeOptions.CAPABILITY, options);
	    
	    capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
	    
	    LoggingPreferences logPrefs = new LoggingPreferences();
	    logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
	    capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);    
	    
	    driver = new ChromeDriver(capabilities);
	    
	    String baseUrl = "https://kite.zerodha.com/";
	    driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		
	    driver.navigate().to(baseUrl + "/");
	    windowHandles.put("dashboard",driver.getWindowHandle());
	    
	    /**
	     * Check first if the login page appears.
	     */
	    
	    driver.findElement(By.name("user_id")).clear();
	    driver.findElement(By.name("user_id")).sendKeys("DV4051");
	    driver.findElement(By.id("inputtwo")).clear();
	    driver.findElement(By.id("inputtwo")).sendKeys("manipal110");
	    driver.findElement(By.name("login")).click();
	    
	    //find the 2f questions
	    String question1 = driver.findElements(By.className("first")).get(0).getText();
	    String question2 = driver.findElements(By.className("second")).get(0).getText();
	    
	    String answer1 = "manipal";
	    String answer2 = "manipal";
	    if(question1.contains("Which year did you complete your graduation")){
	    	answer1 = "2002";
	    }else if(question2.contains("Which year did you complete your graduation")){
	    	answer2 = "2002";
	    }
	    
	    driver.findElement(By.name("answer1")).clear();
	    driver.findElement(By.name("answer1")).sendKeys(answer1);
	    driver.findElement(By.name("answer2")).clear();
	    driver.findElement(By.name("answer2")).sendKeys(answer2);
	    driver.findElement(By.name("twofa")).click();
	    
	    //go to page 3
	    driver.findElement(By.linkText("3")).click();

	    
	    
	    LogEntries les = driver.manage().logs().get(LogType.PERFORMANCE);
	    try {
			for (LogEntry le : les) {
			    //System.out.println(le.getMessage());
				if(le.getMessage().contains("Network.webSocketHandshakeResponseReceived")){
					GeneralJsonObject jsonObject = ApplicationHelper.getObjectMapper().readValue(le.getMessage(), GeneralJsonObject.class);
					this.websocketKey = jsonObject.websocketKey();
					this.destinationUrl = jsonObject.url();
					break;
				}
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	    
	    
	    
	}
}
