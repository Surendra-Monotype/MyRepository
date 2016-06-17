<xsl:stylesheet 
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                  xmlns:math="http://exslt.org/math"
                extension-element-prefixes="math"
>
  <xsl:output indent="yes" />
   
  <xsl:template match="testExecution">
    
	<head>
      <title>Test Report</title>
      <script type="text/javascript">
        function displayTestSuitsInTestPlan(planposition) {
			var testSuiteParentContainerForTestPlan = document.getElementById("testSuite_containor_" + planposition);
			var divsInParentContainer = testSuiteParentContainerForTestPlan.children;
			var divs;
			var i;
            for (i = 0; i <xsl:text disable-output-escaping="yes">&lt;</xsl:text> divsInParentContainer.length;i++)
			{
				var node = divsInParentContainer[i];
				var node_id = node.id;
				if (node_id.lastIndexOf("testSuiteContainer_" + planposition + "_", 0) == 0) {
					if(node.style.display == 'none'){
						node.style.display = 'flex';
					}
					else{
						node.style.display = 'none';
					}
				}
			}
		}
		function displayTestCasesInTestSuite(planposition,suiteposition) {
			var testCaseParentContainerForTestSuite = document.getElementById("testCaseContainer_" + planposition + "_" + suiteposition);
			var divsInParentContainer = testCaseParentContainerForTestSuite.children;
			var divs;
           for (var i = 0; i <xsl:text disable-output-escaping="yes">&lt;</xsl:text> divsInParentContainer.length; i++)
			{
				var node = divsInParentContainer[i];
				var node_id = node.id;
				if (node_id.lastIndexOf("testCaseContainer_" + planposition + "_" + suiteposition + "_" , 0) == 0) {
					if(node.style.display == 'none'){
						node.style.display = 'flex';
					}
					else{
						node.style.display = 'none';
					}
				}
			}
		}
		function displayTestStepsIntestCase(planposition,suiteposition,caseposition) {
			var id_parent = "testStepContainer_" + planposition + "_" + suiteposition + "_" + caseposition;
			var divsInParentContainer = document.getElementById(id_parent).children;
			var divs;
            for (var i = 0;i <xsl:text disable-output-escaping="yes">&lt;</xsl:text> divsInParentContainer.length;i++) 
			{
				var node = divsInParentContainer[i];
				var node_id = node.id;
					if(node.style.display == 'none'){
						node.style.display = 'flex';
					}
					else{
						node.style.display = 'none';
					}
				
			}
		}
	</script>
      <style type="text/css">
	  body {
		display: block;
		margin: 0px;
		}
		table#testCasetable {
			width: 20%;
			margin-left: 50%;
		}
		.row{
		font-weight:bolder;
		}
		#testSuite_table{
			width: 45%;
			margin-left: 40%;
		}
		.plan_row_header{
		font-weight : bolder;}
	  #stepColumn{
	  width: 15%;
	  }
	  table#teststeptable {
			margin-left: 2%;
		}
	  .teststeplist{
	    border-radius: 25px;
		background: #07C03B;
		font-family: monospace;
		display: flex;
		margin-top: 1%;
		width: 70%;
		margin-left: 14%;
	  }
	  span#testcasename {
			background: rgba(10, 128, 20, 1);
			font-size: 13px;
			padding-left: 2%;
			padding-top: 10px;
			width: 11%;
			border-radius: 15px;
			font-weight: bolder;
		}
	  .testcaselist{
	    border-radius: 25px;
		  background: #BCBCBC;
		font-family: monospace;
		display: flex;
		margin-top: 1%;
		width: 80%;
		margin-left: 9%;
	  }
	  span#testCase_name {
		background: rgba(10, 128, 20, 1);
		font-size: 25px;
		padding-left: 2%;
		padding-top: 2px;
		width: 11%;
		border-radius: 15px;
		}
	  .testsuitelist{
		border-radius: 25px;
		background: rgba(174, 136, 15, 0.66);
		font-family: monospace;
		display: flex;
		margin-top: 1%;
		width: 90%;
		margin-left: 4%;
	  }
	  #items{
		  display: block;
			list-style-type: disc;
			-webkit-margin-before: 2%;
  	  }
	  #testPlan_table{
	         width: 70%;
			text-align: center;
			margin-left: 15%;
		
	  }
	  #testplan_name{
		    background: rgba(10, 128, 20, 1);
			font-size: 30px;
			padding-left: 2%;
			width: 11%;
			border-radius: 15px;
		}
	  .testplan_container{
				border-radius: 25px;
				background: rgba(7, 192, 164, 0.63); 
				font-family: monospace;
				  display: flex;
	  }
	  #percentPassed{
		padding-left: 20%;}
		
	  #percentage {
		border-radius: 25px;
		background: #07C03B;
		float: right;
		width: 20%;
		font-family: monospace;
		font-size: 350%;
		font-kerning: initial;
	}
	  #summary_container {
			border-radius: 25px;
			background: #079DC0;
			padding: 20px;
			width: 97%;
			font-family: monospace;
		}
	  .black_overlay{
        display: none;
        position: absolute;
        top: 0%;
        left: 0%;
        width: 100%;
        height: 100%;
        background-color: black;
        z-index:1001;
        -moz-opacity: 0.8;
        opacity:.80;
        filter: alpha(opacity=80);
    }
	#errorDetail{
	font-style: italic;
	color: red;
	}
	#testStep{
	font-weight: bolder;
	color: darkblue;
	padding-bottom: 2%;
	}
	#close{
	float: right;
	z-index: 5000;
	}
    .white_content {
        display: none;
        position: absolute;
        top: 25%;
        left: 25%;
        width: 50%;
        height: 50%;
        padding: 16px;
        border: 3px solid orange;
        background-color: whitesmoke;
        z-index:1002;
        overflow: auto;
		font-family: monospace;
    }	  
	  .containerDiv { 
 			 border: 1px solid orange; 
 			width: 100%; 
 			} 
 			 
 			.rowDivHeader { 
 			border: 1px solid orange; 
 			background-color: orange; 
 			color: black; 
 			font-weight: inherit; 
 			} 
 			 
 			.rowDiv { 
 			 
 			background-color: whitesmoke; 
 			} 
 			 
 			.cellDivHeader { 
 			border-right: 1px solid white; 
 			display: table-cell; 
 			width: 9%; 
 			padding: 1px; 
 			text-align: center; 
 			} 
 			 
 			.cellDiv { 
 			border-right: 1px solid white; 
 			display: table-cell; 
 			width: 9%; 
 			padding-right: 4px; 
 			text-align: center; 
 			border-bottom: none; 
 			} 
 			 
 			.lastCell { 
 			  border-right: none; 
 			} 
          #header{
		font-weight: bold;
		background-color: oldlace;
		border: 1px solid;
}
      
