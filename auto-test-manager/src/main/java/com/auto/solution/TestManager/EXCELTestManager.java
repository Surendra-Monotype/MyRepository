/**
 * 
 */
package com.auto.solution.TestManager;


import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.excel.XlsDataSet;

import com.auto.solution.Common.*;
import com.auto.solution.Common.Property.ERROR_MESSAGES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.io.*;

public class EXCELTestManager implements ITestManager {

	private String testCaseRepository;
	
	private String testDataRepository;
	
	private String objectRepository;
	
	private String testCaseRepositorySheetName;
	
	private String testDataRepositorySheetName;
	
	private String objectRepositorySheetName;
	
	private ITable testCaseTable = null;
	
	private ITable testDataTable = null;
	
	private ITable objectRepoTable = null;
	
	private HashMap<String, String[]> testGroupHierarchy = new HashMap<String, String[]>();
	
	private HashMap<String, List<String>> mapOfTestGroupsAndTheirTestSuites = new HashMap<String, List<String>>();
	
	private ResourceManager rm;
	
	/**
	 * Public Constructor which sets the test suite to locate. 
	 */
	public EXCELTestManager(ResourceManager rm){
		testCaseRepositorySheetName = Property.TestCaseSheet;
		testDataRepositorySheetName = Property.TestDataSheetName;
		objectRepositorySheetName = Property.ObjectRepositorySheetName;
		this.rm = rm;
	}
	
	@Override
	public void locateRepositories(String testSuiteName) {
		
		//testCaseRepository = "src" + Property.FileSeperator + "main"+ Property.FileSeperator + "resources" + Property.FileSeperator + Property.PROJECT_NAME+ Property.FileSeperator + "TestSuites" + Property.FileSeperator + testSuiteName + ".xls";
		testCaseRepository = rm.getTestSuiteLocationInFileSystem().replace("{PROJECT_NAME}", Property.PROJECT_NAME);
		testCaseRepository = testCaseRepository + testSuiteName + ".xls";
		
		testDataRepository = testCaseRepository;
		
		objectRepository = rm.getObjectRepositoryFileLocation().replace("{PROJECT_NAME}", Property.PROJECT_NAME);		
		

	}

	@Override
	public void connectRepositories() throws Exception  {
		
		//connect to test case repository.
		try {
			testCaseTable = null;
			testDataTable = null;
			objectRepoTable = null;
			testCaseTable = connectRepository(testCaseRepository, testCaseRepositorySheetName);
			testDataTable = connectRepository(testDataRepository, testDataRepositorySheetName);
			objectRepoTable = connectRepository(objectRepository, objectRepositorySheetName);
		} 
		catch (Exception e) {
			throw e;
		}
		
	}
	
	/**
	 * @author Nayan
	 * @param Keyword  -  Specify the Keyword to fetch the data in following format: <br> <b>"rownumber:keyword".</b>
	 * @return String data value.
	 */
	@Override
	public String fetchObjectRepositoryContent( String Keyword) {
		
		String datavalue = "";
		try{
		String[] contents = Keyword.split(":");
		
		int row = Integer.parseInt(contents[0]);
		
		datavalue = objectRepoTable.getValue(row, contents[1]).toString();				
		}
		catch(Exception e){
			return "";
		}
		return datavalue;
	}

	/**
	 * @author Nayan
	 * @param Keyword -  Specify the Keyword to fetch the data in following format: <br> <b>"rownumber:keyword".</b>
	 *  @return String data value.
	 */
	@Override
	public String fetchTestDataRepositoryContent(String Keyword) {
		String datavalue = "";
		try{
		String[] contents = Keyword.split(":");
		
		int row = Integer.parseInt(contents[0]);
		
		String columnName = contents[1].toString();
		
		datavalue = testDataTable.getValue(row-1, columnName).toString();				
		}
		catch(Exception e){
			return "";
		}
		return datavalue;
	}

	/**
	 * @author Nayan
	 * @param Keyword -  Specify the Keyword to fetch the data in following format: <br> <b>"rownumber:keyword".</b>
	 *  @return String data value.
	 */
	@Override
	public String fetchTestCaseRepositoryContent(String Keyword) {
		
		String datavalue = "";
		try{
		String[] contents = Keyword.split(":");
		
		int row = Integer.parseInt(contents[0]);
		
		datavalue = testCaseTable.getValue(row, contents[1]).toString();				
		}
		catch(Exception e){
			return "";
		}
		return datavalue;
	}	
		
