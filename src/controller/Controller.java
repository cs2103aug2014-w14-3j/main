/**
 * 
 */
package controller;

import java.util.List;


/**
 * This interface represent Controller
 * 
 * Controller should take a UI object in constructor for display()
 * 
 * Please use this interface for implementing ListController
 * 
 * @author 
 * 
 */
public interface Controller {
	
	/**
	 * This method translate and execute a command
	 * The command may or may not be a valid command
	 * 
	 * Whenever error occurs, an exception will be thrown
	 * 
	 * @param cmd
	 * @throws Exception
	 */
	public Integer execCmd(String cmd) throws Exception;
	
	public List<String> getCurrentList();
	
	public List<String> suggest(String content);
	
	public String getFeedback();
}
