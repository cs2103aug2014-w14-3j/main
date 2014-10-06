/**
 * 
 */
package ui;

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
		// TODO Auto-generated method stub
		initialize();
		exit();
	}
	
	public static void initialize() {
		System.out.println(initMsg);
	}
	
	public static void exit() {
		System.out.println(exitMsg);
		
	}

}
