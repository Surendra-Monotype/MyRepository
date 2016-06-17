package com.auto.solution.TestExecutor;

import java.io.File;

import com.auto.solution.Common.Property;
import com.auto.solution.TestEngine.TestEngine;
import com.auto.solution.TestEngine.TestEngineHelper;
import com.auto.solution.TestLogging.TestLogger;

public class TestRunner {

	private String getProjectBasePath(){
		String projectBasePath = new File(new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).getParent()).getParent();
		return projectBasePath;
	}
	public static void main(String[] args) throws Exception {
		try{
			TestRunner runner = new TestRunner();
			
			TestEngineHelper helper = new TestEngineHelper();	
			
			TestEngine engine = new TestEngine(helper);
			
			engine.prepareTestEngineForExecution(runner.getProjectBasePath());
			
			engine.initiateExecution();
			
			engine.logExecutionDetailsIntoXml();
			
			engine.initateReportingOfTestExecutionDetails();
			
			System.out.println(engine.isAnyTestStepFailedDuringTestExecution());
			
			if(engine.isAnyTestStepFailedDuringTestExecution()){
				TestLogger.getInstance(null).ERROR(Property.ERROR_MESSAGES.TEST_FAILURE.getErrorMessage());
				throw new Exception(Property.ERROR_MESSAGES.TEST_FAILURE.getErrorMessage());
		}		
		}
		catch(Exception e){
			throw e;
		}

	}

}
