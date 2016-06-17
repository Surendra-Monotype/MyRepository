package com.auto.solution.TestEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.time.StopWatch;
import org.dbunit.dataset.ITable;

import com.auto.solution.Common.*;
import com.auto.solution.Common.Property.ERROR_MESSAGES;
import com.auto.solution.DatabaseManager.ConnectDatabase;
import com.auto.solution.HTMLReport.HTMLReportGenerator;
import com.auto.solution.TestDrivers.TestSimulator;
import com.auto.solution.TestInterpretor.CompilerFactory;
import com.auto.solution.TestInterpretor.ICompiler;
import com.auto.solution.TestLogging.ITestExecutionDetailsContainer;
import com.auto.solution.TestLogging.TestExecutionDetailsContainer;
import com.auto.solution.TestLogging.TestLogger;
import com.auto.solution.TestManager.*;
import com.auto.solution.TestReporting.*;


public class TestEngine {
	
	private TestEngineHelper engineHelper;
	
	private Utility utils;
		
	private Boolean IsAnyTestStepFailedDuringExecution = false;
	
	ITable TestORSet = null;
	
	private TestManagerFactory testManagerFactoryInstance = null;
	
	private ITestManager testManager = null;
	
	public String StepAction;
    
    public ResourceManager rManager;
    
    public static ArrayList<String> TestCaseIDsForExecution = new ArrayList<String>();
    
    boolean IsWriteStep = false;
    
    public TestSimulator objTestSimulator;
    
    private String testExecutionLogFileName;
    
	private ArrayList<ArrayList<String>> testStepsDetailsForATestCase = new ArrayList<ArrayList<String>>();
    
    ITestExecutionDetailsContainer objTestExecutionDetails = TestExecutionDetailsContainer.accessTestExecutionDetailsContainer(); 
    
    private HashMap<String, ArrayList<ArrayList<String>>> testCasesWithTestStepDetails = new HashMap<String, ArrayList<ArrayList<String>>>();
    
    public static TestLogger logger = null;
    
    public TestEngine(TestEngineHelper engineHelper) {
		this.engineHelper = engineHelper;
	}
    
