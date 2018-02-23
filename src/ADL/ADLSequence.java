package ADL;

import ADL.Actions.ADLAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pakapon on 3/3/2017 AD.
 */
public class ADLSequence {

    public String name;
    public int currentIndex;
    public List<ADLAction> actions;

    public ADLSequence(String name) {
        this.name = name;
        this.actions = new ArrayList<>();
        this.currentIndex = 0;
    }

    public void nextIndex(){
        this.currentIndex++;
        this.currentIndex %= this.actions.size();
    }
}
