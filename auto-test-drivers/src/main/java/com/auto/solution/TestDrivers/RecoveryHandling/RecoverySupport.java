package com.auto.solution.TestDrivers.RecoveryHandling;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import com.auto.solution.Common.Property;
import com.auto.solution.Common.Property.ERROR_MESSAGES;
import com.auto.solution.Common.ResourceManager;

public abstract class RecoverySupport {
	
	ArrayList<String> recoveryObjectNames = new ArrayList<String>();
	ArrayList<String> recoveryLocatingStrategiesForObject = new ArrayList<String>();
	ArrayList<String> recoveryObjectLocations = new ArrayList<String>();
	ArrayList<String> recoveryActionsToBeExcutedOnObject = new ArrayList<String>();
	ArrayList<String> recoveryObjectsPriorities = new ArrayList<String>();

	Properties locateAndGetRecoverySupportFile(String projectName,ResourceManager rm) throws Exception{
		Properties propertyInfoToHandleRecoveryActions = new Properties();
		try{		
		String propertyDestination = rm.getRecoveryFileLocation().replace("{PROJECT_NAME}", projectName);
		propertyInfoToHandleRecoveryActions.load(new FileInputStream(propertyDestination));
		}
		catch(Exception e){
			throw e;
		}
		return propertyInfoToHandleRecoveryActions;
	}
	
	void  readRecoverySupportFile(Properties recoveryPropertyFile)throws Exception{
		
		try{
		for(Integer objectIndex = 1;;objectIndex++){
			String suffix = objectIndex.toString();
			
			StringTokenizer recoveryInfoTokenizer = new StringTokenizer(recoveryPropertyFile.getProperty(Property.RECOVERY_OBJECT_NAME_KEYWORD + suffix));
			recoveryObjectNames.add(recoveryInfoTokenizer.nextToken());
			
			recoveryInfoTokenizer = new StringTokenizer(recoveryPropertyFile.getProperty(Property.RECOVERY_OBJECT_LOCATING_STRATEGY_KEYWORD + suffix));
			recoveryLocatingStrategiesForObject.add(recoveryInfoTokenizer.nextToken());
			
			recoveryInfoTokenizer = new StringTokenizer(recoveryPropertyFile.getProperty(Property.RECOVERY_OBJECT_LOCATION_KEYWORD + suffix));
			recoveryObjectLocations.add(recoveryInfoTokenizer.nextToken());
			
			recoveryInfoTokenizer = new StringTokenizer(recoveryPropertyFile.getProperty(Property.RECOVERY_ACTION_KEYWORD + suffix));
			recoveryActionsToBeExcutedOnObject.add(recoveryInfoTokenizer.nextToken());
			
			recoveryInfoTokenizer = new StringTokenizer(recoveryPropertyFile.getProperty(Property.RECOVERY_PRIORITY_KEYWORD + suffix));
			recoveryObjectsPriorities.add(recoveryInfoTokenizer.nextToken().toString());
		}	
		}
		catch(Exception e){
			
		}
	}
	
	abstract void doRecovery() throws Exception;
	
	abstract void doRecoveryForSpecialObjectsWithHigherPriority() throws Exception;
}
