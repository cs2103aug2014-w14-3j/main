package controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import storage.Storage;
import storage.StoragePlus;
import ui.Config;

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
	public static final String CMD_LIST2 = "main";
	public static final String CMD_UNDO = "undo";
	public static final String CMD_DONE = "done";
	public static final String CMD_POSTPONE = "pp";
	public static final String CMD_ARCHIVE = "archive";
	public static final String CMD_OVERDUE = "overdue";
	public static final String CMD_PAGE = "page";
	public static final String CMD_FREE1 = "find";
	public static final String CMD_FREE2 = "search";
	public static final String CMD_CLEARARCHIVE = "clear";
	public static final String CMD_PENDING = "floating";
	public static final String CMD_EXIT = "exit";

	public static final String CMD_FORMAT_ADD = "To add a task: add \"[description]\" [times]";
	public static final String CMD_FORMAT_DELETE = "To delete tasks: delete [task numbers..]";
	public static final String CMD_FORMAT_EDIT = "To edit a task: edit desc/time/! [value] (! for priority)";
	public static final String CMD_FORMAT_DISPLAY = "To go to main list: list/main";
	public static final String CMD_FORMAT_UNDO = "To undo last operation: undo";
	public static final String CMD_FORMAT_DONE = "To move tasks to archive: done [task numbers..]";
	public static final String CMD_FORMAT_POSTPONE = "To postpone tasks: pp [task numbers..]";
	public static final String CMD_FORMAT_ARCHIVE = "To go to archive list: archive";
	public static final String CMD_FORMAT_OVERDUE = "To how overdue tasks: overdue";
	public static final String CMD_FORMAT_CHANGEPAGE = "To change page: page up/down or use page up/down on the keyboard";
	public static final String CMD_FORMAT_FREETIME = "To find free time: find [duration (e.g. 3 hours)]/[interval (e.g. 10am to 12am)] +[deadline]";
	public static final String CMD_FORMAT_CLEARARCHIVE = "To clear archive: clear";
	public static final String CMD_FORMAT_PENDING = "To show all floating tasks: pending";
	public static final String CMD_FORMAT_EXIT = "Quit: exit";

	private static final String MESSAGE_EMPTYLIST = "**No task in the %1$s**";
	private static final String MESSAGE_EMPTYSEARCHRESULT = "**No search result**";
	private static final String MESSAGE_TOMANYRESULT = "**Please be more specific to get more results**";
	private static final String MESSAGE_FEEDBACK_MAIN = "Main List.";
	private static final String MESSAGE_FEEDBACK_CLEAR = "Only can clear tasks on archive list.";
	private static final String MESSAGE_FEEDBACK_ARCHIVELIST = "Archive list.";
	private static final String MESSAGE_FEEDBACK_MAINLIST = "Main List.";
	private static final String MESSAGE_FEEDBACK_INVALID = "Invalid %1$s format.";
	private static final String MESSAGE_FEEDBACK_INVALID_EMPTYLIST = "Nothing to %1$s list is empty.";
	private static final String MESSAGE_FEEDBACK_DONE = "Task is marked as done successfully.";
	private static final String MESSAGE_FEEDBACK_DONE_MULTIPLE = " tasks are marked as done successfully.";
	private static final String MESSAGE_FEEDBACK_INVALID_NUMBERFORMAT = "Invalid format. Please enter the task number!";
	private static final String MESSAGE_FEEDBACK_INVALIDLIST = "%1$s can only be done on the main list or search list";
	private static final String MESSAGE_FEEDBACK_UNDO_MAXIMUM = "Reached maximum number of undo!";
	private static final String MESSAGE_FEEDBACK_UNDO = "Undo is successful";
	private static final String MESSAGE_FEEDBACK_POSTPONE = "Task is postponed successfully.";
	private static final String MESSAGE_FEEDBACK_POSTPONEMULTIPLE = " tasks are postponed successfully.";
	private static final String MESSAGE_FEEDBACK_EDIT = "Task is edited successfully.";
	private static final String MESSAGE_FEEDBACK_EDIT_MULTIPLE = " tasks are edited successfully.";
	private static final String MESSAGE_FEEDBACK_EDIT_SPECIFY = "Please specify what to edit (time/desc/!)";
	private static final String MESSAGE_FEEDBACK_EDIT_INVALID_NULLDETAILS = "Please specify details to edit.";
	private static final String MESSAGE_FEEDBACK_DELETE = "Task is successfully deleted.";
	private static final String MESSAGE_FEEDBACK_DELETE_MULTIPLE = " tasks are successfully deleted.";
	private static final String MESSAGE_FEEDBACK_ADD = "Task is successfully added.";
	private static final String MESSAGE_FEEDBACK_ADD_SPECIFY = "Please specify what to add.";
	private static final String MESSAGE_FEEDBACK_OUTOFRANGE = "Task does not exist. Please enter task numbers within the range.";
	private static final String MESSAGE_FEEDBACK_PAGE_FIRST = "On first page.";
	private static final String MESSAGE_FEEDBACK_PAGE_LAST = "On last page.";
	private static final String MESSAGE_FEEDBACK_PAGE_CONNECTOR = " out of ";
	private static final String MESSAGE_FEEDBACK_PAGE = "Page ";
	private static final String MESSAGE_FEEDBACK_PAGE_COMMAND = "Page up/down.";
	private static final String MESSAGE_FEEDBACK_ARCHIVE_CLEAR = "All tasks in archive are deleted.";
	private static final String MESSAGE_FEEDBACK_FREETIME_INVALID = "Please specify time!";
	private static final String MESSAGE_FEEDBACK_FREETIME_INVALIDPERIOD = "Please specify the period of time!";
	private static final String MESSAGE_FEEDBACK_AUTOCOMPLETE = "Press Tab to enter \"%1$s\"";
	private static final String MESSAGE_NO_SLOT_FOUND = "No slot found!";
	private static final String MESSAGE_ONE_SLOT_FOUND = " slot found!";
	private static final String MESSAGE_SLOTS_FOUND = " slots found!";
	private static final String MESSAGE_ONE_SEARCH_RESULT = " search result.";
	private static final String MESSAGE_SEARCH_RESULTS = " search results.";
	private static final String EDIT_ATTRIBUTE_DESC = "desc";
	private static final String EDIT_ATTRIBUTE_TIME = "time";
	private static final String EDIT_ATTRIBUTE_PRIORITY = "!";
	private static final String BOOLEAN_FALSE = "false";
	private static final String BOOLEAN_TRUE = "true";
	private static final String PAGE_DIRECTION_UP = "up";
	private static final String PAGE_DIRECTION_DOWN = "down";
	private static final String EMPTY_STRING = "";
	private static final String SPACE_STRING = " ";
	private static final String ONE_OR_MORE_SPACE="\\s+";
	private static final String OPENING="[ ";
	private static final String CLOSING=" ]";
	private static final String CONNECTOR_TO="  to  ";
	private static final String FREETIME_HOUR1 = "hours";
	private static final String FREETIME_HOUR2 = "hour";
	private static final String FREETIME_MINUTES1 = "minutes";
	private static final String FREETIME_MINUTES2 = "minute";
	private static final String FREETIME_MINUTES3 = "mins";
	private static final String FREETIME_MINUTES4 = "min";
	private static final String LOGGING_PURPOSE_METHODNAME_DELETE = "executeDelete";
	private static final String LOGGING_PURPOSE_METHODNAME_POSTPONE = "postpone";
	private static final Integer IGNORE_RECENTCHANGE = -1;
	private static final int MAX_NUM_OF_RESULTS = 10;
	private static final int MILISECOND_PER_DAY = 24 * 60 * 60 * 1000;
	private static final int MINUTE_PER_HOUR = 60;
	private static final int MILISECOND_PER_MINUTE = 1000 * 60;
	private static final int DAY_PER_MONTH = 30;
	private static final int MAX_HOUR_OF_DAY = 23;
	private static final int MAX_MINUTE_OR_SECOND = 59;
	private static final int NOT_INDEX = -1;

	enum CommandType {
		ADD, DELETE, EDIT, POSTPONE, DISPLAY, UNDO, ARCHIVE, SEARCH, DONE, CHANGEPAGE, OVERDUE, FREETIME, CLEARARCHIVE, PENDING, EXIT
	};

	enum DisplayList {
		MAIN, ARCHIVE, SEARCH, FREESLOTS
	};

	public static final Map<String, CommandType> commandMap;
	public static final Map<String, String> commandFormatMap;

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
		aMap.put(CMD_FREE1, CommandType.FREETIME);
		aMap.put(CMD_FREE2, CommandType.FREETIME);
		aMap.put(CMD_CLEARARCHIVE, CommandType.CLEARARCHIVE);
		aMap.put(CMD_PENDING, CommandType.PENDING);
		aMap.put(CMD_EXIT, CommandType.EXIT);
		commandMap = Collections.unmodifiableMap(aMap);

		Map<String, String> bMap = new HashMap<>();
		bMap.put(CMD_ADD, CMD_FORMAT_ADD);
		bMap.put(CMD_DELETE, CMD_FORMAT_DELETE);
		bMap.put(CMD_EDIT, CMD_FORMAT_EDIT);
		bMap.put(CMD_POSTPONE, CMD_FORMAT_POSTPONE);
		bMap.put(CMD_LIST1, CMD_FORMAT_DISPLAY);
		bMap.put(CMD_LIST2, CMD_FORMAT_DISPLAY);
		bMap.put(CMD_UNDO, CMD_FORMAT_UNDO);
		bMap.put(CMD_ARCHIVE, CMD_FORMAT_ARCHIVE);
		bMap.put(CMD_DONE, CMD_FORMAT_DONE);
		bMap.put(CMD_PAGE, CMD_FORMAT_CHANGEPAGE);
		bMap.put(CMD_OVERDUE, CMD_FORMAT_OVERDUE);
		bMap.put(CMD_FREE1, CMD_FORMAT_FREETIME);
		bMap.put(CMD_FREE2, CMD_FORMAT_FREETIME);
		bMap.put(CMD_CLEARARCHIVE, CMD_FORMAT_CLEARARCHIVE);
		bMap.put(CMD_PENDING, CMD_FORMAT_PENDING);
		bMap.put(CMD_EXIT, CMD_FORMAT_EXIT);
		commandFormatMap = Collections.unmodifiableMap(bMap);
	}

	private static final int POSITION_OF_OPERATION = 0;
	private static final int maxNumOfUndo = 40;
	private static final int numTasksInSinglePage = 10;

	private static final Logger logger = Logger.getLogger(ControllerClass.class
			.getName());
	private static Controller theController = null;
	private TaskList tasks;
	private TaskList archiveTasks;
	private TaskList resultTasks;
	private List<String> freeSlots;

	private DisplayList displayListType;
	private Integer recentChange;
	private Integer currentPageNum;
	private boolean onExit;

	private Storage storage;
	private FixedSizeStack<TaskList> undoList;
	private FixedSizeStack<TaskList> undoArchiveList;
	private String suggestFeedback;

	private String feedbackMessage;

	/**
	 * Constructs the Controller Class object.
	 */
	private ControllerClass() {
		onExit = false;
		storage = createStorageObject();
		undoList = new FixedSizeStack<TaskList>(maxNumOfUndo);
		undoArchiveList = new FixedSizeStack<TaskList>(maxNumOfUndo);

		getFileContent();
		setNumTaskOnPage(numTasksInSinglePage);
		displayListType = DisplayList.MAIN;
		resetRecentChange();

		feedbackMessage = EMPTY_STRING;
		suggestFeedback = null;
	}

	/**
	 * Executes command entered by user. It first retrieves all existing tasks
	 * stored in Storage, then proceeds on executing the command specified by
	 * user and updates the storage. It then returns the position of the task
	 * dealt with, on the page in the current list.
	 * 
	 * @param command
	 *            Input command from user.
	 * @return Task position on a page in the current list.
	 * @throws Exception
	 *             If command entered by user is invalid.
	 */
	//@author A0115194J
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
	 * @return Stringed feedback message.
	 */
	//@author
	public String getFeedback() {
		if (suggestFeedback != null) {
			return suggestFeedback;
		}
		return feedbackMessage;
	}

	public boolean isExiting() {
		return onExit;
	}

	/**
	 * Sets feedback message.
	 * 
	 * @param feedback
	 *            Feedback message after a command is executed.
	 */
	private void setFeedback(String feedback) {
		assert !feedback.trim().equals("");
		feedbackMessage = feedback;
	}

	/**
	 * Sets suggest feedback message. If no suggest feedback exist. Last
	 * feedback from command execution will be shown.
	 * 
	 * @param feedback
	 *            Feedback message after suggest is executed.
	 */
	private void setSuggestFeedback(String feedback) {
		suggestFeedback = feedback;
		
		//remove feedback of last operation
		if (displayListType == DisplayList.MAIN) {
			feedbackMessage = MESSAGE_FEEDBACK_MAIN;
		} 
	}

	/**
	 * Clear suggest feedback message. Last feedback from command execution will
	 * be shown.
	 * 
	 */
	private void clearSuggestFeedback() {
		suggestFeedback = null;
	}

	/**
	 * Gets the current list based on the list that user is currently viewing.
	 * If user is viewing free slots, no page system is implemented as the
	 * displayed results for free slots will always be at most 10.
	 * 
	 * @return List of stringed tasks.
	 */
	public List<String> getCurrentList() {
		List<String> list = null;
		switch (displayListType) {
		case MAIN:
			list = tasks.getNumberedPage(currentPageNum);
			if (list.isEmpty()) {
				list.add(String
						.format(MESSAGE_EMPTYLIST, MESSAGE_FEEDBACK_MAIN));
			}
			break;

		case ARCHIVE:
			list = archiveTasks.getNumberedPage(currentPageNum);
			if (list.isEmpty()) {
				list.add(String.format(MESSAGE_EMPTYLIST, CMD_ARCHIVE));
			}
			break;

		case SEARCH:
			list = resultTasks.getPage(currentPageNum);
			if (list.isEmpty()) {
				list.add(MESSAGE_EMPTYSEARCHRESULT);
			}
			break;

		case FREESLOTS:
			if (freeSlots.size() < numTasksInSinglePage) {
				list = freeSlots;
			} else {
				list = freeSlots.subList(0, numTasksInSinglePage);
			}
			break;
		}

		return list;
	}

	/**
	 * Generates the most possible stringed commands and possible words found in
	 * description of tasks to suggest to user.
	 * 
	 * @param content
	 *            Input from user.
	 * @return List of suggested stringed commands and words.
	 */
	public String suggest(String content) {
		String emptySuggest = "";
		String suggest;

		if (content == null || content.isEmpty()) {
			clearSuggestFeedback();
			return emptySuggest;
		}

		suggest = suggestCmd(content);
		if (suggest != null) {
			if (suggest.trim().equalsIgnoreCase(content.trim())) {
				setSuggestFeedback(commandFormatMap.get(suggest.trim()
						.toLowerCase()));
				return emptySuggest;
			}
			setSuggestFeedback(String.format(MESSAGE_FEEDBACK_AUTOCOMPLETE,
					suggest));
			return suggest;
		}

		suggest = suggestKeyword(content);
		if (suggest != null) {
			if (suggest.trim().equalsIgnoreCase(content.trim())) {
				clearSuggestFeedback();
				return emptySuggest;
			}
			setSuggestFeedback(String.format(MESSAGE_FEEDBACK_AUTOCOMPLETE,
					suggest));
			return suggest;
		}

		clearSuggestFeedback();
		return emptySuggest;
	}

	/**
	 * Suggest a command with prefix, return null if no such word
	 * 
	 * @param content
	 * @return String smallest string with prefix content
	 */
	private String suggestCmd(String content) {
		List<String> suggestList = new ArrayList<String>();
		// suggest commands
		for (String str : commandMap.keySet()) {
			if (str.indexOf(content.trim()) == 0) {
				suggestList.add(str);
			}
		}

		// return smallest from commands
		if (!suggestList.isEmpty()) {
			Collections.sort(suggestList);
			return suggestList.get(0);
		}

		return null;
	}

	/**
	 * Suggest a word with prefix, return null if no such word
	 * 
	 * @param content
	 * @return String smallest string with prefix content
	 */
	private String suggestKeyword(String content) {
		if (content.charAt(content.length() - 1) == ' ') {
			return null;
		}

		List<String> suggestList = new ArrayList<String>();
		// suggest words
		String[] words = content.split(" ");
		String key = words[words.length - 1];
		suggestList.addAll(tasks.suggestWord(key));
		suggestList.addAll(archiveTasks.suggestWord(key));

		// return smallest from results
		if (!suggestList.isEmpty()) {
			Collections.sort(suggestList);
			return suggestList.get(0);
		}

		return null;
	}

	/**
	 * Sets the number of tasks on a page. If number of tasks > 10, set only 10
	 * tasks on a page.
	 * 
	 * @param number
	 *            Number of tasks on a page (10).
	 */
	private void setNumTaskOnPage(Integer number) {
		tasks.setNumTaskOnPage(number);
		archiveTasks.setNumTaskOnPage(number);
	}

	/**
	 * Reads stored tasks in Storage into task list and archive list.
	 */
	//@author A0115194J
	private void getFileContent() {
		tasks = new SimpleTaskList(storage.read());
		archiveTasks = new SimpleTaskList(storage.readArchive());
	}

	/**
	 * Creates a Storage object.
	 * 
	 * @return A Storage object.
	 */
	private Storage createStorageObject() {
		return new StoragePlus();
	}

	/**
	 * Updates the current list type to be displayed to user.
	 * 
	 * @param listType
	 *            Type of displayed list.
	 */
	//@author
	private void setDisplayList(DisplayList listType) {
		this.displayListType = listType;
	}

	/**
	 * Sets the current displayed list to a list of searched results.
	 * 
	 * @param list
	 *            A list of searched results.
	 */
	private void setResultList(TaskList list) {
		this.resultTasks = list;
		resultTasks.setNumTaskOnPage(numTasksInSinglePage);
		resetRecentChange();
		setDisplayList(DisplayList.SEARCH);
	}

	/**
	 * Sets the current displayed list to a list of searched results.
	 * 
	 * @param list
	 *            A list of searched results.
	 */
	private void setFreeSlotList(List<String> list) {
		if (list.size() <= 10) {
			this.freeSlots = list;
		} else {
			this.freeSlots = list.subList(0, numTasksInSinglePage - 2);
			this.freeSlots.add(MESSAGE_TOMANYRESULT);
		}

		resetRecentChange();
		setDisplayList(DisplayList.FREESLOTS);
	}

	/**
	 * Resets recent change to page 1 when changing to another displayed list
	 * type.
	 */
	private void resetRecentChange() {
		currentPageNum = 1;
		recentChange = -1;
	}

	/**
	 * Sets recent change and sorts the current displayed list when a task's
	 * position is changed.
	 * 
	 * @param task
	 *            A Task object whose position is changed after a command is
	 *            executed.
	 * @param taskList
	 *            Current displayed list.
	 */
	private void setRecentChange(Task task, TaskList taskList) {
		taskList.sort();
		Integer index = taskList.indexOf(task);
		setRecentChange(index, taskList);
	}

	/**
	 * Gets the current page number that the task is on and its new task number
	 * on the displayed list.
	 * 
	 * @param recent
	 *            Index of task in the displayed list.
	 * @param taskList
	 *            Current displayed list.
	 */
	private void setRecentChange(Integer recent, TaskList taskList) {
		currentPageNum = taskList.getIndexPageContainTask(recent);
		recentChange = taskList.getIndexTaskOnPage(recent);
	}

	/**
	 * Set recent change as none
	 */
	private void clearRecentChange() {
		recentChange = IGNORE_RECENTCHANGE;
	}

	/**
	 * Parses user input by getting the command type of the user input and
	 * executing the command.
	 * 
	 * @param command
	 *            User input.
	 * @throws Exception
	 *             If user enters an invalid command.
	 */
	//@author A0115194J
	private void parseCommand(String command) throws Exception {
		String operation = getOperation(command);
		CommandType commandType = matchCommandType(operation);
		processInputBasedOnComandType(command, operation, commandType);
	}

	/**
	 * Based on the command type, goes on to execute the user command. If
	 * command type is search, takes a different approach from other command
	 * types.
	 * 
	 * @param command
	 *            User input.
	 * @param operation
	 *            The command type string.
	 * @param commandType
	 *            The command type.
	 * @throws Exception
	 *             If user enters an invalid command.
	 * 
	 */
	private void processInputBasedOnComandType(String command,
			String operation, CommandType commandType) throws Exception {
		if (commandType == CommandType.SEARCH) {
			processInput(commandType, command);
		} else {
			String content = removeCommandType(command, operation);
			processInput(commandType, content);
		}
	}

	/**
	 * Gets the command type specified by user.
	 * 
	 * @param command
	 *            User input.
	 * @return Command type string specified by user.
	 */
	private String getOperation(String command) {
		String[] splitCommandIntoWords = command.split(SPACE_STRING);
		String operation = splitCommandIntoWords[POSITION_OF_OPERATION];
		return operation;
	}

	/**
	 * Chooses which course of action to take according to the command type.
	 *
	 * @param commandType
	 *            Type of command.
	 * @param content
	 *            Content of user input besides the command specified.
	 * @throws Exception
	 *             If an invalid command is entered.
	 */
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
		case PENDING:
			pending();
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
			findFreeTime(content);
			break;
		case CHANGEPAGE:
			changePage(content);
			break;
		case CLEARARCHIVE:
			updateForUndo();
			clearArchive();
			break;
		case EXIT:
			onExit();
			break;
		default:
			assert false : commandType;
		}
	}

	/**
	 * Set the program to be on exiting
	 */
	//@author
	private void onExit() {
		onExit = true;
	}

	/**
	 * Clears all the tasks in the archive.
	 * 
	 * @throws Exception
	 *             If the user wishes to clear the list on other types of lists
	 *             besides archive list.
	 */
	//@author A0115194J
	private void clearArchive() throws Exception {
		if (displayListType == DisplayList.ARCHIVE) {
			archiveTasks.clear();
			setFeedback(MESSAGE_FEEDBACK_ARCHIVE_CLEAR);
		} else {
			setFeedback(MESSAGE_FEEDBACK_CLEAR);
			throw new Exception(MESSAGE_FEEDBACK_CLEAR);
		}
	}

	/**
	 * Changes current displayed list to archive list.
	 */
	//@author
	private void moveToArchive() {
		setDisplayList(DisplayList.ARCHIVE);
		setFeedback(MESSAGE_FEEDBACK_ARCHIVELIST);
	}


	/**
	 * Marks tasks as done.
	 * 
	 * @param content
	 *            Task numbers entered by user.
	 * @throws Exception
	 *             If task numbers entered are out of range.
	 * @throws NumberFormatException
	 *             If task numbers entered are not numbers.
	 */
	//@author A0112044B

	private void markAsDone(String content) throws Exception {
		String[] taskNumbers = content.trim().split(ONE_OR_MORE_SPACE);
		Arrays.sort(taskNumbers, new Comparator<String>() {
			public int compare(String first, String second) {
				return Integer.valueOf(second)
						.compareTo(Integer.valueOf(first));
			}
		});

		try {
			for (int i = 0; i < taskNumbers.length; i++) {
				int taskID = Integer.parseInt(taskNumbers[i].trim()) - 1;

				// move task from task List to archive list
				if (taskID >= 0 && taskID < tasks.size()) {
					Task task = tasks.get(taskID);
					archiveTasks.add(0, task);
					tasks.remove(taskID);
				} else {
					throw new Exception(String.format(MESSAGE_FEEDBACK_INVALID,
							CMD_DONE));
				}
			}

			if (taskNumbers.length == 1) {
				setFeedback(MESSAGE_FEEDBACK_DONE);
			} else {
				setFeedback(taskNumbers.length + MESSAGE_FEEDBACK_DONE_MULTIPLE);

				if (taskNumbers.length == 1) {
					setFeedback(MESSAGE_FEEDBACK_DONE);
				} else {
					setFeedback(taskNumbers.length
							+ MESSAGE_FEEDBACK_DONE_MULTIPLE);

				}
			}
		} catch (NumberFormatException e) {
			throw new Exception(MESSAGE_FEEDBACK_INVALID_NUMBERFORMAT);
		}
		clearRecentChange();
	}
	
	/**
	 * Find free slots of time
	 * 
	 * @param content
	 * @throws Exception
	 */

	//@author A0112044B
	private void findFreeTime(String content) throws Exception {

		ArrayList<longPair> freeSlots = findTime(content);

		ArrayList<String> result = new ArrayList<String>();

		for (int i = 0; i < freeSlots.size(); i++) {
			longPair pair = freeSlots.get(i);

			String str = OPENING + timeToText(pair.getFirst()) + CONNECTOR_TO
					+ timeToText(pair.getSecond()) + CLOSING;
			result.add(str);
		}

		if (result.size() == 0) {
			setFeedback(MESSAGE_NO_SLOT_FOUND);
		} else if (result.size() == 1) {
			setFeedback(result.size() + MESSAGE_ONE_SLOT_FOUND);
		} else if (result.size() >= 2) {
			setFeedback(result.size() + MESSAGE_SLOTS_FOUND);
		}

		// the list size is at most MAX_NUM_OF_RESULTS
		assert result.size() <= MAX_NUM_OF_RESULTS;
		setFreeSlotList(result);
	}

	private String timeToText(Long timeInMilli) {
		Date time = new Date(timeInMilli);
		LocalDateTime timeobj = LocalDateTime.ofInstant(time.toInstant(),
				ZoneId.systemDefault());
		DateTimeFormatter format = DateTimeFormatter
				.ofPattern(Config.taskDateFormat);

		return format.format(timeobj);
	}

	/**
	 * Parses user input to find the time interval where the user is free.
	 * 
	 * @param content
	 *            User input.
	 * @return Time with hours and minutes in a pair.
	 * @throws NumberFormatException
	 *             If user input is not a valid time.
	 * @throws Exception
	 *             If user input is invalid.
	 */
	//@author: A0112044B
	private ArrayList<longPair> findTime(String content) throws Exception {

		if (hasHour(content) || hasMinute(content)) {
			return findHour(content);
		} else {
			return findPeriod(content);
		}

		// return new ArrayList<longPair>();

	}

	/**
	 * Find slots of a length of time
	 * @param content
	 * @return
	 * @throws Exception
	 */
	//@author A0112044B
	private ArrayList<longPair> findHour(String content) throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, DAY_PER_MONTH);
		Date nextMonth = cal.getTime();
		nextMonth = setEndDate(nextMonth);

		if ((hasHour(content) && !hasMinute(content))
				|| (!hasHour(content) && hasMinute(content))) {
			String[] para = content.trim().split(SPACE_STRING);
			int len = para.length;
			if (len == 2) {
				try {

					int number = Integer.parseInt(para[0]);
					if (hasHour(content)) {
						return findTimeLength(number * MINUTE_PER_HOUR,
								new Date(), nextMonth);
					} else {
						return findTimeLength(number, new Date(), nextMonth);
					}
				} catch (NumberFormatException e) {
					throw new Exception(MESSAGE_FEEDBACK_FREETIME_INVALID);
				}
			} else if (len > 2) {
				String date = SPACE_STRING;
				try {
					int number = Integer.parseInt(para[0]);

					for (int i = 2; i < len; i++) {
						date = date + para[i] + SPACE_STRING;
					}

					Date deadline = timeParser(date);

					if (deadline != null) {

						deadline = setEndDate(deadline);
						if (hasHour(content)) {
							return findTimeLength(number * MINUTE_PER_HOUR,
									new Date(), deadline);
						} else {
							return findTimeLength(number, new Date(), deadline);
						}
					} else {
						throw new Exception(MESSAGE_FEEDBACK_FREETIME_INVALID);
					}

				} catch (NumberFormatException e) {
					throw new Exception(MESSAGE_FEEDBACK_FREETIME_INVALID);
				}
			}
		} else if (hasHour(content) && hasMinute(content)) {
			String[] para = content.trim().split(SPACE_STRING);
			int len = para.length;

			if (len == 4) {
				try {
					int hh = Integer.parseInt(para[0]);
					int mm = Integer.parseInt(para[2]);
					return findTimeLength(hh * MINUTE_PER_HOUR + mm,
							new Date(), nextMonth);
				} catch (NumberFormatException e) {
					throw new Exception(MESSAGE_FEEDBACK_FREETIME_INVALID);
				}
			} else if (len > 4) {
				String date = SPACE_STRING;
				try {
					int hh = Integer.parseInt(para[0]);
					int mm = Integer.parseInt(para[2]);

					for (int i = 4; i < len; i++) {
						date = date + para[i] + SPACE_STRING;
					}
					Date deadline = timeParser(date);
					if (deadline != null) {
						deadline = setEndDate(deadline);
						return findTimeLength(hh * MINUTE_PER_HOUR + mm,
								new Date(), deadline);
					} else {
						throw new Exception(MESSAGE_FEEDBACK_FREETIME_INVALID);
					}

				} catch (NumberFormatException e) {
					throw new Exception(MESSAGE_FEEDBACK_FREETIME_INVALID);
				}
			}

		}

		return new ArrayList<longPair>();
	}
	/**
	 * find the frees slot for a certain period of time
	 * @param content
	 * @return
	 * @throws Exception
	 */
	
	//@author A0112044B
	private ArrayList<longPair> findPeriod(String content) throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, DAY_PER_MONTH);
		Date nextMonth = cal.getTime();
		nextMonth = setEndDate(nextMonth);

		List<Date> dates = timeParserPeriod(content);
		if (dates.size() == 2) {
			Date date1 = dates.get(0);
			Date date2 = dates.get(1);

			if (date1 == null || date2 == null || date1.after(date2)) {
				throw new Exception(MESSAGE_FEEDBACK_FREETIME_INVALIDPERIOD);
			} else {
				Date today = new Date();
				// the time of today
				if (compare(date1, today) == 0 && compare(date2, today) == 0) {

					return findTimePeriod(date1, date2, nextMonth);
				} else if (compare(date1, today) > 0
						&& compare(date2, today) > 0
						&& compare(date1, date2) == 0) {

					Calendar now = Calendar.getInstance();
					Calendar cal1 = Calendar.getInstance();
					cal1.setTime(date1);
					Calendar cal2 = Calendar.getInstance();
					cal2.setTime(date2);
					Date deadline = cal2.getTime();

					cal1.set(Calendar.YEAR, now.get(Calendar.YEAR));
					cal1.set(Calendar.MONTH, now.get(Calendar.MONTH));
					cal1.set(Calendar.DATE, now.get(Calendar.DATE));

					cal2.set(Calendar.YEAR, now.get(Calendar.YEAR));
					cal2.set(Calendar.MONTH, now.get(Calendar.MONTH));
					cal2.set(Calendar.DATE, now.get(Calendar.DATE));
					Date newDate1 = cal1.getTime();
					Date newDate2 = cal2.getTime();

					return findTimePeriod(newDate1, newDate2, deadline);
				}
			}
		}
		return new ArrayList<longPair>();

	}

	
	/**
	 * set the time to 23.59.59 of a date
	 * @param date
	 * @return
	 */
	//@author A0112044B
	private Date setEndDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, MAX_HOUR_OF_DAY);
		cal.set(Calendar.MINUTE, MAX_MINUTE_OR_SECOND);
		cal.set(Calendar.SECOND, MAX_MINUTE_OR_SECOND);
		return cal.getTime();
	}

	/**
	 * check if the content has description for hour
	 * 
	 * @param content
	 * @return
	 */

	//@author A0112044B
	private boolean hasHour(String content) {

		if (content.toLowerCase().indexOf(FREETIME_HOUR1) != NOT_INDEX
				|| content.toLowerCase().indexOf(FREETIME_HOUR2) != NOT_INDEX) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * check if the content has description for minute
	 * 
	 * @param content
	 * @return
	 */

	//@author A0112044B
	private boolean hasMinute(String content) {
		if (content.toLowerCase().indexOf(FREETIME_MINUTES1) != NOT_INDEX
				|| content.toLowerCase().indexOf(FREETIME_MINUTES2) != NOT_INDEX
				|| content.toLowerCase().indexOf(FREETIME_MINUTES3) != NOT_INDEX
				|| content.toLowerCase().indexOf(FREETIME_MINUTES4) != NOT_INDEX) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Parses user input to get a date. If no date time is recognized, return
	 * null.
	 * 
	 * @param input
	 *            User input.
	 * @return Date time when user is free.
	 */
	//@author A0112044B
	private Date timeParser(String input) {

		Parser parser = new Parser();

		List<DateGroup> groups = parser.parse(input);
		List<Date> dates = new ArrayList<Date>();
		for (DateGroup group : groups) {
			dates.addAll(group.getDates());
		}

		if (dates.size() == 1) {
			assert dates.size() == 1;
			return dates.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Time parser for a period of time
	 * 
	 * @param input
	 * @return
	 */
	//@author A0112044B
	private List<Date> timeParserPeriod(String input) {

		Parser parser = new Parser();

		List<DateGroup> groups = parser.parse(input);
		List<Date> dates = new ArrayList<Date>();
		for (DateGroup group : groups) {
			dates.addAll(group.getDates());
		}

		if (dates.size() == 2) {
			assert dates.size() == 2;
			return dates;
		} else {
			return null;
		}
	}

	/**
	 * Compares the two Date objects by computing their difference. If date1 is
	 * before date2, return a negative difference, If date1 is after date2,
	 * return a positive difference. If they are the same, return 0.
	 * 
	 * @param date1
	 *            Date object to be compared with.
	 * @param date2
	 *            Date object to be compared with.
	 * @return Difference between the two Date objects.
	 */
	//@author A0112044B
	private int compare(Date date1, Date date2) {

		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);

		if (cal1.get(Calendar.YEAR) != cal2.get(Calendar.YEAR)) {
			return cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		} else if (cal1.get(Calendar.MONTH) != cal2.get(Calendar.MONTH)) {
			return cal1.get(Calendar.MONTH) - cal2.get(Calendar.MONTH);
		} else {
			return cal1.get(Calendar.DAY_OF_MONTH)
					- cal2.get(Calendar.DAY_OF_MONTH);
		}

	}

	/**
	 * find all free period until the deadline, return a list of
	 * MAX_NUM_OF_RESULTS only
	 * 
	 * @param start
	 * @param end
	 * @param deadline
	 * @return
	 */

	//@author A0112044B
	private ArrayList<longPair> findTimePeriod(Date start, Date end,
			Date deadline) {

		deadline=setEndDate(deadline);
		ArrayList<longPair> freeSlots = freeIntervals(new Date(), deadline);
		ArrayList<longPair> result = new ArrayList<longPair>();
		
		longPair interval = new longPair(start.getTime(), end.getTime());
		Date current = new Date();
		int numOfDate = 0;
		while (compare(current, deadline) <= 0
				&& numOfDate < MAX_NUM_OF_RESULTS) {
			assert numOfDate < MAX_NUM_OF_RESULTS;
			if (isFree(interval, freeSlots)) {
				result.add(interval);
				numOfDate++;
			}

			Calendar cal = Calendar.getInstance();
			cal.setTime(current);
			cal.add(Calendar.DATE, 1);
			current = cal.getTime();
			// increase current to the next day

			long first = interval.getFirst();
			long second = interval.getSecond();
			interval = new longPair(first + MILISECOND_PER_DAY, second
					+ MILISECOND_PER_DAY);
		}

		return result;

	}

	/**
	 * check if an interval is free or not
	 * 
	 * @param interval
	 * @param list
	 * @return
	 */

	//@author A0112044B
	private boolean isFree(longPair interval, ArrayList<longPair> list) {

		for (int i = 0; i < list.size(); i++) {
			longPair free = list.get(i);
			if (interval.getFirst() >= free.getFirst()
					&& interval.getSecond() <= free.getSecond()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * find the interval of times that are free
	 * 
	 * @param numOfMin
	 * @param deadline
	 * @return
	 */
	//@author A0112044B
	private ArrayList<longPair> findTimeLength(int numOfMin, Date startTime,
			Date deadline) {

		ArrayList<longPair> freeSlots = freeIntervals(startTime, deadline);
		ArrayList<longPair> result = new ArrayList<longPair>();
		int numOfSlot = 0;

		for (int i = 0; i < freeSlots.size() && numOfSlot < MAX_NUM_OF_RESULTS; i++) {
			longPair slot = freeSlots.get(i);

			long timeLen = (slot.getSecond() - slot.getFirst())
					/ (MILISECOND_PER_MINUTE);
			if (timeLen >= numOfMin) {
				result.add(slot);
				numOfSlot++;
			}

		}
		return result;
	}

	/**
	 * get the list of free intervals
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	//@author A0112044B
	private ArrayList<longPair> freeIntervals(Date start, Date end) {

		ArrayList<longPair> occupiedIntervals = getOccupied(tasks);
		ArrayList<longPair> intervalFree = new ArrayList<longPair>();

		// initialize
		intervalFree.add(new longPair(start.getTime(), end.getTime()));

		for (int i = 0; i < occupiedIntervals.size(); i++) {
			longPair occu = occupiedIntervals.get(i);

			for (int j = 0; j < intervalFree.size(); j++) {
				longPair free = intervalFree.get(j);
				if (hasOverLap(occu, free)) {
					// case 1: occu > free, update
					if (occu.getFirst() > free.getFirst()
							&& occu.getSecond() > free.getSecond()) {
						intervalFree.set(j,
								new longPair(free.getFirst(), occu.getFirst()));
					}// cas2 2: occu< free, update
					else if (occu.getFirst() < free.getFirst()
							&& occu.getSecond() < free.getSecond()) {
						intervalFree
								.set(j,
										new longPair(occu.getSecond(), free
												.getSecond()));
					}// case 3:occu covers free, remove
					else if (occu.getFirst() < free.getFirst()
							&& occu.getSecond() > free.getSecond()) {
						intervalFree.remove(j);
						j--;
					}// case 4: occu is covered by free, add 2 time slots
					else if (occu.getFirst() > free.getFirst()
							&& occu.getSecond() < free.getSecond()) {
						intervalFree.set(j,
								new longPair(free.getFirst(), occu.getFirst()));
						intervalFree
								.add(j+1,
										new longPair(occu.getSecond(), free
												.getSecond()));
						j++;
					}
				}
			}
		
			Collections.sort(intervalFree);
		}
		return intervalFree;

	}

	/**
	 * get the list of occupied slots of time
	 * 
	 * @param listToSearch
	 * @return
	 */
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

	/**
	 * check if 2 intervals have overlap or not
	 * 
	 * @param pair1
	 * @param pair2
	 * @return
	 */

	//@author A0112044B
	private boolean hasOverLap(longPair pair1, longPair pair2) {

		if (Math.max(pair1.getFirst(), pair2.getFirst()) < Math.min(
				pair1.getSecond(), pair2.getSecond())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Searches for tasks that consist of the keywords that the user enters.
	 * 
	 * @param content
	 *            User input
	 */
	//@author A0112044B
	private void search(String content) {
		TaskList resultList = tasks.search(content);
		setResultList(resultList);

		if (resultList.size() == 0 || resultList.size() == 1) {
			setFeedback(resultList.size() + MESSAGE_ONE_SEARCH_RESULT);
		} else {
			setFeedback(resultList.size() + MESSAGE_SEARCH_RESULTS);
		}
	}

	/*
	 * 
	 */
	//@author A0112044B
	private void pending() {
		setResultList(tasks.getFloatingTasks());
	}

	/**
	 * Sets the result list of current overdue tasks.
	 */
	//@author A0112044B
	private void overDue() {
		setResultList(tasks.getOverdueTasks());
	}

	/**
	 * Updates the current lists with the previous changes.
	 */
	//@author A0112044B
	private void updateForUndo() {
		updateUndoList();
		updateUndoArchiveList();
	}

	/**
	 * Updates the current archive list with the previous changes.
	 */
	//@author A0112044B
	private void updateUndoArchiveList() {
		undoArchiveList.push(archiveTasks.clone());
	}

	/**
	 * Pushes the current state into the undo list.
	 */
	//@author A0112044B
	private void updateUndoList() {
		undoList.push(tasks.clone());
	}

	/**
	 * Replaces the current state with the previous states.
	 */
	//@author A0112044B
	private void undo() {
		// if there are states to undo
		if (!undoList.empty()) {
			tasks = undoList.pop();
			archiveTasks = undoArchiveList.pop();
			setFeedback(MESSAGE_FEEDBACK_UNDO);
		} else {
			setFeedback(MESSAGE_FEEDBACK_UNDO_MAXIMUM);
		}
	}

	/*
	 * Postpones the desired task.
	 * 
	 * @param content Task numbers entered by user.
	 * 
	 * @throws Exception If current displayed list is the archive list.
	 * 
	 * @throws NumberFormatException If the task numbers entered by user are not
	 * numbers.
	 */
	//@author A0115584A
	private void postpone(String content) throws Exception {
		logger.entering(getClass().getName(), LOGGING_PURPOSE_METHODNAME_POSTPONE);
		try {
			if (displayListType == DisplayList.ARCHIVE) {
				setFeedback(String.format(MESSAGE_FEEDBACK_INVALIDLIST,
						CMD_POSTPONE));
				throw new Exception(String.format(MESSAGE_FEEDBACK_INVALIDLIST,
						CMD_POSTPONE));

			} else {
				String[] taskNumbers = content.split(SPACE_STRING);
				Arrays.sort(taskNumbers);
				for (int i = 0; i < taskNumbers.length; i++) {
					Integer taskNum = Integer.parseInt(taskNumbers[i]) - 1;
					Task postponedTask = tasks.get(taskNum);
					postponedTask.clearTimes();
					postponedTask.setType(TaskType.FLOATING);

					// set the last task to be highlighted in the UI after postpone is done.
					if (i == taskNumbers.length - 1) { 
						setRecentChange(postponedTask, tasks);
					}
				}

				tasks.sort();
				setDisplayList(DisplayList.MAIN);

				if (taskNumbers.length == 1) {
					setFeedback(MESSAGE_FEEDBACK_POSTPONE);
				} else {
					setFeedback(taskNumbers.length
							+ MESSAGE_FEEDBACK_POSTPONEMULTIPLE);
				}
			}
		} catch (NumberFormatException e) {
			setFeedback(MESSAGE_FEEDBACK_INVALID_NUMBERFORMAT);
			throw new Exception(MESSAGE_FEEDBACK_INVALID_NUMBERFORMAT);
		}
		
		logger.exiting(getClass().getName(), LOGGING_PURPOSE_METHODNAME_POSTPONE);
	}

	/*
	 * Displays the existing tasks to the user.
	 */
	//@author A0115584A
	private void displayMainList() {
		tasks.sort();
		resetRecentChange();
		setDisplayList(DisplayList.MAIN);
		setFeedback(MESSAGE_FEEDBACK_MAINLIST);
	}

	/**
	 * Checks if the list the user is on is the main list or search list. If it
	 * either the main or search list, deems the edit valid and proceeds with
	 * it.
	 * 
	 * @param content
	 *            User input
	 * @throws Exception
	 *             If current displayed list is not on the main or search list.
	 */
	//@author A0115194J
	private void editTask(String content) throws Exception {

		if ((displayListType == DisplayList.MAIN)
				|| (displayListType == DisplayList.SEARCH)) {
			validEdit(content);
		} else {
			setFeedback(String.format(MESSAGE_FEEDBACK_INVALIDLIST,
					CMD_EDIT));
			throw new Exception(String.format(MESSAGE_FEEDBACK_INVALIDLIST,
					CMD_EDIT));
		}

	}

	/**
	 * Checks if the user has specified any task to edit and if specified,
	 * proceeds with the edit.
	 * 
	 * @param content
	 *            User input.
	 * @throws Exception
	 *             If user did not specify what to edit or when list is empty.
	 */
	private void validEdit(String content) throws Exception {
		if (tasks.isEmpty()) {
			setFeedback(MESSAGE_FEEDBACK_INVALID_EMPTYLIST);
			throw new Exception(MESSAGE_FEEDBACK_INVALID_EMPTYLIST);
		}
		if (isEmptyCommand(content)) {
			setFeedback(MESSAGE_FEEDBACK_INVALID_NUMBERFORMAT);
			throw new Exception(MESSAGE_FEEDBACK_INVALID_NUMBERFORMAT);
		} else {
			tasks.sort();
			Task taskEdited = proceedWithEdit(content);
			setRecentChange(taskEdited, tasks);
			setDisplayList(DisplayList.MAIN);
		}
	}

	/**
	 * Edits the task using the specified content.
	 * 
	 * @param content
	 *            User input.
	 * @return Edited Task object.
	 * @throws Exception
	 *             If user input is in the incorrect edit format.
	 * @throws NumberFormatException
	 *             If the user did not enter task number or if in incorrect
	 *             position.
	 */
	private Task proceedWithEdit(String content) throws Exception {
		try {
			String[] words = content.split(SPACE_STRING);
			int positionOfTask = Integer.parseInt(words[0]) - 1;

			checkValidParameters(words, positionOfTask);

			String attributeToChange = words[1];
			Task taskToEdit = tasks.get(positionOfTask);

			if (isMultipleEditPriority(attributeToChange)) {
				editMultiplePriority(words);
			} else {
				editSingleTask(words, attributeToChange, taskToEdit);
			}

			return taskToEdit;
		} catch (NumberFormatException e) {
			setFeedback(String.format(MESSAGE_FEEDBACK_INVALID,
					CMD_EDIT));
			throw new Exception(String.format(MESSAGE_FEEDBACK_INVALID,
					CMD_EDIT));
		}
	}

	/**
	 * Parses the command for a single task to be edited, be it description,
	 * time or priority.
	 * 
	 * @param words
	 *            The words in the edit command.
	 * @param attributeToChange
	 *            The attribute to change.
	 * @param taskToEdit
	 *            The task to be edited.
	 * @throws Exception
	 *             If the edit format is invalid.
	 */
	private void editSingleTask(String[] words, String attributeToChange,
			Task taskToEdit) throws Exception {

		String editDetails = EMPTY_STRING;
		checkDetailsSpecified(words, attributeToChange);

		editDetails = concatenateEditDetails(words, editDetails);
		editDetails = editDetails.trim();

		editAttribute(taskToEdit, attributeToChange, editDetails);
		setFeedback(MESSAGE_FEEDBACK_EDIT);
	}

	/**
	 * Concatenates the details to be edited.
	 * 
	 * @param words
	 *            The words in the edit command.
	 * @param editDetails
	 *            The details to be replaced with.
	 * @return The concatenated string of details.
	 */
	private String concatenateEditDetails(String[] words, String editDetails) {
		for (int i = 2; i < words.length; i++) {
			editDetails += words[i] + SPACE_STRING;
		}
		return editDetails;
	}

	/**
	 * Checks if the details to be replaced with are specified.
	 * 
	 * @param words
	 *            The words in the edit command.
	 * @param attributeToChange
	 *            The attribute to change.
	 * @throws Exception
	 *             If the details are not specified.
	 */
	private void checkDetailsSpecified(String[] words, String attributeToChange)
			throws Exception {
		if ((!attributeToChange.equals(EDIT_ATTRIBUTE_PRIORITY)) && (words.length == 2)) {
			setFeedback(MESSAGE_FEEDBACK_EDIT_INVALID_NULLDETAILS);
			throw new Exception(MESSAGE_FEEDBACK_EDIT_INVALID_NULLDETAILS);
		}
	}

	/**
	 * Edits the priority of multiple tasks.
	 * 
	 * @param words
	 *            The words in the edit command.
	 */
	private void editMultiplePriority(String[] words) {
		for (int i = 0; i < words.length - 1; i++) {
			Task task = tasks.get(Integer.parseInt(words[i]) - 1);
			editPriority(task);
			setFeedback(words.length - 1 + MESSAGE_FEEDBACK_EDIT_MULTIPLE);
			if (i == words.length - 2) {
				setRecentChange(task, tasks);
			}
		}
	}

	/**
	 * This method checks if the parameters in the edit command are valid by
	 * checking if the task number is within range or if the parameters are too
	 * few.
	 * 
	 * @param words
	 *            All the words in the edit command.
	 * @param positionOfTask
	 *            The position of task to be edited.
	 * @throws Exception
	 *             If the task position is out of range or the parameters are
	 *             too few.
	 * 
	 */
	private void checkValidParameters(String[] words, int positionOfTask)
			throws Exception {
		if (positionOfTask < 0 || positionOfTask >= tasks.size()
				|| words.length < 2) {
			setFeedback(String.format(MESSAGE_FEEDBACK_INVALID,
					CMD_EDIT));
			throw new Exception(String.format(MESSAGE_FEEDBACK_INVALID,
					CMD_EDIT));
		}
	}

	/**
	 * Checks if the user wants to edit priority of multiple tasks.
	 * 
	 * @param attributeToChange
	 *            Either task number if multiple tasks are to be prioritised or
	 *            the attribute to change such as description or time or
	 *            exclamation mark if single task is to be edited.
	 * @return true if attribute to change is a number.
	 * @throws NumberFormatException
	 *             If attribute to change is not a number.
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
	 *            Task object to be edited.
	 * @param attribute
	 *            Attribute to be edited.
	 * @param editDetails
	 *            Changes that user specifies.
	 * @throws Exception
	 *             If user did not specify the attribute to be edited.
	 */
	private void editAttribute(Task taskToEdit, String attribute,
			String editDetails) throws Exception {
		if (attribute.equalsIgnoreCase(EDIT_ATTRIBUTE_DESC)) {
			editDescription(taskToEdit, editDetails);
		} else if (attribute.equalsIgnoreCase(EDIT_ATTRIBUTE_TIME)) {
			processTime(taskToEdit, editDetails);
		} else if (attribute.equalsIgnoreCase(EDIT_ATTRIBUTE_PRIORITY)) {
			editPriority(taskToEdit);
		} else {
			setFeedback(MESSAGE_FEEDBACK_EDIT_SPECIFY);
			throw new Exception(MESSAGE_FEEDBACK_EDIT_SPECIFY);
		}
	}

	/**
	 * Edits the priority of a task by reversing the existing priority of the
	 * task.
	 * 
	 * @param taskToEdit
	 *            Task object to be edited.
	 */
	private void editPriority(Task taskToEdit) {
		boolean priorityOfTask = taskToEdit.isPrioritized();
		if (priorityOfTask) {
			taskToEdit.setPriority(BOOLEAN_FALSE);
		} else {
			taskToEdit.setPriority(BOOLEAN_TRUE);
		}
	}

	/**
	 * Replaces the description of task with the given description.
	 * 
	 * @param taskToEdit
	 *            Task object to be edited.
	 * @param desc
	 *            description that user specifies.
	 */
	private void editDescription(Task taskToEdit, String desc) {
		if (desc != null) {
			taskToEdit.setDesc(desc);
		}
	}

	/**
	 * Checks if the task numbers of tasks to be deleted are valid and if valid,
	 * proceeds with deletion.
	 * 
	 * @param content
	 *            Task numbers specified by user
	 * @throws Exception
	 *             If the current displayed list is the archive list.
	 */
	private void deleteTask(String content) throws Exception {

		if ((displayListType == DisplayList.MAIN)
				|| (displayListType == DisplayList.SEARCH)) {
			if (isValidDelete(content)) {
				proceedWithDelete(content);
			}
		} else {
			setFeedback(String.format(MESSAGE_FEEDBACK_INVALIDLIST,
					CMD_DELETE));
			throw new Exception(String.format(MESSAGE_FEEDBACK_INVALIDLIST,
					CMD_DELETE));
		}
	}

	/**
	 * Deletes task/tasks after knowing the task numbers are valid.
	 * 
	 * @param content
	 *            Task numbers specified by user.
	 * @throws NumberFormatException
	 *             If user enters invalid task numbers.
	 */
	private void proceedWithDelete(String content) throws Exception {
		try {

			String[] taskNumbers = content.split(SPACE_STRING);
			List<Integer> taskNumDescending = new ArrayList<Integer>();
			addTaskNumbersToList(taskNumbers, taskNumDescending);
			Collections.sort(taskNumDescending, Collections.reverseOrder());

			deleteTasksDescendingOrder(taskNumDescending);

			tasks.sort();
			setFeedBackDelete(taskNumDescending);

		} catch (NumberFormatException e) {
			setFeedback(String.format(MESSAGE_FEEDBACK_INVALID,
					CMD_DELETE));
			throw new Exception(String.format(MESSAGE_FEEDBACK_INVALID,
					CMD_DELETE));
		}
	}

	/**
	 * This method sets the appropriate feedback depending on how many tasks
	 * were deleted.
	 * 
	 * @param taskNumDescending
	 *            The list of task numbers in descending order.
	 */
	private void setFeedBackDelete(List<Integer> taskNumDescending) {
		if (taskNumDescending.size() == 1) {
			setFeedback(MESSAGE_FEEDBACK_DELETE);
		} else {
			setFeedback(taskNumDescending.size()
					+ MESSAGE_FEEDBACK_DELETE_MULTIPLE);
		}
		resetRecentChange();
		setDisplayList(DisplayList.MAIN);
	}

	/**
	 * Deletes all the tasks in the list.
	 * 
	 * @param taskNumDescending
	 *            The list of task numbers in descending order.
	 * @throws Exception
	 *             If any task number entered is out of range.
	 */
	private void deleteTasksDescendingOrder(List<Integer> taskNumDescending)
			throws Exception {
		for (int i = 0; i < taskNumDescending.size(); i++) {
			int taskNum = taskNumDescending.get(i);
			executeDelete(taskNum);
			if (i == taskNumDescending.size() - 1) {
				setRecentChange(taskNumDescending.get(i) - 1, tasks);
			}
		}
	}

	/**
	 * This method adds task numbers to the list.
	 * 
	 * @param taskNumbers
	 *            The task numbers of tasks to be deleted.
	 * @param taskNumDescending
	 *            The list of task numbers.
	 */
	private void addTaskNumbersToList(String[] taskNumbers,
			List<Integer> taskNumDescending) {
		for (int i = 0; i < taskNumbers.length; i++) {
			int taskNum = Integer.parseInt(taskNumbers[i]);
			taskNumDescending.add(taskNum);
		}
	}

	/**
	 * Deletes the task with the specified number.
	 * 
	 * @param taskNum
	 *            Task number.
	 * @throws IndexOutOfBoundsException
	 *             If task number entered is out of range.
	 */
	private void executeDelete(int taskNum) throws Exception {
		logger.entering(getClass().getName(), LOGGING_PURPOSE_METHODNAME_DELETE);
		try {
			int positionOfTask = taskNum - 1;
			tasks.remove(positionOfTask);
		} catch (IndexOutOfBoundsException e) {
			setFeedback(MESSAGE_FEEDBACK_OUTOFRANGE);
			throw new Exception(MESSAGE_FEEDBACK_OUTOFRANGE);
		}
		logger.exiting(getClass().getName(), LOGGING_PURPOSE_METHODNAME_DELETE);
	}

	/**
	 * Changes the attributes related to the changing of a page.
	 * 
	 * @param content
	 *            User input.
	 * @throws Exception
	 *             If user keys in an invalid direction or if the page is the
	 *             first or the last.
	 */
	private void changePage(String content) throws Exception {
		String direction = content.trim();
		changeCurrentPageNum(direction);
	}

	/**
	 * Checks if it is valid to change the current page number and if so,
	 * changes the current page number.
	 * 
	 * @param direction
	 *            Direction to change page.
	 * @throws Exception
	 *             If user keys in an invalid direction or if the page is the
	 *             first or the last.
	 */
	//@author
	private void changeCurrentPageNum(String direction) throws Exception {
		if (direction.equalsIgnoreCase(PAGE_DIRECTION_UP)) {
			if (checkValidPageUp()) {
				currentPageNum--;
				recentChange = 0;
				setFeedback(MESSAGE_FEEDBACK_PAGE + currentPageNum
						+ MESSAGE_FEEDBACK_PAGE_CONNECTOR
						+ getTotalNumOfPages(displayListType));
			} else {
				setFeedback(MESSAGE_FEEDBACK_PAGE_FIRST);
				throw new Exception(MESSAGE_FEEDBACK_PAGE_FIRST);
			}
		} else if (direction.equalsIgnoreCase(PAGE_DIRECTION_DOWN)) {
			if (checkValidPageDown()) {
				currentPageNum++;
				recentChange = 0;
				setFeedback(MESSAGE_FEEDBACK_PAGE + currentPageNum
						+ MESSAGE_FEEDBACK_PAGE_CONNECTOR
						+ getTotalNumOfPages(displayListType));
			} else {
				setFeedback(MESSAGE_FEEDBACK_PAGE_LAST);
				throw new Exception(MESSAGE_FEEDBACK_PAGE_LAST);
			}
		} else {
			setFeedback(MESSAGE_FEEDBACK_PAGE_COMMAND);
			throw new Exception(MESSAGE_FEEDBACK_PAGE_COMMAND);
		}
	}

	/**
	 * Checks if it is possible to go to the next page by checking if there are
	 * more tasks in the list which are pushed to the next page.
	 * 
	 * @return true if it is possible to go to the next page.
	 */
	private boolean checkValidPageDown() {
		Integer totalNumPages;
		totalNumPages = getTotalNumOfPages(displayListType);
		if (currentPageNum < totalNumPages) {
			return true;
		}
		return false;
	}

	/**
	 * Gets the total number of pages for a type of list.
	 * 
	 * @param displayListType
	 *            Type of list.
	 * @return Total number of pages for a list.
	 */
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
			break;
		case FREESLOTS:
			totalNumPages = 1;
			break;
		default:
			totalNumPages = 0;
			break;
		}

		return totalNumPages;
	}

	/**
	 * Checks if it is possible to go to the previous page. If currently on
	 * first page, it will return false.
	 * 
	 * @return true if it is possible to go to the previous page.
	 */
	//@author A0115194J
	private boolean checkValidPageUp() {
		if (currentPageNum <= 1) {
			return false;
		}
		return true;
	}

	/**
	 * Updates the content stored.
	 */
	private void updateStorage() {
		storage.write(tasks.getStringList());
		storage.writeArchive(archiveTasks.getStringList());
	}

	/**
	 * Checks if the list of tasks is empty or if the user has not specified the
	 * task number to delete.
	 * 
	 * @param content
	 *            User input.
	 * @return boolean true if delete is valid.
	 * @throws Exception
	 *             If there is nothing to delete or user did not specify what to
	 *             delete.
	 */
	private boolean isValidDelete(String content) throws Exception {
		if (tasks.isEmpty()) {
			setFeedback(String.format(
					MESSAGE_FEEDBACK_INVALID_EMPTYLIST, CMD_DELETE));
			throw new Exception(String.format(
					MESSAGE_FEEDBACK_INVALID_EMPTYLIST, CMD_DELETE));
		} else if (isEmptyCommand(content)) {
			setFeedback(MESSAGE_FEEDBACK_INVALID_NUMBERFORMAT);
			throw new Exception(MESSAGE_FEEDBACK_INVALID_NUMBERFORMAT);
		} else {
			return true;
		}
	}

	/**
	 * Checks if the user has entered anything after the command type.
	 * 
	 * @param content
	 *            User input
	 * @return True if user did not enter anything after the command type and
	 *         false if user did enter anything.
	 */
	private boolean isEmptyCommand(String content) {
		return content.trim().equals(EMPTY_STRING);
	}

	/**
	 * Adds tasks
	 * 
	 * @param content
	 *            User input.
	 * @throws Exception
	 *             If user did not specify what to add.
	 */
	//@author A0119381E
	private void addTask(String content) throws Exception {
		if (isEmptyCommand(content)) {
			setFeedback(MESSAGE_FEEDBACK_ADD_SPECIFY);
			throw new Exception(MESSAGE_FEEDBACK_ADD_SPECIFY);
		}

		Task task = processUserInput(content);
		this.tasks.add(task);

		setFeedback(MESSAGE_FEEDBACK_ADD);
		tasks.sort();
		setRecentChange(task, tasks);
		setDisplayList(DisplayList.MAIN);
	}

	/**
	 * Processes user input and creates a Task object.
	 * 
	 * @param content
	 *            User input.
	 * @return Task object made from user input.
	 */
	//@author A0119381E
	private Task processUserInput(String content) {
		String desc = EMPTY_STRING;
		Integer singlePos = 0;
		Integer doublePos = 0;
		singlePos = content.indexOf('\'', 0);
		doublePos = content.indexOf('\"', 0);
		if (singlePos == -1 && doublePos == -1) {
			// return processUserInputClassic(content);
			desc = content + SPACE_STRING;
			content = EMPTY_STRING;
		}

		String regex = "([\"'])(?:(?=(\\\\?))\\2.)*?\\1";
		Matcher matcher = Pattern.compile(regex).matcher(content);
		while (matcher.find()) {
			desc += content.substring(matcher.start() + 1, matcher.end() - 1)
					+ SPACE_STRING;
		}
		desc = desc.substring(0, desc.length() - 1);
		content = content.replaceAll(regex, EMPTY_STRING);

		Task task = new TaskClass();
		Boolean priority;
		if (content.indexOf('!') != -1) {
			content = content.replaceAll("!", EMPTY_STRING);
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

	/**
	 * Processes user input into time.
	 * 
	 * @param task
	 *            Task object that time will be added to.
	 * @param content
	 *            Stringed time input from user.
	 * @return true if time is added to Task object.
	 */
	//@author A0119381E
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
	 * Removes the command type from the command string.
	 * 
	 * @param command
	 *            User input.
	 * @param operation
	 *            Command type that user wants to execute.
	 * @return Trimmed user input without the command type.
	 */
	//@author A0115194J
	private String removeCommandType(String command, String operation) {
		return command.replaceFirst(operation, EMPTY_STRING).trim();
	}

	/**
	 * Matches the command type for each operation.
	 * 
	 * @param operation
	 *            Command input by user.
	 * @return CommandType
	 */
	private CommandType matchCommandType(String operation) {
		CommandType command = commandMap.get(operation.trim().toLowerCase());
		if (command == null) {
			command = CommandType.SEARCH;
		}

		return command;
	}

	/**
	 * Gets an instance of the Controller object.
	 * 
	 * @return An instance of the Controller object.
	 */
	//@author A0119381E
	public static Controller getInstance() {
		if (theController == null) {
			theController = new ControllerClass();
		}

		return theController;
	}

}
