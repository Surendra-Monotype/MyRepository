package com.auto.solution.Common;

import java.util.ArrayList;
import java.util.HashMap;


public class Property {
	
	public static String UIAutomationPropertyFileName = "uiautomation.properties";
	
	public static String ObjectRepositoryFileName = "ObjectRepository.xls";
	
	public static String RECOVERY_FILENAME = "recovery.properties";
	
	public static String LEARNING_FILENAME = "learnCompiler.properties";
	
	public static String TEST_GROUP_FILENAME = "groups.properties";

	public static String PROPERTY_FILE_SUMMARY = "summary.properties";
	
	public static String DB_CONNECT_FLAG_KEY = "dbconnectionrequired";
	
	public static String REPORTXSLFILE = "Report.xsl"; 
	
	public static String TRUE = "true";
	
	public static String TEST_STEP_ID = "";
	
	public static String XMLFileName = "ExecutionDetails";
	
	public static String CURRENT_TEST_GROUP = "";
	
	public static String CURRENT_TESTSUITE = "";
			
	public static String CURRENT_TESTCASE = "";
	
	public static String CURRENT_TESTSTEP = "";
	
	public static String TEST_GROUPNAME_FOR_INTERNAL_TESTCASE = "TC_INTERNAL";
	
	public static String TEST_DATA_FILE = "TestData";
	
	public static String APP_PLATFORM_VERSION = "";
	
	public static String APP_PLATFORM_NAME = "";
	
	public static String DEVICE_NAME = "";
	
	public static String DEVICE_UDID = "";
	
	public static String APP = "";
	
	public static String APP_ACTIVITY = "";
	
	public static String APP_PACKAGE = "";
	
	public static String DRIVER_CAPABILITY_KEYWORD = "drivercapability";
	
	public static String TEST_MANAGEMENT_URL = "";
	
	public static String CONDITIONAL_KEYWORD_SEPERATOR = "|";
	
	public static String TEST_MANAGEMENT_KEY = "";
	
	public static String TEST_MANAGEMENT_USERNAME = "";
	
	public static String CHROME_EXECUTABLE = "chromedriver.exe";
	
	public static String CHROME_EXECUTABLE_SH = "chromedriver";

	public static String PHANTOM_EXECUTABLE = "phantomjs.exe";
	
	public static String INTERNET_EXPLORER_KEYWORD = "ie";
	
	public static String FIREFOX_KEYWORD = "firefox";
	
	public static String CHROME_KEYWORD = "chrome";
	
	public static String PHANTOM_KEYWORD = "phantom";
	
	public static String DEBUG_MODE = "";
	
	public static String PROJECT_NAME;
	
	public static String PROJECT_TESTMANAGEMENT_TOOL = "";
	
	public static String TESTCASE_ID_KEYWORD = "testcase_id";
	
	public static String TESTSTEP_KEYWORD = "testStep";
	
	public static String OPTION_KEYWORD = "options";
	
	public static String FileSeperator = System.getProperty("file.separator");
	
	public static String TEST_EXECUTION_GROUP = "";
	
	public static String ObjectRepositorySheetName = "object_defination";
	
	public static String TestCaseSheet = "test_flow";  
	
	public static String TestDataSheetName = "test_data";
	
	public static String TestDataSheet = Property.TestDataSheetName;  
	
	public static String STRATEGY_TO_USE_COMPILER = "Simple";
	
	public static String BDDFileQuery = "select * from [Steps$]";
	
	public static String TESTOBJECT_KEYWORD_IN_ObjectRepository = "testobject";
	
	public static String Firefox_Keyword_In_ObjectRepo = "ff";
	
	public static String IE_Keyword_In_ObjectRepo = "ie";
	
	public static String Chrome_Keyword_In_ObjectRepo = "chrome";
	
	public static String Locating_Strategy_Keyword = "locatingStrategy";
	
	public static String TestObject_InFrame_Keyword = "InFrame";
	
	public static String TestObject_Filter_Keyword = "TestObjectFilter";
	
	public static String Frame_Details_Seperator = ",";
	
	public static String Locating_Value_Keyword_In_OR = "location";
	
	public static HashMap<String, String> mapOfTestCasesAndTimeTakenByThem = new HashMap<String, String>();
	
	public static HashMap<String, String> mapOfTestSuitesAndTimeTakenByThem = new HashMap<String, String>();
	
	public static String Resource_Base_Path = "src" + FileSeperator +"main"+ FileSeperator +"resources";
	
	//Dynamic JDBC Connection strings declarations.
	public static String JDBCConnectionStringObjectRepository = "jdbc:odbc:Driver={Microsoft Excel Driver (*.xls, *.xlsx, *.xlsm, *.xlsb)};DBQ={0};READONLY=TRUE";
	
