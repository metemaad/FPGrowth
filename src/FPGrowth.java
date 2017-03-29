import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by mohammad on 3/29/17.
 */
public class FPGrowth {
    /**
     * between 0..100
     */

    float MinuimumSupport = 30;
    int MinuimumSupportTreshhold;
    void updateMinuimumSupportTreshhold() {

        MinuimumSupportTreshhold = (this.alltuples.size() * (int) MinuimumSupport) / 100;
    }

    public float getMinuimumSupport() {
        return MinuimumSupport;
    }

    public void setMinuimumSupport(float minuimumSupport) {
        MinuimumSupport = minuimumSupport;
        updateMinuimumSupportTreshhold();
    }

    Vector < Vector < item >> alltuples = new Vector < > ();

    public void setAlltuples(Vector < Vector < item >> alltuples) {
        this.alltuples = alltuples;
        updateMinuimumSupportTreshhold();
    }

    public Vector < Vector < item >> getAlltuples() {
        return alltuples;


    }
    void process() {
        updateCounts();
        FrequentoneItemSetList();
        Collections.sort(FrequentoneItemSets);
        int i=0;
    }
    Vector<ItemSet> FrequentoneItemSets = new Vector<>();
    Map < String, Integer > dictionary = new HashMap < > ();
    Vector<ItemSet> RemovedoneItemSets= new Vector<>();


    void FrequentoneItemSetList() {
        FrequentoneItemSets.removeAllElements();
        RemovedoneItemSets.removeAllElements();
        for (Map.Entry<String, Integer> entry: dictionary.entrySet()) {
            ItemSet tmp=new ItemSet();
            tmp.Cardinality=entry.getValue();
            tmp.item=entry.getKey();
            if (entry.getValue()>=MinuimumSupportTreshhold){

            FrequentoneItemSets.add(tmp);
            }else{
                RemovedoneItemSets.add(tmp);
            }
        }
    }


    void updateCounts() {

        for (Vector < item > vitem: alltuples) {
            for (item it: vitem) {
                if (dictionary.containsKey(it.toString())) {
                    int count = dictionary.get(it.toString());
                    dictionary.put(it.toString(), count + 1);
                } else {
                    dictionary.put(it.toString(), 1);
                }

            }

        }

    }
}