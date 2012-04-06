package cs301.group8.blem;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import cs301.group8.database.AppDatabase;
import cs301.group8.meta.Picture;
import cs301.group8.meta.Util;

/**
 * ImageActivity class is an Android activity that displays the selected image
 * from the CompareActivity ImageView or from the BlemishActivity ListView. It
 * also displays appropriate meta-data regarding the selected photo. 
 * ImageActivity also has an EditText view that allows the user to input
 * string notes associated with the photo. When the user clicks on the image,
 * the activity is finished and the application returns to the previous
 * activity. ImageActivity also has EditText views to input values for "tags"
 * associated with each picture.
 * 
 * Finally, ImageActivity allows the user to select the "Menu" button on the 
 * phone or emulator, which allows for the user to email the photo, rotate 
 * the photo, or archive the photo.
 * 
 * @author Group 08 <cs301-group8@ualberta.ca>
 * @version 2.0
 */

public class ImageActivity extends Activity
{

	private Uri              imageUri;
	private Picture          pic;
	private AppDatabase      db;
	private ImageView        blemishImage;
	public int               settime;
	private File             imageFile; 
	private File             zipFile;
	private AlarmManager     alarmManager;
	private int              x=90;
	private Boolean			 tool;
	private SharedPreferences settings;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

	/**
	 * onCreate is called when the activity is initiated. onCreate pulls the
	 * image path, group, and date from the intent extras and sets the title of
	 * the image to the path to the image. onCreate sets the full screen
	 * ImageView object to the image based on the path it receives. onCreate
	 * also sets TextViews to display appropriate meta-data of the photo.
	 * Finally, onCreate sets the listener that allows the user to click the
	 * image and return to the previous activity
	 * 
	 * @param savedInstanceState
	 *            Bundle of information of the saved state from previous loads
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);

		setContentView(R.layout.imageactivity);

		Bundle bundle = getIntent().getExtras();
		pic = bundle.getParcelable("picture");

		imageFile = new File(Util.getPath(pic));
		if (pic != null)
		{
			TextView textView = (TextView) findViewById(R.id.imagePath);
			textView.setText(pic.getPath());

			((TextView) findViewById(R.id.image_group)).setText(pic.getGroup());
			((TextView) findViewById(R.id.image_date)).setText(Util
					.timeToDate(pic.getTime()));
		}

		Log.i("PATH", "open: " + imageFile.getAbsolutePath().toString());
		imageUri = Uri.fromFile(imageFile);

		blemishImage = (ImageView) findViewById(R.id.BlemishPic);
		blemishImage.setImageDrawable(Drawable.createFromPath(imageUri
				.getPath()));

		blemishImage.setOnClickListener(new View.OnClickListener()
		{

			public void onClick(View v)
			{

				// click on the image = go back to previous activity
				setNote();
				finish();
			}
		});
		db = new AppDatabase(this);
		MultiAutoCompleteTextView input = (MultiAutoCompleteTextView) findViewById(R.id.image_tags);
		input.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, db.getAllTags()));
		input.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

		android.util.Log.i("PERFORM", "Image: onCreate");
		settings = getSharedPreferences(Util.MY_PREFS_FILE, 0);
		tool = settings.getBoolean("tool", false);
		
		db.close();

	}

	/**
	 * Upon resuming the activity, the EditText field is set with any text
	 * string that the user has previously entered which has been saved in the
	 * database.
	 */
	public void onResume()
	{

		android.util.Log.i("PERFORM", "Image: onResume");
		super.onResume();
		db = new AppDatabase(this);

		EditText field = ((EditText) findViewById(R.id.image_note));
		pic.setNote(db.getNotes(pic));
		if (field != null){
			field.setText(pic.getNote());
			// Log.i
		} else
		{
			Log.i("error", "SOMETHING IS NULLLLLL");
		}
		field = ((EditText) findViewById(R.id.image_tags));
		if (field != null)
		{
			ArrayList<String> tags = db.getTags(pic.getId());
			if (tags != null)
			{
				String text = "";
				for (int i = 0; i < tags.size(); i++)
				{
					text += tags.get(i) + ((i + 1 != tags.size()) ? ", " : "");
				}
				field.setText(text);
			}
		}
	}

