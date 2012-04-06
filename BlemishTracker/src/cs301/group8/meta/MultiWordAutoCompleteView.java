package cs301.group8.meta;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;



/**
 * From http://groups.google.com/group/android-developers/browse_thread/thread/3abb0ddb14690e60/da33a7cff5e85c10
 * 
 * This class deals with the auto completion of EditText views for search fields
 */

public class MultiWordAutoCompleteView extends AutoCompleteTextView{

	public static final String DEFAULT_SEPARATOR = ",";
	private String mSeparator = DEFAULT_SEPARATOR;

	public MultiWordAutoCompleteView(Context context){
		super(context);
	}

	public MultiWordAutoCompleteView(Context context, AttributeSet attrs){
		super(context, attrs);
	}

	public MultiWordAutoCompleteView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}


	public String getSeparator(){
		return mSeparator;
	}


	public void setSeparator(String separator){
		mSeparator = separator;
	}

	protected void performFiltering(CharSequence text, int keyCode){
		String newText = text.toString();
		if (newText.indexOf(mSeparator) != -1){
			int lastIndex = newText.lastIndexOf(mSeparator);

			if (lastIndex != newText.length() - 1){
				newText = newText.substring(lastIndex + 1).trim();

				if (newText.length() >= getThreshold()){
					text = newText;
				}
			}
		}
		super.performFiltering(text, keyCode);
	}

	@Override
	protected void replaceText(CharSequence text){

		String newText = getText().toString();

		if (newText.indexOf(mSeparator) != -1){
			int lastIndex = newText.lastIndexOf(mSeparator);
			newText = newText.substring(0, lastIndex + 1) + text.toString();
		}

		else{
			newText = text.toString();
		}
		super.replaceText(newText);
	}

}
