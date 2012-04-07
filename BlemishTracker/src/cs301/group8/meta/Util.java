package cs301.group8.meta;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

/** Util is a class that provides various utility methods used throughout the app for bitmap generation,
 * array population, and other purposes.  
 *
 * @author Group 08 <cs301-group8@ualberta.ca>
 * @version 2.0
 */
public class Util {
	/* Constants for passwords */
	private static final String PASSON_KEY = "first";
	private static final String TIPS_KEY = "tool";
	private static final String PASSWORD_KEY = "password";
	private static final String STARTTAB_KEY = "firsttab";
	public static final String MY_PREFS_FILE = "MyPrefsFile";

	/* Constants for reminders*/
	private static final int BUFFER = 2048; 
	private static final int MILLISECONDS_PER_HOUR = 3600000;
	private static final int MILLISECONDS_PER_DAY = 86400000;
	private static final int MILLISECONDS_PER_WEEK = 604800000;
	private static final int[] background = new int[3];
	private static int[] createColors(int HEIGHT, int WIDTH) {

		int[] colors = new int[WIDTH * HEIGHT];
		Random gen = new Random(System.currentTimeMillis());
		int r = gen.nextInt(256);
		int g = gen.nextInt(256);
		int b = gen.nextInt(256);
		//int a = Math.max(r, g);
		for (int y = 0; y < HEIGHT; y++) {


			for (int x = 0; x < WIDTH; x++) {

				colors[y * x] =  (r << 16) | (g << 8) | b;
			}
		}
		return colors;
	}


	public static  Bitmap generateBitmap(int width, int height) {
		return Bitmap.createBitmap(createColors(width, height), 0, width, width, height, Bitmap.Config.RGB_565);
	}

	public static String timeToDate (long time){
		return DateFormat.getDateTimeInstance( DateFormat.LONG, DateFormat.SHORT).format(new Date(time));
	}

	public static String getPath(Picture pic){
		String path = getRootPath() + pic.getPath();
		return path;
	}

	public static String getRootPath(){
		return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
	}
	/** getSelection is used when the edit dialog is called.  It is used to help set the default selection of the spinner pulldown menu.
	 * 
	 * @param reminder			long value of the reminder time in milliseconds
	 * @return					return an integer number representing the position of the spinner
	 */

