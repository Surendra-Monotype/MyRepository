package com.auto.solution.TestDrivers.RecoveryHandling;

import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.auto.solution.Common.Property;
import com.auto.solution.Common.Property.ERROR_MESSAGES;
import com.auto.solution.Common.ResourceManager;


public class RecoverySupportForSeleniumDriver extends RecoverySupport {

	private WebDriver actionDriver = null;
	public RecoverySupportForSeleniumDriver(WebDriver seleniumDriver,ResourceManager rm) throws Exception{
		this.actionDriver = seleniumDriver;
		try{
		Properties recoveryInfoProperties =  this.locateAndGetRecoverySupportFile(Property.PROJECT_NAME,rm);
		this.readRecoverySupportFile(recoveryInfoProperties);
		}
		catch(Exception e){
		
		}
	}
	
	@Override
	public void doRecovery() throws Exception {
		try{
		for(int recoveryObjectIndex = 0;recoveryObjectIndex < this.recoveryObjectNames.size();recoveryObjectIndex++){
			String recoveryObjectName = this.recoveryObjectNames.get(recoveryObjectIndex);
			String recoveryObjectStrategyToLocate = this.recoveryLocatingStrategiesForObject.get(recoveryObjectIndex);
			String recoveryObjectLocation = this.recoveryObjectLocations.get(recoveryObjectIndex);
			String recoveryActionToPerform = this.recoveryActionsToBeExcutedOnObject.get(recoveryObjectIndex);
			
			WebElement recoveryTestObject = getRecoveryTestObject(recoveryObjectStrategyToLocate, recoveryObjectLocation);
			if(recoveryTestObject == null){continue;}
			performRecoveryActionOnTestObject(recoveryActionToPerform, recoveryTestObject);
			break;
		}
		}
		catch(Exception e){
			throw e;
		}
		
	}
	
	@Override
	public void doRecoveryForSpecialObjectsWithHigherPriority() throws Exception {
		try{
			int objectSize = this.recoveryObjectNames.size();
		for(int recoveryObjectIndex = 0;recoveryObjectIndex < objectSize;recoveryObjectIndex++){
			String recoveryObjectName = this.recoveryObjectNames.get(recoveryObjectIndex);
			String recoveryObjectStrategyToLocate = this.recoveryLocatingStrategiesForObject.get(recoveryObjectIndex);
			String recoveryObjectLocation = this.recoveryObjectLocations.get(recoveryObjectIndex);
			String recoveryActionToPerform = this.recoveryActionsToBeExcutedOnObject.get(recoveryObjectIndex);
			String recoveryObjectPriority = this.recoveryObjectsPriorities.get(recoveryObjectIndex);
			
			if(recoveryObjectPriority.equalsIgnoreCase("high")){
				WebElement recoveryTestObject = getRecoveryTestObject(recoveryObjectStrategyToLocate, recoveryObjectLocation);
				if(recoveryTestObject == null){ continue;}
					performRecoveryActionOnTestObject(recoveryActionToPerform, recoveryTestObject);
					break;
				}
		}
		}
		catch(Exception e){
			throw e;
		}
		
		
	}
	
	private WebElement getRecoveryTestObject(String locatingStrategyForRecoveryObject,String locationOfRecoveryObject){
		List<WebElement> testElements = null;
		WebElement testElement = null;
		try{
			if(locatingStrategyForRecoveryObject.toLowerCase().contains("css")){
				testElements = this.actionDriver.findElements(By.cssSelector(locationOfRecoveryObject));
			}
			else if(locatingStrategyForRecoveryObject.toLowerCase().contains("id")){
				testElements = this.actionDriver.findElements(By.id(locationOfRecoveryObject));
			}
			else if(locatingStrategyForRecoveryObject.toLowerCase().contains("tag")){
				testElements = this.actionDriver.findElements(By.tagName(locationOfRecoveryObject));
			}
			else if(locatingStrategyForRecoveryObject.toLowerCase().contains("class")){
				testElements = this.actionDriver.findElements(By.className(locationOfRecoveryObject));
				}
			else if(locatingStrategyForRecoveryObject.toLowerCase().contains("name")){
				testElements = this.actionDriver.findElements(By.name(locationOfRecoveryObject));
			}
			else if(locatingStrategyForRecoveryObject.toLowerCase().contains("xpath")){
				testElements = this.actionDriver.findElements(By.xpath(locationOfRecoveryObject));
			}
			else if(locatingStrategyForRecoveryObject.toLowerCase().contains("text")){
				testElements = this.actionDriver.findElements(By.linkText(locationOfRecoveryObject));
			}
			
			for (WebElement testObject : testElements) {
				if(testObject.isDisplayed()){
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
	
	private void performRecoveryActionOnTestObject(String actionNameToPerform,WebElement recoveryObject) throws Exception{
		try{
			if(actionNameToPerform.equalsIgnoreCase("click")){
				recoveryObject.click();
			}
			else{
				String errMessage = ERROR_MESSAGES.ER_IN_SPECIFYING_RECOVERY_ACTION.getErrorMessage().replace("{ACTION_NAME}", actionNameToPerform);
				throw new Exception(errMessage);
			}
		}catch(Exception ex){
			throw ex;
		}
		
	}



}
