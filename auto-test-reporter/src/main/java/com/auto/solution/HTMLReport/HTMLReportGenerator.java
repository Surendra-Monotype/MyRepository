package com.auto.solution.HTMLReport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
public class HTMLReportGenerator {
	
	private String xmlFile;
	
	private String htmlFile;
	
	private String referenceProjectBasePath = new File(new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).getParent()).getParent();
	
	private String FileSeperator = System.getProperty("file.separator");
	
	private String extHelperFile;
	
	public HTMLReportGenerator(String sourceFile, String targetFile){
		
		this.xmlFile = sourceFile;
		
		this.htmlFile = targetFile;
		
		this.setExtBaseBath();
	}
	
	private void setExtBaseBath(){
		this.extHelperFile = this.referenceProjectBasePath + FileSeperator + "ext" + FileSeperator;
	}
	
	public void generateHtmlReport(String helperXslFileName) throws Exception{
	try{
		
		TransformerFactory tFactory = TransformerFactory.newInstance();
	 
		Source xslDoc = new StreamSource(this.extHelperFile + helperXslFileName);
		
		Source xmlDoc = new StreamSource(this.xmlFile);
		
		OutputStream htmlFile = new FileOutputStream(this.htmlFile);

		Transformer transformer = tFactory.newTransformer(xslDoc);
		
		transformer.transform(xmlDoc, new StreamResult(htmlFile));
		}
		catch(Exception e){
			throw e;
		}
	}

}
