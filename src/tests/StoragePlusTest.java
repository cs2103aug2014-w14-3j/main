package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import storage.Storage;
import storage.StoragePlus;

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
		List<String> emptyList = new ArrayList<String>();
		Storage storage = new StoragePlus();
		storage.write(emptyList);
		List<String> result = storage.read();
		Assert.assertTrue("failure - should be empty", result.isEmpty());
	}


}
