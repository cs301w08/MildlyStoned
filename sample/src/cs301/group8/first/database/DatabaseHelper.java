package cs301.group8.first.database;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;



public class DatabaseHelper extends SQLiteOpenHelper
{
private static final int VERSION = 1;
public  DatabaseHelper(Context context,String name,CursorFactory factory, int VERSION){
    super(context, name ,factory, VERSION);
    
}
public  DatabaseHelper(Context context,String name, int VERSION){
    this(context,name,null,VERSION);
}
public  DatabaseHelper(Context context,String name){
    this(context,name,VERSION);
}
public void onCreate(SQLiteDatabase db){

    db.execSQL("create table tabel1(pid int,path varchar(20),time varchar(20),group varchar(20));");

    System.out.println("create a database");
}

@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
{

    System.out.println("update a database");
    
}

/**
 * @uml.property  name="deleteoneListener"
 * @uml.associationEnd  inverse="databaseHelper:asnment.waiguan.AsnmentwaiguanActivity.deleteoneListener"
 * @uml.association  name="can delete data in database  "
 */


}
