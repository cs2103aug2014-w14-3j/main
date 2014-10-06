/**
 * 
 */
package controller;

import java.util.Date;

/**
 * This interface represent Task
 * A Task object should be immutable
 * 
 * Please use this interface for implementing 
 * 	FloatingTask, TimedTask, DeadlineTask
 * 
 * @author
 *
 */
public interface Task {
	public enum TaskType {
		FLOATING, TIMED, DEADLINE
	}
	
	/**
	 * This method return the date and time of the task
	 * 
	 * There should not be Date object only with date (and no time)
	 * For Timed Task, it should return the starting time
	 * For Floating Task, it should return null
	 * @return Date (of this task)
	 */
	Date getDateTime();
	
	/**
	 * This method return description string of the task
	 * @return String (Task description)
	 */
	String getDesc();
	
	/**
	 * This method return true if this task is prioritized
	 * @return Boolean (if prioritized)
	 */
	Boolean isPrioritized();
	
	/**
	 * This method calculate if the task is overdue (for timed and deadline tasks)
	 * Will return false if this is a floating task
	 * 
	 * @return Boolean (if overdue)
	 */
	Boolean isOverdue();
	
	/**
	 * This method return the type of this task
	 * TaskType is an enum with
	 * 	{FLOATING, TIMED, DEADLINE}
	 * @return TaskType
	 */
	TaskType getType();
	
	/**
	 * This method return the string representation of the task
	 * This string should be used for Storage or Display
	 * @return String
	 */
	String toString();
}
