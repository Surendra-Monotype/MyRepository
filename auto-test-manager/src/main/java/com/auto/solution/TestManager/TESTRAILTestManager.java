package com.auto.solution.TestManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.excel.XlsDataSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.auto.solution.Common.Property;
import com.auto.solution.Common.Property.ERROR_MESSAGES;
import com.auto.solution.Common.ResourceManager;
import com.auto.solution.Common.Utility;
import com.rmn.testrail.entity.Project;
import com.rmn.testrail.entity.TestInstance;
import com.rmn.testrail.entity.TestPlan;
import com.rmn.testrail.entity.TestRun;
import com.rmn.testrail.entity.TestSuite;
import com.rmn.testrail.service.TestRailService;

import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;

@SuppressWarnings(value = "all")
public class TESTRAILTestManager extends TestManagerUtils implements ITestManager {

	private String objectRepository;

	private String testSuiteName = "";

	private ResourceManager rm;

	private static TestRailService testRailInstance = null;

	private static Project testProject = null;

	private static Integer testProjectID = null;

	private static String objectRepositorySheetName;

	private ITable objectRepoTable = null;

	private List<String> listOfTestGroupSelectedByUser = new ArrayList<String>();

	private HashMap<String, Set<String>> mapOfTestSuiteAndTheirTestCases = new HashMap<String, Set<String>>();

	private HashMap<String, List<String>> mapOfTestGroupsAndTheirTestSuites = new HashMap<String, List<String>>();

	private HashMap<String, List<String>> mapOfTestGroupsAndTheirTestCases = new HashMap<String, List<String>>();

	private static APIClient testRailAPIsClient = null;

	private HashMap<String, List<String>> testDataDetailsMap = new HashMap<String, List<String>>();

	public TESTRAILTestManager(boolean WantToConnectToTestManager, ResourceManager rm) throws Exception {
		this.rm = rm;
		if (WantToConnectToTestManager) {
			try {
				testRailInstance = new TestRailService(fetchTestRailNameFromUrl(Property.TEST_MANAGEMENT_URL),
						Property.TEST_MANAGEMENT_USERNAME, Property.TEST_MANAGEMENT_KEY);
				testRailAPIsClient = new APIClient(Property.TEST_MANAGEMENT_URL);
				testRailAPIsClient.setUser(Property.TEST_MANAGEMENT_USERNAME);
				testRailAPIsClient.setPassword(Property.TEST_MANAGEMENT_KEY);
				try {
					// List<Project> projects = testRailInstance.getProjects();
					testProject = testRailInstance.getProjectByName(Property.PROJECT_NAME);
					testProjectID = testProject.getId();
				} catch (Exception e) {
					// throw new
					// Exception(Property.ERROR_MESSAGES.ER_IN_SPECIFYING_TEST_PROJECT.getErrorMessage());
					System.out.println(e.getMessage());
					System.out.println();
				}
				objectRepositorySheetName = Property.ObjectRepositorySheetName;
				accessLocalTestData(rm.getLocationForExternalFilesInResources().replace("{EXTERNAL_FILE_NAME}", "")
						.replace("{PROJECT_NAME}", Property.PROJECT_NAME));
			} catch (Exception me) {
				throw new Exception(
						Property.ERROR_MESSAGES.ER_CONNECTING_TESTMANAGER.getErrorMessage() + "--" + me.getMessage());
			}
		}
	}

	private String fetchTestRailNameFromUrl(String url) throws Exception {

		URI uri;
		try {
			uri = new URI(url);
			return uri.getHost().replace(".testrail.net", "");
		} catch (URISyntaxException e) {
			throw e;
		}

	}

	private void accessLocalTestData(String targetLocation) throws Exception {
		try {
			File[] propertyFiles = connectToStaticTestDataProperties(targetLocation);
			if (propertyFiles != null)
				setAllKeysInLocalTestDataToGlobalVarMap(propertyFiles);
		} catch (Exception e) {
			String errMessage = Property.ERROR_MESSAGES.ERR_WHILE_PROCESSING_LOCALTESTDATA.getErrorMessage();
			throw new Exception(errMessage + e.getMessage());
		}
	}

