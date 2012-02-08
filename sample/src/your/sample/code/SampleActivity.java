package your.sample.code;



public class SampleActivity {
	
	
	public static void main(String[] args)  {
		long nums[] = {632528420672538449L, 429290590650920920L, 184567231788315850L};
		for(long n : nums)
	        decode(n, 100);
	}
	 
	public static void decode(long num,int x){
		for(;num>=1;num /= 1000){
		    x += dig(num%1000)==9?((x/dig(num%1000%100))*-1) : dig(num%1000)==8?((x/dig(num%1000%100))):dig(num%1000)%2==0?(dig(num%1000%100)):(dig(num%1000%100)*-1);
		    System.out.print((dig(num%1000%10)!=0)?(char)x:"");
		    }
	}
	public static long dig(long n){
		if(n/10<1)
		    return n;
		else
		    return dig(n/10);
	}
	
}