/**
 * 
 */
package ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import controller.Controller;
import controller.ControllerClass;

/**
 * @author Luo Shaohuai
 *
 */
public class Main {
	public static final String initMsg = "        Forget-Me-Not\n"
				+ "------------------------------";
	public static final String exitMsg = "==============================";
	
	/**
	 * Command line entry
	 * @param args
	 */
	public static void main(String[] args) {
		onInitialize();
		String a = "saa$aaa$bbb";
		String aa[] = a.split("$");
		
		
		Controller controller = new ControllerClass();
		while(true) {
			System.out.println("Command: ");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String cmd;
			try {
				cmd = in.readLine();
			} catch (IOException e1) {
				continue;
			}
			if(cmd.trim().isEmpty()) {
				break;
			}
			try {
				ArrayList<String> output = controller.execCmd(cmd);
				for (String item : output) {
					System.out.println(item);
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		onExit();
	}
	
	public static void onInitialize() {
		System.out.println(initMsg);
	}
	
	public static void onExit() {
		System.out.println(exitMsg);
	}

}
