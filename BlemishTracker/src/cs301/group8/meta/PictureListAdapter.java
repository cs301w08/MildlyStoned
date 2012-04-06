package cs301.group8.meta;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import cs301.group8.blem.CompareActivity;
import cs301.group8.blem.Dialogs;
import cs301.group8.blem.ImageActivity;
import cs301.group8.blem.R;

/**PictureListAdapter creates the custom list that will be used to populate the results of the search and the 
 * BlemishActivity list view.  Each 'view' element contains a small thumbnail image, a checkbox (hidden in search), 
 * the time that the picture was taken, and the [X] button which allows for deletion
 *
 * @author Group 08 <cs301-group8@ualberta.ca>
 * @version 2.0
 */
public class PictureListAdapter extends ArrayAdapter<Picture> {
	public int position = 0;
	private String group;
	private OnClickListener listener;
	
	public PictureListAdapter (Context context, int res_id, ArrayList<Picture> pics, String group,  OnClickListener checkListen){
		super (context, res_id, pics);
		this.group = group;
		listener = checkListen;
	}
	

	/**getView takes in the position of the view of the ListAdapter and sets buttons and their listeners.
	 * The remove button allows the user to delete that image
	 * The titleText TextView shows the time that the picture was taken
	 * The checkButton CheckBox allows the user to perform "batch operations"
	 * The image ImageView displays the image in a thumbnail
	 * 
	 * @param position			Integer value of the position in the list
	 * @return view				Returns the associated view
	 */
	public View getView(final int position, View convertView, ViewGroup parent){
		View v = convertView;
		ViewHolder holder;
		if (v==null){
			LayoutInflater inflater=((Activity) this.getContext()).getLayoutInflater();
			v=inflater.inflate(R.layout.blemish_item, null);
			holder = new ViewHolder();
			holder.removeButton = (Button) v.findViewById(R.id.blemish_remove_button);
			holder.titleText = (TextView) v.findViewById(R.id.title_text);
			holder.image = (ImageView) v.findViewById(R.id.littleimage);
			holder.checkButton = (CheckBox) v.findViewById(R.id.picture_list_check);
			
			v.setTag(holder);
		}
		else{
			holder = (ViewHolder) v.getTag();
		}

		// Set the thumbnail
		
		final Picture pic = this.getItem(position);
		Uri imageUri = Uri.fromFile(new File(Util.getPath(pic)));
		
		
		holder.image.setImageDrawable(Drawable.createFromPath(imageUri.getPath()));
		holder.removeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Set listener for delete button [x]
				try{
					PictureListAdapter.this.position = position;
					((Activity) getContext()).showDialog(Dialogs.DIALOG_DELETE_ITEM);
				}catch (Exception e){
					Log.i("error", "Unable to delete " + Util.getPath(pic));
				}

			}
		});
		// Set listener for each row (v)
		final PictureListAdapter adpt = this;
		v.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				Intent intent = new Intent(getContext().getApplicationContext(), CompareActivity.class);
				ArrayList<Picture> pics = new ArrayList<Picture>();
				for (int i=0;i<adpt.getCount();i++){
					pics.add(adpt.getItem(i));
				}
				
				intent.putExtra("position", position); 
				intent.putParcelableArrayListExtra("pics", pics);
				
			//	intent.putExtra(name, value)
				getContext().startActivity(intent);
				return false;
			}
		});

		v.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getContext().getApplicationContext(), ImageActivity.class);
				intent.putExtra("picture", pic);
				getContext().startActivity(intent);
			}
		});
		
		if (listener!= null) v.findViewById(R.id.picture_list_check).setOnClickListener(listener);
		holder.titleText.setText(Util.timeToDate(pic.getTime()));

		return v;
	}

	/**ViewHolder class holds the 4 widget elements of each view for the PictureListAdapter*/
	static class ViewHolder{
		Button removeButton;
		TextView titleText;
		ImageView image;
		CheckBox checkButton;
	}

	




} 

