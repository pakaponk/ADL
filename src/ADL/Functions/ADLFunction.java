package ADL.Functions;

import ADL.ADLAgent;
import ADL.ADLParameter;
import ADL.ADLScript;
import ADL.Actions.ADLAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

/**
 * Created by Pakapon on 3/3/2017 AD.
 */
public abstract class ADLFunction<T> {
    public String name;

    protected List<ADLParameter> parameters;

    ADLFunction(String name) {
        this.name = name;
        this.parameters = new ArrayList<>();
    }

    public void addParameter(ADLParameter parameter){
        this.parameters.add(parameter);
    }

    public abstract T performFunction();

    public ADLParameter getLatestParameter(){
        return this.parameters.get(this.parameters.size() - 1);
    }

    public int getTotalParameter(){
        return this.parameters.size();
    }

    protected static Double convertToDouble(Object value){
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        }
        else {
            return (Double) value;
        }
    }

    protected double getElapsedTime(){
        return ADLAgent.currentUpdatingAgent.getElapsedTime(ADLAction.getPerformingAction());
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
}
