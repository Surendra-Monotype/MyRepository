package com.auto.solution.TestReporting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.auto.solution.Common.Property;
import com.auto.solution.Common.ResourceManager;
import com.auto.solution.TestLogging.TestExecutionDetailsContainer;
import com.auto.solution.TestLogging.TestLogger;


public class XMLReporting {

	
	
	private String XmlFileName = "";
	
	private DocumentBuilderFactory xmlDomFactory;
	
	Document xmlDocument = null;
	
	File XmlFile;
	
	Element testExecution;
	
	private TestExecutionDetailsContainer objTestDetails = null;
	
	private ResourceManager rManager;
	
	
	public XMLReporting(boolean createXML,String xmlFileName,TestExecutionDetailsContainer objTestExecutionDetails,ResourceManager rm){
		this.rManager = rm;
		if(createXML){
			
			this.XmlFileName = xmlFileName;
			
			this.objTestDetails = objTestExecutionDetails;
			
			this.generateXmlFile();
			
			this.prepareSummary();
			
			this.prepareResults();
			
			this.transformToXML();
			
			this.prepareSummaryPropertyFile();
		}
	}
	
	private void generateXmlFile(){		
		
		String XmlFilePath = rManager.getTestExecutionLogFileLocation().replace("{0}", XmlFileName);
				
		XmlFile = new File(XmlFilePath);
		
		XmlFilePath = XmlFile.getAbsolutePath();
		
		XmlFile = new File(XmlFilePath);
		
		  if(!XmlFile.exists()){
			  
			  XmlFilePath.getBytes();  
				
//			  try {
//					//boolean isCreated = XmlFile.createNewFile();
//				} catch (IOException e) {
//					//TODO : Log the warn message.
//				}

		  }
	}
	
	private void prepareSummary(){
		try{
		xmlDomFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder xmlDocBuilder = xmlDomFactory.newDocumentBuilder();
		xmlDocument = xmlDocBuilder.newDocument();
		
		testExecution = xmlDocument.createElement("testExecution");
		xmlDocument.appendChild(testExecution);
		
		Element testExecutionSummaryNode = xmlDocument.createElement("summary");
		
		testExecution.appendChild(testExecutionSummaryNode);
		
		 
		Element startTimeElement =  xmlDocument.createElement("startTime");
		String startTime = objTestDetails.getStartTimeForExecution() == null ? "" : objTestDetails.getStartTimeForExecution();
		startTimeElement.appendChild(xmlDocument.createTextNode(startTime));
		testExecutionSummaryNode.appendChild(startTimeElement);
		
		Element endTimeElement =  xmlDocument.createElement("endTime");
		String endTime = objTestDetails.getEndTimeForExecution() == null ? "" : objTestDetails.getEndTimeForExecution();
		endTimeElement.appendChild(xmlDocument.createTextNode(endTime));
		testExecutionSummaryNode.appendChild(endTimeElement);
		
		Element totalTimeElement =  xmlDocument.createElement("totalTime");
		testExecutionSummaryNode.appendChild(totalTimeElement);
		
		Element testCasePassedElement =  xmlDocument.createElement("testcasepassed");
		//String testCasePassed = String.valueOf(objTestDetails.getTotalTestCasesPassed()) == null ? "" : String.valueOf(objTestDetails.getTotalTestCasesPassed());
		testCasePassedElement.appendChild(xmlDocument.createTextNode(String.valueOf(objTestDetails.getTotalTestCasesPassed())));
		testExecutionSummaryNode.appendChild(testCasePassedElement);
		
		Element testCaseFailedElement =  xmlDocument.createElement("testcasefailed");
		testCaseFailedElement.appendChild(xmlDocument.createTextNode(String.valueOf(objTestDetails.getTotalTestCasesFailed())));
		testExecutionSummaryNode.appendChild(testCaseFailedElement);
		
		Element testGroupElement = xmlDocument.createElement("testgroups");
		testGroupElement.appendChild(xmlDocument.createTextNode(objTestDetails.getTestGroupExecuted()));
		testExecutionSummaryNode.appendChild(testGroupElement);
		
		Element percentageElement = xmlDocument.createElement("percentPassed");
		percentageElement.appendChild(xmlDocument.createTextNode(""));
		testExecutionSummaryNode.appendChild(percentageElement);
		
		}
		catch(Exception e){
			e.printStackTrace();
			TestLogger.getInstance(null).ERROR("PREPARE SUMMARY -- " + e.getMessage());
		}
		
	}
	
