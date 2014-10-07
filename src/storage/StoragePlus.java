package storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class StoragePlus implements Storage {
	// Name of the file to store the list
	private final static String fileName = "ToDoList.txt";

	// Name of the back-up file
	private final static String backUpFile = "ToDoList.backup";

	public ArrayList<String> read() {

		ArrayList<String> toDoList = new ArrayList<String>();

		try {
			Scanner scanner = new Scanner(new File(fileName));

			while (scanner.hasNextLine()) {

				String task = scanner.nextLine();
				toDoList.add(task);
			}

			scanner.close();
		}

		// File not found
		catch (FileNotFoundException e) {
			// do nothing
			// the method will return the empty arrayList
		}
		return toDoList;
	}

	// before write to the list, move the content of current list to
	// "ToDoList.backup"

	public void write(ArrayList<String> list) {

		File backUp = new File(backUpFile);
		File file = new File(fileName);

		// create the backup file
		// if backUp file does not exist, just rename current file to backUp
		// before writing
		// else delete the backUp file and rename current file
		if (!file.exists()) {
			try {
				PrintWriter printWriter = new PrintWriter(file);

				for (int i = 0; i < list.size(); i++) {
					printWriter.println(list.get(i));
				}
				printWriter.close();
			} catch (FileNotFoundException e) {
				// Exception
				e.printStackTrace();

			}
		} else {
			if (!backUp.exists()) {
				file.renameTo(backUp);
			} else {
				backUp.delete();
				file.renameTo(backUp);
			}

			// write the list to the file
			try {
				PrintWriter printWriter = new PrintWriter(file);

				for (int i = 0; i < list.size(); i++) {
					printWriter.println(list.get(i));
				}
				printWriter.close();
			} catch (FileNotFoundException e) {
				// Exception
				e.printStackTrace();
			}
		}
	}
}