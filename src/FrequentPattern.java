import java.util.HashSet;
import java.util.Set;

/**
 * Created by Mohammad Etemad on 4/3/17 10:00 PM.
 */
public class FrequentPattern {
    Set<String> pattern=new HashSet<>();
    int Support=0;

    @Override
    public String toString() {
        return pattern.toString()+" : "+Support;
    }
}
