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
 * @author Luo Shaohuai
 *
 */
public class Log {
	public Log() {
		logWriter = null;
	}
	
	public boolean hasError() {
		if (logWriter == null) {
			return true;
		} else  if (logWriter.checkError()){
			return true;
		}
		
		return false;
	}
	
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
	
	private void closeFile() {
		logWriter.close();
		logWriter = null;
	}
	
	PrintWriter logWriter;
}
