package controller;
import java.util.ArrayList;

/**
 * Class for displaying lists to user.
 */
//@author
public class Display {
	ArrayList<Task> taskList;
	ArrayList<ArrayList<Task>> pageList;
	int numOfPage;
	int maxTaskInOnePage;
	int numOfTask;
	
	/**
	 * Constructor for Display object.
	 * 
	 * @param _taskList			List of tasks to display.
	 * @param _maxTaskInOnePage	Maximum number of tasks to display in a page.
	 */
	//@author
	public Display(ArrayList<Task> _taskList, int _maxTaskInOnePage){
		taskList=_taskList;
		maxTaskInOnePage=_maxTaskInOnePage;
		numOfTask=_taskList.size();
		numOfPage=numOfTask/maxTaskInOnePage+1;
		pageList=new ArrayList<ArrayList<Task>>();
		
		for (int i=0;i<numOfPage;i++){
			ArrayList<Task> page=new ArrayList<Task>();
			for (int j=i;j<i+maxTaskInOnePage;j++)
			   page.add(taskList.get(maxTaskInOnePage*(i+1)+j));
			
		}
	}
	
	/**
	 * Gets the maximum number of tasks per page.
	 * 
	 * @return Maximum number of tasks per page.
	 */
	//@author
	public int getMaxNumOfTaskPerPage(){
		return maxTaskInOnePage;
	}
	
	/**
	 * Gets the number of pages for a list.
	 * 
	 * @return Number of pages.
	 */
	//@author
	public int getNumOfPage(){
		return numOfPage;
	}
	
	/**
	 * Gets the number of tasks in a list.
	 * 
	 * @return Number of tasks.
	 */
	//@author
	public int getNumOfTask(){
		return numOfTask;
	}
	
	/**
	 * Gets the pages of a list.
	 * 
	 * @return pages of a task list.
	 */
	//@author
	public ArrayList<ArrayList<Task>> getPageList(){
		return pageList;
	}
	
	/**
	 * Gets the task list.
	 * 
	 * @return list of tasks.
	 */
	//@author
	public ArrayList<Task> getTaskList(){
		return taskList;
	}

}