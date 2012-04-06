package cs301.group8.database;

import static cs301.group8.database.Constants.COL_GROUP;
import static cs301.group8.database.Constants.COL_NOTE;
import static cs301.group8.database.Constants.COL_PID;
import static cs301.group8.database.Constants.COL_REMINDER;
import static cs301.group8.database.Constants.COL_TAG;
import static cs301.group8.database.Constants.COL_TIME;
import static cs301.group8.database.Constants.DATABASE_NAME;
import static cs301.group8.database.Constants.GROUPTABLE;
import static cs301.group8.database.Constants.PICTURETABLE;
import static cs301.group8.database.Constants.TAGTABLE;

import java.io.File;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import cs301.group8.meta.Picture;
import cs301.group8.meta.Util;

/** AppDatabase uses SQLite database to create a table where we store our app
 * information.  This class provides a variety of methods which allow the application 
 * to create, add to, delete, and modify elements corresponding to different pictures,groups, and tags.
 * 
 * Each row in the picture table (PICTURETABLE) represents a unique image.  The picture table contains 4 
 * columns to describe each picture: group, path, note associated with the photo,
 * and time that the picture was taken.
 * 
 * The group table (GROUPTABLE) corresponds to the existing groups and their reminder times
 * 
 * The tag table (TAGTABLE) corresponds to the existing tags that are associated with each group
 *
 * @author Group 08 <cs301-group8@ualberta.ca>
 * @version 2.0
 */
public class AppDatabase {

	private Context context;

	SQLiteDatabase db;
	DatabaseHelper dbHelper;



	/** AppDatabase constructor uses the current context of the application (to avoid 
	 * duplicating or losing data) to create the database object.
	 *
	 * @param ctx		context of the Android application
	 */
	public AppDatabase(Context ctx){
		context = ctx;
		Log.i("database", "Constructor called");
		dbHelper = new DatabaseHelper(context,DATABASE_NAME);   
		db = dbHelper.getReadableDatabase();
	}

	/** getGroups queries the database and uses the database cursor to return an array 
	 * of strings which corresponds to all existing names of groups.
	 *
	 * @return groups	Array of strings representing the existing groups
	 */
	public ArrayList<String> getGroups(){//get groupnames with condition time!=0
		ArrayList<String> groups = new ArrayList<String>();
		Cursor cursor= db.rawQuery("SELECT DISTINCT " + COL_GROUP  + " FROM " + GROUPTABLE, null);
		int group_i = cursor.getColumnIndex(COL_GROUP);
		while(cursor.moveToNext()){
			groups.add(cursor.getString(group_i));
		}  
		cursor.close();
		return groups;
	}

	/** getPictures queries the database and uses the database cursor to return an array 
	 * of picture objects which consists of all pictures that have been added so far
	 * that match the group name parameter.
	 *
	 * @param groupName	String of the group name of the specific blemish
	 *
	 * @return pics 	Array of picture objects pertaining to that group
	 */
	public ArrayList<Picture> getPictures(String groupName){ //get pictures get all picture based on group name

		ArrayList<Picture> pics = new ArrayList<Picture>();
		Cursor cursor = db.rawQuery("SELECT * FROM " + PICTURETABLE + " WHERE " + COL_GROUP + "=?",  new String[]{groupName});

		int group_i = cursor.getColumnIndex(COL_GROUP), time_i = cursor.getColumnIndex(COL_TIME), note_i=cursor.getColumnIndex(COL_NOTE), id_i = cursor.getColumnIndex(COL_PID);
		while(cursor.moveToNext()){
			if(cursor.getLong(time_i)!=0){
				Picture pic = (new Picture(cursor.getLong(id_i),cursor.getString(group_i),cursor.getLong(time_i)));
				pic.setNote(cursor.getString(note_i));
				pics.add(pic);
			}
		}  
		cursor.close();
		return pics;
	}


