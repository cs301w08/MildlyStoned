/*learned and modified from http://www.jameselsey.co.uk/blogs/techblog/android-how-to-implement-voice-recognition-a-nice-easy-tutorial/
*/
package cs301.group8.blem;

import android.app.Activity;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/** SoundRecognize class allows for the user to use voice input to perform search queries.
 *  SoundRecognize is called when the user selects it from the menu button in the search activity.
 *  
 *  IT IS IMPORTANT TO NOTE THAT CALLING THIS ACTIVITY WILL CRASH THE EMULATOR. 
 *  This activity requires appropriate sound hardware, which causes the AVD emulator to crash.
 *  
 *  When tested on an actual phone, this Activity works quite well to extract string words based on
 *  voice input.  In terms of the project, this Activity is "under development" for "future releases".
 *  
 *  Although it works just fine when downloaded to an actual Android phone, we again stress that this
 *  Activity WILL CRASH THE COMPUTER EMULATOR.
 *  
 * @author Group 08 <cs301-group8@ualberta.ca>
 * @version 2.0
 */
public class SoundReconize extends Activity
{
 
    private static final int REQUEST_CODE = 1234;
    private ListView wordsList;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sound);
 
        Button speakButton = (Button) findViewById(R.id.speakButton);
 
        wordsList = (ListView) findViewById(R.id.list);
 
        // Disable button if no recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            speakButton.setEnabled(false);
            speakButton.setText("Recognizer not present");
        }
    }

    public void speakButtonClicked(View v)
    {
        startVoiceRecognitionActivity();
    }
 
    /** startVoiceRecognitionActivity calls the RecognizerIntent which allows for the parsing of voice intent.
     * 
     */
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**onActivityResult is called upon returning from the RecognizerIntent.  We declare 
     * a wordsList string array and populate it with the results from the recognition engine.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            wordsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                    matches));
           wordsList.setOnItemClickListener(matchwordsListener);
            
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private OnItemClickListener matchwordsListener = new OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                long arg3)
        {
           
           Cursor cursor = (Cursor) arg0.getItemAtPosition
            (arg2);
                String title = cursor.getString(cursor.getColumnIndexOrThrow
            (RingtoneManager.EXTRA_RINGTONE_TITLE)); 
                Intent i = new Intent(SoundReconize.this,SearchActivity.class);   
                
                i.putExtra("match", title);
                SoundReconize.this.startActivity(i);
            // TODO Auto-generated method stub
            
        }       
    };

}