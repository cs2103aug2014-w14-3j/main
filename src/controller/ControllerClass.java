package controller;

import controller.Task.TaskType;
import storage.Storage;
import storage.StoragePlus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;



/*
 * This is the class for the Controller, which serves as the component for logic in the software.
 * It is called by the UI component, processes the user inputs and sends necessary information to the storage to be stored.
 * 
 */

public class ControllerClass implements Controller {

	enum CommandType {
		ADD, DELETE, EDIT, DISPLAY, INVALID
	};

	private static final int POSITION_OF_OPERATION = 0;
	private static final int numTasksInSinglePage = 10;
	private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {
		{
			put("^\\d{8}$", "yyyyMMdd");
			put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
			put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
			put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
			put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
			put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
			put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
			put("^\\d{12}$", "yyyyMMddHHmm");
			put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
			put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$",
					"dd-MM-yyyy HH:mm");
			put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$",
					"yyyy-MM-dd HH:mm");
			put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$",
					"MM/dd/yyyy HH:mm");
			put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$",
					"yyyy/MM/dd HH:mm");
			put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$",
					"dd MMM yyyy HH:mm");
			put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$",
					"dd MMMM yyyy HH:mm");
			put("^\\d{14}$", "yyyyMMddHHmmss");
			put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
			put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$",
					"dd-MM-yyyy HH:mm:ss");
			put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$",
					"yyyy-MM-dd HH:mm:ss");
			put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$",
					"MM/dd/yyyy HH:mm:ss");
			put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$",
					"yyyy/MM/dd HH:mm:ss");
			put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$",
					"dd MMM yyyy HH:mm:ss");
			put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$",
					"dd MMMM yyyy HH:mm:ss");
		}
	};

	private ArrayList<Task> tasks;
	private ArrayList<String> taskStrings;

	// This method starts execution of each user command by first retrieving
	// all existing tasks stored and goes on to parse user command, to determine
	// which course of action to take.
	public ArrayList<String> execCmd(String command) {
		getFileContent();
		parseCommand(command);
		return taskStrings;
	}

	// This method returns all the existing tasks in the list, if any.
	private void getFileContent() {
		Storage storage = createStorageObject();
		taskStrings = storage.read();
	}

	// This method returns a storage object, storagePlus.
	private StoragePlus createStorageObject() {
		return new StoragePlus();
	}

	private void convertStringListTaskList() {
		tasks.clear();
		for (int i = 0; i < taskStrings.size(); i++) {
			tasks.add(convertStringToTask(taskStrings.get(i)));
		}
	}

	private Task convertStringToTask(String taskString) {

	}

	private void convertTaskListStringList() {
		taskStrings.clear();
		for (int i = 0; i < tasks.size(); i++) {
			taskStrings.add(convertTaskToString(tasks.get(i)));
		}
	}

	private String convertTaskToString(Task task) {

	}

	// This method gets the command type of user input and further processes the
	// input.
	private void parseCommand(String command) {
		String operation = getOperation(command);
		CommandType commandType = matchCommandType(operation);
		String content = removeCommandType(command, operation);
		processInput(commandType, content);
	}

	// This method returns the type of operation to be carried out, either add,
	// delete, edit or display.
	public String getOperation(String command) {
		String[] splitCommandIntoWords = command.split(" ");
		String operation = splitCommandIntoWords[POSITION_OF_OPERATION];
		return operation;
	}

	// This method processes each string of user input according to the command
	// type.
	private void processInput(CommandType commandType, String content) {
		switch (commandType) {
		case ADD:
			addTask(content);
			break;
		case DELETE:
			deleteTask(content);
			break;
		case EDIT:
			editTask(content);
		case DISPLAY:
			display();
			break;
		case INVALID:
			// printInvalidCommand();
			break;
		default:
			// throw new Error();
		}
	}

	private void display() {

	}

	private void editTask(String content) {
		if (isEmptyCommand(content)) {
			// throw exception
		} else {
			proceedWithEdit(content);
		}

	}

	private void proceedWithEdit(String content) {
		// TODO Auto-generated method stub
		String[] words = content.split(" ");
		int positionOfTask = getTaskNum(words[0])- 1;
		String attributeToChange = words[1];
		String details = content.substring(content.indexOf(words[2]));
		Task taskToEdit = tasks.get(positionOfTask);
		Task editedTask = editAttribute(taskToEdit, attributeToChange, details);
	}

	private Task editAttribute(Task taskToEdit, String attribute, String details) {
		// TODO Auto-generated method stub
		if (attribute.equalsIgnoreCase("description")) {
			return editDescription(taskToEdit, details);
		} else if (attribute.equalsIgnoreCase("date")) {
			return editDate(taskToEdit, details);
		} else if (attribute.equalsIgnoreCase("time")) {
			return editTime(taskToEdit, details);
		} else {
			editStartEndTimes(taskToEdit, details);
		}
		
	}
	
	

	private void editDescription(Task taskToEdit, String details) {
		// TODO Auto-generated method stub
		TaskType type = taskToEdit.getType();
		if (type==TaskType.FLOATING) {
			Task editedTask = new FloatingTask(taskToEdit.isPrioritized(),details);
		} else if (type==TaskType.DEADLINE) {
			
		} else {
			
		}
		
	}

	// This method checks if the task to be deleted exists and if it exists,
	// proceeds with deletion.
	private void deleteTask(String content) {
		if (isValidDelete(content)) {
			proceedWithDelete(content);
		} else {
			// throw exception
		}
	}

	// This method goes on to delete task after knowing the task to delete
	// exists.
	private void proceedWithDelete(String content) {
		int taskNum = getTaskNum(content);
		executeDelete(taskNum);
	}

	// This method deletes the task with the specified number.
	private void executeDelete(int taskNum) {
		int positionOfTask = taskNum - 1;
		tasks.remove(positionOfTask);
		updateStorage();
	}

	// This method updates the content stored.
	private void updateStorage() {
		convertTaskListStringList();
		Storage storage = createStorageObject();
		storage.write(taskStrings);
	}

	// This method checks if the list of tasks is empty or if the user has not
	// specified the task number to delete. Otherwise, the deletion is deemed
	// valid and it returns true.
	private boolean isValidDelete(String content) {
		try{
			if (tasks.isEmpty()) {
				//throw exception
			} else if (isEmptyCommand(content)) {
				//throw exception
			} else {
				return true;
			}
		} catch (Exception e1) {
			
		} catch (AnotherException e2) {
			
		}
	}

	// This method gets the number of the task to be deleted.
	public static int getTaskNum(String content) {
		return Integer.parseInt(content);
	}

	private void addTask(String content) throws Exception {
		try {
			if (isEmptyCommand(content)) {
				// throw new Exception e;
			} else {
				Task task = processUserInput(content);
				this.tasks.add(task);
			}
		} catch (Exception e) {
			// System.out.println(e.getMessage());
			// Message: Please specify what to add.
		}
	}

	// This method checks if the user has entered anything after the command
	// type.
	private boolean isEmptyCommand(String content) {
		return content.trim().equals("");
	}

	/**
	 * TODO
	 * 
	 * @author Luo Shaohuai
	 * @param content
	 * @return Task Object
	 */
	private Task processUserInput(String content) {
		ArrayList<Date> dates = new ArrayList<Date>();
		String words[] = content.split(" ");
		for (String word : words) {
			String format = determineDateFormat(word);
			if (format != null) {
				SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				try {
					Date date = dateFormat.parse(word);
					if (date != null) {
						dates.add(date);
					}
				} catch (ParseException e) {
					// do nothing
				}
			}
		}

		if (dates.size() > 2) {
			dates = new ArrayList<Date>(dates.subList(dates.size() - 2,
					dates.size()));
		}

		boolean priority = false;
		if (content.contains("!")) {
			priority = true;
		}

		Task task = null;
		if (dates.size() == 0) {
			task = new FloatingTask(priority, content);
		} else if (dates.size() == 1) {
			task = new DeadlineTask(priority, content, dates.get(0));
		} else if (dates.size() == 2) {
			task = new TimedTask(priority, content, dates.get(0), dates.get(1));
		}

		return task;
	}

	/**
	 * @author Retrieved from
	 *         http://stackoverflow.com/questions/3389348/parse-any-date-in-java
	 *         by Luo Shaohuai Determine SimpleDateFormat pattern matching with
	 *         the given date string. Returns null if format is unknown. You can
	 *         simply extend DateUtil with more formats if needed.
	 * @param dateString
	 *            The date string to determine the SimpleDateFormat pattern for.
	 * @return The matching SimpleDateFormat pattern, or null if format is
	 *         unknown.
	 * @see SimpleDateFormat
	 */
	private static String determineDateFormat(String dateString) {
		for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
			if (dateString.toLowerCase().matches(regexp)) {
				return DATE_FORMAT_REGEXPS.get(regexp);
			}
		}
		return null; // Unknown format.
	}

	// This method removes the command, either add, delete, edit or display,
	// from the command string.
	private String removeCommandType(String command, String operation) {
		return command.replace(operation, "").trim();
	}

	// This method returns the command type for each operation.
	private CommandType matchCommandType(String operation) {
		if (operation.equalsIgnoreCase("add")) {
			return CommandType.ADD;
		} else if (operation.equalsIgnoreCase("delete")) {
			return CommandType.DELETE;
		} else if (operation.equalsIgnoreCase("edit")) {
			return CommandType.EDIT;
		} else if ((operation.equalsIgnoreCase("display"))
				|| (operation.equalsIgnoreCase("list"))) {
			return CommandType.DISPLAY;
		} else {
			return CommandType.INVALID;
		}
	}
}
