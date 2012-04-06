package cs301.group8.blem;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

/** BlemishTabActivity class is the activity that is first called upon initiation of the application.  BlemishTabActivity populates the 
 * ListView with names of blemish groups that the user has already initiated (using database calls).  BlemishTabActivity also 
 * initiates buttons that allow the user to create new blemish groups (through a dialog) or delete existing groups. 
 * When BlemishTabActivity first starts, it initiates the database object that will be used to track all of our photos. 
 * 
 * BlemishTabActivity has three tabs:
 * 
 * The Groups tab displays the list of all groups that the user can add to, select, or delete.
 * The Search tab displays the search bar and allows the user to search by tag, note, or date for unique pictures.
 * The Settings tab allows the user to customize their app by changing options and adding, removing, or changing passwords.
 */
public class BlemishTabActivity extends TabActivity
{
	public static final String PREFS_NAME = "MyPrefsFile";
	/** Called when the activity is first created. 
	 * onCreate declares the three tabs from their respective activities:
	 * Groups tab: MainActivity
	 * Settings tab: SettingsActivity
	 * Search tab: SearchActivity
	 * 
	 * onCreate first loads from the settings file to see which tab should be the default for the user to see first
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String settabfirst = settings.getString("firsttab","group");

		//   Resources res = getResources();
		TabHost tabHost = getTabHost();

		Intent intent;

		// Tab for groups
		intent = new Intent().setClass(this, MainActivity.class);
		tabHost.addTab(tabHost.newTabSpec("Groups").setIndicator("Groups").setContent(intent));

		// Tab for Search
		intent = new Intent().setClass(this, SearchActivity.class);
		tabHost.addTab(tabHost.newTabSpec("Search").setIndicator("Search").setContent(intent));

		// Tab for Settings
		intent = new Intent().setClass(this, SettingsActivity.class);
		tabHost.addTab(tabHost.newTabSpec("Settings").setIndicator("Settings").setContent(intent));

		final int tabChildrenCount = tabHost.getTabWidget().getChildCount();
		for (int i = 0; i < tabChildrenCount; i++) {
			tabHost.getTabWidget().getChildAt(i).getLayoutParams().height=40;

		}
		// tabHost.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
		//tabHost.requestLayout();
		if(settabfirst.equals("Group")){
			tabHost.setCurrentTab(0);
		}
		if(settabfirst.equals("Search")){
			tabHost.setCurrentTab(2);
		}
		if(settabfirst.equals("Setting")){
			tabHost.setCurrentTab(2);
		}

	}
}

