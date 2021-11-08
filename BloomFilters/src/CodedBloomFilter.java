import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class CodedBloomFilter {
    private static int SET_COUNT;
    private static int ELEMENTS_COUNT;
    private static int FILTERS_COUNT;
    private static int BITS_COUNT;
    private static int HASH_COUNT;


    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the no of sets(7 for demo): ");
        SET_COUNT = sc.nextInt();
        System.out.println("Enter the no of elements in each set:(1000 for demo) ");
        ELEMENTS_COUNT = sc.nextInt();
        System.out.println("Enter the no of filters(3 for demo)");
        FILTERS_COUNT = sc.nextInt();
        System.out.println("Enter the no of bits in each filter(30000 for demo)");
        BITS_COUNT = sc.nextInt();
        System.out.println("Enter the no of hash functions(7 for demo)");
        HASH_COUNT = sc.nextInt();


        int[][] sets = new int[SET_COUNT][ELEMENTS_COUNT];
        int[][] filters = new int[FILTERS_COUNT][BITS_COUNT];



        int[] randomHash = new int[HASH_COUNT];

        //fill random hashes for mapping
        //using Int max to avoid duplicates
        for (int i = 0; i < HASH_COUNT; i++) {
            //random elements generation with exclusion of 0
            randomHash[i] = (int) ((Math.random() + 0.00001) * Integer.MAX_VALUE);
        }

        //fill random elements in the sets
        //using Int max to avoid duplicates
        for(int i=0; i < SET_COUNT; i++) {
            for (int j = 0; j < ELEMENTS_COUNT; j++) {
                //random elements generation with exclusion of 0
                sets[i][j] = (int) ((Math.random() + 0.00001) * Integer.MAX_VALUE); //using Int max to avoid duplicates
            }
        }

        String[] codes = new String[SET_COUNT];

        //Store the code values for each set to validate in lookup
        //Converting the set number to binary for the codes
        for(int i=1;i<=SET_COUNT;i++){
            codes[i-1] = convertIntToBin(i);
        }

        //Encode each element of the set in its respective filters.
        // if the bit at j'th index of set code is '1' then encode that set elements in j'th filter
        //Math.ceil(Math.log(SET_COUNT+1)/Math.log(2)) this is the number of filters in CodedBloom filter algorithm
        for(int i=0;i<SET_COUNT;i++){
            for(int j = 0; j< (int) Math.ceil(Math.log(SET_COUNT+1)/Math.log(2)); j++) {
               if(codes[i].charAt(j)=='1'){
                   encode(filters[j],randomHash,sets[i]);
               }
            }
        }

        int numberOfCorrectLookups = 0;

        //lookup function checks the number of correct lookups for all the elements
        numberOfCorrectLookups = lookup(filters,sets,randomHash,codes);

        StringBuilder lookupResult = new StringBuilder("");
        lookupResult.append("No of correct lookups : " + numberOfCorrectLookups + "\n");

        Path codedBloomFilterOutput = Path.of("codedBloomFilterOutput.txt");
        Files.writeString(codedBloomFilterOutput, lookupResult);

        System.out.println("No of correct lookups " + numberOfCorrectLookups);

    }

    static void encode(int[] filter, int[] randomHash, int[] set) {

        for (int i = 0; i < set.length; i++) {
            for (int j = 0; j < HASH_COUNT; j++) {
                int index = hashFunc(set[i], randomHash[j]);
                if(filter[index]!=1)
                filter[index] = 1;
            }
        }
    }

    static int lookup(int[][] filters, int[][] sets, int[] randomHash, String[] codes) {
        boolean isCorrectClassification = true;
        int numberOfCorrectLookups = 0;


        for(int i=0;i<SET_COUNT;i++){
            for(int j=0;j<ELEMENTS_COUNT;j++){
                StringBuilder currentCode = new StringBuilder("");
                //Search the element in all the filters
                for(int k=0;k<FILTERS_COUNT;k++){
                    //if the k'th filter has the element then append '1' to the code else append '0'
                    if(lookupHelper(filters[k],sets[i][j],randomHash)==true){
                        currentCode.append("1");
                    }
                    else{
                        currentCode.append("0");
                    }
                }

                //if the generated code matches with our predefined set codes then its a correct lookup
                if(currentCode.toString().equals(codes[i]))
                    numberOfCorrectLookups++;
            }
        }
        return numberOfCorrectLookups;
    }

    //This is a utility function used to check if a element is present in a particular filter
    static boolean lookupHelper(int[] filter, int ele, int[] randomHash ){

        for(int i=0;i<HASH_COUNT;i++){
            int index = hashFunc(ele,randomHash[i]);
            if(filter[index]!=1)
                return false;
        }

        return true;
    }

    //hashfunc to get index by XOR operation with random number
    static int hashFunc(int eleId, int randomNum) {

        int hashRes = (eleId ^ randomNum) % BITS_COUNT;

        return hashRes;
    }

    //This converts a integer number to a binary string
    static String convertIntToBin(int i){
        String res="";

        while(i>0){
            int a = i%2;
            i = i/2;
            res = String.valueOf(a)+res;
        }

        if(res.length()<Math.ceil(Math.log(SET_COUNT+1)/Math.log(2))){
            int k = (int) (Math.ceil(Math.log(SET_COUNT + 1) / Math.log(2)) - res.length());
            while(k-->0)
                res = "0"+res;
        }
        return res;
    }

}
