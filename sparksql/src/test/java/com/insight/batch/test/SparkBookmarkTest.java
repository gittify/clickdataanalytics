package com.insight.batch.test;
//package src.test.java;
import static org.junit.Assert.*;
import com.insight.batch.SparkBookmark;
import org.apache.spark.sql.DataFrame;
import org.junit.Test;
import java.io.File;

public class SparkBookmarkTest {

	@Test
	public void testBookmarksDF() {
		SparkBookmark bkObj = new SparkBookmark();
		String inFile = "src/test/resources/test.txt";
		bkObj.initialize(inFile,"","");
		bkObj.createBookmarks();
		DataFrame frame = bkObj.groupData();
		assertNotNull(frame);
		
	}
	
	@Test
	public void testLinks(){
		SparkBookmark bkObj = new SparkBookmark();
		String inFile = "src/test/resources/test.txt";
		String output = "src/test/resources/links.txt";
		bkObj.initialize(inFile,output,"");
		bkObj.createBookmarks();
		DataFrame frame = bkObj.groupData();
		bkObj.processlinks(frame);
		
		File file = new File(output);
		assertTrue(file.exists());
		
	}

	@Test
	public void tesUsers(){
		SparkBookmark bkObj = new SparkBookmark();
		String inFile = "src/test/resources/test.txt";
		String output = "src/test/resources/links.txt";
		String users  = "src/test/resources/users.txt";
		bkObj.initialize(inFile,output,"");
		bkObj.createBookmarks();
		DataFrame frame = bkObj.groupData();
		bkObj.processlinks(frame);
		bkObj.processUsers(frame);
		File file = new File(users);
		assertTrue(file.exists());
		
	}
}
