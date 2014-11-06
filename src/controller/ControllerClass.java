package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import storage.Storage;
import storage.StoragePlus;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import controller.Task.TaskType;

/*
 * This is the class for the Controller, which serves as the component for logic in the software.
 * It is called by the UI component, processes the user inputs and sends necessary information to the storage to be stored.
 * 
 */

/**
 * ControllerClass which implements the Controller interface. 
 */
//@author
public class ControllerClass implements Controller {

	public static final String CMD_ADD = "add";
	public static final String CMD_DELETE = "delete";
	public static final String CMD_EDIT = "edit";
	public static final String CMD_LIST1 = "list";
	public static final String CMD_LIST2 = "display";
	public static final String CMD_UNDO = "undo";
	public static final String CMD_DONE = "done";
	public static final String CMD_POSTPONE = "pp";
	public static final String CMD_ARCHIVE = "archive";
	public static final String CMD_OVERDUE = "overdue";
	public static final String CMD_PAGE = "page";
	public static final String CMD_FREE = "find";
	public static final String CMD_CLEARARCHIVE = "clear";

	enum CommandType {
		ADD, DELETE, EDIT, POSTPONE, DISPLAY, UNDO, ARCHIVE, SEARCH, DONE, CHANGEPAGE, OVERDUE, FREETIME, CLEARARCHIVE
	};

	enum DisplayList {
		MAIN, ARCHIVE, SEARCH, FREESLOTS
	};

	public static final Map<String, CommandType> commandMap;

	static {
		Map<String, CommandType> aMap = new HashMap<>();
		aMap.put(CMD_ADD, CommandType.ADD);
		aMap.put(CMD_DELETE, CommandType.DELETE);
		aMap.put(CMD_EDIT, CommandType.EDIT);
		aMap.put(CMD_POSTPONE, CommandType.POSTPONE);
		aMap.put(CMD_LIST1, CommandType.DISPLAY);
		aMap.put(CMD_LIST2, CommandType.DISPLAY);
		aMap.put(CMD_UNDO, CommandType.UNDO);
		aMap.put(CMD_ARCHIVE, CommandType.ARCHIVE);
		aMap.put(CMD_DONE, CommandType.DONE);
		aMap.put(CMD_PAGE, CommandType.CHANGEPAGE);
		aMap.put(CMD_OVERDUE, CommandType.OVERDUE);
		aMap.put(CMD_FREE, CommandType.FREETIME);
		aMap.put(CMD_CLEARARCHIVE, CommandType.CLEARARCHIVE);
		commandMap = Collections.unmodifiableMap(aMap);
	}

	private static final int POSITION_OF_OPERATION = 0;
	private static final int maxNumOfUndo = 40;
	private static final int numTasksInSinglePage = 10;

	private static Controller theController = null;
	private TaskList tasks;
	private TaskList archiveTasks;
	private TaskList resultTasks;
	private List<String> freeSlots;
	

	private DisplayList displayListType;
	private Integer recentChange;
	private Integer currentPageNum;

	private Storage storage;
	private FixedSizeStack<TaskList> undoList;
	private FixedSizeStack<TaskList> undoArchiveList;

	private String feedbackMessage = "";

	/**
	 * Constructs the Controller Class object.
	 */
	//@author
	private ControllerClass() {
		storage = createStorageObject();
		undoList = new FixedSizeStack<TaskList>(maxNumOfUndo);
		undoArchiveList = new FixedSizeStack<TaskList>(maxNumOfUndo);

		getFileContent();
		setNumTaskOnPage(numTasksInSinglePage);
		displayListType = DisplayList.MAIN;
		resetRecentChange();
	}

	/**
	 * Executes command entered by user.
	 * It first retrieves all existing tasks stored in Storage, then proceeds on executing the command specified by user
	 * 
	 * @param command	Input command from user.
	 * @return			Task position of a page on the current list.
	 * @throws			Exception 	If command entered by user is invalid.	
	 */
	//@author
	public Integer execCmd(String command) throws Exception {
		getFileContent();
		setNumTaskOnPage(numTasksInSinglePage);
		parseCommand(command);
		updateStorage();
		return recentChange;
	}