    private void ExecuteTestCase(String TestCaseIDToExecute,String testScenarioForTestCase, ArrayList<Integer> testIterationValueForTestCase,boolean isTestCaseInvockedAsSubTestCase) throws Exception{
		
    	boolean isCurrentTestCaseFailed = false;
		
    	String currentTestCaseRemarks = "";
    	
    	try{
		 
    		ITestManager testExecutionManager = testManagerFactoryInstance.getTestManager(false);
    		
    		testExecutionManager.locateRepositories(testScenarioForTestCase);
						
    		testExecutionManager.connectRepositories();
							
			List<String> listOfTestStepInTestCase = testExecutionManager.getTestStepsForTestCase(TestCaseIDToExecute);			
			
			CompilerFactory factoryInstanceToGetCompilerForTestStep = new CompilerFactory(Property.STRATEGY_TO_USE_COMPILER,rManager);
			
			ICompiler testStepCompiler = factoryInstanceToGetCompilerForTestStep.getCompiler();
			
			ArrayList<String> listOfVariableUsedForSpecifyingConditionalTestStep = new ArrayList<String>();
			
			
			
			for (String currentTestStep : listOfTestStepInTestCase) {
				
				Property.TEST_STEP_ID = 
				
				Property.StepDescription = currentTestStep;;
				
				Property.Remarks = "";
				
				Property.StepStatus = "";
				
				testStepCompiler.setStepDefenitionToCompile(currentTestStep);
				
				String testObjectToBeUsedForTestStep = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(testStepCompiler.getObjectDefenition());
				
				String testStepAction = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(testStepCompiler.getStepAction());
				
				String testData = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(testStepCompiler.getTestData());
				
				String options = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(testStepCompiler.getStrategyApplied());
				
				String conditionSpecifiedForTestStep = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(testStepCompiler.getConditionForConditionalTestStep());
				
				Boolean executeTestStep = Utility.decideToExecuteTestStepOnTheBasisOfConditionSpecifiedForTestStep(conditionSpecifiedForTestStep, listOfVariableUsedForSpecifyingConditionalTestStep); 
				
				String  subTestCaseInvocked = testStepCompiler.getSubTestCaseInvockedInTestStep();				
				
				if(!executeTestStep){
					continue;
				}
				if(subTestCaseInvocked != ""){
					
					String testSuiteForSubTestCase = "";
					
					String subTestCaseID = "";
					
					ArrayList<Integer> listOfIterationsForSubTestCaseInvocked = new ArrayList<Integer>();
					
					try{
					//TODO : Add logic of adding the test scenario of the parent test case if not mentioned.
						
						String[] parsedTestCaseDetailsInCurrentTestStep = subTestCaseInvocked.split(":");
						
						testSuiteForSubTestCase = parsedTestCaseDetailsInCurrentTestStep[0].trim();
						
						subTestCaseID = parsedTestCaseDetailsInCurrentTestStep[1].trim();
						
						/*
						 * parse the content to populate the iterationList.
						 * So Key points - 
						 * Main test case must mention the test data iterations in curly braces in sequential order.
						 * For example - If we want to invoke a test case having 5 test data (D1,D2,D3,D4,D5). Then mention @I {1,2,1,6,5};
						 *  where numbers denotes the row number from where the data would be picked.
						*/
						
						String iterationContentsSpecifiedForSubTestCase =  Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(testStepCompiler.getIterations()); //Collect the iteration content from main test case.
												
						listOfIterationsForSubTestCaseInvocked = testStepCompiler.parseAndGetTheListOfIterationIndexForSubTest(iterationContentsSpecifiedForSubTestCase);
					}
					catch(ArrayIndexOutOfBoundsException ae){
						throw new Exception(ERROR_MESSAGES.ER_SPECIFYING_TESTCASE_TO_INVOKE.getErrorMessage());
					}
					
					testStepAction = Property.INTERNAL_TESTSTEP_KEYWORD;
					
					// If reusable test case need to be repeated.
					if(listOfIterationsForSubTestCaseInvocked.size() > 1){
						
						for(int iteration : listOfIterationsForSubTestCaseInvocked){
							
							Utility.setKeyValueToGlobalVarMap("iteration", String.valueOf(iteration));
							ArrayList<Integer> iterationListForThisRepeatition = new ArrayList<Integer>();
							iterationListForThisRepeatition.add(iteration);							
							ExecuteTestCase(subTestCaseID, testSuiteForSubTestCase,iterationListForThisRepeatition,true);
						}
					}					
					else{
							ExecuteTestCase(subTestCaseID, testSuiteForSubTestCase,listOfIterationsForSubTestCaseInvocked,true);
					}
					
				}
				
				HashMap<String, String> objectDefenition = testExecutionManager.getActualObjectDefination(testObjectToBeUsedForTestStep);
				
				objTestSimulator.enableTestDriver(Property.EXECUTION_TEST_DRIVER);
				
				objTestSimulator.setTestObjectInfo(objectDefenition);
				
				/*
				 * Prepare Test Data.
				 */
				List<Integer> lstOfIterationIndexes = new ArrayList<Integer>();
				
				String IterationContentSpecifiedForTestStep = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(testStepCompiler.getIterations());
				
				lstOfIterationIndexes = testStepCompiler.parseAndGetTheListOfIterationIndexForSubTest(IterationContentSpecifiedForTestStep);
				
				//Get test data
				if(testData == ""){
									
					/*If Iteration is empty that means take the data from first row only (ie index = 1) .
					 * If iteration has only single value take the data from specified row.
					 * If iteration is specified in range like [1 to 5] then iterate that particular test step with different test data taken from the rows in range sequentially.
					*/
					if(lstOfIterationIndexes.isEmpty()){
						lstOfIterationIndexes.add(1);
					}
					
					
					for (Integer rowIndexInTestDataRepository : lstOfIterationIndexes) {
						
						Utility.setKeyValueToGlobalVarMap("iteration_index", String.valueOf(rowIndexInTestDataRepository));
						
						testData = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(testExecutionManager.fetchTestDataRepositoryContent(rowIndexInTestDataRepository.toString() + ":" + testObjectToBeUsedForTestStep));
						
						if(testData != "" && testIterationValueForTestCase != null && !testIterationValueForTestCase.isEmpty()){
							// IF user mentioned the iteration like {2,3,4} then test data would be picked (in reusable test case) from specified rows ie. from 2nd row for first test data and so on.
							
							String rowIndexTestData = testIterationValueForTestCase.get(0).toString();
							
							testData = Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(testExecutionManager.fetchTestDataRepositoryContent(rowIndexTestData + ":" + testObjectToBeUsedForTestStep));
						}
						
						objTestSimulator.simulateTestStep(testStepAction, testData, testObjectToBeUsedForTestStep, options,(testStepAction.equals(Property.INTERNAL_TESTSTEP_KEYWORD)));
					}
					
				}
				else{
					if(!lstOfIterationIndexes.isEmpty()){
						for(Integer iterationIndex : lstOfIterationIndexes){
							Utility.setKeyValueToGlobalVarMap("iteration_index", String.valueOf(iterationIndex));
							objTestSimulator.simulateTestStep(testStepAction, testData, testObjectToBeUsedForTestStep, options,(testStepAction.equals(Property.INTERNAL_TESTSTEP_KEYWORD)));
						}
					}
					objTestSimulator.simulateTestStep(testStepAction, testData, testObjectToBeUsedForTestStep, options,(testStepAction.equals(Property.INTERNAL_TESTSTEP_KEYWORD)));
				}
				
				if(conditionSpecifiedForTestStep.contains(Property.CONDITIONAL_KEYWORD_SEPERATOR)){
					
					String[] conditionalVariables = conditionSpecifiedForTestStep.split("\\" + Property.CONDITIONAL_KEYWORD_SEPERATOR);
					
					if(Property.StepStatus == Property.PASS)
							listOfVariableUsedForSpecifyingConditionalTestStep.add(conditionalVariables[0].trim());
					else
							listOfVariableUsedForSpecifyingConditionalTestStep.add(conditionalVariables[1].trim());
					
					Property.StepStatus = Property.PASS;
				}
				
				if(Property.StepStatus == Property.FAIL) { 
					IsAnyTestStepFailedDuringExecution = true; isCurrentTestCaseFailed = true; currentTestCaseRemarks = Property.Remarks;
					}				
				//Prepare step execution detail list.
				
				ArrayList<String> lstTestStepExecutionDetails = new ArrayList<String>();
				
				lstTestStepExecutionDetails.add(Property.StepStatus);
				
				lstTestStepExecutionDetails.add(testObjectToBeUsedForTestStep);
				
				lstTestStepExecutionDetails.add(testData);
				
				lstTestStepExecutionDetails.add(Property.Remarks);
				
				lstTestStepExecutionDetails.add(Property.StepDescription);
				
				lstTestStepExecutionDetails.add(Property.StepSnapShot);
				
				if(!testStepAction.equals(Property.INTERNAL_TESTSTEP_KEYWORD))
				testStepsDetailsForATestCase.add(lstTestStepExecutionDetails);				
				
				String teststep_details = Utility.getTestStepDetailsString(testStepAction, Property.StepStatus, Property.Remarks, testObjectToBeUsedForTestStep, testData);
				
				if((!testStepAction.equals(Property.INTERNAL_TESTSTEP_KEYWORD)))
				{ logger.INFO(teststep_details);}
				
				//loggerForTestExecution.writeStepLog(Property.StepDescription,testStepAction, Property.StepStatus,Property.Remarks,testObjectToBeUsedForTestStep,testData,(!testStepAction.equals(Property.INTERNAL_TESTSTEP_KEYWORD)));
				
				if(Property.LIST_STRATEGY_KEYWORD.contains(Property.STRATEGY_KEYWORD.CRITICAL.toString()) && Property.StepStatus.equals(Property.FAIL)){
					return;
				}
			}
		}
		catch(NullPointerException e){
		    throw e;
			//throw new NullPointerException("Execution has unexpectedly broken.");
		}
		catch(Exception e){
			throw e;
		}
	}

