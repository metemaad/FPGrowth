import java.util.*;

/**
 * Created by Mohammad Etemad on 3/29/17 6:51 PM.
 */
public class FPGrowth {





    Vector < Vector < String >> alltuples = new Vector < > ();

    public void setAlltuples(Vector < Vector < String >> alltuples) {
        this.alltuples = alltuples;

    }

    FPTree FPTreeConstruction(int MinuimumSupportTreshhold)
    {
        Map<String, Integer> dic = ScanDB(alltuples);
        Vector < HeaderTableItem > Ht=CollectFrequentItems(dic,MinuimumSupportTreshhold);
        Collections.sort(Ht);

        System.out.print("ht:"+Ht+"\n");
        Vector < Vector < String >> SortedTuples = ordertuplesbasedonSortedFreqItems(alltuples, Ht);

        //Create root of Tree
        FPTree fpTree = new FPTree();
        fpTree.IsRoot = true;
        fpTree.item = "Null";
        //for each transaction T of DB
        for (Vector < String > FreqItemOfT: SortedTuples) {

            printrecord(FreqItemOfT);
            fpTree = insertFreqItemsOfT(FreqItemOfT, fpTree, Ht);
        }

        //System.out.print(fpTree);
        return fpTree;


    }

    private void printrecord(Vector<String> freqItemOfT) {

        for (String s:freqItemOfT
             ) {
            System.out.print(s+",");

        }
        System.out.print("\n");
    }

    Set < Vector < String >> FPgrowthFreqPatterns(FPTree fpTree,int MinuimumSupportTreshhold) {

        return FPgrowth(fpTree, null,MinuimumSupportTreshhold);
    }

    Set < Vector < String >> FPgrowth(FPTree fpTree, Vector < String > A, int MinuimumSupportTreshhold) {


        if (fpTree.child.size()==0){return null;}

        System.out.println("Mining : "+fpTree+" on "+A);

        FPTree P = null; //Single prefix path
        FPTree Q = null; // Multi Path Part
        Set < Vector < String >> freq_patQ = new HashSet<>();
        Set < Vector < String >> freq_patP = new HashSet<>();

        if (TreeHasSinglePrefixPath(fpTree) == true) {
            GeneratePatternsFromCombinationsInPplusA();
        } else {
            //e let Q be Tree
            //For each item Ai in Q
            Q = fpTree; //there is no Single prefix path

            Vector < HeaderTableItem > Ht = getHeadTable(Q,MinuimumSupportTreshhold);
            System.out.println("HT : "+Ht+" on "+A);



            for (HeaderTableItem Ai: Ht) {
                System.out.println("Mining : Ai :"+Ai);

                Vector < String > B = new Vector < > ();
                B.removeAllElements();
                B.add(Ai.item);

                if (A != null) B.addAll(A);
                System.out.println("Mining : B=ai+A :"+B);
                //b=ai+A **********************************
                //support ai
                MinuimumSupportTreshhold=Ai.Cardinality;
                System.out.println("Mining : Support Ai :"+Ai.Cardinality);

                Vector < Vector < CondDS >> newdataset = ConstructBCondPatBase(Ai, B, Ht);
                System.out.println( B+"Conditional Pattern Base :" );
                Printdataset(newdataset);
                //get the header of newdataset
                //sort the newdataset

                //System.out.print(newtuples.size());
                Map<String, Integer> dic = ScanCondDB(newdataset);
                Vector < HeaderTableItem > ht=CollectFrequentItems(dic,MinuimumSupportTreshhold);
                Collections.sort(ht);

                Vector < Vector < CondDS>> SortedTuples = ordertuplesbasedonSortedFreqItemsRCond(newdataset, ht);
                System.out.println( B+"Conditional Pattern Base HT:" +ht);
                //update MinuimumSupportTreshhold
                //**********************************
                FPTree Tree_B = ConstructBCondPatternBaseAndCondFPTreeCond(SortedTuples ,ht);
                Vector<String> tmp=new Vector<>();
                for (HeaderTableItem inb:ht) {
                    tmp.add(inb.item);
                }

                tmp.addAll(B);
                freq_patQ.add(tmp);
                System.out.println( "Freq: " +tmp);
                if (Tree_B.child.size() > 0) // Tree_BisNotEmpty())
                {

                    System.out.println("Treeb "+B+"|" +Tree_B);

                    Set < Vector < String >> FreqPatternB = FPgrowth(Tree_B, B,MinuimumSupportTreshhold);
                    System.out.println( "Freq: " +FreqPatternB);
                    freq_patQ.addAll(FreqPatternB);

                }
                freq_patQ.add(B);

            }

        }
        return freqPatternPQPXQ(freq_patP, freq_patQ);
    }