	/**
	 * Gets feedback message after a command is executed.
	 * 
	 * @return	Stringed feedback message.
	 */
	//@author
	public String getFeedback() {
		return feedbackMessage;
	}

	/**
	 * Sets feedback message.
	 * 
	 * @param feedback	Feedback message after a command is executed.
	 */
	private void setFeedback(String feedback) {
		feedbackMessage = feedback;
	}

	/**
	 * Gets the current list based on the list that user is currently viewing.
	 * If user is viewing free slots, no page system is implemented as the displayed results for free slots will always be at most 10.
	 * 
	 * @return	List of stringed tasks.
	 */
	//@author
	public List<String> getCurrentList() {
		List<String> list = null;
		switch (displayListType) {
		case MAIN:
			list = tasks.getNumberedPage(currentPageNum);
			if (list.isEmpty()) {
				list.add("**No task in the main list**");
			}
			break;

		case ARCHIVE:
			list = archiveTasks.getNumberedPage(currentPageNum);
			if (list.isEmpty()) {
				list.add("**No task in the archive list**");
			}
			break;

		case SEARCH:
			list = resultTasks.getPage(currentPageNum);
			if (list.isEmpty()) {
				list.add("**No search result**");
			}
			break;
			
		case FREESLOTS:
			if(freeSlots.size() < 10) {
				list = freeSlots;
			} else {
				list = freeSlots.subList(0, 10);
			}
			break;
		}

		return list;
	}

	/**
	 * Generates a list of stringed commands and possible words found in description of tasks to suggest to user.
	 *  
	 * @param content	Input from user.
	 * @return			List of suggested stringed commands and words.
	 */
	//@author
	public List<String> suggest(String content) {
		List<String> suggestList = new ArrayList<String>();

		if (content == null || content.isEmpty()) {
			return suggestList;
		}
		// suggest commands
		for (String str : commandMap.keySet()) {
			if (str.indexOf(content) == 0) {
				suggestList.add(str);
			}
		}

		// suggest search
		TaskList resultList = tasks.searchDesc(content, tasks);

		suggestList.addAll(resultList.getStringList());

		return suggestList;
	}

	/**
	 * Sets the number of tasks on a page.
	 * If number of tasks > 10, set only 10 tasks on a page.
	 * 
	 * @param number	Number of tasks on a page (10).
	 */
	//@author
	private void setNumTaskOnPage(Integer number) {
		tasks.setNumTaskOnPage(number);
		archiveTasks.setNumTaskOnPage(number);
	}

	/**
	 * Reads stored tasks in Storage into task list and archive list.
	 */
	//@author G. Vishnu Priya
	private void getFileContent() {
		tasks = new SimpleTaskList(storage.read());
		archiveTasks = new SimpleTaskList(storage.readArchive());
	}

	/**
	 * Creates a Storage object.
	 * 
	 * @return	A Storage object.
	 */
	//@author G. Vishnu Priya
	private Storage createStorageObject() {
		return new StoragePlus();
	}

	/**
	 * Updates the current list type to be displayed to user.
	 * 
	 * @param listType	Type of displayed list.
	 */
	//@author
	private void setDisplayList(DisplayList listType) {
		this.displayListType = listType;
		resetRecentChange();
	}

	/**
	 * Sets the current displayed list to a list of searched results.
	 * 
	 * @param list	A list of searched results.
	 */
	//@author
	private void setResultList(TaskList list) {
		this.resultTasks = list;
		resultTasks.setNumTaskOnPage(numTasksInSinglePage);
		resetRecentChange();
		setDisplayList(DisplayList.SEARCH);
	}

	/**
	 * Resets recent change to page 1 when changing to another displayed list type.
	 */
	//@author
	private void resetRecentChange() {
		currentPageNum = 1;
		recentChange = 0;
	}

	/**
	 * Sets recent change and sorts the current displayed list when a task's position is changed.
	 * 
	 * @param task		A Task object whose position is changed after a command is executed.
	 * @param taskList	Current displayed list.
	 */
	//@author
	private void setRecentChange(Task task, TaskList taskList) {
		taskList.sort();
		Integer index = taskList.indexOf(task);
		setRecentChange(index, taskList);
	}

