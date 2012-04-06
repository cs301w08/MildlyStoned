package cs301.group8.blem;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/** SensorActivity uses the built in light meter of the camera to detect whether enough light is present 
 * for a clear photo, or if the flash option should be used.  This activity is called from the Menu of the 
 * settings tab of MainActivity.
 * 
 * IT IS IMPORTANT TO NOTE THAT SINCE THE COMPUTER EMULATOR DOES NOT HAVE AN ACTUAL CAMERA, THIS FUNCTIONALITY WILL 
 * NOT WORK WHEN USING THE EMULATOR
 * However, this function does work well on an actual Android phone
 *
 * @author Group 08 <cs301-group8@ualberta.ca>
 * @version 2.0
 */
public class SensorActivity extends Activity {

	ProgressBar lightMeter;
	TextView textMax, textReading;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sensor);
		lightMeter = (ProgressBar)findViewById(R.id.lightmeter);
		textMax = (TextView)findViewById(R.id.max);
		textReading = (TextView)findViewById(R.id.reading);

		SensorManager sensorManager
		= (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		Sensor lightSensor
		= sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

		if (lightSensor == null){
			Toast.makeText(SensorActivity.this,
					"No Light Sensor! quit-",
					Toast.LENGTH_LONG).show();
		}else{
			float max =  lightSensor.getMaximumRange();
			lightMeter.setMax((int)max);
			textMax.setText("Max Reading: " + String.valueOf(max));

			sensorManager.registerListener(lightSensorEventListener,
					lightSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	SensorEventListener lightSensorEventListener
	= new SensorEventListener(){

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if(event.sensor.getType()==Sensor.TYPE_LIGHT){
				float currentReading = event.values[0];
				if(currentReading<100){
					Toast.makeText(SensorActivity.this,
							"Suggest to use mandatory flash.",
							Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(SensorActivity.this,
							"Suggest to use automatic flash.",
							Toast.LENGTH_LONG).show();
				}
				lightMeter.setProgress((int)currentReading);
				textReading.setText("Current Reading: " + String.valueOf(currentReading));
			}
		}

	};
}
