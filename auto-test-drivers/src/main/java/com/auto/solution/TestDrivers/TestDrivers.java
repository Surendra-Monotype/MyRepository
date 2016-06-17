package com.auto.solution.TestDrivers;


import java.net.MalformedURLException;
import java.util.HashMap;

import org.openqa.selenium.NoSuchElementException;


public interface TestDrivers {

	public void injectTestObjectDetail(TestObjectDetails testObjectDetails);
	
	public void initializeApp(String endpoint) throws MalformedURLException,Exception;
	
	public void hitTwice() throws Exception;
	
	public void pressKeyboardKey(String Key) throws Exception;
	
	public void check() throws NoSuchElementException,Exception;
	
	public void uncheck() throws NoSuchElementException,Exception;
	
	public void sendKey(String text) throws NoSuchElementException,Exception;
	
	public void uploadFile(String text)throws NoSuchElementException,Exception;
	
	public void navigateURL(String URL) throws Exception;
	
	public void isResourceLoaded() throws Exception;
	
	public void isTextPresent(String text) throws Exception;
	
	public void click() throws Exception;
	
	public void shutdown() throws Exception;
	
	public void closeAllBrowsersWindow() throws Exception;
	
	public void isObjectThere() throws Exception;
	
	public void isObjectNotThere() throws Exception;
	
	public String fireEvents(String eventCodeSnippet) throws Exception;
	
	public String select(String itemToSelect) throws Exception;
	
	public String getTestElementAttribute(String propertyToFetch) throws Exception;
	
	public void verifyTestElementAttribute(String propertyToVerify,String expectedValueOfProperty) throws Exception;
	
	public void hover() throws Exception;
	
	public void sleep(long timeInMilliseconds);
	
	public void waitUntilObjectIsThere() throws Exception;
	
	public String saveSnapshotAndHighlightTarget(boolean highlight);
	
	public void swipetoElementVisible(String swipeType) throws Exception;
	
	public String getTestObjectCount() throws Exception;
	
	public void refresh() throws Exception;
	
	public void verifyPageAttribute(String property,String value) throws Exception;

	public String getPageProperties(String propertyName) throws Exception;
	
	public void verifySortByFeature(String sortType) throws Exception;

	public void verifyTestElementAttributeNotPresent(String propertyToVerify,
			String expectedValueOfProperty) throws Exception;
	public void verifyAndReportBrokenLinksFromPages() throws Exception;
	
	public void browserNavigation(String navigationOption) throws Exception;
	
	public void verifyAndReportSCO(String scoUrlSource) throws Exception;
	
	public void handleAlert(String option) throws Exception;
	
	public void verifyAndReportRedirectdUrls() throws Exception;
	
	public void verifyAndReportSectionLinks(String sectionName,String sectionValue) throws Exception;
	
	public void clickOnCo_ordinates(int i,int j) throws Exception;
	
	public String getElementDimension() throws NoSuchElementException, Exception;
	
	public void resizeCurrentWindow(int x_coord, int y_coord) throws Exception;
	
	public void resizeToDeafult() throws Exception;
}
