/**
 * Created by mohammad on 3/29/17.
 */
public class ItemSet implements Comparable<ItemSet> {
    String item;
    int Cardinality;


    @Override
    public int compareTo(ItemSet o) {
        if (this.Cardinality>o.Cardinality)
        {
            return 1;
        }
        else
            return this.Cardinality < o.Cardinality ? -1 : 0;

    }
}
