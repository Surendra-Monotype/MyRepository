
package com.auto.solution.TestManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.auto.solution.Common.Utility;

public interface ITestManager {

	public void locateRepositories(String testSuiteName);
	
	public void connectRepositories() throws Exception;
	
	public String fetchObjectRepositoryContent(String Keyword);
	
	public String fetchTestDataRepositoryContent(String Keyword);
	
	public String fetchTestCaseRepositoryContent(String Keyword);
	
	public HashMap<String, String> getActualObjectDefination(String logicalNameOfTheObject) throws Exception;
	
	public List<String>  getTestStepsForTestCase(String testCaseID) throws Exception;
	
	//public void reportTestCaseResult(String testCaseName,String status,String remarks,boolean needToReport) throws Exception;
	
	public List<String> getTestGroupsForExecution() throws Exception;
	
	public HashMap<String, Set<String>> getTestSuiteAndTestCaseHierarchyForExecution() throws Exception;
	
	public HashMap<String,HashMap<String, Set<String>>> prepareAndGetCompleteTestHierarchy() throws Exception;
		
	
}

abstract class TestManagerUtils {
	
	private File[] staticTestDataProperties = null;
	protected TestManagerUtils(){
		
	}
	
	File[] connectToStaticTestDataProperties(String targetLocation) throws Exception{
		
		try{
			
			File file = new File(targetLocation);
			File[] propertiesFile  = null;
			if(file.exists()){
			 propertiesFile = new File(targetLocation).listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".properties");
				}
			});
			}
			return propertiesFile;
		}
		catch(Exception e){
			throw e;
		}
	}
	
	void setAllKeysInLocalTestDataToGlobalVarMap(File[] staticTestDataProperties) throws Exception{
		
		try{
			for (File propertyFile : staticTestDataProperties) {
				
				InputStream instream = new FileInputStream(propertyFile);
				
				Properties prop = new Properties();
				
				prop.load(instream);
				
				Enumeration<?> e = prop.propertyNames();
				
				while(e.hasMoreElements()){
					
					String key = e.nextElement().toString();
					
					String valueString = prop.getProperty(key);
					
					String value = valueString;
					
					int i = 0;
					
					if(valueString.contains(",")){
					
						String[] values = valueString.split(",");
						
						for (String valuecontent : values) {
						
							value = valuecontent;
							
							key = key + "[" + i + "]";
							
							Utility.setKeyValueToGlobalVarMap(key, value);
						}
					}
					else{
					
						Utility.setKeyValueToGlobalVarMap(key, value);
					}
				}
			}
		}
		catch(Exception e){
		throw e;
		}
	}
}
