package com.auto.solution.TestManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.record.formula.functions.Files;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.excel.XlsDataSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.auto.solution.Common.Property;
import com.auto.solution.Common.ResourceManager;
import com.auto.solution.Common.Utility;
import com.auto.solution.Common.Property.ERROR_MESSAGES;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestCaseStep;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;
import br.eti.kinoshita.testlinkjavaapi.model.TestSuite;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;


public class TESTLINKTestManager extends TestManagerUtils implements ITestManager {

	
	private String objectRepository;
	
	private static TestLinkAPI testLinkInstance = null;
	
	private URL testLinkURL = null;
	
	private static TestProject testProject = null;
	
	private static Integer testProjectID = null;
	
	private static String objectRepositorySheetName;
	
	private String testSuiteName = "";
	
	private Integer testCaseID;
	
	private TestCase testCase;
	
	private HashMap<String, List<String>> testDataDetailsMap = new HashMap<String, List<String>>();
	
	private ITable objectRepoTable = null;
	
	private HashMap<String, Set<String>> mapOfTestSuiteAndTheirTestCases = new HashMap<String, Set<String>>();
	
	private HashMap<String, List<String>> mapOfTestGroupsAndTheirTestSuites = new HashMap<String, List<String>>();
	
	private HashMap<String, List<String>> mapOfTestGroupsAndTheirTestCases = new HashMap<String, List<String>>();
	
	private List<String> listOfTestGroupSelectedByUser = new ArrayList<String>();
	
	private ResourceManager rm;
	
	/*
	 * Public constructor
	 */
	public TESTLINKTestManager(boolean WantToConnectToTestManager,ResourceManager rm) throws Exception{
		this.rm = rm;
		if(WantToConnectToTestManager){
		try{
			this.testLinkURL = new URL(Property.TEST_MANAGEMENT_URL); 
			testLinkInstance  = new TestLinkAPI(testLinkURL, Property.TEST_MANAGEMENT_KEY);
			try{
				testProject = testLinkInstance.getTestProjectByName(Property.PROJECT_NAME);
				testProjectID= testProject.getId();}
				catch(TestLinkAPIException te){
					throw new Exception(Property.ERROR_MESSAGES.ER_IN_SPECIFYING_TEST_PROJECT.getErrorMessage());
				}
			objectRepositorySheetName = Property.ObjectRepositorySheetName;
			accessLocalTestData(rm.getLocationForExternalFilesInResources().replace("{EXTERNAL_FILE_NAME}", "").replace("{PROJECT_NAME}", Property.PROJECT_NAME));
		}
		catch(MalformedURLException me){
			throw new Exception(Property.ERROR_MESSAGES.ER_CONNECTING_TESTMANAGER.getErrorMessage());
		}
		catch(Exception e){
			throw e;
		}
		}
	}
	
