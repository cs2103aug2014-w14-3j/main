package storage;

import java.util.List;

/**
 * Interface for StoragePlus.
 */
//@author Tran Cong Thien
public interface Storage {

	/**
	 * Reads from the storage and returns a stringed task list for the main list.
	 * 
	 * @return	List of stringed tasks.
	 */
	//@author
	List<String> read();
	
	/**
	 * Reads from the storage and returns a stringed task list for the archive list.
	 * 
	 * @return List of stringed tasks.
	 */
	//@author
	List<String> readArchive();

	/**
	 * Writes main list into storage.
	 * Before writing to the list, move the content of current list to the backup.
	 * 
	 * @param list	List of stringed tasks.
	 */
	//@author
	void write(List<String> list);
	
	/**
	 * Writes archive list into storage.
	 * 
	 * @param archiveList	List of completed stringed tasks.
	 */
	//@author
	void writeArchive(List<String> archiveList);
}

