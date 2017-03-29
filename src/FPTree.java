import java.util.Vector;

/**
 * This class store a node of FPTree
 * Created by mohammad on 3/29/17.
 */
public class FPTree implements Comparable<FPTree>{

    /**
     * If this node is the root or not
     */
    boolean IsRoot=false;
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
     *
     * @return returns the item value
     */
    public String getItem() {
        return item;
    }

    /**
     * set the item value
     * @param item
     */
    public void setItem(String item) {

        this.item = item;
    }

    /**
     * stores the number of that this item occures
     */
    int cardinality;

    /**
     * shows a threat to the other similar items
     */
    FPTree thread=null;

    int threadcardinality(){ return thread!=null?this.cardinality+thread.cardinality:this.cardinality;}

    @Override
    public String toString() {
        return "["+item+":"+threadcardinality()+"]";
    }

    /**
     * compare fpTree with this node based on their cardinality
     * @param fpTree
     * @return returns 1 if this.cardinality> input. cardinality
     */


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

