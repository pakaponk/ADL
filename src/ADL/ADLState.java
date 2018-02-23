package ADL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pakapon on 3/3/2017 AD.
 */
public class ADLState {
    public String name;

    List<ADLSequence> sequences;

    ADLState(String name){
        this.name = name;
        this.sequences = new ArrayList<>();
    }

    public List<ADLSequence> getSequences() {
        return sequences;
    }
}
