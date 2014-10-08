/**
 * 
 */
package ui;

import javafx.application.Application;
import javafx.stage.Stage;
import controller.Controller;
import controller.ControllerClass;

/**
 * @author Luo Shaohuai
 *
 */
public class Main extends Application{
	public static final String initMsg = "        Forget-Me-Not\n"
				+ "------------------------------";
	public static final String exitMsg = "==============================";
	
	/**
	 * Command line entry
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CmdInitialize();
		CmdExit();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
	}
	
	public static void CmdInitialize() {
		System.out.println(initMsg);
	}
	
	public static void CmdExit() {
		System.out.println(exitMsg);
		
	}
}
