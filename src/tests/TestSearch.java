package tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import controller.Controller;
import controller.ControllerClass;

//@author A0112044B
public class TestSearch {

	

	public String getDesc(String task) {
		String[] para=task.split("%");
		return para[0];
	}
	
	@Test
	//Test exactSearch: the keyword will appear exactly in one of the description of the tasks
	//return the task list
	public void tesExactSearch() throws Exception{
		Controller controller=ControllerClass.getInstance();
		List<String> list;
		//search for "go home"
		controller.execCmd("go home");
		
		list = controller.getCurrentList();
	
		assertEquals(getDesc(list.get(0)),"10. go home");
	}	
	
	
	@Test
	//Test exactSearch
	//the keyword is typed wrongly and the software will give the list of the tasks which are nearly matched
	public void testNearMatchSearch() throws Exception{
		Controller controller=ControllerClass.getInstance();
		List<String> list;
		//search for "eat lunch" but user types it as "ate lunchh";
		controller.execCmd("lunchh");
		
		list = controller.getCurrentList();
	
		assertEquals(getDesc(list.get(0)),"17. go to school and eat lunch");
	}	
	
	
	
	@Test
	//Test search on a day
	public void testSearchDate() throws Exception{
		Controller controller=ControllerClass.getInstance();
		List<String> list;
		
		controller.execCmd("today");
		
		list = controller.getCurrentList();
	
		assertEquals(getDesc(list.get(0)),"2. finish the code");
	}	
	
	
	
	
	@Test
	//Test search by a day

	public void testSearchByDate() throws Exception{
		Controller controller=ControllerClass.getInstance();
		List<String> list;
	
		controller.execCmd("by tomorrow");
		
		list = controller.getCurrentList();
	
		assertEquals(getDesc(list.get(0)),"1. do the homework");
	}	
	
	
	
	@Test
	//Test search a period

	public void testSearchPeriod() throws Exception{
		Controller controller=ControllerClass.getInstance();
		List<String> list;
	
		controller.execCmd("tomorrow to nov 15");
		
		list = controller.getCurrentList();
	
		assertEquals(getDesc(list.get(0)),"6. deadline for MA2213 lab");
	}
	
	
	
	@Test
	//Test search a period

	public void testDescAndDate() throws Exception{
		Controller controller=ControllerClass.getInstance();
		List<String> list;
	
		controller.execCmd("\"go\" nov 15");
		
		list = controller.getCurrentList();
	
		assertEquals(getDesc(list.get(0)),"**No search result**");
	}
	
	
	
	
	
	
	
}
