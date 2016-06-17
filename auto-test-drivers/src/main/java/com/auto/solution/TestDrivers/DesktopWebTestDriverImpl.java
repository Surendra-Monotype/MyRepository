package com.auto.solution.TestDrivers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.interactions.Actions;

import us.codecraft.xsoup.Xsoup;

import com.auto.solution.Common.Property;
import com.auto.solution.Common.ResourceManager;
import com.auto.solution.Common.Property.FILTERS;
import com.auto.solution.Common.Utility;
import com.auto.solution.Common.Property.ERROR_MESSAGES;
import com.auto.solution.TestDrivers.RecoveryHandling.RecoverySupportForSeleniumDriver;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;
import com.thoughtworks.selenium.webdriven.WebDriverBackedSelenium;


public class DesktopWebTestDriverImpl implements TestDrivers{

	private static RecoverySupportForSeleniumDriver recoverySupportHandle = null;
	
	private String browserName = "";
	
	private boolean isRemoteExecution = false;
	
	private boolean isSauceLabExecution = false;
	
	private String remoteURL = "";
	
	private static  WebDriver driver = null;
	
	private static WebDriverWait wait = null;
	
    private WebElement actualTestElement = null;
    
    private ArrayList<WebElement> testObjectsList = new ArrayList<WebElement>();
    
    private boolean getTestObjectList = false;
    
    private ResourceManager rManager;
    
    private TestObjectDetails testObjectInfo = null;
    
    DesktopWebTestDriverImpl(ResourceManager rManager) {
		this.rManager = rManager;
	}
		
    private void scroll(){
		JavascriptExecutor js = (JavascriptExecutor)driver;
		js.executeScript("window.scrollBy(0,500)", "");
		sleep(500);
    }
    
    private File getScreenshot() throws Exception{
    	File srcFile;
    	try {
    		srcFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			srcFile=Utility.reduceScreenShotSize(srcFile,rManager.getTestExecutionLogFileLocation().replace("{0}", "tempFile.png"));
		} catch (Exception e) {
			throw e;
		}
		return srcFile;
	}
    
    private Selenium GetSeleniumOne() {
		
		Selenium seleniumOne = null;
		
		try {
			
			if (seleniumOne == null)
				seleniumOne = new WebDriverBackedSelenium(driver,	driver.getCurrentUrl());
	
				seleniumOne.start();
			
				return seleniumOne;
		} catch (Exception e) {
			return  null;
		}
	}
	
	private Object executeJavaScriptOnBrowserInstance(WebElement testElement,String javaScriptSnippet) throws Exception{
		
		JavascriptExecutor jsExecutor = null;
		Object jsResult = null;
		try{
			if(driver instanceof JavascriptExecutor){
			jsExecutor = (JavascriptExecutor)driver;
			 jsResult = jsExecutor.executeScript(javaScriptSnippet, testElement);
			}
		}
		catch(Exception e){
			throw e;
		}
		return jsResult;
	}
	
    private void switchToMostRecentWindow() {
		try {

			Set<String> windowHandles = driver.getWindowHandles();
			for (String window : windowHandles) {
				driver.switchTo().window(window);
			}

		} catch (Exception e) {
			//Nothing to do.
		}
	}
    
