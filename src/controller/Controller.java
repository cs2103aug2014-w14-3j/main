/**
 * 
 */
package controller;

import java.util.List;


/**
 * Interface for ControllerClass which contains the Logic of the software
 * 
 * @author
 */
public interface Controller {
	
	/**
	 * Executes command entered by user
	 * 
	 * @param cmd	Input command from user
	 * @return		Task position of a page on the current list
	 * @throws		Exception 	If command entered by user is invalid
	 * @author		
	 */
	public Integer execCmd(String cmd) throws Exception;
	
	/**
	 * Gets the current list that user is viewing
	 * 
	 * @return	List of stringed tasks
	 * @author
	 */
	public List<String> getCurrentList();
	
	/**
	 * Generates a list of stringed commands and words to suggest to user
	 * 
	 * @param content	Input from user
	 * @return			List of suggested stringed commands and words
	 * @author			
	 */
	public List<String> suggest(String content);
	
	/**
	 * Gets the feedback message after each operation
	 * 
	 * @return	Stringed feedback messages
	 * @author	Koh Xian Hui
	 */
	public String getFeedback();
}
