package controller;


import java.util.Date;

/**
 * TaskClass for creating a Task object.
 */
//@author A0115584A
public class TaskClass implements Task {
	boolean isPrioritized;
	String description;
	Date startTime;
	Date endTime;
	TaskType type;
	
	/**
	 * Constructs a Task object.
	 */
	TaskClass() {
	
	}
	
	/**
	 * Constructs a Task object from a string.
	 * 
	 * @param stringedTask	Stringed task.
	 */
	TaskClass(String stringedTask) {
		String[] attributes = stringedTask.split("%");
		
		setDesc(attributes[0]);
		
		if(attributes[1].trim().isEmpty()) {
			setStartTime(null);
			setType(TaskType.FLOATING);
		} else {
			setStartTime(new Date(Long.parseLong(attributes[1].trim())));
			setType(TaskType.DEADLINE);
		}
		
		if(attributes[2].trim().isEmpty()) {
			setEndTime(null);
		} else {
			setEndTime(new Date(Long.parseLong(attributes[2].trim())));
			setType(TaskType.TIMED);
		}
		
		setPriority(attributes[3]);
	}
	
	/**
	 * Gets the deadline of a task.
	 * If the task is a Floating task, return null.
	 * 
	 * @return 	Date object representing the deadline of a task.
	 */
	public Date getDeadline() {
		return startTime;
	}
	
	/**
	 * Gets the start time of a task.
	 * If the task is a Floating task, return null.
	 * 
	 * @return	Date object representing the start time of a task.
	 */
	public Date getStartTime() {
		return startTime;
	}
	
	/**
	 * Gets the end time of a task.
	 * If the task is a Floating or Deadline task, return null.
	 * 
	 * @return	Date object representing the end time of a task.
	 */
	public Date getEndTime() {
		return endTime;
	}
	
	/**
	 * Gets the description of a task.
	 * 
	 * @return	Stringed description of a task.
	 */
	public String getDesc() {
		return description;
	}
	
	/**
	 * Checks if a task is prioritized.
	 * 
	 * @return True if a task is prioritized.
	 */
	public Boolean isPrioritized() {
		return isPrioritized;
	}
	
	/**
	 * Checks if a task is overdue.
	 * 
	 * @return	True if a task is overdue.
	 */
	public Boolean isOverdue() {
		return false;
	}
	
	/**
	 * Gets the type of task.
	 * 
	 * @return	Type of task.
	 */
	public TaskType getType() {
		return type;
	}
	
	/**
	 * Sets the priority of a task.
	 * If string is "true", task is prioritized.
	 * If string is "false", remove priority from a task.
	 * 
	 * @param priority	String of "true" or "false.
	 */
	public void setPriority(String priority) {
		if(priority.equals("true")) {
			isPrioritized = true;
		} else {
			isPrioritized = false;
		}
	}
	
	/**
	 * Sets the description of a task.
	 * 
	 * @param desc	Description of a task.
	 */
	public void setDesc(String desc) {
		description = desc;
	}
	
	/**
	 * Sets a deadline for task.
	 * If there is no deadline, it remains null. (Floating task)
	 * 
	 * @param date	Deadline of a task.
	 */
	public void setDeadline(Date date) {
		startTime = date;
	}
	
	/**
	 * Sets the start time of a task.
	 * If there is no start time, it remains null. (Floating task)
	 * 
	 * @param time	Start time of a task.
	 */
	public void setStartTime(Date time) {
		startTime = time;
	}
	
	/**
	 * Sets the end time of a task.
	 * If there is no end time, it remains null. (Floating and Deadline tasks)
	 * 
	 * @param time	End time of a task.
	 */
	public void setEndTime(Date time) {
		endTime = time;
	}
	
	/**
	 * Removes the time attributes of a task.
	 */
	public void clearTimes() {
		startTime = null;
		endTime = null;
	}
	
	/**
	 * Sets the task type to a task.
	 * 
	 * @param type	Type of task.
	 */
	public void setType(TaskType tasktype) {
		type = tasktype;
	}
	
	/**
	 * Converts Task object into string.
	 * 
	 * @return	Stringed task.
	 */
	public String toString() {
		boolean isNullStartTime =(startTime == null);
		boolean isNullEndTime = (endTime == null);
		return   description + "%" + 
				(isNullStartTime? " " : startTime.getTime()) + "%" + 
				(isNullEndTime? " " : endTime.getTime()) + "%" +
				isPrioritized;
	}
	
	/**
	 * Creates a Task object from a string.
	 * 
	 * @return	Task object.
	 */
	//@author
	public Task clone() {
		return new TaskClass(toString());
	}
}
