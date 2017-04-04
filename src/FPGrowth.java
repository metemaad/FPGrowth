import java.util.*;

/**
 * Created by Mohammad Etemad on 3/29/17 6:51 PM.
 */
class FPGrowth {





    private Vector < Vector < String >> alltuples = new Vector < > ();

    public void setAlltuples(Vector < Vector < String >> alltuples) {
        this.alltuples = alltuples;

    }

    FPTreePack FPTreeConstruction(int MinuimumSupportTreshhold)
    {
        FPTreePack fpTreePack=new FPTreePack();

        Map<String, Integer> dic = ScanDB(alltuples);
        fpTreePack.alltuplesNo=alltuples.size();
        Vector < HeaderTableItem > Ht=CollectFrequentItems(dic,MinuimumSupportTreshhold);
        Collections.sort(Ht);
        fpTreePack.HeaderTable=Ht;

//        System.out.print("ht:"+Ht+"\n");
        Vector < Vector < String >> SortedTuples = ordertuplesbasedonSortedFreqItems(alltuples, Ht);

        FPTree fpTree = new FPTree();
        fpTree.IsRoot = true;
        fpTree.item = "Null";
        fpTree.last=fpTree;
        for (int i = 0, sortedTuplesSize = SortedTuples.size(); i < sortedTuplesSize; i++) {
            Vector<String> FreqItemOfT = SortedTuples.get(i);
            fpTree = insertFreqItemsOfT(FreqItemOfT, fpTree, Ht);
        }

  //     System.out.println("done.");
        fpTreePack.fpTree=fpTree;
        return fpTreePack;


    }

    Set < FrequentPattern> FPgrowthFreqPatterns(FPTree fpTree,int MinuimumSupportTreshhold) {

        return FPgrowth(fpTree, null,MinuimumSupportTreshhold);
    }

