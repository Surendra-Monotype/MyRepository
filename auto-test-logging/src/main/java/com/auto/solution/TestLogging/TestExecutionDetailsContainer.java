package com.auto.solution.TestLogging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.auto.solution.Common.Property;

public class TestExecutionDetailsContainer implements ITestExecutionDetailsContainer{
	
	private List<String> listOfTestGroupSelectedForExecution = new ArrayList<String>();
	
	HashMap<String,HashMap<String,Set<String>>> completeTestExecutionHierarchy = new HashMap<String, HashMap<String,Set<String>>>();
	
	private HashMap<String, List<String>> mapOfTestGroupAndTheirTestCases = new HashMap<String, List<String>>();
	
	private HashMap<String,ArrayList<ArrayList<String>>> mapOfTestCasesAndTheirTestStepsWithDetails = new HashMap<String, ArrayList<ArrayList<String>>>();
	
	private HashMap<String, String> mapOfTestCasesAndItsExecutionStatus = new HashMap<String, String>();
	
	private HashMap<String, HashMap<String,String>> mapOfTestSuiteExecutionStatusCorrespondingToTestGroup = new HashMap<String, HashMap<String,String>>();
	
	private HashMap<String, HashMap<String, Set<String>>> executionDetailMap = new HashMap<String, HashMap<String,Set<String>>>();
	
	private TestExecutionDetailsContainer() { }
	
	public static ITestExecutionDetailsContainer accessTestExecutionDetailsContainer(){
		return new TestExecutionDetailsContainer();	
	}
	
	public void setListOfTestGroupSelectedForExecution(List<String> testGroups){
		this.listOfTestGroupSelectedForExecution = testGroups;
	}
	
	public void setMapOfTestCasesAndTheirTestStepsWithDetails( HashMap<String, ArrayList<ArrayList<String>>> testCasesWithStepDetails){
		this.mapOfTestCasesAndTheirTestStepsWithDetails = testCasesWithStepDetails;
	}
	
	public void setCompleteTestExecutionHierarchyDetails(HashMap<String,HashMap<String,Set<String>>> testExecutionHierarchy){
		this.completeTestExecutionHierarchy = testExecutionHierarchy;
	}
	
	@Override
	public HashMap<String,HashMap<String,Set<String>>> getCompleteTestExecutionHierarchyDetails(){
		return this.completeTestExecutionHierarchy;
	}
	
	@Override
	public HashMap<String, ArrayList<ArrayList<String>>> getMapOfTestCasesAndTheirTestStepsWithDetails(){
		return this.mapOfTestCasesAndTheirTestStepsWithDetails;
	}
		
	public void setMapOfTestGroupAndTheirTestCases(HashMap<String,List<String>> testGroupAndTestCaseDetails){
		this.mapOfTestGroupAndTheirTestCases = testGroupAndTestCaseDetails;
	}
	
	public void prepareMapOfTestCasesAndTheirExecutionStatus(){
		if(!mapOfTestCasesAndTheirTestStepsWithDetails.isEmpty()){			
			for (String testcase : mapOfTestCasesAndTheirTestStepsWithDetails.keySet()) {				
				ArrayList<ArrayList<String>> testStepsWithDetails = mapOfTestCasesAndTheirTestStepsWithDetails.get(testcase);
				
				mapOfTestCasesAndItsExecutionStatus.put(testcase, Property.PASS);
				
				for (ArrayList<String> testStep : testStepsWithDetails) {
					String stepStatus = testStep.get(0);
					if(stepStatus.equals(Property.FAIL)){
						mapOfTestCasesAndItsExecutionStatus.put(testcase, Property.FAIL);
						break;
					}					
				}
			}
		}
	}
	
	public void prepareMapOfTestSuitesAndTheirExecutionStatus(){
		
		HashMap<String,HashMap<String,Set<String>>> testExecutionHierarchy = this.completeTestExecutionHierarchy;
		HashMap<String,HashMap<String,String>> executionStatusHierarchy = new HashMap<String, HashMap<String,String>>();
		
		for (String testGroup : testExecutionHierarchy.keySet()) {
			Set<String> testSuites = testExecutionHierarchy.get(testGroup).keySet();
			HashMap<String,String> testSuiteStatuses = new HashMap<String, String>();
			for (String testSuite : testSuites) {
				Set<String> testCases = testExecutionHierarchy.get(testGroup).get(testSuite);
				for (String testCase : testCases) {
					String testCaseStatus = this.mapOfTestCasesAndItsExecutionStatus.get(testCase);
					testSuiteStatuses.put(testSuite, Property.PASS);
					if(testCaseStatus.equals(Property.FAIL)){
						testSuiteStatuses.put(testSuite, Property.FAIL);
						break;
					}
				}
			}
			executionStatusHierarchy.put(testGroup, testSuiteStatuses);
		}
		this.mapOfTestSuiteExecutionStatusCorrespondingToTestGroup = executionStatusHierarchy;
		
	}
	
