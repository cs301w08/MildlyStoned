package cs301.group8.blem;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;
import cs301.group8.database.AppDatabase;
import cs301.group8.meta.Picture;
import cs301.group8.meta.PictureListAdapter;
import cs301.group8.meta.Util;

/** SearchActivity class is one of the three tabs organized on the BlemishTabActivity main class.
 * SearchActivity allows for the user to input search strings and query the database to find unique photos
 * that correspond to the search.  SearchActivity is very responsive, and updates as the user inputs text 
 * character by character, and utilizing the auto-fill which prompts the user suggestions based on values
 * that already exist in the database.
 * 
 * @author Group 08 <cs301-group8@ualberta.ca>
 * @version 2.0
 */
public class SearchActivity extends ListActivity implements TextWatcher, OnItemSelectedListener{

	private AppDatabase db;
	private ArrayList<Picture> pics;
	private Spinner spinner ;


	/** onCreate is called when the activity is created in the application.  onCreate sets the listener for the 
	 * spinner (which the user selects to determine which type of value to search by), the EditText field for 
	 * search string input, and the ListView of pictures resulting from the search.
	 */
	public void onCreate(Bundle savedInstanceState) {
		Log.i("PERFOM", "Search: onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		pics = new ArrayList<Picture>();

		setListAdapter(new PictureListAdapter(this, R.layout.group_item, pics, "test", new OnClickListener() {
			public void onClick(View v) {
				setSpinner();
			}
		}));
		db = new AppDatabase(this);
		MultiAutoCompleteTextView input =(MultiAutoCompleteTextView)findViewById(R.id.search_text);
		input.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, db.getAllTags()));
		input.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
		input.addTextChangedListener(this);