	private ITable connectRepository(String repository, String worksheet) throws Exception {
		ITable objSheet = null;
		try {
			File inFile = new File(repository);
			XlsDataSet ds = new XlsDataSet(inFile);
			objSheet = ds.getTable(worksheet);

		} catch (DataSetException de) {
			throw new DataSetException(
					ERROR_MESSAGES.ER_CONNECTING_REPOSITORIES.getErrorMessage().replace("{REPOSITORY}", repository));
		} catch (Exception e) {
			throw e;
		}
		return objSheet;
	}

	private HashMap<String, List<String>> getTestDataDetailsFromTestLinkToMapCollection(String testSuiteName)
			throws Exception {

		HashMap<String, List<String>> testDataCollection = new HashMap<String, List<String>>();

		// ArrayList<Integer> testSuiteIds = new ArrayList<Integer>();
		// int i = 0;

		try {

			TestSuite testSuite = testProject.getTestSuiteByName(testSuiteName);

			String descriptionOfTestSuite = testSuite.getDescription();

			String[] testDataContents = descriptionOfTestSuite.split("\r\n");

			for (String keyValuePairs : testDataContents) {

				if (keyValuePairs.contains("=")) {

					String[] key_values = keyValuePairs.split("=");

					String key = key_values[0].trim();

					String valuesAsString = key_values[1].trim();

					String[] values = valuesAsString.split(Pattern.quote("||"));

					List<String> listOfValues = new ArrayList<String>();

					for (String value : values) {

						value = value.trim();

						listOfValues.add(value);

					}

					testDataCollection.put(key, listOfValues);
				}

			}

			alsoStoreTestDataInGlobalVariables(testDataCollection);

		} catch (NullPointerException ne) {
			// No test data defined for test suite.
		} catch (TestLinkAPIException te) {
			String errMessage = ERROR_MESSAGES.ER_RETRIEVING_TESTCASE.getErrorMessage();
			errMessage = errMessage.replace("{PROJECT}", testProject.getName());
			errMessage = errMessage.replace("{TESTNAME}", testSuiteName);
			throw new Exception(errMessage);
		}
		return testDataCollection;
	}

	private void alsoStoreTestDataInGlobalVariables(HashMap<String, List<String>> testDataCollection) {
		try {

			for (String key : testDataCollection.keySet()) {
				List<String> listOfValues = testDataCollection.get(key);
				if (listOfValues.size() > 1) {
					int i = 0;
					for (String value : listOfValues) {
						Utility.setKeyValueToGlobalVarMap(key + "[" + i + "]", value.trim());
						i++;
					}
				} else {
					for (String value : listOfValues) {
						Utility.setKeyValueToGlobalVarMap(key, value.trim());
					}
				}
			}
		} catch (Exception e) {
			// Nothing to do here, as test step will fail if test data not set
			// successfully.
		}
	}

	@Override
	public void locateRepositories(String testSuiteName) {
		this.testSuiteName = testSuiteName;
		objectRepository = rm.getObjectRepositoryFileLocation().replace("{PROJECT_NAME}", Property.PROJECT_NAME);
	}

	@Override
	public void connectRepositories() throws Exception {
		testDataDetailsMap.clear();

		objectRepoTable = null;

		testDataDetailsMap = getTestDataDetailsFromTestLinkToMapCollection(this.testSuiteName);

		objectRepoTable = connectRepository(objectRepository, objectRepositorySheetName);
	}

	@Override
	public String fetchObjectRepositoryContent(String Keyword) {
		String datavalue = "";
		try {
			String[] contents = Keyword.split(":");

			int row = Integer.parseInt(contents[0]);

			datavalue = objectRepoTable.getValue(row, contents[1]).toString();
		} catch (Exception e) {
			return "";
		}
		return datavalue;
	}

	@Override
	public String fetchTestDataRepositoryContent(String Keyword) {
		String datavalue = "";
		try {
			String[] contents = Keyword.split(":");

			int row = Integer.parseInt(contents[0]);

			String columnName = contents[1].toString();

			datavalue = testDataDetailsMap.get(columnName).get(row - 1);

		} catch (Exception e) {
			return "";
		}
		return datavalue;
	}

