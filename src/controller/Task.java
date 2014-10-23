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
	
	public Date getDeadline();
	
	public Date getStartTime();
	
	public Date getEndTime();
	
	public String getDesc();
	
	public Boolean isPrioritized();
	
	public Boolean isOverdue();
	
	public TaskType getType();
	
	public void setPriority(String priority);
	
	public void setDesc(String desc);
	
	public void setDeadline(Date date);
	
	public void setStartTime(Date time);
	
	public void setEndTime(Date time);
	
	public void clearTimes();
	
	public void setType(TaskType type);
	
	public String toString();
}