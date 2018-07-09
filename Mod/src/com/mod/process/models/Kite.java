package com.mod.process.models;

import static org.junit.Assert.fail;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mod.support.ApplicationHelper;
import com.mod.support.GeneralJsonObject;

public class Kite {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();
  
  
  static List<String> targetList = new ArrayList<String>();

  static int recordCount=1;
  static Map<String,String> windowHandles = new HashMap<String, String>();
  
  static final String dashboard = "dashboard";
  static final String position = "position";
  static final String orders = "orders";
  
 // private BrowserMobProxy proxy;
  
  @Before
  public void setUp() throws Exception {
	getTargetList();  
	//System.setProperty("webdriver.gecko.driver", "C:/Users/nkumar/git/repo1/master/Mod/WebContent/WEB-INF/lib/geckodriver.exe");
	System.setProperty("webdriver.chrome.driver", "C:/Users/nkumar/git/repo1/master/Mod/WebContent/WEB-INF/lib/chromedriver.exe");
	
	// start the proxy
//	proxy = new BrowserMobProxyServer();
//	proxy.setTrustAllServers(true);
//	
//    proxy.start(0);
//    int port = proxy.getPort();
//    System.out.println(port);
//
//    // get the Selenium proxy object
//    Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
    

    // configure it as a desired capability
    DesiredCapabilities capabilities = new DesiredCapabilities();
    //capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
	
    ChromeOptions options = new ChromeOptions();
    options.addArguments("start-maximized");
    capabilities.setCapability(ChromeOptions.CAPABILITY, options);
    
    capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
    //driver = new FirefoxDriver(capabilities);
    
    LoggingPreferences logPrefs = new LoggingPreferences();
    logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
    capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);    
    
    driver = new ChromeDriver(capabilities);
    
    // enable more detailed HAR capture, if desired (see CaptureType for the complete list)
//    proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);    
//    proxy.newHar("zerodha");
    
