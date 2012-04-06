package cs301.group8.blem.test;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import cs301.group8.blem.MainActivity;
import cs301.group8.database.AppDatabase;
import cs301.group8.meta.Picture;

/** This test class MUST be run from a "clean" app (where there are currently no pictures and no groups present)
 * @author Group 08 <cs301-group8@ualberta.ca>
 * @version 2.0
 */
public class AppDatabaseTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private MainActivity mActivity;
	private ArrayList<String> groups;
	private ArrayList<Picture> pics;
	private AppDatabase db;
	private long theTime;
	private Picture picOne;
	private Picture picTwo;
	private Picture picThree;
	private Picture blemishPic;
	private Picture doesntExist;
	private long time;

	public AppDatabaseTest() {
		super("cs301.group8.blem", MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mActivity = this.getActivity();
		db = new AppDatabase(mActivity);
		theTime = System.currentTimeMillis();
		picOne = new Picture("group1", theTime);
		picTwo = new Picture("group1", theTime);
		blemishPic = new Picture("blemish", theTime);
		doesntExist = new Picture("doesntExist", theTime);
		db.addGroup("group1");
		db.addGroup("group2");
		db.addGroup("blemish");
	}

	public void testAddPictureandGetPictures() throws Exception{
		//test that getPictures returns empty list if no pics
		pics = db.getPictures("group1");
		assertEquals(pics.size(), 0);

		db.addPicture(picOne);
		db.addPicture(picTwo);
		db.addPicture(blemishPic);

		//test that size has increased to 2
		pics = db.getPictures("group1");
		assertEquals(pics.size(), 2);

		//test that adding a picture to a group doesn't affect any other arrays
		db.addPicture(doesntExist);
		pics = db.getPictures("group1");
		assertEquals(pics.size(), 2);

		cleanTest();
	}

	public void testgetPictureCount() throws Exception {
	        long number;
	        //assert that empty table has 0 pics
	        assertEquals(db.getPicCount("group1"), 0);
		Picture pic = new Picture("group1", 112);
		db.addPicture(pic);
		
		//assert that getPicCount returns 1 if there is 1 pic
		number=db.getPicCount("group1");
		assertEquals(number,1);
		
		//assert that getPicCount returns 2 if 2 pics
		db.addPicture(picOne);
		number = db.getPicCount("group1");
		assertEquals(number, 2);
		
		cleanTest();

	}

	public void testAddNoteandGetNote() {
		db.addPicture(picOne);
		String note = "";
		//assert that note is null if path doesn't exist in table
		assertEquals("", note);

		//assert that note is null if path doesn't exist in table
		note = db.getNotes(picOne);
		assertNull(note);

		cleanTest();
	}

	public void testGetPicStats(){
		String group = picOne.getGroup();
		long time = picOne.getTime();

		//assert time is equal to when initiated
		assertEquals(time, theTime);
		//assert group is equal to the group
		assertEquals(group, "group1");

		cleanTest();

	}


	public void testAddGroup() {
		groups = db.getGroups();

		//assert that groups array is of size 3
		assertEquals(db.getGroups().size(), 3);
		db.addGroup("candy");
		//assert that the groups array is now one element larger than above
		assertEquals(db.getGroups().size(), 4);

		int gSize = db.getGroups().size(); //size is 4
		db.deleteGroup("chocolate");
		//assert that the size has not changed after attempting to delete a group that was not in the array
		assertEquals(db.getGroups().size(), gSize);
		db.deleteGroup("candy");

		cleanTest();
	}

	public void testDeletePicture() throws Exception {
		db.addPicture(picOne);
		//delete the picture
		//assure it doesn't exist anymore

		cleanTest();
	}


	public void testGetGroups() {
		groups = db.getGroups();
		//assert that the database isn't null
		assertNotNull(db);
		//assert that groups return isn't null
		assertNotNull(groups);
		//assert that groups array is of size 3
		assertEquals(db.getGroups().size(), 3);

		//assert that the string in the array is equal to the right name
		assertEquals(groups.get(0), "blemish"); 
		assertEquals(groups.get(1), "group1");
		assertEquals(groups.get(2), "group2");

		//delete groups for empty database
		db.deleteGroup("group1");
		db.deleteGroup("group2");
		db.deleteGroup("blemish");
		assertEquals(db.getGroups().size(), 0);

	}



	public void testDeleteGroup() {
		groups = db.getGroups();

		//assert that groups array is of size 3
		int gsize0 = db.getGroups().size(); //size should be 3
		assertTrue(gsize0 == 3);
		db.deleteGroup("group2");
		//assert that the groups array is now one element smaller than above
		assertEquals(db.getGroups().size(), 2);

		int gSize = db.getGroups().size(); //size is 2
		db.deleteGroup("candy");
		//assert that the size has not changed after attempting to delete a group that was not in the array
		assertEquals(db.getGroups().size(), gSize);

		db.deleteGroup("group1");
		db.deleteGroup("blemish");

		groups = db.getGroups();
		//assert that the group list is now empty after deleting all of the groups
		assertEquals(groups.size(), 0);

	}

	public void testGroupExists() {
		groups = db.getGroups();

		//assert that groupExists returns false if the group does not already exist
		assertFalse(db.groupExists("fruit"));

		//assert that groupExists returns true if the group does exist
		assertTrue(db.groupExists("group1"));

		//assert that groupExists returns false on a null strings
		assertFalse(db.groupExists(""));

		//assert that a group exists after adding it
		db.addGroup("fruit");

		assertTrue(db.groupExists("fruit"));
		db.deleteGroup("fruit");
		cleanTest();
	}

	
	public void testSetReminderandGetReminder() {
		//assert that getReminder returns 0 if there is no reminder set
		assertEquals(db.getReminder("group1"), 0);
		
		//test that a reminder is set
		db.setReminder("group1", 12);
		assertEquals(db.getReminder("group1"), 12);
	}
	
	public void testGetMostRecent() {
		//test that getMostRecent returns 0 if no pics
		time = db.getMostRecent("group1");
		assertEquals(time, 0);

		db.addPicture(picOne);
		db.addPicture(picTwo);

		//test that the most recent picture is returned
		time = db.getMostRecent("group1");
		assertEquals(time, picTwo.getTime());

		cleanTest();
		
	}
	
	public void testRenameGroup() {
		groups = db.getGroups();

		//assert that groups name is what is is supposed to be
		assertTrue(db.groupExists("group1"));

		db.renameGroup("group1","chocolate");
		//assert that the groups name is now chocolate and that group1 no longer exists
		assertFalse(db.groupExists("chocolate"));
		assertTrue(db.groupExists("group1"));

		cleanTest();
		
	}
	
	public void testUpdateGroup() {
		groups = db.getGroups();
		
		db.updateGroup("group1", "", 13);
		assertEquals(db.getReminder("group1"), 13);
		//assert old group is updated
		db.updateGroup("group1", "", 67);
		assertEquals(db.getReminder("group1"), 67);
		
		cleanTest();	
	}
	
	
	public void testGetPicsByDate() {
	        picThree = new Picture("group2", 1234);
	        db.addPicture(picThree);
	    
	        //assert that the correct pic is found
	        pics = db.getPicsByDate(1224, "after");
	        assertNotNull(pics);
	}

	private void cleanTest() {
		db.deleteGroup("group1");
		db.deleteGroup("group2");
		db.deleteGroup("blemish");
	}


}