	public static String JDBCConnectionStringTestCase = "jdbc:odbc:Driver={Microsoft Excel Driver (*.xls, *.xlsx, *.xlsm, *.xlsb)};DBQ=" + "{0}" + ";READONLY=TRUE";
	
	public static String JDBCConnectionStringTestData = "jdbc:odbc:Driver={Microsoft Excel Driver (*.xls, *.xlsx, *.xlsm, *.xlsb)};DBQ={0};READONLY=TRUE";
	
	public static String JDBCConnectionStringBDDFile = "jdbc:odbc:Driver={Microsoft Excel Driver (*.xls, *.xlsx, *.xlsm, *.xlsb)};DBQ={0};READONLY=TRUE";
	
	public static String IsSauceLabExecution = "false";
	
	public static String IsRemoteExecution = "false";
	
	public static String RemoteURL = "";
	
	public static HashMap<String, String> paramMap = new HashMap<String, String>();
	
	public static String BrowserName = "firefox";
	
	public static String TestSuite = "";
	
	public static String RECOVERY_OBJECT_NAME_KEYWORD = "RecoveryObjectName";
	
	public static String RECOVERY_OBJECT_LOCATING_STRATEGY_KEYWORD = "LocatingStrategy";
	
	public static String RECOVERY_OBJECT_LOCATION_KEYWORD = "Location";
	
	public static String RECOVERY_ACTION_KEYWORD  = "RecoveryAction";
	
	public static String RECOVERY_PRIORITY_KEYWORD = "RecoveryPriority";
	
	public static HashMap<String, String> globalVarMap = new HashMap<String, String>();
	
	protected static HashMap<String,Object> globalObjectCollections = new HashMap<String, Object>();
	
	public static String Remarks = "";
	
	public static String TEST_DRIVER_KEY = "test_driver";
	
	public static final String PASS = "pass";
	
	public static final String FAIL = "fail";
	
	public static String StepStatus = "";
	
	public static String ApplicationURL = "";
	
	public static String SEPERATOR = "#";
	
	public static String TESTDATA_SEPERATOR = "#";
	
	public static String COOKIE_SEPERATOR = "###";
	
	public static String BROWSER_COOKIES = "";
	
	public static String COOKIE_VALUE = "cookieValue";
	
	public static String COOKIE_DOMAIN_NAME = "cookieDomainName";
	
	public static String Pending = "pending";
	
	public static String Verified = "verified";
	
	public static String BROWSER_COOKIE_TRIAL = "";
	
	public static String StepDescription = "";
	
	public static String StepSnapShot = "";
	
	public static String SyncTimeOut = "";
	
	public static String ExecutionStartTime = "";
	
	public static String ExecutionEndTime = "";
	
	public static String OSString = "";
	
	public static String StepExecutionTime = "";
	
	public static String KeyIgnoreStep = "ignorestep";
	
	public static String DESKTOP_WEB_TESTDRIVER_KEYWORD = "desktop";
	
	public static String MOBILE_APP_ANDRIOD_TESTDRIVER_KEYWORD = "android";
	
	public static String MOBILE_WEB_TESTDRIVER_KEYWORD = "mobile";
	
	public static String MOBILE_IOS_TESTDRIVER_KEYWORD = "ios";
	
	public static String EXECUTION_TEST_DRIVER = "";
	
	public static String INTERNAL_TESTSTEP_KEYWORD = "internal-stepsaction";
	
	public static String Logger_Level = "";
	
	public static String Logger_Level_INFO = "info";
	
	public static String Logger_Level_WARN = "warn";
	
	public static String Logger_Level_ERROR = "error";
	
	public static String Logger_Level_ALL = "all";
	
	public static String Logger_Level_DEBUG = "debug";
	
	public static String Logger_Level_OFF = "off";
	
	public static String Logger_Level_FATAL = "fatal";
	
	public static String Logger_Log4j_Properties = "log4j.properties";
	
	public static String Logger_LogFile_Name = "log4j-application.log";
	
	public static String TEST_STEP_LOG_ENTRY = "------------------------------------------------------------------------------------\n" + 
	 "Test Step Description : {TEST_STEP_NAME} \n" + "Test Step Action : {TEST_STEP_ACTION} \n" + "Test object : {TEST_OBJECT} \n" + 
	 "Test Data used -- '{TEST_DATA}' \n" + "Status : {STATUS} \n" + "Remarks : {REMARKS} \n" + "Execution Time : {EXECUTION_TIME} \n" +
	 "------------------------------------------------------------------------------------\n";
	
	
	public static ArrayList<String > LIST_STRATEGY_KEYWORD = new ArrayList<String>();
	
