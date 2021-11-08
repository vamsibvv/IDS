import javax.swing.text.Segment;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class HashTables {
    private static  int ENTRIES_COUNT;
    private static  int FLOW_COUNT;
    private static  int HASH_COUNT;
    private static  int CUCKOO_STEPS;
    private static  int SEGMENT_COUNT;
    private static int noOfFlowsInDLeftHash = 0;
    private static int noOfFlowsInCuckooHash = 0;
    private static int noOfFlowsInMultiHash=0;

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the no of entries in hashtable: ");
        ENTRIES_COUNT = sc.nextInt();
        System.out.println("Enter the no of flows: ");
        FLOW_COUNT = sc.nextInt();
        System.out.println("Enter the no of hash functions: ");
        HASH_COUNT = sc.nextInt();
        System.out.println("Enter the no of cuckoo steps: ");
        CUCKOO_STEPS = sc.nextInt();
        System.out.println("Enter the no of segments: ");
        SEGMENT_COUNT = sc.nextInt();

        int [] multiHashTable = new int[ENTRIES_COUNT];
        int [] dLeftHashTable = new int[ENTRIES_COUNT];
        int [] cuckooHashTable = new int[ENTRIES_COUNT];
        int [] flowIds = new int [FLOW_COUNT];
        int [] randomArr = new int[HASH_COUNT];
        int [] dLeftRandomArr = new int[SEGMENT_COUNT];
        for (int i = 0; i < FLOW_COUNT; i++) {
            //random flowIds generation with exclusion of 0
            flowIds[i] = (int) ((Math.random()+0.00001)* Integer.MAX_VALUE);
        }
        for (int i = 0; i < HASH_COUNT; i++) {
            //randomArr values to generate multiple hash functions
            randomArr[i] = (int) (Math.random() * Integer.MAX_VALUE);
        }
        for(int i=0; i<SEGMENT_COUNT; i++){
            //dlefthash has to generate random segment count of hashes
            dLeftRandomArr[i] = (int) (Math.random()* Integer.MAX_VALUE);
        }

        noOfFlowsInMultiHash = multiHashInsert(multiHashTable,flowIds,randomArr);
        noOfFlowsInCuckooHash = cuckooInsert(cuckooHashTable,flowIds,randomArr);

        for(int i=0; i<FLOW_COUNT; i++)
          dLeftHashReceive(flowIds[i], dLeftRandomArr, dLeftHashTable);

        //Generating output files for all 3 techniques by creating a string and writing that into respective
        //output files
        StringBuilder multiHashString = new StringBuilder("");
        multiHashString.append("************************************************************\n");
        multiHashString.append("MultiHashTable entries summary      \n");
        multiHashString.append("No of flow entries  in MultiHashTable: "+noOfFlowsInMultiHash+"\n\n");
        multiHashString.append("Below are the flow ids in MultiHashTable(zero represents an empty entry)\n\n");
        multiHashString.append("FLOWID | "+"INDEX\n");
        for(int i=0;i<ENTRIES_COUNT;i++)
            multiHashString.append(multiHashTable[i]+"  |  "+i+"\n");

        Path multiHashOutputFile = Path.of("multiHashOutputFile.txt");
        Files.writeString(multiHashOutputFile, multiHashString);

        StringBuilder cuckooHashString = new StringBuilder("");
        cuckooHashString.append("************************************************************\n");
        cuckooHashString.append("CuckooHashTable entries summary      \n");
        cuckooHashString.append("No of flow entries  in CuckooHashTable: "+noOfFlowsInCuckooHash+"\n\n");
        cuckooHashString.append("Below are the flow ids in CuckooHashTable(zero represents an empty entry)\n\n");
        cuckooHashString.append("FLOWID | "+"INDEX\n");
        for(int i=0;i<ENTRIES_COUNT;i++)
            cuckooHashString.append(cuckooHashTable[i]+"  |  "+i+"\n");

        Path cuckooHashOutputFile = Path.of("cuckooHashOutputFile.txt");
        Files.writeString(cuckooHashOutputFile, cuckooHashString);

        StringBuilder dLeftHashString = new StringBuilder("");
        dLeftHashString.append("************************************************************\n");
        dLeftHashString.append("DLeftHashTable entries summary      \n");
        dLeftHashString.append("No of flow entries  in DLeftHashTable: "+noOfFlowsInDLeftHash+"\n\n");
        dLeftHashString.append("Below are the flow ids in DLeftHashTable(zero represents an empty entry)\n\n");
        dLeftHashString.append("FLOWID | "+"INDEX\n");
        for(int i=0;i<ENTRIES_COUNT;i++)
            dLeftHashString.append(dLeftHashTable[i]+"  |  "+i+"\n");

        Path dLeftHashOutputFile = Path.of("dLeftHashOutputFile.txt");
        Files.writeString(dLeftHashOutputFile, dLeftHashString);


    }

    //MultiHash Table insert
    static int multiHashInsert(int[] multiHashTable, int[] flowIds, int[] randomArr){
        int noOfFlowsInTable = 0;
        for (int i = 0; i < FLOW_COUNT; i++) {
            for (int j = 0; j < HASH_COUNT; j++) {
                //multiple hash-functions to check empty slot
                int hashKey = hashFunc(flowIds[i], randomArr[j]);
                //if present already exit
                if(multiHashTable[hashKey]==flowIds[i]) break;
                //if empty slot insert the flowid
                if (multiHashTable[hashKey] == 0) {
                    multiHashTable[hashKey] = flowIds[i];
                    noOfFlowsInTable++;
                    break;
                }
            }
        }
        return noOfFlowsInTable;
    }

    //Cuckoo HashTable insert
    static int cuckooInsert(int[] cuckooHashTable, int[] flowIds,  int[] randomArr){
        int noOfFlowsInTable=0;

        for (int i = 0; i < FLOW_COUNT; i++) {
            boolean foundEmptySpot = false;

            for (int j = 0; j < HASH_COUNT; j++) {
                //multiple hash-keys to find an empty slot
                int hashKey = hashFunc(flowIds[i], randomArr[j]);

                if (cuckooHashTable[hashKey] == flowIds[i]){
                    foundEmptySpot = true;
                    break;
                }
                //if empty slot is found return without doing cuckoo steps
                if (cuckooHashTable[hashKey] == 0) {
                    foundEmptySpot=true;
                    cuckooHashTable[hashKey] = flowIds[i];
                    noOfFlowsInTable++;
                    break;
                }
            }

            if(foundEmptySpot == true) continue;

            //if no empty slot then we need cuckoo steps to be performed to create vacant spot for incoming flowId
            for(int j=0; j<HASH_COUNT; j++) {
                int hashKey = hashFunc(flowIds[i],randomArr[j]);
                if (cuckooMove(cuckooHashTable,hashKey, CUCKOO_STEPS,randomArr) == true) {
                    //if cuckoo move is successful then fill the current flow in the emptied slot
                    cuckooHashTable[hashKey] = flowIds[i];
                    noOfFlowsInTable++;
                    foundEmptySpot = true;
                    break;
                }
            }
        }
        return noOfFlowsInTable;
    }

    //Cuckoo Move used in Cuckoo Insert
    static boolean cuckooMove(int[] cuckooHashTable, int x, int steps, int[] randomArr){
        //this function is to migrate the existing flow-id to next empty slot to create a vacant spot for incoming flowid
        int existingFlowId = cuckooHashTable[x];

        for(int i=0;i<HASH_COUNT;i++){
            //if existing flow can be accommodated in another empty slot, migrate the existing flow id to new slot
            //and empty the current slot
            int newHashKey = hashFunc(existingFlowId,randomArr[i]);
            if(newHashKey!=x && cuckooHashTable[newHashKey]==0){
                cuckooHashTable[newHashKey]=existingFlowId;
                return true;
            }
        }

        //if the next hash-key is not empty then perform another cuckoo step to find an next empty slot till all
        //cuckoo steps expire
        for(int i=0; i< HASH_COUNT;i++){
            int newHashkey = hashFunc(existingFlowId, randomArr[i]);
            if(newHashkey!=x && steps>=1 && cuckooMove(cuckooHashTable,newHashkey,steps-1,randomArr)==true){
                cuckooHashTable[newHashkey]=existingFlowId;
                return true;
            }
        }

        return false;
    }

    //D-left receive & insert functions
    static boolean dLeftHashReceive(int flowId, int[] dLeftRandomArr, int[] dLeftHashTable){

        for (int i = 0; i < SEGMENT_COUNT; i++) {
            //Generate segment number of hashes for all segments and if the flow is already present just return true
            //else insert in the left most segment of the hashtable
            //int hashKey = (flowId^dLeftRandomArr[i])%(ENTRIES_COUNT/SEGMENT_COUNT)+(i)*(ENTRIES_COUNT/SEGMENT_COUNT);
            int hashKey = dLeftHashKeyGen(flowId, dLeftRandomArr[i])+(i)*(ENTRIES_COUNT/SEGMENT_COUNT);
            if (dLeftHashTable[hashKey] == flowId)
                return true;
        }

        return dLeftHashInsert(flowId,dLeftRandomArr,dLeftHashTable);

    }

    static boolean dLeftHashInsert(int flowId, int[] dLeftRandomArr, int[] dLeftHashTable){

        //starting from the left most segment if you find an empty slot fill the flowId into that segment.
        for(int i=0;i<SEGMENT_COUNT;i++){
            //int hashKey = (flowId^dLeftRandomArr[i])%(ENTRIES_COUNT/SEGMENT_COUNT)+(i)*(ENTRIES_COUNT/SEGMENT_COUNT);
            int hashKey = dLeftHashKeyGen(flowId, dLeftRandomArr[i])+(i)*(ENTRIES_COUNT/SEGMENT_COUNT);
            if(dLeftHashTable[hashKey]==0){
                dLeftHashTable[hashKey]=flowId;
                noOfFlowsInDLeftHash++;
                return true;
            }
        }
        return false;
    }

    static boolean ifPrime(int n){
        if(n%2==0 || n%3==0)
            return false;

        for(int i=5;i<n;i+=5){
            if(n%i==0)
                return false;
        }
        return true;
    }
    static int immediatePrime(int n){
        boolean found = false;
        n+=1;
        while(!found){
            if(ifPrime(n)) {
                found=true;
                return n;
            }
            n+=1;
        }
        return 0;
    }
    //Hash function used for multiHash and cuckooHash techniques(XOR technique)
    //For doing modulo function its better to mod with a prime number to get a better random hash
    //So find immediate next-prime of entries_count and perform mod with that number
    static int hashFunc(int flowId, int randomNum){
        int immediatePrime = immediatePrime(ENTRIES_COUNT);
        int hashRes = (flowId^randomNum)%immediatePrime;
        if(hashRes < ENTRIES_COUNT)
            return hashRes;
        return (hashRes)%ENTRIES_COUNT;
    }

    //dLeftHash hashes based on no of segments hence writing a separate hashfunc for dleft
    static int dLeftHashKeyGen(int flowId, int randomNum){
        int immediatePrime = immediatePrime(ENTRIES_COUNT/SEGMENT_COUNT);
        int hashRes = (flowId^randomNum)%immediatePrime;
        if(hashRes < ENTRIES_COUNT/SEGMENT_COUNT)
            return hashRes;
        return (hashRes)%(ENTRIES_COUNT/SEGMENT_COUNT);
    }
}
