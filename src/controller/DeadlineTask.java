package controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class DeadlineTask implements Task{
	boolean isPrioritized;
	String description;
	Date deadline;
	
	DeadlineTask(boolean priority, String desc, Date endDate) {
		isPrioritized = priority;
		description = desc;
		deadline = endDate;
	}
	
	DeadlineTask(String task) throws ParseException {
		String[] getAttributes = task.split("$");
		SimpleDateFormat timeFormat = new SimpleDateFormat("ddMMyy");
		
		if(getAttributes[0].equals("!")) {
			isPrioritized = true;
			description = getAttributes[1];
			deadline = timeFormat.parse(getAttributes[2]);
		} else {
			isPrioritized = false;
			description = getAttributes[0];
			deadline = timeFormat.parse(getAttributes[1]);
		}
	}
		
	public Date getDateTime() {
		return deadline;
	}
	
	public Date getEndTime() {
		return null;
	}
		
	public String getDesc() {
		return description;
	}
		
	public Boolean isPrioritized() {
		return isPrioritized;
	}
		
	public String toString() {
		String task = description + "$" + deadline.toString();
		
		if(isPrioritized()) {
			return "!" + "$" + task;
		} else {
			return task;
		}
	}

	@Override
	public Boolean isOverdue() {
		return false;
	}

	@Override
	public controller.Task.TaskType getType() {
		return controller.Task.TaskType.DEADLINE;
	}
}
