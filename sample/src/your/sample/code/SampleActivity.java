package your.sample.code;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;



public class SampleActivity extends Activity{
	 public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			    
			    
			setContentView(R.layout.main);
			((EditText)findViewById(R.id.texty)).setText("");
		//	this.main();
	 }
	 
	public  void main()  {
		long nums[] = {632528420672538449L, 429290590650920920L, 184567231788315850L};
		for(long n : nums)
	        decode(n, 100);
	}
	 
	public void decode(long num,int x){
		for(;num>=1;num /= 1000){
		    x += dig(num%1000)==9?((x/dig(num%1000%100))*-1) : dig(num%1000)==8?((x/dig(num%1000%100))):dig(num%1000)%2==0?(dig(num%1000%100)):(dig(num%1000%100)*-1);
		   Log.i("FUN", ((dig(num%1000%10)!=0)?(char)x:"").toString());
		}
	}
	public  long dig(long n){
		if(n/10<1)
		    return n;
		else
		    return dig(n/10);
	}
	
	public void print (Object s){
		//((EditText)findViewById(R.id.texty)).append(s.toString());
	}
}