package controller;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

/**
 * Unit tests for controller class for delete functionality 
 * @author G. Vishnu Priya
 *
 */
public class controllerClassTestVishnu {
	
	Controller controller;
	List<String> taskDisplayList;
	
	@Before
	public void initialize() {
		controller = new ControllerClass();
	}
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	public String getDescription(String displayString) {
		String[] splitDisplayString = displayString.split("%");
		return splitDisplayString[0];
	}
	
	@Test
	//This is a boundary case for the positive values within the range of task numbers, of the partition.
	public void testExecuteDeletePostiveValue() throws Exception {
		controller.execCmd("add have lunch at school");
		controller.execCmd("add meet friends for movie");
		controller.execCmd("delete 1");
		controller.execCmd("list");
		taskDisplayList = controller.getCurrentList();
		
		assertEquals("meet friends for movie", getDescription(taskDisplayList.get(0)));
		
		controller.execCmd("delete 1");
	}
	
	@Test
	//This is a case around the boundary for the values outside the range of task numbers, of the partition.
	public void testExecuteDeleteNegativeValue() throws Exception {
		controller.execCmd("add have lunch at school");
		controller.execCmd("add meet friends for movie");
	
		try {
			controller.execCmd("delete -1");
			fail("Should throw an Exception when negative numbers are given as input.");
		} catch (Exception e) {
			assertEquals("Task does not exist. Please enter task number within the range.", e.getMessage());
			controller.execCmd("delete 2");
			controller.execCmd("delete 1");
		}
	}
	
	@Test
	public void testEditDescription() throws Exception {
		controller.execCmd("add have lunch at school");
		controller.execCmd("edit 1 desc have lunch at home");
		controller.execCmd("list");
		taskDisplayList = controller.getCurrentList();
		
		assertEquals("have lunch at home", getDescription(taskDisplayList.get(0)));
		controller.execCmd("delete 1");
	}
	
	
}
