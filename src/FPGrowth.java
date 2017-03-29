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

        MinuimumSupportTreshhold = 3;//(this.alltuples.size() * (int) MinuimumSupport) / 100;
    }

    public float getMinuimumSupport() {
        return MinuimumSupport;
    }

    public void setMinuimumSupport(float minuimumSupport) {
        MinuimumSupport = minuimumSupport;
        updateMinuimumSupportTreshhold();
    }

    Vector < Vector < String>> alltuples = new Vector < > ();

    public void setAlltuples(Vector<Vector<String>> alltuples) {
        this.alltuples = alltuples;
        updateMinuimumSupportTreshhold();
    }

    public Vector < Vector < String>> getAlltuples() {
        return alltuples;


    }
    void process() {
        updateCounts();
        FrequentoneItemSetList();
        Collections.sort(HeaderTable);
        Vector<Vector<String>> newtuples = ordertuplesbasedonSortedFreqItems(alltuples, HeaderTable);

        FPTree fpTree= new FPTree();
        fpTree.IsRoot=true;
        fpTree.item="Null";
        for (Vector<String> tp:newtuples ) {

            fpTree=AddTupletoTree(tp,fpTree);
        }

        int i=0;
    }


    FPTree iteminfq1listhaspointer(String item,FPTree fpTree)
    {
        FPTree p=null;
        FPTree ret=null;
        HeaderTableItem htt=null;
        for (HeaderTableItem ht:HeaderTable) {
            if (ht.item.equals(item))
            {
                htt= ht;
                break;
            }

        }
        if (htt.threadpointer==null)
        {
            return null;
        }else
        {
            ret=htt.threadpointer;
            p=ret;
            while (p.thread!=null)
            {

                p=p.thread;
                ret=p;

            }
            return ret;
        }

    }


    FPTree AddTupletoTree(Vector<String> tuple,FPTree fpTree)

    {
        FPTree root=fpTree;
        FPTree pointer=fpTree;
        FPTree pointer2=fpTree;
        for (String i:tuple) {
            pointer2=pointer;
            for (FPTree ch:pointer.child)
            {
                if (ch.item.equals(i.toString()))
                {
                    pointer=ch;
                    pointer.cardinality+=1;
                    break;

                }

            }
            if(pointer==pointer2)
            {

                FPTree newbranch=new FPTree();
                newbranch.parent=pointer;
                newbranch.item=i.toString();
                FPTree pl=iteminfq1listhaspointer(i.toString(),root);
                if (pl==null)
                {
                    setfq1pointer(i.toString(),newbranch);
                }
                else
                    {
                        pl.thread=newbranch;
                    }
                newbranch.cardinality=1;
                pointer.child.add(newbranch);
                pointer=newbranch;
              //  break;

            }



        }
        return fpTree;

    }

    private void setfq1pointer(String s, FPTree newbranch) {
        for (HeaderTableItem f: HeaderTable) {
            if (f.item.equals(s))
            {
                f.threadpointer=newbranch;
                break;

            }

        }
    }

    Vector<String> sortBasedonVector(Vector<String> tuple,Vector<HeaderTableItem> order)
    {
        Vector<String> tmp=new Vector<>();
        for (HeaderTableItem tmpI:order) {

            for (String tupleitm:tuple) {
                if (tupleitm.toString().equals(tmpI.item))
                {

                    //tupleitm.setCardinality(tmpI.Cardinality);
                    tmp.add(0,tupleitm);
                    break;
                }

            }

        }
        return tmp;

    }
    private Vector<Vector<String>> ordertuplesbasedonSortedFreqItems(Vector<Vector<String>> alltuples,Vector<HeaderTableItem> freqOneItemsets)
    {
        Vector<Vector<String>> ret=new Vector<>();
        for (Vector<String> vi:alltuples ) {
            ret.add(sortBasedonVector(vi, freqOneItemsets));

        }
        return ret;
    }

    Vector<HeaderTableItem> HeaderTable = new Vector<>();
    Map < String, Integer > dictionary = new HashMap < > ();
    Vector<HeaderTableItem> removedoneHeaderTableItems = new Vector<>();


    void FrequentoneItemSetList() {
        HeaderTable.removeAllElements();
        removedoneHeaderTableItems.removeAllElements();
        for (Map.Entry<String, Integer> entry: dictionary.entrySet()) {
            HeaderTableItem tmp=new HeaderTableItem();
            tmp.Cardinality=entry.getValue();
            tmp.item=entry.getKey();
            if (entry.getValue()>=MinuimumSupportTreshhold){

            HeaderTable.add(tmp);
            }else{
                removedoneHeaderTableItems.add(tmp);
            }
        }
    }


    void updateCounts() {

        for (Vector < String > vitem: alltuples) {
            for (String it: vitem) {
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