	public static int getSelection(long reminder){

		if ((reminder / MILLISECONDS_PER_WEEK) > 0) {
			return 3;
		} else if ((reminder / MILLISECONDS_PER_DAY) > 0) {
			return 2;
		} else if (reminder > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	/**getFreqFloat is used when the edit dialog is called.  It is used to help populate the edittext views with the current data regarding the group.
	 * 
	 * @param reminder		long value of the reminder time in milliseconds
	 * @return result		float value to fill the edit text view
	 */
	public static float getFreqFloat(long reminder) {
		float result;
		if ((reminder / MILLISECONDS_PER_WEEK) > 0) {
			result = ((float) reminder / (float) MILLISECONDS_PER_WEEK);
		} else if ((reminder / MILLISECONDS_PER_DAY) > 0) {
			result = ((float) reminder / (float) MILLISECONDS_PER_DAY);
		} else if (reminder > 0) {
			result = ((float) reminder / (float) MILLISECONDS_PER_HOUR);
		} else {
			result = 0;
		}
		return result;
	}

	/**parseReminder uses the input from the ADD_GROUP dialog to parse the value of the reminder time (in milliseconds)
	 * so that it can be stored in the database
	 * 
	 * @param position  Integer value of the reminder category spinner
	 * @param value		Float value of the reminder time
	 * @return result	Reminder time in milliseconds
	 */
	public static long parseReminder(int position, float value){
		float result = 0;
		Log.i("position value", Integer.toString(position));
		Log.i("value value", Float.toString(value));

		if (position == 1) {
			result = (MILLISECONDS_PER_HOUR * value);
		} else if (position == 2) {
			result = (MILLISECONDS_PER_DAY * value);
		} else if (position == 3) {
			result = (MILLISECONDS_PER_WEEK * value);
		} else {
			result = 0;
		}

		Log.i("result value", Float.toString(result));
		return (long) result;
	}

	/**checkReminder takes in a long value of the reminder time of the group and the time of the most recent picture in the group.
	 * checkReminder returns true if most recent picture time + reminder time < current time (i.e. a new picture should be taken)
	 * otherwise, checkReminder returns false	
	 * 
	 * @param reminderTime			long value of reminder time in milliseconds
	 * @param mostRecentTime		long value of most recent time of picture taken
	 * @return						boolean
	 */

	public static boolean checkReminder(long reminderTime, long mostRecentTime){
		//return true if most recent picture time + reminder time < current time
		long currentTime = System.currentTimeMillis();
		Log.i("currentTime", Long.toString(currentTime));
		Log.i("reminderTime", Long.toString(reminderTime));
		Log.i("mostRecentTime", Long.toString(mostRecentTime));

		if (((mostRecentTime + reminderTime) < currentTime) && reminderTime > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**zip method takes in a string array of the paths to the files and a string value 
	 * of the path to the zipfile.  zip creates a zip archive by compressing the desired 
	 * files and archiving them at the zipFile locations
	 * 
	 * @param files			String array of the file paths to be archived
	 * @param zipFile		String value of the zip file path
	 */
	public static void zip(String[] files, String zipFile) { 

		try  { 
			BufferedInputStream origin = null; 
			FileOutputStream dest = new FileOutputStream(zipFile); 

			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest)); 

			byte data[] = new byte[BUFFER]; 

			for(int i=0; i < files.length; i++) { 
				Log.v("Compress", "Adding: " + files[i]); 
				FileInputStream fi = new FileInputStream(files[i]); 
				origin = new BufferedInputStream(fi, BUFFER); 
				ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1)); 
				out.putNextEntry(entry); 
				int count; 
				while ((count = origin.read(data, 0, BUFFER)) != -1) { 
					out.write(data, 0, count); 
				} 
				origin.close(); 
			} 

			out.close(); 
		} catch(Exception e) { 
			e.printStackTrace(); 
		} 

	}

	/**Returns if the password setting is on or off*/
	public static boolean getPassOn (SharedPreferences file){
		return file.getBoolean(PASSON_KEY, false);
	}

	/**Returns if the tool tips (toasts) are on or off*/
	public static boolean getTipsOn (SharedPreferences file){
		return file.getBoolean(TIPS_KEY, false);
	}

	/**Sets the tool tips option in the preferences file on or off*/
	public static void setTipsOn (SharedPreferences file, boolean isOn){
		SharedPreferences.Editor editor = file.edit();
		editor.putBoolean(TIPS_KEY,isOn);
		editor.commit();
	}

	/**Sets the password option in the preferences file on or off*/
	public static void setPassOn (SharedPreferences file, boolean isOn){
		SharedPreferences.Editor editor = file.edit();
		editor.putBoolean(PASSON_KEY,isOn);
		editor.commit();
	}

	/**Sets the password for the preferences file from the input*/
	public static void setPassword (SharedPreferences file, String pass){
		SharedPreferences.Editor editor = file.edit();
		editor.putString(PASSWORD_KEY,pass);
		editor.commit();
	}

	/**Gets the password from the preferences file*/
	public static String getPassword(SharedPreferences file){
		return file.getString(PASSWORD_KEY, "");
	}

	/**Gets the default starting tab from the preferences file*/
	public static String getStartTab(SharedPreferences file){
		return file.getString(STARTTAB_KEY, "group");
	}

	/**Sets the default starting tab from the preferences file*/
	public static void setStartTab(SharedPreferences file, String name){
		SharedPreferences.Editor editor = file.edit();
		editor.putString(STARTTAB_KEY,name);
		editor.commit();
	}
}
