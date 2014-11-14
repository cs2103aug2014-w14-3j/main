package tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import controller.Controller;
import controller.ControllerClass;

//@author A0115584A
public class TestControllerClassSortAndPostpone {

	Controller controller;
	List<String> displayTaskList;
	
	@Before
	public void setup() {
		controller = ControllerClass.getInstance();
	}

	public String getDescFromTaskStrings(String stringedTask) {
		String[] splitStringedTasks = stringedTask.split("%");
		return splitStringedTasks[0];
	}
	
	@Test
	// This is the case below the boundary for sorting the displayed list by date & time partition - sorting alphabetically
	public void testSortTasks() throws Exception {
		controller.execCmd("add little brown fox");
		controller.execCmd("add jumped over blue moon");
		controller.execCmd("list");
		displayTaskList = controller.getCurrentList();
		assertEquals(2, displayTaskList.size());
		assertEquals("1. jumped over blue moon\n" + "2. little brown fox", 
				getDescFromTaskStrings(displayTaskList.get(0)) + "\n" + getDescFromTaskStrings(displayTaskList.get(1)));
		controller.execCmd("delete 2");
		controller.execCmd("delete 1");
	}	
	
	@Test 
	// This is the boundary case for sorting the displayed list by date & time partition - sorting by date
	public void testSortTasksWithDate() throws Exception {
		controller.execCmd("add jumped over blue moon");
		controller.execCmd("add \"little brown fox\" 10/14");
		displayTaskList = controller.getCurrentList();
		assertEquals(2, displayTaskList.size());
		assertEquals("1. little brown fox\n" + "2. jumped over blue moon", getDescFromTaskStrings(displayTaskList.get(0)) + "\n" + 
				 getDescFromTaskStrings(displayTaskList.get(1)));
		controller.execCmd("delete 2");
		controller.execCmd("delete 1");
	}
	
	@Test
	// This is the boundary case for sorting the displayed list by date & time partition - sorting by date and time
	public void testSortTasksWithDateAndTime() throws Exception {
		controller.execCmd("add \"jumped over blue moon\" 10/14");
		controller.execCmd("add \"little brown fox\" 10/14");
		displayTaskList = controller.getCurrentList();
		assertEquals(2, displayTaskList.size());
		assertEquals("1. jumped over blue moon\n" + "2. little brown fox", getDescFromTaskStrings(displayTaskList.get(0)) + "\n" + 
				getDescFromTaskStrings(displayTaskList.get(1)));
		controller.execCmd("delete 2");
		controller.execCmd("delete 1");
	}
	
	@Test
	// This is boundary case for sorting the displayed list by date & time partition - sorting by priority with date & time
	public void testSortTasksWithPriority() throws Exception {
		controller.execCmd("add \"jumped over blue moon\" 10/15 23:50");
		controller.execCmd("add ! \"little brown fox\" 10/14 23:00");
		displayTaskList = controller.getCurrentList();
		assertEquals(2, displayTaskList.size());
		assertEquals("1. little brown fox\n" + "2. jumped over blue moon", getDescFromTaskStrings(displayTaskList.get(0)) + "\n" + 
				getDescFromTaskStrings(displayTaskList.get(1)));
		controller.execCmd("delete 2");
		controller.execCmd("delete 1");
	}
	
	@Test
	public void testPostpone() throws Exception {
		controller.execCmd("add \"little brown fox\" 10/19");
		controller.execCmd("add jumped over blue moon");
		controller.execCmd("pp 1");
		displayTaskList = controller.getCurrentList();
		assertEquals(2, displayTaskList.size());
		assertEquals("1. jumped over blue moon\n" + "2. little brown fox", getDescFromTaskStrings(displayTaskList.get(0)) + "\n" + 
				getDescFromTaskStrings(displayTaskList.get(1)));
		controller.execCmd("delete 2");
		controller.execCmd("delete 1");
	}
}