	private void prepareSummaryPropertyFile(){
		String summary = rManager.getTestExecutionLogFileLocation().replace("{0}", Property.PROPERTY_FILE_SUMMARY);
		File summaryFile = null;
		try{
			summaryFile = new File(summary);
			summaryFile.createNewFile();
		}
		catch(Exception e){
			TestLogger.getInstance(null).ERROR("Couldn't create a summary file : " + e.getMessage());
		}
		try{
		Properties summaryPropertyFile = new Properties();
		summaryPropertyFile.load(new FileInputStream(summary));
		summaryPropertyFile.setProperty("startTime", objTestDetails.getStartTimeForExecution());
		summaryPropertyFile.setProperty("endTime", objTestDetails.getEndTimeForExecution());
		summaryPropertyFile.setProperty("testGroups", objTestDetails.getTestGroupExecuted());
		int passed = objTestDetails.getTotalTestCasesPassed();
		int failed = objTestDetails.getTotalTestCasesFailed();
		summaryPropertyFile.setProperty("testPassed", String.valueOf(passed));
		summaryPropertyFile.setProperty("testFailed", String.valueOf(failed));
		float percentPassed = ((float)passed/(passed+failed))*100;
		double percentPassedInDouble = Math.round(percentPassed*100.0)/100.0;
		summaryPropertyFile.setProperty("successRate", String.valueOf(percentPassedInDouble) + "%");
		
		List<String> failedTestCasesList = objTestDetails.getListOfFailedTestCases();
		String failedTestCases = "";
		for (String s : failedTestCasesList)
		{
			failedTestCases += "," + s;
		}
		summaryPropertyFile.setProperty("failedTestCases", failedTestCases);
		
		
		summaryPropertyFile.store(new FileOutputStream(summary), null);
		}
		catch(Exception e){
			TestLogger.getInstance(null).ERROR("Error while writing summary file : " + e.getMessage());
		}
		
		
	}
	
