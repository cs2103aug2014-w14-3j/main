package tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import controller.Controller;
import controller.ControllerClass;

//@author A0112044B
public class TestSearch {

	Controller controller;
	List<String> list;

	public String getDesc(String task) {
		String[] para=task.split("%");
		return para[0];
	}
	
	@Before
	public void setup() throws Exception {
		controller = ControllerClass.getInstance();
	
	
	}
	
	@Test
	//Test exactSearch: the keyword will appear exactly in one of the description of the tasks
	//return the task list
	public void tesExactSearch() throws Exception{
		
		controller.execCmd("add go home");
		controller.execCmd("add go to school");
		controller.execCmd("add \"do homework and sleep\" nov 20 3pm");
		controller.execCmd("add \"have lunch\" nov 18");
		controller.execCmd("add \"do homework\" today 3pm");
		controller.execCmd("add \"get up early\" tomorrow 7am");
		//search for "go home"
		controller.execCmd("go home");
		
		list = controller.getCurrentList();
		
		assertEquals(getDesc(list.get(0)),"5. go home");
	
	
	}	
	
	
	@Test
	//Test exactSearch
	//the keyword is typed wrongly and the software will give the list of the tasks which are nearly matched
	public void testNearMatchSearch() throws Exception{
		
		//search for "eat lunch" but user types it as "ate lunchh";
		
		
		controller.execCmd("lunnch");
		list = controller.getCurrentList();
		
		assertEquals(getDesc(list.get(0)),"3. have lunch");
		
		
	
	}	
	
	
	
	@Test
	//Test search on a day
	public void testSearchDate() throws Exception{
		
		
	
		controller.execCmd("today");
		
		list = controller.getCurrentList();
	
	
		assertEquals(getDesc(list.get(0)),"1. do homework");
	
	}	
	
	
	
	
	@Test
	//Test search by a day

	public void testSearchByDate() throws Exception{
	
	
	
		controller.execCmd("by tomorrow");
		
		list = controller.getCurrentList();
		
		assertEquals(getDesc(list.get(0)),"1. do homework");
		assertEquals(getDesc(list.get(1)),"2. get up early");
		
	}	
	
	
	
	@Test
	//Test search a period

	public void testSearchPeriod() throws Exception{
	
	
	
		
		controller.execCmd("nov 18 to nov 21");
		
		list = controller.getCurrentList();
		

		assertEquals(getDesc(list.get(0)),"3. have lunch");
		assertEquals(getDesc(list.get(1)),"4. do homework and sleep");
		
		
	}
	
	
	
	@Test
	//Test search a period

	public void testDescAndDate() throws Exception{
		
		
		controller.execCmd(" \"homework\" nov 20");
		list = controller.getCurrentList();
		controller.execCmd("delete 1 2 3 4 5 6");
		assertEquals(getDesc(list.get(0)),"4. do homework and sleep");
		
		
	}
	
	
}
