import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mohammad Etemad on 3/9/17.
 */
public class Attribute {

    String AttributeName;
    /**
     * this is a set of class names. all of the classes are in an attribute are listed in this set.
     * when we need to access this aggrigated information we just call it without forcing to check the dataset again
     */
    Set < String > AttributeClasses = new HashSet < > ();

    /**
     * we save the number counted for each class in attribute in this dictionary to have an instance access to it when it is necessary.
     */
    Map < String, Integer > countDictionary = new HashMap < > ();
    /**
     * the total number of items has been readed in this attribute. it shows how many items are in this attribute in total inculding all the sub classes.
     */
    int total = 0;

    /**
     * each time we add a class to our attribute it adds to the number of that type of class. in this way the function calculate the cardinality of each class
     * while adding them to the structure
     * @param s this is the name of the class to be added
     */
    public void addClass(String s) {
        AttributeClasses.add(s);
        total++;
        if (!countDictionary.containsKey(s)) {
            countDictionary.put(s, 1);
        } else {
            Integer val = countDictionary.get(s);
            countDictionary.put(s, val + 1);
        }

    }

    public int classTotal() {        return total;    }

    /**
     * this function search a class name in a dictionary and returns the number of items in this class that appeared in the related dataset
     * @param Classname this is the name of a class in an attribute which its cardinality is required.
     * @return the return value is the number counted for the input class which is saved in a dictionary.
     */
    public int classCount(String Classname) {

        int val = 0;
        if (!countDictionary.containsKey(Classname)) {
            val = 0;
        } else {
            val = countDictionary.get(Classname);
        }
        return val;


    }

    /**
     * this function defines that how two different attributes should be compared.
     * @param obj this is an attribute instance that is going to be compared
     * @return if the attribute names are equal the result is true.
     */
    @Override
    public boolean equals(Object obj) {
        if (this.AttributeName==((Attribute)obj).AttributeName){return true; }else
        {return false;}


    }

    /**
     *  a description for the attribute instance
     * @return returns a full description of an attribute and classes in it
     */
    @Override
    public String toString() {
        String st="";
        for (String cls:this.AttributeClasses ) {
            st+=" "+cls+"("+this.classCount(cls)+") ";

        }
        return " [ "+this.AttributeName+","+this.total+"|"+st+"]";
    }
}