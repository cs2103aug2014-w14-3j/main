package controller;

import java.text.ParseException;
import java.util.Date;

class TaskClass implements Task {
	boolean isPrioritized;
	String description;
	Date deadline;
	Date startTime;
	Date endTime;
	TaskType type;
	
	TaskClass() {
	
	}
	
	TaskClass(String stringedTask) {
		String[] attributes = stringedTask.split("%");
		
		setPriority(attributes[0]);
		setDesc(attributes[1]);
		
		if(attributes[2].trim().isEmpty()) {
			setDeadline(null);
		} else {
			setDeadline(new Date(Long.parseLong(attributes[2].trim())));
		}
		
		if(attributes[3].trim().isEmpty()) {
			setStartTime(null);
		} else {
			setStartTime(new Date(Long.parseLong(attributes[3].trim())));
		}
		
		if(attributes[4].trim().isEmpty()) {
			setEndTime(null);
		} else {
			setEndTime(new Date(Long.parseLong(attributes[4].trim())));
		}
		
		setType();
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
		deadline = date;
	}
	
	public void setStartTime(Date time) {
		startTime = time;
	}
	
	public void setEndTime(Date time) {
		endTime = time;
	}
	
	public void setType() {
		if(deadline == null && (startTime == null && endTime == null)) {
			type = TaskType.FLOATING;
		} else if(startTime == null && endTime == null) {
			type = TaskType.DEADLINE;
		} else {
			type = TaskType.TIMED;
		}
	}
	
	
	public String toString() {
		boolean isNullStartTime =(startTime == null);
		boolean isNullEndTime = (endTime == null);
		boolean isNullDeadline = (deadline == null);
		return isPrioritized + "%" +  
				description + "%" + 
				(isNullDeadline? " " : deadline.getTime()) + "%" +
				(isNullStartTime? " " : startTime.getTime()) + "%" + 
				(isNullEndTime? " " : endTime.getTime());
	}
}
