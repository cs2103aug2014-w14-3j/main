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
	
	private final static String archiveFile="Archive.txt";

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
	
	
	public ArrayList<String> readArchive(){
		
		ArrayList<String> archiveList=new ArrayList<String>();
		
		try{
			Scanner scanner=new Scanner(new File(archiveFile));
			
			while(scanner.hasNextLine()){
				String task=scanner.nextLine();
				archiveList.add(task);
			}
			
			scanner.close();
			}
		
		//File not found
		catch (FileNotFoundException e){
			
		}
		
		return archiveList;
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
	
	
	//wrtie the archive list to the storage file
	public void writeArchive(ArrayList<String> archiveList){
		
		File archive=new File(archiveFile);
		
		try {
			PrintWriter printWriter = new PrintWriter(archive);

			for (int i = 0; i < archiveList.size(); i++) {
				printWriter.println(archiveList.get(i));
			}
			printWriter.close();
		} catch (FileNotFoundException e) {
			// Exception
			e.printStackTrace();
		}
		
	}
}