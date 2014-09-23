/**
 * 
 */
package controller;

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
	
	public static final Integer taskPerPage = 10;
	
	/**
	 * This method translate and execute a command
	 * The command may or may not be a valid command
	 * 
	 * Whenever error occurs, an exception will be thrown
	 * 
	 * @param cmd
	 * @throws Exception
	 */
	void execCmd(String cmd) throws Exception;
	
	/**
	 * List of commands
	 */
	static final String CMD_ADD= "add";
	static final String CMD_LIST= "list";
	//TO-DO
}
