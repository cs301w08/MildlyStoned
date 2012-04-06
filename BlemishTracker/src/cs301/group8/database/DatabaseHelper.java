package cs301.group8.database;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import static cs301.group8.database.Constants.*;

/** DatabaseHelper class provides DatabaseHelper objects which are used in modifying tables in SQLite
 *
 * @author Group 08 <cs301-group8@ualberta.ca>
 * @version 1.0
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
	private static final String pictureTable = "create table " + PICTURETABLE + "(" + COL_PID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_TIME + " int," + COL_GROUP + " varchar(20), " +COL_NOTE + " varchar(200));";
	private static final String groupTable = "create table " + GROUPTABLE+ "(" + COL_GID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_GROUP + " varchar(20)," +COL_REMINDER + " int);";
	private static final String tagTable  =  "create table " + TAGTABLE + " (" + COL_TID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_TAG + " varchar(20), " + COL_PID + " int);";
	
	
	public  DatabaseHelper(Context context,String name,CursorFactory factory, int VERSION){
		super(context, name ,factory, VERSION);

	}
	public  DatabaseHelper(Context context,String name, int VERSION){
		this(context,name,null,VERSION);
	}
	public  DatabaseHelper(Context context,String name){
		this(context,name, VERSION);
	}
	public void onCreate(SQLiteDatabase db){
		Log.i("database", "Called create");
		
		Log.i("database", "Creating: " + pictureTable);
		db.execSQL(pictureTable);
		
		Log.i("database", "Creating: "+ groupTable);
		db.execSQL(groupTable);
		
		Log.i("database", "Creating: " + tagTable);
		db.execSQL(tagTable);
		
	}


	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		Log.i("database", "Upgrade for version change. Old: " + oldVersion + " new: " + newVersion);
		db.execSQL("DROP TABLE IF EXISTS " + PICTURETABLE);
		Log.i("database", "Dropped table: " + PICTURETABLE);
		
		db.execSQL("DROP TABLE IF EXISTS " + GROUPTABLE);
		Log.i("database", "Dropped table: " + GROUPTABLE);
		
		db.execSQL("DROP TABLE IF EXISTS " + TAGTABLE);
		Log.i("database", "Dropped table: " + TAGTABLE);
		
		onCreate(db);
		
	}   
}