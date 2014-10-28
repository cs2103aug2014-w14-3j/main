/**
 * 
 */
package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author 
 *
 */
public class SimpleTaskList implements TaskList {
	
	private List<Task> tasks;
	
	public SimpleTaskList() {
		tasks = new ArrayList<Task>();
	}
	
	public SimpleTaskList(List<String> strList) {
		tasks = new ArrayList<Task>();
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
	public void update(Integer pos, Task task) {
		tasks.set(pos, task);
	}

	@Override
	public void remove(Integer pos) {
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
		return new SimpleTaskList(getStringList());
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

}
