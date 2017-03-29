import java.util.Vector;

/**
 * Created by mohammad on 3/29/17.
 */
public class main {

    public static void main(String[] args)
    {
        FileHandler fileHandler=new FileHandler();
        //fileHandler.setFilename("/src/preprocessed_connect-4");
        fileHandler.setFilename("/src/simpledata");
        fileHandler.loadData();
        Vector<Vector<item>> alltuples = fileHandler.getDataset();


        FPGrowth fpGrowth=new FPGrowth();
        fpGrowth.setAlltuples(alltuples);
        fpGrowth.setMinuimumSupport(70);
        fpGrowth.process();


    }
    }
