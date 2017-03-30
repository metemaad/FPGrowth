import java.io.FileNotFoundException;
import java.util.Vector;

/**
 * Created by mohammad on 3/29/17.
 */
public class main {

    public static void main(String[] args) throws FileNotFoundException {
        FileHandler fileHandler=new FileHandler();
        //fileHandler.setFilename("/src/preprocessed_connect-4");


        fileHandler.setFilename("/src/simpledata");
        fileHandler.loadData();
        Vector<Vector<String>> alltuples = fileHandler.getDataset();


        FPGrowth fpGrowth=new FPGrowth();
        fpGrowth.setAlltuples(alltuples);

        FPTree fptree=fpGrowth.FPTreeConstruction(3);
        Vector<Vector<String>> freq = fpGrowth.FPgrowthFreqPatterns(fptree,3);
        System.out.println("Freq pats: "+freq);


    }
    }