	@Override
	public List<Integer> getTestCaseNumbersDetailsForTestGroup(String testGroup){
		
		List<Integer> testCaseNumbersDetailsForAGroup = new ArrayList<Integer>();
		HashMap<String,HashMap<String,Set<String>>> testExecutionHierarchy = this.completeTestExecutionHierarchy;
		int totalNumberOfTestCaseInTestGroup = 0;
		int numberOfTestCasesPassedInTestGroup = 0;
		int numberOfTestCasesFailedInTestGroup = 0;
		if(!testExecutionHierarchy.keySet().contains(testGroup)){
			return null;
		}
		
		for (String testSuite : testExecutionHierarchy.get(testGroup).keySet()) {
			Set<String> testCasesInTestSuite = testExecutionHierarchy.get(testGroup).get(testSuite);
			totalNumberOfTestCaseInTestGroup = totalNumberOfTestCaseInTestGroup + testCasesInTestSuite.size();
			for (String testCase : testCasesInTestSuite) {
				String testCaseStatus = this.mapOfTestCasesAndItsExecutionStatus.get(testCase);
				if(testCaseStatus.equals(Property.PASS)){
					numberOfTestCasesPassedInTestGroup = numberOfTestCasesPassedInTestGroup + 1;
				}
				else{
					numberOfTestCasesFailedInTestGroup = numberOfTestCasesFailedInTestGroup + 1;
				}
			}
		}
				
		testCaseNumbersDetailsForAGroup.add(totalNumberOfTestCaseInTestGroup);
		testCaseNumbersDetailsForAGroup.add(numberOfTestCasesPassedInTestGroup);
		testCaseNumbersDetailsForAGroup.add(numberOfTestCasesFailedInTestGroup);
		return testCaseNumbersDetailsForAGroup;
	}
	
	@Override
	public int getTotalTestCasesPassed(){
		int testCasePassed = 0;
		for (String status : mapOfTestCasesAndItsExecutionStatus.values()) {
			if(status.equals(Property.PASS))
				testCasePassed ++;
		}
		return testCasePassed;
	}
	
	@Override
	public int getTotalTestCasesFailed(){
		int testCaseFailed = 0;
		for (String status : mapOfTestCasesAndItsExecutionStatus.values()) {
			if(status.equals(Property.FAIL))
				testCaseFailed ++;
		}
		return testCaseFailed;
	}
	
	@Override
	public int getTotalNumberOfTestCases(){
		return this.mapOfTestCasesAndItsExecutionStatus.keySet().size();
	}
	
	@Override
	public String getTestGroupExecuted(){
		String testGroupInString = "";
		for (String testGroup : this.listOfTestGroupSelectedForExecution) {
			testGroupInString = testGroupInString + "," + testGroup;
		}
		return testGroupInString;
	}
	@Override
	public String getTestCaseStatus(String testcaseid){
		return this.mapOfTestCasesAndItsExecutionStatus.get(testcaseid);
	}
	
	@Override
	public String getTestSuiteStatusInTestGroup(String testSuite,String testGroup){
		HashMap<String,HashMap<String,String>> testSuiteExecutionStatus = this.mapOfTestSuiteExecutionStatusCorrespondingToTestGroup;
		String testSuiteStatus = testSuiteExecutionStatus.get(testGroup).get(testSuite);
		return testSuiteStatus;
	}

	@Override
	public TestExecutionDetailsContainer getTestExecutionContainer() {
		return this;
	}

	@Override
	public String getStartTimeForExecution() {
		return Property.ExecutionStartTime;
	}

	@Override
	public String getEndTimeForExecution() {
		return Property.ExecutionEndTime;
	}

	@Override
	public String getTestGroupStatus(String testGroupName) {
		String testGroupStatus = Property.PASS;
		
		HashMap<String,HashMap<String, String>> testSuiteStatusDetails = this.mapOfTestSuiteExecutionStatusCorrespondingToTestGroup;
		
		HashMap<String, String> testSuitesInGivenTestGroup = testSuiteStatusDetails.get(testGroupName);
		
		if(testSuitesInGivenTestGroup.containsValue(Property.FAIL)){
			testGroupStatus = Property.FAIL;
		}
		return testGroupStatus;
	}
	
	@Override
	public List<String> getListOfFailedTestCases() {
		List<String> listOfFailedTestCases = new ArrayList<String>();
		try {
			for (String testCaseName : this.mapOfTestCasesAndItsExecutionStatus.keySet()) {
				String status = this.mapOfTestCasesAndItsExecutionStatus.get(testCaseName);
				if (status == Property.FAIL) {
					listOfFailedTestCases.add(testCaseName);
				}
			}
		}
		catch (Exception e) {

		}
		return listOfFailedTestCases;
	}
	
}