	private void prepareResults(){
		try{
		Element resultElement = xmlDocument.createElement("Results");
		testExecution.appendChild(resultElement);
		
		HashMap<String,HashMap<String,Set<String>>> testExecutionHierarchyDetails = objTestDetails.getCompleteTestExecutionHierarchyDetails();
		
		for (String testgroup : testExecutionHierarchyDetails.keySet()) {
			List<Integer> testCaseNumberDetails = objTestDetails.getTestCaseNumbersDetailsForTestGroup(testgroup);
			Element testPlanElement = xmlDocument.createElement("TestPlan");
			
			Element testplanName = xmlDocument.createElement("name");
			testplanName.appendChild(xmlDocument.createTextNode(testgroup));
			testPlanElement.appendChild(testplanName);
			
			Element statusElement = xmlDocument.createElement("status");
			String status = objTestDetails.getTestGroupStatus(testgroup) == null ? "" : objTestDetails.getTestGroupStatus(testgroup);
			statusElement.appendChild(xmlDocument.createTextNode(status));
			testPlanElement.appendChild(statusElement);
			
			Element totalTestCaseElement = xmlDocument.createElement("TotalTestCase");
			String totalTestCase = testCaseNumberDetails.get(0) == null ? "" : testCaseNumberDetails.get(0).toString();
			totalTestCaseElement.appendChild(xmlDocument.createTextNode(totalTestCase));
			testPlanElement.appendChild(totalTestCaseElement);
			
			Element passedElement = xmlDocument.createElement("passed");
			passedElement.appendChild(xmlDocument.createTextNode(testCaseNumberDetails.get(1).toString()));
			testPlanElement.appendChild(passedElement);
			
			Element failedElement = xmlDocument.createElement("failed");
			failedElement.appendChild(xmlDocument.createTextNode(testCaseNumberDetails.get(2).toString()));
			testPlanElement.appendChild(failedElement);
			
			for(String testSuite : testExecutionHierarchyDetails.get(testgroup).keySet()){
				Element testSuiteElement = xmlDocument.createElement("testsuite");
				
				Element nameElement = xmlDocument.createElement("name");
				nameElement.appendChild(xmlDocument.createTextNode(testSuite));
				testSuiteElement.appendChild(nameElement);
				
				Element testsuiteStatusElement = xmlDocument.createElement("status");
				testsuiteStatusElement.appendChild(xmlDocument.createTextNode(objTestDetails.getTestSuiteStatusInTestGroup(testSuite,testgroup)));
				testSuiteElement.appendChild(testsuiteStatusElement);
				
				Element timeTaKenElement = xmlDocument.createElement("timetaken");
				String timeTaken = Property.mapOfTestSuitesAndTimeTakenByThem.get(testSuite) == null ? "" : Property.mapOfTestSuitesAndTimeTakenByThem.get(testSuite);
				timeTaKenElement.appendChild(xmlDocument.createTextNode(timeTaken));
				testSuiteElement.appendChild(timeTaKenElement);
				
				Set<String> testCasesinTestSuite = testExecutionHierarchyDetails.get(testgroup).get(testSuite);
				for (String testCase : testCasesinTestSuite) {
					Element testCaseElement = xmlDocument.createElement("TestCase");
					
					Element nameTestElement = xmlDocument.createElement("name");
					nameTestElement.appendChild(xmlDocument.createTextNode(testCase));
					testCaseElement.appendChild(nameTestElement);
					
					Element statusTestElement = xmlDocument.createElement("status");
					statusTestElement.appendChild(xmlDocument.createTextNode(objTestDetails.getTestCaseStatus(testCase)));
					testCaseElement.appendChild(statusTestElement);
					
					Element timeTakenTestElement = xmlDocument.createElement("timetaken");
					String timeTakenByTestCase = Property.mapOfTestCasesAndTimeTakenByThem.get(testCase) == null ? "" : Property.mapOfTestCasesAndTimeTakenByThem.get(testCase);
					timeTakenTestElement.appendChild(xmlDocument.createTextNode(timeTakenByTestCase));
					testCaseElement.appendChild(timeTakenTestElement);
					
					ArrayList<ArrayList<String>> testStepsDetailsForATestCase = objTestDetails.getMapOfTestCasesAndTheirTestStepsWithDetails().get(testCase);
					for(ArrayList<String> testStepDetail : testStepsDetailsForATestCase){
						Element testStepElement = xmlDocument.createElement("TestStep");
						
						Element timeTakenElement = xmlDocument.createElement("timetaken");
						timeTakenElement.appendChild(xmlDocument.createTextNode(""));
						testStepElement.appendChild(timeTakenElement);
						
						Element stepStatusElement = xmlDocument.createElement("status");
						String stepStatus = testStepDetail.get(0) == null ? "" : testStepDetail.get(0);
						stepStatusElement.appendChild(xmlDocument.createTextNode(stepStatus));
						testStepElement.appendChild(stepStatusElement);
						
						Element testObjectElement = xmlDocument.createElement("testObectused");
						String testObjectUsed = testStepDetail.get(1) == null ? "" : testStepDetail.get(1);
						testObjectElement.appendChild(xmlDocument.createTextNode(testObjectUsed));
						testStepElement.appendChild(testObjectElement);
						
						Element testDataElement = xmlDocument.createElement("testdata");
						String testData = testStepDetail.get(2) == null ? "" : testStepDetail.get(2);
						testDataElement.appendChild(xmlDocument.createTextNode(testData));
						testStepElement.appendChild(testDataElement);
						
						Element remarksElement = xmlDocument.createElement("remarks");
						String remarks = testStepDetail.get(3) == null ? "" : testStepDetail.get(3);
						remarksElement.appendChild(xmlDocument.createTextNode(remarks));
						testStepElement.appendChild(remarksElement);
						
						Element stepDescriptionElement = xmlDocument.createElement("step_description");
						String stepDescription = testStepDetail.get(4) == null ? "" : testStepDetail.get(4);
						stepDescriptionElement.appendChild(xmlDocument.createTextNode(stepDescription));
						testStepElement.appendChild(stepDescriptionElement);
						
						Element stepSnapShot = xmlDocument.createElement("step_snapshot");
						String snapshotName = testStepDetail.get(5) == null ? "" : testStepDetail.get(5);
						stepSnapShot.appendChild(xmlDocument.createTextNode(snapshotName));
						testStepElement.appendChild(stepSnapShot);
						
						testCaseElement.appendChild(testStepElement);
					}
					
					testSuiteElement.appendChild(testCaseElement);
				}
				
				testPlanElement.appendChild(testSuiteElement);
			}
			resultElement.appendChild(testPlanElement);
		}
		
		
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("PREPARE RESULTS" + e.getMessage());
		}
		
	}

	private void transformToXML(){
		try{
		
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
		
			Transformer transformer = transformerFactory.newTransformer();
			
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		
			DOMSource source = new DOMSource(xmlDocument);
		
			StreamResult result = new StreamResult(XmlFile);
		
			//transformer.transform(source, new StreamResult(new OutputStreamWriter(System.out, "UTF-8")));
			
			transformer.transform(source, result);
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("TRANSFORM EXCEPTION" + e.getMessage());
		}
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
