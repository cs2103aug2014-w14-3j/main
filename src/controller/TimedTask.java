package controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class TimedTask implements Task{
	boolean isPrioritized;
	String description;
	Date start;
	Date end;
	
	TimedTask(boolean priority, String desc, Date startTime, Date endTime) {
		isPrioritized = priority;
		description = desc;
		start = startTime;
		end = endTime;
	}
	
	TimedTask(String task) throws ParseException {
		String[] getAttributes = task.split("$");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm");

		if(getAttributes[0].equals("!")) {
			isPrioritized = true;
			description = getAttributes[1];
			start = timeFormat.parse(getAttributes[2]);
			end = timeFormat.parse(getAttributes[3]);
		} else {
			isPrioritized = false;
			description = getAttributes[0];
			start = timeFormat.parse(getAttributes[1]);
			end = timeFormat.parse(getAttributes[2]);
		}
	}
		
	public Date getDateTime() {
		return start;
	}
	
	public Date getEndTime() {
		return end;
	}
	
	public String getDesc() {
		return description;
	}
		
	public Boolean isPrioritized() {
		return isPrioritized;
	}
		
	public String toString() {
		String task = description + "$" + start.toString() + "$" + end.toString();
		
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
		return controller.Task.TaskType.TIMED;
	}
}