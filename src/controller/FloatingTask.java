package controller;

import java.util.Date;

class FloatingTask implements Task{
	String task;
		
	FloatingTask(String command) {
		if(isPrioritized()) {
			task = command.substring(1);
		} else {
			task = command;
		}
	}
		
	public Date getDateTime() {
		return null;
	}
		
	public String getDesc() {
		return task;
	}
		
	public Boolean isPrioritized() {
		if(task.substring(0, 1).equals("!")) {
			return true;
		} else {
			return false;
		}
	}
		
	public String toString() {
		return task;
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