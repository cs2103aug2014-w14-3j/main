/**
 * 
 */
package ui;

import java.util.ArrayList;

/**
 * @author Luo Shaohuai
 *
 */
public class CommandLineUI implements UI {

	static final String initMsg = "        Forget-Me-Not\n"
			 					+ "------------------------------";
	static final String exitMsg = "==============================";
	
	/* (non-Javadoc)
	 * @see ui.UI#updateToScreen()
	 */
	@Override
	public void updateToScreen() {
		for(String i : displayBuf) {
			System.out.println(i);
		}
	}
	
	/* (non-Javadoc)
	 * @see ui.UI#printInitMessage()
	 */
	@Override
	public void initialize() {
		System.out.println(initMsg);
	}
	
	@Override
	public void exit() {
		System.out.println(exitMsg);
		
	}
	
	ArrayList<String> displayBuf;
}
