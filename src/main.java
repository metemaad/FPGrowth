

import java.io.FileNotFoundException;
import java.util.Set;
import java.util.Vector;

/**
 * Created by mohammad on 3/29/17.
 */
class main {

    public static void main(String[] args) throws FileNotFoundException {
        long startTime = System.currentTimeMillis();


        FileHandler fileHandler=new FileHandler();
        //fileHandler.setFilename("/src/preprocessed_connect-4");


        fileHandler.setFilename("/src/stest1");
        fileHandler.loadData();
        Vector<Vector<String>> alltuples = fileHandler.getDataset();


        FPGrowth fpGrowth=new FPGrowth();
        fpGrowth.setAlltuples(alltuples);
        int minsup=2;//(alltuples.size()*95/100);

        FPTreePack fpTreePack=fpGrowth.FPTreeConstruction(minsup);
        Set<FrequentPattern> freq = fpGrowth.FPgrowthFreqPatterns(fpTreePack.fpTree,minsup);
       // System.out.println("Freq pats: "+freq);


        System.out.println("Freq # : "+freq.size());

        boolean rep=true;
        int j=1,k=0;
        while (rep){
            rep=false;
        for (FrequentPattern s:freq )
        {
            if (s.pattern.size()==j) {
                double f=(float)(s.Support)/alltuples.size();
                System.out.printf( "%d-itemsets: support %.2f for ",j ,f);
                System.out.print(s.pattern+"\n");
                rep=true;
                k++;
            }
        }
            System.out.println(j + " #: " + k);
        j++;k=0;
        }
        System.out.println(alltuples.size());



        long endTime = System.currentTimeMillis();

        System.out.println("That took " + (endTime - startTime) + " milliseconds");

    }

    }
