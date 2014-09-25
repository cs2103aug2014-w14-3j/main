import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

class Task {
	enum TaskType { FLOATING, TIMED, DEADLINE; }
	String[] wordsInString;
	String task;
	String dateTime;
	
	Task(String command) {
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

class Controller {
	
	enum CommandType { ADD, DELETE, EDIT, DISPLAY; }
	
	private static final int POSITION_OF_OPERATION = 0;
	void execCmd(String command) {
		parseCommand(command);
	}
	
	void parseCommand(String command) {
		String[] splitCommandIntoWords = command.split(" ");
		String operation = splitCommandIntoWords[POSITION_OF_OPERATION];
		executeOperation(operation);
	}
	
	void executeOperation(String operation) {
		
	}
}