 private ITable connectRepository(String repository,String worksheet) throws Exception{		 
		 
		 ITable objSheet = null;
			try {
				File inFile = new File(repository);
				XlsDataSet ds = new XlsDataSet(inFile);
				objSheet = ds.getTable(worksheet);
			    	
			} 
			catch(DataSetException de){
				throw new DataSetException(ERROR_MESSAGES.ER_CONNECTING_REPOSITORIES.getErrorMessage().replace("{REPOSITORY}", repository));
			}
			catch (Exception e) {
				throw e;
			}
		 return objSheet;
	 }

@Override
public HashMap<String, String> getActualObjectDefination(	String logicalNameOfTheObject) throws Exception {
	
	HashMap<String,String> objDef = new HashMap<String, String>();
	try{
		if(objectRepoTable == null){
			throw new Exception("Locate Object repository to access its data!");
		}
		
		int rowCount = objectRepoTable.getRowCount();
		
		Integer iterativeRow = 0;
		if(logicalNameOfTheObject != ""){	
			
			while(iterativeRow < rowCount){			
			if(fetchObjectRepositoryContent(iterativeRow.toString() + ":" + Property.TESTOBJECT_KEYWORD_IN_ObjectRepository).equals(logicalNameOfTheObject)){
				
				String locatingStrategy = fetchObjectRepositoryContent(iterativeRow.toString() + ":" + Property.Locating_Strategy_Keyword );
				
				String locationOfObject = fetchObjectRepositoryContent(iterativeRow.toString() + ":" + Property.Locating_Value_Keyword_In_OR);
				
				String inFrame = fetchObjectRepositoryContent(iterativeRow.toString() + ":" + Property.TestObject_InFrame_Keyword);
				
				String testObjectFilter = fetchObjectRepositoryContent(iterativeRow.toString() + ":" + Property.TestObject_Filter_Keyword);
				
				objDef.put(Property.TESTOBJECT_KEYWORD_IN_ObjectRepository, logicalNameOfTheObject);
				
				objDef.put(Property.Locating_Strategy_Keyword, locatingStrategy);
				
				objDef.put(Property.Locating_Value_Keyword_In_OR, locationOfObject);
				
				objDef.put(Property.TestObject_InFrame_Keyword, inFrame);
				
				objDef.put(Property.TestObject_Filter_Keyword,testObjectFilter);
				
				break;
			}
			iterativeRow++;
		}
		}
	}
	catch(Exception e){
		throw new Exception(e.getMessage());
	}	
	return objDef;
}

/**
 * @author Nayan
 * @param testCaseID : String Test Case ID.
 * @return List of Test Step in Test Case.
 * @throws Exception 
 */
@Override
public List<String> getTestStepsForTestCase(String testCaseID) throws Exception {
	List<String> lstTestStep = new ArrayList<String>();
	try{
		
		int rowIndex = 0;
		
		boolean isTestCaseFound = false; //Flag to handle the logic to get the steps for particular test case.
		
		int rowCount = testCaseTable.getRowCount();
		
		String fetchedTestCaseId = "";
		
		//Iterate through each row.
		while(rowIndex < rowCount){
			
			String testStepDefinition = "";
			
			try{
			
				//Get the value of testCaseId. It can be a blank value || Null Value || Any other testCaseID.				
				fetchedTestCaseId = fetchTestCaseRepositoryContent(rowIndex + ":" + Property.TESTCASE_ID_KEYWORD);}
			
			catch(NullPointerException ne){
				fetchedTestCaseId = "";
			}
			
			//If i got my test case id then i found the correct test case.
			if(fetchedTestCaseId.equalsIgnoreCase(testCaseID)){
				
				isTestCaseFound = true;
			}
				if(isTestCaseFound){
					
					if(fetchedTestCaseId != "" && !fetchedTestCaseId.equals(testCaseID)){
						isTestCaseFound = false;
						break;
					}
					else{
						String testStep = fetchTestCaseRepositoryContent(rowIndex + ":" + Property.TESTSTEP_KEYWORD);
						
						String Options = fetchTestCaseRepositoryContent(rowIndex + ":" + Property.OPTION_KEYWORD);
						
						testStepDefinition = testStep + Options;
						
						lstTestStep.add(testStepDefinition);
					}
				}
					rowIndex ++;	
		}
	}
	catch(Exception e){
		throw e;
	}
	return lstTestStep;
}

@Override
public List<String> getTestGroupsForExecution() throws Exception {
	return null;
}

@Override
public HashMap<String, Set<String>> getTestSuiteAndTestCaseHierarchyForExecution()
		throws Exception {
	// TODO Auto-generated method stub
	return null;
}

@Override
public HashMap<String, HashMap<String, Set<String>>> prepareAndGetCompleteTestHierarchy()
		throws Exception {
	// TODO Auto-generated method stub
	return null;
}
 


 }
