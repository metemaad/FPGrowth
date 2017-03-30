import java.util.*;

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

    Vector<Vector<String>> alltuples = new Vector<>();

    public void setAlltuples(Vector<Vector<String>> alltuples) {
        this.alltuples = alltuples;
        updateMinuimumSupportTreshhold();
    }

    public Vector<Vector<String>> getAlltuples() {
        return alltuples;


    }

    void FPTreeConstruction() {
        ScanDB();
        CollectFrequentItems();
        Collections.sort(HeaderTable);

        Vector<Vector<String>> SortedTuples = ordertuplesbasedonSortedFreqItems(alltuples, HeaderTable);

        //Create root of Tree
        FPTree fpTree = new FPTree();
        fpTree.IsRoot = true;
        fpTree.item = "Null";
        //for each transaction T of DB
        for (Vector<String> FreqItemOfT : SortedTuples) {

            fpTree = insertFreqItemsOfT(FreqItemOfT, fpTree);
        }

        FPgrowthFreqPatterns(fpTree);
//        minedFreqPat.removeAllElements();
//        for (HeaderTableItem ht:HeaderTable) {
//
//            Vector<String> condition=new Vector<>();
//            condition.add(ht.item);
//            minedFreqPat.add(condition);
//            HeaderTableItem htaaa= findheadertablecell(ht.item,HeaderTable);
//            Vector<String> netset = generateFreqPat(htaaa,fpTree,condition);
//        }
//
//        int i=0;
    }

    void FPgrowthFreqPatterns(FPTree fpTree) {

        FPgrowth(fpTree, null);
    }


    FPTree FPgrowth(FPTree fpTree, Vector<String> A) {

        FPTree P = null; //Single prefix path
        FPTree Q = null;// Multi Path Part
        if (TreeHasSinglePrefixPath(fpTree) == true) {
            GeneratePatternsFromCombinationsInPplusA();
        } else {
            //e let Q be Tree
            //For each item Ai in Q
            Q = fpTree; //there is no Single prefix path

            dic=new HashMap<>();
            getdic(Q);
            Vector<HeaderTableItem> Ht=getHeadTable();
            Collections.sort(Ht);


            for (HeaderTableItem Ai : Ht) {

                Vector<String> B=new Vector<>();
                //b=ai+A **********************************
                Vector<Vector<CondDS>> newtuples = ordertuplesbasedonSortedFreqItemsR(newdataset, Ht);//m cond   ConstructBCondPatBase(B);//=Ai+A
                //**********************************
                FPTree Tree_B = ConstructBCondPatternBaseAndCondFPTree(); //m ftpree
                //**********************************

                if (Tree_B.child.size() >= 0) // Tree_BisNotEmpty())
                {
                    //Vector<String> B=new Vector<>();

                    FPTree FreqPatternB = FPgrowth(Tree_B, B);
                }

            }
        }
        return freqPatternPQPXQ(P, Q);//freq-pattern(P)+freq-pattern(Q)+freq-pattern(P)Xfreq-pattern(Q) //**********************************
    }

    Map<String, Integer> dic=new HashMap<>();
    Vector<HeaderTableItem> getHeadTable(){
        Vector<HeaderTableItem> headerTable = new Vector<>();

        for (Map.Entry<String, Integer> entry : dic.entrySet()) {
            HeaderTableItem tmp = new HeaderTableItem();
            tmp.Cardinality = entry.getValue();
            tmp.item = entry.getKey();
            if (entry.getValue() >= MinuimumSupportTreshhold) {

                headerTable.add(tmp);
            }
        }
        return  headerTable;
    }
     void getdic(FPTree fpTree)
    {
        if (dic.containsKey(fpTree.item))
        {
            int i=dic.get(fpTree.item);
            dic.put(fpTree.item,i++);

        }else
            {
                if (!fpTree.IsRoot)
                dic.put(fpTree.item,1);
            }


        for (FPTree ch : fpTree.child) {

            getHeadTable(ch);
        }

    }


    FPTree freqPatternPQPXQ(Object P, Object Q) {
        return null;

    }

    private FPTree ConstructBCondPatternBaseAndCondFPTree() {
        return null;
    }

    private void GeneratePatternsFromCombinationsInPplusA() {
    }

    Vector<String> AiInQ(FPTree fpTree) {
        Vector<String> ret = new Vector<>();
        if (fpTree.child.size() > 0) {

            for (FPTree ch : fpTree.child)
            {
                Vector<String> tmp=AiInQ(ch);
                if (tmp!=null) for (String s:tmp){
                if (ret.contains(s)==false) ret.add(s);
                }
            }
        } else {
            Vector<String> ret0 = new Vector<>();
            ret0.add(fpTree.item);
            return ret0;
        }
        return ret;


    }
    public void traverseTree(FPTree tree) {

        for (int i=0; i<tree.child.size(); i++) {
            FPTree child = tree.child.get(i);

            traverseTree(tree);
        }
    }

    boolean TreeHasSinglePrefixPath(FPTree fpTree) {

        //return fpTree.child.size() <= 1 && (fpTree.child.size() != 1 || TreeHasSinglePrefixPath(fpTree.child.get(0)));
        return false;

    }

    Vector<String> generateFreqPat(HeaderTableItem ht, FPTree root, Vector<String> condition) {
        String item = ht.item;

        Vector<Vector<CondDS>> newdataset = new Vector<>();
        Map<String, Integer> dic = new HashMap<>();


        FPTree first = ht.headofNodeLink;
        Vector<FPTree> list = new Vector<>();
        while (first != null) {
            list.add(first);
            first = first.nodeLink;

        }


        for (FPTree l : list) {
            Vector<CondDS> record = new Vector<>();
            FPTree p = l;
            int car = p.cardinality;
            while ((!p.item.equals("Null"))) {
                if ((!p.item.equals(item))) {
                    CondDS condDS = new CondDS();
                    condDS.item = p.item;
                    condDS.cardinality = car;
                    if (dic.containsKey(p.item)) {
                        int i = dic.get(p.item);
                        dic.put(p.item, i + car);
                    } else {
                        dic.put(p.item, car);
                    }

                    record.add(condDS);
                }
                p = p.parent;

            }
            newdataset.add(record);


        }


        Vector<HeaderTableItem> headerTable = new Vector<>();
        //removedoneHeaderTableItems.removeAllElements();
        for (Map.Entry<String, Integer> entry : dic.entrySet()) {
            HeaderTableItem tmp = new HeaderTableItem();
            tmp.Cardinality = entry.getValue();
            tmp.item = entry.getKey();
            if (entry.getValue() >= MinuimumSupportTreshhold) {

                headerTable.add(tmp);
            } else {
                //removedoneHeaderTableItems.add(tmp);
            }
        }

        if (headerTable.size() <= 0) {
            return condition;
        }

        //sort dic
        Collections.sort(headerTable);
        //remove not freq
        //generate tree


        Vector<Vector<CondDS>> newtuples = ordertuplesbasedonSortedFreqItemsR(newdataset, headerTable);

        FPTree fpTree = new FPTree();
        fpTree.IsRoot = true;
        fpTree.item = "Null";
        for (Vector<CondDS> tp : newtuples) {

            fpTree = AddTupletoTreeR(tp, fpTree);
        }


        for (HeaderTableItem htaa : headerTable) {
            Vector<String> condition1 = new Vector<>();
            condition1.addAll(condition);
            condition1.add(htaa.item);
            minedFreqPat.add(condition1);
            HeaderTableItem htaaa = findheadertablecell(htaa.item, headerTable);
            Vector<String> netset = generateFreqPat(htaaa, fpTree, condition1);

        }

        return condition;

    }

    Vector<Vector<String>> minedFreqPat = new Vector<>();

    FPTree iteminfq1listhaspointerR(String item, FPTree fpTree, Vector<HeaderTableItem> headerTable) {
        FPTree p = null;
        FPTree ret = null;
        HeaderTableItem htt = null;
        for (HeaderTableItem ht : headerTable) {
            if (ht.item.equals(item)) {
                htt = ht;
                break;
            }

        }
        if (htt.headofNodeLink == null) {
            return null;
        } else {
            ret = htt.headofNodeLink;
            p = ret;
            while (p.nodeLink != null) {

                p = p.nodeLink;
                ret = p;

            }
            return ret;
        }

    }


    HeaderTableItem findheadertablecell(String item, Vector<HeaderTableItem> headerTable) {
        HeaderTableItem htt = null;
        for (HeaderTableItem ht : headerTable) {
            if (ht.item.equals(item)) {
                htt = ht;
                break;
            }

        }
        return htt;

    }

    FPTree iteminfq1listhaspointer(String item, FPTree fpTree) {
        FPTree p = null;
        FPTree ret = null;
        HeaderTableItem htt = null;
        for (HeaderTableItem ht : HeaderTable) {
            if (ht.item.equals(item)) {
                htt = ht;
                break;
            }

        }
        if (htt.headofNodeLink == null) {
            return null;
        } else {
            ret = htt.headofNodeLink;
            p = ret;
            while (p.nodeLink != null) {

                p = p.nodeLink;
                ret = p;

            }
            return ret;
        }

    }

    FPTree AddTupletoTreeR(Vector<CondDS> tuple, FPTree fpTree)

    {
        FPTree root = fpTree;
        FPTree pointer = fpTree;
        FPTree pointer2 = fpTree;
        for (CondDS i : tuple) {
            pointer2 = pointer;
            for (FPTree ch : pointer.child) {
                if (ch.item.equals(i.item)) {
                    pointer = ch;
                    pointer.cardinality += 1;
                    break;

                }

            }
            if (pointer == pointer2) {

                FPTree newbranch = new FPTree();
                newbranch.parent = pointer;
                newbranch.item = i.item;
                FPTree pl = iteminfq1listhaspointer(i.item, root);
                if (pl == null) {
                    setfq1pointer(i.item, newbranch);
                } else {
                    pl.nodeLink = newbranch;
                }
                newbranch.cardinality = 1;
                pointer.child.add(newbranch);
                pointer = newbranch;
                //  break;

            }


        }
        return fpTree;

    }


    FPTree insertFreqItemsOfT(Vector<String> FreqItemOfT, FPTree fpTree)

    {
        FPTree root = fpTree;
        FPTree pointer = fpTree;
        FPTree pointer2 = fpTree;
        for (String i : FreqItemOfT) {
            pointer2 = pointer;
            for (FPTree ch : pointer.child) {
                if (ch.item.equals(i.toString())) {
                    pointer = ch;
                    pointer.cardinality += 1;
                    break;

                }

            }
            if (pointer == pointer2) {

                FPTree newbranch = new FPTree();
                newbranch.parent = pointer;
                newbranch.item = i.toString();
                FPTree pl = iteminfq1listhaspointer(i.toString(), root);
                if (pl == null) {
                    setfq1pointer(i.toString(), newbranch);
                } else {
                    pl.nodeLink = newbranch;
                }
                newbranch.cardinality = 1;
                pointer.child.add(newbranch);
                pointer = newbranch;
                //  break;

            }


        }
        return fpTree;

    }

    private void setfq1pointer(String s, FPTree newbranch) {
        for (HeaderTableItem f : HeaderTable) {
            if (f.item.equals(s)) {
                f.headofNodeLink = newbranch;
                break;

            }

        }
    }

    Vector<CondDS> sortBasedonVectorR(Vector<CondDS> tuple, Vector<HeaderTableItem> order) {
        Vector<CondDS> tmp = new Vector<>();
        for (HeaderTableItem tmpI : order) {

            for (CondDS tupleitm : tuple) {
                if (tupleitm.item.toString().equals(tmpI.item)) {

                    //tupleitm.setCardinality(tmpI.Cardinality);
                    tmp.add(0, tupleitm);
                    break;
                }

            }

        }
        return tmp;

    }

    Vector<String> sortBasedonVector(Vector<String> tuple, Vector<HeaderTableItem> order) {
        Vector<String> tmp = new Vector<>();
        for (HeaderTableItem tmpI : order) {

            for (String tupleitm : tuple) {
                if (tupleitm.toString().equals(tmpI.item)) {

                    //tupleitm.setCardinality(tmpI.Cardinality);
                    tmp.add(0, tupleitm);
                    break;
                }

            }

        }
        return tmp;

    }

    private Vector<Vector<CondDS>> ordertuplesbasedonSortedFreqItemsR(Vector<Vector<CondDS>> alltuples, Vector<HeaderTableItem> freqOneItemsets) {
        Vector<Vector<CondDS>> ret = new Vector<>();
        for (Vector<CondDS> vi : alltuples) {
            ret.add(sortBasedonVectorR(vi, freqOneItemsets));

        }
        return ret;
    }

    private Vector<Vector<String>> ordertuplesbasedonSortedFreqItems(Vector<Vector<String>> alltuples, Vector<HeaderTableItem> freqOneItemsets) {
        Vector<Vector<String>> ret = new Vector<>();
        for (Vector<String> vi : alltuples) {
            ret.add(sortBasedonVector(vi, freqOneItemsets));

        }
        return ret;
    }

    Vector<HeaderTableItem> HeaderTable = new Vector<>();
    Map<String, Integer> dictionary = new HashMap<>();
    Vector<HeaderTableItem> removedoneHeaderTableItems = new Vector<>();


    void CollectFrequentItems() {
        HeaderTable.removeAllElements();
        removedoneHeaderTableItems.removeAllElements();
        for (Map.Entry<String, Integer> entry : dictionary.entrySet()) {
            HeaderTableItem tmp = new HeaderTableItem();
            tmp.Cardinality = entry.getValue();
            tmp.item = entry.getKey();
            if (entry.getValue() >= MinuimumSupportTreshhold) {

                HeaderTable.add(tmp);
            } else {
                removedoneHeaderTableItems.add(tmp);
            }
        }
    }


    void ScanDB() {

        for (Vector<String> vitem : alltuples) {
            for (String it : vitem) {
                if (dictionary.containsKey(it.toString())) {
                    int count = dictionary.get(it.toString());
                    dictionary.put(it.toString(), count + 1);
                } else {
                    dictionary.put(it.toString(), 1);
                }

            }

        }

    }


    private void ConstructBCondPatBase(Vector<String> b) {
    }
}