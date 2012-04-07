package cs301.group8.blem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cs301.group8.database.AppDatabase;
import cs301.group8.meta.Picture;
import cs301.group8.meta.PictureListAdapter;
import cs301.group8.meta.Util;

/** BlemishActivity is an Android activity that displays the list of existing photos for a specific type of blemish.
 * BlemishActivity is launched when the user selects a blemish group from the main menu.
 * BlemishActivity also establishes buttons that allow the user to take pictures or delete them from memory.
 * 
 * In the most recent addition to the project, we added checkboxes that allow the user to "batch delete", 
 * "batch email" or "batch archive" based on checking the desired photos and selecting the function from the 
 * Mode dropdown spinner.
 * 
 * Finally, BlemishActivity allows the user to click on the cog image in the top right corner which brings up
 * a dialog allowing the user to change the group name and/or reminder time associated with those pictures.
 *
 * @author Group 08 <cs301-group8@ualberta.ca>
 * @version 2.0
 */

public class BlemishActivity extends ListActivity implements View.OnClickListener{

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri imageUri;
	private AppDatabase db;
	private Dialog dialog = null;
	public ListView lv;

	protected ArrayAdapter<CharSequence> mAdapter;
	protected ArrayAdapter<CharSequence> mAdapter2;
	protected int mPos;
	protected String mSelection;

	private PictureListAdapter listAdpt;
	protected ArrayList<Picture> pics;
	private String blemishGroup;
	private Picture pic = null;
	public boolean tool;
	public static final String PREFS_NAME = Util.MY_PREFS_FILE;
	private int position;
	public boolean deleteFlag;