	/** deleteGroup takes in a string of the group name and uses the database helper to
	 * query the database for matching group names.  The database will delete all entries
	 * (including pictures) associated with that group name.
	 *
	 * @param groupName	String of the group name of the specific blemish
	 */
	public void deleteGroup(String groupName){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(PICTURETABLE, COL_GROUP + "=?", new String[]{groupName});
		db.delete(GROUPTABLE, COL_GROUP + "=?", new String[]{groupName});
	}

	/** deletePicture takes in a picture object and uses the path of the picture to query the 
	 * database for the matching entry.  The database will remove this entry from the table.
	 *
	 * @param pic 			Picture object that is to be deleted from the table
	 * @throws Exception	Thrown if the picture is not present in the table 
	 */
	public void deletePicture(Picture pic) throws Exception{ //delete picture based on path
		if (pic.getId() == -1){
			Log.i("database", "Picture has not been added to the database so there is no ID associated with it");
			throw new Exception("Picture has not been added to the database so there is no ID associated with it");
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(PICTURETABLE, COL_PID+"=" + pic.getId(),null);
	}

	/** addGroup method takes a string input and calls the addValues method to add an entry to 
	 * the picture table. It also sets the default value of reminder to 0 in the reminder table (this 
	 * will be changed later if necessary).
	 *
	 * @param groupName	String of the group name of the specific blemish
	 */
	public void addGroup(String groupName){  //add group base on group name iff the group does not already exist
		if (groupExists(groupName)){
			Log.i("database", "Unable to add group: `" + groupName + "` as it already exists");
			return;
		}

		ContentValues values = new ContentValues();
		values.put(COL_GROUP, groupName);
		values.put(COL_REMINDER, 0);

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.insert(GROUPTABLE, null, values);

		Log.i("database", "Group added: `" + groupName +"` reminder: " + 0);

	}

	/** renameGroup method takes in 2 strings pertaining to the old group name and new group name.  This method
	 * queries the database tables and replaces the group names where appropriate (in the GROUPTABLE and PICTURETABLE)
	 * renameGroup must also change all the file paths of the old pictures to their appropriate new paths
	 * 
	 * @param oldGroup
	 * @param newGroup
	 */
	public void renameGroup(String oldGroup, String newGroup){
		// Rename the group if it exists. Keep reminder's value
		if(!groupExists(oldGroup)){
			Log.i("database", "Unable to rename group: `" + oldGroup +"` to `"+newGroup +"` as it does not exist");
			return;
		}

		boolean success = new File(Util.getRootPath() + oldGroup).renameTo(new File( Util.getRootPath() + newGroup));
		if(success){
			ContentValues values = new ContentValues();
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			values.put(COL_GROUP, newGroup);
			// Update group table
			db.update(GROUPTABLE, values, COL_GROUP + "='" + oldGroup + "'",null);

			// Update every picture
			db.update(PICTURETABLE, values, COL_GROUP + "='" + oldGroup + "'",null);
		}else{
			Log.i("database", "Unable to rename group `" + oldGroup +"` to `" + newGroup +"`");
		}

	}

	/** addPicture method takes in a picture object and calls the addValues method on the 
	 * object's group, and time of creation
	 * 
	 * @param pic		Created picture object
	 */
	public void addPicture(Picture pic){ //insert pic base on groupname path and time
		addValues(pic.getGroup(), pic.getTime());
	}

	/** addValues method takes in a string, time, and path corresponding to a picture object.  
	 * addValues uses each of these values to fill the appropriate columns for the new entry.
	 * The values are first inserted into a ContentValues object and added to the PICTURETABLE with 
	 * the db.insert call.
	 *
	 * @param name		Taken from the picture object: string name of the blemish group
	 * @param time		Taken from the picture object: long time of creation
	 */
	private void addValues(String name, long time){
		ContentValues values = new ContentValues();
		values.put(COL_GROUP,name);
		values.put(COL_TIME,time);

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.insert(PICTURETABLE, null, values);
	}

	/** groupExists takes in a string of the group name and returns a boolean value indicating 
	 * whether the group name exists in the table or not.
	 *
	 * @param groupName		String representing the name of the blemish group
	 *
	 * @return boolean		Returns true if the group exists in the table.  False otherwise
	 */

	public boolean groupExists(String groupName){

		Cursor cursor= db.query(true,GROUPTABLE, new String[]{COL_GROUP}, COL_GROUP+"=?", new String[]{groupName}, null, null,null, null);

		if(cursor.getCount()==0)
			return false;  
		else
			return true; //true means already exists


	}
	/** pictureExists takes in a picture object and returns a boolean value indicating 
	 * whether the picture exists in the table or not.
	 *
	 * @param pic			Picture object
	 *
	 * @return boolean		Returns true if the picture exists in the table.  False otherwise
	 * @throws Exception 	Exception thrown if picture does not exist in the table
	 */
	public boolean pictureExists(Picture pic) throws Exception{
		if (pic.getId() == -1){
			Log.i("database", "Picture has not been added to the database so there is no ID associated with it");
			throw new Exception("Picture has not been added to the database so there is no ID associated with it");
		}
		Cursor cursor=db.query(PICTURETABLE, new String[]{COL_PID}, COL_PID+"=" + pic.getId(), null, null, null, null);
		if(cursor == null)
			return false;
		else
			return true;
	}

	/** Closes the database upon clean exiting.
	 */
	public void close(){
		db.close();
		dbHelper.close();
	}


	/** addNote method takes in a unique picture ID and a string value of the desired note to add. 
	 * The method uses the path to find a specific photo, and then adds the string note to the NOTE column
	 * of the PICTURETABLE
	 * 
	 * @param pid			long value corresponding to the picture ID
	 * @param note			String note to accompany the photo
	 */
	public void addNote(long pid, String note){
		ContentValues values = new ContentValues();
		values.put(COL_NOTE,note);

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.update(PICTURETABLE, values, COL_PID + "=" + pid,null);
	}



	/** getNotes queries the database for the specified photo using the picture object.  getNotes will
	 * return the string value of the note stored in the NOTE column of the database.
	 *
	 * @param pic		Picture object
	 *
	 * @return note		String value of the note associated with the photo
	 */
	public String getNotes(Picture pic){
		String note = null;
		Cursor cursor= db.query(PICTURETABLE, new String[]{COL_NOTE}, COL_PID+"="+pic.getId(), null, null, null, null);
		while(cursor.moveToNext()){
			note = cursor.getString(cursor.getColumnIndex(COL_NOTE));
		} 
		cursor.close();
		return note;
	}

	/**getPicCount takes in a string value of the group name, and returns
	 * the number of pictures associated with that group in PICTURETABLE
	 * 
	 * @param group			String value of the group name
	 * @return				long value of the number of pics
	 */
	public long getPicCount(String group){
		SQLiteStatement s = db.compileStatement("select count(*) from " + 
				PICTURETABLE + " where " + COL_GROUP + "='" + group +"'");

		return s.simpleQueryForLong();
	}

	/** setReminder takes in a string value of the group name and a long value of the reminder time.
	 * setReminder puts the value of the reminder into the REMINDER column of the GROUPTABLE.
	 * This long value will correspond to the maximum amount of time between the user's current time 
	 * and the time the most recent picture was taken.  If this exceeds the reminder time, the user 
	 * should be notified. 
	 * 
	 * @param group			String group name associated with the reminder
	 * @param time			Long time value (in milliseconds) of reminder
	 */
	public void setReminder(String group, long time){
		if (!groupExists(group)){
			Log.i("database", "Unable to set reminder: " + time +" as group `" + group +"` does not exist");
			return;
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COL_REMINDER, time);
		db.update(GROUPTABLE, values, COL_GROUP + "='" + group + "'",null);

	}

	/** getMostRecent takes in a string value of the group name and returns the long value of the time the most recent picture was taken.
	 * It uses a database query to get all the pictures in the group, and uses the getTime function to return the milliseconds long time
	 * that the last picture in the list (most recent) was taken
	 * 
	 * @param group				string value of the group name
	 * @return	pictime			long value of the millisecond time of the most recent picture
	 */
	public long getMostRecent(String group) {
		//returns long value of the time that the most recent (largest #) photo was taken
		ArrayList<Picture> pics = getPictures(group);
		if (!pics.isEmpty()){
			long pictime = pics.get((pics.size()-1)).getTime();
			Log.i("picturetime", Long.toString(pictime));
			return pictime;
		} else {
			return 0;
		}
	}

	/** updateGroup takes in the string value of the group name, the string value of the new name,
	 * and the long value of the time.  This is called when the user calls the Edit Group dialog in
	 * BlemishActivity and enters new values to update the database.  We rename the groups (if necessary)
	 * and change the reminder times(if necessary) inside the GROUPTABLE and PICTURETABLE.
	 * 
	 * @param group			String value of the old group name
	 * @param newName		String value of the new group name
	 * @param time			Long value of the reminder time
	 */
	public void updateGroup (String group, String newName, long time){
		if (group == null || group.equals("")){
			Log.i("database", "Unable to update empty group");
			return;
		}
		if (groupExists(group)){
			// Should me rename?
			if (newName==null || newName.equals("") || newName.equals(group)){
				Log.i("database", "Setting reminder of group `" + group +"` to: " + time);
				setReminder(group, time);
			}
			else{
				Log.i("database", "Setting reminder of group `" + newName +"` to: " + time);
				renameGroup(group, newName);
				setReminder(newName, time);
			}
		}
		else{
			Log.i("database", "Adding new group `" + group+"`");
			addGroup(group);
			setReminder(group, time);
		}
	}

	/**getReminder queries the GROUPTABLE based on the group name parameter and returns a long value of the 
	 * reminder time.
	 * 
	 * @param group			String value of the group name
	 * @return long			Long value of the reminder time (in milliseconds)
	 */
	public long getReminder(String group){
		Cursor cursor = db.rawQuery("SELECT " + COL_REMINDER + " FROM " + GROUPTABLE + " WHERE " + COL_GROUP +" = '" + group +"'", null);

		if(cursor.moveToFirst()){	// Should check if cursor is null?
			Log.i("database", "returning reminder for group: " + group);
			return cursor.getLong(cursor.getColumnIndex(COL_REMINDER));
		}
		else{
			Log.i("database", "no group: " + group);
			return -1;
		}
	}

	/**setTags takes in an array of strings corresponding to the tags input by the user.  The tags
	 * are stored in the TAGTABLE and correspond to a unique picture object specified by the pid parameter
	 * 
	 * @param pid			long value of the picture ID
	 * @param tags			String array of tags to add to the database
	 */
	public void setTags(long pid, String[] tags){

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(TAGTABLE, COL_PID+"="+pid, null);

		ContentValues values = new ContentValues();
		values.put(COL_PID, pid);
		for (String tag : tags){
			tag = tag.trim().toLowerCase();
			if (tag.length()<3){
				continue;
			}
			values.remove(COL_TAG);
			values.put(COL_TAG, tag);
			db.insert(TAGTABLE, null, values);
		}
	}

	/**getTags takes in a long value of the picture ID and returns an ArrayList of strings that 
	 * have been stored with that picture.
	 * 
	 * @param pid						Long value of the unique picture ID
	 * @return ArrayList<String>		Array of of the tags associated with the picture
	 */
	public ArrayList<String> getTags (long pid){
		Cursor cursor = db.rawQuery("SELECT " + COL_TAG + " FROM " + TAGTABLE + " WHERE " + COL_PID + "="+pid, null);
		ArrayList<String> tags = new ArrayList<String>();
		final int tag_i = cursor.getColumnIndex(COL_TAG);
		while(cursor.moveToNext()){
			tags.add(cursor.getString(tag_i));
		} 
		cursor.close();
		return tags;

	}

	/**getPicsByTag takes in a string value of the tag and searches the TAGTABLE and PICTURETABLE for 
	 * unique picture IDs that have stored the tag.  getPicsByTag returns an array list of picture objects.
	 * 
	 * @param tag			String value of the tag to search for
	 * @return	pics		ArrayList of pictures with the tag
	 */
	public ArrayList<Picture> getPicsByTag (String tag){
		tag=tag.trim().toLowerCase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + PICTURETABLE + " pics, "+TAGTABLE + " tags WHERE tags." + COL_TAG + "='" + tag +"' AND pics."+ COL_PID +" = tags."+COL_PID, null);
		return cursorToPic(cursor);
	}

