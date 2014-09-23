/**
 * 
 */
package storage;

import java.util.ArrayList;

/**
 * @author 
 *
 */
public interface Storage {
	ArrayList<String> read();
	void write(ArrayList<String> list);
}
