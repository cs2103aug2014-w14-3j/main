package controller;

import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

class TaskClass {
	enum TaskType { FLOATING, TIMED, DEADLINE; }
	String[] wordsInString;
	String task;
	String dateTime;
		
	TaskClass(String command) {
		wordsInString = command.split(" ");

	}
		
	Date getDateTime() throws ParseException {
		DateFormat df = new SimpleDateFormat("dd.MM h:mm a");
		Date dateForm = (Date) df.parse(dateTime);
		return dateForm;
	}
		
	String getDesc() {
		return task;
	}
		
	boolean isPrioritised() {
		if(task.substring(0, 1).equals("!")) {
			return true;
		} else {
			return false;
		}
	}
		
	public String toString() {
		return task + " " + dateTime;
	}
}