	/**
	 * Gets the current page number that the task is on and its new task number on the displayed list.
	 * 
	 * @param recent	Index of task in the displayed list.
	 * @param taskList	Current displayed list.
	 */
	//@author
	private void setRecentChange(Integer recent, TaskList taskList) {
		currentPageNum = taskList.getIndexPageContainTask(recent);
		recentChange = taskList.getIndexTaskOnPage(recent);
	}

	/**
	 * Parses user input by getting the command type of the user input and executing the command.
	 * 
	 * @param command	User input.
	 * @throws			Exception	If user enters an invalid command.
	 */
	//@author
	private void parseCommand(String command) throws Exception {
		String operation = getOperation(command);
		CommandType commandType = matchCommandType(operation);
		if (commandType == CommandType.SEARCH) {
			processInput(commandType, command);
		} else {
			String content = removeCommandType(command, operation);
			processInput(commandType, content);
		}
		updateStorage();
	}

	/**
	 * Gets the command specified by user input.
	 * 
	 * @param command	User input.
	 * @return			Command specified by user.
	 */
	//@author
	private String getOperation(String command) {
		String[] splitCommandIntoWords = command.split(" ");
		String operation = splitCommandIntoWords[POSITION_OF_OPERATION];
		return operation;
	}

	/**
	 * Chooses which course of action to take according to the command type.
	 *
	 * @param commandType	Type of command.
	 * @param content		Content of user input besides the command specified.
	 * @throws Exception	If an invalid command is entered.
	 */
	//@author G. Vishnu Priya
	private void processInput(CommandType commandType, String content)
			throws Exception {
		switch (commandType) {
		case ADD:
			updateForUndo();
			addTask(content);
			break;
		case DELETE:
			updateForUndo();
			deleteTask(content);
			break;
		case EDIT:
			updateForUndo();
			editTask(content);
			break;
		case UNDO:
			undo();
			break;
		case SEARCH:
			search(content);
			break;
		case DISPLAY:
			displayMainList();
			break;
		case ARCHIVE:
			moveToArchive();
			break;
		case OVERDUE:
			overDue();
			break;
		case POSTPONE:
			updateForUndo();
			postpone(content);
			break;
		case DONE:
			updateForUndo();
			markAsDone(content);
			break;
		case FREETIME:
			TestfreeIntervals();
			break;
		case CHANGEPAGE:
			changePage(content);
			break;
		case CLEARARCHIVE:
			updateForUndo();
			clearArchive();
			break;
		default:
			throw new Exception("Invalid command.");
		}
	}
		
	/**
	 * Clears all the tasks in the archive.
	 * 
	 * @throws	Exception	If the user clears the list on other types of lists besides archive list.
	 */
	//@author G. Vishnu Priya
	private void clearArchive() throws Exception {
		if(displayListType == DisplayList.ARCHIVE) {
			archiveTasks.clear();
		} else {
			throw new Exception("Only can clear tasks on archive list.");
		}
	}

	/**
	 * Changes current displayed list to archive list.
	 */
	//@author
	private void moveToArchive() {
		setDisplayList(DisplayList.ARCHIVE);
		setFeedback("Archive List.");
	}

	// the format will be "done <number>"
	private void markAsDone(String content) throws Exception {
		String[] taskNumbers = content.trim().split("\\s+");
		Arrays.sort(taskNumbers, new Comparator<String>() {
			public int compare(String first, String second) {
				return Integer.valueOf(second)
						.compareTo(Integer.valueOf(first));
			}
		});

		try {
			for (int i = 0; i < taskNumbers.length; i++) {
				int taskID = Integer.parseInt(taskNumbers[i].trim()) - 1;
				// move task from task List to archive
				if (taskID >= 0 && taskID < tasks.size()) {
					Task task = tasks.get(taskID);
					archiveTasks.add(0, task);
					tasks.remove(taskID);
				} else {
					throw new Exception("Invalid arguments");
				}
			}

			if (taskNumbers.length == 1) {
				setFeedback("Task is marked as done successfully.");
			} else {
				setFeedback(taskNumbers.length
						+ " tasks are marked as done successfully.");
			}
		} catch (NumberFormatException e) {
			throw new Exception("Invalid format. Please enter the task number!");
		}
		tasks.sort();
	}

	
	