	/**
	 * Upon pausing the activity, any input text in the EditText field is loaded
	 * into the database through the addNote call.
	 */
	public void onPause()
	{

		android.util.Log.i("PERFORM", "Image: onPause");
		super.onPause();
		setNote();
		setTags();
		db.close();
	}

	/** setNote parses the input from the note EditText field and 
	 * stores it in the database with the addNote call.
	 */
	private void setNote(){

		String text = getText(((EditText) findViewById(R.id.image_note)));
		if ((!text.equals("")) && (pic.getNote() == null || !text.equals(pic.getNote()))){
			pic.setNote(text);
			db.addNote(pic.getId(), pic.getNote());
		}
	}

	/** setTags parses input from the tags EditText field and stores
	 * it in the database with the setTags method call.
	 */
	private void setTags(){

		String text = getText(((EditText) findViewById(R.id.image_tags)));
		db.setTags(pic.getId(), text.split(","));
	}

	/**getText takes in the EditText field and returns the string value associated
	 * with that field
	 * 
	 * @param field			The unique EditText widget
	 * @return current		String value of what is in the field
	 */
	private String getText(EditText field)
	{

		if (field != null && field.getText() != null)
		{
			String current = field.getText().toString();
			return current;
		} else
		{
			return ("");
		}
	}

	/**Declares the options displayed from the Menu button
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0,Menu.FIRST,0,"archive").setIcon(android.R.drawable.ic_menu_save);
		menu.add(0,Menu.FIRST+1,1,"email").setIcon(android.R.drawable.ic_menu_send);

		// menu.add(1,Menu.FIRST+2,2,"sound reconize").setIcon(android.R.drawable.ic_btn_speak_now);
		menu.add(1,Menu.FIRST+2,2,"rotate").setIcon(android.R.drawable.ic_menu_rotate);
		//menu.add(1,Menu.FIRST+3,3,"ceshi").setIcon(android.R.drawable.ic_menu_rotate);
		return super.onCreateOptionsMenu(menu);
	}

	/**Assigns methods for the options in the Android Menu for emailing, archiving,
	 * and rotating an image.  Makes the appropriate call to the Util class depending on the 
	 * menu item that was selected.
	 * 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case Menu.FIRST:
			String zipname=imageFile.getPath();
			String[] beforezip=new String[]{imageFile.getPath()};
			zipname=zipname.replace(".jpg", ".zip");
			System.out.println(imageFile.getPath());
			Util.zip(beforezip,zipname);
			zipFile=new File(zipname);
			if(tool){  Toast.makeText(getApplicationContext(), "Archive successfully! Path:"+zipname, Toast.LENGTH_LONG).show(); }
			break;

		case Menu.FIRST+1:
			Toast.makeText(this,"Email",Toast.LENGTH_SHORT).show();

			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(Intent.EXTRA_EMAIL, new String[]{"cs301-group8@ualberta.ca"});
			i.putExtra(Intent.EXTRA_SUBJECT,"BlemishPicture");
			i.putExtra(Intent.EXTRA_STREAM,imageUri);
			i.setType("image/jpeg");
			startActivity(Intent.createChooser(i, "select email app."));
			break;

		case Menu.FIRST+2:
			Bitmap bitmapOrg = BitmapFactory.decodeFile(imageUri.getPath());
			int width = bitmapOrg.getWidth();
			int height = bitmapOrg.getHeight();
			Matrix matrix = new Matrix();
			matrix.postRotate(x);
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,

					width, height, matrix, true);
			BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);
			x+=90;
			blemishImage.setImageDrawable(bmd);
			break;

		}
		return false;
	}
}
