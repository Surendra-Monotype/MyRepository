package com.auto.solution.TestInterpretor;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.auto.solution.Common.Property;
import com.auto.solution.Common.Property.ERROR_MESSAGES;
import com.auto.solution.Common.Property.STRATEGY_KEYWORD;
import com.auto.solution.Common.ResourceManager;
import com.auto.solution.TestLearn.*;
public class Compiler implements ICompiler{

	private String testStepDefenition = "";
	private Pattern p = null;
	private ResourceManager rManager;
	public Compiler(ResourceManager rm){
		this.rManager = rm;
	}
	
	private String parseStepAction(String stepAction) throws FileNotFoundException{
		LearnTestAutomationKeywords objLearn = new LearnTestAutomationKeywords();
		String actualStepAction = stepAction;
		try{
				HashMap<String, String[]> keywordMap = objLearn.learn(rManager.getTestDriverLearningFileLocation());
				for(String key : keywordMap.keySet()){
					String[] aliases = keywordMap.get(key);
					for (String alias : aliases) {
						 if(alias.equals(stepAction)){
							 actualStepAction = key;
							 break;
						 }
					}
					if(actualStepAction == key){break;}
				}
			}
		catch(FileNotFoundException fe){
			throw fe;
		}
			catch(Exception e){			
			}
		return actualStepAction;
	}
	
	private void prepareStrategyForTestStep(String strategyString){
		Property.LIST_STRATEGY_KEYWORD.clear();
		
		for(STRATEGY_KEYWORD keyword : STRATEGY_KEYWORD.values()){
			if(strategyString.toLowerCase().contains(keyword.getStartegy())){
				Property.LIST_STRATEGY_KEYWORD.add(keyword.toString());
			}
		}
		
	}
	
	@Override
	public String getConditionForConditionalTestStep() {
		String conditionAttachedToTestStep = "";
		try{
			p = Pattern.compile("(?<=@C | @c)[^;]+");
			Matcher m = p.matcher(testStepDefenition);
			if(m.find()){
				conditionAttachedToTestStep = m.group();
				conditionAttachedToTestStep = conditionAttachedToTestStep.trim();
			}
		}
		catch(Exception e){
			
		}		
		return conditionAttachedToTestStep;
	}
	@Override
	public String getStepAction() throws FileNotFoundException {
		String stepAction = "";
		try{
		p = Pattern.compile("(?<=@A | @a)[^;]+");
		Matcher m = p.matcher(testStepDefenition);
		if(m.find()){
			stepAction = m.group();
			
			stepAction = stepAction.replaceAll(" ", "");
			
			stepAction = stepAction.trim().toLowerCase();
			
			stepAction = parseStepAction(stepAction);
		}
		}
		catch(FileNotFoundException fe){
			throw fe;
		}
		catch(Exception e){
			stepAction = "";
		}
		
		return stepAction;
	}

	@Override
	public String getObjectDefenition() {
		String objDef = "";
		try{
			p = Pattern.compile("(?<=@O | @o)[^;]+");
			Matcher m = p.matcher(testStepDefenition);
			if(m.find()){
				objDef = m.group();
				objDef = objDef.trim();
			}
			}
			catch(Exception e){
				objDef = "";
			}
		return objDef;
	}

	@Override
	public String getTestData() {
		String testData = "";
		try{
			p = Pattern.compile("(?<=@D | @d)[^;]+");
			Matcher m = p.matcher(testStepDefenition);
			if(m.find()){
				testData = m.group();
				testData = testData.trim();
			}
			
			}
			catch(Exception e){
				testData = "";
			}
		return testData;
	}

	@Override
	public String getStrategyApplied() {
		String strategy = "";
		try{
			p = Pattern.compile("(?<=@S | @s)[^;]+");
			
			Matcher m = p.matcher(testStepDefenition);
			
			if(m.find()){
				
				strategy = m.group();
				
				strategy = strategy.trim();
				
				//remove spaces
				strategy = strategy.replaceAll(" ", "");
				
				strategy = strategy.toLowerCase();		
			}
			prepareStrategyForTestStep(strategy);
			}
			catch(Exception e){
				System.out.println(e.getMessage());
				//TODO Nothing to do in case of exception. Think over it again.
				}
		return strategy;
	}

	@Override
	public void setStepDefenitionToCompile(String testStepDefination) {
		this.testStepDefenition = testStepDefination;
		this.testStepDefenition = this.testStepDefenition.replace("&nbsp;", " ");
		this.testStepDefenition = this.testStepDefenition.replace("&quot;", "\"");
		//handling for single quote
		this.testStepDefenition = this.testStepDefenition.replace("&#39;", "'");
		this.testStepDefenition = this.testStepDefenition.replace("&gt;", ">");
		this.testStepDefenition = this.testStepDefenition.replace("&lt;", "<");
	}

	@Override
	/**
	 * @author Nayan
	 * @return : String in the form of "TestScenario:TestCaseID"
	 **/
	public String getSubTestCaseInvockedInTestStep() {
		String internalTestCase = "";
		try{
			p = Pattern.compile("(?<=@R | @r)[^;]+");
			Matcher m = p.matcher(testStepDefenition);
			if(m.find()){
				internalTestCase = m.group();
				internalTestCase = internalTestCase.trim();
			}
			}
			catch(Exception e){
				internalTestCase = "";
			}
		return internalTestCase;
	}

	@Override
	public String getIterations() {
		String iteration = "";
		try{
			p = Pattern.compile("(?<=@I | @i)[^;]+");
			Matcher m = p.matcher(testStepDefenition);
			if(m.find()){
				iteration = m.group();
				iteration = iteration.trim();
			}
			}
			catch(Exception e){
				iteration = "";
			}
		return iteration;
	}

	@Override
	public ArrayList<Integer> parseAndGetTheListOfIterationIndexForSubTest(String iterationContent) throws Exception{
		
		ArrayList<Integer> lstIteration = new ArrayList<Integer>();
		
		try{
			if(iterationContent == ""){
				return lstIteration;
			}
			if(!iterationContent.contains("{") || !iterationContent.contains("}")){
				throw new Exception(ERROR_MESSAGES.ER_SPECIFYING_ITERATION_CONTENT.getErrorMessage());
			}
			int openCurlyIndex = iterationContent.indexOf("{");
			int closingCurlyIndex = iterationContent.lastIndexOf("}");
			
			String actualContent = iterationContent.substring(openCurlyIndex + 1, closingCurlyIndex);
			
			if(actualContent.contains(",")){
			String[] iterationIndexes = actualContent.split(",");
			
			for (String index : iterationIndexes) {
				if(!index.isEmpty()){
				lstIteration.add(Integer.parseInt(index));}
			}
			}
			else if(actualContent.contains("to")){
				
				String[] iterationRange = actualContent.split("to");
				
				int iterationIndexStart = Integer.parseInt(iterationRange[0].trim());
				
				int iterationIndexEnd = Integer.parseInt(iterationRange[1].trim());
				
				for(int iterationIndex = iterationIndexStart;iterationIndex <= iterationIndexEnd;iterationIndex++){
					lstIteration.add(iterationIndex);
				}
				
			}
			else if(actualContent != ""){
				lstIteration.add(Integer.parseInt(actualContent));
			}
		}
		catch(Exception e){
			throw e;
		}
		return lstIteration;
	}


	
	

}
