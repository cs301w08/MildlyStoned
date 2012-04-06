package cs301.group8.blem;


import java.util.ArrayList;
import java.util.Calendar;

import cs301.group8.blem.Dialogs;
import cs301.group8.database.AppDatabase;
import cs301.group8.meta.Util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/** MainActivity class is the activity that populates the list of group names.  MainActivity is called by the 
 * BlemishTabActivity as the "Groups" tab to display the list of all groups present.  MainActivity also allows 
 * for the user to create a new group, by calling the ADD_GROUP dialog.
 * 
 * @author Group 08 <cs301-group8@ualberta.ca>
 * @version 2.0
 */

public class MainActivity extends ListActivity implements View.OnClickListener{

	private Dialog dialog = null;
	private AppDatabase db;

        public boolean tool;
        public static final String PREFS_NAME = "MyPrefsFile";
        private static final int VOICE_RECOGNITION_REQUEST_CODE = 0;
	protected ArrayAdapter<CharSequence> mAdapter;
	protected int mPos;
	protected String mSelection;

	protected ArrayList<String> groups;
	private CustomAdapter listAdpt;
	private int pos;
	/** onCreate is called when the activity is created in the application.  onCreate sets the listener for the 
	 * add blemish button.  onCreate reads the settings file to check for user preferences to determine which tab is the default 
	 * tab seen.  Also, if a password is set, then MainActivity is no longer the first activity that is called when the app starts.
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_list);
		     SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	                tool = settings.getBoolean("tool", false);

		findViewById(R.id.add_blemish).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(Dialogs.DIALOG_ADD_GROUP);
			}
		});
		
		android.util.Log.i("PERFORM", "MAIN: onCreate");


	}
	 

	/** onStart is called when the activity starts for the user. onStart creates the PictureDatabase object and 
	 * pulls any existing groups from the database.  It fills an array of groups using the getGroups database method and sets 
	 * the ListView adapter to this array
	 */
	protected void onStart(){
		android.util.Log.i("PERFORM", "MAIN: onStart");
		super.onStart();
		db = new AppDatabase(this);
		groups = db.getGroups();
		listAdpt = new CustomAdapter(this, R.layout.group_item);
		this.getListView().setAdapter(listAdpt); 

	}
	protected void onStop(){
		android.util.Log.i("PERFORM", "MAIN: onStop");
		super.onStop();
		db.close();

	}

	@Override
	protected void onResume (){
		android.util.Log.i("PERFORM", "MAIN: onResume");
		super.onResume();
		update();
	}
	private void update(){
		android.util.Log.i("PERFORM", "MAIN: update");
		if (listAdpt != null){
			groups = db.getGroups();
			listAdpt.clear();
			for (int i=0;i<groups.size();i++){
				listAdpt.add(groups.get(i));
				android.util.Log.i("VERRIFY", "Added to ListAdpt " + i);
			}
		}
		listAdpt.notifyDataSetChanged();

	}
	
	protected void onListItemClick(ListView l, View v,int position, long id){
		android.util.Log.i("VERRIFY", "Clicked item: " + position + " String " + groups.get(position));
		super.onListItemClick(l, v, position, id);

		Intent intent = new Intent(getApplicationContext(), BlemishActivity.class);
		intent.putExtra("groupName", groups.get(position));
		db.close();
		startActivity(intent);
	}