		spinner = (Spinner) findViewById(R.id.search_type);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.search_array,android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);

		Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
		ArrayAdapter<CharSequence> mAdapter2 = ArrayAdapter.createFromResource(this, R.array.ModeArray,
				android.R.layout.simple_spinner_item);
		mAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(mAdapter2);
		spinner2.setOnItemSelectedListener(this);

		android.util.Log.i("VERRIFY", "Search: onCreate finished");

	}

	private boolean hasChecked(){
		boolean re = false;
		ListView lv = this.getListView();
		for (int i=0;i<lv.getChildCount();i++){
			if (((CheckBox) lv.getChildAt(i).findViewById(R.id.picture_list_check)).isChecked()){
				re = true;
				break;
			}
		}
		return re;
	}

	/**search takes in input from the EditText and sends the string value of the input and 
	 * the selected category from the spinner to the searcher from the database
	 */
	private  void search(){
		EditText input = (MultiAutoCompleteTextView) findViewById(R.id.search_text);
		String search = input.getText().toString();

		String type = spinner.getSelectedItem().toString().toLowerCase();
		String[] tags = search.split(",");
		ArrayList<Picture> temp= new ArrayList<Picture>();

		if (type.equals("tag")){
			for(String tag: tags){
				for (Picture pic : db.getPicsByTag(tag)){
					if (!temp.contains(pic)){
						temp.add(pic);
						Log.i("VERRIFY", "Search: added: " + pic.getPath());
					}
				}
			}
		}else if (type.equals("date")){
			for(String tag: tags){
				String pre = null;
				if(tag.contains(":")){
					pre = tag.substring(0,tag.indexOf(":")).trim();
					try{
						tag = tag.substring(tag.indexOf(":")+1).trim();
					}catch (IndexOutOfBoundsException e){
						continue;
					}
				}
				Log.i("VERRIFY", "pre: " + pre + "\ttag: " + tag);
				try{

					Date date = new SimpleDateFormat("dd/MM/yyyy").parse(tag);
					for (Picture pic : db.getPicsByDate(date.getTime(),pre)){
						if (!temp.contains(pic)){
							temp.add(pic);
							Log.i("VERRIFY", "Search: added: " + pic.getPath());
						}
					}
					Log.i("VERRIFY", "Searching by date: " + tag);
				}catch (ParseException e) {
					continue;
				}
			}

		}else if (type.equals("note")){
			for(String tag: tags){
				for (Picture pic : db.getPicsByNote(tag)){
					if (!temp.contains(pic)){
						temp.add(pic);
						Log.i("VERRIFY", "Search: added: " + pic.getPath());
					}
				}
			}

		}else Log.i("VERRIFY", "No such type |" + type + "|");


		Log.i("VERRIFY", "Searching finished");

		pics.clear();
		for (Picture pic: temp){
			pics.add(pic);
			Log.i("VERRIFY", "Search: added " + pic.getPath() + " to pics");
		}
		update(temp);
	}

	/**Update is called when the adapter is updated with new pictures so that the list
	 * can be refreshed to show the new results of the search.
	 */
	private void update(ArrayList<Picture> pics){
		Log.i("PERFORM", "Search: update");

		PictureListAdapter adpt = (PictureListAdapter) getListAdapter();
		adpt.clear();
		setSpinner();
		for (Picture pic : pics){
			Log.i("VERRIFY", "Added: " + pic.getPath());
			adpt.add(pic);
			//	View v = lv.getChildAt(count);
			//	count++;
			//	v.findViewById(R.id.picture_list_check).setVisibility(8);
		}
		adpt.notifyDataSetChanged();

	}



	/** onStart is called when the activity starts for the user. onStart creates the AppDatabase object and 
	 * pulls any existing groups from the database.  It fills an array of groups using the getGroups database method and sets 
	 * the ListView adapter to this array
	 */
	protected void onStart(){
		Log.i("PERFORM", "Search: onStart");
		super.onStart();
		db = new AppDatabase(this);
	}
	protected void onStop(){
		Log.i("PERFORM", "Search: onStop");
		super.onStop();
	}

	@Override
	protected void onResume (){
		android.util.Log.i("PERFORM", "Search: onResume");
		super.onResume();
		setSpinner();
		search();

	}

	public void setSpinner(){
		Spinner spinner = (Spinner) findViewById(R.id.spinner2);
		if (spinner == null) return;
		Log.i("Verrify", "Search: Setting spinners: " + hasChecked());
		if (hasChecked()){
			spinner.setVisibility(0);
		}else{
			spinner.setVisibility(8);
		}
	}

	protected void onPause(){
		super.onResume();
	}

	/**onCreateOptionsMenu declares the menu options that are seen when the user
	 * selects the "Menu" button
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0,Menu.FIRST,0,"sound reconize").setIcon(android.R.drawable.ic_btn_speak_now);
		//menu.add(0,Menu.FIRST+1,1,"email").setIcon(android.R.drawable.ic_menu_send);
		//menu.add(1,Menu.FIRST+2,2,"sound reconize").setIcon(android.R.drawable.ic_btn_speak_now);
		//menu.add(1,Menu.FIRST+2,2,"rotate").setIcon(android.R.drawable.ic_menu_rotate);
		return super.onCreateOptionsMenu(menu);
	}
	/**onOptionsItemSelected declares the functions associated with the menu options
	 * For this version of the App, the Menu allows us to use the sound recognizer 
	 * **NOTE THAT THIS DOES NOT WORK FOR THE COMPUTER EMULATOR BECAUSE THERE
	 * IS NO SOUND INPUT HARDWARE**
	 * 
	 * 
	 * Future development of the app will allow for more options 
	 * such as emailing or archiving the result of the search
	 * 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case Menu.FIRST:
			Toast.makeText(this,"sound reconize",Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.setClass(SearchActivity.this,SoundReconize.class);
			SearchActivity.this.startActivity(intent);
			break;

		}
		return false;
	}

	@Override
	public void afterTextChanged(Editable s) {
		Log.i("VERRIFY", "Search: text changed");
		search();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before,
			int count) {
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		Log.i("spinner", "Selcted called with ID: " + arg0.getId());
		Log.i("spinner", "spinner2: " + R.id.spinner2);
		Log.i("spinner", "search type: " + R.id.search_type);
		switch (arg0.getId()) {
		case R.id.search_type:
			search();
			break;
		case R.id.spinner2:
			Log.i("spinner", "Pos: " + pos);
			if (pos == 1) {
				deleteSelected();
			} else if (pos == 2) {
				archiveSelected();
			} else if (pos == 3) {
				emailSelected();
			}
			((Spinner) findViewById(R.id.spinner2)).setSelection(0);
			setSpinner();
			break;
		default:
			break;
		}

	}
	protected Dialog onCreateDialog(int which){
		Dialog dialog = null;
		final int pos = ((PictureListAdapter) getListAdapter()).position;
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
					search();
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
					for (int i = 0; i <getListView().getChildCount(); i++){
						View v = getListView().getChildAt(i);
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
					search();
				}
			}, new DialogInterface.OnClickListener() {
				// Set no listener
				@Override
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(Dialogs.DIALOG_DELETE_ITEM);
				}
			});
			break;


		default:
			break;
		}
		return dialog;
	}

	private void deleteSelected() {

		try{
			this.showDialog(Dialogs.DIALOG_DELETE_ALL);
		}catch (Exception e){
			//	Log.i("error", "Unable to delete " + Util.getPath(pic));
		}

	}

	/**archiveSelected is called when the user selects the Archive Selected options from the Mode spinner.
	 * archiveSelected gets the list of selected pictures and creates an archived zipfile with the name value
	 * of the current time in milliseconds.  This archive file is saved to the sd card
	 */
	private void archiveSelected() {
		ListView lv = this.getListView();

		List<String> zoom = new ArrayList<String>();
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
		if(Util.getTipsOn(getSharedPreferences(Util.MY_PREFS_FILE, 0))){  Toast.makeText(getApplicationContext(), "Archive successfully! Path:"+zipname, Toast.LENGTH_LONG).show(); }
	}

	/**emailSelected is called when the user selects the Email selected option from the Mode spinner.
	 * This function gets the list of selected pictures and creates an archived zipfile of the selected pictures, and starts
	 * the email activity to send them to a target audience
	 */
	private void emailSelected() {
		ListView lv = this.getListView();
		List<String> zoom = new ArrayList<String>();
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

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}