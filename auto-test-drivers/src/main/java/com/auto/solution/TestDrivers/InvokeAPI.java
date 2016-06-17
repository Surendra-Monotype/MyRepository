package com.auto.solution.TestDrivers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.saxon.functions.Replace;

import com.auto.solution.Common.Property;
import com.auto.solution.Common.ResourceManager;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.support.PropertiesMap;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.model.testsuite.TestRunner.Status;
import com.eviware.soapui.model.testsuite.TestSuite;

public class InvokeAPI {
	private String project_name = "";
	
	ResourceManager resource_manager = null;
	
	private String soapui_testSuite_name = "";
	private String soapui_testCase_name = "";
	
	
	InvokeAPI(ResourceManager rManager) {
		this.resource_manager = rManager;
	}
	
	private TestCase accessSoapUIProject() throws Exception{
		try{
			String soapuiProject = resource_manager.getLocationForExternalFilesInResources().replace("{PROJECT_NAME}", Property.PROJECT_NAME).replace("{EXTERNAL_FILE_NAME}", this.project_name + ".xml");
			
			WsdlProject soapui_project = new WsdlProject(soapuiProject);
			
			List<TestSuite> testSuitesInProject = soapui_project.getTestSuiteList();
			
			TestSuite first_testsuite = testSuitesInProject.get(0);
			
			soapui_testCase_name = first_testsuite.getName();
			
			List<TestCase> testCasesInTestSuite = first_testsuite.getTestCaseList(); 
			
			TestCase first_testcase = testCasesInTestSuite.get(0);
			
			soapui_testCase_name = first_testcase.getName();
			
			return first_testcase;
		}
		catch(Exception e){
			String errMessage = Property.ERROR_MESSAGES.ERR_ACCESSING_SOAP_PROJECT.getErrorMessage() + ". " + e.getMessage();
			throw new Exception(errMessage);
		}
	}
	
	protected String getSoapUITestSuiteName(){
		return this.soapui_testSuite_name;
	}
	
	protected String getSoapUITestCaseName(){
		return this.soapui_testCase_name;
	}
	
	public List<String> invoke(String soapui_projectname, HashMap<String, String> propertiesMap) throws Exception{
		this.project_name = soapui_projectname;
		
		List<String> testCaseStatusWithReason = new ArrayList<String>();
		
		try{	
		
			TestCase soapui_testcase = this.accessSoapUIProject();
	
			PropertiesMap mapOfProperties = new PropertiesMap();
			
			if(propertiesMap != null)
			{
				for (String property_key : propertiesMap.keySet()) {
					String property_value = propertiesMap.get(property_key);
					soapui_testcase.setPropertyValue(property_key, property_value);
				}
			}
			
			TestCaseRunner testRunner = soapui_testcase.run(mapOfProperties, false);
		
			Status status = testRunner.getStatus();
		
			//while(status != Status.FINISHED || status != Status.FAILED){}
		
			String reason = testRunner.getReason();
		
			testCaseStatusWithReason.add(status.toString());
		
			testCaseStatusWithReason.add(reason);
		}
		catch(Exception e){
			throw e;
		}
		return testCaseStatusWithReason;
	}
	
	
	
	
}
