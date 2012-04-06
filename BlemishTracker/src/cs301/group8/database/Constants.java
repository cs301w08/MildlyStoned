package cs301.group8.database;

/** Constants class simply contains an organized list of a few constant values to be used throughout the application.  
 * 
 * @author group 08 <cs301-group8@ualberta.ca>
 * @version 2.0
 */
public interface Constants {
	
	// Global
	public static final String DATABASE_NAME = "blemdb";
	public static final int VERSION = 9;
	
	// Picture Constants
	public static final String PICTURETABLE = "blems";
	public static final String COL_TIME = "time";
	public static final String COL_PID = "pid";
	public static final String COL_NOTE = "note";
	// Foreign key COL_GROUP
	
	// Group Constants
	public static final String GROUPTABLE = "groups";
	public static final String COL_GID = "gid";
	public static final String COL_GROUP = "groupname";
	public static final String COL_REMINDER = "reminder";
	
	// Tag Constants
	public static final String TAGTABLE = "tags";
	public static final String COL_TID = "tid";
	public static final String COL_TAG = "tag";
	// Foreign key COL_GROUP
	
	// Timing
}
