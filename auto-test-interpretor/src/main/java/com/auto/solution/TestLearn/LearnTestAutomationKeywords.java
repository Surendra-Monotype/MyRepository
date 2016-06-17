package com.auto.solution.TestLearn;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Properties;

public class LearnTestAutomationKeywords {

	private Properties prop = new Properties();
	/**
	 * @author Nayan
	 * @param whereToStudyFrom - Specify the properties file where to study from.
	 * @return Learned Keywords in the form of Map<Key, Aliases[]>.
	 * @throws FileNotFoundException And Exception.
	 */
	public HashMap<String, String[]> learn(String whereToStudyFrom) throws FileNotFoundException,Exception{
		
		HashMap<String, String[]> learnedTestKeyword = new HashMap<String,String[]>();
		
		try{
			prop.load(new FileInputStream(whereToStudyFrom));
			
			for (Object Keyword : prop.keySet()) {
			
				String values = prop.getProperty((String) Keyword);				
				
				values = values + ";" + Keyword.toString(); // Add key also to the list of aliases.
				
				values = values.toLowerCase();
				
				values = values.replaceAll(" ", ""); // remove all spaces.
				
				values = values.replaceAll(";;", ";");
				
				String[] aliases = values.split(";");
				
				learnedTestKeyword.put(Keyword.toString(), aliases);
				
			}
		}
		catch(FileNotFoundException fe){
			throw fe;
		}
		catch(Exception e){
			throw e;
		}
		return learnedTestKeyword;
	}
}