    public void prepareTestEngineForExecution(String projectBasePath){
		try{
			
			rManager = new ResourceManager(projectBasePath);
			
			engineHelper.updateTargetAndResourcesIfProvided(rManager);
			
			utils = new Utility(rManager);
			
			utils.loadPropertiesDefinedForExecution();
			
			Utility.populateGlobalVarMapWithPropertiesDefinedInPropertiesFile();
			
			Properties systemProperties = System.getProperties();
			
			Utility.storeSystemPropertiesToGlobalVarMap(systemProperties);
			
			Utility.showAllTestEnginePropertiesOnConsole();
			
			IsWriteStep = true;	
			
			engineHelper.setConfigurationInputsToSharedObject();
			
			testExecutionLogFileName = Property.PROJECT_NAME + "_" + Utility.getCurrentTimeStampInAlphaNumericFormat();
			
			logger = TestLogger.getInstance(rManager.getTestExecutionLogFileLocation().replace("{0}", testExecutionLogFileName + ".txt"));
			
			logger.setLogLevel(Property.Logger_Level);
			
			try{
			if(Utility.getValueForKeyFromGlobalVarMap(Property.DB_CONNECT_FLAG_KEY).equalsIgnoreCase(Property.TRUE)){
				ConnectDatabase.connectToAllDatabase();
			}
			}
			catch(NullPointerException ne){
				
			}
		}
		catch (Exception e) {
			logger.ERROR(e.getMessage());
			//loggerForTestExecution.logMessageConsole(e.getMessage());
		}
	}
	
