/**
 * 
 */
package ui;

/**
 * This interface store static configurations
 * 
 * @author Luo Shaohuai
 */
public interface Config {
	public static final boolean onDevelopment = true;
	
	
	//log
	public static final String logFile = "log.txt";
	public static final String logDateFormat = "yyyy/MM/dd HH:mm:ss";
	
	//UI
	//fxml
	public static final String main = "Main.fxml";
	
	
	//misc
	public static final String title = "Forget-Me-Not";
	public static final String floating = "Floating";
	public static final String timeSeparater = " to ";
	public static final String curTimeDateFormat = "MMM dd, yyyy hh:mm:ss a";
	public static final String taskDateFormat = "MMM dd, yyyy hh:mm a";
	
	//style
	public static final String minorTextStyle = "-fx-fill: #777777";
	public static final String timeStyle = "-fx-fill: #222222";
	
	//command
	public static final String cmdList = "list";
	public static final String cmdPageUp = "page up";
	public static final String cmdPageDown = "page down";
	public static final int maxNumHistory = 20;
	
	
}