	//format of find
	//	**Find for a time to time period to be free.Ex: find 10pm to 1pm date
	//	**Find intervals of free time that in the schedule.Ex: find 3 hours 30 mins by date(before a date) or
	//find 3 hours 30 mins on date (check on that date)
	
	private Pair parserForFind(String content) throws Exception {

		if (content.indexOf("to") == -1) {
			String[] para = content.trim().split("\\s+");
			int len = para.length;
			if (len == 4) {
				int hh = Integer.parseInt(para[0]);
				int mm = Integer.parseInt(para[2]);
				int num = hh * 6 + (int) Math.ceil(mm / 10);

				return new Pair(-1, num);
			} else if (len == 2) {
				try {
					if (para[1].equalsIgnoreCase("hours")
							|| para[1].equalsIgnoreCase("hour")) {
						int hh = Integer.parseInt(para[0]);
						return new Pair(-1, hh * 6);
					} else if (para[1].equalsIgnoreCase("minutes")
							|| para[1].equalsIgnoreCase("minutes")
							|| para[1].equalsIgnoreCase("mins")
							|| para[1].equalsIgnoreCase("min")) {

						int mm = Integer.parseInt(para[0]);
						return new Pair(-1, (int) Math.ceil(mm / 10));
					}
				} catch (NumberFormatException e) {
					throw new Exception("Please specify time!");
				}
			} else {
				throw new Exception("Please specify time");
			}

		} else {

			String[] para = content.trim().split("to");

			if (para.length != 2) {
				throw new Exception("Please specify the period of time!");
			} else {
				Date date1 = parserForFindTime(para[0]);
				Date date2 = parserForFindTime(para[1]);

				if (date1 == null || date2 == null || date1.after(date2)) {
					throw new Exception("Please specify the period of time!");
				}

				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(date1);

				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(date2);

				int hh1 = cal1.get(Calendar.HOUR_OF_DAY);
				int mm1 = cal1.get(Calendar.MINUTE);

				int hh2 = cal2.get(Calendar.HOUR_OF_DAY);
				int mm2 = cal2.get(Calendar.MINUTE);

				return new Pair(hh1 * 6 + mm1 / 10, hh2 * 6
						+ (int) Math.ceil(mm2 / 10));
			}
		}
		return null;
	}

	// return the date time
	// return null if no date time is recognized
	private Date parserForFindTime(String input) {

		Parser parser = new Parser();

		List<DateGroup> groups = parser.parse(input);
		List<Date> dates = new ArrayList<Date>();
		for (DateGroup group : groups) {
			dates.addAll(group.getDates());
		}

		if (dates.size() == 1) {
			return dates.get(0);
		} else {
			return null;
		}
	}
	
	
	//just for testing, can ignore
	private void TestfreeIntervals(){

		Date now=new Date();
		Date endTime=new Date(now.getTime()+24*60*60*1000*7);
	
		ArrayList<longPair> intervalFree=freeIntervals(now,endTime);
		
		for (int i=0;i<intervalFree.size();i++){
			longPair inter=intervalFree.get(i);
			Date start=new Date(inter.getFirst());
			Date end=new Date(inter.getSecond());
			
			System.out.println("[ "+start+"to "+end+" ]");
		}
	}

