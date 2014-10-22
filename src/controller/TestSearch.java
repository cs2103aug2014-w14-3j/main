package controller;

import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Test;

public class TestSearch {

	

	public String getDesc(String task) {
		String[] para=task.split("%");
		return para[1];
	}
	
	@Test
	//Test exactSearch
	public void tesExactSearch() throws Exception{
		Controller controller=new ControllerClass();
		ArrayList<String> list;
		controller.execCmd("add go home");
		controller.execCmd("add go to school and eat lunch");
		//search for "go home"
		controller.execCmd("go home");
		
		list = controller.getCurrentList();
	
		assertEquals(getDesc(list.get(0))," go home");
	}	
	
	
	@Test
	//Test exactSearch
	public void testNearMatchSearch() throws Exception{
		Controller controller=new ControllerClass();
		ArrayList<String> list;
		controller.execCmd("add go home");
		controller.execCmd("add go to school and eat lunch");
		//search for "go home"
		controller.execCmd("ate lunchh");
		
		list = controller.getCurrentList();
	
		assertEquals(getDesc(list.get(0)),"go to school and eat lunch");
	}	
	
	
	
	
	
	
}
