package controller;

import java.util.List;

public interface TaskList {

	boolean add(Task task);
	
	void add(int index,Task task);
	
	void addAll(List<String> strList);
		
	void set(int pos, Task task);
	
	void remove(int pos);
	
	void sort();
	
	boolean isEmpty();
	
	Integer indexOf(Task task);
	
	Task get(Integer pos);
	
	int size();
	
	TaskList clone();
	
	List<String> getStringList();
	
	List<String> getNumberedStringList();

	void clear();
	
	void setNumTaskOnPage(Integer number);
	
	List<String> getPage(Integer pageNum);
	
	List<String> getNumberedPage(Integer pageNum);
	
	Integer getTotalPageNum();
	
	Integer getIndexPageContainTask(Integer taskIndex);
	
	Integer getIndexTaskOnPage(Integer taskIndex);
	
	TaskList getOverdueTasks();
	
	TaskList search(String content);
}
