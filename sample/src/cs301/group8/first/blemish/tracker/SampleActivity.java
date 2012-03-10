package cs301.group8.first.blemish.tracker;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
/***
 * @author Dylan Sheil
 * @ID 1238623
 * @CCID sheil
 * 
 * This Class is the source point of the FuelApp activity
 * It uses a listview with a custom layout for both the view and its rows
 * This is done via the InnerClass: CustomAdapter which extends adapter
 * 
 * All data storage is handled by talking to the LogWriter class
 * 
 * This class overrides onCreate, onCreateDialog, onListItemClick, 
 * 		onResume and ArrayAdapter.getEvent
 *
 */

public class SampleActivity extends ListActivity implements OnClickListener{
   
    private CustomAdapter listAdpt;
    private ListView logList;
    protected ArrayList<String> groups;
    
    
    
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		    
		    
		setContentView(R.layout.group_list);
		logList = this.getListView();
		   
		
		this.listAdpt = new CustomAdapter(this, R.layout.list_item, this.groups);
		logList.setAdapter(listAdpt);  
		
	//	View newLog = findViewById(R.id.new_log_button);
	//	View viewStats = findViewById(R.id.view_stats_button);
	//	newLog.setOnClickListener(this);
	//	viewStats.setOnClickListener(this);
		    
		
		android.util.Log.i("VERRIFY", "working");
    }

    @Override
    protected void onListItemClick(ListView l, View v,int position, long id){
    	
    	android.util.Log.i("VERRIFY", "Clicked item: " + position);
    	super.onListItemClick(l, v, position, id);
    	
    }
    
    

    @Override
    protected void onResume (){
    	android.util.Log.i("VERRIFY", "onResume");
        super.onResume();
    }
    
    /*
     * Method is only used to move the logs from the array list into the arrayadapter
     * and to notify the adapter of a change to the dataset
     * IT does not update the date itself
     */
    private void update(){
        
        if (listAdpt != null){
        	listAdpt.clear();
       //     for (int i=0;i<logs.size();i++){
       //     	listAdpt.add(logs.get(i));
       //     	android.util.Log.i("VERRIFY", "Added to ListAdpt " + i);
       //     }
            listAdpt.notifyDataSetChanged();
        }
        
        
     }
    

    
    /*
     * Removes a single log from the file.
     * Done by remove from arraylist and then rewriting entire list...
     */
      
    private class CustomAdapter extends ArrayAdapter<String>{

        private ArrayList<String> logs;
        
        public CustomAdapter (Context context, int res_id, ArrayList<String> l){
        	
            super (context, res_id, l);
            this.logs = l;
            
            android.util.Log.i("VERRIFY", "Custom Created");
            
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
      
        	View v = convertView;
        	
            return v;
        }
}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}



    
}   