	private ArrayList<longPair> freeIntervals(Date start, Date end) {

		ArrayList<longPair> occupiedIntervals = getOccupied(tasks);
		ArrayList<longPair> intervalFree = new ArrayList<longPair>();

		// initialize
		intervalFree.add(new longPair(start.getTime(), end.getTime()));
	
		for (int i = 0; i < occupiedIntervals.size() && i<10; i++) {
			longPair occu = occupiedIntervals.get(i);

			for (int j=0;j<intervalFree.size();j++){
				longPair free=intervalFree.get(j);
				if (hasOverLap(occu,free)){
					//case 1: occu > free, update
					if (occu.getFirst() > free.getFirst() && occu.getSecond() > free.getSecond()){
						intervalFree.set(j, new longPair(free.getFirst(), occu.getFirst()));
					}//cas2 2: occu< free, update
					else if (occu.getFirst() <free.getFirst() && occu.getSecond()< free.getSecond()){
						intervalFree.set(j, new longPair(occu.getSecond(),free.getSecond()));
					}//case 3:occu covers free, remove
					else if (occu.getFirst()< free.getFirst() && occu.getSecond() > free.getSecond()){
						intervalFree.remove(j);
						j--;
					}//case 4: occu is covered by free, add 2 time slots
					else if (occu.getFirst()> free.getFirst() && occu.getSecond()< free.getSecond()){
						intervalFree.set(j,new longPair(free.getFirst(),occu.getFirst()));
						intervalFree.add(j,new longPair(occu.getSecond(),free.getSecond()));
						j++;
					}				
				}						
			}
			Collections.sort(intervalFree);
		}
		return intervalFree;

	}
	
	//Tran Cong Thien
	//get the occupied slots of time
	private ArrayList<longPair> getOccupied(TaskList listToSearch) {
		int numOfTask = listToSearch.size();

		ArrayList<longPair> resultList = new ArrayList<longPair>();

		for (int i = 0; i < numOfTask; i++) {
			Task task = listToSearch.get(i);

			if (task.getType() == TaskType.TIMED) {
				resultList.add(new longPair(task.getStartTime().getTime(), task
						.getEndTime().getTime()));
			}
		}
		// sort in order first
		Collections.sort(resultList);
		return resultList;

	}

	
	// if 2 intervals is overlapped
	private boolean hasOverLap(longPair pair1, longPair pair2) {

		if (Math.max(pair1.getFirst(), pair2.getFirst()) < Math.min(
				pair1.getSecond(), pair2.getSecond())) {
			return true;
		} else {
			return false;
		}
	}



	//Tran Cong Thien
	private void search(String content) {
		TaskList resultList = tasks.search(content);
		setResultList(resultList);

		if (resultList.size() == 0 || resultList.size() == 1) {
			setFeedback(resultList.size() + " search result.");
		} else {
			setFeedback(resultList.size() + " search results.");
		}
	}

	// return the list of tasks that are overdue at current time
	// Author: Tran Cong Thien
	private void overDue() {
		setResultList(tasks.getOverdueTasks());
	}

	//Tran Cong Thien
	private void updateForUndo() {
		updateUndoList();
		updateUndoArchiveList();
	}

	//Tran Cong Thien
	private void updateUndoArchiveList() {
		undoArchiveList.push(archiveTasks.clone());
	}

	// push the current state to the undoList
	// Tran Cong Thien
	private void updateUndoList() {
		undoList.push(tasks.clone());
	}

	// undo command, the tasks will be replaced by the previous state
	// Tran Cong Thien
	private void undo() {
		// if there is states to undo
		if (!undoList.empty()) {
			tasks = undoList.pop();
			archiveTasks = undoArchiveList.pop();
			setFeedback("Undo is successful");
		}
	}

	/*
	 * Postpones the desired task.
	 * 
	 * @author Koh Xian Hui
	 */
	private void postpone(String content) throws Exception {
		try {
			if (displayListType == DisplayList.ARCHIVE) {
				throw new Exception(
						"Postpone can only be done in main list or search list.");
			} else {
				String[] taskNumbers = content.split(" ");

				for (int i = 0; i < taskNumbers.length; i++) {
					Integer taskNum = Integer.parseInt(taskNumbers[i]) - 1;
					Task postponedTask = tasks.get(taskNum);
					postponedTask.clearTimes();
					postponedTask.setType(TaskType.FLOATING);
					
					if(i == taskNumbers.length - 1) {
						if(displayListType == DisplayList.MAIN) {
							setRecentChange(postponedTask, tasks);
						} else {
							setRecentChange(postponedTask, resultTasks);
						}
					}
				}

				tasks.sort();

				if (taskNumbers.length == 1) {
					setFeedback("Task is postponed successfully.");
				} else {
					setFeedback(taskNumbers.length
							+ " tasks are postponed successfully.");
				}
			}
		} catch (NumberFormatException e) {
			throw new Exception(
					"Invalid postpone format. Please enter task number.");
		}
	}

