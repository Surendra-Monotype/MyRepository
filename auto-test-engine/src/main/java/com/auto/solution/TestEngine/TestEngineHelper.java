package com.auto.solution.TestEngine;

import com.auto.solution.Common.Property;
import com.auto.solution.Common.ResourceManager;
import com.auto.solution.Common.Utility;
import com.auto.solution.Common.Property.ERROR_MESSAGES;
import com.auto.solution.TestLogging.TestLogger;

public class TestEngineHelper {

	void updateTargetAndResourcesIfProvided(ResourceManager rm){
		try{
			String resourceDirectoryProvided = System.getProperty("resourcedirectory");
			String targetProvided = System.getProperty("targetname");
			if(resourceDirectoryProvided != null){
				rm.setResourcesBaseLocationRelativeToProjectBase(resourceDirectoryProvided);
			}
			if(targetProvided != null){
				rm.setTargetBaseLocationRelativeToProjectBase(targetProvided);
			}
		}
		catch(Exception e){
			//Nothing to do here.
		}
	}
	
	void setConfigurationInputsToSharedObject(){
		
		try{
		Property.OSString = System.getProperty("os.name") + " " + System.getProperty("os.version");;
		
		Property.PROJECT_NAME = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(Utility.getValueForKeyFromGlobalVarMap("project.name"));
		
		Property.TEST_MANAGEMENT_URL = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(Utility.getValueForKeyFromGlobalVarMap("project.testmanagement.url"));
		
		Property.TEST_MANAGEMENT_KEY = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(Utility.getValueForKeyFromGlobalVarMap("project.testmanagement.key"));
		
		Property.TEST_MANAGEMENT_USERNAME = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(Utility.getValueForKeyFromGlobalVarMap("project.testmanagement.username") == null ? "" : Utility.getValueForKeyFromGlobalVarMap("project.testmanagement.username"));
		
		Property.BrowserName = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(Utility.getValueForKeyFromGlobalVarMap("drivercapability.browserName"));
		
		Property.SyncTimeOut = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(Utility.getValueForKeyFromGlobalVarMap("MaximumTimeout"));
		
		Property.IsRemoteExecution = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(Utility.getValueForKeyFromGlobalVarMap("execution.remote"));
		
		Property.IsSauceLabExecution = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(Utility.getValueForKeyFromGlobalVarMap("execution.saucelab") == null ? "false" : Utility.getValueForKeyFromGlobalVarMap("execution.saucelab") );
		
		Property.ApplicationURL = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(Utility.getValueForKeyFromGlobalVarMap("application.url"));
		
		Property.RemoteURL = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(Utility.getValueForKeyFromGlobalVarMap("execution.remote.url"));
		
		Property.PROJECT_TESTMANAGEMENT_TOOL = Utility.getValueForKeyFromGlobalVarMap("project.testmanagement").trim().toLowerCase();
		
		Property.EXECUTION_TEST_DRIVER = Utility.getValueForKeyFromGlobalVarMap("execution.test.driver").trim().toLowerCase();
		
		
		
		Property.DEBUG_MODE = Utility.getValueForKeyFromGlobalVarMap("execution.debugmode").trim().toLowerCase();
		
		Property.TEST_EXECUTION_GROUP  = Utility.getValueForKeyFromGlobalVarMap("execution.group").trim().toLowerCase();
		
		Property.APP_PLATFORM_VERSION = Utility.getValueForKeyFromGlobalVarMap("drivercapability.platformVersion").trim();
		
		Property.APP_PLATFORM_NAME = Utility.getValueForKeyFromGlobalVarMap("drivercapability.platformName").trim();
		
		
		
		Property.DEVICE_NAME = Utility.getValueForKeyFromGlobalVarMap("drivercapability.deviceName").trim();
		
		Property.DEVICE_UDID = Utility.getValueForKeyFromGlobalVarMap("drivercapability.udid").trim();
		
		Property.Logger_Level = (Utility.getValueForKeyFromGlobalVarMap("execution.logger.level") == null ? "" : Utility.getValueForKeyFromGlobalVarMap("execution.logger.level").trim());
		
		Property.APP_ACTIVITY = Utility.getValueForKeyFromGlobalVarMap("drivercapability.appActivity").trim();
		
		Property.APP_PACKAGE = Utility.getValueForKeyFromGlobalVarMap("drivercapability.appPackage").trim();
		
		Property.BROWSER_COOKIES = (Utility.getValueForKeyFromGlobalVarMap("browser.cookie") == null ? "" : Utility.getValueForKeyFromGlobalVarMap("browser.cookie").trim());
		
		Property.BROWSER_COOKIE_TRIAL = (Utility.getValueForKeyFromGlobalVarMap("browser.cookie.trial") == null ? "0" :Utility.getValueForKeyFromGlobalVarMap("browser.cookie.trial").trim());
		
		
		}
		catch(Exception e){
			TestLogger.getInstance(null).INFO(ERROR_MESSAGES.ER_WHILE_SETTING_CONFIGURATION_PROPERTY.getErrorMessage());
		}
	}
}
