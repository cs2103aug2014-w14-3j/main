package storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;



/**
 * Storage class to create a Storage object for storing tasks.
 */
//@author A0112044B
public class StoragePlus implements Storage {
	
	// Name of the file to store the list
	private final static String fileName = "ToDoList.txt";

	// Name of the back-up file
	private final static String backUpFile = "ToDoList.backup";
	
	private final static String archiveFile="Archive.txt";
	
	private static final Logger logger = Logger.getLogger(StoragePlus.class
			.getName());
	private static final String READ="read";
	private static final String WRITE="write";
	private static final String READ_ARCHIVE="readArchive";
	private static final String WRITE_ARCHIVE="writeArchive";
	/**
	 * Reads from the storage and returns a stringed task list for the main list.
	 * 
	 * @return	List of stringed tasks.
	 */
	public List<String> read() {

		List<String> toDoList = new ArrayList<String>();

		logger.entering(getClass().getName(),READ);
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
		logger.exiting(getClass().getName(), READ);
		return toDoList;
	}
	
	/**
	 * Reads from the storage and returns a stringed task list for the archive list.
	 * 
	 * @return List of stringed tasks.
	 */
	public List<String> readArchive(){
		
		List<String> archiveList=new ArrayList<String>();
		
		logger.entering(getClass().getName(),READ_ARCHIVE);
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
		logger.exiting(getClass().getName(), READ_ARCHIVE);
		return archiveList;
	}

	/**
	 * Writes main list into storage.
	 * Before writing to the list, move the content of current list to the backup.
	 * 
	 * @param list	List of stringed tasks.
	 */
	public void write(List<String> list) {

		File backUp = new File(backUpFile);
		File file = new File(fileName);

		// create the backup file
		// if backUp file does not exist, just rename current file to backUp
		// before writing
		// else delete the backUp file and rename current file
		if (!file.exists()) {
			logger.entering(getClass().getName(),WRITE);
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
			logger.exiting(getClass().getName(), WRITE);
		} else {
			if (!backUp.exists()) {
				file.renameTo(backUp);
			} else {
				backUp.delete();
				file.renameTo(backUp);
			}

			// write the list to the file
			logger.entering(getClass().getName(),WRITE);
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
			logger.exiting(getClass().getName(), WRITE);
		}
	}
	
	
	/**
	 * Writes archive list into storage.
	 * 
	 * @param archiveList	List of completed stringed tasks.
	 */
	public void writeArchive(List<String> archiveList){
		
		File archive=new File(archiveFile);
		
		logger.entering(getClass().getName(),WRITE_ARCHIVE);
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
		logger.exiting(getClass().getName(), WRITE_ARCHIVE);
		
	}
}