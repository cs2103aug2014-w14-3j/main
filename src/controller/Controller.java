/**
 * 
 */
package controller;

/**
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
	void execCmd(String cmd) throws Exception;
	
	/**
	 * List of commands
	 */
	static final String CMD_ADD= "add";
	static final String CMD_LIST= "list";
	//TO-DO
}
