package counters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;



public class CountMin {
	
	
	static int k = 3;  //no of counter arrays
	static int w = 3000; // no of counters in each counter array
	static int n; //no of flow ids
	static int randomArr[] = new int[k];
	public static int [][] countmin = new int[k][w];
	
	public static void main(String[] args) throws IOException {
		
		for(int i=0;i<k;i++) {
			randomArr[i] = new Random().nextInt(Integer.MAX_VALUE-10)+10;
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
			addIntoCounter(flows[i].getFlowId(), flows[i].getPacketSize());
		}
		
		for(int i=0; i<flows.length;i++) {
		 int estimatedFlowSize = populateEstimatedValues(flows[i].getFlowId());
		   flows[i].setEstimatedValue(estimatedFlowSize);
		}
		
		double avgError = calcAverageError(flows);
		
		Arrays.sort(flows, (a,b)->b.getEstimatedValue() - a.getEstimatedValue());
		

        StringBuilder countMinOutput = new StringBuilder("");
        countMinOutput.append("Average error among all flows: "+avgError+"\n");
        System.out.println("Average error among all flows: "+avgError+"\n");
		System.out.println("Flow\t\t\t\tEst\t\t\tActual");
		countMinOutput.append("Flow\t\t\t\tEst\t\t\tActual\n");

        for(int i=0;i<100;i++) {
        countMinOutput.append(flows[i]+"\n");
        System.out.println(flows[i]);
        }
        
        Path countMinOutputFile = Path.of("countMinOutputFile.txt");
        Files.writeString(countMinOutputFile, countMinOutput);
        
        System.out.println("done");
		
	}

	//calculates estimated values for each flow
	static int populateEstimatedValues(String flowId) {
		int min = Integer.MAX_VALUE;
		
		for(int i=0;i<k;i++) {
			int index = hashfunc(flowId, randomArr[i]);
		
			min = Math.min(min, countmin[i][index]);  //taking min value of all hashed indices
		}
		return min;
		
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
	
	static void addIntoCounter(String flowId, String packetSize) {
		int flowsize = Integer.parseInt(packetSize);
		   for(int i=0;i<flowsize;i++) {  //running algorithm for all individual packets
			for(int j=0;j<k;j++) {
				int index = hashfunc(flowId,randomArr[j]);
				countmin[j][index] += 1;
			}
		   }
		
	}
	
	static int hashfunc(String flowId, int random) {
		int flowHash = Math.abs(flowId.hashCode());
		
		return  (flowHash^random)%w;
	}

}
