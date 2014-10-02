package controller;

import ui.UI;
import java.util.ArrayList;

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
	
	private ArrayList<Task> tasks;
	
	//deleted constructor
	//Changed return value, added return null 
	//Update needed (TO-DO)
	public ArrayList<String> execCmd(String command) {
			parseCommand(command);
			return null;
		}
	
	//This method gets the command type of user input and further processes the input.
	private void parseCommand(String command) {
			String operation = getOperation(command);
			CommandType commandType = matchCommandType(operation);
			String content = removeCommandType(command, operation);
			processInput(commandType, content);
		}
	
	//This method returns the type of operation to be carried out, either add, delete, edit or display.
	public String getOperation(String command) {
		String[] splitCommandIntoWords = command.split(" ");
		String operation = splitCommandIntoWords[POSITION_OF_OPERATION];
		return operation;
	}
	
	//This method processes each string of user input according to the command type.
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
			//printInvalidCommand();
			break;
		default:
			//throw new Error();
	}
	}


	private void display() {

	}

	private void editTask(String content) {
		
	}

	private void deleteTask(String content) {
		
	}

	private void addTask(String content) {
		
	}
	
	//This method removes the command, either add, delete, edit or display, from the command string.
	private String removeCommandType(String command, String operation) {
		return command.replace(operation,"").trim();
	}
	
	//This method returns the command type for each operation.
	private	CommandType matchCommandType (String operation) {
			if (operation.equalsIgnoreCase("add")) {
				return CommandType.ADD;
			} else if (operation.equalsIgnoreCase("delete")) {
				return CommandType.DELETE;
			} else if (operation.equalsIgnoreCase("edit")){
				return CommandType.EDIT;
			} else if ((operation.equalsIgnoreCase("display")) || (operation.equalsIgnoreCase("list"))) {
				return CommandType.DISPLAY;
			} else {
				return CommandType.INVALID;
			}
		}
	}