    private Set < FrequentPattern> FPgrowth(FPTree fpTree, Set<String> A, int MinuimumSupportTreshhold) {


        FPTree P; //Single prefix path
        FPTree Q; // Multi Path Part
        Set < FrequentPattern> freq_patQ = new HashSet<>();
        Set < FrequentPattern> freq_patP = new HashSet<>();
        dividetwographs dv=TreeHasSinglePrefixPath(fpTree);
        if ( dv.Multipart!=fpTree)
        {
            P=dv.Singlepart;
            Q=dv.Multipart;
            Set<Set<String>> PPset=new HashSet<>();
            int min_sup = MinuimumSupportTreshhold;

            Set<String> Pset=new HashSet<>();
            Pset.add(P.item);
            PPset.add(Pset);

            min_sup=P.cardinality;

            while(P.child.size()>0)
            {
                Pset=new HashSet<>();
                Pset.add(P.child.get(0).item);
                PPset.add(Pset);
                P=P.child.get(0);
                min_sup=P.child.get(0).cardinality;
            }

            Set<Set<String>> SelfPset=selfJoining(PPset);
            for (Set<String> set:SelfPset) {
                FrequentPattern B = new FrequentPattern();

                B.pattern.addAll(set);
                B.Support=min_sup;
                if (A != null) B.pattern.addAll(A);
                freq_patP.add(B);
            }


        }
        else
        {
            Q = fpTree;

            Vector < HeaderTableItem > Ht = getHeadTable(Q,MinuimumSupportTreshhold);


            for (HeaderTableItem Ai: Ht) {


                Set < String > B = new HashSet<>();
                B.clear();
                B.add(Ai.item);
                if (A != null) B.addAll(A);

                FrequentPattern FpB=new FrequentPattern();
                FpB.pattern=B;
                FpB.Support=Ai.Cardinality;

                freq_patQ.add(FpB);




                Vector < Vector < CondDS >> newdataset = ConstructBCondPatBase(Ai);
                Map<String, Integer> dic = ScanCondDB(newdataset);
                Vector < HeaderTableItem > ht=CollectFrequentItems(dic,MinuimumSupportTreshhold);
                Collections.sort(ht);

                Vector < Vector < CondDS>> SortedTuples = ordertuplesbasedonSortedFreqItemsRCond(newdataset, ht);

                FPTree Tree_B = GenerateBTree(SortedTuples ,ht);



                if (Tree_B.child.size() > 0)
                {

                    Set<FrequentPattern> FreqPatternB = FPgrowth(Tree_B, B, MinuimumSupportTreshhold);
                    freq_patQ.addAll(FreqPatternB);
                }

            }

        }
        return freqPatternPQPXQ(freq_patP, freq_patQ);
    }
    public static Set<Set<String>> selfJoining(Set<Set<String>> L) {
        Set<Set<String>> res = new HashSet<Set<String>>();
        Iterator<Set<String>> itr = L.iterator();
        int ik = 1;
        itr.next();

        while (itr.hasNext()) {
            Set<String> AP = new HashSet<String>();
            AP = itr.next();
            Iterator<Set<String>> jtr = L.iterator();
            int jk = 0;
            while (jtr.hasNext()) {
                Set<String> BP = new HashSet<String>();
                BP = jtr.next();
                Set<String> mrg = new HashSet<String>();
                mrg.addAll(AP);
                mrg.addAll(BP);
                if (mrg.size() == AP.size() + 1) {
                    res.add(mrg);
                }
                jk++;
                if (jk > ik)
                    break;
            }
            ik++;
        }
        return res;
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

    private FPTree GenerateBTree(Vector<Vector<CondDS>> SortedTuples, Vector<HeaderTableItem> ht) {

        FPTree fpTree = new FPTree();
        FPTree root = fpTree;
        fpTree.IsRoot = true;
        fpTree.item = "Null";
        fpTree.last=fpTree;
        for (Vector < CondDS > FreqItemOfT: SortedTuples) {

            fpTree = insertFreqItemsOfTCond(FreqItemOfT, fpTree,ht);
        }


        return root;
    }

    private Vector < HeaderTableItem > getHeadTable(FPTree fpTree, int MinuimumSupportTreshhold) {

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

    private Vector < Vector < CondDS >> ConstructBCondPatBase(HeaderTableItem ai) {

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
        for (FPTree l: list)
        {
            Vector < CondDS > record = new Vector < > ();
            FPTree p = l;
            int car = p.cardinality;
            while ((!p.item.equals("Null")))
            {
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

    Set<FrequentPattern> Product(Set<FrequentPattern> a, Set<FrequentPattern> b) {
        Set<FrequentPattern> product = new HashSet<>();

        if (a.size()==0) return b;
        if (b.size()==0) return a;
        for(FrequentPattern s : a) {

            for(FrequentPattern t : b) {
                FrequentPattern tmp= new FrequentPattern();

                tmp.pattern.addAll(s.pattern);

                tmp.pattern.addAll(t.pattern);

                product.add(tmp);
            }

        }
        return  product;


    }
    private Set < FrequentPattern> freqPatternPQPXQ(Set<FrequentPattern> freq_pat_P, Set<FrequentPattern> freq_pat_Q) {
        Set < FrequentPattern> ret = new HashSet<>();
        // P U Q
        ret.addAll(freq_pat_P);
        ret.addAll(freq_pat_Q);

        Set < FrequentPattern>  prod=Product(freq_pat_P,freq_pat_Q);

        ret.addAll(prod);
        return ret;

    }


    private dividetwographs TreeHasSinglePrefixPath(FPTree fpTree) {

        dividetwographs ret=new dividetwographs();


boolean o=false;
        FPTree multipart;


            multipart=new FPTree();
            multipart.IsRoot=false;
            multipart.item="Null";

            FPTree Singlepart=fpTree;
            while(Singlepart.child.size()==1)
            {
                Singlepart=Singlepart.child.get(0);
o=true;
            }

       if (o){     multipart.child.addAll(Singlepart.child);
        Singlepart.child.removeAllElements();
           ret.Singlepart=Singlepart;
           ret.Multipart=multipart;
           ret.Singlepart=fpTree;
           ret.Multipart=fpTree;
            }else
                {
                    ret.Singlepart=fpTree;
                    ret.Multipart=fpTree;
                }




        return ret;

    }


    private FPTree search(String item, FPTree fpTree) {

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
    private boolean isintree(String item, FPTree fpTree) {

        while (fpTree.next != null) {
            if ((fpTree.item.equals(item))) {
                return true;

            }
            fpTree = fpTree.next;
        }
        return false;
    }

    private FPTree iteminfq1listhaspointer(String item, Vector<HeaderTableItem> HeaderTable) {
        FPTree p;
        FPTree ret;
        HeaderTableItem htt = null;
        for (HeaderTableItem ht: HeaderTable) {
            if (ht.item.equals(item)) {
                htt = ht;
                break;
            }

        }
        assert htt != null;
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


    private FPTree findendpointer(FPTree root) {

        if (root.next == null) return root;
        while (root.next != null) {
            root = root.next;
        }
            return root;



    }

    private FPTree insertFreqItemsOfTCond(Vector<CondDS> FreqItemOfT, FPTree fpTree, Vector<HeaderTableItem> ht)

    {
        FPTree root = fpTree;
        FPTree rootend;

        FPTree pointer = fpTree;
        FPTree pointer2;
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
                FPTree pl = iteminfq1listhaspointer(i.item, ht);
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

    private FPTree insertFreqItemsOfT(Vector<String> FreqItemOfT, FPTree fpTree, Vector<HeaderTableItem> ht)

    {
        FPTree root = fpTree;
        FPTree rootend=fpTree.last;

        FPTree pointer = fpTree;
        FPTree pointer2;
        for (String i: FreqItemOfT) {
            pointer2 = pointer;
            for (FPTree ch: pointer.child) {
                if (ch.item.equals(i)) {
                    pointer = ch;
                    pointer.cardinality += 1;
                    //rootend = findendpointer(root);
                    if ((pointer.next == null)&(pointer!=rootend))
                    {
                        root.last.next = pointer;
                        root.last = pointer;
                        pointer.next=null;
                    }

                    break;

                }

            }
            if (pointer == pointer2) {

                boolean f = isintree(i, root);
                FPTree newbranch = new FPTree();
                newbranch.Isstart = !f;
                newbranch.parent = pointer;
                newbranch.item = i;
                FPTree pl = iteminfq1listhaspointer(i, ht);
                if (pl == null) {
                    setfq1pointer(i, newbranch,ht);
                } else {
                    pl.nodeLink = newbranch;
                }
                newbranch.cardinality = 1;
                //rootend = findendpointer(root);
                root.last.next=newbranch;
                root.last = newbranch;


                newbranch.next = null;
                pointer.child.add(newbranch);
                pointer = newbranch;
            }
        }
        return fpTree;

    }

    private FPTree insertFreqItemsOfT2(Vector<CondDS> FreqItemOfT, FPTree fpTree, Vector<HeaderTableItem> ht)

    {
        FPTree root = fpTree;
        FPTree rootend=fpTree.last;

        FPTree pointer = fpTree;
        FPTree pointer2;
        for (CondDS condDS: FreqItemOfT) {
            pointer2 = pointer;
            for (FPTree ch: pointer.child) {
                if (ch.item.equals(condDS.item)) {
                    pointer = ch;
                    pointer.cardinality += 1;

                    if ((pointer.next == null)&(pointer!=rootend))
                    {
                        root.last.next = pointer;
                        root.last = pointer;
                        pointer.next=null;
                    }

                    break;

                }

            }
            if (pointer == pointer2) {

                boolean f = isintree(condDS.item, root);
                FPTree newbranch = new FPTree();
                newbranch.Isstart = !f;
                newbranch.parent = pointer;
                newbranch.item = condDS.item;
                FPTree pl = iteminfq1listhaspointer(condDS.item, ht);
                if (pl == null) {
                    setfq1pointer(condDS.item, newbranch,ht);
                } else {
                    pl.nodeLink = newbranch;
                }
                newbranch.cardinality = 1;
                //rootend = findendpointer(root);
                root.last.next=newbranch;
                root.last = newbranch;


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

    private Vector < CondDS > sortBasedonVectorR(Vector<CondDS> tuple, Vector<HeaderTableItem> order) {
        Vector < CondDS > tmp = new Vector < > ();
        for (HeaderTableItem tmpI: order) {

            for (CondDS tupleitm: tuple) {
                if (tupleitm.item.equals(tmpI.item)) {


                    tmp.add(0, tupleitm);
                    break;
                }

            }

        }
        return tmp;

    }

    private Vector < String > sortBasedonVector(Vector<String> tuple, Vector<HeaderTableItem> order) {
        Vector < String > tmp = new Vector < > ();
        for (HeaderTableItem tmpI: order) {

            for (String tupleitm: tuple) {
                if (tupleitm.equals(tmpI.item)) {

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





    private Vector<HeaderTableItem> CollectFrequentItems(Map<String, Integer> dictionary, int MinuimumSupportTreshhold) {

        Vector < HeaderTableItem > HeaderTable = new Vector < > ();
        HeaderTable.removeAllElements();

        for (Map.Entry < String, Integer > entry: dictionary.entrySet())
        {
            HeaderTableItem tmp = new HeaderTableItem();
            tmp.Cardinality = entry.getValue();
            tmp.item = entry.getKey();
            if (entry.getValue() >= MinuimumSupportTreshhold) HeaderTable.add(tmp);
        }
        return HeaderTable;
    }


    private Map<String, Integer> ScanDB(Vector<Vector<String>> alltuples)
    {
        Map < String, Integer > dictionary = new HashMap < > ();
        for (Vector < String > vitem: alltuples) {
            for (String it: vitem)
            {
                if (dictionary.containsKey(it))
                {
                    int count = dictionary.get(it);
                    dictionary.put(it, count + 1);
                } else {
                    dictionary.put(it, 1);
                }

            }

        }

        return dictionary;
    }

    private Map<String, Integer> ScanCondDB(Vector<Vector<CondDS>> alltuples) {
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


    public Set<Set<String>> allSubsetsWithNMember(Set<String> frequentItem, Integer n) {

        Set<Set<String>> allSubsets = powerSet(frequentItem);
        Set<Set<String>> allSubsetstmp = new HashSet<Set<String>>();
        for (Set<String> set : allSubsets) {
            if (set.size() == n) {
                allSubsetstmp.add(set);
            }
        }

        return allSubsetstmp;

    }
    public  Set<Set<String>> powerSet(Set<String> A) {

        Set<Set<String>> subSets = new HashSet<Set<String>>();
        for (String addToSets : A) {
            Set<Set<String>> newSets = new HashSet<Set<String>>();
            for (Set<String> curSet : subSets) {
                Set<String> copyPlusNew = new HashSet<String>();
                copyPlusNew.addAll(curSet);
                copyPlusNew.add(addToSets);
                newSets.add(copyPlusNew);
            }
            Set<String> newValSet = new HashSet<String>();
            newValSet.add(addToSets);
            newSets.add(newValSet);
            subSets.addAll(newSets);
        }
        /*
         * for (Set<String> set : subSets) { for (String setEntry : set) {
		 * System.out.print(setEntry + " "); } System.out.println(); }
		 */
        return subSets;
    }

    public  Set<Set<String>> ruleGeneration(Set<Set<String>> freqitemsets, float minimumSupport,
    float minimumConfidence,FPTreePack fpTreePack)
    {


        Set<Set<String>> ret=new HashSet<>();
        for (Set<String> fset:freqitemsets ) {
            if (fset.size()>1){
                Set<Set<String>> tmp=ruleGenerationWithPruning(fset,minimumSupport,minimumConfidence,fpTreePack);
                    ret.addAll(tmp);
            }
        }
        return ret;
    }
    public  Set<Set<String>> ruleGenerationWithPruning(Set<String> l, float minimumSupport,
                                                             float minimumConfidence,FPTreePack fpTreePack) {

        int m = l.size();

        Set<Set<String>> bad = new HashSet<Set<String>>();
        Set<Set<String>> good = new HashSet<Set<String>>();
        for (int i = m - 1; i > 0; i--) {

            Set<Set<String>> lk = allSubsetsWithNMember(l, i);

            if (bad.size() > 0) {
                Set<Set<String>> lkn = new HashSet<Set<String>>();
                for (Set<String> set : lk) {
                    Set<String> b = new HashSet<String>();
                    b.addAll(l);
                    b.removeAll(set);
                    for (Set<String> bd : bad) {
                        boolean chk = true;
                        for (String s : bd) {
                            chk = chk & b.contains(s);
                        }
                        if (!chk) {
                            lkn.add(set);
                            break;
                        }
                    }
                }

                lk.removeAll(lk);
                lk.addAll(lkn);
            }
            for (Set<String> set : lk) {
                if (CheckConfidenceOfItemset(set, l, minimumSupport, minimumConfidence,fpTreePack)) {
                    good.add(set);

                } else {
                    Set<String> b = new HashSet<String>();
                    b.addAll(l);
                    b.removeAll(set);
                    bad.add(b);

                }
            }
        }

        return good;
    }

    int support(Set<String> s,FPTreePack fpTreePack)
    {
        Vector < Vector < String >> itemset=new Vector<>();
        Vector < String > item=new Vector<>();
        item.addAll(s);
        itemset.add(item);
        Vector < Vector < String >> SortedTuples = ordertuplesbasedonSortedFreqItems(itemset,fpTreePack.HeaderTable);
        FPTree pointer=fpTreePack.fpTree;
        for (Vector<String> st:SortedTuples ) {
            for (String is:st ) {
                for (FPTree ch:pointer.child ) {
                    if(ch.item.equals(is))
                    {pointer=ch;
                        break;
                    }
                }
            }

        }
        return pointer.cardinality;

        //sort items based on ht;
        // traverse the tree to the end item
        //find the leaf cardinality
    }
    private boolean CheckConfidenceOfItemset(Set<String> set, Set<String> lk, float minimumSupport,
                                                    float minimumConfidence
    , FPTreePack fpTreePack) {

        int transactionNo = fpTreePack.alltuplesNo;
        int c_set =support(set,fpTreePack);
        int c_lk = support(lk,fpTreePack);
        float support = (float) c_lk / transactionNo;
        float confidence = (float) c_lk / c_set;

        if ((support >= minimumSupport) & (confidence >= minimumConfidence)) {

            return true;
        } else {
            return false;
        }

    }


}