	/*
	 * Displays the existing tasks to the user.
	 * 
	 * @return ArrayList<String>
	 * 
	 * @author Koh Xian Hui
	 */
	private void displayMainList() {
		// getFileContent();
		// setNumTaskOnPage(numTasksInSinglePage);
		tasks.sort();
		setDisplayList(DisplayList.MAIN);
		setFeedback("Main List.");
	}

	/**
	 * Checks if the list the user is on is the main list. IF it is the main
	 * list, deems the edit valid and proceeds with it.
	 * 
	 * @param content
	 * @return void
	 * @author G. Vishnu Priya
	 * @throws Exception
	 */
	private void editTask(String content) throws Exception {

		if ((displayListType==DisplayList.MAIN) || (displayListType == DisplayList.SEARCH) ) {
			validEdit(content); 
		} else {
			throw new Exception("Editing can only be done on the main list or search list.");
		}

	}

	/**
	 * Checks if the user has specified any task to edit and if specified,
	 * proceeds with the edit.
	 * 
	 * @param content
	 * @throws Exception
	 * @return
	 * @author G. Vishnu Priya
	 */
	private void validEdit(String content) throws Exception {
		if (tasks.isEmpty()) {
			throw new Exception("Nothing to edit list is empty.");

		} if (isEmptyCommand(content)) {
		throw new Exception("Please specify what to edit.");
} else {
		Task taskEdited = proceedWithEdit(content);
		tasks.sort();
		setDisplayList(displayListType);
		setRecentChange(taskEdited, tasks);
		
}

	}

	/**
	 * Edits the task using the specified content.
	 * 
	 * @param content
	 * @return void
	 * @author G. Vishnu Priya
	 * @throws Exception
	 */
private Task proceedWithEdit(String content) throws Exception {
	try {
		String[] words = content.split(" ");

	int positionOfTask = Integer.parseInt(words[0]) - 1;
	
	if (positionOfTask < 0 || positionOfTask >= tasks.size()
			|| words.length < 2) {
		throw new Exception("Invalid edit format.");
	}

	String attributeToChange = words[1];
	Task taskToEdit = tasks.get(positionOfTask);

	if (isMultipleEditPriority(attributeToChange)) {
		for (int i = 0; i < words.length - 1; i++) {
			Task task = tasks.get(Integer.parseInt(words[i]) - 1);
			editPriority(task);
			setFeedback(words.length - 1 + " tasks are edited successfully.");
			if (i == words.length - 2) {
				setRecentChange(task, tasks);
			}
		}
		
	} else {

		String editDetails = "";
		if ((!attributeToChange.equals("!")) && (words.length==2)) {
			throw new Exception("Please specify details to edit.");
		}
		for (int i = 2; i < words.length; i++) {
			editDetails += words[i] + " ";
		}
		if (!editDetails.isEmpty()) {
			editDetails = editDetails
					.substring(0, editDetails.length() - 1);
		}

		editAttribute(taskToEdit, attributeToChange, editDetails);
		setFeedback("Task is edited successfully.");
		
	}
	
	return taskToEdit;
	} catch (NumberFormatException e) {
		throw new Exception("Invalid edit format.");
	}
}

