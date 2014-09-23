/**
 * 
 */
package ui;

import java.util.ArrayList;

/**
 * This interface represent the UI
 * 
 * Please use this for implementing CommandLineUI, GraphicalUI
 * 
 * @author Luo Shaohuai
 *
 */
public interface UI {
	
	/**
	 * This method update the display buffer
	 * the display buffer will be updated to screen
	 * 
	 * @param page : page should contain one page of task element
	 */
	void display(ArrayList<String> page);
}
