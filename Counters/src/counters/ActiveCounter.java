package counters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class ActiveCounter {
   public static void main(String[] args) throws IOException {
	   int numberSize = 16;
	   int exponentSize = 16;
	   
	   int cn=0,ce=0;
	   
	   int activeCounterSize = 1000000;
	   
	   for(int i=0;i<activeCounterSize;i++) { //increasing counter by 1000000 times
		   
		  
			   int probability = new Random().nextInt((int)Math.pow(2, ce));
			   
			   if(probability == 0) //we increase cn with a probability of 1/(2^ce)
				   cn++;
			   String binaryStr = convertToBinary(cn);
			   if(binaryStr.length()>numberSize) { // if cn overflows then right shift cn by one bit and increment ce
				   ce++;
				   cn = cn>>1;
			   }
		   
		   
	   }
	   
	   int result = cn*(int)Math.pow(2, ce);
	   
	   StringBuilder activeCounterOutput = new StringBuilder("");
	   activeCounterOutput.append(result);
    
       Path activeCounterOutputFile = Path.of("activeCounterOutputFile.txt");
       Files.writeString(activeCounterOutputFile, activeCounterOutput);
	   
	   System.out.println(result);
   }
   
   static String convertToBinary(int n) {
	   String res = "";
	   
	   while(n>0) {
		   if(n%2==0) {
			   res = '0'+res;
		   }else {
			   res = '1'+res;
		   }
		   n = n/2;
	   }
	   
	   return res;
   }
}
