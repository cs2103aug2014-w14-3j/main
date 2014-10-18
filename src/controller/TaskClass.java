package controller;


import java.util.Date;

class TaskClass implements Task, Comparable<Task> {
	boolean isPrioritized;
	String description;
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
			setStartTime(null);
			setType(TaskType.FLOATING);
		} else {
			setStartTime(new Date(Long.parseLong(attributes[3].trim())));
			setType(TaskType.DEADLINE);
		}
		
		if(attributes[3].trim().isEmpty()) {
			setEndTime(null);
		} else {
			setEndTime(new Date(Long.parseLong(attributes[4].trim())));
			setType(TaskType.TIMED);
		}
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
		return isPrioritized + "%" +  
				description + "%" + 
				(isNullStartTime? " " : startTime.getTime()) + "%" + 
				(isNullEndTime? " " : endTime.getTime());
	}
	
	@Override
	public int compareTo(Task task) {
		
		if(this.isPrioritized() && !task.isPrioritized()) {
			return -1;
		} else if(!this.isPrioritized() && task.isPrioritized()) {
			return 1;
		} else {
			if(this.getStartTime() == null && task.getStartTime() != null) {
				return 1;
			} else if(this.getStartTime() != null && task.getStartTime() == null) {
				return -1;
			} else {
				if(this.getStartTime() == null && task.getStartTime() == null) {
					return this.getDesc().compareTo(task.getDesc());
				} else {
					Long thisDate = this.getStartTime().getTime();
					Long taskDate = task.getStartTime().getTime();
					
					return thisDate.compareTo(taskDate);
				}
			}
		}		
	}
	
}
