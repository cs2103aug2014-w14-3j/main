/**
 * 
 */
package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author 
 *
 */
public class SimpleTaskList implements TaskList {
	
	private List<Task> tasks;
	private Integer numTaskOnPage;
	
	public SimpleTaskList() {
		tasks = new ArrayList<Task>();
		numTaskOnPage = null;
	}
	
	public SimpleTaskList(List<String> strList) {
		this();
		addAll(strList);
	}
	
	@Override
	public void addAll(List<String> strList) {
		for (String str : strList) {
			tasks.add(new TaskClass(str));
		}
	}
	
	@Override
	public void clear() {
		tasks.clear();
	}
	
	@Override
	public boolean add(Task task) {
		return tasks.add(task);
	}

	@Override
	public void set(int pos, Task task) {
		tasks.set(pos, task);
	}

	@Override
	public void remove(int pos) {
		tasks.remove(pos);
	}

	@Override
	public boolean isEmpty() {
		return tasks.isEmpty();
	}
	
	@Override
	public Integer indexOf(Task task) {
		return tasks.indexOf(task);
	}

	@Override
	public Task get(Integer pos) {
		return tasks.get(pos);
	}
	
	@Override
	public TaskList clone() {
		TaskList clone = new SimpleTaskList(getStringList());
		clone.setNumTaskOnPage(numTaskOnPage);
		return clone;
	}


	@Override
	public List<String> getStringList() {
		ArrayList<String> taskStrings = new ArrayList<String>();
		for (Task task : tasks) {
			taskStrings.add(task.toString());
		}

		return taskStrings;
	}

	@Override
	public List<String> getNumberedStringList() {
		ArrayList<String> taskStrings = new ArrayList<String>();
		for (int i = 0; i < size(); i++) {
			taskStrings.add((i + 1) + ". " + tasks.get(i));
		}

		return taskStrings;
	}

	@Override
	public void sort() {
		Collections.sort(tasks, (task1, task2) -> {
			if (task1.isPrioritized() && !task2.isPrioritized()) {
				return -1;
			} else if (!task1.isPrioritized() && task2.isPrioritized()) {
				return 1;
			}

			if (task1.getStartTime() == null && task2.getStartTime() != null) {
				return 1;
			} else if (task1.getStartTime() != null
					&& task2.getStartTime() == null) {
				return -1;
			}

			if (task1.getStartTime() == null && task2.getStartTime() == null) {
				return task1.getDesc().compareTo(task2.getDesc());
			} else {
				Long thisDate = task1.getStartTime().getTime();
				Long taskDate = task2.getStartTime().getTime();

				return thisDate.compareTo(taskDate);
			}

		});
	}

	@Override
	public int size() {
		return tasks.size();
	}

	@Override
	public void setNumTaskOnPage(Integer number) {
		assert number > 0;
		
		numTaskOnPage = number;
	}

	@Override
	public List<String> getPage(Integer pageNum) {
		assert numTaskOnPage != null;
		
		if (getTotalPageNum() == 0) {
			return new ArrayList<String>();
		}
		
		if (pageNum < 0 || pageNum > getTotalPageNum()) {
			throw new IndexOutOfBoundsException("Invalid Page Number");
		}
		
		ArrayList<String> taskStrings = new ArrayList<String>();
		Integer from = (pageNum - 1) * numTaskOnPage;
		Integer to = Math.min(pageNum * numTaskOnPage, tasks.size());
		for (Task task : tasks.subList(from, to)) {
			taskStrings.add(task.toString());
		}
		
		return taskStrings;
	}

	@Override
	public Integer getTotalPageNum() {
		assert numTaskOnPage != null;
		
		Integer totalPage = tasks.size() / numTaskOnPage;
		totalPage += tasks.size() % numTaskOnPage != 0 ? 1 : 0;
		return totalPage;
	}

	@Override
	public List<String> getNumberedPage(Integer pageNum) {
		assert numTaskOnPage != null;
		
		if (getTotalPageNum() == 0) {
			return new ArrayList<String>();
		}
		
		if (pageNum < 0 || pageNum > getTotalPageNum()) {
			throw new IndexOutOfBoundsException("Invalid Page Number");
		}
		
		ArrayList<String> taskStrings = new ArrayList<String>();
		Integer from = (pageNum - 1) * numTaskOnPage;
		Integer to = Math.min(pageNum * numTaskOnPage, tasks.size());
		for (int i = from; i < to; i++) {
			taskStrings.add((i + 1) + ". " + tasks.get(i).toString());
		}
		
		return taskStrings;
	}

	@Override
	public Integer getIndexPageContainTask(Integer taskIndex) {
		if (taskIndex < 0 || taskIndex >= tasks.size()) {
			throw new IndexOutOfBoundsException("Invalid Index");
		}
		
		return taskIndex / numTaskOnPage + 1;
	}

	@Override
	public Integer getIndexTaskOnPage(Integer taskIndex) {
		if (taskIndex < 0 || taskIndex >= tasks.size()) {
			throw new IndexOutOfBoundsException("Invalid Index");
		}
		
		return taskIndex % numTaskOnPage;
	}

	@Override
	public TaskList getOverdueTasks() {
		int numOfTask = tasks.size();
		Date current = new Date();
		TaskList resultList = new SimpleTaskList();

		for (int i = 0; i < numOfTask; i++) {
			Task task = tasks.get(i);
			if (task.getDeadline() != null) {
				if (task.getDeadline().compareTo(current) <= 0) {
					Task withNum = task.clone();
					withNum.setDesc((i + 1) + ". " + withNum.getDesc());
					resultList.add(task);
				}
			}
		}

		return resultList;
	}

}
