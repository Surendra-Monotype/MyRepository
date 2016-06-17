package com.auto.solution.TestDrivers;

import io.appium.java_client.android.AndroidDriver;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.auto.solution.Common.Property;
import com.auto.solution.Common.Property.ERROR_MESSAGES;
import com.auto.solution.Common.Property.FILTERS;
import com.auto.solution.Common.ResourceManager;
import com.auto.solution.Common.Utility;
import com.auto.solution.TestDrivers.RecoveryHandling.RecoverySupportForSeleniumDriver;

public class MobileAndriodTestDriverImpl implements TestDrivers{

	private static RecoverySupportForSeleniumDriver recoverySupportHandle = null;
	
	private DesiredCapabilities driverCapability = new DesiredCapabilities();
	
	private boolean isRemoteExecution = false;
	
	private String appiumUrlForExecution = "";
	
	private  static AndroidDriver driver = null;
	
	private static WebDriverWait wait = null;
	
	private WebElement actualTestElement = null;
	
	private boolean getTestObjectList = false;
	
	private ArrayList<WebElement> testObjectList = new ArrayList<WebElement>();
	
	private TestObjectDetails testObjectInfo = null;
	
	private ResourceManager rManager;
	
	MobileAndriodTestDriverImpl(ResourceManager rm) {
		this.rManager = rm;
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
	
	@Override
	public void injectTestObjectDetail(TestObjectDetails objDetails) {
		this.testObjectInfo = objDetails;
		
	}

	private void loadAndSetDriverCapabilities() throws Exception{
		try{
		
			Set<String> inputKeys = Property.globalVarMap.keySet();
			
			//String apkFilePath = rManager.getMobileAPKFileLocation().replace("{PROJECT_NAME}", Property.PROJECT_NAME);
			
			for (String capabilitykey : inputKeys) {
				if(capabilitykey.contains(Property.DRIVER_CAPABILITY_KEYWORD)){
					String capabilityValue = Property.globalVarMap.get(capabilitykey);
					
					String actualCapabilityName = capabilitykey.replace(Property.DRIVER_CAPABILITY_KEYWORD + ".", "");
					
					if(capabilityValue.toLowerCase().contains("null"))
					{capabilityValue = "";}
					
					/*if(actualCapabilityName.equalsIgnoreCase("app")){
						if(!capabilityValue.trim().equals(""))
							capabilityValue = Utility.getAbsolutePath(apkFilePath.replace("{APK_FILENAME}", capabilityValue));
					    }*/
					
					driverCapability.setCapability(actualCapabilityName, capabilityValue);
				}
			}
		}
		catch(Exception e){
			throw new Exception(ERROR_MESSAGES.ER_IN_LOADING_DRIVER_CAPABILITIES.getErrorMessage() + e.getMessage());
		}
	}
	
	private void captureAndriodExecutionDetails(){
		if(Property.IsRemoteExecution.equalsIgnoreCase("true")){
			this.appiumUrlForExecution = Property.RemoteURL + "/wd/hub";
		}
		else{
			this.appiumUrlForExecution = "http://127.0.0.1:4723/wd/hub";
		}
	}
	
	private void swipe(String swipeType){
		int screenWidth = 0;
		int screenHight = 0;
		Dimension screenSize = driver.manage().window().getSize();
		screenWidth = Integer.valueOf(String.valueOf(screenSize
				.getWidth())) / 2;
		screenHight = Integer.valueOf(String.valueOf(screenSize
				.getHeight())) / 2;
		
		if(swipeType.trim().toLowerCase().contains("up")){
			driver.swipe(screenWidth, (2*screenHight)-170, screenWidth, screenHight, 2000);	
		}else if(swipeType.trim().toLowerCase().contains("down")){
			driver.swipe(screenWidth, screenHight, screenWidth, (2*screenHight)-100, 2000);	
		}
		else if(swipeType.trim().toLowerCase().contains("left")){
			
			driver.swipe((2*screenWidth)-100, screenHight, 20, screenHight, 2000);
		}
	 
	}
	
	@Override
	public void swipetoElementVisible(String swipeType) throws Exception{	
		int currentTime = 0;
		try {	
			int timeOut =Integer.parseInt(Property.SyncTimeOut);
			actualTestElement =  getActualTestObject();
			while(actualTestElement == null && currentTime < timeOut ){
				swipe(swipeType);
				Thread.sleep(1000);
				actualTestElement =  getActualTestObject();
				currentTime++;
			}	
		}catch(Exception ex){	
			String errMessage = Property.ERROR_MESSAGES.ERR_IN_SWIPING_TO_OBJECT.getErrorMessage().replaceAll("{TIME}",String.valueOf(currentTime));
			throw new Exception(errMessage + ex.getMessage());
			
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
	private void performKeyBoardAction(String Key) throws Exception{
		int keyValue = -1;
		try{
			keyValue = Integer.parseInt(Key);
		}catch(Exception ex){
			throw new Exception(Property.ERROR_MESSAGES.ER_SPECIFYING_KEYBOARD_KEY.getErrorMessage());
		}
		try {
			driver.sendKeyEvent(keyValue);
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
				this.testObjectList.clear();
				this.testObjectList = (ArrayList<WebElement>) testElements;
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
		   WebDriverWait wait = new WebDriverWait(driver,1);
		   wait.until(ExpectedConditions.elementToBeClickable(testObject));
		   return true;
		}
		catch (Exception e)
		{
		  return false;
		}
	}
	
    private WebElement waitAndGetTestObject(Boolean isWaitRequiredToFetchTheTestObject) throws NoSuchElementException, Exception{
	try{
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
	
		return actualTestElement;
	}
	
	private WebElement getActualTestObject(){
		WebElement testElement = null;		
		String locatingStrategyForObject = testObjectInfo.getLocatingStrategyOfTestObject();
		
		String locationOfObject = testObjectInfo.getLocationOfTestObject();
		
		testElement = this.getTestObject(locatingStrategyForObject, locationOfObject);
			
		return testElement;					
	}
	
	@Override
	public void initializeApp(String endpoint) throws MalformedURLException,
			Exception {
		try{	
			this.loadAndSetDriverCapabilities();
			this.captureAndriodExecutionDetails();
			driver = new AndroidDriver(new URL(this.appiumUrlForExecution), driverCapability);
			wait = new WebDriverWait(driver, Long.parseLong(Property.SyncTimeOut));
			recoverySupportHandle = new RecoverySupportForSeleniumDriver(driver,rManager);
			Utility.addObjectToGlobalObjectCollection(Property.TEST_DRIVER_KEY, driver);
		}
		catch(Exception e){
			throw e;
		}
	}

	@Override
	public void hitTwice() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pressKeyboardKey(String Key) throws Exception {
		try{
			this.performKeyBoardAction(Key);
		}
		catch(Exception e){
			throw e;
		}
		
	}

	@Override
	public void check() throws NoSuchElementException, Exception {
		WebElement testElement= null;
		try{
			testElement = this.waitAndGetTestObject(true);
		}
		catch(NoSuchElementException ne){
			throw ne;
		}
		try{
			
			if(!Boolean.parseBoolean(testElement.getAttribute("checked"))){
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
			if(Boolean.parseBoolean(testElement.getAttribute("checked"))){
				testElement.click();
			}
		}
		catch(Exception e){
			throw e;
		}
		
	}

	@Override
	public void sendKey(String text) throws NoSuchElementException, Exception {
		WebElement testElement = null;
		try{
			testElement = this.waitAndGetTestObject(true);				
		}
		catch(NoSuchElementException ne){
			throw ne;
		}
		try{
			try {
			testElement.click();
			testElement.clear();
			}catch(Exception ex){
				
			}
			testElement.sendKeys(text);	
			try {
				driver.hideKeyboard();
			} catch (Exception e) {
				
			}
		}
		catch(Exception e){
			throw e;
		}		
	}

	@Override
	public void navigateURL(String URL) throws Exception {
		// TODO Auto-generated method stub
		
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
			testElement.click();
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
			try{
				driver.quit();
			}
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
		// TODO Auto-generated method stub
		
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

	@Override
	public void isObjectNotThere() throws Exception {
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
		String selectedOption;
		try{
			if(testElement == null){
				throw new Exception(ERROR_MESSAGES.ER_GET_TESTOBJECT.getErrorMessage());
			}
			Select objSelect = new Select(testElement);
			//select particular option.
			objSelect.selectByVisibleText(itemToSelect);
			//verify if selected
			WebElement optionSelected = objSelect.getFirstSelectedOption();
			if(!optionSelected.getText().equals(itemToSelect)){
				throw new Exception(ERROR_MESSAGES.ER_COULDNOT_SELECT_VALUE.getErrorMessage().replace("{$OPTION}", itemToSelect));
			}
			selectedOption = optionSelected.getText().toString().trim();
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
	public void verifyTestElementAttribute(String propertyToVerify,
			String expectedValueOfProperty) throws Exception {
		String actualTestElementProperty = this.getTestElementAttribute(propertyToVerify);
		if(!Utility.matchContentsBasedOnStrategyDefinedForTestStep(expectedValueOfProperty, actualTestElementProperty)){
			String errMessage = ERROR_MESSAGES.ER_IN_VERIFYING_TESTELEMENT_PROPERTY.getErrorMessage().replace("{EXPECTED}", expectedValueOfProperty);
			errMessage = errMessage.replace("{ACTUAL}", actualTestElementProperty);
			throw new Exception(errMessage);
		}
		
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
	public void sleep(long timeInMilliseconds) {
		try{
			Thread.sleep(timeInMilliseconds);
			}
			catch(Exception e){
				// nothing to throw.
			}
		
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
	public void waitUntilObjectIsThere() throws Exception {
		
		wait.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				boolean isObjectNotPresent = true;		
				try {
						actualTestElement =  getActualTestObject();
						if(actualTestElement != null){ isObjectNotPresent = false;}
						else{isObjectNotPresent =  true ;}
						return isObjectNotPresent;
					} catch (Exception e) {					
					}
				return isObjectNotPresent;
			}
	});
		
		
	}

	@Override
	public String getTestObjectCount() throws Exception {
		int testObjectCount = 0;
		
		try {
			this.getTestObjectList = true;
			this.waitAndGetTestObject(true);
			testObjectCount = this.testObjectList.size();
		} catch (Exception e) {
			testObjectCount = 0;
		}
		
		return String.valueOf(testObjectCount);
	}

	@Override
	public void refresh() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void verifyPageAttribute(String property, String value)
			throws Exception {
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
	public String getPageProperties(String propertyName) throws Exception {
		String actualProperty = "";
		actualProperty = this.getPageAttribute(propertyName);
		if(actualProperty == ""){
			String errMessage = ERROR_MESSAGES.ER_SPECIFYING_INPUT.getErrorMessage().replace("{INPUT}", propertyName);
			throw new Exception(errMessage);
		}
		return actualProperty;
	}

	@Override
	public void verifySortByFeature(String sortType) throws Exception {
		throw new Exception(ERROR_MESSAGES.FEATURE_NOT_IMPLEMENTED.getErrorMessage());
		
	}
	
	@Override
	public void verifyAndReportRedirectdUrls() throws Exception{
		throw new Exception(ERROR_MESSAGES.FEATURE_NOT_IMPLEMENTED.getErrorMessage());
	}

	@Override
	public void verifyAndReportBrokenLinksFromPages() throws Exception {
		throw new Exception(ERROR_MESSAGES.FEATURE_NOT_IMPLEMENTED.getErrorMessage());
		
	}

	@Override
	public void browserNavigation(String navigationOption) throws Exception
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void verifyAndReportSCO(String scoUrlSource) throws Exception {
		throw new Exception(ERROR_MESSAGES.FEATURE_NOT_IMPLEMENTED.getErrorMessage());
		
	}

	@Override
	public void handleAlert(String option) throws Exception
	{
		throw new Exception(ERROR_MESSAGES.FEATURE_NOT_IMPLEMENTED.getErrorMessage());
	}	

	public void uploadFile(String text)throws NoSuchElementException,Exception{
		throw new Exception(ERROR_MESSAGES.FEATURE_NOT_IMPLEMENTED.getErrorMessage());
	}

	@Override
	public void verifyAndReportSectionLinks(String sectionName,String sectionValue)
			throws Exception {
		throw new Exception(ERROR_MESSAGES.FEATURE_NOT_IMPLEMENTED.getErrorMessage());
		
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
	 			return width + "," + height;
	 		}
	 		catch(Exception e){
	 			throw e;
	 		}
	 		
	 	}

	public void clickOnCo_ordinates(int i,int j) throws Exception{
		throw new Exception(ERROR_MESSAGES.FEATURE_NOT_IMPLEMENTED.getErrorMessage());
	}

	@Override
	public void resizeCurrentWindow(int x_coord, int y_coord) throws Exception {
		throw new Exception(ERROR_MESSAGES.FEATURE_NOT_IMPLEMENTED.getErrorMessage());
		
	}

	@Override
	public void resizeToDeafult() throws Exception {
		throw new Exception(ERROR_MESSAGES.FEATURE_NOT_IMPLEMENTED.getErrorMessage());
		
	}
}
