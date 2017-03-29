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
        Vector<Vector<item>> newtuples = ordertuplesbasedonSortedFreqItems(alltuples, FrequentoneItemSets);
        FPTree fpTree= new FPTree();
        fpTree.IsRoot=true;
        for (Vector<item> tp:newtuples ) {
            fpTree=AddTupletoTree(tp,fpTree);
        }

        int i=0;
    }

    FPTree AddTupletoTree(Vector<item> tuple,FPTree fpTree)

    {
        FPTree pointer=fpTree;
        FPTree pointer2=fpTree;
        for (item i:tuple) {
            pointer2=pointer;
            for (FPTree ch:pointer.child)
            {
                if (ch.item.equals(i.toString()))
                {
                    pointer=ch;
                    break;

                }

            }
            if(pointer==pointer2)
            {
                FPTree newbranch=new FPTree();
                newbranch.parent=pointer;
                newbranch.item=i.toString();
                newbranch.cardinality=i.getCardinality();
                pointer.child.add(newbranch);
                pointer=newbranch;
              //  break;

            }



        }
        return fpTree;

    }
    Vector<item> sortBasedonVector(Vector<item> tuple,Vector<ItemSet> order)
    {
        Vector<item> tmp=new Vector<>();
        for (ItemSet tmpI:order) {

            for (item tupleitm:tuple) {
                if (tupleitm.toString().equals(tmpI.item))
                {

                    tupleitm.setCardinality(tmpI.Cardinality);
                    tmp.add(0,tupleitm);
                    break;
                }

            }

        }
        return tmp;

    }
    private Vector<Vector<item>> ordertuplesbasedonSortedFreqItems(Vector<Vector<item>> alltuples,Vector<ItemSet> FreqOneItemsets)
    {
        Vector<Vector<item>> ret=new Vector<>();
        for (Vector<item> vi:alltuples ) {
            ret.add(sortBasedonVector(vi,FreqOneItemsets));

        }
        return ret;
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