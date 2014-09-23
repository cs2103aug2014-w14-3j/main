/**
 * 
 */
package storage;

import java.util.ArrayList;

/**
 * This interface represent the storage
 * 
 * Please use this for implementing FileStorage
 * 
 * @author 
 *
 */
public interface Storage {
	/**
	 * This method return a list of String (generated by Task object)
	 * 
	 * @return ArrayList<String>
	 */
	ArrayList<String> read();
	
	/**
	 * This method store the list of string into the storage
	 * 
	 * @param list : list of string representation of Tasks
	 */
	void write(ArrayList<String> list);
}