#TB_TestSuite {}
table {
    width: 100%;
}
#td_testSuite { 
 			background-color: rgb(64, 119, 64); 
 			background-color: rgb(184, 78, 78); 
 			z-index: -1; 
 			position: inherit; 
 			} 
 			#execution_Details{ 
 			display: block; 
 			height: 140px; 
 			padding-top: 4%; 
 			width: 50%; 
 			}

#test_Header {
    display: block;
    text-align: center;
    font-size: 32px;
    font-family: "Times New Roman", Times, serif;
	padding-bottom: 20px;
}
#errorLink{
  text-decoration: inherit;
  color: red;
  font-variant: small-caps;
}
body {
    font-family: "Times New Roman", Times, serif;
}
			.headerRow{ 
 			background-color: blanchedalmond; 
 			font-weight: bold; 
 			color: black; 
 			} 
 			.contentRow{ 
 			background-color: antiquewhite; 
 			}
      </style>
	</head>
      <body>
	  <div id="test_Header">  
 				<img src="http://static.jabong.com/images/jlite/logo-new.png" alt="Jabong.com" style="float: left;"></img> 
 			    <span style="font-family: monospace;">Test Execution Summary Report </span> 
 			  </div> 
 	 <div id = "summary_container">
	 <xsl:variable name="failed" select="summary/testcasefailed" />
	 <xsl:variable name="passed" select="summary/testcasepassed" />
	 <xsl:variable name="percent" select="($passed div ($passed + $failed) * 100)" />
	 <table style="width: 40%;font-weight: bolder;">
	 <tr>
	 <td><span>Test Groups : </span></td> 
	 <td><span id = "groups"><xsl:value-of select="summary/testgroups" /></span></td>
	 </tr>
	 <tr>
	 <td><span>Start Time : </span></td> 
	 <td><span id = "startTime"><xsl:value-of select="summary/startTime" /></span></td>
	 </tr>
	 <tr>
	 <td><span>End Time : </span></td> 
	 <td><span id = "endTime"><xsl:value-of select="summary/endTime" /></span></td>
	 </tr>
	 <tr>
	 <td><span>TestCase executed : </span> </td> 
	 <td><span id = "executed"><xsl:value-of select="$passed + $failed" /></span></td>
	 </tr>
	 <tr>
	 <td><span>Passed : </span> </td> 
	 <td><span id = "testcasepassed"><xsl:value-of select="summary/testcasepassed" /></span></td>
	 </tr>
	 <tr>
	 <td><span>Total testcases failed : </span> </td> 
	 <td><span id = "testcasefailed"><xsl:value-of select="summary/testcasefailed" /></span></td>
	 </tr>
	 <div id = "percentage">
	 <span id = "percentPassed"><xsl:value-of select="concat(format-number( round(100*$percent) div 100 ,'##0.00' ),'%')" /></span>
	 </div>
	 </table>
	 </div>
	 <xsl:for-each select="Results/TestPlan">
        <div id = "items">
		
          <xsl:apply-templates select="." />
        </div>
      </xsl:for-each>
	  
    </body>
  </xsl:template>
	<xsl:template match="TestPlan">
		<xsl:variable name="testplan_Count" select="count(preceding-sibling::TestPlan)"/>
		<div id = "{concat('testplan_containor_',$testplan_Count)}" class = "testplan_container">
		<xsl:choose>
			<xsl:when test="status = 'fail'">
			<span id = "testplan_name" onclick='displayTestSuitsInTestPlan({$testplan_Count})' style = "background: rgba(202, 23, 57, 0.64);">
			<xsl:value-of select="name" />
		</span>
			</xsl:when>
			<xsl:otherwise>
				<span id = "testplan_name" onclick='displayTestSuitsInTestPlan({$testplan_Count})'>
			<xsl:value-of select="name" />
		</span>
			</xsl:otherwise>
		</xsl:choose>
		
		
		<table id = "testPlan_table">
	<xsl:variable name="testplan_passed" select="passed" />
	 <xsl:variable name="testplan_failed" select="failed" />
	 <xsl:variable name="testplan_percent" select="($testplan_passed div ($testplan_passed + $testplan_failed) * 100)" />
			<tr class = "plan_row_header">
			<td id="plan_column">Status</td>
			<td id="plan_column">TestCase Executed</td>
			<td id="plan_column">Passed</td>
			<td id="plan_column">Failed</td>
			<td id="plan_column">Percent Passed</td>
			</tr>
			<tr>
			<td><xsl:value-of select="status" /></td>
			<td><xsl:value-of select="TotalTestCase" /></td>
			<td><xsl:value-of select="passed" /></td>
			<td><xsl:value-of select="failed" /></td>
			<td><xsl:value-of select="concat(format-number( round(100*$testplan_percent) div 100 ,'##0.00' ),'%')"/></td>
			</tr>
		</table>
		</div>
		<div id="{concat('testSuite_containor_',$testplan_Count)}">
			<xsl:for-each select="testsuite">
				<xsl:call-template name="TSuite">					
					<xsl:with-param name="TestPlanPosition" select="$testplan_Count"/>
				</xsl:call-template>
			</xsl:for-each>
	  </div>
	</xsl:template>
	<xsl:template match="testsuite" name="TSuite">
		<xsl:param name="TestPlanPosition"/>
		<xsl:variable name="testsuiteCount" select="count(preceding-sibling::testsuite)"/>
		<div id = "{concat('testSuiteContainer_',$TestPlanPosition,'_',$testsuiteCount)}" class="testsuitelist" style="display: none;">
		
		<xsl:choose>
			<xsl:when test="status='fail'">
			<span id = "testCase_name" onclick='displayTestCasesInTestSuite({$TestPlanPosition},{$testsuiteCount})'  style="background: rgba(165, 18, 46, 0.62);">
		<xsl:value-of select="name" />
		</span>
			</xsl:when>
			<xsl:otherwise>
			<span id = "testCase_name" onclick='displayTestCasesInTestSuite({$TestPlanPosition},{$testsuiteCount})'>
		<xsl:value-of select="name" />
		</span>
			</xsl:otherwise>
		</xsl:choose>
		
		<table id="testSuite_table">
			<tr class ="row">
				<td>Status</td>
				<td>Time Taken</td>
				
			</tr>
			<tr>
				<td><xsl:value-of select="status" /></td>
				<td><xsl:value-of select="timetaken" /></td>
			</tr>
		</table>
		</div>	
		<div id="{concat('testCaseContainer_',$TestPlanPosition,'_',$testsuiteCount)}">
			<xsl:for-each select="TestCase">
				<xsl:call-template name="TCase">
					<xsl:with-param name="testplanposition" select = "$TestPlanPosition" />
					<xsl:with-param name="TestSuitePosition" select="$testsuiteCount"/>
				</xsl:call-template>
			</xsl:for-each>
		</div>
		
	</xsl:template>
  <xsl:template match="TestCase" name = "TCase">
	<xsl:param name = "testplanposition" />
	<xsl:param name = "TestSuitePosition" />
	<xsl:variable name="testCaseCount" select="count(preceding-sibling::TestCase)"/>
	<div id = "{concat('testCaseContainer_',$testplanposition,'_',$TestSuitePosition,'_',$testCaseCount)}" class = "testcaselist"  style="display: none;">
	<xsl:choose>
		<xsl:when test="status='fail'">
		<span id="testcasename" onclick='displayTestStepsIntestCase({$testplanposition},{$TestSuitePosition},{$testCaseCount})' style="background: rgba(195, 16, 23, 0.6);"><xsl:value-of select="name" /></span>
		</xsl:when>
		<xsl:otherwise>
		<span id="testcasename" onclick='displayTestStepsIntestCase({$testplanposition},{$TestSuitePosition},{$testCaseCount})'><xsl:value-of select="name" /></span>
		</xsl:otherwise>
	</xsl:choose>
	
	
	<table id = "testCasetable">
	<tr>
	<td>Status</td>
	<td><xsl:value-of select='status' /></td>
	</tr>
	<tr>
	<td>Time Taken</td>
	<td><xsl:value-of select='timetaken' /></td>
	</tr>
	</table>
	</div>
	<div id="{concat('testStepContainer_',$testplanposition,'_',$TestSuitePosition,'_',$testCaseCount)}">
			<xsl:for-each select="TestStep">
				<xsl:call-template name="TStep">					
					<xsl:with-param name="testplanpos" select="$testplanposition" />
					<xsl:with-param name="testsuitepos" select="$TestSuitePosition" />
					<xsl:with-param name="TestCasePos" select="$testCaseCount"/>
				</xsl:call-template>
			</xsl:for-each>
		</div>
  </xsl:template>
  <xsl:template match="TestStep" name = "TStep">
	<xsl:param name = "TestCasePos" />
	<xsl:param name = "testplanpos" />
	<xsl:param name = "testsuitepos" />
	<xsl:variable name="testStepPosition" select="count(preceding-sibling::TestStep)"/>
	
	<xsl:choose>
		<xsl:when test="status = 'fail'">
		<div id = "{concat('teststepContainer_',$testplanpos,'_',$testsuitepos,'_',$TestCasePos,'_',$testStepPosition)}"  style="display: none;background: rgba(192, 7, 21, 0.68);" class = "teststeplist">
	<table id = "teststeptable">
	<tr>
		<td id = "stepColumn">Status</td>
		<td><xsl:value-of select="status" /></td>
	</tr>
	<tr>
		<td id = "stepColumn">Step Description</td>
		<td><xsl:value-of select="step_description" /></td>
	</tr>
		<tr>
		<td id = "stepColumn">Test Data</td>
		<td><xsl:value-of select="testdata" /></td>
	</tr>
	<tr>
		<td id = "stepColumn">Test Object</td>
		<td><xsl:value-of select="testObectused" /></td>
	</tr>
	<tr>
		<td id = "stepColumn">Remarks</td>
		<td><xsl:value-of select="remarks" /></td>
	</tr>
	<tr>
		<td id = "stepColumn">SnapShot</td>
		<td>
			<xsl:element name="a">
				<xsl:attribute name="href">
					<xsl:value-of select="step_snapshot" />
				</xsl:attribute>
				<xsl:value-of select="step_snapshot" />
			</xsl:element>
		</td>
	</tr>
	</table>
	</div>
	</xsl:when>
	<xsl:otherwise>
				<div id = "{concat('teststepContainer_',$testplanpos,'_',$testsuitepos,'_',$TestCasePos,'_',$testStepPosition)}"  style="display: none;background: rgba(49, 131, 15, 0.81);" class = "teststeplist">
	<table id = "teststeptable">
	<tr>
		<td id = "stepColumn">Status</td>
		<td><xsl:value-of select="status" /></td>
	</tr>
	<tr>
		<td id = "stepColumn">Step Description</td>
		<td><xsl:value-of select="step_description" /></td>
	</tr>
		<tr>
		<td id = "stepColumn">Test Data</td>
		<td><xsl:value-of select="testdata" /></td>
	</tr>
	<tr>
		<td id = "stepColumn">Test Object</td>
		<td><xsl:value-of select="testObectused" /></td>
	</tr>
		<tr>
		<td id = "stepColumn">Remarks</td>
		<td><xsl:value-of select="remarks" /></td>
	</tr>
	<tr>
		<td id = "stepColumn">SnapShot</td>
		<td>
			<xsl:element name="a">
				<xsl:attribute name="href">
					<xsl:value-of select="step_snapshot" />
				</xsl:attribute>
				<xsl:value-of select="step_snapshot" />
			</xsl:element>
		</td>
	</tr>
	</table>
	</div>
	</xsl:otherwise>
	</xsl:choose>
	
  </xsl:template>
  <xsl:template match="testSuite">
    <xsl:variable name="testSuite_Count" select="count(preceding-sibling::testSuite)"/>
	 <table id = "TB_TestSuite">
		<tr id = "header">
		<td style="width: 1%;"> </td>
		<td style="width: 33%;">TestSuiteName</td>
		<td style="width: 7%;">Start-Time</td>
		<td style="width: 10%;">Status</td>
		<td style="width: 33%;">TotalExecutionTime(msec)</td>
		</tr>
		
		<tr style="height: 55px;">
		<td id = "expander" style="width: 2%;"> <a id="{concat('hs_',$testSuite_Count)}"  href='javascript:doMenu("{concat("containor_",$testSuite_Count)}","{concat("hs_",$testSuite_Count)}")'>[+]</a> </td>

		<xsl:choose>
		<xsl:when test="status = 'FINISHED'"> 
		<td id = "td_testSuite" style="background-color: rgb(52, 205, 52);"><xsl:value-of select="testSuiteName"/> </td>
		<td> <xsl:value-of select="startTime" /> </td>
		<td style="color: rgb(21, 131, 21);"> <xsl:value-of select="status" /></td>
		</xsl:when>
		<xsl:otherwise>
		<td id = "td_testSuite" style="background-color: rgb(194, 97, 105);">
		<xsl:value-of select="testSuiteName" /> </td>
		<td> <xsl:value-of select="startTime" /> </td>
		<td style="color: rgb(194, 97, 105);"> <xsl:value-of select="status" />
		</td>
		</xsl:otherwise>
		
		</xsl:choose>
		<td> <xsl:value-of select="timeTaken" /> </td>
		</tr>
		</table>	  
	  
      <xsl:if test="testSuite">
        <ul>
          <xsl:apply-templates select="testSuite" />
        </ul>
      </xsl:if>
	  <div id="{concat('containor_',$testSuite_Count)}" style="display: none;">
	  <xsl:for-each select="testRunnerResults/testCase">
	  <xsl:call-template name="TC">
	  <xsl:with-param name="TestCasePosition" select="math:random()"/>
	  <xsl:with-param name="TestSuitePosition" select="$testSuite_Count"/>
	  </xsl:call-template>
	  </xsl:for-each>
	  </div>
    
  </xsl:template>
  <xsl:template match="testRunnerResults/testCase" name="TC">
  <xsl:param name="TestSuitePosition"/>
  <xsl:param name="TestCasePosition"/>
  	<table id = "{concat('TB_TestCase',$TestSuitePosition)}">
		<tr id = "header">
		<td style="width: 2%;" id = "testcase_Expander"><a id="{concat('ns_',$TestCasePosition)}" href='javascript:doMenu("{concat("stepContainer_",$TestCasePosition)}","{concat("ns_",$TestCasePosition)}")'>[+]</a></td>
		<td style="width: 33%;"> TestCaseName</td>
		<td style="width: 7%;"> Start-Time </td>
		<td style="width: 10%;"> Status </td>
		<xsl:choose>
		<xsl:when test="status = 'FAILED'">
			<td style="width: 33%;">Error Details</td>
		</xsl:when>
		</xsl:choose>
		<td style="width: 33%;"> ExectionTime(msec) </td>
		</tr>
		<tr> 
		<td> </td>
		<td> <xsl:value-of select="testCaseName"/> </td>
		<td> <xsl:value-of select="startTime"/> </td>
		<xsl:choose>
		<xsl:when test="status = 'FAILED'">		
		
		<td style="color: rgb(255, 0, 31);"> <xsl:value-of select="status" /> </td>
		
		<td> 
		<a id="errorLink" href = "javascript:void(0)" onclick = "document.getElementById('{concat('light_',$TestCasePosition)}').style.display='block';document.getElementById('{concat('fade_',$TestCasePosition)}').style.display='block'" style="text-decoration: inherit;">Click To View Error Details</a> 
		</td>
			
			<xsl:for-each select="failedTestSteps/error">
				<div id="{concat('light_',$TestCasePosition)}" class="white_content">
				<button id="close" onclick = "document.getElementById('{concat('light_',$TestCasePosition)}').style.display='none';document.getElementById('{concat('fade_',$TestCasePosition)}').style.display='none'">X</button>
				<div id="testStep"><span>TestStepName : </span><xsl:value-of select="testStepName"/></div> 
				<div id="errorDetail"><span>Error Details : </span><span id="errorMessage"><xsl:value-of select="detail"/></span></div>
			 
			</div>
			<div id="{concat('fade_',$TestCasePosition)}" class="black_overlay"></div>
			</xsl:for-each>
		</xsl:when>
		<xsl:otherwise>
		<td> <xsl:value-of select="status" /> </td>
		</xsl:otherwise>
		
		</xsl:choose>
		<td> <xsl:value-of select="timeTaken"/> </td>
		</tr>
	</table>
	<div id = "{concat('stepContainer_',$TestCasePosition)}" style="display: none;">
	<table>
	<tr id = "header">
	<td> </td>
	<td> TestStepExecuted </td>
	<td> Started </td>
	<td> Status </td>
	<td> Time Taken(msec) </td>
	<td> Remark </td>
	</tr>
	<xsl:for-each select="testStepResults/result">
	<xsl:choose>
	 <xsl:when test="status = 'FAILED'">
	<tr style="color: rgb(255, 0, 31);">
	<td> </td>
	<td><xsl:value-of select="name"/></td>
	<td><xsl:value-of select="started"/></td>
	<td><xsl:value-of select="status"/></td>
	<td><xsl:value-of select="timeTaken"/></td>	
	<td><xsl:value-of select="message"/></td>
	
	</tr>
	 </xsl:when>
	 <xsl:otherwise>
	 <tr style="color: rgb(16, 129, 16);">
	<td> </td>
	<td><xsl:value-of select="name"/></td>
	<td><xsl:value-of select="started"/></td>
	<td><xsl:value-of select="status"/></td>
	<td><xsl:value-of select="timeTaken"/></td>
	<td><xsl:value-of select="message"/></td>
	</tr>
	 </xsl:otherwise>
	</xsl:choose>
	</xsl:for-each>
	</table>
	</div>
  </xsl:template>
</xsl:stylesheet>