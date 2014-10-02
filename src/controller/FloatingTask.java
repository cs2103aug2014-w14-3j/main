package controller;

import java.util.Date;

class FloatingTask implements Task{
	boolean isPrioritized;
	String description;
		
	FloatingTask(boolean priority, String desc) {
		description = desc;
		isPrioritized = priority;
	}
		
	public Date getDateTime() {
		return null;
	}
		
	public String getDesc() {
		return description;
	}
		
	public Boolean isPrioritized() {
		return isPrioritized;
	}
		
	public String toString() {
		return description;
	}

	@Override
	public Boolean isOverdue() {
		return false;
	}

	@Override
	public controller.Task.TaskType getType() {
		return controller.Task.TaskType.FLOATING;
	}
}