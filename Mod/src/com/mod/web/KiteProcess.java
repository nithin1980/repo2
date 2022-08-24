package com.mod.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

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
import com.mod.objects.GroupPosition;
import com.mod.process.models.CacheService;
import com.mod.process.models.DashBoard;
import com.mod.support.ApplicationHelper;
import com.mod.support.ConfigData;
import com.mod.support.GeneralJsonObject;
import com.mod.support.XMLParsing;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

public class KiteProcess {

private String websocketKey;

private String destinationUrl;


/***
 * Websocket data
 * 
 * LoggingPreferences loggingprefs = new LoggingPreferences();
loggingprefs.enable(LogType.PERFORMANCE, Level.ALL);

ChromeOptions options = new ChromeOptions();
options.setCapability("goog:loggingPrefs", loggingprefs);
options.addArguments("--enable-devtools-experiments", "--force-devtools-available", "--debug-devtools");

ChromeDriver chromeDriver = new ChromeDriver(options);
DevTools devTools = chromeDriver.getDevTools();
devTools.createSession();
devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));`

devTools.addListener(Network.webSocketFrameSent(), webSocketFrameSent -> printData(webSocketFrameSent));
devTools.addListener(Network.webSocketFrameReceived(), webSocketFrameReceived -> printData(webSocketFrameReceived));
 * 
 * 
 * 
 * 
 */
 
public void startPE(){
 try {
  KiteService.orderPE();
 } catch (RuntimeException e) {
  // TODO Auto-generated catch block
  e.printStackTrace();
 }
}
public void startCE(){
 try {
  KiteService.orderCE();
 } catch (RuntimeException e) {
  // TODO Auto-generated catch block
  e.printStackTrace();
 }
}
public void bothPE_CE(){
 KiteService.orderBoth();
}
public void bothEquals(){
 KiteService.orderEquals();
}
public void combination(){
 KiteService.orderCombination();
}
public void stopPE(){
 
}
public void stopCE(){
 
}

public void startProcess(){
 
 getkeys();
 System.out.println(ApplicationHelper.getProperty("config.location")+"app.config");;
 ConfigData configData = XMLParsing.readAppConfig(ApplicationHelper.getProperty("config.location")+"app.config");

 configData.getKeyValueConfigs().put("Sec-WebSocket-Key",websocketKey);
 configData.getKeyValueConfigs().put("destination_url",destinationUrl);
 
 System.out.println("websocket key:"+configData.getKeyValueConfigs().get("Sec-WebSocket-Key"));
 System.out.println("destination url:"+configData.getKeyValueConfigs().get("destination_url"));
 
 ApplicationHelper.Application_Config_Cache.put("app", configData);

//  configData = XMLParsing.readAppConfig(ApplicationHelper.getProperty("config.location")+"genwsclient.config");
//  ApplicationHelper.Application_Config_Cache.put("mode1", configData);
//  
//  configData = XMLParsing.readAppConfig(ApplicationHelper.getProperty("config.location")+"pmodel5.config");
//  ApplicationHelper.Application_Config_Cache.put("pmodel5", configData);
//  
//  configData = XMLParsing.readAppConfig(ApplicationHelper.getProperty("config.location")+"pmodel6.config");
//  ApplicationHelper.Application_Config_Cache.put("pmodel6", configData);
//
//  configData = XMLParsing.readAppConfig(ApplicationHelper.getProperty("config.location")+"pmodel7.config");
//  ApplicationHelper.Application_Config_Cache.put("pmodel7", configData);
//
//  configData = XMLParsing.readAppConfig(ApplicationHelper.getProperty("config.location")+"pmodel10.config");
//  ApplicationHelper.Application_Config_Cache.put("pmodel10", configData);

 ApplicationHelper.placeConfig("genwsclient");
 ApplicationHelper.placeConfig("pmodel5");
 ApplicationHelper.placeConfig("pmodel6");
 ApplicationHelper.placeConfig("pmodel7");
 ApplicationHelper.placeConfig("pmodel10");
 ApplicationHelper.placeConfig("pmodel11");
 ApplicationHelper.placeConfig("pmodel12");
 ApplicationHelper.placeConfig("pmodel13");
 ApplicationHelper.placeConfig("pmodel14");
 ApplicationHelper.placeConfig("pmodel15");
 
 setDashboardPosition();
 
 KiteStockConverter.build();
 
// CacheService.clearDateDataRecord();
// CacheService.addMetaDataToDateRecording("group1", metadata());
// CacheService.initializeDataArray(initialSetup());
 
 //setApplicableTime();
 DashBoard.enableStatusCheck();
 DashBoard.setKiteGenerlWebSocketClient(new KiteGeneralWebSocketClient());
 DashBoard.kiteWebSocketClient.connect();
 
}
private void setDashboardPosition(){
 for(int i=1;i<20;i++){
  DashBoard.positionMap.put("pmodel"+i, new GroupPosition());
 }
}
public void dumpData(){
 System.out.println("Saving data....");
// CacheService.dumpDateRecording();
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

//private void setApplicableTime(){
// String url =  appConfig().getKeyValueConfigs().get("time_url");
// HttpClient client = HttpClientBuilder.create().build();
// HttpGet get = new HttpGet(url);
// GeneralJsonObject jsonObject = null;
// try {
//  HttpResponse response = client.execute(get);
// 
//  BufferedReader rd = new BufferedReader(
//          new InputStreamReader(response.getEntity().getContent()));
//
//  StringBuffer result = new StringBuffer();
//  String line = "";
//  while ((line = rd.readLine()) != null) {
//   result.append(line);
//  }
//  jsonObject = ApplicationHelper.getObjectMapper().readValue(result.toString(), GeneralJsonObject.class);
// 
//  System.out.println(jsonObject.getDateTime_24HR());
// } catch (ClientProtocolException e) {
//  // TODO Auto-generated catch block
//  e.printStackTrace();
//  throw new RuntimeException("Not able to get time"+e);
// } catch (UnsupportedOperationException e) {
//  // TODO Auto-generated catch block
//  e.printStackTrace();
//  throw new RuntimeException("Not able to get time"+e);
// } catch (IOException e) {
//  // TODO Auto-generated catch block
//  e.printStackTrace();
//  throw new RuntimeException("Not able to get time"+e);
// }
// 
// if(jsonObject.getDateTime_24HR()==null){
//  throw new RuntimeException("Not able to get time,as the value is empty. Process stopped");
// }
// 
// DashBoard.checkedTime=jsonObject.getDateTime_24HR();
// 
// 
// 
//}
private static ConfigData appConfig(){
 return ApplicationHelper.Application_Config_Cache.get("app");
}

private void timeCheck(String time){
 
}

private void getkeys(){
 WebDriver driver =  null;
 Map<String,String> windowHandles = new HashMap<String, String>();
 
 System.setProperty("webdriver.chrome.driver", ApplicationHelper.getProperty("driver.location")+"Mod/WebContent/WEB-INF/lib/chromedriver.exe");
    DesiredCapabilities capabilities = new DesiredCapabilities();
 
    ChromeOptions options = new ChromeOptions();
    options.addArguments("start-maximized");
    capabilities.setCapability(ChromeOptions.CAPABILITY, options);
   
    capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
   
    LoggingPreferences logPrefs = new LoggingPreferences();
    logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
    capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);    
   
    driver = new ChromeDriver(capabilities);
   
    String baseUrl = "https://kite.zerodha.com";
    driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
 
    driver.navigate().to(baseUrl + "/");
    windowHandles.put("dashboard",driver.getWindowHandle());
   
    /**
     * Check first if the login page appears.
     */
   
   
   
    //driver.findElements(By.cssSelector("input")).get(0).clear();
    driver.findElements(By.cssSelector("input")).get(0).sendKeys("DV4051");
    //driver.findElements(By.cssSelector("input")).get(1).clear();
    driver.findElements(By.cssSelector("input")).get(1).sendKeys("manipal111");
   
//     driver.findElement(By.name("user_id")).clear();
//     driver.findElement(By.name("user_id")).sendKeys("DV4051");
//     driver.findElement(By.id("inputtwo")).clear();
//     driver.findElement(By.id("inputtwo")).sendKeys("manipal110");
    driver.findElements(By.cssSelector("button")).get(0).click();
    //driver.findElement(By.name("login")).click();
   
    try {
  Thread.sleep(3000);
 } catch (InterruptedException e1) {
  // TODO Auto-generated catch block
  e1.printStackTrace();
 }
   
    //find the 2f questions
//     String question1 = driver.findElements(By.cssSelector("input")).get(0).getAttribute("label");
//     String question2 = driver.findElements(By.cssSelector("input")).get(1).getAttribute("label");
//    
//     String answer1 = "manipal";
//     String answer2 = "manipal";
//     if(question1.contains("Which year did you complete your graduation")){
//      answer1 = "2002";
//     }else if(question2.contains("Which year did you complete your graduation")){
//      answer2 = "2002";
//     }
   
    //driver.findElements(By.cssSelector("input")).get(0).sendKeys(answer1);
   
    driver.findElements(By.cssSelector("input")).get(0).sendKeys("118143");
    //driver.findElements(By.cssSelector("input")).get(1).sendKeys(answer2);
   
    driver.findElements(By.cssSelector("button")).get(0).click();
   
    //go to page 3
  //  driver.findElement(By.linkText("3")).click();

    try {
  Thread.sleep(5000);
 } catch (InterruptedException e1) {
  // TODO Auto-generated catch block
  e1.printStackTrace();
 }
   
    
    
   LogEntries les = driver.manage().logs().get(LogType.PERFORMANCE);
   
   
//   String method = messageJSON.getJSONObject("message").getString("method");
//   if(method.equalsIgnoreCase("Network.webSocketFrameSent")){
//       System.out.println("Message Sent: " + messageJSON.getJSONObject("message").getJSONObject("params").getJSONObject("response").getString("payloadData"));
//   }else if(method.equalsIgnoreCase("Network.webSocketFrameReceived")){
//       System.out.println("Message Received: " + messageJSON.getJSONObject("message").getJSONObject("params").getJSONObject("response").getString("payloadData"));
//   }
//   } catch (JSONException e) {
//       e.printStackTrace();
//   }   

    try {
  for (LogEntry le : les) {
	  
      System.out.println(le.getMessage());
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
  System.out.println();  
   
}
}
