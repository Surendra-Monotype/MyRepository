package com.auto.solution.TestLogging;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;

import com.auto.solution.Common.Property;

public class TestLogger {

	final static Logger logger = Logger.getLogger(TestLogger.class);
	
	private String loggerPropertiesPath = "";
	
	String logFileLocation = "";
	
	private static TestLogger tstlogger = null;
	
	private TestLogger(String absoluteLogFileLocation){
		
		this.logFileLocation = absoluteLogFileLocation;
		
		String module_path = new File(new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).getParent()).getParent();
		
		String loggerPropertyFilePath = module_path + Property.FileSeperator + "src" + Property.FileSeperator + "main" + Property.FileSeperator + "resources" + Property.FileSeperator + Property.Logger_Log4j_Properties;
		
		this.loggerPropertiesPath = loggerPropertyFilePath;
	}
	
	public static TestLogger getInstance(String absoluteLogFileLocation){
		if(tstlogger == null){
			tstlogger = new TestLogger(absoluteLogFileLocation);
		}
		return tstlogger;
	}
	
	private void setLogFileLocation() {
		
		PropertyConfigurator.configure(loggerPropertiesPath);
		
		PatternLayout layout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
		
		RollingFileAppender fileAppender = null;
		
		try {
			fileAppender = new RollingFileAppender(layout, logFileLocation,false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LogManager.getRootLogger().addAppender(fileAppender);
	}

	public void setLogLevel(String level) {

		setLogFileLocation();

		if (level.toLowerCase().equals(Property.Logger_Level_INFO)
				|| level.equals("")) {
			LogManager.getRootLogger().setLevel(Level.INFO);

		} else if (level.toLowerCase().equals(Property.Logger_Level_ALL)) {
			LogManager.getRootLogger().setLevel(Level.ALL);

		} else if (level.toLowerCase().equals(Property.Logger_Level_DEBUG)) {
			LogManager.getRootLogger().setLevel(Level.DEBUG);

		} else if (level.toLowerCase().equals(Property.Logger_Level_ERROR)) {
			LogManager.getRootLogger().setLevel(Level.ERROR);

		} else if (level.toLowerCase().equals(Property.Logger_Level_FATAL)) {
			LogManager.getRootLogger().setLevel(Level.FATAL);

		} else if (level.toLowerCase().equals(Property.Logger_Level_OFF)) {
			LogManager.getRootLogger().setLevel(Level.OFF);

		} else if (level.toLowerCase().equals(Property.Logger_Level_WARN)) {
			LogManager.getRootLogger().setLevel(Level.WARN);
		}
	}

	public void INFO(String infoMessage) {
		logger.info(infoMessage);
	}

	public void DEBUG(String debugMessage) {
		logger.debug(debugMessage);
	}

	public void ERROR(String errMessage) {
		logger.error(errMessage);
	}

	public void WARN(String warnMessage) {
		logger.warn(warnMessage);
	}

	public void FATAL(String fatalMessage) {
		logger.fatal(fatalMessage);
	}

}
