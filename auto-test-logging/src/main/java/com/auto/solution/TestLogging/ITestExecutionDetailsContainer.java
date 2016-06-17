package com.auto.solution.TestLogging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface ITestExecutionDetailsContainer {

public TestExecutionDetailsContainer getTestExecutionContainer();

public String getStartTimeForExecution();

public String getEndTimeForExecution();

public String getTestCaseStatus(String testCaseId);

public String getTestSuiteStatusInTestGroup(String testSuite,String testGroup);

public int getTotalTestCasesPassed();

public int getTotalTestCasesFailed();

public int getTotalNumberOfTestCases();

public String getTestGroupExecuted();

public HashMap<String, ArrayList<ArrayList<String>>> getMapOfTestCasesAndTheirTestStepsWithDetails();

public String getTestGroupStatus(String testGroupName);

public List<Integer> getTestCaseNumbersDetailsForTestGroup(String testGroupName);

public List<String> getListOfFailedTestCases();

public HashMap<String,HashMap<String,Set<String>>> getCompleteTestExecutionHierarchyDetails();
	
}
