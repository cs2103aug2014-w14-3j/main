package controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

class DeadlineTask implements Task{
	int LENGTH_OF_DATE = 8;
	String[] wordsInString;
	String task;
	String dateTime;
		
	DeadlineTask(String command) {
		wordsInString = command.split(" ");
		dateTime = wordsInString[wordsInString.length - 1];
		
		if(isPrioritized()) {
			task = command.substring(1, command.length() - LENGTH_OF_DATE);
		} else {
			task = command.substring(0, command.length() - LENGTH_OF_DATE);
		}
	}
		
	public Date getDateTime() {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
			return dateFormat.parse(dateTime);
		} catch(ParseException e) {
			
		}
		
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
		return controller.Task.TaskType.DEADLINE;
	}
}
