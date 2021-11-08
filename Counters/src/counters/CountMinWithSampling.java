package counters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;



public class CountMinWithSampling {
	
	
	static int k = 3;  //no of counter arrays
	static int w = 3000; // no of counters in each counter array
	static int n; //no of flow ids
	static int randomArr[] = new int[k];
	public static int [][] countmin = new int[k][w];
	static double prob =  0.01;
	static int missed = 0;
    static FlowSampling[] flows;
	public static void main(String[] args) throws IOException {
		//prob = prob/100f;
		for(int i=0;i<k;i++) {
			randomArr[i] = new Random().nextInt(Integer.MAX_VALUE);
		}
		
		
		
		File file = new File("project3input.txt");
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		 n = Integer.parseInt(br.readLine());
		 
		 flows = new FlowSampling[n];
		
		String p;
		int idx=0;
		while((p = br.readLine()) != null) {
		    String[] curr = p.split("\\s+");
		    flows[idx++] = new FlowSampling(curr[0],curr[1]);
		}
      	while(prob<1) {
      		countmin = new int[k][w];
       		missed=0;
       		prob += 0.1;
       		invoke();
       	
       	}
		
	}

	static void invoke() throws IOException{
		
		
		for(int i=0;i<flows.length;i++) {
			addIntoCounter(flows[i].getFlowId(), flows[i].getPacketSize());
		}
		
		for(int i=0; i<flows.length;i++) {
		 int estimatedFlowSize = populateEstimatedValues(flows[i].getFlowId());
		   flows[i].setEstimatedValue(estimatedFlowSize);
		}
		
		double avgError = calcAverageError(flows);
		
		Arrays.sort(flows, new Comparator<FlowSampling>() {
			public int compare(FlowSampling f1, FlowSampling f2) {
				if(f1.estimatedValue > f2.estimatedValue)
					return -1;
				else if(f1.estimatedValue < f2.estimatedValue)
					return 1;
				return 0;
			}
		});
		

       // StringBuilder countMinOutput = new StringBuilder("");
       // countMinOutput.append("Average error among all flows: "+avgError+" for probability: "+prob+" \n");
        System.out.println(avgError+"\t"+prob+"\t"+missed+"\n");
		//System.out.println("Flow\t\t\t\tEst\t\t\tActual");
		//countMinOutput.append("Flow\t\t\t\tEst\t\t\tActual\n");

       /* for(int i=0;i<100;i++) {
        countMinOutput.append(flows[i]+"\n");
        System.out.println(flows[i]);
        }*/
        
       // Path countMinSamplingOutput = Path.of("countMinSamplingOutput.txt");
        //Files.writeString(countMinSamplingOutput, countMinOutput);
        
       // System.out.println("done");
	}
	//calculates estimated values for each flow
	static int populateEstimatedValues(String flowId) {
		int min = Integer.MAX_VALUE;
		
		for(int i=0;i<k;i++) {
			int index = hashfunc(flowId, randomArr[i]);
		
			min = Math.min(min, countmin[i][index]);  //taking min value of all hashed indices
		}
		return (int) (min*1.0*(1.0/prob));
		
	}


	static double calcAverageError(FlowSampling[] flows) {
		int totalError = 0;
		
		for(int i=0;i<flows.length;i++) {
			String flowId = flows[i].getFlowId();
			int estimatedSize = flows[i].getEstimatedValue();
			int actualSize = Integer.parseInt(flows[i].getPacketSize());
			
			totalError += Math.abs(estimatedSize-actualSize);
		}
		
		return (totalError*1.0)/(flows.length);
	}
	
	static void addIntoCounter(String flowId, String packetSize) {
		int flowsize = Integer.parseInt(packetSize);
		int count=0;
		   for(int i=0;i<flowsize;i++) {  //running algorithm for all individual packets
			 int randomNum = new Random().nextInt(Integer.MAX_VALUE);
			   // p = new Random().nextFloat();
			  if(randomNum < prob*Integer.MAX_VALUE) {
			      count++;
			  }
		   }
		   
		   for(int i=0;i<k;i++) {
			   int index = hashfunc(flowId, randomArr[i]);
			   countmin[i][index]+=count;
		   }
		
	}
	
	static int hashfunc(String flowId, int random) {
		int flowHash = Math.abs(flowId.hashCode());
		
		return  (flowHash^random)%w;
	}

}
