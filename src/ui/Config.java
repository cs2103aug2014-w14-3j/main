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
	
	public static String titleStyle = "-fx-fill: #FFEEDD;";
	public static String rootStyle = "-fx-background-color: #CCBBAA;";
	public static String topStyle = "-fx-background-color: #AABBCC;";
	public static String bottomStyle = "-fx-background-color: #AABBCC;";
	public static String centerStyle = "-fx-background-color: #CCBBAA;";
	public static String inputFieldStyle = "-fx-font-size: 16pt;";
	public static String inputFieldPlaceholder = "Enter command here";
	
	//window
	public static Integer width = 600;
	public static Integer height = 400;
	
	//command handle by UI
	public static String cmdExit = "exit";
	
}
