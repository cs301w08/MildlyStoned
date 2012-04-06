package cs301.group8.blem;

import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;


import cs301.group8.database.AppDatabase;
import cs301.group8.meta.Picture;
import cs301.group8.meta.Util;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/** CompareActivity class is an Android activity class that allows the user to view two blemishes at a time, side by side, 
 * for comparative reasons.  The activity allows the user to cycle through the pictures of the blemish group and long click
 * the top image to set it to the bottom image view.
 *
 * @author Group 08 <cs301-group8@ualberta.ca>
 * @version 2.0
 */

public class CompareActivity extends Activity{

	private AppDatabase db;
	private ArrayList<Picture> pics;
	private int current;
	private int constant;
	private int x=90;
	private int y=90;
	private boolean left_hidden=false;
	private boolean right_hidden=false;
	/** onCreate recalls the AppDatabase and gets the pictures based on the group name (pulled from the intent extras).
	 *  onCreate initializes both image views to be the the selected picture initially.  onCreate also sets listeners for 
	 *  arrow buttons to scroll through the top image, and on the image itself so that the user can click an image and be 
	 *  taken to the ImageActivity activity.
	 *  
	 *  @param savedInstanceState		Bundle of information regarding the state of the activity from previous loads
	 * 
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.compare_activity);
		Intent intent = getIntent();
		db = new AppDatabase(this);
		pics = intent.getParcelableArrayListExtra("pics");
		

		current = intent.getIntExtra("position", 0);
		constant = current;


		findViewById(R.id.compare_next).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (current + 1 < pics.size()){
					current ++;
					setTop();
				}

			}
		});
		findViewById(R.id.compare_prev).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (current > 0){
					current --;
					setTop();
				}
			}
		});

		findViewById(R.id.compare_image_frist).setOnLongClickListener( new OnLongClickListener() {
			public boolean onLongClick(View v) {
				constant = current;
				setBottom();
				return true;
			}
		});
		findViewById(R.id.compare_image_frist).setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
				intent.putExtra("picture", pics.get(current));
				startActivity(intent);
			}
		});
		findViewById(R.id.compare_image_second).setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
				intent.putExtra("picture", pics.get(constant));

				startActivity(intent);
			}
		});

		setTop();
		setBottom();
		Toast t = Toast.makeText(getApplicationContext(), "Long click to set bottom image", Toast.LENGTH_SHORT);
		t.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
		t.show();

		android.util.Log.i("PERFORM", "Compare: onCreate");
	}

	/** setTop method is called when the user is cycling through the pictures with the arrow buttons.  setTop
	 * calls the new path of the image based on the relative position of the image.  setTop will set the top 
	 * image to the new scrolled-to image and the title to the (user friendly) time that the picture was taken
	 */
	private void setTop(){
		if (current < pics.size() && current >= 0){
			Picture pic = pics.get(current);
			((TextView) findViewById(R.id.compare_text_first)).setText(Util.timeToDate(pic.getTime()));

			ImageView image = (ImageView) findViewById(R.id.compare_image_frist);
			Bitmap bMap = BitmapFactory.decodeFile(Util.getPath(pic));
			image.setImageBitmap(bMap);



		}
		else{
			System.out.println("Index out of bounds");
			Log.i("error", "Index out of bounds");
		}
		if (current + 1 == pics.size()){
			((ImageView)findViewById(R.id.compare_next)).setImageDrawable(null);
			right_hidden = true;
		}

		else if (right_hidden){
			((ImageView)findViewById(R.id.compare_next)).setImageResource(R.drawable.forward_arrow);
			right_hidden = false;
		}

		if (current == 0){
			((ImageView)findViewById(R.id.compare_prev)).setImageDrawable(null);
			left_hidden = true;
		}
		else if (left_hidden){
			((ImageView)findViewById(R.id.compare_prev)).setImageResource(R.drawable.back_arrow);
			left_hidden = false;
		}

	}

	/** setBottom is called on activity creation and when the user long clicks the top image.  By long clicking the
	 * top image, setBottom will transfer the top image and save it to the bottom image so that the user can continue
	 * scrolling the top image and compare it to the new bottom image. 
	 */
	private void setBottom() {
		if (constant < pics.size() && constant >= 0){
			Picture pic = pics.get(constant);
			((TextView) findViewById(R.id.compare_text_second)).setText(Util.timeToDate(pic.getTime()));

			ImageView image = (ImageView) findViewById(R.id.compare_image_second);
			Bitmap bMap = BitmapFactory.decodeFile(Util.getPath(pic));
			image.setImageBitmap(bMap);
		}
		else{
			System.out.println("Index out of bounds");
			Log.i("error", "Index out of bounds");
		}

	}

	/**Declares the Android menu options */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,Menu.FIRST,0,"rotatetop").setIcon(android.R.drawable.ic_menu_rotate);
		menu.add(0,Menu.FIRST+1,1,"rotatebot").setIcon(android.R.drawable.ic_menu_rotate);

		return super.onCreateOptionsMenu(menu);
	}
	/**Declares the method options associated with the Android menu options.  These menu options
	 * allow the user to rotate the top or bottom picture (for comparison reasons, if desired)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case Menu.FIRST:
			Picture pic = pics.get(current);
			ImageView imagetop = (ImageView) findViewById(R.id.compare_image_frist);
			Bitmap bitmapOrg1 =BitmapFactory.decodeFile(Util.getPath(pic));
			int width = bitmapOrg1.getWidth();
			int height = bitmapOrg1.getHeight(); 
			Matrix matrix1 = new Matrix(); 
			matrix1.postRotate(y); 
			Bitmap resizedBitmap1 = Bitmap.createBitmap(bitmapOrg1, 0, 0, 

					width, height, matrix1, true); 
			BitmapDrawable bmd1 = new BitmapDrawable(resizedBitmap1);
			y+=90;
			imagetop.setImageDrawable(bmd1);
			break;
		case Menu.FIRST+1:
			pic = pics.get(constant);
			Bitmap bitmapOrg =BitmapFactory.decodeFile(Util.getPath(pic));
			width = bitmapOrg.getWidth();
			height = bitmapOrg.getHeight(); 
			Matrix matrix = new Matrix(); 
			matrix.postRotate(x); 
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, 

					width, height, matrix, true); 
			BitmapDrawable bmd = new BitmapDrawable(resizedBitmap); 
			x+=90;
			ImageView image = (ImageView) findViewById(R.id.compare_image_second);
			image.setImageDrawable(bmd);
			break;
		}
		return false;
	}
}
