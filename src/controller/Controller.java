/**
 * 
 */
package controller;

import java.util.List;


/**
 * Interface for ControllerClass which contains the Logic of the software.
 */
//@author A0115194J
public interface Controller {
	
	/**
	 * Executes command entered by user.
	 * 
	 * @param cmd	Input command from user.
	 * @return		Task position of a page on the current list.
	 * @throws		Exception 	If command entered by user is invalid.	
	 */
	public Integer execCmd(String cmd) throws Exception;
	
	/**
	 * Gets the current list that user is viewing.
	 * 
	 * @return	List of stringed tasks.
	 */
	public List<String> getCurrentList();
	
	/**
	 * Generates the most possible stringed commands and words to suggest to user.
	 * 
	 * @param content	Input from user.
	 * @return			List of suggested stringed commands and words.			
	 */
	public String suggest(String content);
	
	/**
	 * Gets feedback message after each operation.
	 * 
	 * @return	Stringed feedback message.
	 */
	//@author A0115584A
	public String getFeedback();
	
	/**
	 * Return if the program is exiting
	 * 
	 * @return boolean
	 */
	//@author A0119381E
	public boolean isExiting();
}
