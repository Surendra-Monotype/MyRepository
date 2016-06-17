package com.auto.solution.DatabaseManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.auto.solution.Common.Property;
import com.auto.solution.Common.Utility;

public class AccessResultsetData {
	
	/**
	 * This method is used to fetch the result of query executed in previous
	 * steps. Data format in test management tool should be like: 
	 * @D GetResultVar:=QueryResultObject(rowNumber,ColumnName)
	 * "{EmployeeNameVar:=EmployeeResultSetVar(0,EmpName)}"
	 * 
	 * @param collectionName - Variable name to store result
	 * @param collectionIndex - Row number and Column name to fetch result data
	 * @param globalMapKey - Global map key in which executed query result is stored
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void fetchResultSetData(String collectionName, String collectionIndex, String globalMapKey) throws Exception{
		
		Object ResultSetData = Utility.getObjectFromGlobalObjectCollection(collectionName);
		try{
			if(ResultSetData!=null)
			{
				List<HashMap<String, Object>> queryResult = new ArrayList<HashMap<String, Object>>();
				queryResult = (List<HashMap<String, Object>>) ResultSetData;
				
				int row = Integer.parseInt(collectionIndex.split(",")[0]);
				String columnName = collectionIndex.split(",")[1].toString();
				
				String data = String.valueOf(queryResult.get(row).get(columnName));
				Utility.setKeyValueToGlobalVarMap(globalMapKey, data);
			}
			else{
				throw new Exception(Property.ERROR_MESSAGES.ER_EMPTY_RESULTSET.getErrorMessage());
			}
		}
		catch(IndexOutOfBoundsException ie){
			throw new Exception(Property.ERROR_MESSAGES.ER_IN_SPECIFYING_RESULTSET_INDEX.getErrorMessage());
		}
		catch(NumberFormatException ne){
			throw new Exception(Property.ERROR_MESSAGES.ER_IN_SPECIFYING_RESULTSET_INDEX.getErrorMessage());
		}
		catch(Exception e)
		{
			throw e;
		}
		
	}

}