    private void Printdataset(Vector<Vector<CondDS>> convert) {

        for (Vector<CondDS> v:convert
             ) {


            for (CondDS s : v
                    ) {
                System.out.print(s.item+":"+s.cardinality + ",");

            }
            System.out.print("\n");
        }
    }

    FPTree ConstructBCondPatternBaseAndCondFPTree(Vector<Vector<String>> SortedTuples, Vector<HeaderTableItem> ht) {

        FPTree fpTree = new FPTree();
        FPTree root = fpTree;
        fpTree.IsRoot = true;
        fpTree.item = "Null";
        //for each transaction T of DB
        for (Vector < String > FreqItemOfT: SortedTuples) {

            fpTree = insertFreqItemsOfT(FreqItemOfT, fpTree,ht);
        }


        return root;
    }

    FPTree ConstructBCondPatternBaseAndCondFPTreeCond(Vector<Vector<CondDS>> SortedTuples, Vector<HeaderTableItem> ht) {

        FPTree fpTree = new FPTree();
        FPTree root = fpTree;
        fpTree.IsRoot = true;
        fpTree.item = "Null";
        //for each transaction T of DB
        for (Vector < CondDS > FreqItemOfT: SortedTuples) {

            fpTree = insertFreqItemsOfTCond(FreqItemOfT, fpTree,ht);
        }


        return root;
    }

    Vector < HeaderTableItem > getHeadTable(FPTree fpTree,int MinuimumSupportTreshhold) {

        Vector < HeaderTableItem > headerTable = new Vector < > ();

        Map < String, Integer > dic = new HashMap < > ();

        while (fpTree.next != null) {

            if (dic.containsKey(fpTree.item)) {
                int i = dic.get(fpTree.item);
                dic.put(fpTree.item, i + fpTree.cardinality);

            } else {
                if (!fpTree.IsRoot)
                    dic.put(fpTree.item, fpTree.cardinality);
            }

            fpTree = fpTree.next;
        }

        //last node
        if (dic.containsKey(fpTree.item)) {
            int i = dic.get(fpTree.item);
            dic.put(fpTree.item, i + fpTree.cardinality);

        } else {
            if (!fpTree.IsRoot)
                dic.put(fpTree.item, fpTree.cardinality);
        }


        for (Map.Entry < String, Integer > entry: dic.entrySet()) {
            HeaderTableItem tmp = new HeaderTableItem();
            tmp.Cardinality = entry.getValue();
            tmp.item = entry.getKey();
            tmp.headofNodeLink = search(tmp.item, fpTree);
            if (entry.getValue() >= MinuimumSupportTreshhold) {

                headerTable.add(tmp);
            }
        }

        Collections.sort(headerTable);
        return headerTable;
    }
    Vector < Vector < String >> convert(Vector < Vector < CondDS >> d)
    {
        Vector < Vector < String >> retd = new Vector < > ();


        for (Vector < CondDS > v:d) {
            Vector<String> s=new Vector<>();
            for (CondDS c:v) {
                s.add(c.item);
            }
            retd.add(s);

        }
        return retd;

    }

