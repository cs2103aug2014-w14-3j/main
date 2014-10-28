package controller;

import java.util.List;

public interface TaskList {

	boolean add(Task task);
	
	void addAll(List<String> strList);
		
	void update(Integer pos, Task task);
	
	void remove(Integer pos);
	
	void sort();
	
	boolean isEmpty();
	
	Integer indexOf(Task task);
	
	Task get(Integer pos);
	
	int size();
	
	TaskList clone();
	
	List<String> getStringList();
	
	List<String> getNumberedStringList();

	void clear();
}
