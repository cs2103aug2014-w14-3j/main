package controller;

import controller.Task.TaskType;
import storage.Storage;
import storage.StoragePlus;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.joestelmach.natty.*;
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
	
	enum CommandType {
		ADD, DELETE, EDIT, POSTPONE, DISPLAY,UNDO, SEARCH, DONE, CHANGEPAGE
	};

	private static final int POSITION_OF_OPERATION = 0;
	private static final int numTasksInSinglePage = 10;
	
	
	private static Controller theController = null;
	private ArrayList<Task> tasks;
	private ArrayList<String> taskStrings;
	private ArrayList<Task> archiveTasks;
	private ArrayList<String> archiveTaskStrings;
	private ArrayList<String> displayList;
	private Storage storage;
	private Stack<ArrayList<Task>> undoList;
	private Stack<ArrayList<Task>> undoArchiveList;
 	private static int totalNumPages;
	private static int currentPageNum;
	private static int numFirstTaskOnPage;
	private static int numLastTaskOnPage;

	public ControllerClass() {
		storage = createStorageObject();
		tasks = new ArrayList<Task>();
		archiveTasks = new ArrayList<Task>();
		displayList = new ArrayList<String>();
		undoList=new Stack<ArrayList<Task>>();
		undoArchiveList=new Stack<ArrayList<Task>>();
		getFileContent();
		totalNumPages = (int)Math.ceil((double)(taskStrings.size())/(numTasksInSinglePage));
		currentPageNum = 1;
		updateTaskNumOnPage();
		
	}

	// This method starts execution of each user command by first retrieving
	// all existing tasks stored and goes on to parse user command, to determine
	// which course of action to take.
	public ArrayList<String> execCmd(String command) throws Exception {
		parseCommand(command);
		return displayList;
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
		archiveTaskStrings = storage.readArchive();
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
			for (int i = 0; i < archiveTaskStrings.size(); i++) {
				archiveTasks.add(convertStringToTask(archiveTaskStrings.get(i)));
			}
		} catch (ParseException e) {
			// nothing
		}
	}

	private Task convertStringToTask(String taskString) throws ParseException {
		 
		return new TaskClass(taskString);
	}

	// This method converts tasks from tasks list to taskStrings list.
	private void convertTaskListStringList() {
		taskStrings.clear();
		for (int i = 0; i < tasks.size(); i++) {
			taskStrings.add(convertTaskToString(tasks.get(i)));
		}
		
		archiveTaskStrings.clear();
		for (int i = 0; i < archiveTasks.size(); i++) {
			archiveTaskStrings.add(convertTaskToString(archiveTasks.get(i)));
		}
	}

	// This method converts tasks to strings to be stored in taskStrings list.
	private String convertTaskToString(Task task) {
		return task.toString();
	}

	// This method gets the command type of user input and further processes the
	// input.
	private void parseCommand(String command) throws Exception {
		String operation = getOperation(command);
		CommandType commandType = matchCommandType(operation);
		String content = removeCommandType(command, operation);
		processInput(commandType, content);
		convertTaskListStringList();
		updateStorage();
		addTaskNum();
	}

	/**
	 * Appends the task number to the beginning of each string in displayList.
	 * @return void
	 * @author G. Vishnu Priya
	 */
	private void addTaskNum() {
		totalNumPages = (int)Math.ceil((double)(taskStrings.size())/(numTasksInSinglePage));
		displayList.clear();
		displayList.add(Integer.toString(totalNumPages));
		updateTaskNumOnPage();
		copySectionTaskStringsDisplayList();
		addNumDisplayList();
	}

	private void addNumDisplayList() {
		for (int i=1; i<displayList.size(); i++) {
			String numberedTask = Integer.toString(i) + "%" + displayList.get(i);
			displayList.set(i, numberedTask);
		}
	}

	/**
	 * This method copies the specific strings to be displayed out of the whole list, to the display list.
	 * @return void
	 * @author G. Vishnu Priya
	 */
	private void copySectionTaskStringsDisplayList() {
		for(int i=numFirstTaskOnPage; i<=numLastTaskOnPage; i++) {
			displayList.add(taskStrings.get(i-1));
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
				display();
				break;
			case CHANGEPAGE:
				changePage(content);
				break;
			case POSTPONE:
				updateForUndo();
				postpone(content);
				break;
			case DONE:
				updateForUndo();
				markAsDone(content);
				break;
			default:
				throw new Exception("Invalid command.");
			}
	}
	
	
	/**
	 * This method changes the attributes related to the changing of a page.
	 * @return void
	 * @author G. Vishnu Priya
	 * @throws Exception 
	 */
	private void changePage(String content) throws Exception {
		String direction = content.trim();
		changeCurrentPageNum(direction);
		updateTaskNumOnPage();
	}
	
	private void updateTaskNumOnPage() {
		numFirstTaskOnPage = (numTasksInSinglePage * (currentPageNum-1)) + 1;
		numLastTaskOnPage = getNumLastTaskOnPage();
	}

	/**
	 * This method returns the number of the last task on the current page.
	 * @return int
	 * @author G. Vishnu Priya
	 */
	private int getNumLastTaskOnPage() {
		if(totalNumPages == 0) {
			return 0;
		}
		if (currentPageNum < totalNumPages) {
			 return numTasksInSinglePage * currentPageNum;
		} else {
			int remainder = tasks.size() % numTasksInSinglePage;
			if (remainder==0) {
				return numTasksInSinglePage + ((currentPageNum-1) * numTasksInSinglePage);
			} else {
			return remainder + ((currentPageNum-1) * numTasksInSinglePage);
			}
		}
	}
	
	/**
	 * This method checks if it is valid to change the current page number and if so, changes the current page number.
	 * @throws Exception
	 * @return void
	 * @author G. Vishnu Priya
	 */
	private void changeCurrentPageNum(String direction) throws Exception {
		if(direction.equalsIgnoreCase("up")) {
			if (checkValidPageUp()) {
				currentPageNum--;
			} else {
				throw new Exception("On first page.");
			}
		} else {
			if (checkValidPageDown()) {
			currentPageNum++;
			} else {
				throw new Exception("On last page.");
			}
		}
	}

	/**
	 * This method checks if it is possible to go to the next page by checking if there are more tasks in the list which are pushed to the next page.
	 * @return boolean
	 * @author G. Vishnu Priya
	 */
	private boolean checkValidPageDown() {
		if (currentPageNum<= totalNumPages) {
		return true;
		}
		return false;
	}

	/**
	 * This method checks if it is possible to go to the previous page. If currently on first page, it will return false.
	 * Otherwise, it will return true.
	 * @return boolean
	 * @author G. Vishnu Priya
	 */
	private boolean checkValidPageUp() {
		if (currentPageNum == 1) {
		return false;
		} 
		
		return true;
	}

	// the format will be "done <number>"
	private void markAsDone(String content){
	
		int taskID=getTaskNum(content.trim())-1;
		
		
		//move task from task List to archive
		if (taskID>=0 && taskID<tasks.size()){
			Task task=tasks.get(taskID);
			archiveTasks.add(task);
			tasks.remove(taskID);
			
		}

	}
	
	
	
			/**
	 * 
	 * @return: the result list of task
	 * if the key appears in the list of task, return the list of exact search
	 * else return the list of nearMatch search
	 * @author: Tran Cong Thien
	 */
	
	private  ArrayList<Task> search(String key) {
		ArrayList<Task> resultList=new ArrayList<Task>();
		int numOfTask=tasks.size();
		String[] str=key.trim().split("\\s+");
		int keyLen=str.length;
		
		ArrayList<Triple> list=new ArrayList<Triple>();
		
		for (int i=0;i<numOfTask;i++)
		{
			Task task=tasks.get(i);
			Pair result=searchScore(key,task.getDesc() );
			if (result.getFirst()>keyLen/2){
				list.add(new Triple(result.getFirst(),result.getSecond(),task));
			}
		}
		
		Collections.sort(list);
		
		for (int i=list.size() - 1;i >=0;i--){
			Task task=list.get(i).getThird();
			resultList.add(task);
		}
		
		return resultList;
	}
	
	
	private Pair searchScore(String keyword, String strToSearch){
		String[] key=keyword.trim().split("\\s+");
		int strLen=key.length;
		int searchScore=0;
		int numOfMatch=0;
		
		for (int i=0;i<strLen;i++){
			if(matchScore(key[i],strToSearch)!=0){
				numOfMatch++;
			}
			searchScore+=matchScore(key[i],strToSearch);
		}
		
		return new Pair(numOfMatch,searchScore);
	}
	
	//keyword is one word only
	//return maxScore of matching of this keyword in the string
	private int matchScore(String key, String strToSearch){
	
		String[] string=strToSearch.trim().split("\\s+");
		int strLen=string.length;
		int maxScore=0;
		
		for (int i=0;i<strLen;i++){
			int score=approximateMatchScore(key,string[i]);
			if (maxScore< score){
				maxScore=score;
			}
		}
		
		return maxScore;
	}
	
	
	//Criteria to be matched between 2 words, if the editDistance/lenghOfKeyWord is <=0.5
	//the 2 strings are considered approximately matched
	private int approximateMatchScore(String keyword, String string){
		int editDist=editDistance(keyword,string);
		int lenOfKey=keyword.length();
		if (editDist/lenOfKey <=0.5)
			return 1000-1000*editDist/lenOfKey;
		else
			return 0;
		
	}
	
	
	//the edit Distance score between 2 strings, used for nearMatch Search
	//the lower, the better
	//Tran Cong Thien
	private int editDistance(String sourceString, String destString){
		int sourceStrLen=sourceString.length();
		int destStrLen=destString.length();
		
		
		//sourceString in for vertical axis
		//destString in the horizontal axis
		int[][] editDistance=new int[sourceStrLen+1][destStrLen+1];
		
		for (int i=1;i<=sourceStrLen;i++){
			editDistance[i][0]=i;
		}
		
		for (int j=1;j<=destStrLen;j++){
			editDistance[0][j]=j;
		}
		
		for (int j=1;j<=destStrLen;j++){
			for (int i=1;i<=sourceStrLen;i++){
		
				if (sourceString.charAt(i-1)==destString.charAt(j-1)){
					editDistance[i][j]=editDistance[i-1][j-1];
				} else {
					editDistance[i][j]=Math.min(editDistance[i-1][j]+1, Math.min(editDistance[i][j-1]+1,editDistance[i-1][j-1]+1));
				}
		}
	}
		
	 return editDistance[sourceStrLen][destStrLen];
	}
	
	
	private void updateForUndo(){
		updateUndoList();
		updateUndoArchiveList();
	}
	
	
	private void updateUndoArchiveList(){
		ArrayList<Task> item=new ArrayList<Task>();
		
		for (int i=0;i<archiveTasks.size();i++)
			item.add(archiveTasks.get(i));
		
		undoArchiveList.push(item);
	}
	
	//push the current state to the undoList
	//Tran Cong Thien
	private void updateUndoList(){
		ArrayList<Task> item=new ArrayList<Task>();
		//copy content of tasks to item
		for (int i=0;i<tasks.size();i++)
			item.add(tasks.get(i));
		
		undoList.push(item);
	}
	
	//undo command, the tasks will be replaced by the previous state
	//Tran Cong Thien
	private void undo(){
		//if there is states to undo
		if(!undoList.empty()){
			tasks=undoList.pop();
			archiveTasks=undoArchiveList.pop();
		}
	}
	
	/*
	 *Postpones the desired task.
	 *
	 * @author Koh Xian Hui
	 */
	private void postpone(String taskNum) {
		try {
			Task postponedTask = tasks.get(getTaskNum(taskNum) - 1);
			postponedTask.clearTimes();
			postponedTask.setType(TaskType.FLOATING);
		} catch (NumberFormatException e){
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
	private ArrayList<String> display() {
		ArrayList<String> displayTasks = new ArrayList<String>();
		if (!tasks.isEmpty()) {
			sortTasks();
			for (Task taskItem : tasks) {
				String stringedTask = taskItem.toString().replace("%", " ");
				
				if(stringedTask.startsWith("!")) {
					displayTasks.add(stringedTask.substring(1));
				} else {
					displayTasks.add(stringedTask);
				}
			}
		}

		return displayTasks;
	}
	
	/**
	 * 
	 */
	private void sortTasks() {
		Collections.sort(tasks, (task1, task2) -> {
			if(task1.isPrioritized() && !task2.isPrioritized()) {
				return -1;
			} else if(!task1.isPrioritized() && task2.isPrioritized()) {
				return 1;
			} 
			
			if(task1.getStartTime() == null && task2.getStartTime() != null) {
				return 1;
			} else if(task1.getStartTime() != null && task2.getStartTime() == null) {
				return -1;
			} 
			
			if(task1.getStartTime() == null && task2.getStartTime() == null) {
				return task1.getDesc().compareTo(task2.getDesc());
			} else {
				Long thisDate = task1.getStartTime().getTime();
				Long taskDate = task2.getStartTime().getTime();
				
				return thisDate.compareTo(taskDate);
			}

		});
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
		String editDetails;
		if (words.length >= 3) {
			editDetails = content.substring(content.indexOf(words[2]));
		} else {
			editDetails = null;
		}
		Task taskToEdit = tasks.get(positionOfTask);
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
	private void editAttribute(Task taskToEdit, String attribute,
			String editDetails) {
		if (attribute.equalsIgnoreCase("desc")) {
			editDescription(taskToEdit, editDetails);
		} else if (attribute.equalsIgnoreCase("time")) {
			processTime(taskToEdit, editDetails);
		}
		/*else if (attribute.equalsIgnoreCase("date")) {
			
		} else if (attribute.equalsIgnoreCase("time")) {
			
		} else if (attribute.equalsIgnoreCase("from")) {

				
		} */
		else {
			editPriority(taskToEdit);
		}
	}
	/**
	 * This methods edits the priority of a task so that the existing priority of the task is reversed.
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

/*	private Task editStartEndTimes(Task taskToEdit, String details) {
=======
	/*
	private Task editStartEndTimes(Task taskToEdit, String details) {
>>>>>>> 9c45c50f5f0174cff1a320c6b11a2a7d32a16649
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
*/

	/**
	 * Replaces the description of task with the desc string.
	 * 
	 * @param taskToEdit
	 * @param details
	 * @return Task Object
	 * @author G. Vishnu Priya
	 */
	private void editDescription(Task taskToEdit, String desc) {
		taskToEdit.setDesc(desc);
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
		storage.writeArchive(archiveTaskStrings);
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
		int numEntered = Integer.parseInt(content);
		int numInTasksList = (numTasksInSinglePage*(currentPageNum-1)) + numEntered;
		
		return numInTasksList;
	}

	/**
	 * @author Luo Shaohuai
	 * @param content
	 * @throws Exception
	 */
	private void addTask(String content) throws Exception {
			if (isEmptyCommand(content)) {
				throw new Exception("Please specify what to add.");
			} else {
				Task task = processUserInput(content);
				this.tasks.add(task);
			}
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
			//return processUserInputClassic(content);
			desc = content + " ";
			content = "";
		}
		
		String regex = "([\"'])(?:(?=(\\\\?))\\2.)*?\\1";
		Matcher matcher = Pattern.compile(regex).matcher(content);
		while (matcher.find()) {
			desc += content.substring(matcher.start() + 1, matcher.end() - 1) + " ";
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
		for(DateGroup group : groups) {
			dates.addAll(group.getDates());
		}
		
		Collections.sort(dates);
		
		if(dates.size() < 1) {
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
		if (operation.equalsIgnoreCase("add")) {
			return CommandType.ADD;
		} else if (operation.equalsIgnoreCase("delete")) {
			return CommandType.DELETE;
		} else if (operation.equalsIgnoreCase("edit")) {
			return CommandType.EDIT;
		} else if ((operation.equalsIgnoreCase("display"))
				|| (operation.equalsIgnoreCase("list"))) {
			return CommandType.DISPLAY;
		} else if (operation.equalsIgnoreCase("undo")) {
			return CommandType.UNDO;
		} else if (operation.equalsIgnoreCase("done")) { 
			return CommandType.DONE;
		} else if(operation.equalsIgnoreCase("page")) {
			return CommandType.CHANGEPAGE;
		} else if(operation.equalsIgnoreCase("pp")) {
			return CommandType.POSTPONE;
		}
		else {
			return CommandType.SEARCH;
		}
	}
	
	
	public static Controller getInstance() {
		if (theController == null) {
			theController = new ControllerClass();
		}
		
		return theController;
	}
	
}
