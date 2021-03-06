/**
 * 
 */
package controller;

import java.util.Date;

/**
 * Interface for TaskClass.
 */
//@author A0115584A
public interface Task {
	public enum TaskType {
		FLOATING, TIMED, DEADLINE
	}
	
	/**
	 * Gets the deadline of a task.
	 * If the task is a Floating task, return null.
	 * 
	 * @return 	Date object representing the deadline of a task.
	 */
	public Date getDeadline();
	
	/**
	 * Gets the start time of a task.
	 * If the task is a Floating task, return null.
	 * 
	 * @return	Date object representing the start time of a task.
	 */
	public Date getStartTime();
	
	/**
	 * Gets the end time of a task.
	 * If the task is a Floating or Deadline task, return null.
	 * 
	 * @return	Date object representing the end time of a task.
	 */
	public Date getEndTime();
	
	/**
	 * Gets the description of a task.
	 * 
	 * @return	Stringed description of a task.
	 */
	public String getDesc();
	
	/**
	 * Checks if a task is prioritized.
	 * 
	 * @return True if a task is prioritized.
	 */
	public Boolean isPrioritized();
	
	/**
	 * Checks if a task is overdue.
	 * 
	 * @return	True if a task is overdue.
	 */
	public Boolean isOverdue();
	
	/**
	 * Gets the type of task.
	 * 
	 * @return	Type of task.
	 */
	public TaskType getType();
	
	/**
	 * Sets the priority of a task.
	 * If string is "true", task is prioritized.
	 * If string is "false", remove priority from a task.
	 * 
	 * @param priority	String of "true" or "false.
	 */
	public void setPriority(String priority);
	
	/**
	 * Sets the description of a task.
	 * 
	 * @param desc	Description of a task.
	 */
	public void setDesc(String desc);
	
	/**
	 * Sets a deadline for task.
	 * If there is no deadline, it remains null. (Floating task)
	 * 
	 * @param date	Deadline of a task.
	 */
	public void setDeadline(Date date);
	
	/**
	 * Sets the start time of a task.
	 * If there is no start time, it remains null. (Floating task)
	 * 
	 * @param time	Start time of a task.
	 */
	public void setStartTime(Date time);
	
	/**
	 * Sets the end time of a task.
	 * If there is no end time, it remains null. (Floating and Deadline tasks)
	 * 
	 * @param time	End time of a task.
	 */
	public void setEndTime(Date time);
	
	/**
	 * Removes the time attributes of a task.
	 */
	public void clearTimes();
	
	/**
	 * Sets the task type to a task.
	 * 
	 * @param type	Type of task.
	 */
	public void setType(TaskType type);
	
	/**
	 * Converts Task object into string.
	 * 
	 * @return	Stringed task.
	 */
	public String toString();
	
	/**
	 * Creates a Task object from a string.
	 * 
	 * @return	Task object.
	 */
	public Task clone();
}