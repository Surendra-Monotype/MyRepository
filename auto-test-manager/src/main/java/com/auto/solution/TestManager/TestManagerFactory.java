package com.auto.solution.TestManager;

import com.auto.solution.Common.Property;
import com.auto.solution.Common.ResourceManager;

public class TestManagerFactory {	
	
	String testManagerString = "";
	ResourceManager rmanager;
	public TestManagerFactory(String testManagerKeyword,ResourceManager rm){
	this.testManagerString = testManagerKeyword.toLowerCase();
	this.rmanager = rm;
	}
	
	public ITestManager getTestManager(boolean isTestManagerNeedToReinitialized) throws Exception{
		ITestManager testManager = null;
		if(this.testManagerString.contains("testlink")){
			testManager = new TESTLINKTestManager(isTestManagerNeedToReinitialized,this.rmanager);
		}
		else{
			String err_Message = Property.ERROR_MESSAGES.ERR_SPECIFYING_TESTMANAGER.getErrorMessage().replace("{TESTMANAGERKEY}", testManagerString);
			throw new Exception(err_Message);
		}
		return testManager;
	}
}