	@Override
	public String fetchTestCaseRepositoryContent(String Keyword) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, String> getActualObjectDefinition(String logicalNameOfTheObject) throws Exception {
		HashMap<String, String> objDef = new HashMap<String, String>();
		try {
			if (objectRepoTable == null) {
				throw new Exception("Locate Object repository to access its data!");
			}

			int rowCount = objectRepoTable.getRowCount();

			Integer iterativeRow = 0;

			if (logicalNameOfTheObject != "") {

				while (iterativeRow < rowCount) {
					if (fetchObjectRepositoryContent(
							iterativeRow.toString() + ":" + Property.TESTOBJECT_KEYWORD_IN_ObjectRepository)
									.equals(logicalNameOfTheObject)) {

						String locatingStrategy = fetchObjectRepositoryContent(
								iterativeRow.toString() + ":" + Property.Locating_Strategy_Keyword);

						String locationOfObject = fetchObjectRepositoryContent(
								iterativeRow.toString() + ":" + Property.Locating_Value_Keyword_In_OR);

						String inFrame = fetchObjectRepositoryContent(
								iterativeRow.toString() + ":" + Property.TestObject_InFrame_Keyword);

						String testObjectFilter = fetchObjectRepositoryContent(
								iterativeRow.toString() + ":" + Property.TestObject_Filter_Keyword);

						objDef.put(Property.TESTOBJECT_KEYWORD_IN_ObjectRepository, logicalNameOfTheObject);

						objDef.put(Property.Locating_Strategy_Keyword, locatingStrategy);

						objDef.put(Property.Locating_Value_Keyword_In_OR, locationOfObject);

						objDef.put(Property.TestObject_InFrame_Keyword, inFrame);

						objDef.put(Property.TestObject_Filter_Keyword, testObjectFilter);

						break;
					}
					iterativeRow++;
				}
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return objDef;
	}

	@Override
	public List<String> getTestStepsForTestCase(String testCaseID) throws Exception {
		ArrayList<String> testCaseSteps = new ArrayList<String>();

		try {

			JSONArray testCasesArray = (JSONArray) JSONValue
					.parse(testRailAPIsClient.sendGet("get_cases/" + testProjectID).toString());

			String testCase_id = "-1";

			for (Object testCase : testCasesArray) {
				JSONObject testCaseObj = (JSONObject) testCase;
				if (testCaseObj.get("title").equals(testCaseID)) {
					testCase_id = testCaseObj.get("id").toString();
					break;
				}
			}

			if (testCase_id.equals("-1")) {
				String err_Message = Property.ERROR_MESSAGES.ER_IN_FETCHING_TESTCASE.getErrorMessage()
						.replace("{TESTCASE}", testCaseID);
				err_Message = err_Message.replace("{TESTSUITE}", this.testSuiteName);
				throw new Exception(err_Message);
			}

			JSONObject testCaseDetails = (JSONObject) testRailAPIsClient.sendGet("get_case/" + testCase_id);

			JSONArray stepInstance = (JSONArray) testCaseDetails.get("custom_steps_separated");

			for (Object object : stepInstance) {
				JSONObject testObj = (JSONObject) object;
				testCaseSteps.add(testObj.get("content").toString());
			}
		} catch (Exception e) {
			throw new Exception(
					ERROR_MESSAGES.ER_IN_GETTING_TESTSTEPS_FOR_TESTCASE.getErrorMessage() + " --" + e.getMessage());
		}
		return testCaseSteps;
	}

	@Override
	public List<String> getTestGroupsForExecution() throws Exception {
		return this.listOfTestGroupSelectedByUser;
	}

	@Override
	public HashMap<String, Set<String>> getTestSuiteAndTestCaseHierarchyForExecution() throws Exception {

		HashMap<String, Set<String>> testSuiteAndTestCaseDetails = new HashMap<String, Set<String>>();

		HashMap<String, List<String>> testGroupAndTestSuiteDetails = new HashMap<String, List<String>>();

		HashMap<String, List<String>> testGroupAndTestCaseDetails = new HashMap<String, List<String>>();

		try {
			String[] groupMentionedByUser = Utility.getValueForKeyFromGlobalVarMap("execution.group").split(",");

			// TestPlan[] existingTestGroupsInProject =
			// testLinkInstance.getProjectTestPlans(testProjectID);

			List<TestPlan> existingTestGroupsInProject = testProject.getTestPlans();

			List<String> groupMentionedByUserAsList = Arrays.asList(groupMentionedByUser);

			if (groupMentionedByUser.length < 1
					|| (groupMentionedByUser.length == 1 && groupMentionedByUser[0] == "")) {

				groupMentionedByUserAsList.clear();

				for (TestPlan testGroup : existingTestGroupsInProject) {

					String testGroupName = testGroup.getName().toLowerCase();

					groupMentionedByUserAsList.add(testGroupName);

				}
			}

			this.listOfTestGroupSelectedByUser.addAll(groupMentionedByUserAsList);

			for (String testGroup : groupMentionedByUserAsList) {

				ArrayList<String> testCasesInGroup = new ArrayList<String>();
				TestPlan testPlan = testProject.getTestPlanByName(testGroup);

				List<TestRun> testSuitsInTestGroup = testPlan.getTestRuns();

				ArrayList<String> testSuitesInTestGroup = new ArrayList<String>();

				for (TestRun testSuite : testSuitsInTestGroup) {
					String testSuiteName = testSuite.getName();
					testSuitesInTestGroup.add(testSuiteName);

					List<TestInstance> testCaseInstancesInTestSuite = testSuite.getTests();
					ArrayList<String> testCasesInTestSuite = new ArrayList<String>();
					for (TestInstance testCase : testCaseInstancesInTestSuite) {
						String testCaseName = testCase.getTitle();
						testCasesInTestSuite.add(testCaseName);
						testCasesInGroup.add(testCaseName);
					}
					Set<String> testCasesAsSet = new HashSet<String>(testCasesInTestSuite);
					testSuiteAndTestCaseDetails.put(testSuiteName, testCasesAsSet);
				}
				testGroupAndTestSuiteDetails.put(testGroup, testSuitesInTestGroup);
				testGroupAndTestCaseDetails.put(testGroup, testCasesInGroup);
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}

		this.mapOfTestGroupsAndTheirTestSuites = testGroupAndTestSuiteDetails;

		this.mapOfTestGroupsAndTheirTestCases = testGroupAndTestCaseDetails;

		this.mapOfTestSuiteAndTheirTestCases = testSuiteAndTestCaseDetails;

		return testSuiteAndTestCaseDetails;
	}

	@Override
	public HashMap<String, HashMap<String, Set<String>>> prepareAndGetCompleteTestHierarchy() throws Exception {
		try {
			HashMap<String, List<String>> testGroupAndTestCases = mapOfTestGroupsAndTheirTestCases;

			HashMap<String, List<String>> testGroupAndTestSuites = mapOfTestGroupsAndTheirTestSuites;

			HashMap<String, HashMap<String, Set<String>>> testGroupTestSuiteAndTestCasesDetails = new HashMap<String, HashMap<String, Set<String>>>();

			for (String testGroup : testGroupAndTestSuites.keySet()) {

				HashMap<String, Set<String>> testSuiteAndTestCases = new HashMap<String, Set<String>>();

				testSuiteAndTestCases = this.mapOfTestSuiteAndTheirTestCases;

				List<String> testSuitesInTestGroup = new ArrayList<String>();

				testSuitesInTestGroup = testGroupAndTestSuites.get(testGroup);

				HashMap<String, Set<String>> testSuiteAndTestCasesForThisGroup = new HashMap<String, Set<String>>();

				for (String testSuite : testSuitesInTestGroup) {

					HashMap<String, Set<String>> testSuiteDetails = new HashMap<String, Set<String>>(
							testSuiteAndTestCases);

					Set<String> testCasesForCurrentTestSuite = new HashSet<String>();

					Set<String> testCasesForCurrentGroup = new HashSet<String>(testGroupAndTestCases.get(testGroup));

					for (String testCase : testSuiteDetails.get(testSuite)) {

						if (testCasesForCurrentGroup.contains(testCase)) {
							testCasesForCurrentTestSuite.add(testCase);
						}
					}

					testSuiteAndTestCasesForThisGroup.put(testSuite, testCasesForCurrentTestSuite);
				}
				testGroupTestSuiteAndTestCasesDetails.put(testGroup, testSuiteAndTestCasesForThisGroup);
			}
			return testGroupTestSuiteAndTestCasesDetails;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void setResultsInTestManager(HashMap<String, String> testCaseAndExecutionStatus) throws Exception {
		System.out.println("Sending results to Test Rail . . . ");
		JSONArray testCasesArray = (JSONArray) JSONValue
				.parse(testRailAPIsClient.sendGet("get_cases/" + testProjectID).toString());
		Iterator it = testCaseAndExecutionStatus.entrySet().iterator();
		int case_id = 0;
		JSONArray ja = new JSONArray();

		while (it.hasNext()) {
			JSONObject jo = new JSONObject();
			HashMap.Entry pair = (HashMap.Entry) it.next();
			String testCaseTitle = pair.getKey().toString();
			String testCaseStatus = pair.getValue().toString();
			int status_id = 0;
			if (testCaseStatus.equalsIgnoreCase(Property.PASS)) {
				status_id = 1;
			} else {
				status_id = 5;
			}

			for (Object testCase : testCasesArray) {
				JSONObject testCaseObj = (JSONObject) testCase;
				if (testCaseObj.get("title").equals(testCaseTitle)) {
					case_id = Integer.parseInt(testCaseObj.get("id").toString());
					break;
				}
			}
			jo.put("case_id", case_id);
			jo.put("status_id", status_id);
			ja.add(jo);
		}
		JSONObject postRequestData = new JSONObject();
		postRequestData.put("results", ja);

		// Insert results in TestRail
		testRailAPIsClient.sendPost("add_results_for_cases/" + getTestRunID(Property.TEST_EXECUTION_GROUP),
				postRequestData);
		System.out.println("Success --- Results sent to Test Rail !!! ");
	}

	public String getTestRunID(String testPlanName) throws Exception {
		String testPlanID = "";
		JSONArray testPlansArray = (JSONArray) JSONValue
				.parse(testRailAPIsClient.sendGet("get_plans/" + testProjectID).toString());
		for (Object testPlan : testPlansArray) {
			JSONObject testPlanObj = (JSONObject) testPlan;
			if (testPlanObj.get("name").toString().equalsIgnoreCase(testPlanName)) {
				testPlanID = testPlanObj.get("id").toString();
				break;
			}
		}

		JSONObject testPlanDetailsObj = (JSONObject) JSONValue
				.parse(testRailAPIsClient.sendGet("get_plan/" + testPlanID).toString());
		JSONArray entriesArray = (JSONArray) testPlanDetailsObj.get("entries");
		for (Object entry : entriesArray) {
			JSONObject entryObj = (JSONObject) entry;
			JSONArray runsArray = (JSONArray) entryObj.get("runs");
			for (Object run : runsArray) {
				JSONObject runObj = (JSONObject) run;
				return runObj.get("id").toString();
			}
		}
		return "";
	}

}

/**
 * TestRail API binding for Java (API v2, available since TestRail 3.0)
 *
 * Learn more:
 *
 * http://docs.gurock.com/testrail-api2/start
 * http://docs.gurock.com/testrail-api2/accessing
 *
 * Copyright Gurock Software GmbH. See license.md for details.
 */

class APIClient {
	private String m_user;
	private String m_password;
	private String m_url;

	public APIClient(String base_url) {
		if (!base_url.endsWith("/")) {
			base_url += "/";
		}

		this.m_url = base_url + "index.php?/api/v2/";
	}

	/**
	 * Get/Set User
	 *
	 * Returns/sets the user used for authenticating the API requests.
	 */
	public String getUser() {
		return this.m_user;
	}

	public void setUser(String user) {
		this.m_user = user;
	}

	/**
	 * Get/Set Password
	 *
	 * Returns/sets the password used for authenticating the API requests.
	 */
	public String getPassword() {
		return this.m_password;
	}

	public void setPassword(String password) {
		this.m_password = password;
	}

	/**
	 * Send Get
	 *
	 * Issues a GET request (read) against the API and returns the result (as
	 * Object, see below).
	 *
	 * Arguments:
	 *
	 * uri The API method to call including parameters (e.g. get_case/1)
	 *
	 * Returns the parsed JSON response as standard object which can either be
	 * an instance of JSONObject or JSONArray (depending on the API method). In
	 * most cases, this returns a JSONObject instance which is basically the
	 * same as java.util.Map.
	 */
	public Object sendGet(String uri) throws MalformedURLException, IOException, APIException {
		return this.sendRequest("GET", uri, null);
	}

	/**
	 * Send POST
	 *
	 * Issues a POST request (write) against the API and returns the result (as
	 * Object, see below).
	 *
	 * Arguments:
	 *
	 * uri The API method to call including parameters (e.g. add_case/1) data
	 * The data to submit as part of the request (e.g., a map)
	 *
	 * Returns the parsed JSON response as standard object which can either be
	 * an instance of JSONObject or JSONArray (depending on the API method). In
	 * most cases, this returns a JSONObject instance which is basically the
	 * same as java.util.Map.
	 */
	public Object sendPost(String uri, Object data) throws MalformedURLException, IOException, APIException {
		return this.sendRequest("POST", uri, data);
	}

	private Object sendRequest(String method, String uri, Object data)
			throws MalformedURLException, IOException, APIException {
		URL url = new URL(this.m_url + uri);

		// Create the connection object and set the required HTTP method
		// (GET/POST) and headers (content type and basic auth).
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.addRequestProperty("Content-Type", "application/json");

		String auth = getAuthorization(this.m_user, this.m_password);
		conn.addRequestProperty("Authorization", "Basic " + auth);

		if (method == "POST") {
			// Add the POST arguments, if any. We just serialize the passed
			// data object (i.e. a dictionary) and then add it to the
			// request body.
			if (data != null) {
				byte[] block = JSONValue.toJSONString(data).getBytes("UTF-8");

				conn.setDoOutput(true);
				OutputStream ostream = conn.getOutputStream();
				ostream.write(block);
				ostream.flush();
			}
		}

		// Execute the actual web request (if it wasn't already initiated
		// by getOutputStream above) and record any occurred errors (we use
		// the error stream in this case).
		int status = conn.getResponseCode();

		InputStream istream;
		if (status != 200) {
			istream = conn.getErrorStream();
			if (istream == null) {
				throw new APIException(
						"TestRail API return HTTP " + status + " (No additional error message received)");
			}
		} else {
			istream = conn.getInputStream();
		}

		// Read the response body, if any, and deserialize it from JSON.
		String text = "";
		if (istream != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(istream, "UTF-8"));

			String line;
			while ((line = reader.readLine()) != null) {
				text += line;
				text += System.getProperty("line.separator");
			}

			reader.close();
		}

		Object result;
		if (text != "") {
			result = JSONValue.parse(text);
		} else {
			result = new JSONObject();
		}

		// Check for any occurred errors and add additional details to
		// the exception message, if any (e.g. the error message returned
		// by TestRail).
		if (status != 200) {
			String error = "No additional error message received";
			if (result != null && result instanceof JSONObject) {
				JSONObject obj = (JSONObject) result;
				if (obj.containsKey("error")) {
					error = '"' + (String) obj.get("error") + '"';
				}
			}

			throw new APIException("TestRail API returned HTTP " + status + "(" + error + ")");
		}

		return result;
	}

	private static String getAuthorization(String user, String password) {
		try {
			return getBase64((user + ":" + password).getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// Not thrown
		}

		return "";
	}

	private static String getBase64(byte[] buffer) {
		final char[] map = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
				'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
				'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
				'8', '9', '+', '/' };

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buffer.length; i++) {
			byte b0 = buffer[i++], b1 = 0, b2 = 0;

			int bytes = 3;
			if (i < buffer.length) {
				b1 = buffer[i++];
				if (i < buffer.length) {
					b2 = buffer[i];
				} else {
					bytes = 2;
				}
			} else {
				bytes = 1;
			}

			int total = (b0 << 16) | (b1 << 8) | b2;

			switch (bytes) {
			case 3:
				sb.append(map[(total >> 18) & 0x3f]);
				sb.append(map[(total >> 12) & 0x3f]);
				sb.append(map[(total >> 6) & 0x3f]);
				sb.append(map[total & 0x3f]);
				break;

			case 2:
				sb.append(map[(total >> 18) & 0x3f]);
				sb.append(map[(total >> 12) & 0x3f]);
				sb.append(map[(total >> 6) & 0x3f]);
				sb.append('=');
				break;

			case 1:
				sb.append(map[(total >> 18) & 0x3f]);
				sb.append(map[(total >> 12) & 0x3f]);
				sb.append('=');
				sb.append('=');
				break;
			}
		}

		return sb.toString();
	}
}

class APIException extends Exception {
	public APIException(String message) {
		super(message);
	}
}
