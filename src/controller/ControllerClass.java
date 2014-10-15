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
import java.util.TimeZone;

/*
 * This is the class for the Controller, which serves as the component for logic in the software.
 * It is called by the UI component, processes the user inputs and sends necessary information to the storage to be stored.
 * 
 */

public class ControllerClass implements Controller {

	enum CommandType {
		ADD, DELETE, EDIT, DISPLAY, SEARCH
	};

	private static final int POSITION_OF_OPERATION = 0;
	private static final int numTasksInSinglePage = 10;
	private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {
		private static final long serialVersionUID = -8905622371814695255L;

		{
			put("^\\d{8}$", "yyyyMMdd");
			put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
			put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
			put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "dd/MM/yyyy");
			put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
			put("^\\d{1,2}-\\d{1,2}$", "dd-MM");
			put("^\\d{1,2}/\\d{1,2}$", "dd/MM");
		}
	};

	private static final Map<String, String> TIME_FORMAT_REGEXPS = new HashMap<String, String>() {
		private static final long serialVersionUID = -1690161551539169383L;

		{
			put("^\\d{1,2}:\\d{2}$", "HH:mm");
			put("^\\d{4}$", "HHmm");
		}
	};

	private ArrayList<TaskClass> tasks;
	private ArrayList<String> taskStrings;
	private Storage storage;

	public ControllerClass() {
		storage = createStorageObject();
		tasks = new ArrayList<TaskClass>();
		getFileContent();
	}

	// This method starts execution of each user command by first retrieving
	// all existing tasks stored and goes on to parse user command, to determine
	// which course of action to take.
	public ArrayList<String> execCmd(String command) {
		parseCommand(command);
		return taskStrings;
	}

	//

	/**
	 * This method returns all the existing tasks in the list, if any.
	 * 
	 * @return void
	 * @author G. Vishnu Priya
	 */
	private void getFileContent() {
		taskStrings = storage.read();
		convertStringListTaskList();
	}

	/**
	 * This method returns a storage object, storagePlus.
	 * 
	 * @return StoragePlus Object
	 * @author G. Vishnu Priya
	 */
	private StoragePlus createStorageObject() {
		return new StoragePlus();
	}

	private void convertStringListTaskList() {
		tasks.clear();
		try {
			for (int i = 0; i < taskStrings.size(); i++) {
				tasks.add(convertStringToTask(taskStrings.get(i)));
			}
		} catch (ParseException e) {
			// nothing
		}
	}

	private Task convertStringToTask(String taskString) throws ParseException {

		String[] para = taskString.trim().split("%");
		Boolean isPrioritized;
		Task task = null;

		if (para[1].equals("0")) {
			isPrioritized = false;
		} else {
			isPrioritized = true;
		}

		String content = para[2];

		switch (Integer.parseInt(para[0])) {

		case 1:
			task = new FloatingTask(isPrioritized, content);
			break;
		case 2:
			Date date = new Date(Long.parseLong(para[3]));
			task = new DeadlineTask(isPrioritized, content, date);
			break;
		case 3:
			Date startTime = new Date(Long.parseLong(para[3]));
			Date endTime = new Date(Long.parseLong(para[4]));
			task = new TimedTask(isPrioritized, content, startTime, endTime);
			break;
		}

		return task;

	}

	// This method converts tasks from tasks list to taskStrings list.
	private void convertTaskListStringList() {
		taskStrings.clear();
		for (int i = 0; i < tasks.size(); i++) {
			taskStrings.add(convertTaskToString(tasks.get(i)));
		}
	}

	// This method converts tasks to strings to be stored in taskStrings list.
	private String convertTaskToString(Task task) {
		return task.toString();
	}

	// This method gets the command type of user input and further processes the
	// input.
	private void parseCommand(String command) {
		String operation = getOperation(command);
		CommandType commandType = matchCommandType(operation);
		String content = removeCommandType(command, operation);
		processInput(commandType, content);
		convertTaskListStringList();
		addTaskNum();
		updateStorage();
	}

	/**
	 * Appends the task number to the beginning of each string in taskStrings.
	 * @return void
	 * @author G. Vishnu Priya
	 */
	private void addTaskNum() {
		int numPages = (int)Math.ceil((double)(taskStrings.size())/(numTasksInSinglePage));
		taskStrings.add(0,Integer.toString(numPages));
		for (int i=1; i<taskStrings.size(); i++) {
			int numTask = i%(numTasksInSinglePage);
			if (numTask==0) {
				numTask = numTasksInSinglePage;
			}
			String numberedTask = Integer.toString(numTask) + ". " + taskStrings.get(i);
			taskStrings.set(i, numberedTask);
		}
	}

	// This method returns the type of operation to be carried out, either add,
	// delete, edit or display.
	public String getOperation(String command) {
		String[] splitCommandIntoWords = command.split(" ");
		String operation = splitCommandIntoWords[POSITION_OF_OPERATION];
		return operation;
	}

	/**
	 * Chooses which course of action to take according to the command type.
	 *
	 * @return void
	 * @author G. Vishnu Priya
	 * @throws Exception 
	 */
	private void processInput(CommandType commandType, String content) throws Exception {
		switch (commandType) {
		case ADD:
			addTask(content);
			break;
		case DELETE:
			deleteTask(content);
			break;
		case EDIT:
			editTask(content);
			break;
		case DISPLAY:
			display();
			break;
		case SEARCH:
			search();
			break;
		default:
			System.out.println("Invalid command.");
		}
	}

	/**
	 * 
	 * @return
	 * @author
	 */
	private void search() {
		// TODO Auto-generated method stub
		
	}

	/*
	 * Displays the existing tasks to the user.
	 * 
	 * @return ArrayList<String>
	 * 
	 * @author Koh Xian Hui
	 */
	private ArrayList<String> display() {
		ArrayList<String> displayTasks = new ArrayList<String>();
		if (!tasks.isEmpty()) {
			for (Task taskItem : tasks) {
				String stringedTask = taskItem.toString().replace("%", " ");

				displayTasks.add(stringedTask.substring(4));

			}
		}

		return displayTasks;
	}

	/**
	 * Checks if the user has specified any task to edit and if specified,
	 * proceeds with the edit.
	 * 
	 * @param content
	 * @return void
	 * @author G. Vishnu Priya
	 * @throws Exception 
	 */
	private void editTask(String content) throws Exception {
			if (isEmptyCommand(content)) {
				throw new Exception("Please specify what to edit.");
			} else {
				proceedWithEdit(content);
			}
	}

	/**
	 * Edits the task using the specified content.
	 * 
	 * @param content
	 * @return void
	 * @author G. Vishnu Priya
	 */
	private void proceedWithEdit(String content) {
		String[] words = content.split(" ");
		int positionOfTask = getTaskNum(words[0]) - 1;
		String attributeToChange = words[1];
		String editDetails = content.substring(content.indexOf(words[2]));
		TaskClass taskToEdit = tasks.get(positionOfTask);
		editAttribute(taskToEdit, attributeToChange,
				editDetails);
	}

	/**
	 * Matches the attribute to be edited and calls the relevant function.
	 * 
	 * @param taskToEdit
	 * @param attribute
	 * @param editDetails
	 * @return void
	 * @author G. Vishnu Priya
	 */
	private void editAttribute(TaskClass taskToEdit, String attribute,
			String editDetails) {
		if (attribute.equalsIgnoreCase("desc")) {
			editDescription(taskToEdit, editDetails);
		} else if (attribute.equalsIgnoreCase("date")) {
			editDate(taskToEdit, editDetails);
		} else if (attribute.equalsIgnoreCase("time")) {
			editStartEndTimes(taskToEdit, editDetails);
		}
	}

	private Task editStartEndTimes(Task taskToEdit, String details) {
		Task editedTask = null;
		String[] times = details.split(" ");
		Date start;
		Date end;
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm");
		try {
			start = timeFormat.parse(times[0]);
			end = timeFormat.parse(times[2]);
			editedTask = new TimedTask(taskToEdit.isPrioritized(),
					taskToEdit.getDesc(), start, end);
		} catch (ParseException e) {
			// nothing
		}

		return editedTask;
	}

	// First initialize editedTask is null, then when use the method, just check
	// if it's null or not
	// Timed task no date? so only possible tasks are floating or deadline. If
	// timed task given throw exception?
	private Task editDate(Task taskToEdit, String details) {
		TaskType type = taskToEdit.getType();
		Task editedTask = null;
		SimpleDateFormat timeFormat = new SimpleDateFormat("ddMMyy");
		try {
			Date date = timeFormat.parse(details);
			if (type == TaskType.DEADLINE) {
				editedTask = new DeadlineTask(taskToEdit.isPrioritized(),
						taskToEdit.getDesc(), date);
			} else if (type == TaskType.TIMED) {
				editedTask = new TimedTask(taskToEdit.isPrioritized(),
						taskToEdit.getDesc(), taskToEdit.getStartTime(),
						taskToEdit.getEndTime());
			}
		} catch (ParseException e) {
			// nothing
		}
		return editedTask;
	}

	/**
	 * Replaces the description of task with the desc string.
	 * 
	 * @param taskToEdit
	 * @param details
	 * @return Task Object
	 * @author G. Vishnu Priya
	 */
	private void editDescription(TaskClass taskToEdit, String desc) {
			taskToEdit.setDesc(desc);
	}

	/**
	 * This method checks if the task to be deleted exists or is specified and
	 * if it is valid, proceeds with deletion.
	 * 
	 * @param content
	 * @return void
	 * @author G. Vishnu Priya
	 * @throws Exception 
	 */
	private void deleteTask(String content) throws Exception {
			if (isValidDelete(content)) {
				proceedWithDelete(content);
			}
	}

	/**
	 * This method goes on to delete task after knowing the task to delete
	 * exists.
	 * 
	 * @param content
	 * @throws NumberFormatException
	 * @return void
	 * @author G. Vishnu Priya
	 * @throws Exception 
	 */
	private void proceedWithDelete(String content) throws Exception {
		try {
			int taskNum = getTaskNum(content);
			executeDelete(taskNum);
		} catch (NumberFormatException e) {
			throw new Exception("Invalid delete format. Please enter task number.");
		}
	}

	/**
	 * This method deletes the task with the specified number.
	 * 
	 * @param taskNum
	 * @return void
	 * @author G. Vishnu Priya
	 * @throws Exception 
	 */
	private void executeDelete(int taskNum) throws Exception {
		try {
			int positionOfTask = taskNum - 1;
			tasks.remove(positionOfTask);
		} catch (IndexOutOfBoundsException e) {
			throw new Exception("Task does not exist. Please enter task number within the range.");
		}
	}

	/**
	 * This method updates the content stored.
	 * 
	 * @return void
	 * @author G. Vishnu Priya
	 */
	private void updateStorage() {
		convertTaskListStringList();
		storage.write(taskStrings);
	}

	/**
	 * This method checks if the list of tasks is empty or if the user has not
	 * specified the task number to delete. Otherwise, the deletion is deemed
	 * valid and it returns true.
	 * 
	 * @param content
	 * @return boolean
	 * @throws Exception
	 * @author G. Vishnu Priya
	 */
	private boolean isValidDelete(String content) throws Exception {
		if (tasks.isEmpty()) {
			throw new Exception("Nothing to delete list is empty!");
		} else if (isEmptyCommand(content)) {
			throw new Exception("Please specify what to delete.");
		} else {
			return true;
		}
	}
	
	/**
	 * This method checks if the user has entered anything after the command
	 * type.
	 * 
	 * @param content
	 * @return boolean
	 * @author G. Vishnu Priya
	 */
	private boolean isEmptyCommand(String content) {
		return content.trim().equals("");
	}

	/**
	 * This method gets the number of the task.
	 * 
	 * @param content
	 * @return integer
	 * @throws NumberFormatException
	 * @author G. Vishnu Priya
	 */
	public static int getTaskNum(String content) throws NumberFormatException {
		return Integer.parseInt(content);
	}

	private void addTask(String content) throws Exception {
			if (isEmptyCommand(content)) {
				throw new Exception("Please specify what to add.");
			} else {
				TaskClass task = processUserInput(content);
				this.tasks.add(task);
			}
	}

	/**
	 * Process user input for add task
	 * 
	 * @author Luo Shaohuai
	 * @param content
	 * @return Task Object
	 */
	private Task processUserInput(String content) {
		boolean priority = false;
		if (content.contains("!")) {
			priority = true;
		}

		String words[] = content.split(" ");
		content = "";
		Date date = null;
		Date timeStart = null;
		Date timeEnd = null;
		for (int i = 0; i < words.length; i++) {
			String word = words[i].trim();

			String format = determineDateFormat(word);
			if (format != null) {
				SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
				try {
					date = dateFormat.parse(word);
					continue;
				} catch (ParseException e) {
					// do nothing
				}
			}

			format = determineTimeFormat(word);
			if (format != null) {
				SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
				try {
					Date time = dateFormat.parse(word);
					if (timeEnd == null) {
						timeStart = time;
						int timeStartPos = i;
						int timeEndPos = timeStartPos;

						while (timeEndPos < timeStartPos + 3) {
							timeEndPos++;
							format = determineTimeFormat(words[timeEndPos]
									.trim());
							if (format != null) {
								dateFormat = new SimpleDateFormat(format);
								dateFormat.setTimeZone(TimeZone
										.getTimeZone("UTC"));
								timeEnd = dateFormat.parse(words[timeEndPos]);
								i = timeEndPos;
								break;
							}
						}
					}
					if (timeEnd != null) {
						continue;
					}
				} catch (ParseException e) {
					// do nothing
				}
			}

			content += word + " ";
		}

		if (date == null) {
			return new FloatingTask(priority, content);
		} else if (timeEnd == null) {
			return new DeadlineTask(priority, content, date);
		} else {
			timeStart = addDate(date, timeStart);
			timeEnd = addDate(date, timeEnd);
			return new TimedTask(priority, content, timeStart, timeEnd);
		}
	}

	private Date addDate(Date date1, Date date2) {
		long ms = date1.getTime() + date2.getTime();
		return new Date(ms);
	}

	/**
	 * Determine SimpleDateFormat pattern matching with the given date string.
	 * Returns null if format is unknown.
	 * 
	 * @author Retrieved from
	 *         http://stackoverflow.com/questions/3389348/parse-any-date-in-java
	 *         and modified by Luo Shaohuai
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

	/**
	 * Determine SimpleDateFormat pattern matching with the given date string.
	 * Returns null if format is unknown.
	 * 
	 * @author Luo Shaohuai
	 * @param dateString
	 *            The date string to determine the SimpleDateFormat pattern for.
	 * @return The matching SimpleDateFormat pattern, or null if format is
	 *         unknown.
	 */
	private static String determineTimeFormat(String dateString) {
		for (String regexp : TIME_FORMAT_REGEXPS.keySet()) {
			if (dateString.toLowerCase().matches(regexp)) {
				return TIME_FORMAT_REGEXPS.get(regexp);
			}
		}
		return null; // Unknown format.
	}

	/**
	 * This method removes the command, either add, delete, edit or display,
	 * from the command string.
	 * 
	 * @param command
	 * @param operation
	 * @return String
	 * @author G. Vishnu Priya
	 */
	private String removeCommandType(String command, String operation) {
		return command.replace(operation, "").trim();
	}

	/**
	 * This method returns the command type for each operation.
	 * 
	 * @param operation
	 * @return CommandType
	 * @author G. Vishnu Priya
	 */
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
			return CommandType.SEARCH;
		}
	}
}
