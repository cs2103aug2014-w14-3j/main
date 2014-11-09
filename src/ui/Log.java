/**
 * 
 */
package ui;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is for log in ui
 * 
 * @author A0119381E
 *
 */
//@author A0119381E
public class Log {
	PrintWriter logWriter;
	
	public Log() {
		logWriter = null;
	}
	
	/**
	 * return if the log cannot work properly
	 * 
	 * @return if there is an error
	 */
	//@author A0119381E
	public boolean hasError() {
		if (logWriter == null) {
			return true;
		} else  if (logWriter.checkError()){
			return true;
		}
		
		return false;
	}
	
	/**
	 * Log the text to file
	 * 
	 * @param logText
	 */
	//@author A0119381E
	public void log(String logText) {
		openFile();
		if (hasError()) {
			return;
		}
		
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(Config.logDateFormat);
		String prefix = dateFormat.format(now) + " - ";
		
		logWriter.println(prefix + logText);
		closeFile();
	}
	
	/**
	 * Open log file
	 */
	//@author A0119381E
	private void openFile() {
		try {
			logWriter = new PrintWriter(new FileWriter(Config.logFile, true));
		} catch (IOException e) {
			if(Config.onDevelopment) {
				e.printStackTrace();
			}
			logWriter = null;
		}	
	}
	
	/**
	 * Close log file
	 */
	//@author A0119381E
	private void closeFile() {
		logWriter.close();
		logWriter = null;
	}
}
