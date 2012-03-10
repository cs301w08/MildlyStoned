package testdatabase;




import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;

import your.sample.code.R;
import cs301.group8.first.database.DatabaseHelper;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;



/**
 * @uml.dependency   supplier="asnment.waiguan.AsnmentwaiguanActivity"
 */
public class result extends Activity {
    /** Called when the activity is first created. */
    private EditText datatext1;
    private EditText datatext2;
    private EditText datatext3;
    private EditText datatext4;
    private Button create;
    //  private int i=0;
    
    @Override
       public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        create = (Button)findViewById(R.id.button1);
        create.setOnClickListener (new createListener());
    }
    
    public class createListener implements OnClickListener{
      
        @Override
        public void onClick(View v)
        {
            DatabaseHelper dbHelper = new DatabaseHelper(result.this,"test18");
   

            SQLiteDatabase db = dbHelper.getReadableDatabase();
    
            db.close();  
            String factor1 = datatext1.getText().toString();
            String factor2 = datatext2.getText().toString();
            String factor3 = datatext3.getText().toString();
            String factor4 = datatext4.getText().toString();
           
            if (factor4.length()==0){

                toase();
                return;
            }  
            if (factor3.length()==0){
     
                toase();
                return;
            }  
            if (factor2.length()==0){
            
                toase();
                return;
            }  
            if (factor1.length()==0){
  
                toase();
                return;
            }  
            Intent intent = new Intent();
            String jihe= factor1+" | " + factor2+" | "+ factor3+" | "+factor4+" | ";
            setResult(RESULT_OK);
            String text = jihe;
        
            ContentValues values = new ContentValues();
           
            values.put("date",factor1);
            values.put("station",factor2);
            values.put("fuelgrade",factor3);
            values.put("fuelamount",factor4);
       
            DatabaseHelper dbHelper1 = new DatabaseHelper(result.this,"test18");
            SQLiteDatabase db1 = dbHelper1.getWritableDatabase();
            db1.insert("nimei", null, values);
            db1.close();  
      //      DatabaseHelper dbHelper1 = new DatabaseHelper(result.this,"test8");
      //      SQLiteDatabase db1 = dbHelper.getReadableDatabase();
      //      Cursor cursor= db1.query("nimei1", new String[]{"totaldistance","totalfuel","totalrate"}, "id=?", new String[]{"1"}, null, null, null);
      //      while(cursor.moveToNext()){
      //          String totalfuel2 = cursor.getString(cursor.getColumnIndex("totalfuel"));
      //          String totaldistance2 = cursor.getString(cursor.getColumnIndex("totaldistance"));
      //          String totalrate = cursor.getString(cursor.getColumnIndex("totalrate"));
         
     //           }
     //       int fuelcost2 = Integer.parseInt("fuelcost");
     //       int fuelunitcost2= Integer.parseInt("fuelunitcost");
     //       int tripdistance = Integer.parseInt("tripdistance");


            result.this.startActivity(intent);

         
            
        }

        /**
         * @uml.property  name="databaseHelper"
         * @uml.associationEnd  inverse="jiaListener:database.huoxing.DatabaseHelper"
         * @uml.association  name="can add data"
         */
        private DatabaseHelper databaseHelper;

        /**
         * Getter of the property <tt>databaseHelper</tt>
         * @return  Returns the databaseHelper.
         * @uml.property  name="databaseHelper"
         */
        public DatabaseHelper getDatabaseHelper()
        
        
        
        
        
        
        {
        
            return databaseHelper;
        }

        /**
         * Setter of the property <tt>databaseHelper</tt>
         * @param databaseHelper  The databaseHelper to set.
         * @uml.property  name="databaseHelper"
         */
        public void setDatabaseHelper(DatabaseHelper databaseHelper)
        
        
        
        
        
        
        {
        
            this.databaseHelper = databaseHelper;
        }

        /** 
         * @uml.property name="databaseHelper1"
         * @uml.associationEnd inverse="jiaListener1:database.huoxing.DatabaseHelper"
         * @uml.association name="jiaListener will add a database at the first time"
         */
        private DatabaseHelper databaseHelper1;

        /** 
         * Getter of the property <tt>databaseHelper1</tt>
         * @return  Returns the databaseHelper1.
         * @uml.property  name="databaseHelper1"
         */
        public DatabaseHelper getDatabaseHelper1()
        
        
        
        
        
        {
            return databaseHelper1;
        }

        /** 
         * Setter of the property <tt>databaseHelper1</tt>
         * @param databaseHelper1  The databaseHelper1 to set.
         * @uml.property  name="databaseHelper1"
         */
        public void setDatabaseHelper1(DatabaseHelper databaseHelper1)
        
        
        
        
        
        {
            this.databaseHelper1 = databaseHelper1;
        }

        /** 
         * @uml.property name="addListener"
         * @uml.associationEnd inverse="jiaListener:asnment.waiguan.AsnmentwaiguanActivity.addListener"
         * @uml.association name="add will change page to jia listener"
         */
    //    private addListener addListener;

        /** 
         * Getter of the property <tt>addListener</tt>
         * @return  Returns the addListener.
         * @uml.property  name="addListener"
         */
      //  public addListener getAddListener()
        
        
        
        
      //  {
         //   return addListener;
       // }

        /** 
         * Setter of the property <tt>addListener</tt>
         * @param addListener  The addListener to set.
         * @uml.property  name="addListener"
         */
       // public void setAddListener(addListener addListener)
        
        
        
        
       // {
     //       this.addListener = addListener;
      //  }
        
    }
    
  /*  class chuangjianListener implements OnClickListener{

        @Override
        public void onClick(View v)
        {
            DatabaseHelper dbHelper = new DatabaseHelper(result.this,"test20");
            
            SQLiteDatabase db = dbHelper.getReadableDatabase();
    
            db.close();  
            String jieguo1 = "CREATE DATABASE Successfully";
            //  int factorOneInt = Integer.parseInt(factorOneStr);
             // int result =  factorOneInt ;
              jieguo.setText(jieguo1 + "");
        }
        
    }  */

    
    

    public void toase(){
        Toast toast=Toast.makeText(this, "Please fill in all blanks in form.", 2000);
        toast.setGravity(Gravity.CENTER, 0, 0);
         toast.show();
        
    }
}

