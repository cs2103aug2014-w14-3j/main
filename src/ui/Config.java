/**
 * 
 */
package ui;

/**
 * @author Luo Shaohuai
 *
 */
public interface Config {
	public static boolean onDevelopment = true;
	
	
	//log
	public static String logFile = "log.txt";
	public static String logDateFormat = "yyyy/MM/dd HH:mm:ss";
	
	//UI
	//fxml
	public static String main = "Main.fxml";
	
	
	//sytle
	public static String title = "Forget-Me-Not";
	public static String curTimeDateFormat = "MMM dd, yyyy hh:mm:ss a";
	public static String taskDateFormat = "MMM dd, yyyy hh:mm a";
	
	//command handle by UI
	public static String cmdExit = "exit";
	
}