	/**
	 * Checks if the user wants to edit priority of multiple tasks.
	 * 
	 * @param attributeToChange
	 * @return boolean
	 * @author G. Vishnu Priya
	 */
	private boolean isMultipleEditPriority(String attributeToChange) {
		try {
			Integer.parseInt(attributeToChange);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * Matches the attribute to be edited and calls the relevant function.
	 * 
	 * @param taskToEdit
	 * @param attribute
	 * @param editDetails
	 * @return void
	 * @author G. Vishnu Priya
	 * @throws Exception
	 */
	private void editAttribute(Task taskToEdit, String attribute,
			String editDetails) throws Exception {
		if (attribute.equalsIgnoreCase("desc")) {
			editDescription(taskToEdit, editDetails);
		} else if (attribute.equalsIgnoreCase("time")) {
			processTime(taskToEdit, editDetails);
		} else if (attribute.equalsIgnoreCase("!")) {
			editPriority(taskToEdit);
		} else {
			throw new Exception("Please specify what to edit (time/desc/!)");
		}
	}

	/**
	 * This methods edits the priority of a task by reversing the existing
	 * priority of the task.
	 * 
	 * @return void
	 * @author G. Vishnu Priya
	 */
	private void editPriority(Task taskToEdit) {
		boolean priorityOfTask = taskToEdit.isPrioritized();
		if (priorityOfTask) {
			taskToEdit.setPriority("false");
		} else {
			taskToEdit.setPriority("true");
		}
	}

	/**
	 * Replaces the description of task with the desc string.
	 * 
	 * @param taskToEdit
	 * @param details
	 * @return Task Object
	 * @author G. Vishnu Priya
	 */
	private void editDescription(Task taskToEdit, String desc) {
		if (desc != null) {
			taskToEdit.setDesc(desc);
		}
	}

	/**
	 * This method checks if the task to be deleted exists or is specified and
	 * if it is valid, proceeds with deletion.
	 * 
	 * @param content
	 * @return void
	 * @author G. Vishnu Priya
	 */
	private void deleteTask(String content) throws Exception {

		if ((displayListType == DisplayList.MAIN) || (displayListType == DisplayList.SEARCH) ) {
		if (isValidDelete(content)) {
			proceedWithDelete(content);
		} 
		} else {
			throw new Exception("Deletion can only be done in main list or search list.");
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

			String[] taskNumbers = content.split(" ");
			List<Integer> taskNumDescending = new ArrayList<Integer>();
			for (int i = 0; i < taskNumbers.length; i++) {
				int taskNum = Integer.parseInt(taskNumbers[i]);
				taskNumDescending.add(taskNum);
			}
			Collections.sort(taskNumDescending, Collections.reverseOrder());

			for (int i = 0; i < taskNumDescending.size(); i++) {
				int taskNum = taskNumDescending.get(i);
				executeDelete(taskNum);
				taskNum -= 1;
			}
			tasks.sort();

			if (taskNumDescending.size() == 1) {
				setFeedback("Task is successfully deleted.");
			} else {
				setFeedback(taskNumDescending.size()
						+ " tasks are successfully deleted.");
			}
		} catch (NumberFormatException e) {
			throw new Exception(
					"Invalid delete format. Please enter task number.");
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
			throw new Exception(
					"Task does not exist. Please enter task numbers within the range.");
		}
	}

	/**
	 * This method changes the attributes related to the changing of a page.
	 * 
	 * @return void
	 * @author G. Vishnu Priya
	 * @throws Exception
	 */
	private void changePage(String content) throws Exception {
		String direction = content.trim();
		changeCurrentPageNum(direction);
	}

	/**
	 * This method checks if it is valid to change the current page number and
	 * if so, changes the current page number.
	 * 
	 * @throws Exception
	 * @return void
	 * @author G. Vishnu Priya
	 */
	private void changeCurrentPageNum(String direction) throws Exception {
		if (direction.equalsIgnoreCase("up")) {
			if (checkValidPageUp()) {
				currentPageNum--;
				recentChange = 0;
				setFeedback("Page " + currentPageNum + " out of "
						+ getTotalNumOfPages(displayListType));
			} else {
				throw new Exception("On first page.");
			}
		} else if (direction.equalsIgnoreCase("down")) {
			if (checkValidPageDown()) {
				currentPageNum++;
				recentChange = 0;
				setFeedback("Page " + currentPageNum + " out of "
						+ getTotalNumOfPages(displayListType));
			} else {
				throw new Exception("On last page.");
			}
		} else {
			throw new Exception("Page up/down");
		}
	}

	/**
	 * This method checks if it is possible to go to the next page by checking
	 * if there are more tasks in the list which are pushed to the next page.
	 * 
	 * @return boolean
	 * @author G. Vishnu Priya
	 */
	private boolean checkValidPageDown() {
		Integer totalNumPages;
		// TODO: getCurDisplayList() after fix search
		totalNumPages = getTotalNumOfPages(displayListType);
		if (currentPageNum < totalNumPages) {
			return true;
		}
		return false;
	}

	private int getTotalNumOfPages(DisplayList displayListType) {
		int totalNumPages;

		switch (displayListType) {
		case MAIN:
			totalNumPages = tasks.getTotalPageNum();
			break;
		case ARCHIVE:
			totalNumPages = archiveTasks.getTotalPageNum();
			break;
		case SEARCH:
			totalNumPages = resultTasks.getTotalPageNum();
		default:
			totalNumPages = 0;
			break;
		}

		return totalNumPages;
	}

	/**
	 * This method checks if it is possible to go to the previous page. If
	 * currently on first page, it will return false. Otherwise, it will return
	 * true.
	 * 
	 * @return boolean
	 * @author G. Vishnu Priya
	 */
	private boolean checkValidPageUp() {
		if (currentPageNum <= 1) {
			return false;
		}

		return true;
	}

	/**
	 * This method updates the content stored.
	 * 
	 * @return void
	 * @author G. Vishnu Priya
	 */
	private void updateStorage() {

		storage.write(tasks.getStringList());
		storage.writeArchive(archiveTasks.getStringList());
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
	 * @author Luo Shaohuai
	 * @param content
	 * @throws Exception
	 */
	private void addTask(String content) throws Exception {
		if (isEmptyCommand(content)) {
			throw new Exception("Please specify what to add.");
		}

		Task task = processUserInput(content);
		this.tasks.add(task);

		setFeedback("Task is successfully added.");
		tasks.sort();
		setDisplayList(displayListType);
		setRecentChange(task, tasks);
	}

	/**
	 * @author Luo Shaohuai
	 * @param content
	 * @return
	 */
	private Task processUserInput(String content) {
		String desc = "";
		Integer singlePos = 0;
		Integer doublePos = 0;
		singlePos = content.indexOf('\'', 0);
		doublePos = content.indexOf('\"', 0);
		if (singlePos == -1 && doublePos == -1) {
			// return processUserInputClassic(content);
			desc = content + " ";
			content = "";
		}

		String regex = "([\"'])(?:(?=(\\\\?))\\2.)*?\\1";
		Matcher matcher = Pattern.compile(regex).matcher(content);
		while (matcher.find()) {
			desc += content.substring(matcher.start() + 1, matcher.end() - 1)
					+ " ";
		}
		desc = desc.substring(0, desc.length() - 1);
		content = content.replaceAll(regex, "");

		Task task = new TaskClass();
		Boolean priority;
		if (content.indexOf('!') != -1) {
			content = content.replaceAll("!", "");
			priority = true;
		} else {
			priority = false;
		}
		task.setPriority(priority.toString());

		task.setDesc(desc);
		task.setType(TaskType.FLOATING);
		processTime(task, content);

		return task;
	}

	private boolean processTime(Task task, String content) {
		content = content.trim();
		if (content.isEmpty()) {
			return false;
		}

		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(content);
		List<Date> dates = new ArrayList<Date>();
		for (DateGroup group : groups) {
			dates.addAll(group.getDates());
		}

		Collections.sort(dates);

		if (dates.size() < 1) {
			return false;
		} else if (dates.size() == 1) {
			task.clearTimes();
			task.setDeadline(dates.get(0));
			task.setType(TaskType.DEADLINE);
			return true;
		} else {
			task.clearTimes();
			task.setStartTime(dates.get(0));
			task.setEndTime(dates.get(dates.size() - 1));
			task.setType(TaskType.TIMED);
			return true;
		}
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
		return command.replaceFirst(operation, "").trim();
	}

	/**
	 * This method returns the command type for each operation.
	 * 
	 * @param operation
	 * @return CommandType
	 * @author G. Vishnu Priya
	 */
	private CommandType matchCommandType(String operation) {
		CommandType command = commandMap.get(operation.toLowerCase());
		if (command == null) {
			command = CommandType.SEARCH;
		}

		return command;
	}

	public static Controller getInstance() {
		if (theController == null) {
			theController = new ControllerClass();
		}

		return theController;
	}

}
