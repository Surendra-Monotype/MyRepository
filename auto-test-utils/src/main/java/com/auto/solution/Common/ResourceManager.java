package com.auto.solution.Common;

public class ResourceManager {
	
	private String FileSeperator = System.getProperty("file.separator");
	
	private String resourceBasePath = "";
	
	private String targetBasePath = "";
	
	private String projectBasePath = "";
	
	public ResourceManager(String basePath){
		this.projectBasePath = basePath;
		this.resourceBasePath = basePath + FileSeperator + "src" + FileSeperator +"main"+ FileSeperator +"resources";
		this.targetBasePath = basePath + FileSeperator + "target";
		
	}
	
	public void setTargetBaseLocationRelativeToProjectBase(String targetLocation){
		this.targetBasePath = this.projectBasePath + FileSeperator + targetLocation;
	}
	
	public void setResourcesBaseLocationRelativeToProjectBase(String resourceLocation){
		this.resourceBasePath = this.projectBasePath + FileSeperator + resourceLocation;
	}
	
	public String getChromeDriverExecutibleLocation(){
		String chromedriver_location = this.resourceBasePath + FileSeperator + Property.CHROME_EXECUTABLE;
		return chromedriver_location;
	}
	public String getTestExecutionLogFileLocation(){
		String logFileLocation = (this.targetBasePath + FileSeperator + "Execution_Log" + FileSeperator + "{0}").replace(" ", "");
		return logFileLocation; 
	}
	
	public String getUIAutoamtionPropertyFileLocation(){
		String uiAutomationPropertyFileLocation = this.resourceBasePath + FileSeperator + Property.UIAutomationPropertyFileName;
		return uiAutomationPropertyFileLocation;
	}
	
	public String getObjectRepositoryFileLocation(){
		String location_OR = this.resourceBasePath + FileSeperator + "{PROJECT_NAME}" + FileSeperator + "ObjectRepository" + FileSeperator + Property.ObjectRepositoryFileName;
		return location_OR;
	}
	
	public String getLocationForExternalFilesInResources(){
		String externalFileLocation = this.resourceBasePath + FileSeperator + "{PROJECT_NAME}"+ FileSeperator + "External_Files" + FileSeperator + "{EXTERNAL_FILE_NAME}";
		return externalFileLocation;
	}
	
	public String getMobileAPKFileLocation(){
		String apk_file_location = this.resourceBasePath + FileSeperator + "{PROJECT_NAME}" + FileSeperator + "{APK_FILENAME}";
		return apk_file_location;
	}
	
	public String getRecoveryFileLocation(){
		String recovery_file_location = this.resourceBasePath + FileSeperator + "{PROJECT_NAME}"+ FileSeperator + "RecoveryFiles" + FileSeperator + Property.RECOVERY_FILENAME;
		return recovery_file_location;
	}
	
	public String getTestSuiteLocationInFileSystem(){
		String testsuite_location = this.resourceBasePath + FileSeperator + "{PROJECT_NAME}" + FileSeperator + "TestSuites" + FileSeperator;
		return testsuite_location;
	}
	
	public String getTestDriverLearningFileLocation(){
		String learning_file_location = this.resourceBasePath + FileSeperator + Property.LEARNING_FILENAME;
		return learning_file_location;
	}	
	
	public String getTestGroupPropertyFileLocationForFileSystem(){
		String testGroup_file_location = this.resourceBasePath + FileSeperator + Property.TEST_GROUP_FILENAME;
		return testGroup_file_location;
	}
}
