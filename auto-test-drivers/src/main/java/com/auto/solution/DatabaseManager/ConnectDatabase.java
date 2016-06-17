package com.auto.solution.DatabaseManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.auto.solution.Common.Property;
import com.auto.solution.Common.Utility;



public class ConnectDatabase {
	
	public static Connection connectionVerificationAndEstablishment(String dbName) throws Exception{
		Connection conn = (Connection) Utility.getObjectFromGlobalObjectCollection(dbName);
		try {
			if(Utility.getValueForKeyFromGlobalVarMap("dbconnectionrequired")==null || 
					Utility.getValueForKeyFromGlobalVarMap("dbconnectionrequired").equalsIgnoreCase("false")){
				throw new IOException(Property.ERROR_MESSAGES.ER_CONNECT_TO_DB_NOT_OPTED.getErrorMessage());
			}
			if(conn.isClosed() || conn==null){
				connectToDatabase(dbName);
			}
		}
		catch(IOException ie){
			throw ie;
		}
		catch (Exception e) {
			throw new Exception(Property.ERROR_MESSAGES.ERR_DATABASE_CONNECTION_TIMEOUT.getErrorMessage());
		}
		return conn;	
	} 
	
	public static void connectToAllDatabase() throws Exception{
		try{
			Set<String> globalVarKeySet = Property.globalVarMap.keySet();
			for(String key:globalVarKeySet){
				if(key.contains("application.db")){
					String connectionString = Property.globalVarMap.get(key);
					makeDBConnection(key, connectionString);
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			throw new Exception(Property.ERROR_MESSAGES.ERR_DATABASE_CONNECTION_ERROR.getErrorMessage());
		}
		
	}
	
	public static void connectToDatabase(String dbName) throws Exception{
		String connectionString = Property.globalVarMap.get("application.db."+dbName);
		makeDBConnection("application.db."+dbName, connectionString);
	}
	
	private static void makeDBConnection(String key, String connectionString) throws Exception{
		Connection conn = null;
		
		try{
			String[] variablesInConectionString = fetchDriverAndconnectionDetailFromConnectionString(connectionString);
			String connectionDriver = variablesInConectionString[0];
			String connectionStringUrlNameAndPassword = variablesInConectionString[1];
			Class.forName(connectionDriver);
			conn = DriverManager.getConnection(connectionStringUrlNameAndPassword);
			
			try {
				Utility.addObjectToGlobalObjectCollection(key.replace("application.db.", ""), conn);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		
		}
		catch(Exception ex){
			ex.printStackTrace();
			throw new Exception(Property.ERROR_MESSAGES.ERR_DATABASE_CONNECTION_ERROR.getErrorMessage());
		}
	}
	
	public static String[] fetchDriverAndconnectionDetailFromConnectionString(String driverUrl){
		String[] identifierValues = driverUrl.split("###");
		return identifierValues;
	}
	
	
	/**
	 * This method executes the query provided by user on given database. Data
	 * format in test management tool should be like: @D
	 * "ResultObject:=query#dbname" (EmployeeResultSetVar:=select name from
	 * employee#boblive)
	 * 
	 * @param query - query to execute
	 * @param dbName - database on which query to execute
	 * @throws Exception
	 */
	public static void executeQuery(String query,String dbName) throws Exception{
		
		Connection conn;
		ResultSet rs;
		Statement stmt;
		int totalColumns;
		List<HashMap<String, Object>> queryResult = new ArrayList<HashMap<String, Object>>();

		if(dbName==null)
		{
			Set<String> globalVarKeySet = Property.globalVarMap.keySet();
			for(String key:globalVarKeySet){
				if(key.contains("application.db")){
					dbName = key.replace("application.db.", "");
					break;
				}
			}
		}
		connectionVerificationAndEstablishment(dbName);
		conn = (Connection) Utility.getObjectFromGlobalObjectCollection(dbName);
	
		try 
		{
			stmt = conn.createStatement();
			if(query.toLowerCase().contains("select"))
			{
				String queryKeyValue[] = query.split(":=");
				stmt.execute(queryKeyValue[1]);
				rs = stmt.getResultSet();
				ResultSetMetaData rsmd = rs.getMetaData();
				totalColumns = rsmd.getColumnCount();
				
				while (rs.next()) {
					HashMap<String, Object> rowData = new HashMap<String, Object>();
					for (int column = 0; column < totalColumns; column++) {
						rowData.put(rsmd.getColumnName(column + 1),	rs.getObject(column + 1));
					}
					queryResult.add(rowData);
				}
				 
				 Utility.addObjectToGlobalObjectCollection(queryKeyValue[0], queryResult);
			}
			else{
				stmt.execute(query);
			}	
		}
		
		catch (Exception e) {
			throw new Exception(Property.ERROR_MESSAGES.ER_MISSING_TESTDATA.getErrorMessage());	
		}
		
	}
	

}
