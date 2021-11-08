import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class BloomFilter {

    private static int ELEMENTS_COUNT;
    private static int BITS_COUNT;
    private static int HASH_COUNT;


    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the no of elements to be encoded(1000 for demo): ");
        ELEMENTS_COUNT = sc.nextInt();
        System.out.println("Enter the no of bits in the filter(10000 for demo): ");
        BITS_COUNT = sc.nextInt();
        System.out.println("Enter the no of hash functions:(7 for demo): ");
        HASH_COUNT = sc.nextInt();

        int[] filter = new int[BITS_COUNT];
        int[] setA = new int[ELEMENTS_COUNT];
        int[] setB = new int[ELEMENTS_COUNT];
        int[] randomHash = new int[HASH_COUNT];

        //fill random numbers in setA
        //using Int max for random number to avoid duplicates
        for (int i = 0; i < ELEMENTS_COUNT; i++) {
            //random elements generation with exclusion of 0
            setA[i] = (int) ((Math.random() + 0.00001) * Integer.MAX_VALUE);
        }

        //fill random numbers in setB
        //using Int max to avoid duplicates
        for (int i = 0; i < ELEMENTS_COUNT; i++) {
            //random elements generation with exclusion of 0
            setB[i] = (int) ((Math.random() + 0.00001) * Integer.MAX_VALUE);
        }

        //fill random hashes for mapping
        //using Int max to avoid duplicates
        for (int i = 0; i < HASH_COUNT; i++) {
            //random element's generation with exclusion of 0
            randomHash[i] = (int) ((Math.random() + 0.00001) * Integer.MAX_VALUE);
        }

        //encode elements of setA in the filter
        encode(filter,randomHash,setA);
        int lookUpIn_A = lookup(filter,setA,randomHash);

        //For setB just perform a look up in the existing filter
        int lookUpIn_B = lookup(filter,setB,randomHash);

        System.out.println("Number of successful lookups for Set A is: "+lookUpIn_A);
        System.out.println("Number of successful lookups for Set B is: " + lookUpIn_B);

        StringBuilder lookupResult = new StringBuilder("");
        lookupResult.append("Number of successful lookups for Set A is: "+lookUpIn_A+"\n");
        lookupResult.append("Number of successful lookups for Set B is: "+lookUpIn_B+"\n");

        Path bloomFilterOutput = Path.of("bloomFilterOutput.txt");
        Files.writeString(bloomFilterOutput, lookupResult);

    }

    static void encode(int[] filter, int[] randomHash, int[] set){

        for(int i=0;i<ELEMENTS_COUNT;i++) {
            for (int j = 0; j < HASH_COUNT; j++) {
                int index = hashFunc(set[i],randomHash[j]);
                if(filter[index]!=1)
                    filter[index]=1;
            }
        }
    }

    static int lookup(int[] filter, int[] set, int[] randomHash){
       int count=0;
       boolean found = true;

        for (int i = 0; i < ELEMENTS_COUNT; i++) {
            found=true;
            for (int j = 0; j < HASH_COUNT; j++) {
                int index = hashFunc(set[i], randomHash[j]);
                if (filter[index] != 1) {
                    found = false;
                    break;
                }
            }
            if(found){
                count++;
            }
        }
       return count;
    }



    static int hashFunc(int eleId, int randomNum) {

        int hashRes = (eleId ^ randomNum) % BITS_COUNT;

        return hashRes;
    }




}
