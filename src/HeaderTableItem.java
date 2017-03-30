/**
 * Created by mohammad on 3/29/17.
 */
public class HeaderTableItem implements Comparable<HeaderTableItem> {
    String item;
    int Cardinality;


    @Override
    public int compareTo(HeaderTableItem o) {
        if (this.Cardinality>o.Cardinality)
        {
            return 1;
        }
        else
            return this.Cardinality < o.Cardinality ? -1 : 0;

    }

    FPTree headofNodeLink =null;
    @Override
    public String toString() {
        return item+":"+Cardinality;
    }
}
