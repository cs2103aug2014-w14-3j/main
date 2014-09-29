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

	/* (non-Javadoc)
	 * @see ui.UI#display(java.util.ArrayList)
	 */
	@Override
	public void display(ArrayList<String> page) {
		// TODO Auto-generated method stub
		displayBuf = page;
	}
	
	public void printToScreen() {
		for(String i : displayBuf) {
			System.out.println(i);
		}
	}
	
	public void printInitMessage() {
		System.out.println("hahahahahha");
	}
	
	ArrayList<String> displayBuf;
}
