
package controller;

import controller.Task.TaskType;
import storage.Storage;
import storage.StoragePlus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
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
		ADD, DELETE, EDIT, POSTPONE, DISPLAY,UNDO, ARCHIVE, SEARCH, DONE
	};

	private static final int POSITION_OF_OPERATION = 0;
	private static final int maxNumOfUndo=40;
	
	private static Controller theController = null;
	private ArrayList<Task> tasks;
	private ArrayList<Task> archiveTasks;
	
	private ArrayList<String> displayList;
	private Integer recentChange;
	
	private Storage storage;
	private FixedSizeStack<ArrayList<Task>> undoList;
	private FixedSizeStack<ArrayList<Task>> undoArchiveList;
	private FixedSizeStack<Integer> undoRecentChanges;


	public ControllerClass() {
		storage = createStorageObject();
		tasks = new ArrayList<Task>();
		archiveTasks = new ArrayList<Task>();
		recentChange = 0;
		undoList=new FixedSizeStack<ArrayList<Task>>(maxNumOfUndo);
		undoArchiveList=new FixedSizeStack<ArrayList<Task>>(maxNumOfUndo);
		undoRecentChanges = new FixedSizeStack<Integer>(maxNumOfUndo);
		getFileContent();		
	}

	// This method starts execution of each user command by first retrieving
	// all existing tasks stored and goes on to parse user command, to determine
	// which course of action to take.
	public Integer execCmd(String command) throws Exception {
		parseCommand(command);
		return recentChange;
	}
	
	public ArrayList<String> getCurrentList() {
		return displayList;
	}


	/**
	 * This method returns all the existing tasks in the list, if any.
	 * 
	 * @return void
	 * @author G. Vishnu Priya
	 */
	private void getFileContent() {
		ArrayList<String> stringList;
		stringList = storage.read();
		tasks = convertStringListTaskList(stringList);
		stringList = storage.readArchive();
		archiveTasks = convertStringListTaskList(stringList);
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

	private ArrayList<Task> convertStringListTaskList(ArrayList<String> taskStrings) {
		ArrayList<Task> result = new ArrayList<Task>();
		
		for (String str : taskStrings) {
			result.add(convertStringToTask(str));
		}
		
		return result;
	}

	private Task convertStringToTask(String taskString) {
		return new TaskClass(taskString);
	}

	// This method converts tasks from tasks list to taskStrings list.
	private ArrayList<String> convertTaskListStringList(ArrayList<Task> taskList) {
		ArrayList<String> taskStrings = new ArrayList<String>();
		for (Task task : taskList) {
			taskStrings.add(convertTaskToString(task));
		}
		
		return taskStrings;
	}

	// This method converts tasks to strings to be stored in taskStrings list.
	private String convertTaskToString(Task task) {
		return task.toString();
	}
	
	/**
	 * @author Luo Shaohuai
	 * @param taskList
	 */
	private void setDisplayList(ArrayList<Task> taskList) {
		displayList = convertTaskListStringList(taskList);
		recentChange = 0;
	}
	
	private void setRecentChange(Task task, ArrayList<Task> taskList) {
		recentChange = taskList.indexOf(task);
	}
	
	private void setRecentChange(Integer recent) {
		recentChange = recent;
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
				displayMainList();
				break;
			case ARCHIVE:
				moveToArchive();
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
	
	
	
	private void moveToArchive(){
		setDisplayList(archiveTasks);
		
	}
	// the format will be "done <number>"
	private void markAsDone(String content) throws Exception{
		int taskID=Integer.parseInt(content.trim())-1;
		//move task from task List to archive
		if (taskID>=0 && taskID<tasks.size()){
			Task task=tasks.get(taskID);
			archiveTasks.add(task);
			tasks.remove(taskID);
			displayMainList();
			setRecentChange(taskID);
		} else {
			throw new Exception("Invalid arguments");
		}
	}
	
	
	//used for searching date
	//if this method returns null, it means that user is typing in a description
	
	private Date timeParser(String input){
		Parser parser = new Parser();
		List<DateGroup> groups = parser.parse(input);
		List<Date> dates = new ArrayList<Date>();
		for(DateGroup group : groups) {
			dates.addAll(group.getDates());
		}
		
		if (dates.size()==1){
			return dates.get(0);
		}else {
			return null;
		}
		
	}
	
	
	//search for date and description
	// if the user types in one date only, 
	//the software will understand as search for date
	private void search(String content){
		ArrayList<Task> listToDisplay=null;
		Date date=timeParser(content);
		if (date==null) {
			listToDisplay= searchDesc(content);
		} else {
			listToDisplay= searchOnDate(date);
		}
		
		setDisplayList(listToDisplay);
	}
	
	
	//Author: Tran Cong Thien
	//return true if the 2 dates are the same date
	//return false if not
	private boolean isSameDate(Date date1, Date date2){
		
		Calendar cal1=Calendar.getInstance();
		cal1.setTime(date1);
		
		Calendar cal2=Calendar.getInstance();
		cal2.setTime(date2);
		
		if (cal1.get(Calendar.YEAR)==cal2.get(Calendar.YEAR) 
			&& cal1.get(Calendar.MONTH)==cal2.get(Calendar.MONTH)
			&& cal1.get(Calendar.DAY_OF_MONTH)==cal2.get(Calendar.DAY_OF_MONTH)){
			return true;
		}else {
			return false;
		}
	}
	
	//search on the exact date
	private ArrayList<Task> searchOnDate(Date deadline){
		int numOfTask=tasks.size();
		ArrayList<Task> resultList=new ArrayList<Task>();
		
		for (int i=0;i<numOfTask;i++){
			Task task=tasks.get(i);
			if (task.getDeadline()!=null ) {
			
			   if (isSameDate(task.getDeadline(),deadline)){
				    resultList.add(task);
			   }
		    }
		}
		
		return resultList;
	}
	
	
	
	
	private ArrayList<Task> searchByDate(Date deadline){
		int numOfTask=tasks.size();
		ArrayList<Task> resultList=new ArrayList<Task>();
		
		for (int i=0;i<numOfTask;i++){
			Task task=tasks.get(i);
			if (task.getDeadline().compareTo(deadline)<=0){
				resultList.add(task);
			}
		}
		
		return resultList;
	}
	
	/**
	 * 
	 * @return: the result list of task
	 * if the key appears in the list of task, return the list of exact search
	 * else return the list of nearMatch search
	 * @author: Tran Cong Thien
	 */
	
	private ArrayList<Task> searchDesc(String keyWord){
		ArrayList<Task> resultList=exactSearch(keyWord);
		
		if (resultList.size()==0){
			//exactSearch list is empty
			return nearMatchSearch(keyWord);
		} else {
			return resultList;
		}
		
	}
	
	private ArrayList<Task> exactSearch(String keyWord){
		ArrayList<Task> resultList=new ArrayList<Task>();
		
		int numOfTask=tasks.size();
		for (int i=0;i<numOfTask;i++){
			if (isExact(keyWord.toLowerCase(),tasks.get(i).getDesc().toLowerCase())){
				resultList.add(tasks.get(i));
			}
		}
		
		return resultList;
	}
	
	
	private boolean isExact(String keyword, String strToSearch){
		String[] str=keyword.trim().split("\\s+");
		int lenOfKey=str.length;
		int numOfMatch=0;
		
		for (int i=0;i<lenOfKey;i++){
			//match exactly in the string
			if (strToSearch.indexOf(str[i]) !=-1){
				numOfMatch++;
			}
		}
		
		if (numOfMatch==lenOfKey){
			return true;	
		}else{
			return false;
		}
	}
	
	private  ArrayList<Task> nearMatchSearch(String key) {
		ArrayList<Task> resultList=new ArrayList<Task>();
		int numOfTask=tasks.size();
		String[] str=key.trim().split("\\s+");
		int keyLen=str.length;
		
		ArrayList<Triple> list=new ArrayList<Triple>();
		
		for (int i=0;i<numOfTask;i++)
		{
			Task task=tasks.get(i);
			Pair result=searchScore(key.toLowerCase(),task.getDesc().trim().toLowerCase() );
			if (result.getFirst()> keyLen/2){
				if (result.getSecond()>=500*keyLen) {
	
					list.add(new Triple(result.getFirst(),result.getSecond(),task));
				}
			}
		}
		
		Collections.sort(list);
		
		for (int i=list.size() - 1;i >=0;i--){
			Task task=list.get(i).getThird();
			resultList.add(task);
		}
		
		return resultList;
		//setDisplayList(resultList);
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
	
	
	//Criteria to be matched between 2 words, if the editDistance/lenghOfKeyWord is <0.5
	//the 2 strings are considered approximately matched
	private int approximateMatchScore(String keyword, String string){
		int editDist=editDistance(string,keyword);
		int lenOfKey=keyword.length();
		if (editDist/lenOfKey <0.5)
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
		updateUndoRecentChanges();
	}
	
	
	private void updateUndoRecentChanges() {
		undoRecentChanges.push(recentChange);
	}

	private void updateUndoArchiveList(){
		ArrayList<Task> item=new ArrayList<Task>();
		
		for (int i=0;i<archiveTasks.size();i++)
			item.add(
					cloneTask(archiveTasks.get(i))
					);
		
		undoArchiveList.push(item);
	}
	
	//push the current state to the undoList
	//Tran Cong Thien
	private void updateUndoList(){
		ArrayList<Task> item=new ArrayList<Task>();
		//copy content of tasks to item
		for (int i=0;i<tasks.size();i++)
			item.add(
				cloneTask(tasks.get(i))
				);
		
		undoList.push(item);
	}
	
	private Task cloneTask(Task task) {
		return new TaskClass(task.toString());
	}
	
	//undo command, the tasks will be replaced by the previous state
	//Tran Cong Thien
	private void undo(){
		//if there is states to undo
		if(!undoList.empty()){
			tasks=undoList.pop();
			archiveTasks=undoArchiveList.pop();
			setDisplayList(tasks);
		}
	}
	
	/*
	 *Postpones the desired task.
	 *
	 * @author Koh Xian Hui
	 */
	private void postpone(String content) {
		try {
			Integer taskNum = Integer.parseInt(content) - 1;
			Task postponedTask = tasks.get(taskNum);
			postponedTask.clearTimes();
			postponedTask.setType(TaskType.FLOATING);
			displayMainList();
			setRecentChange(postponedTask, tasks);
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
	private void displayMainList() {
		sortTasks(tasks);
		setDisplayList(tasks);
	}
	
	/**
	 * 
	 */
	private void sortTasks(ArrayList<Task> taskList) {
		Collections.sort(taskList, (task1, task2) -> {
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
	 * @throws Exception 
	 */
	private void proceedWithEdit(String content) throws Exception {
		String[] words = content.split(" ");
		int positionOfTask = Integer.parseInt(words[0]) - 1;
		
		if (positionOfTask < 0 
				|| positionOfTask >= tasks.size() 
				|| words.length < 2) {
			throw new Exception("Invalid arguments");
		}
		
		String attributeToChange = words[1];
		String editDetails = "";
		for (int i = 2; i< words.length; i++) {
			editDetails += words[i] + " ";
		}
		if (!editDetails.isEmpty()) {
			editDetails = editDetails.substring(0, editDetails.length() - 1);
		}
			
		Task taskToEdit = tasks.get(positionOfTask);
		editAttribute(taskToEdit, attributeToChange,
				editDetails);
		
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
		/*else if (attribute.equalsIgnoreCase("date")) {
			
		} else if (attribute.equalsIgnoreCase("time")) {
			
		} else if (attribute.equalsIgnoreCase("from")) {

				
		} */
		else if (attribute.equalsIgnoreCase("!")){
			editPriority(taskToEdit);
		} else {
			throw new Exception("Please specify what to edit (time/desc/!)");
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
				setRecentChange(tasks.size() - 1);
			} else {
				setRecentChange(taskNum);
			}
			
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
		
		storage.write(
				convertTaskListStringList(tasks)
				);
		storage.writeArchive(
				convertTaskListStringList(archiveTasks)
				);
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
		} else if(operation.equalsIgnoreCase("pp")) {
			return CommandType.POSTPONE;
		} else if (operation.equalsIgnoreCase("archive")){
			return CommandType.ARCHIVE;
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

