package cs301.group8.blem;

import cs301.group8.meta.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;



/** LoginActivity class is the first Activity seen by the user if there is a password present.  LoginActivity 
 * handles password input and compares to the stored password to see whether the user is allowed in.
 * 
 * @author Group 08 <cs301-group8@ualberta.ca>
 * @version 2.0
 */
public class LoginActivity extends Activity {

	/** Called when the activity is first created. 
	 * Reads settings from the preferences file and initializes the password
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		SharedPreferences settings = getSharedPreferences(Util.MY_PREFS_FILE, 0);
		if(!Util.getPassOn(settings)){
			startActivity(new Intent(this, BlemishTabActivity.class));
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verify);
		findViewById(R.id.button3).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				checkPassword();
			}
		});
	}

	public void onResume(){
		super.onResume();
		SharedPreferences settings = getSharedPreferences(Util.MY_PREFS_FILE, 0);
		if (!Util.getPassOn(settings)){
			this.finish();
		}else{

		}
	}

	/** checkPassword parses in the string value from the EditText field of the LoginActivity.
	 * checkPassword compares to to the password that is saved in the settings preferences. 
	 * If the input password equals the saved password, we are taken to the BlemishTabActivity class.
	 */
	private void checkPassword(){
		String password = ((EditText) findViewById(R.id.password)).getText().toString();
		SharedPreferences settings = getSharedPreferences(Util.MY_PREFS_FILE,0);
		if (password.equals(Util.getPassword(settings))){
			startActivity(new Intent(this, BlemishTabActivity.class));
		}
		else{
			Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE); 
			long [] pattern = {100,400};
			vibrator.vibrate(pattern,1);
			Toast t = Toast.makeText(getApplicationContext(), "Password Incorrect", Toast.LENGTH_SHORT);
			t.show();
			((EditText)findViewById(R.id.password)).setText("");
		}
	}

}