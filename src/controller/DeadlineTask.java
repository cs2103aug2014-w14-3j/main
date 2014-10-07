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
	
	public Date getStartTime(){
		return null;
	}
	
	
	public Date getEndTime() {
		return deadline;
	}
	
	public Date getEndTime() {
		return deadline;
	}
		
	public String getDesc() {
		return description;
	}
		
	public Boolean isPrioritized() {
		return isPrioritized;
	}
		
	
		//Author: Cong Thien
	   //TaskType: 1 for floating, 2 for Deadline and 3 for TimedTask
		// isPrioritized: 0 is false, 1 is true
		//Format for toString() method
		//    *DeadlineTask: <taskType>$<isPrioritized>$<content>$<date>
	
	public String toString() {
		
		if(!isPrioritized) {
			return "2$0$"+description+"$"+deadline.toString();
		} else{
			return "2$1$"+description+"$"+deadline.toString();
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
