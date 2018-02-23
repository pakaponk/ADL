package ADL;

import ADL.Actions.ADLAction;
import ADL.Actions.ADLSetAction;

import java.util.HashMap;
import java.util.Optional;

/**
 * Created by Pakapon on 3/3/2017 AD.
 */
public class ADLScript {

    private String agentName;

    private HashMap<String, ADLState> states;

    private HashMap<String, ADLScript> subAgentScripts;

    ADLScript(String agentName){
        this.agentName = agentName;
        this.states = new HashMap<>();
        this.subAgentScripts = new HashMap<>();
    }

    public String getAgentName() {
        return agentName;
    }

    public HashMap<String, ADLState> getStates() {
        return states;
    }

    public HashMap<String, ADLScript> getSubAgentScripts() {
        return subAgentScripts;
    }

    ADLInstantState getInitState(){
        return (ADLInstantState) this.states.get("init");
    }

    public double getAgentInitialPositionX() throws Exception {
        Optional<ADLAction> positionXInitializeAction = this.getInitState().sequences.get(0).actions.stream().filter((ADLAction action) -> {
            if (action instanceof ADLSetAction)
            {
                return ((ADLSetAction) action).getPropertyName().equals("x");
            }
            return false;
        }).findFirst();

        if (positionXInitializeAction.isPresent()) {
            return (double) ((ADLSetAction) positionXInitializeAction.get()).getPropertyValue();
        }
        else
            throw new Exception("Initialize Action for \'x\' is not found.");
    }

    public double getAgentInitialPositionY() throws Exception {
        Optional<ADLAction> positionYInitializeAction = this.getInitState().sequences.get(0).actions.stream().filter((ADLAction action) -> {
            if (action instanceof ADLSetAction)
            {
                return ((ADLSetAction) action).getPropertyName().equals("y");
            }
            return false;
        }).findFirst();

        if (positionYInitializeAction.isPresent())
            return (double) ((ADLSetAction) positionYInitializeAction.get()).getPropertyValue();
        else
            throw new Exception("Initialize Action for \'x\' is not found.");
    }
}
