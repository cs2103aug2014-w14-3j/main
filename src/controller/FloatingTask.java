package controller;

import java.util.Date;

// Author: Xian Hui (Author is Xian Hui unless specified.)
// This class is used to represent FloatingTask.
class FloatingTask implements Task{
	boolean isPrioritized;
	String description;
		
	FloatingTask(boolean priority, String desc) {
		description = desc;
		isPrioritized = priority;
	}
		
	public Date getStartTime() {
		return null;
	}
	
	public Date getEndTime(){
		return null;
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
		//   *FloatingTask: <taskType>$<isPrioritized>$<content>
	public String toString() {
		if (!isPrioritized){
			return "1$0$"+description;
		}else{
			return "1$1$"+description;
		}
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