	/** onCreateDialog is executed when MainActivity is first started.  This method initiates the two 
	 * dialogs for this class by calling the makeDialog method for both types ('add group' and 'delete item').
	 * onCreateDialog returns the dialog object to the activity.
	 * 
	 * @param id			integer value corresponding to the identification number of each unique dialog
	 * 
	 * @return dialog		dialog object returned to the activity
	 */
	@Override
	protected Dialog onCreateDialog(int id){
		Dialog dialog = null;
		switch (id) {
		case Dialogs.DIALOG_ADD_GROUP:
			dialog = Dialogs.makeAddGroupDialog(this, this, this);
			this.dialog = dialog;
			Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner1);
			this.mAdapter = ArrayAdapter.createFromResource(this, R.array.ReminderArray,
					android.R.layout.simple_spinner_item);
			OnItemSelectedListener spinnerListener = new myOnItemSelectedListener(this,this.mAdapter);
			mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(this.mAdapter);
			spinner.setOnItemSelectedListener(spinnerListener);
			break;
		case Dialogs.DIALOG_DELETE_ITEM:
			final int position = pos;
			dialog = Dialogs.makeDeleteDialog(this, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					db.deleteGroup(groups.get(position));
					update();
					removeDialog(Dialogs.DIALOG_DELETE_ITEM);
				}
			}, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(Dialogs.DIALOG_DELETE_ITEM);
				}
			});
		default:
			break;
		}
		return dialog;

	}
	/** onClick is called when the user presses the Add Blemish button.  onClick initiates the 'add blemish' dialog
	 * and draws user input from the EditText regarding the group name that is desired by the user.  As well, the edit text 
	 * field of the reminder time and pulldown spinner menu for reminder type are visible. If the group name
	 * already exists, onClick produces a toast to inform the user that the group name cannot be duplicated.  Otherwise,
	 * onClick adds the group to the database with the addGroup method and starts an intent to transfer to BlemishActivity.
	 * onClick also adds the reminder appropriate reminder time in milliseconds to the reminder database
	 *
	 * @param v 		The view element that was clicked on by the user (Add Blemish button in this case)
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_add_blemish:
			if (this.dialog != null){
				String value = ((EditText) dialog.findViewById(R.id.BlemishGroup)).getText().toString();
				//int value of reminder 
				String reminderFreqString = ((EditText) dialog.findViewById(R.id.ReminderFreq)).getText().toString();
				float reminderFreq;
				if (reminderFreqString.equals("") || reminderFreqString == null){
					reminderFreq = 0;
				} else {
					reminderFreq = Float.parseFloat(reminderFreqString);
				}

				if (value.equals("") || value == null){
					//user enters an empty string
					removeDialog(Dialogs.DIALOG_ADD_GROUP);
					if(tool){Toast.makeText(getApplicationContext(), "Error: Cannot add a group with an empty name.",
							Toast.LENGTH_SHORT).show();}
					Log.i("DATA", "Error: Cannot add empty string group");
				} else {
					//value has something significant
					if (!db.groupExists(value)){
						long reminder = Util.parseReminder(mPos, reminderFreq);
						removeDialog(Dialogs.DIALOG_ADD_GROUP);
						db.updateGroup(value, null, reminder);
						Intent intent = new Intent(getApplicationContext(), BlemishActivity.class);
						intent.putExtra("groupName", value);
						intent.putExtra("freqFloat", reminderFreq);
						startActivity(intent);
					} else {
						removeDialog(Dialogs.DIALOG_ADD_GROUP);
					if(tool)	{Toast.makeText(getApplicationContext(), "Error: Cannot add '" + value + "' as group already exists",
								Toast.LENGTH_SHORT).show();}
						Log.i("DATA", "Error: Cannot add group as group already exists");
					}
				}
			}
			break;
		case R.id.dialog_no_blemish:
			removeDialog(Dialogs.DIALOG_ADD_GROUP);
			break;
		default:
			break;
		}
	}

	private class myOnItemSelectedListener implements OnItemSelectedListener {

		/*
		 * provide local instances of the mLocalAdapter and the mLocalContext
		 */

		ArrayAdapter<CharSequence> mLocalAdapter;
		Activity mLocalContext;

		/**
		 *  Constructor
		 *  @param c - The activity that displays the Spinner.
		 *  @param ad - The Adapter view that
		 *    controls the Spinner.
		 *  Instantiate a new listener object.
		 */
		public myOnItemSelectedListener(Activity c, ArrayAdapter<CharSequence> ad) {

			this.mLocalContext = c;
			this.mLocalAdapter = ad;

		}

		/**
		 * When the user selects an item in the spinner, this method is invoked by the callback
		 * chain. Android calls the item selected listener for the spinner, which invokes the
		 * onItemSelected method.
		 *
		 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(
		 *  android.widget.AdapterView, android.view.View, int, long)
		 * @param parent - the AdapterView for this listener
		 * @param v - the View for this listener
		 * @param pos - the 0-based position of the selection in the mLocalAdapter
		 * @param row - the 0-based row number of the selection in the View
		 */
		public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {

			mPos = pos;
			mSelection = parent.getItemAtPosition(pos).toString();
			/*
			 * Set the value of the text field in the UI
			 */
		}

		/**
		 * The definition of OnItemSelectedListener requires an override
		 * of onNothingSelected(), even though this implementation does not use it.
		 * @param parent - The View for this Listener
		 */
		public void onNothingSelected(AdapterView<?> parent) {

			// do nothing

		}
	}
	
	
	private class CustomAdapter extends ArrayAdapter<String>{

		public CustomAdapter (Context context, int res_id){
			super (context, res_id, groups);
			android.util.Log.i("VERRIFY", "Custom Created");
		}

		/** getView is called for each element in the list.  It allows for us to place a button (the X delete button) inside
		 * each ListView item based on the layout file.  getView returns 'row', the custom view used to populate the ListView
		 *
		 * @param position		position of the element in the ListView
		 * @param convertView		
		 * @param parent		parent viewgroup that is passed to the inflater
		 *
		 * @return row			row is the custom view used to populate the ListView in the MainActivity
		 */
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			if (groups == null){
				return convertView;
			}

			LayoutInflater inflater=getLayoutInflater();
			View row=inflater.inflate(R.layout.group_item, parent, false);
			((Button) row.findViewById(R.id.group_remove_button)).setOnClickListener(new OnClickListener() {
				/** onClick is called when the user presses the X button for a particular group. onClick shows the 
				 * 'delete item' dialog which prompts the user to confirm if they want to delete the group or not.
				 *
				 * @param v 		The view element that was clicked on by the user (X button in this case)
				 */
				public void onClick(View v) {
					pos = position;
					showDialog(Dialogs.DIALOG_DELETE_ITEM);
				}
			});

			TextView label=(TextView)row.findViewById(R.id.title_text);
			String group = groups.get(position);

			String format = "%-2s%3d pics%12s%-20s";
			label.setText(String.format(format, (Util.checkReminder(db.getReminder(group), db.getMostRecent(group))) ? "*" : "",  db.getPicCount(group),"", group));

			return row;
		}
	}
}