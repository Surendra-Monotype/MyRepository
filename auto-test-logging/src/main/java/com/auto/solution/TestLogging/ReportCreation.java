package com.auto.solution.TestLogging;

import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.auto.solution.Common.Property;


public class ReportCreation {
	
	private static Logger logger = Logger.getLogger(ReportCreation.class);
	private String TestCaseColor = ""; 
	public static ResultSet InputHtmlResultSet= null;
	private String Html_Header_String = "<html>" + 
                                   " <head>" + 
                                    "<meta charset=UTF-8>" + 
                                           "<script>" + 
                                                 "function doMenu(item,obj) {"+
                                                                            "tbl=item;" +
                                                                            "if (tbl.style.display=='none')"+ 
                                                                              "{" +
                                                                                 "tbl.style.display='block';"+ 
                                                                                 "obj.innerHTML= '[-]';"+
                                                                               "}"+
                                                                             "else {"+
                                                                                  "tbl.style.display='none';"+
                                                                                  "obj.innerHTML= '[+]';"+
                                                                                  "}"+
                                                                             "}"+

                                            "</script>"+
                                        "</head>"+
                                            "<body>"+
                                                 "<div>"+                                                    
                                                    "<p>"+
                                                    
                                                    "<font style='font-weight: bold; font-size: 14px; font-style:normal; font-family: Arial; color: black;'>Execution Started at: </font>"+

                                                    "<font style='font-weight: bold; font-size: 14px; font-style:normal; font-family: Arial; color: blue;'>$STARTTIME</font>&nbsp;&nbsp;&nbsp;"+
                                                    "<font style='font-weight: bold; font-size: 14px; font-style:normal; font-family: Arial; color: black;'>Execution Finished at: </font>"+
                                                    "<font style='font-weight: bold; font-size: 14px; font-style:normal; font-family: Arial; color: blue;'>$FINISHTIME</font>"+
                                                    "<font style='font-weight: bold; font-size: 14px; font-style:normal; font-family: Arial; color: black;'>Environment: </font>"+
                                                    "<font style='font-weight: bold; font-size: 14px; font-style:normal; font-family: Arial; color: blue;'>$ENVIRONMENT</font></p>"+
                                                    "<p><font style='font-weight: bold; font-size: 14px; font-style:normal; font-family: Arial; color: black;'>Total Run Duration: </font>"+

                                                    "<font style='font-weight: bold; font-size: 14px; font-style:normal; font-family: Arial; color: blue;'>$RUNDURATION</font></p>"+
                    "<p>"+
                    "<font style='font-weight: bold; font-size: 14px; font-style:normal; font-family: Arial; color: black;'>Test Case(s) Executed:</font>"+
                                                      "<font style='font-weight: bold; font-size: 14px; font-style:normal; font-family: Arial;  color: blue;'>1</font>&nbsp;&nbsp;&nbsp;"+
                                                    "<font style='font-weight: bold; font-size: 14px; font-style:normal; font-family: Arial; color: black;'>Total Passed:</font>"+
                                                    "<font style='font-weight: bold; font-size: 14px; font-style:normal; font-family: Arial;  color: blue;'>0"+ 
                    "</font>&nbsp;&nbsp;&nbsp;"+

                                                "    <font style='font-weight: bold; font-size: 14px; font-style:normal; font-family: Arial; color: black;'>Total Failed: </font>"+
                                                    "<font style='font-weight: bold; font-size: 14px; font-style:normal; font-family: Arial; color: blue;'>1 "+
                    "</font>&nbsp;&nbsp;&nbsp;"+
                                                    "<font style='font-weight: bold; font-size: 14px; font-style:normal; font-family: Arial; color: black;'>Total Warning: </font>"+
                                                    "<font style='font-weight: bold; font-size: 14px; font-style:normal; font-family: Arial; color: blue;'>0"+
                    "</font>"+
                    "</p>"+
                    "<p>"+
                    "<font style='font-weight: bold; font-size: 14px; font-style:normal; font-family: Arial; color: black;'>Report Location: </font>"+

                                                    "<font style='font-weight: bold; font-size: 14px; font-style:normal; font-family: Arial; color: blue;'>REPORT_LOCATION"+
                   " </p>"+
                                              "  <p>"+
                                                  "  </p></div><p>"+
                                             " <font style='font-weight: bold; font-size: 14px; font-style:normal; font-family: Arial; color: black;'>Execution Report</font>"+
                                              "<table width='100%' style='border:1px solid #8E001D'>"+
                                               " <td>"+
                                                    "<table  id='header'  width='100%'>"+
                                                         "<tr style='font-weight: bold; font-size: 14px; font-style:normal; font-family: Arial; color: black; background-color: #f3f3f3'>"+

