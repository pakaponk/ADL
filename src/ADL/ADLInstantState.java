package ADL;

/**
 * Created by Pakapon on 5/18/2017 AD.
 */
public class ADLInstantState extends ADLState{

    public ADLInstantState(String name) {
        super(name);
    }

    public void performActions(ADLAgent agent){
        this.getInitialSequence().actions.forEach(action -> {
            action.performAction(agent);
        });
    }

    private ADLSequence getInitialSequence(){
        return this.sequences.get(0);
    }
}
