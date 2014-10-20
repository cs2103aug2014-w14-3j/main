package controller;
import java.util.ArrayList;


public class Display {
	ArrayList<Task> taskList;
	ArrayList<ArrayList<Task>> pageList;
	int numOfPage;
	int maxTaskInOnePage;
	int numOfTask;
	
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
	
	public int getMaxNumOfTaskPerPage(){
		return maxTaskInOnePage;
	}
	
	public int getNumOfPage(){
		return numOfPage;
	}
	
	public int getNumOfTask(){
		return numOfTask;
	}
	
	public ArrayList<ArrayList<Task>> getPageList(){
		return pageList;
	}
	
	public ArrayList<Task> getTaskList(){
		return taskList;
	}

}