	/**cursorToPic takes in a cursor object and returns (for that cursor) an ArrayList of picture objects that from that cursor (for the getPicsByTag method call)
	 * 
	 * @param cursor		Cursor object corresponding to the getPicsByTag query
	 * @return	pics		ArrayList of pictures resulting from the query
	 */
	private ArrayList<Picture> cursorToPic (Cursor cursor){
		ArrayList<Picture> pics = new ArrayList<Picture>();
		final int id_i = cursor.getColumnIndex(COL_PID), group_i = cursor.getColumnIndex(COL_GROUP), time_i = cursor.getColumnIndex(COL_TIME), note_i = cursor.getColumnIndex(COL_NOTE);
		while (cursor.moveToNext()){
			Picture pic = new Picture(cursor.getLong(id_i),cursor.getString(group_i), cursor.getLong(time_i));
			pic.setNote(cursor.getString(note_i));
			pics.add(pic);
		}
		return pics;
	}

	/**getAllTags returns a string array of all tags stored in the TAGTABLE.  
	 * 
	 * @return tags			Array of strings corresponding to all stored tags.
	 */
	public String[] getAllTags(){
		Log.i("database", "getting all Tags");
		Cursor cursor = db.rawQuery("SELECT DISTINCT " + COL_TAG + " FROM " + TAGTABLE, null);

		String[] tags = new String[cursor.getCount()];
		cursor.moveToFirst();
		int tag_i = cursor.getColumnIndex(COL_TAG);
		for(int i=0; i<tags.length;i++, cursor.moveToNext()){
			Log.i("database", "TAG: " + cursor.getString(tag_i));
			tags[i] = cursor.getString(tag_i);
		}

		return tags;
	}