	public void initiateExecution() throws Exception {
		
		try{
			
		String testManagerToolDefinedByUser = Property.PROJECT_TESTMANAGEMENT_TOOL;
		
		testManagerFactoryInstance = new TestManagerFactory(testManagerToolDefinedByUser,rManager);
		
		testManager = testManagerFactoryInstance.getTestManager(true);
		
		objTestSimulator = new TestSimulator(rManager);
		
		HashMap<String, Set<String>> filteredTestExecutionHierarchy = testManager.getTestSuiteAndTestCaseHierarchyForExecution();
		
		HashMap<String,HashMap<String,Set<String>>> completeTestHierarchy = testManager.prepareAndGetCompleteTestHierarchy();
		
		objTestExecutionDetails.getTestExecutionContainer().setListOfTestGroupSelectedForExecution(testManager.getTestGroupsForExecution());
		
		List<String> testScenarioListInExecutionGroup = new ArrayList<String>();			
		
		objTestExecutionDetails.getTestExecutionContainer().setCompleteTestExecutionHierarchyDetails(completeTestHierarchy);
		 
		testScenarioListInExecutionGroup.addAll(filteredTestExecutionHierarchy.keySet());
		
		logger.INFO("Execution logs [" + Property.PROJECT_NAME + "] at " + Utility.getCurrentTimeStampInAlphaNumericFormat() + ": \n");
		
		//loggerForTestExecution.prepareLoggingHeaderForTestexecution();
				 
		if(testScenarioListInExecutionGroup.size() == 0){
			  
				String message = ERROR_MESSAGES.NO_TEST_SCENARIOS_In_TEST_GROUP.getErrorMessage().replace("{TEST_GROUP}", Property.TEST_EXECUTION_GROUP);
			  
				logger.ERROR(message);
		}
		  Property.ExecutionStartTime = Utility.getCurrentDateAndTimeInStringFormat();
		  
		  for (String testScenarioToExecute : testScenarioListInExecutionGroup) {
					
			Property.CURRENT_TESTSUITE = testScenarioToExecute;
			
			logger.INFO("TestSuite : " + testScenarioToExecute + "\n");
			logger.INFO("***********************************************************");
			
			//loggerForTestExecution.logHeaderForTestSuite(testScenarioToExecute);
			
			Set<String> listOfTestCasesInTestSuite = filteredTestExecutionHierarchy.get(testScenarioToExecute);
			
			if(listOfTestCasesInTestSuite == null){
				
				//loggerForTestExecution.logMessageConsole(ERROR_MESSAGES.ER_IN_SPECIFYING_TEST_SCENARIO.getErrorMessage().replace("{$TESTSCENARIONAME}", testScenarioToExecute));
				logger.ERROR(ERROR_MESSAGES.ER_IN_SPECIFYING_TEST_SCENARIO.getErrorMessage().replace("{$TESTSCENARIONAME}", testScenarioToExecute));
				
				IsAnyTestStepFailedDuringExecution = true;
				
				continue;				
			}			
		
			StopWatch watchTestSuite = Utility.getAndStartStopWatch();
			
			for (String testCase : listOfTestCasesInTestSuite) {
				
				String CurrentTestCaseID = testCase;
				
				Property.CURRENT_TESTCASE = CurrentTestCaseID;
				
				logger.INFO("TestCase : " + testCase + "\n");
				//loggerForTestExecution.logTestCaseHeader(testCase);
				
				logger.INFO("Execution start for " + CurrentTestCaseID);
				
				//loggerForTestExecution.logMessageConsole("Execution start for " + CurrentTestCaseID);
				
				testStepsDetailsForATestCase = new ArrayList<ArrayList<String>>();
				
				StopWatch stopwatch = Utility.getAndStartStopWatch();
				
				ExecuteTestCase(CurrentTestCaseID, testScenarioToExecute,null,false);
				
				String timeTakenByTestCaseInSeconds = Utility.haltAndGetTotalTimeRecordedInStopWatch(stopwatch);
				
				Property.mapOfTestCasesAndTimeTakenByThem.put(CurrentTestCaseID, timeTakenByTestCaseInSeconds);
				
				testCasesWithTestStepDetails.put(CurrentTestCaseID, testStepsDetailsForATestCase);
				
				try{
				objTestSimulator.simulateTestStep("shutdown", "", "", "",false);
				}
				catch(Exception e){					
				}
				
				logger.INFO("Execution ends for " + CurrentTestCaseID);
				//loggerForTestExecution.logMessageConsole("Execution ends for " + CurrentTestCaseID);
			}	
			String timeTakenByTestSute = Utility.haltAndGetTotalTimeRecordedInStopWatch(watchTestSuite);
			
			Property.mapOfTestSuitesAndTimeTakenByThem.put(testScenarioToExecute, timeTakenByTestSute);
		}
		
		Property.ExecutionEndTime = Utility.getCurrentDateAndTimeInStringFormat();
		
		objTestExecutionDetails.getTestExecutionContainer().setMapOfTestCasesAndTheirTestStepsWithDetails(testCasesWithTestStepDetails);

		objTestExecutionDetails.getTestExecutionContainer().prepareMapOfTestCasesAndTheirExecutionStatus();
		
		objTestExecutionDetails.getTestExecutionContainer().prepareMapOfTestSuitesAndTheirExecutionStatus();
		
		}
		catch(Exception e){
			IsAnyTestStepFailedDuringExecution = true;
			throw e;			
		}
		
		
	}	
	public void logExecutionDetailsIntoXml() throws Exception{
		try{
			new XMLReporting(true, Property.XMLFileName + ".xml", objTestExecutionDetails.getTestExecutionContainer(),rManager);
			}
			catch(Exception e){
				throw new Exception(Property.ERROR_MESSAGES.ERR_GENERATING_XML.getErrorMessage() + ":- " + e.getMessage());
			}
	}
	public void initateReportingOfTestExecutionDetails() throws Exception{
		try{
			String sourceFile = rManager.getTestExecutionLogFileLocation().replace("{0}", Property.XMLFileName + ".xml");
			String targetFile = rManager.getTestExecutionLogFileLocation().replace("{0}", testExecutionLogFileName + ".html");
			String helperFileName = Property.REPORTXSLFILE;
			HTMLReportGenerator objReporter = new HTMLReportGenerator(sourceFile, targetFile);
			objReporter.generateHtmlReport(helperFileName);
		}
		catch(Exception e){
			throw new Exception(Property.ERROR_MESSAGES.ERR_INITIATING_REPORT.getErrorMessage() + ":- " + e.getMessage());
		}
	}
	
	public boolean isAnyTestStepFailedDuringTestExecution(){
		return this.IsAnyTestStepFailedDuringExecution;
	}
}