	private void accessLocalTestData(String targetLocation) throws Exception{
		try{
			File[] propertyFiles = connectToStaticTestDataProperties(targetLocation);
			if(propertyFiles != null)
				setAllKeysInLocalTestDataToGlobalVarMap(propertyFiles);
		}
		catch(Exception e){
			String errMessage = Property.ERROR_MESSAGES.ERR_WHILE_PROCESSING_LOCALTESTDATA.getErrorMessage();
			throw new Exception(errMessage + e.getMessage());
		}
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
	
	private void storeParagraphContentAsTestDataFormatInGlobalVarMap(Elements paragraphNodesContent){
		try{
		
			for (Element element : paragraphNodesContent) {
			
				String parahContent = element.text();
			
				if(!parahContent.contains(":")){
				continue;}
				
				else{
				
					String[] splittedContent = parahContent.split(":");
				
					String Key = splittedContent[0].trim();
				
					String[] splittedValues = splittedContent[1].split("||");
					if(splittedValues.length == 1) {
						Utility.setKeyValueToGlobalVarMap(Key, splittedValues[0].trim());
					}
					else{
					int i = 0;				
						for (String value : splittedValues) {
							String originalKey = Key + "[" + i + "]";					
							Utility.setKeyValueToGlobalVarMap(originalKey, value.trim());					
							i++;
						}
					}
				}
			}
		}
		catch(Exception e){
			//Nothing to do here, as test step will fail if test data not set successfully.
		}
	}
	
	private HashMap<String, List<String>> getTestDataDetailsFromTestLinkToMapCollection(String testSuiteName) throws Exception{

		HashMap<String, List<String>> testDataCollection = new HashMap<String, List<String>>();
		
		ArrayList<Integer> testSuiteIds = new ArrayList<Integer>();
		int i = 0;
		
		try{
		TestSuite[] testSuiteInProject = this.testLinkInstance.getFirstLevelTestSuitesForTestProject(testProjectID);
		
		for (TestSuite testSuite : testSuiteInProject) {
			if(testSuite.getName().equals(testSuiteName)){
				testSuiteIds.add(testSuite.getId());
			}
		}
		
		TestSuite[] testSuitesByID = this.testLinkInstance.getTestSuiteByID(testSuiteIds);
		
		String testSuiteDetails = testSuitesByID[0].getDetails();
		
		Document doc = Jsoup.parse(testSuiteDetails);		
		
		Element firstRow = doc.getElementsByTag("tr").first();
		
		Elements paragraphElements = doc.getElementsByTag("p");
		
		this.storeParagraphContentAsTestDataFormatInGlobalVarMap(paragraphElements);
		
		Elements columns = firstRow.getElementsByTag("th");
		
		int numberOfColumns = columns.size();	
		
		while(i < numberOfColumns){				
			String key = "";
			
			
			Elements rows = doc.getElementsByTag("tr");
			
			ArrayList<String> columnValues = new ArrayList<String>();
			
			for (Element row : rows) {
				Elements columnsInRow = row.children();				 
				columnValues.add(columnsInRow.get(i).text());				
			}
			
			key = columnValues.get(0).toString();
			
			columnValues.remove(0);
			
			testDataCollection.put(key, columnValues);
			
			i++;
		}
	}
		catch(NullPointerException ne){
			//No test data defined for test suite.
		}
		catch(TestLinkAPIException te){
			String errMessage = ERROR_MESSAGES.ER_RETRIEVING_TESTCASE.getErrorMessage();
			errMessage = errMessage.replace("{PROJECT}", testProject.getName());
			errMessage = errMessage.replace("{TESTNAME}", testSuiteName);
			throw new Exception(errMessage);
		}
		return testDataCollection;
	}
	
	@Override
	public void locateRepositories(String testSuiteName) {
		
		this.testSuiteName = testSuiteName;
		objectRepository = rm.getObjectRepositoryFileLocation().replace("{PROJECT_NAME}", Property.PROJECT_NAME);
	}

	@Override
	public void connectRepositories() throws Exception {
		testDataDetailsMap.clear();
		
		objectRepoTable = null;		
		
		testDataDetailsMap = getTestDataDetailsFromTestLinkToMapCollection(this.testSuiteName);
		
		objectRepoTable = connectRepository(objectRepository, objectRepositorySheetName);
		
	}

	@Override
	public String fetchObjectRepositoryContent(String Keyword) {
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

	@Override
	public String fetchTestDataRepositoryContent(String Keyword) {
		String datavalue = "";
		try{
		String[] contents = Keyword.split(":");
		
		int row = Integer.parseInt(contents[0]);
		
		String columnName = contents[1].toString();
		
		datavalue = testDataDetailsMap.get(columnName).get(row-1);

		}
		catch(Exception e){
			return "";
		}
		return datavalue;
	}

	@Override
	public String fetchTestCaseRepositoryContent(String Keyword) {
		
		return null;
	}

	@Override
	public HashMap<String, String> getActualObjectDefination(
			String logicalNameOfTheObject) throws Exception {
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

	@Override
	public List<String> getTestStepsForTestCase(String testCaseID) throws Exception {
		
		ArrayList<String> testCaseSteps = new ArrayList<String>();
		
		try{
		Integer testCaseid = testLinkInstance.getTestCaseIDByName(testCaseID, this.testSuiteName, this.testProject.getName(), "");
		
		this.testCaseID = testCaseid;
		
		this.testCase = testLinkInstance.getTestCase(this.testCaseID, null, null);
		
		List<TestCaseStep> testSteps = this.testCase.getSteps();
		
		for (TestCaseStep testCaseStep : testSteps) {
			String testStep = testCaseStep.getActions().replaceAll("\\<.*?>","");
			testCaseSteps.add(testStep);
		}
	}
		catch(Exception e){
			throw new Exception(ERROR_MESSAGES.ER_IN_GETTING_TESTSTEPS_FOR_TESTCASE.getErrorMessage() + " --" + e.getMessage());
		}
		return testCaseSteps;
	}

	@Override
	public HashMap<String, Set<String>> getTestSuiteAndTestCaseHierarchyForExecution() throws Exception{
		
		HashMap<String, Set<String>> testSuiteAndTestCaseDetails = new HashMap<String, Set<String>>();
		
		HashMap<String, List<String>> testGroupAndTestSuiteDetails = new HashMap<String, List<String>>();
		
		HashMap<String, List<String>> testGroupAndTestCaseDetails = new HashMap<String, List<String>>();
		
		try{
		String[]  groupMentionedByUser = Utility.getValueForKeyFromGlobalVarMap("execution.group").toLowerCase().split(",");
		
		TestPlan[] existingTestGroupsInProject = testLinkInstance.getProjectTestPlans(testProjectID);
		
		List<String> groupMentionedByUserAsList = Arrays.asList(groupMentionedByUser);		
		
		if(groupMentionedByUser.length < 1 || (groupMentionedByUser.length == 1 && groupMentionedByUser[0] == "")){			
				
			groupMentionedByUserAsList.clear();
				
			for (TestPlan testGroup : existingTestGroupsInProject) {
					
				String testGroupName = testGroup.getName().toLowerCase();
					
				groupMentionedByUserAsList.add(testGroupName);				
				
			}		
		}	
		
		this.listOfTestGroupSelectedByUser.addAll(groupMentionedByUserAsList);
		
		for (String testGroup : groupMentionedByUserAsList) {
			
			TestPlan currentTestPlan = null;
			
			ArrayList<String> testSuiteListForCurrentGroup = new ArrayList<String>();
			
			try{
			currentTestPlan = testLinkInstance.getTestPlanByName(testGroup, testProject.getName());
			}
			catch(TestLinkAPIException te){
				throw new Exception(te.getMessage());
			}
			TestCase[] testCasesForCurrentTestPlan = testLinkInstance.getTestCasesForTestPlan(currentTestPlan.getId(), null, null, null, null, null, null, null, ExecutionType.AUTOMATED, true,null);
			List<String> testCaseListInGroup = new ArrayList<String>();
			for (TestCase testCase : testCasesForCurrentTestPlan) {
				
				TestCase currentTestCaseInTestPlan = testLinkInstance.getTestCase(testCase.getId(), null, null);
				
				String currentTestCaseName = currentTestCaseInTestPlan.getName();
				
				testCaseListInGroup.add(currentTestCaseName);
				
				Integer testSuiteID = currentTestCaseInTestPlan.getTestSuiteId();
				
				ArrayList<Integer> testSuiteIdsInList = new ArrayList<Integer>();
				
				testSuiteIdsInList.add(testSuiteID);
				
				String testSuiteName;
				
				try{
				testSuiteName = testLinkInstance.getTestSuiteByID(testSuiteIdsInList)[0].getName();
				}
				catch(Exception e){
					String err_message = Property.ERROR_MESSAGES.ERR_GETTING_TESTSUITE_FOR_TESTCASE.getErrorMessage().replace("{TESTCASENAME}", currentTestCaseName);
					throw new Exception(err_message);
				}
				
				if(testSuiteAndTestCaseDetails.containsKey(testSuiteName)){
					Set<String> existingTestCaseList = testSuiteAndTestCaseDetails.get(testSuiteName);
					
					existingTestCaseList.add(currentTestCaseName);
					
					testSuiteAndTestCaseDetails.put(testSuiteName, existingTestCaseList);
				}
				else{
					Set<String> testCaseList = new HashSet<String>();
					
					testCaseList.add(currentTestCaseName);
					
					testSuiteAndTestCaseDetails.put(testSuiteName, testCaseList);
				}
				
				if(!testSuiteListForCurrentGroup.contains(testSuiteName)){
					testSuiteListForCurrentGroup.add(testSuiteName);
				}
			}
			testGroupAndTestCaseDetails.put(testGroup, testCaseListInGroup);
			testGroupAndTestSuiteDetails.put(testGroup, testSuiteListForCurrentGroup);
		}		
		}
		catch(Exception e){
			throw new Exception(e.getMessage());
		}
		
		this.mapOfTestGroupsAndTheirTestSuites = testGroupAndTestSuiteDetails;
		
		this.mapOfTestGroupsAndTheirTestCases = testGroupAndTestCaseDetails;
		
		this.mapOfTestSuiteAndTheirTestCases = testSuiteAndTestCaseDetails;
		
		return testSuiteAndTestCaseDetails;
	}
	
	@Override
	public HashMap<String,HashMap<String, Set<String>>> prepareAndGetCompleteTestHierarchy() throws Exception{
		try{
		HashMap<String, List<String>> testGroupAndTestCases = mapOfTestGroupsAndTheirTestCases;
		
		HashMap<String, List<String>> testGroupAndTestSuites = mapOfTestGroupsAndTheirTestSuites;
		
		HashMap<String, HashMap<String, Set<String>>> testGroupTestSuiteAndTestCasesDetails = new HashMap<String, HashMap<String,Set<String>>>();
		
		for (String testGroup : testGroupAndTestSuites.keySet()) {
			
			HashMap<String,Set<String>> testSuiteAndTestCases = new HashMap<String, Set<String>>();
			
			testSuiteAndTestCases = this.mapOfTestSuiteAndTheirTestCases;
			
			List<String> testSuitesInTestGroup = new ArrayList<String>();
			
			testSuitesInTestGroup = testGroupAndTestSuites.get(testGroup);
			
			HashMap<String, Set<String>> testSuiteAndTestCasesForThisGroup = new HashMap<String, Set<String>>();
			
			for (String testSuite : testSuitesInTestGroup) {
				
				HashMap<String,Set<String>> testSuiteDetails = new HashMap<String, Set<String>>(testSuiteAndTestCases);
				
				Set<String> testCasesForCurrentTestSuite = new HashSet<String>();
				
				Set<String> testCasesForCurrentGroup = new HashSet<String>(testGroupAndTestCases.get(testGroup));
				
				for(String testCase : testSuiteDetails.get(testSuite)){
				
					if(testCasesForCurrentGroup.contains(testCase)){
						testCasesForCurrentTestSuite.add(testCase);
					}
				}				
				
				testSuiteAndTestCasesForThisGroup.put(testSuite, testCasesForCurrentTestSuite);
			}
			testGroupTestSuiteAndTestCasesDetails.put(testGroup, testSuiteAndTestCasesForThisGroup);
		}		
		return testGroupTestSuiteAndTestCasesDetails;
		}
		catch(Exception e){
			throw e;
		}
	}
	
//	@Override
//	public void reportTestCaseResult(String testCaseName,String status, String remarks, boolean needToReport)	throws Exception {
//		String groupName = "";
//		ExecutionStatus executionStatus;
//		if(needToReport)
//		{
//		if(status.equals(Property.PASS)){
//			executionStatus = ExecutionStatus.PASSED;
//		}
//		else{
//			executionStatus = ExecutionStatus.FAILED;
//		}
//		
//		try{
//			
//			if(this.testHierarchyOnFilter != null){
//				for(String group : testHierarchyOnFilter.keySet()){
//					List<String> testSuites = testHierarchyOnFilter.get(group);
//					if(testSuites.contains(this.testSuiteName)){
//						groupName = group;
//						List<String>  groupMentionedByUser = Arrays.asList(Utility.getValueForKeyFromGlobalVarMap("execution.group").toLowerCase().split(","));
//						if(groupMentionedByUser.contains(groupName)){
//						break;}
//					}
//				}
//				Integer testCaseid = testLinkInstance.getTestCaseIDByName(testCaseName, this.testSuiteName, this.testProject.getName(), "");
//				Integer testPlanId = testLinkInstance.getTestPlanByName(groupName, Property.PROJECT_NAME).getId();
//				Integer buildId = null;
//				
//				try{
//					Build[] testBuilds = testLinkInstance.getBuildsForTestPlan(testPlanId);
//				for(Build build : testBuilds){
//					buildId = build.getId();
//				}
//				}
//				catch(Exception e){
//					throw new Exception(ERROR_MESSAGES.ER_IN_GETTING_TEST_EXECUTION_BUILD_TEST_MANAGEMENT_TOOL.getErrorMessage());
//				}
//				testLinkInstance.reportTCResult(testCaseid, null, testPlanId,executionStatus, buildId, null, remarks, null, null, null, null, null, true);
//			}
//		}
//		catch(Exception e){
//			throw new Exception(ERROR_MESSAGES.ER_IN_REPORTING_TESTCASE_STATUS.getErrorMessage() + e.getMessage());
//		}
//		}
//	}

	@Override
	public List<String> getTestGroupsForExecution() throws Exception {				
		return this.listOfTestGroupSelectedByUser;
	}
}
