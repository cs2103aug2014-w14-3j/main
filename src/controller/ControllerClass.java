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
 * 
 * 
 */

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
	
	enum CommandType {
		ADD, DELETE, EDIT, POSTPONE, DISPLAY, UNDO, ARCHIVE, SEARCH, DONE, CHANGEPAGE, OVERDUE
	};

	enum DisplayList {
		MAIN, ARCHIVE, SEARCH
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
		commandMap = Collections.unmodifiableMap(aMap);
	}

	private static final int POSITION_OF_OPERATION = 0;
	private static final int maxNumOfUndo = 40;
	private static final int numTasksInSinglePage = 10;

	private static Controller theController = null;
	private TaskList tasks;
	private TaskList archiveTasks;
	private TaskList resultTasks;

	private DisplayList displayListType;
	private Integer recentChange;
	private Integer currentPageNum;

	private Storage storage;
	private FixedSizeStack<TaskList> undoList;
	private FixedSizeStack<TaskList> undoArchiveList;

	public ControllerClass() {
		storage = createStorageObject();
		undoList = new FixedSizeStack<TaskList>(maxNumOfUndo);
		undoArchiveList = new FixedSizeStack<TaskList>(maxNumOfUndo);

		getFileContent();
		setNumTaskOnPage(numTasksInSinglePage);
		displayListType = DisplayList.MAIN;
		resetRecentChange();
	}

	// This method starts execution of each user command by first retrieving
	// all existing tasks stored and goes on to parse user command, to determine
	// which course of action to take.
	public Integer execCmd(String command) throws Exception {
		parseCommand(command);
		return recentChange;
	}

	public List<String> getCurrentList() {
		switch (displayListType) {
		case MAIN:
			return tasks.getNumberedPage(currentPageNum);

		case ARCHIVE:
			return archiveTasks.getNumberedPage(currentPageNum);

		case SEARCH:
			return resultTasks.getPage(currentPageNum);
		}
		return null;
	}
	
	public List<String> suggest(String content) {
		List<String> suggestList = new ArrayList<String>();
		
		//suggest commands
		for (String str : commandMap.keySet()) {
			if (str.indexOf(content) == 0) {
				suggestList.add(str);
			}
		}
		
		//suggest search
		TaskList resultList = processSearch(content);
		suggestList.addAll(resultList.getStringList());
		
		return suggestList;
	}

	private void setNumTaskOnPage(Integer number) {
		tasks.setNumTaskOnPage(number);
		archiveTasks.setNumTaskOnPage(number);
	}

	/**
	 * This method returns all the existing tasks in the list, if any.
	 * 
	 * @return void
	 * @author G. Vishnu Priya
	 */
	private void getFileContent() {
		tasks = new SimpleTaskList(storage.read());
		archiveTasks = new SimpleTaskList(storage.readArchive());
	}

	/**
	 * This method returns a storage object, storagePlus.
	 * 
	 * @return StoragePlus Object
	 * @author G. Vishnu Priya
	 */
	private Storage createStorageObject() {
		return new StoragePlus();
	}

	/**
	 * @author Luo Shaohuai
	 * @param taskList
	 */
	private void setDisplayList(DisplayList listType) {
		this.displayListType = listType;
		resetRecentChange();
	}

	private void setResultList(TaskList list) {
		this.resultTasks = list;
		resultTasks.setNumTaskOnPage(numTasksInSinglePage);
		resetRecentChange();
		setDisplayList(DisplayList.SEARCH);
	}

	private void resetRecentChange() {
		currentPageNum = 1;
		recentChange = 0;
	}

	private void setRecentChange(Task task, TaskList taskList) {
		taskList.sort();
		Integer index = taskList.indexOf(task);
		setRecentChange(index, taskList);
	}

	private void setRecentChange(Integer recent, TaskList taskList) {
		currentPageNum = taskList.getIndexPageContainTask(recent);
		recentChange = taskList.getIndexTaskOnPage(recent);
	}

	// This method gets the command type of user input and further processes the
	// input.
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

	// This method returns the type of operation to be carried out, either add,
	// delete, edit or display.
	private String getOperation(String command) {
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
			case CHANGEPAGE:
				changePage(content);
				break;
			default:
				throw new Exception("Invalid command.");
		}
	}

	private void moveToArchive() {
		setDisplayList(DisplayList.ARCHIVE);
	}

	// the format will be "done <number>"
	private void markAsDone(String content) throws Exception {
		String[] taskNumbers = content.split(" ");
		Arrays.sort(taskNumbers, new Comparator<String>() {
			public int compare(String first, String second) {
				return Integer.valueOf(second).compareTo(Integer.valueOf(first));
			}
		});
		
		for(int i = 0; i < taskNumbers.length; i++) {
			int taskID = Integer.parseInt(taskNumbers[i].trim()) - 1;
			// move task from task List to archive
			if (taskID >= 0 && taskID < tasks.size()) {
				Task task = tasks.get(taskID);
				archiveTasks.add(task);
				tasks.remove(taskID);
				
				if(i == taskNumbers.length - 1) {
					setRecentChange(taskID, tasks);
				}
			} else {
				throw new Exception("Invalid arguments");
			}
		}
		
		displayMainList();
	}

	// used for searching date
	// if this method returns null, it means that user is typing in a
	// description

	private Date timeParser(String input) {
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

	private void search(String content) {
		TaskList resultList = processSearch(content);
		//TODO: move search to tasklist class
		//TODO: add num in desc
		setResultList(resultList);
	}

	private TaskList processSearch(String content) {

		String desc = "";
		Integer singlePos = 0;
		Integer doublePos = 0;
		singlePos = content.indexOf('\'', 0);
		doublePos = content.indexOf('\"', 0);
		if (singlePos == -1 && doublePos == -1) {
			// return normal case, just single search(content);
			desc = content;
			return simpleSearch(content, tasks);
		} else {

			String regex = "([\"'])(?:(?=(\\\\?))\\2.)*?\\1";
			Matcher matcher = Pattern.compile(regex).matcher(content);
			while (matcher.find()) {
				desc += content.substring(matcher.start() + 1,
						matcher.end() - 1) + " ";
			}
			if (desc.length() > 0) {
				desc = desc.substring(0, desc.length() - 1);
				content = content.replaceAll(regex, "");
			}
			// now content is time

			return complexSearch(desc, content, tasks);
		}
	}

	// search for date and description
	// if the user types in one date only,
	// the software will understand as search for date
	private TaskList complexSearch(String desc, String content,
			TaskList listToSearch) {

		TaskList resultForTime = simpleSearch(content, listToSearch);
		// search for time first

		return simpleSearch(desc, resultForTime);

	}

	private TaskList simpleSearch(String content, TaskList listToSearch) {

		TaskList listToDisplay = null;

		Date date = timeParser(content);
		if (date == null) {
			listToDisplay = searchDesc(content, listToSearch);
		} else {
			String[] para = content.trim().split("\\s+");
			if (para[0].equalsIgnoreCase("by")) {

				listToDisplay = searchByDate(date, listToSearch);
			} else {
				listToDisplay = searchOnDate(date, listToSearch);
			}
		}

		return listToDisplay;
	}

	// search on the exact date
	private TaskList searchOnDate(Date deadline, TaskList listToSearch) {
		int numOfTask = listToSearch.size();
		TaskList resultList = new SimpleTaskList();

		for (int i = 0; i < numOfTask; i++) {
			Task task = listToSearch.get(i);
			if (task.getDeadline() != null) {

				if (compare(task.getDeadline(), deadline) == 0) {
					resultList.add(task);
				}
			}
		}

		return resultList;
	}

	private TaskList searchByDate(Date deadline, TaskList listToSearch) {
		int numOfTask = listToSearch.size();
		TaskList resultList = new SimpleTaskList();

		for (int i = 0; i < numOfTask; i++) {
			Task task = listToSearch.get(i);
			if (task.getDeadline() != null) {

				if (compare(task.getDeadline(), deadline) <= 0) {
					resultList.add(task);
				}
			}
		}

		return resultList;
	}

	// return negative if date1 is before date2
	// positive if date1 is after date2
	// 0 if they are the same

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
	 * 
	 * @return: the result list of task if the key appears in the list of task,
	 *          return the list of exact search else return the list of
	 *          nearMatch search
	 * @author: Tran Cong Thien
	 */

	private TaskList searchDesc(String keyWord, TaskList listToSearch) {
		TaskList resultList = exactSearch(keyWord, listToSearch);

		if (resultList.size() == 0) {
			// exactSearch list is empty
			return nearMatchSearch(keyWord, listToSearch);
		} else {
			return resultList;
		}

	}

	private TaskList exactSearch(String keyWord, TaskList listToSearch) {
		TaskList resultList = new SimpleTaskList();

		int numOfTask = listToSearch.size();
		for (int i = 0; i < numOfTask; i++) {
			if (isExact(keyWord.toLowerCase(), listToSearch.get(i).getDesc()
					.toLowerCase())) {
				resultList.add(listToSearch.get(i));
			}
		}

		return resultList;
	}

	private boolean isExact(String keyword, String strToSearch) {
		String[] str = keyword.trim().split("\\s+");
		int lenOfKey = str.length;
		int numOfMatch = 0;

		for (int i = 0; i < lenOfKey; i++) {
			// match exactly in the string
			if (strToSearch.indexOf(str[i]) != -1) {
				numOfMatch++;
			}
		}

		if (numOfMatch == lenOfKey) {
			return true;
		} else {
			return false;
		}
	}

	private TaskList nearMatchSearch(String key, TaskList listToSearch) {
		TaskList resultList = new SimpleTaskList();
		int numOfTask = listToSearch.size();
		String[] str = key.trim().split("\\s+");
		int keyLen = str.length;

		ArrayList<Triple> list = new ArrayList<Triple>();

		for (int i = 0; i < numOfTask; i++) {
			Task task = listToSearch.get(i);
			Pair result = searchScore(key.toLowerCase(), task.getDesc().trim()
					.toLowerCase());
			if (result.getFirst() > keyLen / 2) {
				if (result.getSecond() >= 500 * keyLen) {

					list.add(new Triple(result.getFirst(), result.getSecond(),
							task));
				}
			}
		}

		Collections.sort(list);

		for (int i = list.size() - 1; i >= 0; i--) {
			Task task = list.get(i).getThird();
			resultList.add(task);
		}

		return resultList;
	}

	private Pair searchScore(String keyword, String strToSearch) {
		String[] key = keyword.trim().split("\\s+");
		int strLen = key.length;
		int searchScore = 0;
		int numOfMatch = 0;

		for (int i = 0; i < strLen; i++) {
			if (matchScore(key[i], strToSearch) != 0) {
				numOfMatch++;
			}
			searchScore += matchScore(key[i], strToSearch);
		}

		return new Pair(numOfMatch, searchScore);
	}

	// keyword is one word only
	// return maxScore of matching of this keyword in the string
	private int matchScore(String key, String strToSearch) {

		String[] string = strToSearch.trim().split("\\s+");
		int strLen = string.length;
		int maxScore = 0;

		for (int i = 0; i < strLen; i++) {
			int score = approximateMatchScore(key, string[i]);
			if (maxScore < score) {
				maxScore = score;
			}
		}

		return maxScore;
	}

	// Criteria to be matched between 2 words, if the
	// editDistance/lenghOfKeyWord is <0.5
	// the 2 strings are considered approximately matched
	private int approximateMatchScore(String keyword, String string) {
		int editDist = editDistance(string, keyword);
		int lenOfKey = keyword.length();
		if (editDist / lenOfKey < 0.5)
			return 1000 - 1000 * editDist / lenOfKey;
		else
			return 0;

	}

	// the edit Distance score between 2 strings, used for nearMatch Search
	// the lower, the better
	// Tran Cong Thien
	private int editDistance(String sourceString, String destString) {
		int sourceStrLen = sourceString.length();
		int destStrLen = destString.length();

		// sourceString in for vertical axis
		// destString in the horizontal axis
		int[][] editDistance = new int[sourceStrLen + 1][destStrLen + 1];

		for (int i = 1; i <= sourceStrLen; i++) {
			editDistance[i][0] = i;
		}

		for (int j = 1; j <= destStrLen; j++) {
			editDistance[0][j] = j;
		}

		for (int j = 1; j <= destStrLen; j++) {
			for (int i = 1; i <= sourceStrLen; i++) {

				if (sourceString.charAt(i - 1) == destString.charAt(j - 1)) {
					editDistance[i][j] = editDistance[i - 1][j - 1];
				} else {
					editDistance[i][j] = Math.min(editDistance[i - 1][j] + 1,
							Math.min(editDistance[i][j - 1] + 1,
									editDistance[i - 1][j - 1] + 1));
				}
			}
		}

		return editDistance[sourceStrLen][destStrLen];
	}

	// return the list of tasks that are overdue at current time
	// Author: Tran Cong Thien
	private void overDue() {
		setResultList(tasks.getOverdueTasks());
	}

	private void updateForUndo() {
		updateUndoList();
		updateUndoArchiveList();
	}

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
			setDisplayList(DisplayList.MAIN);
			resetRecentChange();
		}
	}

	/*
	 * Postpones the desired task.
	 * 
	 * @author Koh Xian Hui
	 */
	private void postpone(String content) {
		try {
			String[] taskNumbers = content.split(" ");
		
			for(int i = 0; i < taskNumbers.length; i++) {
				Integer taskNum  = Integer.parseInt(taskNumbers[i]) - 1;
				Task postponedTask = tasks.get(taskNum);
				postponedTask.clearTimes();
				postponedTask.setType(TaskType.FLOATING);
				
				if(i == taskNumbers.length - 1) {
					setRecentChange(postponedTask, tasks);
				}
			}
			displayMainList();
		} catch (NumberFormatException e) {
			System.out.println("invalid number");
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
		tasks.sort();
		setDisplayList(DisplayList.MAIN);
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
	 * @throws Exception
	 */
	private void proceedWithEdit(String content) throws Exception {
		String[] words = content.split(" ");
		int positionOfTask = Integer.parseInt(words[0]) - 1;

		if (positionOfTask < 0 || positionOfTask >= tasks.size()
				|| words.length < 2) {
			throw new Exception("Invalid arguments");
		}

		String attributeToChange = words[1];
		String editDetails = "";
		for (int i = 2; i < words.length; i++) {
			editDetails += words[i] + " ";
		}
		if (!editDetails.isEmpty()) {
			editDetails = editDetails.substring(0, editDetails.length() - 1);
		}

		Task taskToEdit = tasks.get(positionOfTask);
		editAttribute(taskToEdit, attributeToChange, editDetails);

		displayMainList();
		setRecentChange(taskToEdit, tasks);
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
		}
		/*
		 * else if (attribute.equalsIgnoreCase("date")) {
		 * 
		 * } else if (attribute.equalsIgnoreCase("time")) {
		 * 
		 * } else if (attribute.equalsIgnoreCase("from")) {
		 * 
		 * 
		 * }
		 */
		else if (attribute.equalsIgnoreCase("!")) {
			editPriority(taskToEdit);
		} else {
			throw new Exception("Please specify what to edit (time/desc/!)");
		}
	}

	/**
	 * This methods edits the priority of a task so that the existing priority
	 * of the task is reversed.
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
			int taskNum = Integer.parseInt(content);
			executeDelete(taskNum);
			displayMainList();

			taskNum -= 1;
			if (taskNum >= tasks.size()) {
				setRecentChange(tasks.size() - 1, tasks);
			} else {
				setRecentChange(taskNum, tasks);
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
					"Task does not exist. Please enter task number within the range.");
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
			} else {
				throw new Exception("On first page.");
			}
		} else if (direction.equalsIgnoreCase("down")) {
			if (checkValidPageDown()) {
				currentPageNum++;
				recentChange = 0;
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
		// quick fix
		// TODO: getCurDisplayList() after fix search
		switch (displayListType) {
		case MAIN:
			totalNumPages = tasks.getTotalPageNum();
			break;
		case ARCHIVE:
			totalNumPages = archiveTasks.getTotalPageNum();
			break;
		default:
			totalNumPages = 0;
			break;
		}
		if (currentPageNum < totalNumPages) {
			return true;
		}
		return false;
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

		displayMainList();
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
