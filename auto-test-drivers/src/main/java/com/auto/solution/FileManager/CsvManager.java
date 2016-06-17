package com.auto.solution.FileManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.auto.solution.Common.Property;
import com.auto.solution.Common.ResourceManager;
import com.auto.solution.Common.Utility;
	
	public class CsvManager {

		private ResourceManager rManager;
		public CsvManager(ResourceManager rmanager){
				this.rManager = rmanager;
			}
		
		/**
		 * This method is to check wether the file exists or not. 
		 * it checks whether the specified file is present or not.
		 * 
		 * @param String - nameofCsv
		 */
		private void checkWetherFileExistOrNot(String nameOfCsv){
			String tempFile = null; 
			tempFile =	rManager.getLocationForExternalFilesInResources().replace("{EXTERNAL_FILE_NAME}", nameOfCsv);
			tempFile =  tempFile.replace("{PROJECT_NAME}", Property.PROJECT_NAME);
			
			String filepath = Utility.getAbsolutePath(tempFile);
			try{
	    		File file = new File(filepath);
	    		if(!file.exists())
	    		{
	    			throw new Exception(Property.ERROR_MESSAGES.ERR_CSV_MATCH.getErrorMessage());
	    		}
	    	}
	    	catch(Exception e){
	    		
	    	}
		}
			
		/**
		 * This method is to replace any value from the mentioned csv. 
		 * it replaces the given value from the desired location.
		 * @param String - nameofCsv
		 * @param String - column index
		 * @param String - row index
		 */
		public void replaceAnyValueInCsv(String ... dataSet) throws Exception{
			 checkWetherFileExistOrNot(dataSet[0]);
			 
			 try{
			 String tempFile = null; 
			 tempFile =	rManager.getLocationForExternalFilesInResources().replace("{EXTERNAL_FILE_NAME}", dataSet[0]);
			 tempFile =  tempFile.replace("{PROJECT_NAME}", Property.PROJECT_NAME);
			 String filepath = Utility.getAbsolutePath(tempFile);
			 
			 File inputFile = new File(filepath);

			// Read existing file 
			CSVReader reader = new CSVReader(new FileReader(inputFile), ',');
			List<String[]> csvBody = reader.readAll();
			
			// get CSV row column  and replace with by using row and column
			csvBody.get(Integer.parseInt(dataSet[2]))[Integer.parseInt(dataSet[3])] = dataSet[1];
			reader.close();

			// Write to CSV file which is open
			CSVWriter writer = new CSVWriter(new FileWriter(inputFile), ',');
			writer.writeAll(csvBody);
			writer.flush();
			writer.close();
			 }
			 catch(Exception e){
				throw new Exception(Property.ERROR_MESSAGES.ER_SPECIFYING_OBJECT.getErrorMessage()); 
			 }
		}
		
		/**
		 * This method is to replace any value from the mentioned csv. 
		 * it deletes the given value from the desired location.
		 * @param String - nameofCsv
		 * @param String - DATA
		 * @param String - column index
		 * @param String - row index
		 */
		public void deleteAnyValueInCsv(String ... dataSet) throws Exception{
			 checkWetherFileExistOrNot(dataSet[0]);
			 
			 try{
				 String tempFile = null; 
				 tempFile =	rManager.getLocationForExternalFilesInResources().replace("{EXTERNAL_FILE_NAME}", dataSet[0]);
				 tempFile =  tempFile.replace("{PROJECT_NAME}", Property.PROJECT_NAME);
				 String filepath = Utility.getAbsolutePath(tempFile);
				 
				 File inputFile = new File(filepath);

				// Read existing file 
				CSVReader reader = new CSVReader(new FileReader(inputFile), ',');
				List<String[]> csvBody = reader.readAll();
				// get CSV row column  and delete with by using row and column
				csvBody.get(Integer.parseInt(dataSet[1]))[Integer.parseInt(dataSet[2])] = "";
				reader.close();

				// Write to CSV file which is open
				CSVWriter writer = new CSVWriter(new FileWriter(inputFile), ',');
				writer.writeAll(csvBody);
				writer.flush();
				writer.close();
				 }
				 catch(Exception e){
					throw new Exception(Property.ERROR_MESSAGES.ER_SPECIFYING_OBJECT.getErrorMessage()); 
				 }
		}
		 
		/**
		 * This method is to replace any value from the mentioned csv. 
		 * it verifies the given value from the desired location in the csv.
		 * @param String - nameofCsv
		 * @param String - column index
		 * @param String - row index
		 */
		public boolean verifyAnyValueInCsv(String ... dataSet) throws Exception{
			 checkWetherFileExistOrNot(dataSet[0]);
			 boolean flag = false;
			 try{
				 String tempFile = null; 
				 tempFile =	rManager.getLocationForExternalFilesInResources().replace("{EXTERNAL_FILE_NAME}", dataSet[0]);
				 tempFile =  tempFile.replace("{PROJECT_NAME}", Property.PROJECT_NAME);
				 String filepath = Utility.getAbsolutePath(tempFile);
				 
				 File inputFile = new File(filepath);

				// Read existing file
				 
				CSVReader reader = new CSVReader(new FileReader(inputFile), ',');
				List<String[]> csvBody = reader.readAll();
				// get CSV row column  and verify with by using row and column
				String data = csvBody.get(Integer.parseInt(dataSet[2]))[Integer.parseInt(dataSet[3])];
				
				if(data.toString().equals(dataSet[1])){
					flag = true;
				}
				else{
					flag = false;
				}
				reader.close();

				// Write to CSV file which is open
				CSVWriter writer = new CSVWriter(new FileWriter(inputFile), ',');
				writer.writeAll(csvBody);
				writer.flush();
				writer.close();
				return flag;
				 }
				 catch(Exception e){
					throw new Exception(Property.ERROR_MESSAGES.ER_SPECIFYING_OBJECT.getErrorMessage()); 
				 }
		}
	
		/**
		 * This method is to replace any value from the mentioned csv. 
		 * it appends the given value from the last point in the csv.
		 * @param String - nameofCsv
		 * @param String - DATA to be appended
		 */
		public void appendAnyValueInCsv(String ... dataSet) throws Exception{
			 checkWetherFileExistOrNot(dataSet[0]);
			 try{
			 String tempFile = null; 
			 tempFile =	rManager.getLocationForExternalFilesInResources().replace("{EXTERNAL_FILE_NAME}", dataSet[0]);
			 tempFile =  tempFile.replace("{PROJECT_NAME}", Property.PROJECT_NAME);
			 String filepath = Utility.getAbsolutePath(tempFile);
			 
			 File inputFile = new File(filepath);

			// Read existing file 
			 CSVWriter writer = new CSVWriter(new FileWriter(inputFile,true),',');
		      //Write the record to file
		      writer.writeNext(dataSet[1].split(","));
		        
		      //close the writer
		      writer.close();
		 }
		 catch(Exception e){
			throw new Exception(Property.ERROR_MESSAGES.ER_SPECIFYING_OBJECT.getErrorMessage()); 
		 }
			 
		}
		 
		/**
		 * This method is to replace any value from the mentioned csv. 
		 * it deletes the given csv from the FilesToUploadFolder
		 * @param String - nameofCsv
		 */
		public void deleteWholeCsvFromTheFolder(String ... dataSet) throws Exception{
			 

			 String tempFile = null; 
			 tempFile =	rManager.getLocationForExternalFilesInResources().replace("{EXTERNAL_FILE_NAME}", dataSet[0]);
			 tempFile =  tempFile.replace("{PROJECT_NAME}", Property.PROJECT_NAME);
				
			 String filepath = Utility.getAbsolutePath(tempFile);
			 Utility.deleteFile(filepath);
			 
		 }
		
}