    private File highlightElement(WebElement element) throws Exception {
		File fileSnapshot  = null;
		try{
			
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].style.border='4px groove Red'", element);
			fileSnapshot = this.getScreenshot();
			js.executeScript("arguments[0].style.border=''", element);
		}
		catch(Exception e){
			throw e;
		}
		return fileSnapshot;
		
	}
    
    private ArrayList<String> getHyperRefrenceOfAllLinksOnPage(){
		
		ArrayList<String> hrefs = new ArrayList<String>();
		
		List<WebElement> linksOnPage = driver.findElements(By.xpath("//a"));
		
		for (WebElement link : linksOnPage) {
		
			String href = link.getAttribute("href");
			hrefs.add(href);
		}
		
		return hrefs;
	}
    
    /**
	 * @author Nayan
	 * </br><b> Description : </b></br> This will fetch all the user inputs regarding the execution. Like Remote Execution details, Browser details, SauceLab Execution details.
	 */
	private void fetchUserInputs(){
		System.setProperty("webdriver.ie.driver", "IEDriverServer.exe");
		
		this.browserName = Property.BrowserName;
		if(Property.IsRemoteExecution.equalsIgnoreCase("true")){
			this.isRemoteExecution = true;
			this.isSauceLabExecution = false;
			this.remoteURL = Property.RemoteURL;
		}
		else{
			if(Property.IsSauceLabExecution.equalsIgnoreCase("true")){
				this.isSauceLabExecution = true;
				this.remoteURL = Property.RemoteURL;
			}
			else{
				this.isSauceLabExecution = false;
			}
			this.isRemoteExecution = false;
		}
	}
	
	/**
	 * @author Nayan
	 * Delete all the cookies of current browser session.
	 */
	private  void deleteAllCookies() {
		try {
			//driver.manage().deleteAllCookies();
			Set<Cookie> cookies = driver.manage().getCookies();

			for (Cookie cookie : cookies) {
				driver.manage().deleteCookie(cookie);

			}

		} catch (Exception e) {
			// Nothing to throw;
		}

	}
	
	private void setCookies(){
		try{
			boolean isCookieSet = false;
			int browserCookieTrial = 0;	
			HashMap<String, HashMap<String, String>> cookies = Utility.getBrowserCookiesMap();
			if(cookies.size() > 0 ){
				try{
					browserCookieTrial = Integer.parseInt(Property.BROWSER_COOKIE_TRIAL);
				}catch(Exception ex){
					
				}
				
				while(!isCookieSet && browserCookieTrial >= 0){
					browserCookieTrial --;
					for (String cookieKey : cookies.keySet()) {
						Cookie cookie = null;
						if(cookies.get(cookieKey).containsKey(Property.COOKIE_DOMAIN_NAME))
							cookie = new Cookie(cookieKey, cookies.get(cookieKey).get(Property.COOKIE_VALUE),cookies.get(cookieKey).get(Property.COOKIE_DOMAIN_NAME),null,null);
						else 
							cookie = new Cookie(cookieKey, cookies.get(cookieKey).get(Property.COOKIE_VALUE));
					
						driver.manage().addCookie(cookie);	 
					}
					driver.navigate().refresh();
				
					for (String cookieKey : cookies.keySet()) {
						boolean isCookieFound = false;
						Cookie browserCookie =  driver.manage().getCookieNamed(cookieKey);
							    if(browserCookie.getName().equalsIgnoreCase(cookieKey) && browserCookie.getValue().equalsIgnoreCase(cookies.get(cookieKey).get(Property.COOKIE_VALUE))){
							    	isCookieFound = true;
					}
						  
					if(!isCookieFound){
						isCookieSet = false;
						break;
					}else{
						isCookieSet = true;
					}
						  
				}
					
					
					
			}
		}
			
	}
		catch(Exception e){
			//Nothing to throw.
		}
	}
	
	/**
	 * @author Nayan
	 * @param endPoint - String value of URL.
	 * @throws Exception
	 */
	private void openEndPointInBrowser(String endPoint) throws Exception{
		try{
			wait = new WebDriverWait(driver, Long.parseLong(Property.SyncTimeOut));
			
			driver.get(endPoint);
			//this.deleteAllCookies();
			
			//this.switchToMostRecentWindow();
			
			driver.manage().window().maximize();
		}
		catch(Exception e){
			throw e;
		}
		
	}
	
	
	private String getPageAttribute(String attributeName) throws Exception{
		
		String attributeValue = "";
		try {
			if(attributeName.toLowerCase().contains("url")){
				attributeValue = driver.getCurrentUrl();
			}
			else if(attributeName.toLowerCase().contains("title")){
				attributeValue = driver.getTitle();
			}
			else if(attributeName.toLowerCase().contains("source")){
				attributeValue = driver.getPageSource();
			}
			else {
				String errMessage = ERROR_MESSAGES.ER_SPECIFYING_INPUT.getErrorMessage().replace("{INPUT}", attributeName);
				throw new Exception(errMessage);
			}
		} catch (Exception e) {
			throw e;
		}
		return attributeValue;
	}
	
	private void performKeyBoardAction(String Key,WebElement testElement) throws Exception{
		try {
			
			if(Key.equalsIgnoreCase("enter")){
				testElement.sendKeys(Keys.ENTER);
			}
			else if(Key.equalsIgnoreCase("backspace")){
				testElement.sendKeys(Keys.BACK_SPACE);
			}
			else if(Key.equalsIgnoreCase("arrowdown")){
				testElement.sendKeys(Keys.ARROW_DOWN);
			}
			else if(Key.equalsIgnoreCase("arrowright")){
				testElement.sendKeys(Keys.ARROW_RIGHT);
			}
			else if(Key.equalsIgnoreCase("arrowleft")){
				testElement.sendKeys(Keys.ARROW_LEFT);
			}
			else if(Key.equalsIgnoreCase("space")){
				testElement.sendKeys(Keys.SPACE);
			}
			else if(Key.equalsIgnoreCase("tab")){
				testElement.sendKeys(Keys.TAB);
			}
			else{
				throw new Exception(Property.ERROR_MESSAGES.ER_SPECIFYING_KEYBOARD_KEY.getErrorMessage());
			}
		} catch (Exception e) {
				throw e;
		}
}
	
	private WebElement getTestObject(String locatingStrategy,String location){
		List<WebElement> testElements = null;
		WebElement testElement = null;
		try{
			if(locatingStrategy.toLowerCase().contains("css")){
				testElements = driver.findElements(By.cssSelector(location));
			}
			else if(locatingStrategy.toLowerCase().contains("id")){
				testElements = driver.findElements(By.id(location));
			}
			else if(locatingStrategy.toLowerCase().contains("tag")){
				testElements = driver.findElements(By.tagName(location));
			}
			else if(locatingStrategy.toLowerCase().contains("class")){
				testElements = driver.findElements(By.className(location));
				}
			else if(locatingStrategy.toLowerCase().contains("name")){
				testElements = driver.findElements(By.name(location));
			}
			else if(locatingStrategy.toLowerCase().contains("xpath")){
				testElements = driver.findElements(By.xpath(location));
			}
			else if(locatingStrategy.toLowerCase().contains("text")){
				testElements = driver.findElements(By.linkText(location));
			}
			
			if(this.getTestObjectList){
				this.testObjectsList.clear();
				this.testObjectsList = (ArrayList<WebElement>) testElements;
			}
			
			String[] filtersForTestObject = testObjectInfo.getFiltersAppliedOnTestObject().split(",");
			
			
			for (WebElement testObject : testElements) {				
			
				boolean testObjectValidForFilters = true;
				
				for (String filter : filtersForTestObject) {
					if(filter.equals("")){filter = FILTERS.IS_DISPLAYED.getFilter();}
					
					if(filter.toLowerCase().contains(FILTERS.IS_ENABLED.getFilter())){
						if(!testObject.isEnabled()){testObjectValidForFilters = false; break;}
					}
					
					if(filter.toLowerCase().contains(FILTERS.IS_CLICKABLE.getFilter())){
						if(!isClickable(testObject)){testObjectValidForFilters = false; break;}
					}
				
					if(filter.toLowerCase().contains(FILTERS.IS_DISPLAYED.getFilter())){
						if(!testObject.isDisplayed()){testObjectValidForFilters = false; break;}
					}
					
					
				}
				
				if(testObjectValidForFilters){
					testElement = testObject;
					break;
				}
			}
			
		}
		catch(Exception e){
			return null;
		}
		return testElement;
	}
	
	public static boolean isClickable(WebElement testObject){
		
		try
		{
		   WebDriverWait wait = new WebDriverWait(driver, 1);
		   wait.until(ExpectedConditions.elementToBeClickable(testObject));
		   return true;
		}
		catch (Exception e)
		{
		  return false;
		}
	}
	
	private WebElement getFrameForTestObjectOnTheBasisOfFrameDetails(String[] locatorDetailsOfFrame){
		
		WebElement frameWebElement = null;
		try {
			String frameLocatingStrategy = locatorDetailsOfFrame[0].trim();
			String frameLOcation = locatorDetailsOfFrame[1].trim();
			frameWebElement = getTestObject(frameLocatingStrategy, frameLOcation);
		} catch (Exception e) {
			return null;
		}
		return frameWebElement;
	}
	
	private void switchToTestObjectFrame() throws NoSuchFrameException{
		try {
			
			if(testObjectInfo.getFramedetailsOfTestObject().contains(Property.Frame_Details_Seperator)){
				String[] parsedLocatorDetailsofFrameForTestObject = testObjectInfo.getFramedetailsOfTestObject().split(Property.Frame_Details_Seperator);
				WebElement frameWebElement = getFrameForTestObjectOnTheBasisOfFrameDetails(parsedLocatorDetailsofFrameForTestObject);
				driver.switchTo().frame(frameWebElement);
			}
						
		} 
		catch(NullPointerException ne){
			//nothing to do 
		}
		catch (Exception e) {
			String errMessage = ERROR_MESSAGES.ER_WHILE_SWITCHING_TO_FRAME.getErrorMessage().replace("{FRAME_DETAILS}", testObjectInfo.getFramedetailsOfTestObject());
			throw new NoSuchFrameException(errMessage);
		}
	}
	
	private WebElement getActualTestObject(){
		WebElement testElement = null;		
		
		String 	locatingStrategyForObject = testObjectInfo.getLocatingStrategyOfTestObject();
		
		String locationOfObject = testObjectInfo.getLocationOfTestObject();
		
		testElement = this.getTestObject(locatingStrategyForObject, locationOfObject);
			
		return testElement;					
	}

	private WebElement waitAndGetTestObject(Boolean isWaitRequiredToFetchTheTestObject) throws NoSuchElementException, Exception{
	
		
		try{
			switchToMostRecentWindow();
			driver.switchTo().defaultContent();
			this.switchToTestObjectFrame();
			if(Property.LIST_STRATEGY_KEYWORD.contains(Property.STRATEGY_KEYWORD.NOWAIT.toString())){
				isWaitRequiredToFetchTheTestObject = false;
			}
			
			String locatingStrategyForObject = testObjectInfo.getLocatingStrategyOfTestObject();
			
			String locationOfObject = testObjectInfo.getLocationOfTestObject();
			
			if(((locatingStrategyForObject=="")||locatingStrategyForObject==null)&&((locationOfObject=="")||locationOfObject==null))
			{
				throw new Exception(Property.ERROR_MESSAGES.ER_GETTING_TESTOBJECT.getErrorMessage());
			}
			
			if(isWaitRequiredToFetchTheTestObject){
					wait.until(new ExpectedCondition<WebElement>() {
							public WebElement apply(WebDriver d) {
								try {
										actualTestElement =  getActualTestObject();
										return actualTestElement;
									} catch (Exception e) {
									actualTestElement = null;
									return null;
									}
							}
					});
					try{
					recoverySupportHandle.doRecoveryForSpecialObjectsWithHigherPriority();
					
					if(actualTestElement==null)
						recoverySupportHandle.doRecovery();
					}
					catch(Exception e){  System.out.println("RECOVERY_ACTION - " + e.getMessage());}
					waitAndGetTestObject(false);
			}
			else{
				actualTestElement = getActualTestObject();
			}
		}
		catch(TimeoutException te){
			try {
				recoverySupportHandle.doRecovery();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new Exception(Property.ERROR_MESSAGES.ER_IN_SPECIFYING_RECOVERY_ACTION.getErrorMessage());
			}
			waitAndGetTestObject(false);
			if(actualTestElement==null)
					throw new NoSuchElementException(Property.ERROR_MESSAGES.ER_GET_TESTOBJECT.getErrorMessage());
		}
		catch(NoSuchFrameException ne){
			throw ne;
		}
		return actualTestElement;
	}
	
	public void waitUntilObjectIsThere(){
		
		driver.switchTo().defaultContent();
		
		this.switchToTestObjectFrame();
		
		wait.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				boolean isObjectNotPresent = true;		
				try {
						actualTestElement =  getActualTestObject();
						if(actualTestElement != null){ isObjectNotPresent = false;}
						return isObjectNotPresent;
					} catch (Exception e) {					
					}
				return isObjectNotPresent;
			}
	});
		
	}
	
	
	@Override
	public void injectTestObjectDetail(TestObjectDetails objDetails) {
		this.testObjectInfo = objDetails;
		
	}

	@Override
	public void initializeApp(String endpoint) throws MalformedURLException,Exception {
		
		try{
					//Get all the related Info.
						fetchUserInputs();
						DesiredCapabilities executionCapabilities = new DesiredCapabilities();
						executionCapabilities.setJavascriptEnabled(true);
						executionCapabilities.setPlatform(Platform.ANY);
						
										
						if(isRemoteExecution){
								String remoteUrl = this.remoteURL + "/wd/hub";
			
								URL uri = new URL(remoteUrl);
								executionCapabilities.setCapability("webdriver.remote.quietExceptions", true);
								if(this.browserName.contains(Property.FIREFOX_KEYWORD)){
									FirefoxProfile remoteProfile = new FirefoxProfile();
									remoteProfile.setAssumeUntrustedCertificateIssuer(false);
									remoteProfile.setAcceptUntrustedCertificates(true);
									remoteProfile.setEnableNativeEvents(true);
									// We can also add extension to firefox profile if needed in future.
									executionCapabilities.setBrowserName("firefox");									
									executionCapabilities.setCapability("firefox_profile", remoteProfile.toString());									
								}
								else if(this.browserName.contains(Property.INTERNET_EXPLORER_KEYWORD)){
									executionCapabilities.setBrowserName("internet explorer");
									executionCapabilities.setCapability("ignoreProtectedModeSettings", true);
								}
								else if(this.browserName.contains(Property.CHROME_KEYWORD)){
									executionCapabilities = DesiredCapabilities.chrome();
									executionCapabilities.setCapability("chrome.switches", Arrays.asList("--start-maximized","--ignore-certificate-errors"));
								}
								else{
									throw new Exception(Property.ERROR_MESSAGES.ER_SPECIFY_BROWSER.getErrorMessage());
								}
								driver = new RemoteWebDriver(uri, executionCapabilities);
						}
						else if(isSauceLabExecution){
								//SauceLabExecution.
						}
						else{
								//LocalHost Execution.
								if(this.browserName.contains(Property.FIREFOX_KEYWORD)){
									FirefoxProfile ffProfile = new FirefoxProfile();
									ffProfile.setAssumeUntrustedCertificateIssuer(false);
									ffProfile.setAcceptUntrustedCertificates(true);
									ffProfile.setEnableNativeEvents(true);
									ffProfile.setPreference("browser.download.folderList", 2);
									ffProfile.setPreference("browser.download.manager.showWhenStarting", false);									
									ffProfile.setPreference("browser.download.dir",
											Utility.getAbsolutePath(rManager.getLocationForExternalFilesInResources().replace("{EXTERNAL_FILE_NAME}","").
													replace("{PROJECT_NAME}", Property.PROJECT_NAME)));
									ffProfile.setPreference("browser.helperApps.neverAsk.openFile",
											"text/csv,application/x-msexcel,application/excel,application/x-excel,"
											+ "application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,"
											+ "application/msword,application/xml");
									ffProfile.setPreference("browser.helperApps.neverAsk.saveToDisk",
											"text/csv,application/x-msexcel,application/excel,application/x-excel,"
											+ "application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,"
											+ "application/xml");
									// We can also add extension to firefox profile if needed in future.
									executionCapabilities.setBrowserName("firefox");									
									executionCapabilities.setCapability("firefox_profile", ffProfile.toString());	
									driver = new FirefoxDriver(executionCapabilities);
								}
								else if(this.browserName.contains(Property.INTERNET_EXPLORER_KEYWORD)){
									executionCapabilities.setBrowserName("internet explorer");
									executionCapabilities.setCapability("ignoreProtectedModeSettings", true);
									driver = new InternetExplorerDriver(executionCapabilities);
								}
								else if(this.browserName.contains(Property.CHROME_KEYWORD)){
								    if(!Property.OSString.toLowerCase().contains("window"))
								    	Property.CHROME_EXECUTABLE = Property.CHROME_EXECUTABLE_SH;
								    	
									executionCapabilities = DesiredCapabilities.chrome();
									Map<String,Object> profile=new HashMap<String,Object>();
									profile.put("disable-popup-blocking", "true");
									profile.put("download.default_directory",Utility.getAbsolutePath(rManager.getLocationForExternalFilesInResources().replace("{EXTERNAL_FILE_NAME}","").replace("{PROJECT_NAME}", Property.PROJECT_NAME)));
									profile.put("download.directory_upgrade", "true");
									profile.put("download.prompt_for_download", "false");
									ChromeOptions options = new ChromeOptions();
									options.setExperimentalOption("prefs", profile);
									executionCapabilities.setCapability("chrome.switches", Arrays.asList("--start-maximized","--ignore-certificate-errors"));
									executionCapabilities.setCapability(ChromeOptions.CAPABILITY, options);
									ChromeDriverService service = new ChromeDriverService.Builder()
									.usingAnyFreePort()
									.usingDriverExecutable(new File(rManager.getChromeDriverExecutibleLocation()))
									.build();
									service.start();
									driver = new ChromeDriver(service, executionCapabilities);
								}
								else if(this.browserName.contains(Property.PHANTOM_KEYWORD)){
									executionCapabilities = DesiredCapabilities.phantomjs();
									executionCapabilities.setCapability("phantomjs.binary.path",Property.PHANTOM_EXECUTABLE);
									driver = new PhantomJSDriver(executionCapabilities);
								}
								else{
									throw new Exception(Property.ERROR_MESSAGES.ER_SPECIFY_BROWSER.getErrorMessage());
								}
								
								}
						
						// Open the URL to respective Browser
						this.openEndPointInBrowser(endpoint);
						this.setCookies();

						recoverySupportHandle = new RecoverySupportForSeleniumDriver(driver,rManager);
						Utility.addObjectToGlobalObjectCollection(Property.TEST_DRIVER_KEY, driver);
		}
		catch(MalformedURLException me){
			throw me;
		}
		catch(Exception e){
			throw e;
		}
		
	}

	@Override
	public void hitTwice() throws Exception {
		
		Selenium objSelenium = this.GetSeleniumOne();
		try{	
			if(objSelenium != null){
				objSelenium.doubleClick(testObjectInfo.getLocationOfTestObject());
			}
			else{
				throw new SeleniumException(Property.ERROR_MESSAGES.ER_GETTING_DRIVER_SELENIUM.getErrorMessage());
			}
		}
		catch(Exception e){
			throw e;
		}
		
		
	}

	@Override
	public void pressKeyboardKey(String Key) throws NoSuchElementException,Exception {
		WebElement testElement = null;
		try{
			testElement = this.waitAndGetTestObject(true);
		}
		catch(NoSuchElementException ne){
			throw ne;
		}
		
		try{
			this.performKeyBoardAction(Key, testElement);
		}
		catch(Exception e){
			throw e;
		}
		
	}

	/**
	 * 
	 */
	@Override
	public void check() throws Exception{
		WebElement testElement= null;
		try{
			testElement = this.waitAndGetTestObject(true);
		}
		catch(NoSuchElementException ne){
			throw ne;
		}
		try{
			
			if(!testElement.isSelected()){
				testElement.click();
			}
		}
		catch(Exception e){
			throw e;
		}
	}

	@Override
	public void uncheck() throws NoSuchElementException, Exception {
		WebElement testElement = null;
		try{
			testElement = this.waitAndGetTestObject(true);
		}
		catch(NoSuchElementException ne){
			throw ne;
		}
		
		try{
			if(testElement.isSelected()){
				testElement.click();
			}
		}
		catch(Exception e){
			throw e;
		}
	}

	/**
	 * Would be used for upload file and enterdata
	 */
	@Override
	public void sendKey(String text) throws NoSuchElementException, Exception {
		WebElement testElement = null;
		try{
			testElement = this.waitAndGetTestObject(true);	;			
		}
		catch(NoSuchElementException ne){
			throw ne;
		}
		try{
			try{
			testElement.clear();}
			catch(Exception e){}
			testElement.sendKeys(text);			
		}
		catch(Exception e){
			throw e;
		}
		
	}
	
	public void uploadFile(String text) throws NoSuchElementException, Exception {
		String file = null; 
		file =	rManager.getLocationForExternalFilesInResources().replace("{EXTERNAL_FILE_NAME}", text);
		
		file =  file.replace("{PROJECT_NAME}", Property.PROJECT_NAME);
		
		String filepath = Utility.getAbsolutePath(file);

		WebElement testElement = null;;
		try{
			testElement = this.waitAndGetTestObject(true);;			
		}
		catch(NoSuchElementException ne){
			throw ne;
		}
		try{
			testElement.sendKeys(filepath);			
		}
		catch(Exception e){
			throw e;
		}
		
	}
	

	@Override
	public void navigateURL(String URL) throws Exception {
		try{
			driver.navigate().to(URL);
		}
		catch(Exception e){
			throw e;
		}
		
	}

	@Override
	public void isResourceLoaded() throws Exception {
		String currentUrl = "";
		String expectedUrl = testObjectInfo.getLocationOfTestObject();
		if(expectedUrl == ""){
			throw new Exception(Property.ERROR_MESSAGES.ER_SPECIFYING_OBJECT.getErrorMessage());
		}
		try{
			currentUrl = driver.getCurrentUrl();
			if(!(currentUrl.contains(expectedUrl))){
				throw new AssertionError(Property.ERROR_MESSAGES.ER_ASSERTION.getErrorMessage());
			}
		}
		catch(Exception e){
			throw e;
		}
		
	}

	@Override
	public void isTextPresent(String text) throws Exception {
		WebElement testElement = null;
		
		try{
			testElement = this.waitAndGetTestObject(true);
		}
		catch(NoSuchElementException ne){
			if(testObjectInfo.getLocationOfTestObject() != ""){
				throw new NoSuchElementException(Property.ERROR_MESSAGES.ER_ELEMENT_NOT_PRESENT.getErrorMessage());
			}
			testElement = null;
		}
		
		String ObjectText = "";
		try{
		if(testElement == null){
			ObjectText = driver.getPageSource();
			}
		else{
			ObjectText = testElement.getText();
			}
			if(!Utility.matchContentsBasedOnStrategyDefinedForTestStep(text, ObjectText)){
				String msgException = Property.ERROR_MESSAGES.ER_ASSERTION.getErrorMessage().replace("{ACTUAL_DATA}",ObjectText);
				msgException = msgException.replace("{$TESTDATA}", text);
				throw new Exception(msgException);
			}		
		}
		catch(Exception e){
			throw e;
		}
		
	}

	@Override
	public void click() throws Exception {
		WebElement testElement = null;
		try{
			testElement = this.waitAndGetTestObject(true);
			
			try{
			this.executeJavaScriptOnBrowserInstance(testElement, "arguments[0].focus()");
			
			testElement.click();
			}catch(Exception ex){
				if(ex.getMessage().toLowerCase().contains("element is not clickable")){
					this.executeJavaScriptOnBrowserInstance(testElement, "arguments[0].click();");

				}else{
					throw ex;
				}
//				
			}
			
		}
		catch(NoSuchElementException ne){
			throw ne;
		}
		catch(Exception e){
			throw e;
		}
		
	}

	@Override
	public void shutdown() throws Exception {
		try{
			if(driver.getTitle() != "" || driver.getTitle() != null){
				driver.close();
			}
			try{
			driver.quit();}
			catch(Exception te){
				//Nothing to do.
			}
		}
		catch(Exception e){
			throw e;
		}
		
	}

	@Override
	public void closeAllBrowsersWindow() throws Exception {
		try{
			for (String  window : driver.getWindowHandles()) {
				driver.switchTo().window(window);
				driver.close();
			}
			
		}
		catch(Exception e){
			throw e;
		}
		
	}

	@Override
	public void isObjectThere() throws Exception {
		WebElement testElement = this.waitAndGetTestObject(true);
		try{
		if(testElement == null){
			throw new Exception(Property.ERROR_MESSAGES.ER_ELEMENT_NOT_PRESENT.getErrorMessage());
		}
		if(!testElement.isDisplayed()){
			throw new Exception(Property.ERROR_MESSAGES.ER_ELEMENT_NOT_DISPLAYED.getErrorMessage());
		}
		
		}
		catch(Exception e){
			throw e;
		}
		
	}
	
	public void isObjectNotThere() throws Exception{
				
		WebElement testElement = this.waitAndGetTestObject(false);		
		try {
			if(testElement != null){
				String errMessage = Property.ERROR_MESSAGES.TESTOBJECT_IS_THERE.getErrorMessage().replace("{IS_DISPALYED}", String.valueOf(testElement.isDisplayed()));
				throw new Exception(errMessage);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public String fireEvents(String eventCodeSnippet) throws Exception {
		
		String codeSnippetOutput = "";
		try{
			WebElement testElement = null;
			try{
				testElement = this.waitAndGetTestObject(true);
			}
			catch(Exception e){
			 //nothing to do here!
			}
			
			Object snippetResult = this.executeJavaScriptOnBrowserInstance(testElement, eventCodeSnippet);
			if(snippetResult != null){
			codeSnippetOutput = snippetResult.toString();}
		}
		catch(Exception e){
			throw e;
		}
		return codeSnippetOutput;		
	}

	@Override
    public String select(String itemToSelect) throws Exception {
        WebElement testElement = this.waitAndGetTestObject(true);
        Pattern p = null;
        String selectedOption;
        String value = "";
        int itemIndexToSelect = -1;
        try{
            if(testElement == null){
                throw new Exception(ERROR_MESSAGES.ER_GET_TESTOBJECT.getErrorMessage());
            }
            Select objSelect = new Select(testElement);
            
            //select particular option.
            try{
                p = Pattern.compile("\\[(.*?)\\]");
                Matcher m = p.matcher(itemToSelect);
                if(m.find()) {
                    value = m.group(1);
                }else{
                    value = itemToSelect;
                }
                
                if(itemToSelect.toLowerCase().contains("value[")){
                    objSelect.selectByValue(value);
                }else if(itemToSelect.toLowerCase().contains("text[")){
                    objSelect.selectByVisibleText(value);
                }else{
                    try{
                        itemIndexToSelect = Integer.parseInt(value);
                        objSelect.selectByIndex(itemIndexToSelect);
                    }catch(Exception ex){
                        objSelect.selectByVisibleText(value);
                    }
                }
                //verify if selected
                WebElement optionSelected = objSelect.getFirstSelectedOption();
                if(itemIndexToSelect == -1){
                    if(!optionSelected.getText().equalsIgnoreCase(value) && !optionSelected.getAttribute("value").equalsIgnoreCase(value)){
                        throw new Exception(ERROR_MESSAGES.ER_COULDNOT_SELECT_VALUE.getErrorMessage().replace("{$OPTION}", value));
                    }
                }
                
            }
            catch(Exception e)
            {
                throw new Exception(ERROR_MESSAGES.ER_COULDNOT_SELECT_VALUE.getErrorMessage().replace("{$OPTION}", value));    
            }
            
            selectedOption = objSelect.getFirstSelectedOption().getText().toString().trim();
            return selectedOption;
        }
        catch(Exception e){
            throw e;
        }
        
    }


	@Override
	public String getTestElementAttribute(String propertyToFetch)
			throws Exception {
		
		
		try{
		WebElement testElement = this.waitAndGetTestObject(true);
		
		String attributeValue = "";
		
		if(propertyToFetch.equalsIgnoreCase("tag") || propertyToFetch.equalsIgnoreCase("tagname")){
			attributeValue = testElement.getTagName();
		}
		else if(propertyToFetch.equalsIgnoreCase("text")){
			attributeValue = testElement.getText();
		}
		else if(propertyToFetch.toLowerCase().contains("css")){
			String[] inputContent = propertyToFetch.split(":");
			if(inputContent.length != 2){
				throw new Exception(ERROR_MESSAGES.ER_SPECIFYING_CSS_PROPERTY.getErrorMessage());
			}
			attributeValue = testElement.getCssValue(inputContent[1]);
		}
		else{
			attributeValue = testElement.getAttribute(propertyToFetch);
		}
		
		return attributeValue;
		}
		catch(NullPointerException ne){
			throw new Exception(ERROR_MESSAGES.ER_GET_TESTOBJECT.getErrorMessage());
		}
		catch(Exception e){
			throw e;
		}
	}

	@Override
	public void verifyTestElementAttributeNotPresent(String propertyToVerify,String expectedValueOfProperty) throws Exception {
		String actualTestElementProperty = this.getTestElementAttribute(propertyToVerify);
		if(Utility.matchContentsBasedOnStrategyDefinedForTestStep(expectedValueOfProperty, actualTestElementProperty)){
			String errMessage = ERROR_MESSAGES.ER_IN_VERIFYING_TESTELEMENT_PROPERTY.getErrorMessage().replace("{EXPECTED}", expectedValueOfProperty);
			errMessage = errMessage.replace("{ACTUAL}", actualTestElementProperty);
			throw new Exception(errMessage);
		}
		
		
	}
	
	
	@Override
	public void verifyTestElementAttribute(String propertyToVerify,String expectedValueOfProperty) throws Exception {
		String actualTestElementProperty = this.getTestElementAttribute(propertyToVerify);
		if(!Utility.matchContentsBasedOnStrategyDefinedForTestStep(expectedValueOfProperty, actualTestElementProperty)){
			String errMessage = ERROR_MESSAGES.ER_IN_VERIFYING_TESTELEMENT_PROPERTY.getErrorMessage().replace("{EXPECTED}", expectedValueOfProperty);
			errMessage = errMessage.replace("{ACTUAL}", actualTestElementProperty);
			throw new Exception(errMessage);
		}
		
		
	}
	
	@Override
	public String getPageProperties(String propertyName) throws Exception{
		String actualProperty = "";
		actualProperty = this.getPageAttribute(propertyName);
		if(actualProperty == ""){
			String errMessage = ERROR_MESSAGES.ER_SPECIFYING_INPUT.getErrorMessage().replace("{INPUT}", propertyName);
			throw new Exception(errMessage);
		}
		return actualProperty;
	}

	@Override
	public void hover() throws Exception {
		try{
			
			WebElement testElement = this.waitAndGetTestObject(true);
			
			Actions builder = new Actions(driver);
			
			builder.moveToElement(testElement);
			
			builder.build().perform();
						
		}
		catch(Exception e){
			throw new Exception(ERROR_MESSAGES.ER_HOVER_TO_ELEMENT.getErrorMessage() + " -- " + e.getMessage());
		}
		
	}

	@Override
	public void sleep(long timesinmilliseconds){
		try{
		Thread.sleep(timesinmilliseconds);
		}
		catch(Exception e){
			// nothing to throw.
		}
		
	}

	
	@Override
	public String saveSnapshotAndHighlightTarget(boolean highlight) {
		/*
		 * For Debug Mode  OFF : take screenshots for FAILED test steps only
		 * FOr Debug MOde ON : take screenshots for ALL the test step irrespective of its status.
		 * For DebugMode  Strict Off : don't take screenshot at all. 
		 */
		File fileContainingSnapshot = null;
		File destinationFileThatWillContainSnapshot =  null;
		String screeShotFileName = "";
		try{			
			if(highlight){
				try{
					JavascriptExecutor js = (JavascriptExecutor) driver;
					js.executeScript("arguments[0].style.border='4px groove Red'", actualTestElement);
					fileContainingSnapshot = this.getScreenshot();
					js.executeScript("arguments[0].style.border=''", actualTestElement);
				}
				catch(Exception e){ //nothing to do.
					}
				}				
			else{
			fileContainingSnapshot = this.getScreenshot();}				
			
			if(fileContainingSnapshot != null){
			
				String modifiedStepExecutionTimeString = Property.StepExecutionTime.replace("/", "");
				modifiedStepExecutionTimeString = modifiedStepExecutionTimeString.replace(":", "");
				modifiedStepExecutionTimeString = modifiedStepExecutionTimeString.replace(" ", "");
				destinationFileThatWillContainSnapshot = new File(rManager.getTestExecutionLogFileLocation().replace("{0}", Property.CURRENT_TEST_GROUP + "_" + Property.CURRENT_TESTSUITE + "_" + Property.CURRENT_TESTCASE + "_" + Property.CURRENT_TESTSTEP + "_" + Property.StepStatus + modifiedStepExecutionTimeString + ".jpg"));
				FileUtils.copyFile(fileContainingSnapshot,destinationFileThatWillContainSnapshot);
				screeShotFileName = destinationFileThatWillContainSnapshot.getName();
			}
		}
		catch(Exception e){
			//Nothing to do here.
		}
		return screeShotFileName;
	}

	@Override
	public void swipetoElementVisible(String swipeType) throws Exception{
		int currentTime = 0;
		try {	
			scroll();
/*			int timeOut =Integer.parseInt(Property.SyncTimeOut);
			actualTestElement =  getActualTestObject();
			boolean displayed;
			try {
				displayed = actualTestElement.isEnabled();
			} catch (Exception e) {
				displayed = false;
			}

			while( !displayed && currentTime < timeOut )
			{
				scroll();
				Thread.sleep(1000);
				currentTime++;
				displayed = actualTestElement.isDisplayed();
			}*/
			
		}catch(Exception ex){	
			String errMessage = Property.ERROR_MESSAGES.ERR_IN_SWIPING_TO_OBJECT.getErrorMessage().replaceAll("{TIME}",String.valueOf(currentTime));
			throw new Exception(errMessage + ex.getMessage());
			
		}
	}
	
	@Override
	public String getTestObjectCount() throws Exception {
		
		int testObjectCount = 0;
		
		try {		
			this.getTestObjectList = true;
			this.waitAndGetTestObject(true);
			testObjectCount = this.testObjectsList.size();		
		} catch (Exception e) {
			testObjectCount = 0;
		}
		
		return String.valueOf(testObjectCount);
		
	}

	@Override
	public void refresh() throws Exception {
		
		try {
			driver.navigate().refresh();
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void verifyPageAttribute(String property, String value) throws Exception{
		String propertyValue = "";
		try {
			propertyValue = this.getPageAttribute(property);
			boolean isVerified = Utility.matchContentsBasedOnStrategyDefinedForTestStep(value, propertyValue);
			if(!isVerified){
				String errMessage = ERROR_MESSAGES.ERR_VERIFYING_PAGE_PROPERTY.getErrorMessage().replace("{PAGE_ATTRIBUTE}", property);
				errMessage = errMessage.replace("{ATTRIBUTE_VALUE}", propertyValue);
				errMessage = errMessage.replace("{EXPECTED_ATTRIBUTE_VALUE}", value);
				throw new Exception(errMessage);
			}
		} catch (Exception e) {
			throw e;
		}
		
	}

	@Override
	public void verifySortByFeature(String sortText) throws Exception {
		Document doc ;
		int priceOfOneProd;
		int tempVar = 0;
		Elements eachProd;
		Elements productContainer ;
		Elements tag;
		String currentURL;
		String xpathForSort = testObjectInfo.getFiltersAppliedOnTestObject();
		String xpathForProdContainer = testObjectInfo.getLocationOfTestObject();
		StringTokenizer strToken ;
		 try {
			 	currentURL = driver.getPageSource();
			    Element doc1 =  Jsoup.parse(currentURL).body();
	            productContainer =  Xsoup.compile(xpathForProdContainer).evaluate(doc1).getElements();		           
	            if(productContainer.size() == 0){
	            	String errMessage = ERROR_MESSAGES.ERR_NO_PRODUCT_FOUND_ON_CATALOG.getErrorMessage();
	            	throw new Exception(errMessage);
	            }	            	
		        else{
		        	eachProd = productContainer.get(0).getElementsByAttributeValueContaining("data-product-id", "INDFAS");
	            	for(Element prod : eachProd){
	        			tag = Xsoup.compile(xpathForSort).evaluate(prod).getElements();
	        			try{
	        				
	            			if(sortText.equalsIgnoreCase("desc")){
	            				if( tag.size() == 0){
	            					String errMessage = ERROR_MESSAGES.ERR_TAG_NOT_FOUND_AFTER_SORTING.getErrorMessage().replace("{SORT_SELECTED}", sortText);
	        						errMessage = errMessage.replace("{SKU}", prod.attr("data-product-id"));
	        						throw new Exception(errMessage);
	            				}
	            				else{
	            					priceOfOneProd = Integer.parseInt(tag.get(0).text());
		            				if(tempVar != 0){
		            					if(tempVar < priceOfOneProd){
		            						String errMessage = ERROR_MESSAGES.ERR_PRICE_MISMATCH.getErrorMessage().replace("{SORT_SELECTED}", sortText);
			        						errMessage = errMessage.replace("{SKU}", prod.attr("data-product-id"));
			        						errMessage = errMessage.replace("{PRICE_FOUND}", String.valueOf(priceOfOneProd));
			        						throw new Exception(errMessage);
		            					}
		            						
		            				}
		            				tempVar = priceOfOneProd;
	            				}
	            			}	            			
	            			else if(sortText.equalsIgnoreCase("asc")){
	            				if( tag.size() == 0)
	            					throw new Exception("Price Tag Not found for product "+prod.attr("data-product-id"));
	            				else{
		            				priceOfOneProd = Integer.parseInt(tag.get(0).text());
		            				if(tempVar != 0){
		            					if(tempVar > priceOfOneProd){
		            						String errMessage = ERROR_MESSAGES.ERR_PRICE_MISMATCH.getErrorMessage().replace("{SORT_SELECTED}", sortText);
			        						errMessage = errMessage.replace("{SKU}", prod.attr("data-product-id"));
			        						errMessage = errMessage.replace("{PRICE_FOUND}", String.valueOf(priceOfOneProd));
			        						throw new Exception(errMessage);
		            					}
		            				}
		            				tempVar = priceOfOneProd;
	            				}
	            			}
	            			else if(sortText.matches("b|B.*\\d+.*") || sortText.matches("a|A.*\\d+.*") || sortText.matches(".*\\d+.*-.*\\d+.*")){
	            				if( tag.size() == 0)
	            					throw new Exception("Price Tag Not found for product "+prod.attr("data-product-id"));
	            				else{
	            					if(sortText.matches("b|B.*\\d+.*")  ){
	            						int lowerRange = 0;
	            						strToken = new StringTokenizer(sortText,"[b-B]elow");
	            						int upperRange = Integer.parseInt(strToken.nextToken().trim());
	            						priceOfOneProd = Integer.parseInt(tag.get(0).text());
			            				if(priceOfOneProd == 0 || priceOfOneProd > upperRange){
			            					String errMessage = ERROR_MESSAGES.ERR_PRICE_OUT_OF_RANGE.getErrorMessage().replace("{SORT_SELECTED}", sortText);
			        						errMessage = errMessage.replace("{SKU}", prod.attr("data-product-id"));
			        						errMessage = errMessage.replace("{PRICE_FOUND}", String.valueOf(priceOfOneProd));
			        						throw new Exception(errMessage);
			            				}
	            					}
	            					else if(sortText.matches("a|A.*\\d+.*")){
	            						strToken = new StringTokenizer(sortText,"[a-A]bove");
	            						int range = Integer.parseInt(strToken.nextToken().trim());
	            						priceOfOneProd = Integer.parseInt(tag.get(0).text());
			            				if(priceOfOneProd < range){
			            					String errMessage = ERROR_MESSAGES.ERR_PRICE_OUT_OF_RANGE.getErrorMessage().replace("{SORT_SELECTED}", sortText);
			        						errMessage = errMessage.replace("{SKU}", prod.attr("data-product-id"));
			        						errMessage = errMessage.replace("{PRICE_FOUND}", String.valueOf(priceOfOneProd));
			        						throw new Exception(errMessage);
			            				}
	            					}
	            					else{
	            						int lowerRange = Integer.parseInt(sortText.split("-")[0].trim());
	            						int upperRange = Integer.parseInt(sortText.split("-")[1].trim());
	            						priceOfOneProd = Integer.parseInt(tag.get(0).text());
	            						if(priceOfOneProd < lowerRange  || priceOfOneProd > upperRange){
	            							String errMessage = ERROR_MESSAGES.ERR_PRICE_OUT_OF_RANGE.getErrorMessage().replace("{SORT_SELECTED}", sortText);
			        						errMessage = errMessage.replace("{SKU}", prod.attr("data-product-id"));
			        						errMessage = errMessage.replace("{PRICE_FOUND}", String.valueOf(priceOfOneProd));
			        						throw new Exception(errMessage);
	            						}
	            					}
	            					
	            				}
	            			}
	            			else{
	            				if( tag.size() == 0 || !tag.get(0).text().contains(sortText) ){
	        						String errMessage = ERROR_MESSAGES.ERR_TAG_NOT_FOUND_AFTER_SORTING.getErrorMessage().replace("{SORT_SELECTED}", sortText);
	        						errMessage = errMessage.replace("{SKU}", prod.attr("data-product-id"));
	        						throw new Exception(errMessage);
	        					}	     
	            			}
	        			
	        			}
	        			catch(Exception ex){
	        				throw ex;
	        			}
	        	}
	    	}
	            
		 }
		 catch(Exception e){
			 throw e;
		 }
		
	}
	
	@Override
	public void verifyAndReportRedirectdUrls() throws Exception{

		try{
			HashMap<String, String> redirectedUrls = new HashMap<String, String>();
			
			String fileLocation = rManager.getLocationForExternalFilesInResources().replace("{PROJECT_NAME}", Property.PROJECT_NAME);
			fileLocation = fileLocation.replace("{EXTERNAL_FILE_NAME}","SourceForRedirectedUrls.csv");
			String sourceFileForBrokenLinks = fileLocation;
			
			ArrayList<String> pageUrls = Utility.getPageUrlsInListFormatFromCSV(sourceFileForBrokenLinks);
			
			for (String url : pageUrls) {
				try{
					if(!url.contains("http")){
						url = "http://" + url;
					}
					driver.navigate().to(url);
					String redirectedUrl = this.getPageAttribute("url");
					
					if(!redirectedUrl.equalsIgnoreCase(url) && !redirectedUrl.equalsIgnoreCase(url + "/")){
						redirectedUrls.put(url, "Error -- Redirecting to " + redirectedUrl);
					}
					else{
					      redirectedUrls.put(url, "OK");
					    }
				}
				catch(Exception e){
					redirectedUrls.put(url, "FAILED -- Couldn't navigate to URL");
				}
			}
			
			Utility.reportUrlsStatus(redirectedUrls,rManager.getTestExecutionLogFileLocation().replace("{0}", "RedirectedUrls.csv"));
		}
		catch(Exception e){
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * this function use to verify section links of web application.
	 *  variable sectionName is name of the section
	 *  sectionValue is xpath of the section
	 * @see com.auto.solution.TestDrivers.TestDrivers#verifyAndReportSectionLinks(java.lang.String, java.lang.String)
	 */
	
	@Override
	public void verifyAndReportSectionLinks(String sectionName, String sectionValue) throws Exception{
		try{
			String PageNotFoundLocator = testObjectInfo.getLocationOfTestObject();
			
			HashMap<String, String> brokenUrls = new HashMap<String, String>();;
			String fileLocation = rManager.getLocationForExternalFilesInResources().replace("{PROJECT_NAME}", Property.PROJECT_NAME);
			fileLocation = fileLocation.replace("{EXTERNAL_FILE_NAME}", "ApplicationURLSourceForCatagory.csv");
			String sourceFileForBrokenLinks = fileLocation;
			
			ArrayList<String> pageUrls = Utility.getPageUrlsInListFormatFromCSV(sourceFileForBrokenLinks);
			
			for (String url : pageUrls) {
				HashMap<String, String> catagorySectionHrefs = new HashMap<String, String>();
				try{
					driver.navigate().to(url);
					addAndUpdateLinks(catagorySectionHrefs,url,sectionValue);
						try{		
							while(catagorySectionHrefs.containsValue(Property.Pending)){
								for (String hrefKey : catagorySectionHrefs.keySet()) {
									if(catagorySectionHrefs.get(hrefKey).equalsIgnoreCase(Property.Pending)){
										try{
										driver.navigate().to(hrefKey);
											try{
												WebElement pageNotFoundTestObject = driver.findElement(By.xpath(PageNotFoundLocator));
												if(pageNotFoundTestObject.isDisplayed()){
													brokenUrls.put(hrefKey, "ERROR -- Page Not Found");
													}
												}
												catch(Exception ne){
													
												}
											try{
												String curURL = driver.getCurrentUrl();
												if(!curURL.toLowerCase().contains(hrefKey.toLowerCase())){
													brokenUrls.put(hrefKey, curURL);
												}
											}catch(Exception ex){
												
											}
										}
										catch(Exception ne){
											
										}
										addAndUpdateLinks(catagorySectionHrefs,hrefKey,sectionValue);
										break;
									}
								}
							}
						
						
						}
						catch(Exception e){
							brokenUrls.put(url, "FAILED --" + e.getMessage());
						}
							
				}
				catch(Exception e){
					brokenUrls.put(url, "FAILED --" + e.getMessage());
				}
			}
			
			Utility.reportUrlsStatus(brokenUrls,rManager.getTestExecutionLogFileLocation().replace("{0}", sectionName+"_BrokenLinks.csv"));
		}
		catch(Exception e){
			throw e;
		}
	}
	
	private void addAndUpdateLinks(HashMap<String, String> catagorySectionHrefs,String hrefKey,String sectionType)throws Exception{
		try{
			catagorySectionHrefs.put(hrefKey, Property.Verified);
		    List<WebElement> elems =  driver.findElements(By.xpath(sectionType));
		      for (WebElement webElement : elems) {
		    	  try{
		    	  String hrefValue = webElement.getAttribute("href");
		    	  if(!catagorySectionHrefs.containsKey(hrefValue))
		    		  catagorySectionHrefs.put(hrefValue, Property.Pending);
		    	  }catch(Exception ex){
		    		  
		    	  }
		      }
			
		}catch(Exception ex){
			throw ex;
		}
		
	}
	
	
	@Override
	public void verifyAndReportBrokenLinksFromPages() throws Exception{
		
		
		try{
			String PageNotFoundLocator = testObjectInfo.getLocationOfTestObject();
			
			HashMap<String, String> brokenUrls = new HashMap<String, String>();
			
			String fileLocation = rManager.getLocationForExternalFilesInResources().replace("{PROJECT_NAME}", Property.PROJECT_NAME);
			fileLocation = fileLocation.replace("{EXTERNAL_FILE_NAME}", "ApplicationURLSource.csv");
			String sourceFileForBrokenLinks = fileLocation;
			
			ArrayList<String> pageUrls = Utility.getPageUrlsInListFormatFromCSV(sourceFileForBrokenLinks);
			
			for (String url : pageUrls) {
				try{
					if(!url.contains("http")){
						url = "http://" + url;
					}
				
					driver.navigate().to(url);
				
					ArrayList<String> hrefs = this.getHyperRefrenceOfAllLinksOnPage();
				
					for (String linkUrl : hrefs) {
						
						try{
						if(linkUrl.contains("#")){
							brokenUrls.put(linkUrl, "ERROR -- Contains # in hyper reference");
							continue;
						}
						driver.navigate().to(linkUrl);
						
						try{
						WebElement pageNotFoundTestObject = driver.findElement(By.xpath(PageNotFoundLocator));
						if(pageNotFoundTestObject.isDisplayed()){
							brokenUrls.put(linkUrl, "ERROR -- Page Not Found");
							}
						}
						catch(Exception ne){
							
						}
						}
						catch(Exception e){
							brokenUrls.put(linkUrl, "FAILED --" + e.getMessage());
						}
					}				
				}
				catch(Exception e){
					brokenUrls.put(url, "FAILED --" + e.getMessage());
				}
			}
			
			Utility.reportUrlsStatus(brokenUrls,rManager.getTestExecutionLogFileLocation().replace("{0}","BrokenLinks.csv"));
		}
		catch(Exception e){
			throw e;
		}
	}
	@Override
	public void verifyAndReportSCO(String scoUrlSource) throws Exception{
		try{
			String SEO_Article = testObjectInfo.getLocationOfTestObject();
			
			HashMap<String, String> SEOURLS_STATUS = new HashMap<String, String>();
			
			String fileLocation = rManager.getLocationForExternalFilesInResources().replace("{PROJECT_NAME}", Property.PROJECT_NAME);
			fileLocation = fileLocation.replace("{EXTERNAL_FILE_NAME}", scoUrlSource);
			String sourceFileForSEOUrls = fileLocation;
			
			ArrayList<String> pageUrls = Utility.getPageUrlsInListFormatFromCSV(sourceFileForSEOUrls);
			
			for (String url : pageUrls) {
				try{
					if(!url.contains("http")){
						url = "http://" + url;
					}
				driver.navigate().to(url);
				}
				catch(Exception e){
					SEOURLS_STATUS.put(url, "FAIL:: INVALID URL");
				}
				try{
				WebElement article = driver.findElement(By.xpath(SEO_Article));
				WebElement heading = article.findElement(By.tagName("h2"));
				String heading_content = heading.getText();
				boolean containKeyword = false;
				for (String heading_keyword : heading_content.split(" ")) {
					if(url.contains(heading_keyword.toLowerCase())){containKeyword = true; break;}
					else {containKeyword = false;}
				}
				if(!containKeyword){
					SEOURLS_STATUS.put(url, "SEO content doesn't contains brick name");
				}
				else{
					SEOURLS_STATUS.put(url, "OK");
				}
				
				}
				catch(Exception e){
					SEOURLS_STATUS.put(url, "FAILED :: NO SEO Content Found");
				}
				
			}
			String outputFileName = "SEOStatus_" + Utility.getCurrentTimeStampInAlphaNumericFormat() + ".csv";
			Utility.reportUrlsStatus(SEOURLS_STATUS, rManager.getTestExecutionLogFileLocation().replace("{0}", outputFileName));
		}
		catch(Exception e){
			
		}
	}
	@Override
	public void browserNavigation(String navigationOption) throws Exception
	{
		try{
			if(navigationOption.equalsIgnoreCase("back")){
				driver.navigate().back();
			}
			else if(navigationOption.equalsIgnoreCase("forward")){
				driver.navigate().forward();
			}
			else{
				String errMessage = ERROR_MESSAGES.ER_SPECIFYING_TESTDATA.getErrorMessage();
				throw new Exception(errMessage);
			}
		}
		catch(Exception e){
			throw e;
		}
	}
	
	@Override
	public void handleAlert(String option) throws Exception
	{
		Alert alert;
		try {
			alert = driver.switchTo().alert();
		}
		catch (NoAlertPresentException e) {
			throw e;
		}
		try{
			if (option.equalsIgnoreCase("accept")) {
				alert.accept();
			} else if(option.equalsIgnoreCase("dismiss")){
				alert.dismiss();
			}
			else
			{
				String errMessage = ERROR_MESSAGES.ER_SPECIFYING_TESTDATA.getErrorMessage();
				throw new Exception(errMessage);
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
 	public String getElementDimension() throws NoSuchElementException, Exception {
 		WebElement testElement = null;
 		try{
 			testElement = this.waitAndGetTestObject(true);
 		}
 		catch(NoSuchElementException ne){
 			throw ne;
 		}
 		
 		try{
 			int width = testElement.getSize().getWidth();
 			int height = testElement.getSize().getHeight();
 			return width +"," + height;
 		}
 		catch(Exception e){
 			throw e;
 		}
 		
 	}

	public void clickOnCo_ordinates(int i,int j) throws Exception{
		throw new Exception(ERROR_MESSAGES.FEATURE_NOT_IMPLEMENTED.getErrorMessage());
	}
	
	@Override
	public void resizeToDeafult() throws Exception{
		try{
			driver.manage().window().maximize();
		}
		catch(Exception e){
			throw e;
		}
	}

	@Override
	public void resizeCurrentWindow(int x_coord, int y_coord) throws Exception {
		
		try{
		Dimension targetWindowDimension = new Dimension(x_coord, y_coord);
		driver.manage().window().setSize(targetWindowDimension);
		}
		catch(Exception e){
			throw e;
		}
	}
	
}

