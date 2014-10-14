package storage;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

public class StoragePlusTest {

	@Test
	public void testRead() {
		fail("Not yet implemented");
	}

	@Test
	public void testWrite() {
		fail("Not yet implemented");
	}
	
	@Test 
	public void testEmptyListStorage() {
		ArrayList<String> emptyList = new ArrayList<String>();
		Storage storage = new StoragePlus();
		storage.write(emptyList);
		ArrayList<String> result = storage.read();
		Assert.assertTrue("failure - should be empty", result.isEmpty());
	}


}