                                                         "<td width='2%'></td>"+
                                                         "<td width='35%'>Steps</td>"+                                                         
                                                         "<td width='10%'>Status</td>"+
                                                        "<td width='5%'>Browser</td>"+
                                                            "<td width='5%'>Machine</td>"+
                                                         "<td width='15%'>Execution Date and Time</td>"+
                                                         "<td width='28%'>Remarks</td>"+                                                            
                                                    "</table>";
	private String Html_Content_String = "";
	
	private void setIOForHtmlReport(){
		
	}
	public static void WriteLogMessage(String message){
		try{
		System.out.println(message);
		}
		catch(Exception e){
			System.out.println("Exception in logging");
		}
	}
	
	
	private void prepareHtmlReportHeader(){
		try{
		     Html_Header_String = Html_Header_String.replace("$STARTTIME", Property.ExecutionStartTime);
		     Html_Header_String = Html_Header_String.replace("$FINISHTIME", Property.ExecutionEndTime);
		     Html_Header_String = Html_Header_String.replace("$ENVIRONMENT", Property.OSString);
		     Html_Header_String = Html_Header_String.replace("$RUNDURATION", "");
 		}
		catch(Exception e){
			
		}
	}
	
	private String getTestCaseStatusAndSetColor(ResultSet DataresultSet,String TestID) throws Exception{
		String TestCaseStatus = Property.PASS;
		try {
			while(DataresultSet.next()){
				String TestCaseID = DataresultSet.getString("TestCaseID");
				if(TestCaseID.equals(TestID) || TestCaseID.equals("") || TestCaseID == null){
					String Status = DataresultSet.getString("Status");
					if(Status.toLowerCase().equals(Property.FAIL)){
						TestCaseStatus = Property.FAIL;
						break;
					}
				}
			}
			if(TestCaseStatus.equals(Property.FAIL)){
				TestCaseColor = "red";
			}
			else { 
				TestCaseStatus = "green";
			}
			return TestCaseStatus;
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	
	
	private void prepareHtmlContent(ResultSet DataresultSet){
		Boolean ISStartOfNewTestCase = false;
		int i = 0;
		try {
			while(DataresultSet.next()){
				
				String TestCaseID = DataresultSet.getString("TestCaseID");
				String TestStep = DataresultSet.getString("TestStep");
				String BDDStep = DataresultSet.getString("BDDStepName");
				String Status = DataresultSet.getString("Status");
				if(TestCaseID != null && !TestCaseID.equals("")){
					ISStartOfNewTestCase = true;
					//Table row TestCase.
					Html_Content_String = "<table width='100%'>" + 

	                                            "<tr style='font-family: Arial; background-color: #FFF8C6'>";
					//FirstCoulmn.
					Html_Content_String = Html_Content_String + "<td width='2%' style='font-weight: normal; font-size: 12px; font-style:normal; color: black'>";
					
					Html_Content_String = Html_Content_String + "<a id='x_"+ i +"' href='javascript:doMenu(tblID_"+ i + ",x_" + i + ")'>[+]</a></td>";
					
					//SecondCoulmn.
					Html_Content_String = Html_Content_String + "<td width='35%' style='font-weight: normal; font-size: 12px; font-style:normal; color: black'>[" +TestCaseID + "]</td>";
					
					//ThirdColumn
					String TStatus =  getTestCaseStatusAndSetColor(DataresultSet, TestCaseID);
					Html_Content_String = Html_Content_String + "<td width='10%' style='font-weight: bold; font-size: 12px; font-style:normal; '><span id='span_" + i +"' style='color: " + TestCaseColor + ";'>" + TStatus + "</span></td>";
					
					//ForthColumn
					
					Html_Content_String = Html_Content_String + "<td width='5%' style='font-weight: normal; font-size: 12px; font-style:normal; color: black'></td>"+
                                            "<td width='5%' style='font-weight: normal; font-size: 12px; font-style:normal; color: black'></td>"+
                                             "<td width='15%' style='font-weight: normal; font-size: 12px; font-style:normal; color: black'>11/21/2012 : 04:20:13 PM</td>";

					
					i++;
				}
				else{
					ISStartOfNewTestCase = false;
				}
								
				
				
				
				
				
				
				
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	

}
