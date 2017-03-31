import java.io.FileNotFoundException;
import java.util.Set;
import java.util.Vector;

/**
 * Created by mohammad on 3/29/17.
 */
public class main {

    public static void main(String[] args) throws FileNotFoundException {
        FileHandler fileHandler=new FileHandler();
        //fileHandler.setFilename("/src/preprocessed_connect-4");


        fileHandler.setFilename("/src/preprocessed_connect-4");
        fileHandler.loadDataBinary();
        Vector<Vector<String>> alltuples = fileHandler.getDataset();


        FPGrowth fpGrowth=new FPGrowth();
        fpGrowth.setAlltuples(alltuples);
        int minsup=(alltuples.size()*1)/100;

        FPTree fptree=fpGrowth.FPTreeConstruction(minsup);
        Set<Vector<String>> freq = fpGrowth.FPgrowthFreqPatterns(fptree,minsup);
        System.out.println("Freq pats: "+freq);


    }
    }
