package com.auto.solution.Common;

public class ResourceManager {
	
	private String fileSeparator = System.getProperty("file.separator");
	
	private String resourceBasePath = "";
	
	private String targetBasePath = "";
	
	private String projectBasePath = "";
	
	public ResourceManager(String basePath){
		this.projectBasePath = basePath;
		this.resourceBasePath = basePath + fileSeparator + "src" + fileSeparator +"main"+ fileSeparator +"resources";
		this.targetBasePath = basePath + fileSeparator + "target";
		
	}
	
	public void setTargetBaseLocationRelativeToProjectBase(String targetLocation){
		this.targetBasePath = this.projectBasePath + fileSeparator + targetLocation;
	}
	
	public void setResourcesBaseLocationRelativeToProjectBase(String resourceLocation){
		this.resourceBasePath = this.projectBasePath + fileSeparator + resourceLocation;
	}
	
	public String getChromeDriverExecutableLocation(){
		String chromedriver_location = this.resourceBasePath + fileSeparator + Property.CHROME_EXECUTABLE;
		return chromedriver_location;
	}
	public String getTestExecutionLogFileLocation(){
		String logFileLocation = (this.targetBasePath + fileSeparator + "Execution_Log" + fileSeparator + "{0}").replace(" ", "");
		return logFileLocation; 
	}
	
	public String getUIAutomationPropertyFileLocation(){
		String uiAutomationPropertyFileLocation = this.resourceBasePath + fileSeparator + Property.UIAutomationPropertyFileName;
		return uiAutomationPropertyFileLocation;
	}
	
	public String getObjectRepositoryFileLocation(){
		String location_OR = this.resourceBasePath + fileSeparator + "{PROJECT_NAME}" + fileSeparator + "ObjectRepository" + fileSeparator + Property.ObjectRepositoryFileName;
		return location_OR;
	}
	
	public String getLocationForExternalFilesInResources(){
		String externalFileLocation = this.resourceBasePath + fileSeparator + "{PROJECT_NAME}"+ fileSeparator + "External_Files" + fileSeparator + "{EXTERNAL_FILE_NAME}";
		return externalFileLocation;
	}
	
	public String getMobileAPKFileLocation(){
		String apk_file_location = this.resourceBasePath + fileSeparator + "{PROJECT_NAME}" + fileSeparator + "{APK_FILENAME}";
		return apk_file_location;
	}
	
	public String getRecoveryFileLocation(){
		String recovery_file_location = this.resourceBasePath + fileSeparator + "{PROJECT_NAME}"+ fileSeparator + "RecoveryFiles" + fileSeparator + Property.RECOVERY_FILENAME;
		return recovery_file_location;
	}
	
	public String getTestSuiteLocationInFileSystem(){
		String testsuite_location = this.resourceBasePath + fileSeparator + "{PROJECT_NAME}" + fileSeparator + "TestSuites" + fileSeparator;
		return testsuite_location;
	}
	
	public String getTestDriverLearningFileLocation(){
		String learning_file_location = this.resourceBasePath + fileSeparator + Property.LEARNING_FILENAME;
		return learning_file_location;
	}	
	
	public String getTestGroupPropertyFileLocationForFileSystem(){
		String testGroup_file_location = this.resourceBasePath + fileSeparator + Property.TEST_GROUP_FILENAME;
		return testGroup_file_location;
	}
	
	public String getToastScreenshotImageFileLocation(){
		String toast_screenshot_file_location = this.resourceBasePath + fileSeparator + "{PROJECT_NAME}" + fileSeparator + "ObjectRepository" + fileSeparator + Property.ToastScreenshotImageFileName;
		return toast_screenshot_file_location;
	}
	
	public String getObjectRepositoryFolderLocation(){
		String location_OR_Folder = this.resourceBasePath + fileSeparator + "{PROJECT_NAME}" + fileSeparator + "ObjectRepository" + fileSeparator;
		return location_OR_Folder;
	}
}
