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
	public static final String CMD_FREE="find";
	enum CommandType {
		ADD, DELETE, EDIT, POSTPONE, DISPLAY, UNDO, ARCHIVE, SEARCH, DONE, CHANGEPAGE, OVERDUE, FREETIME
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
		aMap.put(CMD_FREE, CommandType.FREETIME);
		commandMap = Collections.unmodifiableMap(aMap);
	}

	private static final int POSITION_OF_OPERATION = 0;
	private static final int maxNumOfUndo = 40;
	private static final int numTasksInSinglePage = 10;

	private static Controller theController = null;
	private TaskList tasks;
	private TaskList archiveTasks;
	private TaskList resultTasks;
	private boolean[][] timeSlots;
	
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

		// suggest commands
		for (String str : commandMap.keySet()) {
			if (str.indexOf(content) == 0) {
				suggestList.add(str);
			}
		}

		// suggest search
		TaskList resultList = tasks.search(content);
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
			
		case FREETIME:
			freeTime(content);
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
				return Integer.valueOf(second)
						.compareTo(Integer.valueOf(first));
			}
		});

		for (int i = 0; i < taskNumbers.length; i++) {
			int taskID = Integer.parseInt(taskNumbers[i].trim()) - 1;
			// move task from task List to archive
			if (taskID >= 0 && taskID < tasks.size()) {
				Task task = tasks.get(taskID);
				archiveTasks.add(task);
				tasks.remove(taskID);

				if (i == taskNumbers.length - 1) {
					setRecentChange(taskID, tasks);
				}
			} else {
				throw new Exception("Invalid arguments");
			}
		}

		displayMainList();
	}
	
	
	
	private void freeTime(String content) throws Exception{
		
			Pair pair=parserForFind(content);
			int hh=pair.getFirst();
			int mm=pair.getSecond();
			
			ArrayList<Date> resultList=findFreeTime(hh,mm);
			
			for (int i=0;i<resultList.size();i++){
				System.out.println(resultList.get(i));
			}
			
			System.out.println("End of search");
	
	}
	
	// xx hours, xx hours yy mins/minute
	//yy minutes
	
	private Pair parserForFind(String content) throws Exception{
		
		String[] para=content.trim().split("\\s+");
		
		int len=para.length;
		
		if (len==4){
			return new Pair(Integer.parseInt(para[0]),Integer.parseInt(para[2]));
		} else if (len==2){
			if (para[1].equalsIgnoreCase("hours") ||para[1].equalsIgnoreCase("hour")){
				return new Pair(Integer.parseInt(para[0]),0);
			} else if (para[1].equalsIgnoreCase("minutes") ||para[1].equalsIgnoreCase("minutes")
					||para[1].equalsIgnoreCase("mins") ||para[1].equalsIgnoreCase("min")){
				return new Pair(0,Integer.parseInt(para[0]));
			} else {
				throw new Exception("Please specify time");
			}
	
		}else {
			throw new Exception("Please specify time");
		}
	}
		
	
	private ArrayList<Date> findFreeTime(int hours, int mins){
		int numOfSlots=numOfSlotsNeed(hours,mins);
		
		initTimeSlots();
		processDate();
		ArrayList<Integer> indexList=findEmptySlots(numOfSlots);
		return dateList(indexList);
		
	}
	
	
	
	private ArrayList<Date> dateList(ArrayList<Integer> number){
		
		ArrayList<Date> resultList=new ArrayList<Date>();
		
		for (int i=0;i<number.size();i++){
			
			int num=number.get(i);
			
			Calendar cal=Calendar.getInstance();
			cal.add(Calendar.DATE,num);
			
			resultList.add(cal.getTime());
		}
		
		return resultList;
	}
	private void initTimeSlots(){
		timeSlots=new boolean[30][144];
		
		
		//avoid time from 0.00am to 7.00am
		for (int i=0;i<30;i++){
			for (int j=0;j<144;j++){
				if (j>=0 && j<42){
					timeSlots[i][j]=false;
				}else{
					timeSlots[i][j]=true;
				}
			}
		}
		
	
	}
	
	
	private int numOfSlotsNeed(int hour, int min){
		int totalMin=hour*60+min;
		return (int) Math.ceil(totalMin/10);
		
	}
	
	private ArrayList<Integer> findEmptySlots(int numOfSlot){
		ArrayList<Integer> list=new ArrayList<Integer>();
		
		for (int i=0;i<30;i++){
			if (hasEmpty(numOfSlot,i)){
				list.add(i);
			}
		}
			
		return list;
	}
	
	private boolean hasEmpty(int numOfSlot,int dateIndex){
		
		int count;
		for (int i=0;i<144;i++){
			if (timeSlots[dateIndex][i]==true){
				count=1;
				boolean exit=false;
				for (int j=i+1;j<144 && !exit;j++){
					if (timeSlots[dateIndex][j]==true)
					{
						count++;
						if (count>=numOfSlot)
							return true;
					}
					else 
						exit=true;
				}
			}
		}
		
		return false;
	}
	
	
	private void processDate(){
		int numOfTask=tasks.size();
		Date now=new Date();
		
		Calendar lastDay=Calendar.getInstance();
		lastDay.add(Calendar.DATE, 29);
		
		Date lastday=lastDay.getTime();
		
		
		for (int i=0;i<numOfTask;i++){
			Task task=tasks.get(i);
			if (task.getType()==TaskType.TIMED){
				Date startTime=task.getStartTime();
				Date endTime=task.getEndTime();
				
				if (compare(now,startTime) <=0 && compare(endTime,lastday) <=0){
					occupySlot(startTime,endTime);
				}
			}
		}
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
	
	private void occupySlot(Date start,Date end){
		int dateIndex=getDateIndex(start);
		int timeIndex=getTimeIndex(start);
	
		int dateEndIndex=getDateIndex(end);
		int timeEndIndex=getTimeIndex(end);
		int numOfSlots=numSlots(start,end);
		
		if (dateIndex!=dateEndIndex){
			for (int i=dateIndex;i<144;i++){
				timeSlots[dateIndex][i]=false;
			}
			
			for (int i=dateIndex+1;i<dateEndIndex-1;i++){
				for (int j=0;j<144;j++){
					timeSlots[i][j]=false;
				}
			}
			
			for (int i=0;i<=timeEndIndex;i++){
				timeSlots[dateEndIndex][i]=false;
			}
			
		} else {
			for (int i=timeIndex;i<timeIndex+numOfSlots;i++)
				timeSlots[dateIndex][i]=false;
		}
		
		
	}
	private int getTimeIndex(Date date){
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		int hours = cal.get(Calendar.HOUR_OF_DAY);
		int minutes = cal.get(Calendar.MINUTE);
		
		return hours*6+minutes/10;
		
	}
	//return i if date is the i-th date from now
	//-1 if date is before now
	private int getDateIndex(Date date){
		Date now=new Date();
		
		long diff=date.getTime()-now.getTime();
		if (diff >=0){
			return (int) Math.floor(diff/(1000*60*60*24));
		}else{
			return -1;
		}
	}
	
	private int numSlots(Date start, Date end){
		
		long diff=end.getTime()-start.getTime();
		
		int numOfSlots=(int) Math.ceil(diff/(1000*60*10));
		
		return numOfSlots;
		
	}
	


	private void search(String content) {
		TaskList resultList = tasks.search(content);
		setResultList(resultList);
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

			for (int i = 0; i < taskNumbers.length; i++) {
				Integer taskNum = Integer.parseInt(taskNumbers[i]) - 1;
				Task postponedTask = tasks.get(taskNum);
				postponedTask.clearTimes();
				postponedTask.setType(TaskType.FLOATING);

				if (i == taskNumbers.length - 1) {
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
		Task taskToEdit = tasks.get(positionOfTask);
		
		if(isMultipleEditPriority(attributeToChange)) {
			for (int i=0; i<words.length-1;i++) {
				Task task = tasks.get(Integer.parseInt(words[i])-1);
				editPriority(task);
				if(i== words.length-2) {
					setRecentChange(task, tasks);
				}
			}
		} else {
		
		String editDetails = "";
		for (int i = 2; i < words.length; i++) {
			editDetails += words[i] + " ";
		}
		if (!editDetails.isEmpty()) {
			editDetails = editDetails.substring(0, editDetails.length() - 1);
		}

		editAttribute(taskToEdit, attributeToChange, editDetails);
		setRecentChange(taskToEdit, tasks);
		}
		displayMainList();
		
	}

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

			String[] taskNumbers = content.split(" ");
			List<Integer> taskNumDescending = new ArrayList<Integer>();
			for (int i=0; i< taskNumbers.length; i++) {
				int taskNum = Integer.parseInt(taskNumbers[i]);
				taskNumDescending.add(taskNum);
			}
			Collections.sort(taskNumDescending, Collections.reverseOrder());
			
			for (int i = 0; i < taskNumDescending.size(); i++) {
				int taskNum = taskNumDescending.get(i);
				executeDelete(taskNum);
				taskNum -= 1;
				if (taskNum >= tasks.size()) {
					setRecentChange(tasks.size() - 1, tasks);
				} else {
					setRecentChange(taskNum, tasks);
				}
			}
			displayMainList();

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
		case SEARCH:
			totalNumPages = resultTasks.getTotalPageNum();
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
