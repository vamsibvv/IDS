import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class CountingBloom {

    private static int ELEMENTS_COUNT;
    private static int COUNTER_COUNT;
    private static int ADD_COUNT;
    private static int REMOVE_COUNT;
    private static int HASH_COUNT;


    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the no of elements to be encoded(1000 for demo): ");
        ELEMENTS_COUNT = sc.nextInt();
        System.out.println("Enter the no of counters in the filter(10000 for demo): ");
        COUNTER_COUNT = sc.nextInt();
        System.out.println("Enter the no of elements you want to remove(500 for demo)");
        REMOVE_COUNT = sc.nextInt();
        System.out.println("Enter the no of elements you want to add (500 for demo)");
        ADD_COUNT = sc.nextInt();
        System.out.println("Enter the no of hash functions(7 for demo)");
        HASH_COUNT = sc.nextInt();



        int[] filter = new int[COUNTER_COUNT];
        int[] setA = new int[ELEMENTS_COUNT];
        int[] setB = new int[ADD_COUNT];
        int[] randomHash = new int[HASH_COUNT];

        //fill random numbers in setA
        for (int i = 0; i < ELEMENTS_COUNT; i++) {
            //random elements generation with exclusion of 0
            setA[i] = (int) ((Math.random() + 0.00001) * Integer.MAX_VALUE); //using Int max to avoid duplicates
        }

        //fill random numbers in setB
        for (int i = 0; i < ADD_COUNT; i++) {
            //random elements generation with exclusion of 0
            setB[i] = (int) ((Math.random() + 0.00001) * Integer.MAX_VALUE); //using Int max to avoid duplicates
        }

        //fill random hashes for mapping
        for (int i = 0; i < HASH_COUNT; i++) {
            //random elements generation with exclusion of 0
            randomHash[i] = (int) ((Math.random() + 0.00001) * Integer.MAX_VALUE); //using Int max to avoid duplicates
        }

        //add elements of setA in filter
        encode(filter, randomHash, setA);
        //remove elements of setA in filter
        remove(filter, randomHash, setA);
        //add elements of setB in filter
        encode(filter,randomHash,setB);

        int result = lookup(filter, setA, randomHash);

        StringBuilder lookupResult = new StringBuilder("");
        lookupResult.append("No of original elements of set A found in lookup: " + result+ "\n");

        Path countingBloomOutput = Path.of("countingBloomOutput.txt");
        Files.writeString(countingBloomOutput, lookupResult);

        System.out.println( "No of original elements of set A found in lookup: "+result);

    }


    static void encode(int[] filter, int[] randomHash, int[] set) {

        for (int i = 0; i < set.length; i++) {
            for (int j = 0; j < HASH_COUNT; j++) {
                int index = hashFunc(set[i], randomHash[j]);
                    filter[index] += 1;
            }
        }
    }

    static void remove(int[] filter, int[] randomHash, int[] set){
        for (int i = 0; i < REMOVE_COUNT; i++) {
            for (int j = 0; j < HASH_COUNT; j++) {
                int index = hashFunc(set[i], randomHash[j]);
                filter[index] -= 1;
            }
        }
    }

    static int lookup(int[] filter, int[] set, int[] randomHash) {
        int count = 0;
        boolean found = true;
        int minCount = 0;

        for (int i = 0; i < set.length; i++) {
            found = true;
            minCount = Integer.MAX_VALUE;
            for (int j = 0; j < HASH_COUNT; j++) {
                int index = hashFunc(set[i], randomHash[j]);
                if (filter[index] < 1) {
                    found = false;
                    break;
                }
                minCount = Math.min(minCount,filter[index]);
            }
            if (found) {
                count += minCount;
            }
        }
        return count;
    }

    //hashFunc returns the hash value for multiple hashfunctions by using XOR operations
    static int hashFunc(int eleId, int randomNum) {

        int hashRes = (eleId ^ randomNum) % COUNTER_COUNT;

        return hashRes;
    }


}
