package controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	
	TaskClass(String stringedTask) throws ParseException {
		String[] attributes = stringedTask.split("%");
		
		setPriority(attributes[0]);
		setDesc(attributes[1]);
		setDeadline(new Date(Long.parseLong(attributes[2].trim())));
		setStartTime(new Date(Long.parseLong(attributes[3].trim())));
		setEndTime(new Date(Long.parseLong(attributes[4].trim())));
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
	
	public void setDeadline(Date date) throws ParseException {
		deadline = date;
	}
	
	public void setStartTime(Date time) throws ParseException {
		startTime = time;
	}
	
	public void setEndTime(Date time) throws ParseException {
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
		return isPrioritized + "%" +  description + "%" + deadline.getTime() + "%" + startTime.getTime() + "%" + endTime.getTime();
	}
}
