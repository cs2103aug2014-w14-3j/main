package controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class TimedTask implements Task{
	int LENGTH_OF_TIME = 9;
	String[] wordsInString;
	String task;
	String dateTime;
	
	TimedTask(String command) {
		wordsInString = command.split(" ");
		int numOfWordsInString = wordsInString.length;
		dateTime = wordsInString[numOfWordsInString - 2] + " " + wordsInString[numOfWordsInString - 1];
		
		if(isPrioritized()) {
			task = command.substring(1, command.length() - LENGTH_OF_TIME);
		} else {
			task = command.substring(0, command.length() - LENGTH_OF_TIME);
		}
	}
		
	public Date getDateTime() {
		try {
			SimpleDateFormat dateTimeFormat = new SimpleDateFormat("HHHH HHHH");
			return dateTimeFormat.parse(dateTime);
		} catch (ParseException e) {
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
		return controller.Task.TaskType.TIMED;
	}
}