	public static enum FILTERS{
		IS_ENABLED("enabled"),
		IS_DISPLAYED("displayed"),
		IS_CLICKABLE("clickable");
		private String filter;
		private FILTERS(String filter){
			this.filter = filter;
		}
		public String getFilter(){
			return this.filter;
		}
		
	}
	public static enum ERROR_MESSAGES{
		ER_SPECIFY_BROWSER ("Incorrect browser specification."),
		ER_GET_TESTOBJECT("Failed to get the test object, kindly reverify the object locators."),
		ER_GETTING_DRIVER_SELENIUM("Couldn't get the driver(Selenium One)  instance"),
		ER_SPECIFYING_KEYBOARD_KEY("Incorrect keyboard key specified."),
		ER_SPECIFYING_OBJECT("Error in specifying test object"),
		ER_SPECIFYING_TESTDATA("Error in specifying test data"),
		ER_SPECIFYING_INPUT("Incorerct input specified '{INPUT}'"),
		ER_ASSERTION("Runtime assertion failed, test data used -- {$TESTDATA} and we found -- {ACTUAL_DATA}."),
		STEP_IGNORED("The test step is marked to be ignored."),
		ER_MISSING_TESTDATA("Test Data missing, please provide proper test data."),
		ER_NO_STEP_ACTION("Engine is confused with the step action provided.Please be precise!"),
		ER_ELEMENT_NOT_PRESENT("Test Element not available"),
		ER_ELEMENT_NOT_DISPLAYED("Test Element is present but not displayed"),
		ER_IN_SPECIFYING_TEST_SCENARIO("Test scenario mentioned '{$TESTSCENARIONAME}' is not written yet, please cross check!"),
		ER_COULDNOT_SELECT_VALUE("Sorry Test engine could not select the option -- {$OPTION}."),
		ER_SPECIFYING_CSS_PROPERTY("Getting the CSS property has a particular format like 'Css:background-image', this will give the background image."),
		ER_SPECIFYING_ITERATION_CONTENT("Engine found inappropriate contents in specifying test data iterations"),
		ER_SPECIFYING_TESTCASE_TO_INVOKE("Incorrect syntax in specifying testcase to invoke. HINT - @R<TestScenario>:<TestCase>;"),
		ER_CONNECTING_TESTMANAGER("Engine couldn't find 'TEST MANAGER' on specified host."),
		ER_IN_VERIFYING_TESTELEMENT_PROPERTY("Test Element property couldn't verifyed.Expected Attribute -- {EXPECTED} but actual attribute value found -- {ACTUAL}."),
		ER_IN_SPECIFYING_TEST_PROJECT("Engine couldn't find the project specified -- '" + PROJECT_NAME + "'"),
		ER_INFILTERING_TESTSUITES("Something went wrong while filtering Test Scenarios to execute"),
		ER_IN_GETTING_TESTSTEPS_FOR_TESTCASE("Something went wrong while getting test steps for the testcase"),
		ER_IN_REPORTING_TESTCASE_STATUS("Error in reporting test result."),
		ER_CONNECTING_REPOSITORIES("Couldn't connect to {REPOSITORY}."),
		ER_HOVER_TO_ELEMENT("Couldn't hover to test element"),
		STEP_MARKED_OPTIONAL("Test Step has been marked as optional"),
		ER_WHILE_SWITCHING_TO_FRAME("Couldn't switch to specified frame details.Please reverify the frame details - {FRAME_DETAILS}"),
		TESTOBJECT_IS_THERE("Test element is present on current page.Its displayed property is -- '{IS_DISPALYED}'"),
		ER_IN_GETTING_TEST_EXECUTION_BUILD_TEST_MANAGEMENT_TOOL("Error in getting test execution build."),
		ER_IN_SPECIFYING_RECOVERY_ACTION("No such recovery action ({ACTION_NAME}) supported"),
		ER_IN_FETCHING_TESTCASE("Test case:'{TESTCASE}' is not present in test suite:{TESTSUITE}"),
		ER_IN_WRITING_RECOVERY_PROPERTY_FILE("Error in recovery property file.Please review the property names."),
		ER_IN_LOADING_DRIVER_CAPABILITIES("Error in loading driver capabilities."),
		NO_TEST_SCENARIOS_In_TEST_GROUP("No test scenario to execute in test group -- '{TEST_GROUP}' ."),
		ERR_IN_SWIPING_TO_OBJECT("Test Driver couldn't swipe to test object, current time -- {TIME}."),
		ER_EXECUTING_JAVA_SNIPPET("Error in executing java snippet -- {JAVA_SNIPPET}"),
		ER_RETRIEVING_TESTCASE("Error retreiving test details, Project - '{PROJECT}' TestName - '{TESTNAME}' "),
		ER_WHILE_SETTING_CONFIGURATION_PROPERTY("Error while setting configuration input to shared object"),
		ERR_CANNOT_SPECIFY_LEFT_AND_RIGHT_STRING("Cannot split Actual and Expected string values, String value found {STRING_VALUE}"),
		ERR_NULL_INPUT_FOUND("User has entered null value , String Value entered as actual String {ACTUAL_STRING_VALUE} "),
		ERR_STRINGS_ARE_UNEQUAL("Input values are unequal , Actual Input Value Recieved from user is {ACTUAL_STRING_VALUE} "),
		ERR_VERIFYING_PAGE_PROPERTY("Page property '{PAGE_ATTRIBUTE}' is - {ATTRIBUTE_VALUE} ,it doesn't contains - {EXPECTED_ATTRIBUTE_VALUE}"),
		ERR_NO_PRODUCT_FOUND_ON_CATALOG("Products are not found on catalog page"),
		ERR_TAG_NOT_FOUND_AFTER_SORTING("{SORT_SELECTED} Tag Not found for product {SKU}"),
		ERR_PRICE_MISMATCH("Mismatch at sorting {SORT_SELECTED} for Product {SKU} , price found {PRICE_FOUND}"),
		ERR_PRICE_OUT_OF_RANGE("Price is not in provided range {SORT_SELECTED} for Product {SKU} , price found {PRICE_FOUND}"),
		ERR_DATABASE_CONNECTION_ERROR("Couldn't Connect to DB -- "),
		ERR_DATABASE_CONNECTION_TIMEOUT("DB Connection TimeOut !"),
		ERR_IN_READING_URL_SOURCE("Error in reading Url source file"),
		FEATURE_NOT_IMPLEMENTED("Feature not implemented for Test Driver"),
		TEST_FAILURE("Oops there are test failure, please refer the execution reports in your targets."),
		ER_GETTING_TESTOBJECT("Test object is not present in ObjectRepository."),
		ER_EMPTY_RESULTSET("Query result set is empty."),
		ER_IN_SPECIFYING_RESULTSET_INDEX("Result set index is not specified clearly"),
		ER_CONNECT_TO_DB_NOT_OPTED("User not opted for connect to database. Please set the flag 'dbconnectionrequired=true' in uiautomation.properties file."),
		ERR_INCORRECT_COMPARISION_STRATEGY("Cannot understand the comparing strategy , Actual Value {ACTUAL_STRING}"),
		ERR_TESTDATA_MATCH("Cannot find any preferrable match , Actual Value {ACTUAL_STRING}"),
		ERR_CSV_MATCH("Mentioned CSV is not present in the folder"),
		ERR_NOT_AN_INTEGER("Actual/Expected test datas are not integers {ACTUAL_STRING_VALUE} "),
		ERR_GETTING_TESTSUITE_FOR_TESTCASE("Error while getting testsuite details for a test case - {TESTCASENAME}"),
		ERR_SPECIFYING_TESTMANAGER("TestManager - '{TESTMANAGERKEY}' selected is no longer supported, please contact admin."),
		ERR_GENERATING_XML("Error occured while generating execution details logs in XML"),
		ERR_INITIATING_REPORT("Error occured while generating test execution report"),
		ERR_WHILE_PROCESSING_LOCALTESTDATA("Error while reading local test data, typical error details are - "),
		ERR_ACCESSING_SOAP_PROJECT("Error in accessing soap ui project, please review again"),
		ERR_SET_TESTOBJECT_DETAILS("Error in setting test object details");
		private String errorKeyword;
		private ERROR_MESSAGES(String ErrorKeyword){
			this.errorKeyword= ErrorKeyword;
		}
		public String getErrorMessage(){
			return this.errorKeyword;
		}
	}
	public static enum STRATEGY_KEYWORD{
		IGNORE_SPACE("ignorespace"),
		IGNORE_STEP( "skip"),
		IGNORE_CASE("ignorecase"),
		PARTIAL_MATCH("partialmatch"),
		EXACT_MATCH("exactmatch"),
		OPTIONAL("optional"),
		CRITICAL("critical"),
		NOWAIT("nowait");
		
		private String strategy;
		private STRATEGY_KEYWORD(String strategyKeyword){
			this.strategy = strategyKeyword;
		}
		public String getStartegy(){
			return this.strategy;
		}
	}
	
}

