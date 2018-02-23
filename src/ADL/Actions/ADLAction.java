package ADL.Actions;

import ADL.ADLAgent;
import ADL.ADLParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pakapon on 3/3/2017 AD.
 */
public abstract class ADLAction {

    private static ADLAction performingAction = null;

    public static ADLAction getPerformingAction(){
        return ADLAction.performingAction;
    }

    static Double convertToDouble(Object value){
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        }
        else {
            return (Double) value;
        }
    }

    protected String name;

    protected List<ADLParameter> parameters;

    public ADLAction(String name) {
        this.name = name;
        this.parameters = new ArrayList<>();
    }

    public void performAction(ADLAgent agent){
        ADLAction.performingAction = this;

        agent.increaseElapsedTime(this);
    }

    @Override
    public String toString() {
        return this.parameters
                .stream()
                .map(ADLParameter::toString)
                .reduce((String a, String b) -> a + ", " + b)
                .map(s -> this.name + "(" + s + ")")
                .orElseGet(() -> this.name + "()");
    }

    // Getter Methods

    public List<ADLParameter> getParameters() {
        return parameters;
    }

    public String getName() {
        return name;
    }
}
