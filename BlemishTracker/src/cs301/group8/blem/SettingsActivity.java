package cs301.group8.blem;

import java.io.File;

import cs301.group8.meta.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/** SettingsActivity is the activity in which the user can select a variety of settings to customize their app.
 * Settings that are currently developed:
 * 		-Enabling/setting a password
 *      -Enabling tool tips (toasts)
 *      -Setting the default MainActivity tab
 *      -Enabling the sound recognizer
 *      
 * Future settings that "will be" developed in the future:
 * 	    -Changing the theme/background
 *      -Adding specific users functionality (i.e. doctor, unique users, etc)
 *      
 * SettingsActivity is in charge of storing passwords and other customizable settings.
 * 
 * @author Group 08 <cs301-group8@ualberta.ca>
 * @version 2.0
 */
public class SettingsActivity extends Activity implements OnClickListener
{
	private CheckBox usePass;
	private CheckBox tooltip;
	private SharedPreferences settings;
	private Dialog dialog;

	/** onCreate establishes the spinners, textviews, and checkboxes that the user will interact with to adjust their settings
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		Spinner spinner = (Spinner) findViewById(R.id.settings_startscreen);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.settings_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());

		settings = getSharedPreferences(Util.MY_PREFS_FILE, 0);
		tooltip = (CheckBox) findViewById(R.id.settings_tooltip_check);
		usePass = (CheckBox) findViewById(R.id.settings_password_check);
		final Button setPass = (Button) findViewById(R.id.settings_password_button);
		
		tooltip.setChecked(Util.getTipsOn(settings));
		usePass.setChecked(Util.getPassOn(settings));
		
		usePass.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setPass.setEnabled(usePass.isChecked());
			}
		});
		setPass.setEnabled(usePass.isChecked());
		setPass.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				showDialog(Dialogs.DIALOG_CHANGE_PASSWORD);
				dialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			}
		}
		);
	}
	 @Override
         public boolean onCreateOptionsMenu(Menu menu) {
         menu.add(0,Menu.FIRST,0,"light sensor").setIcon(android.R.drawable.ic_btn_speak_now);
         
        // menu.add(1,Menu.FIRST+2,2,"sound reconize").setIcon(android.R.drawable.ic_btn_speak_now);
         //menu.add(1,Menu.FIRST+2,2,"rotate").setIcon(android.R.drawable.ic_menu_rotate);
             return super.onCreateOptionsMenu(menu);
         }
	  @Override
	    public boolean onOptionsItemSelected(MenuItem item){
	    switch(item.getItemId()){
	    case Menu.FIRST:
	   Toast.makeText(this,"light sensor",Toast.LENGTH_SHORT).show();
           Intent intent = new Intent();
           intent.setClass(SettingsActivity.this,SensorActivity.class);
           SettingsActivity.this.startActivity(intent);
	   break;

	
	    }
	    return false;
	    }
	@Override
	protected void onPause(){
		Log.i("PERFROM", "Settings: onPause");
		super.onPause();
		Log.i("VERRIFY", "Settings: setting tooltips to: " + tooltip.isChecked());
		Util.setTipsOn(settings, tooltip.isChecked());
		Log.i("VERRIFY", "Settings: setting password enabled to: " + usePass.isChecked());
		Util.setPassOn(settings, usePass.isChecked());
	}
	

	public class MyOnItemSelectedListener implements OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?>parent,View view, int pos,long id){
			SharedPreferences settings = getSharedPreferences(Util.MY_PREFS_FILE, 0);
			Util.setStartTab(settings, parent.getItemAtPosition(pos).toString());
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0){
		}
	}
	/**onCreateDialog dictates the DIALOG_CHANGE_PASSWORD dialog which allows the user to set a new password
	 * 
	 */
	protected Dialog onCreateDialog(int id){
		Dialog dialog = null;
		switch (id) {
		case Dialogs.DIALOG_CHANGE_PASSWORD:
			dialog = Dialogs.makeChangePassDialog(this, this,  this);
			break;
		default:
			break;
		}
		dialog.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				SettingsActivity.this.dialog = null;
			}
		});
		this.dialog = dialog;
		return dialog;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.dialog_ok_password:
			if (checkFields()){
				Util.setPassword(settings, ((EditText) dialog.findViewById(R.id.NewPassword)).getText().toString());
				Log.i("VERRIFY", "Settings: password set to `" + Util.getPassword(settings) +"`");
				dismissDialog(Dialogs.DIALOG_CHANGE_PASSWORD);
			}
			break;
		case R.id.dialog_cancel_password:
			dismissDialog(Dialogs.DIALOG_CHANGE_PASSWORD);
			break;
		default:
			break;
		}
	}
	
	/**checkFields parses the input from the fields in the DIALOG_CHANGE_PASSWORD dialog
	 * and uses the input to store the new password value.
	 * 
	 * @return boolean		Returns true if successful
	 */
	private boolean checkFields(){
		
		boolean pass = false;
		if(dialog == null){
			Log.i("VERRIFY", "Settings, checkFields: dialog not set");
			return false;
		}
		EditText oldPass = (EditText) dialog.findViewById(R.id.OldPassword);
		EditText newPass1 =(EditText) dialog.findViewById(R.id.NewPassword);
		EditText newPass2 =(EditText) dialog.findViewById(R.id.ConfirmPassword);
		
		
		if (oldPass!=null && newPass1!=null && newPass2 !=null){
			String error = "";
			if(!oldPass.getText().toString().equals(Util.getPassword(settings))){
				error = "Old pasword: `" + oldPass.getText() + "` does not match `" + Util.getPassword(settings) + "`";
			}
			else if (!newPass1.getText().toString().equals(newPass2.getText().toString())){
				error = "New passwords do not match";
			}
			else if (newPass1.getText().toString().equals(oldPass.getText().toString())){
				error = "Old password is the same as the new password";
			}
			else{
				error = "Password set";
				pass = true;
			}
			Log.i("VERRIFY", "Settings: " + error);
			Toast t = Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT);
			t.show();
			return pass;
		}else{
			oldPass = (EditText)findViewById(R.id.OldPassword);
			Object[] check = {oldPass, Util.getPassword(settings), newPass1, newPass2 };
			for(int i=0;i<check.length;i++){
				try{
					Log.i("VERRIFY", "Settings: checkFields, Item " + (i+1) + ": " + check[i].toString());
					
				}catch (NullPointerException e){
					Log.i("VERRIFY", "Settings: checkFields found null at item: " + (i+1));
				}
			}
			return false;
		}
		
		
		
		
	}
}