	/**getPicsByDate takes in the value of time for which the user is querying for pictures from. Long value of time is the 
	 * time in milliseconds and the string value of when corresponds to whether the user is searching "before or after".  If 
	 * 'when' is null, search for all pictures that were taken on the day specified by time. getPicsByDate returns an ArrayList 
	 * pictures that correspond to the appropriate search by date.
	 * 
	 * @param time					Long value of the time in milliseconds
	 * @param when					Specifies if the search should look before or after this time
	 * @return ArrayList<Picture>	Array list of pictures that meet the query specifications
	 */
	public ArrayList<Picture> getPicsByDate(long time, String when){
		String where = null;
		if (when == null) where = COL_TIME + " >= " + time + " AND " + COL_TIME +" < " + (time+86400000);
		else if (when.toLowerCase().equals("before")) where = COL_TIME + " <= " + time;
		else if (when.toLowerCase().equals("after")) where = COL_TIME + " >= " + time;

		final String query = "SELECT * FROM " + PICTURETABLE + " WHERE " + where;
		Log.i("database", "Search: " + query);


		Cursor cursor = db.rawQuery(query, null );	


		return cursorToPic(cursor);
	}

	/**getPicsByNote takes in a string value of the text to query for.  getPicsByNote queries the PICTURETABLE
	 * and returns an array list of pictures which have stored the query text into their note column (from ImageActivity)
	 * 
	 * @param text						String value of the note we are searching for
	 * @return	ArrayList<Picture>		ArrayList of picture objects from the search
	 */
	public ArrayList<Picture> getPicsByNote(String text){
		String query = "SELECT * FROM " + PICTURETABLE + " WHERE " + COL_NOTE + " like '%" + text +"%'";
		Log.i("database", "byNote: " + query);
		Cursor cursor = db.rawQuery(query, null );	

		return cursorToPic(cursor);

	}
}
