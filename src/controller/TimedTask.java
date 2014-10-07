package controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Represents timed tasks
 * 
 * @author Koh Xian Hui (unless specified)
 */
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
		
	public Date getStartTime() {
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
		
	
	//Author: Cong Thien
    //TaskType: 1 for floating, 2 for Deadline and 3 for TimedTask
	// isPrioritized: 0 is false, 1 is true
	//Format for toString() method:
	//   *TimedTask   : <taskType>$<isPrioritized>$<content>$<startTime>$<endTime>
	public String toString() {
		
		if(!isPrioritized) {
			return "3%0%"+description+"%"+start.getTime()+"%"+end.getTime();
		} else {
			return "3%1%"+description+"%"+start.toString()+"%"+end.toString();
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