    baseUrl = "https://kite.zerodha.com/";
    driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
  }

  @Test
  public void testKite() throws Exception {
	  
	    
	    
	  
	    //driver.navigate().to(baseUrl + "/");
	    driver.navigate().to(baseUrl);
	    windowHandles.put(dashboard,driver.getWindowHandle());
	    
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
	    
	    //Which year did you complete your graduation? (e.g. 2000, 1990 etc)

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

	    /**
	     * Triggering javascript
	     */
//	    String scriptToExecute = "var performance = window.performance || window.mozPerformance || window.msPerformance || window.webkitPerformance || {}; var network = performance.getEntries() || {}; return network;";
//	    String netData = ((JavascriptExecutor)driver).executeScript(scriptToExecute).toString();
	    
	    
	    LogEntries les = driver.manage().logs().get(LogType.PERFORMANCE);
	    for (LogEntry le : les) {
	        //System.out.println(le.getMessage());
	    	if(le.getMessage().contains("Network.webSocketHandshakeResponseReceived")){
	    		GeneralJsonObject jsonObject = ApplicationHelper.getObjectMapper().readValue(le.getMessage(), GeneralJsonObject.class);
	    		System.out.println(jsonObject.websocketKey()+"--"+jsonObject.url());
	    	}
	    }	    
	    System.out.println();
//	    Har har = proxy.getHar();	
//	    List<HarEntry> list = har.getLog().getEntries();
//	    for(int i=0;i<list.size();i++){
//	    	System.out.println(list.get(i).getRequest().getUrl());
//	    	if(list.get(i).getRequest().getUrl().contains("websocket")){
//	    		HarEntry ent = list.get(i);
//	    		System.out.println();
//	    	}
//	    }	    
//	    System.out.println();
	    //--------------------------------------------------------------
	    /**
	     *  Experiments are here.
	     *  capturing request using firebug
	     *  https://eveningsamurai.wordpress.com/2013/06/29/capturing-network-traffic-using-selenium-webdriverfirebug/
	     */
	    
//	    Set<Cookie> allCookies = driver.manage().getCookies();
//	    
//	    Iterator<Cookie> itr1 =  allCookies.iterator();
//	    while(itr1.hasNext()){
//	    	Cookie cookie = itr1.next();
//	    	System.out.println(cookie.getName()+"--"+cookie.getValue());
//	    }
	    //newTab(position,baseUrl+"/apps", driver);
	    
	    //driver.switchTo().window(windowHandles.get(dashboard));

//		For another window to monitor positions/order	    
//	    WebDriver secondDriver = new FirefoxDriver();
//	    secondDriver.navigate().to(baseUrl + "/positions");

	    //-------------------------------------------------------------------
	    
    	/**
    	 * Get the traffic
    	 */
    	    
	    
	    List<WebElement> itemList = driver.findElements(By.className("item"));
	    //WebDriverWait wait = new WebDriverWait(driver, 3);
	    
	    if(itemList!=null && !itemList.isEmpty()){
	    	Iterator<WebElement> itr = itemList.iterator();
	    	WebElement rootListElement = null;
	    	WebElement childElement1 = null;
	    	
	    	String symbolText = null;
	    	Map<String,WebElement> approvedList = new HashMap<String,WebElement>();
	    	
//	  	  targetList.add("NIFTY JUL 9700 CE");
//		  targetList.add("NIFTY 50");
//		  targetList.add("NIFTY JUL 9600 CE");	    	
	    	
	    	while(itr.hasNext()){
	    		rootListElement = itr.next();
	    		//wait.until(ExpectedConditions.elementToBeClickable(rootListElement.findElement(By.className("symbol"))));
	    		if(rootListElement!=null 
	    				&& isElementPresent(By.className("symbol"))
	    				//run the check until it is equal to the number of items i am looking for.
	    				&& approvedList.size()<targetList.size()){
	    			childElement1 = rootListElement.findElement(By.className("symbol"));
	    			symbolText = childElement1.getText();
	    			if(symbolText!=null 
	    					&& targetList.contains(symbolText.trim())){
	    				//System.out.println(symbolText);
	    				approvedList.put(symbolText,rootListElement);
	    			}
	    		}
	    	}
	    	

	    	
	    	if(approvedList.isEmpty()){
	    		throw new RuntimeException("No items to scan");
	    	}
	    	String price = null;
	    	StringBuilder builder = new StringBuilder();
	    	System.out.println(targetList.get(0)+","+targetList.get(1)+","+targetList.get(2));
	    	long t = System.currentTimeMillis()+50000;
	    	//Run this continously
	    	while(true){
	    		
		    	for(int i=0;i<approvedList.size();i++){
		    		price = priceInfo(approvedList.get(targetList.get(i)));
		    		builder.append(price+",");
		    	}
		    	builder.insert(0, Instant.now().toString()+",");
		    	createRecord(builder.toString());
		    	builder.delete(0, builder.length());
//		    	if(System.currentTimeMillis()<t){
//		    		Thread.sleep(2000);
//		    	}
		    	
	    	}
	    	
	    }
	    
	    
	    System.out.println();
	    
	    /**
	     * notification icon class:menu-right-wrap>right-menu>icon-notification
	     * alert table class: menu-right-wrap>right-menu>alerts-table this is a full table. 
	     * need to apply wait for the table to appear.
	     */
	    
//	    driver.findElement(By.xpath("//ul[@id='instruments']/li/div/span/button[3]")).click();
//	    driver.findElement(By.xpath("//ul[@id='instruments']/li[2]/div/span/button[3]")).click();
//	    driver.findElement(By.xpath("//ul[@id='instruments']/li[5]/div/span/button")).click();
  }
  
  private void createRecord(String data){
	  CacheService.write(String.valueOf(recordCount), data);
	  recordCount++;
	  System.out.println(data);
  }
  private String priceInfo(WebElement rootListElement){
	  /**
	   * Need error and wait time handling.
	   */
	  return rootListElement.findElement(By.className("price")).findElement(By.className("ng-scope")).getText();
  }
  private String depthInfo(WebElement rootListElement){
	  
  	WebElement marketDepth = null;
  	String symbolText = null;

	List<WebElement> buttons = rootListElement.findElements(By.tagName("button"));
	//WebElement parentSpan = rootListElement.findElement(By.className("action-buttons"));
	
	for(int i=0;buttons!=null && i<buttons.size();i++){
		symbolText = buttons.get(i).getAttribute("data-hint");
		if(symbolText!=null && symbolText.contains("Market depth")){
			//market-depth-buy
			Actions builder = new Actions(driver);
			builder.moveToElement(rootListElement).build().perform();
			WebDriverWait wait = new WebDriverWait(driver, 10);
			wait.until(ExpectedConditions.elementToBeClickable(buttons.get(i)));
			buttons.get(i).click();
			
			marketDepth = rootListElement.findElement(By.className("market-depth-buy"));
			wait.until(ExpectedConditions.elementToBeClickable(marketDepth.findElement(By.tagName("td"))));
			String html = marketDepth.getAttribute("innerHTML");
			
			driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			
			//buttons.get(i).click();
			html = marketDepth.getAttribute("innerHTML");
			
			System.out.println();
		}
	}
		
		return null;
  }
  
  private void newTab(String windowName,String url,WebDriver driver){
	  
	  try {
		Robot r = new Robot();                          
		  r.keyPress(KeyEvent.VK_CONTROL); 
		  r.keyPress(KeyEvent.VK_T); 
		  r.keyRelease(KeyEvent.VK_CONTROL); 
		  r.keyRelease(KeyEvent.VK_T);
		  
		  Thread.sleep(2000);
		  
		  for (String handle : driver.getWindowHandles()) {
			    if(!windowHandles.containsValue(handle)){
			    	driver.switchTo().window(handle);
			    }
		  }		  
		  driver.navigate().to(url);
		  Thread.sleep(2000);
		  windowHandles.put(windowName,driver.getWindowHandle());
	} catch (AWTException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	System.out.println();
	  
  }
  
  public void getTargetList(){
	  targetList.add("NIFTY 50 INDEX");
	  targetList.add("NIFTY OCT 10000 CE");
	  targetList.add("NIFTY OCT 9900 CE");
	  targetList.add("NIFTY OCT 10100 CE");
	  targetList.add("NIFTY OCT 10100 PE");
	  targetList.add("NIFTY OCT 9900 PE");
	  targetList.add("NIFTY OCT 10200 PE");
	   
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}