    Vector < Vector < CondDS >> ConstructBCondPatBase(HeaderTableItem ai, Vector < String > B, Vector < HeaderTableItem > ht) {

        //fetch all the paths
        FPTree first = ai.headofNodeLink;
        Vector < FPTree > list = new Vector < > ();
        while (first != null) {
            list.add(first);
            first = first.nodeLink;
        }

        Map < String, Integer > dic = new HashMap < > ();

        //generate records based on each path
        Vector < Vector < CondDS >> newdataset = new Vector < > ();
        for (FPTree l: list) {
            Vector < CondDS > record = new Vector < > ();
            FPTree p = l;
            int car = p.cardinality;
            while ((!p.item.equals("Null"))) {
                if ((!p.item.equals(ai.item))) {
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

        return newdataset;
    }
    

    Set < Vector < String >> freqPatternPQPXQ(Set < Vector < String >> freq_pat_P, Set < Vector < String >> freq_pat_Q) {
        Set < Vector < String >> ret = new HashSet<>();
        ret.addAll(freq_pat_P);
        ret.addAll(freq_pat_Q);
        if (freq_pat_P.size() > 0 & freq_pat_Q.size() > 0) {
            for (Vector < String > patp: freq_pat_P) {
                for (Vector < String > patq: freq_pat_Q) {
                    Set < String > tmp = new HashSet < > ();
                    tmp.addAll(patp);
                    tmp.addAll(patq);

                    ret.add((Vector < String > ) tmp);

                }
            }
        }
        return ret;

    }

    private void GeneratePatternsFromCombinationsInPplusA() {}

    boolean TreeHasSinglePrefixPath(FPTree fpTree) {

        //return fpTree.child.size() <= 1 && (fpTree.child.size() != 1 || TreeHasSinglePrefixPath(fpTree.child.get(0)));
        return false;

    }


    FPTree search(String item, FPTree fpTree) {

        while (!fpTree.IsRoot) {
            fpTree = fpTree.parent;
        }
        while (fpTree.next != null) {
            if ((fpTree.item.equals(item)) & (fpTree.Isstart)) {
                return fpTree;

            }
            fpTree = fpTree.next;
        }
        return null;
    }
    boolean isintree(String item, FPTree fpTree) {

        while (fpTree.next != null) {
            if ((fpTree.item.equals(item))) {
                return true;

            }
            fpTree = fpTree.next;
        }
        return false;
    }

    FPTree iteminfq1listhaspointer(String item, FPTree fpTree, Vector < HeaderTableItem > HeaderTable) {
        FPTree p = null;
        FPTree ret = null;
        HeaderTableItem htt = null;
        for (HeaderTableItem ht: HeaderTable) {
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


    FPTree findendpointer(FPTree root) {
        if (root.next == null) return root;
        while (root.next != null) {
            root = root.next;
        }
        return root;

    }

    FPTree insertFreqItemsOfTCond(Vector<CondDS> FreqItemOfT, FPTree fpTree, Vector<HeaderTableItem> ht)

    {
        FPTree root = fpTree;
        FPTree rootend;

        FPTree pointer = fpTree;
        FPTree pointer2 = fpTree;
        for (CondDS i: FreqItemOfT) {
            pointer2 = pointer;
            for (FPTree ch: pointer.child) {
                if (ch.item.equals(i.item)) {
                    pointer = ch;
                    pointer.cardinality += i.cardinality;
                    rootend = findendpointer(root);
                    if ((pointer.next == null)&(pointer!=rootend)) {
                        rootend.next = pointer;
                    }

                    break;

                }

            }
            if (pointer == pointer2) {

                boolean f = isintree(i.item, root);
                FPTree newbranch = new FPTree();
                newbranch.Isstart = !f;
                newbranch.parent = pointer;
                newbranch.item = i.item;
                FPTree pl = iteminfq1listhaspointer(i.item, root,ht);
                if (pl == null) {
                    setfq1pointer(i.item, newbranch,ht);
                } else {
                    pl.nodeLink = newbranch;
                }
                newbranch.cardinality = i.cardinality;
                rootend = findendpointer(root);
                rootend.next = newbranch;
                newbranch.next = null;
                pointer.child.add(newbranch);
                pointer = newbranch;
            }
        }
        return fpTree;

    }

    FPTree insertFreqItemsOfT(Vector<String> FreqItemOfT, FPTree fpTree, Vector<HeaderTableItem> ht)

    {
        FPTree root = fpTree;
        FPTree rootend;

        FPTree pointer = fpTree;
        FPTree pointer2 = fpTree;
        for (String i: FreqItemOfT) {
            pointer2 = pointer;
            for (FPTree ch: pointer.child) {
                if (ch.item.equals(i.toString())) {
                    pointer = ch;
                    pointer.cardinality += 1;
                    rootend = findendpointer(root);
                    if (pointer.next == null) {
                        rootend.next = pointer;
                    }

                    break;

                }

            }
            if (pointer == pointer2) {

                boolean f = isintree(i.toString(), root);
                FPTree newbranch = new FPTree();
                newbranch.Isstart = !f;
                newbranch.parent = pointer;
                newbranch.item = i.toString();
                FPTree pl = iteminfq1listhaspointer(i.toString(), root,ht);
                if (pl == null) {
                    setfq1pointer(i.toString(), newbranch,ht);
                } else {
                    pl.nodeLink = newbranch;
                }
                newbranch.cardinality = 1;
                rootend = findendpointer(root);
                rootend.next = newbranch;
                newbranch.next = null;
                pointer.child.add(newbranch);
                pointer = newbranch;
            }
        }
        return fpTree;

    }

    private void setfq1pointer(String s, FPTree newbranch,Vector < HeaderTableItem > HeaderTable) {
        for (HeaderTableItem f: HeaderTable) {
            if (f.item.equals(s)) {
                f.headofNodeLink = newbranch;
                break;

            }

        }
    }

    Vector < CondDS > sortBasedonVectorR(Vector < CondDS > tuple, Vector < HeaderTableItem > order) {
        Vector < CondDS > tmp = new Vector < > ();
        for (HeaderTableItem tmpI: order) {

            for (CondDS tupleitm: tuple) {
                if (tupleitm.item.toString().equals(tmpI.item)) {

                    //tupleitm.setCardinality(tmpI.Cardinality);
                    tmp.add(0, tupleitm);
                    break;
                }

            }

        }
        return tmp;

    }

    Vector < String > sortBasedonVector(Vector < String > tuple, Vector < HeaderTableItem > order) {
        Vector < String > tmp = new Vector < > ();
        for (HeaderTableItem tmpI: order) {

            for (String tupleitm: tuple) {
                if (tupleitm.toString().equals(tmpI.item)) {

                    //tupleitm.setCardinality(tmpI.Cardinality);
                    tmp.add(0, tupleitm);
                    break;
                }

            }

        }
        return tmp;

    }

    private Vector < Vector < String >> ordertuplesbasedonSortedFreqItemsR(Vector < Vector < CondDS >> alltuples, Vector < HeaderTableItem > freqOneItemsets) {
        Vector < Vector < CondDS >> ret = new Vector < > ();
        for (Vector < CondDS > vi: alltuples) {
            ret.add(sortBasedonVectorR(vi, freqOneItemsets));

        }
        Set<Set<String>> d=new HashSet<>();
        for (Vector<CondDS> v:ret ) {
            Set<String> s=new HashSet<>();
            int i=0;
            for (CondDS c:v) {
                s.add(c.item);
            }
            d.add(s);

        }
        Vector < Vector < String >> retd = new Vector < > ();


        for (Set<String> v:d) {
            Vector<String> s=new Vector<>();
            for (String c:v) {
                s.add(c);
            }
            retd.add(s);

        }
        return retd;
    }
    private Vector < Vector < CondDS>> ordertuplesbasedonSortedFreqItemsRCond(Vector < Vector < CondDS >> alltuples, Vector < HeaderTableItem > freqOneItemsets) {
        Vector < Vector < CondDS >> ret = new Vector < > ();
        for (Vector < CondDS > vi: alltuples) {
            ret.add(sortBasedonVectorR(vi, freqOneItemsets));

        }

        return ret;
    }

    private Vector < Vector < String >> ordertuplesbasedonSortedFreqItems(Vector < Vector < String >> alltuples, Vector < HeaderTableItem > freqOneItemsets) {
        Vector < Vector < String >> ret = new Vector < > ();
        for (Vector < String > vi: alltuples) {
            ret.add(sortBasedonVector(vi, freqOneItemsets));

        }
        return ret;
    }

    //Vector < HeaderTableItem > HeaderTable = new Vector < > ();




    Vector<HeaderTableItem> CollectFrequentItems(Map < String, Integer > dictionary ,int MinuimumSupportTreshhold) {
        Vector < HeaderTableItem > HeaderTable = new Vector < > ();
        HeaderTable.removeAllElements();

        for (Map.Entry < String, Integer > entry: dictionary.entrySet()) {
            HeaderTableItem tmp = new HeaderTableItem();
            tmp.Cardinality = entry.getValue();
            tmp.item = entry.getKey();
            if (entry.getValue() >= MinuimumSupportTreshhold) {

                HeaderTable.add(tmp);
            }
        }
        return HeaderTable;
    }


    Map<String, Integer> ScanDB(Vector<Vector<String>> alltuples) {
        Map < String, Integer > dictionary = new HashMap < > ();
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

        return dictionary;
    }

    Map<String, Integer> ScanCondDB(Vector<Vector<CondDS>> alltuples) {
        Map < String, Integer > dictionary = new HashMap < > ();
        for (Vector < CondDS > vitem: alltuples) {
            for (CondDS it: vitem) {
                if (dictionary.containsKey(it.item)) {
                    int count = dictionary.get(it.item);
                    dictionary.put(it.item, count + it.cardinality);
                } else {
                    dictionary.put(it.item, it.cardinality);
                }

            }

        }

        return dictionary;
    }

}