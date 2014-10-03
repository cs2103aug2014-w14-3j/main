/**
 * 
 */
package ui;


/**
 * This interface represent the UI
 * 
 * Please use this for implementing CommandLineUI, GraphicalUI
 * 
 * @author Luo Shaohuai
 *
 */
public interface UI {
	
	void updateToScreen();
	
	void initialize();
	
	void exit();
}
