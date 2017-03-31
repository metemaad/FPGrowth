import java.util.*;

/**
 * This class store a node of FPTree
 * Created by mohammad on 3/29/17.
 */
public class FPTree implements Comparable<FPTree>{

    /**
     * If this node is the root or not
     */
    boolean IsRoot=false;
    FPTree next=null;

    /**
     * child of this node
     */
    Vector<FPTree> child= new Vector<>();
    /**
     * parent of this node
     */
    FPTree parent;
    /**
     * this node item
     */
    String item;

    /**
     * stores the number of that this item occures
     */
    int cardinality;

    /**
     * shows a threat to the other similar items
     */
    FPTree nodeLink =null;

//    int threadcardinality(){ return nodeLink !=null?this.cardinality+ nodeLink.cardinality:this.cardinality;}

    @Override
    public String toString()
    {
//        String s=" [ ";
//        for (FPTree ch:this.child ) {
//            s+=" "+ch.toString();
//
//        }
//        s+=" ] \n";
        return "["+item+":"+cardinality+"]";//+" of "+ threadcardinality()+ "|"+"]";
    }

    /**
     * compare fpTree with this node based on their cardinality
     * @param fpTree
     * @return returns 1 if this.cardinality> input. cardinality
     */

    boolean Isstart=false;

    @Override
    public int compareTo(FPTree fpTree) {
        if (this.cardinality>fpTree.cardinality)
        {
            return 1;
        }
        else
            return this.cardinality < fpTree.cardinality ? -1 : 0;
    }

}

