package com.auto.solution.TestInterpretor;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public interface ICompiler {

	public void setStepDefinitionToCompile(String stepDefinition);
	
	public String getStepAction() throws FileNotFoundException;
	
	public String getObjectDefinition();
	
	public String getTestData();
	
	public String getStrategyApplied();
	
	public String getSubTestCaseInvokedInTestStep();
	
	public String getIterations();
	
	public String getConditionForConditionalTestStep();
	
	public ArrayList<Integer> parseAndGetTheListOfIterationIndexForSubTest(String iterationContent)throws Exception;
	
	}