	/** onCreate establishes the clickable ListView of blemishes and the Add Blemish button.
	 * onCreate populates the custom list adapter using the getPictures database call.
	 * onCreate also declares the checkboxes and spinner used for "batch calls"
	 * 
	 * @param savedInstanceState		Bundle of information of the saved state from previous loads
	 */
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		android.util.Log.i("PERFORM", "Blemish: onCreate");
		setContentView(R.layout.picture_list);
		db = new AppDatabase(this);
		lv = this.getListView();

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		tool = settings.getBoolean("tool", false);
		findViewById(R.id.add_blemish).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				takeAPhoto();
			}
		});

		((Button)this.findViewById(R.id.add_blemish)).setText("Take a Photo");

		blemishGroup = this.getIntent().getStringExtra("groupName");
		pics = db.getPictures(blemishGroup);

		findViewById(R.id.EditGroupButton).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(Dialogs.DIALOG_ADD_GROUP);
			}
		});


		this.listAdpt = new PictureListAdapter(this, R.layout.group_item, pics, blemishGroup);
		this.getListView().setAdapter(listAdpt);

		Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
		this.mAdapter2 = ArrayAdapter.createFromResource(this, R.array.ModeArray,
		android.R.layout.simple_spinner_item);
		mAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(this.mAdapter2);
		spinner2.setOnItemSelectedListener(new MyModeItemSelectedListener());


		Toast t = Toast.makeText(getApplicationContext(), "Long click to compare images", Toast.LENGTH_SHORT);
		t.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 85);
		if(tool){t.show();}
		android.util.Log.i("VERR", "Blemish: onCreate finished");
		db.close();

	}


	protected void onStart(){
		android.util.Log.i("PERFORM", "Blemish: onStart");
		super.onStart();
		db = new AppDatabase(this);
	}
	protected void onStop(){
		android.util.Log.i("PERFORM", "Blemish: onStop");
		super.onStop();
		db.close();
	}
	@Override
	protected void onResume (){
		android.util.Log.i("PERFORM", "Blemish: onResume");
		update();
		super.onResume();
	}

	/** When called, update refreshes the list adapter with new pictures (from the database)
	 * and updates what is seen in the GUI
	 */
	private void update(){
		android.util.Log.i("PERFORM", "Blemish: update");
		// Make sure the list adapter has been set and then add the pictures to it
		String current = ((TextView)findViewById(R.id.BlemishViewTitle)).getText().toString();
		if (current==null || !current.equals(blemishGroup)){
			((TextView)findViewById(R.id.BlemishViewTitle)).setText(blemishGroup);
		}

		if (listAdpt != null){		
			pics = db.getPictures(blemishGroup);
			listAdpt.clear();
			for (int i=0;i<pics.size();i++){
				listAdpt.add(pics.get(i));
				android.util.Log.i("VERRIFY", "Added to ListAdpt " + i);
			}
		}
		// Notify to update GUI
		listAdpt.notifyDataSetChanged();

	}

	/**onClick handles the input from the Dialog EditText fields and Reminder fields.  This dialog is called 
	 * when the "Cog" image is pressed by the user, which allows them to change the group name and/or reminder time
	 * associated with the group.
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
					if(tool){Toast.makeText(getApplicationContext(), "Error: Cannot rename a group with an empty name.",
							Toast.LENGTH_SHORT).show();}
					Log.i("DATA", "Error: Cannot add empty string group");
				} else {
					//value has something significant
					if ((!db.groupExists(value)) || (blemishGroup.equals(value))){
						long reminder = Util.parseReminder(mPos, reminderFreq);
						removeDialog(Dialogs.DIALOG_ADD_GROUP);
						db.updateGroup(blemishGroup, value, reminder);
						blemishGroup = value;
						update();
					} else {
						removeDialog(Dialogs.DIALOG_ADD_GROUP);
						if(tool){Toast.makeText(getApplicationContext(), "Error: Cannot add '" + value + "' as group already exists",
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

	/** onCreateDialog calls the make dialog method in the Dialogs class for the delete item dialog.  It sets the
	 *  three Dialogs which the user can call from this activity:
	 *  
	 *  DIALOG_DELETE_ITEM: called when the user selects an [X] button which deletes the single picture if confirmed.
	 *  DIALOG_ADD_GROUP:   called when the user selects the cog icon which allows them to edit the group name/reminder.
	 *  					Uses appropriate database calls to change the group name/reminder if confirmed.
	 *  DIALOG_DELETE_ALL:	called when the user selects a subset of pictures to "batch delete".  Deletes all selected pictures.
	 *  					if confirmed.
	 *  
	 *  @param which	integer corresponding to which dialog ID is being initiated
	 */
	@Override
	protected Dialog onCreateDialog(int which){
		Dialog dialog = null;
		final int pos = position;
		switch (which) {
		case Dialogs.DIALOG_DELETE_ITEM:
			dialog = Dialogs.makeDeleteDialog(this, new DialogInterface.OnClickListener() {
				// Set yes listener
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Picture pic = pics.get(pos);
					new File(Util.getPath(pic)).delete();
					try {
						db.deletePicture(pic);
					} catch (Exception e) {
						Log.i("error", "Unable to delete pic " + pic.getPath());
						e.printStackTrace();
					}
					removeDialog(Dialogs.DIALOG_DELETE_ITEM);
					update();
				}
			}, new DialogInterface.OnClickListener() {
				// Set no listener
				@Override
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(Dialogs.DIALOG_DELETE_ITEM);
				}
			});
			break;
		case Dialogs.DIALOG_DELETE_ALL:
			dialog = Dialogs.makeDeleteAll(this, new DialogInterface.OnClickListener() {
				// Set yes listener
				@Override
				public void onClick(DialogInterface dialog, int which) {
					for (int i = 0; i < lv.getChildCount(); i++){
						View v = lv.getChildAt(i);
						CheckBox box = (CheckBox) v.findViewById(R.id.picture_list_check);
						if (box.isChecked()){
							Picture pic = pics.get(i);
							new File(Util.getPath(pic)).delete();
							try {
								db.deletePicture(pic);
							} catch (Exception e) {
								Log.i("error", "Unable to delete pic " + pic.getPath());
								e.printStackTrace();
							}
						}
					}
					removeDialog(Dialogs.DIALOG_DELETE_ALL);
					update();
				}
			}, new DialogInterface.OnClickListener() {
				// Set no listener
				@Override
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(Dialogs.DIALOG_DELETE_ITEM);
				}
			});
			break;
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
			int selection = Util.getSelection(db.getReminder(blemishGroup));
			spinner.setSelection(selection); 

			dialog.setTitle("Edit Blemish Group");

			float freqFloat = Util.getFreqFloat(db.getReminder(blemishGroup));

			EditText groupfield = ((EditText) dialog.findViewById(R.id.BlemishGroup));
			if(groupfield != null){
				groupfield.setText(blemishGroup);
			}

			EditText reminderfield = ((EditText) dialog.findViewById(R.id.ReminderFreq));
			if(reminderfield != null){
				reminderfield.setText(Float.toString(freqFloat));
			}

			break;

		default:
			break;
		}
		return dialog;
	}

	/** takeAPhoto is the method that calls the camera through an intent and initiates the picture object.
	 * takeAPhoto uses a system call to get the current time in milliseconds and creates a new picture object 
	 * based on the current group and the current time.  It also creates the Uri object that will be passed to the
	 * camera so that the camera knows to save the image in the appropriate folder.  If the folder (designated by 
	 * group name) does not exist, it will first create it.
	 */
	protected void takeAPhoto(){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// Create the dir to store images
		String folder = Util.getRootPath();
		File folderF = new File(folder+blemishGroup);

		if (!folderF.exists()) {
			folderF.mkdir();
		}

		// Created picture and pass uri
		this.pic = new Picture(blemishGroup, System.currentTimeMillis());

		Log.i("take", pic.getPath());

		File imageFile = new File(Util.getPath(pic));
		imageUri = Uri.fromFile(imageFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	/** deleteSelected is called when the user selects the Delete Selected option from the Mode spinner.
	 * deleteSelected creates the DELETE_ALL dialog which confirms if the user wants to delete the subset of images
	 */
	private void deleteSelected() {

		try{
			this.showDialog(Dialogs.DIALOG_DELETE_ALL);
		}catch (Exception e){
			Log.i("error", "Unable to delete " + Util.getPath(pic));
		}

	}

	/**archiveSelected is called when the user selects the Archive Selected options from the Mode spinner.
	 * archiveSelected gets the list of selected pictures and creates an archived zipfile with the name value
	 * of the current time in milliseconds.  This archive file is saved to the sd card
	 */
	private void archiveSelected() {
		ListView lv = this.getListView();

		List zoom = new ArrayList<String>();
		for (int i = 0; i < lv.getChildCount(); i++){
			View v = lv.getChildAt(i);
			CheckBox box = (CheckBox) v.findViewById(R.id.picture_list_check);
			if (box.isChecked()){
				Log.i("ischecked", "count");
				Picture pic = pics.get(i);
				zoom.add(Util.getPath(pic));

			}

		}
		String[] beforezip  = new String[zoom.size()];
		zoom.toArray(beforezip);
		String zipname=String.valueOf("/sdcard/"+System.currentTimeMillis()+".zip");
		Log.i("zippath", zipname);
		Util.zip(beforezip,zipname);
		if(tool){  Toast.makeText(getApplicationContext(), "Archive successfully! Path:"+zipname, Toast.LENGTH_LONG).show(); }
	}

	/**emailSelected is called when the user selects the Email selected option from the Mode spinner.
	 * This function gets the list of selected pictures and creates an archived zipfile of the selected pictures, and starts
	 * the email activity to send them to a target audience
	 */
	private void emailSelected() {
		ListView lv = this.getListView();
		List zoom = new ArrayList<String>();
		for (int i = 0; i < lv.getChildCount(); i++){
			View v = lv.getChildAt(i);
			CheckBox box = (CheckBox) v.findViewById(R.id.picture_list_check);
			if (box.isChecked()){
				Picture pic = pics.get(i);
				zoom.add(Util.getPath(pic));
			}
		}    
		String[] beforezip  = new String[zoom.size()];
		zoom.toArray(beforezip);
		String zipname=String.valueOf("/sdcard/"+System.currentTimeMillis()+".zip");
		Log.i("zippath", zipname);
		Util.zip(beforezip,zipname);
		File zipFile=new File(zipname);
		Intent intentq = new Intent(Intent.ACTION_SEND);
		intentq.setType("text/plain");
		intentq.putExtra(Intent.EXTRA_EMAIL, new String[]{"cs301-group8@ualberta.ca"});
		intentq.putExtra(Intent.EXTRA_SUBJECT,"BlemishPicture");
		intentq.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(zipFile));
		intentq.setType("application/zip");
		startActivity(Intent.createChooser(intentq, "select email app."));
	}

	/** onActivityResult is called upon the return of the camera call.  It checks that the results of the camera
	 * are appropriate and then creates a toast to inform the user that the picture was taken correctly.  Finally,
	 * onActivityResult adds the picture object to the database through the addPicture() method.
	 * 
	 * @param requestCode		integer request code returned from the camera.  Should be 100
	 * @param resultCode		integer result code returned from the camera.  Should be RESULT_OK
	 * @param intent			caller intent
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){
		//check request code, check result code, show image on image button
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
			if (resultCode == RESULT_OK){

				db.addPicture(pic);
			} 
		}
		// After the pic has been added set it back to null
		pic = null;

	}

	/**MyModeItemSelectedListener is the listener for the Mode spinner.  Depending on which selection is pressed, the 
	 * listener calls the appropriate function to act on the subset of checked images in the listview.
	 *
	 */
	public class MyModeItemSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?>parent,View view, int pos,long id){
			if (pos == 1) {
				deleteSelected();
			} else if (pos == 2) {
				archiveSelected();
			} else if (pos == 3) {
				emailSelected();
			}
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0){
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
		 * @param v -   the View for this listener
		 * @param pos - the 0-based position of the selection in the mLocalAdapter
		 * @param row - the 0-based row number of the selection in the View
		 */
		public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {

			mPos = pos;
			mSelection = parent.getItemAtPosition(pos).toString();			

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



}   
