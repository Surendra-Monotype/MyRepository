package com.auto.solution.TestDrivers;

import java.util.HashMap;

import com.auto.solution.Common.Property;
import com.auto.solution.Common.Utility;

public class TestObjectDetails {

private HashMap<String, String> testObjectInfo = new HashMap<String, String>();

public TestObjectDetails(HashMap<String, String> currentTestObjectDetails){
	this.testObjectInfo = currentTestObjectDetails;
}

public String getLocationOfTestObject(){
	return Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(testObjectInfo.get(Property.Locating_Value_Keyword_In_OR));
}

public String getFiltersAppliedOnTestObject(){
	return Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(testObjectInfo.get(Property.TestObject_Filter_Keyword));
}

public String getLocatingStrategyOfTestObject(){
	return Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(testObjectInfo.get(Property.Locating_Strategy_Keyword));
}

public String getFramedetailsOfTestObject(){
	return Utility.replaceAllOccurancesOfStringInVariableFormatIntoItsRunTimeValue(testObjectInfo.get(Property.TestObject_InFrame_Keyword));
}
 
}
