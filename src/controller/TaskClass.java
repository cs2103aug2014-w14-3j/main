package controller;


import java.util.Date;

public class TaskClass implements Task {
	boolean isPrioritized;
	String description;
	Date startTime;
	Date endTime;
	TaskType type;
	
	TaskClass() {
	
	}
	
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
	
	public Date getDeadline() {
		return startTime;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	public Date getEndTime() {
		return endTime;
	}
	
	public String getDesc() {
		return description;
	}
	
	public Boolean isPrioritized() {
		return isPrioritized;
	}
	
	public Boolean isOverdue() {
		return false;
	}
	
	public TaskType getType() {
		return type;
	}
	
	public void setPriority(String priority) {
		if(priority.equals("true")) {
			isPrioritized = true;
		} else {
			isPrioritized = false;
		}
	}
	
	public void setDesc(String desc) {
		description = desc;
	}
	
	public void setDeadline(Date date) {
		startTime = date;
	}
	
	public void setStartTime(Date time) {
		startTime = time;
	}
	
	public void setEndTime(Date time) {
		endTime = time;
	}
	
	public void clearTimes() {
		startTime = null;
		endTime = null;
	}
	
	public void setType(TaskType tasktype) {
		type = tasktype;
	}
	
	
	public String toString() {
		boolean isNullStartTime =(startTime == null);
		boolean isNullEndTime = (endTime == null);
		return   description + "%" + 
				(isNullStartTime? " " : startTime.getTime()) + "%" + 
				(isNullEndTime? " " : endTime.getTime()) + "%" +
				isPrioritized;
	}
}
