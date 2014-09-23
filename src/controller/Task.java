/**
 * 
 */
package controller;

import java.util.Date;

/**
 * @author
 *
 */
public interface Task {
	enum TaskType {
		FLOATING, TIMED, DEADLINE
	}

	Date getDateTime();
	String getDesc();
	Integer getRank();
	TaskType getType();
	String toString();
}
