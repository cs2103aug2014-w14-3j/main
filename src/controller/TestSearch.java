package controller;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class TestSearch {

	

	public String getDesc(String task) {
		String[] para=task.split("%");
		return para[1];
	}
	
	@Test
	//Test exactSearch: the keyword will appear exactly in one of the description of the tasks
	//return the task list
	public void tesExactSearch() throws Exception{
		Controller controller=new ControllerClass();
		List<String> list;
		controller.execCmd("add go home");
		controller.execCmd("add go to school and eat lunch");
		//search for "go home"
		controller.execCmd("go home");
		
		list = controller.getCurrentList();
	
		assertEquals(getDesc(list.get(0))," go home");
	}	
	
	
	@Test
	//Test exactSearch
	//the keyword is typed wrongly and the software will give the list of the tasks which are nearly matched
	public void testNearMatchSearch() throws Exception{
		Controller controller=new ControllerClass();
		List<String> list;
		controller.execCmd("add go home");
		controller.execCmd("add go to school and eat lunch");
		//search for "eat lunch" but user types it as "ate lunchh";
		controller.execCmd("ate lunchh");
		
		list = controller.getCurrentList();
	
		assertEquals(getDesc(list.get(0)),"go to school and eat lunch");
	}	
	
	
	
	
	
	
}
