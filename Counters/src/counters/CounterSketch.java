package counters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;

public class CounterSketch {

	static int k = 3; // no of counter arrays
	static int w = 3000; //no of counters in each counter array
	static int n; // no of flow ids
	static int randomArr[] = new int[k];
	public static int [][] counterSketch = new int[k][w];
	
	public static void main(String[] args) throws IOException {
		
		for(int i=0;i<k;i++) { //generating random numbers with large range to avoid duplicates
			randomArr[i] = new Random().nextInt(Integer.MAX_VALUE-10)+1;
		}
		
		
		
		File file = new File("project3input.txt");
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		 n = Integer.parseInt(br.readLine());
		 
		 Flow[] flows = new Flow[n];
		
		String k;
		int idx=0;
		while((k = br.readLine()) != null) {
		    String[] curr = k.split("\\s+");
		    flows[idx++] = new Flow(curr[0],curr[1]);
		}
		
		for(int i=0;i<flows.length;i++) {
			addIntoCounterSketch(flows[i].getFlowId(), flows[i].getPacketSize());
		}
		
		for(int i=0; i<flows.length;i++) {
		 int estimatedFlowSize = populateEstimatedValues(flows[i].getFlowId());
		   flows[i].setEstimatedValue(estimatedFlowSize);
		}
		
		double avgError = calcAverageError(flows);

		//sorting flows in decreasing order of their estimated values
		Arrays.sort(flows, (a,b)->b.getEstimatedValue() - a.getEstimatedValue());
		

        StringBuilder counterSketchOutput = new StringBuilder("");
        counterSketchOutput.append("Average error among all flows: "+avgError+"\n");
        System.out.println("Average error among all flows: "+avgError+"\n");
		System.out.println("Flow\t\t\t\tEst\t\t\tActual");
		counterSketchOutput.append("Flow\t\t\t\tEst\t\t\tActual\n");

        for(int i=0;i<100;i++) {
        counterSketchOutput.append(flows[i]+"\n");
        System.out.println(flows[i]);
        }
        
        Path counterSketchOutputFile = Path.of("counterSketchOutputFile.txt");
        Files.writeString(counterSketchOutputFile, counterSketchOutput);
        
        System.out.println("Done");
		
	}

	//calculates estimated values for each flow
	static int populateEstimatedValues(String flowId) {
		int eVals[] = new int[k];
		
		int flowHash = (flowId.hashCode()); //hashcode generates a 32 bit integer
		
		for(int i=0;i<k;i++) {
			int x = flowHash^randomArr[i]; //hashing without mod
			char operation = (x>>31 & 1)==1?'+':'-'; //checking for first bit of hashed value
			int index = (int)Math.abs(x%w); //hashing with mod
		  eVals[i] = (operation=='+')?counterSketch[i][index]:-counterSketch[i][index];
		}
		
		Arrays.sort(eVals);
		return (k%2==1)?eVals[k/2]:(eVals[k/2]+eVals[k/2-1])/2; //returning the median of all values
		
	}
	
	static double calcAverageError(Flow[] flows) {
		double totalError = 0.0;
		
		for(int i=0;i<flows.length;i++) {
			String flowId = flows[i].getFlowId();
			int estimatedSize = flows[i].getEstimatedValue();
			int actualSize = Integer.parseInt(flows[i].getPacketSize());
			
			totalError += Math.abs(estimatedSize-actualSize);
		}
		
		return totalError/(flows.length);
	}
	
	static void addIntoCounterSketch(String flowId, String packetSize) {
		int flowsize = Integer.parseInt(packetSize);
		int flowHash = flowId.hashCode(); // hash code is a 32 bit integer
	
		   for(int i=0;i<flowsize;i++) {  //running the algorithm for all individual packets in the flow
			for(int j=0;j<k;j++) {	
				int x = flowHash^randomArr[j];
				char operation = (x>>31 & 1)==1?'+':'-'; //checking for first bit of hashed value
				int index = (int)Math.abs(x%w);
				if(operation == '+')
				  counterSketch[j][index] += 1;
				else
				  counterSketch[j][index] -= 1;	
			}
		   }
		
	  }
	
	static int hashfunc(String flowId, int random) {
		int flowHash = Math.abs(flowId.hashCode());
		
		return  (flowHash^random)%w;
	}
}
