package ADL;

import ADL.Actions.ADLAction;
import com.google.gson.annotations.Expose;

import java.util.HashMap;

/**
 * Created by Pakapon on 3/14/2017 AD.
 */
public class ADLAgent extends ADLBaseAgent{

    public static final double FIXED_UPDATE_TIME = 0.02;

    public static ADLAgent currentUpdatingAgent;

    @Expose(serialize = false, deserialize = false)
    private ADLScript script;

    @Expose(serialize = false, deserialize = false)
    private SimulationState simulationState;

    public ADLAgent(ADLScript script){
        super(script.getAgentName());

        this.script = script;
    }

    public ADLScript getScript() {
        return script;
    }

    public SimulationState getSimulationState() {
        return simulationState;
    }

    public void start(){
        this.simulationState = new SimulationState(this.script.getStates().get("state1"));
        this.script.getInitState().performActions(this);
    }

    public void update(){
        currentUpdatingAgent = this;

        this.setVelocity(0, 0);

        this.simulationState.currentState.sequences.forEach(seq -> {
            Integer index = this.simulationState.sequenceIndexes.get(seq.name);

            if (!seq.actions.isEmpty()) {
                ADLAction action = seq.actions.get(index);
                this.simulationState.elapsedTimes.putIfAbsent(action, 0.0);
                action.performAction(this);

                if (!(action instanceof SpannableAction) ||
                        ((SpannableAction) action).isEnd(this)) {
                    this.simulationState.elapsedTimes.remove(action);

                    int nextActionIndex = (index + 1) % seq.actions.size();
                    this.simulationState.sequenceIndexes.put(seq.name, nextActionIndex);
                }
            }
        });

        this.move();
    }

    private void move(){
        this.setPosition(this.x + this.getVelocityX() * FIXED_UPDATE_TIME, this.y + this.getVelocityY() * FIXED_UPDATE_TIME);
    }

    public void increaseElapsedTime(ADLAction action) {
        if (this.simulationState.elapsedTimes.containsKey(action)) {
            Double currentElapsedTime = this.simulationState.elapsedTimes.get(action);
            this.simulationState.elapsedTimes.put(action, currentElapsedTime + ADLAgent.FIXED_UPDATE_TIME);
        }
    }

    public Double getElapsedTime(ADLAction action) {
        return this.simulationState.elapsedTimes.get(action);
    }

    public class SimulationState implements Cloneable{
        private ADLState currentState;
        private HashMap<String, Integer> sequenceIndexes;
        private HashMap<ADLAction, Double> elapsedTimes;

        private SimulationState(ADLState state) {
            this.setCurrentState(state);
        }

        void setCurrentState(ADLState currentState) {
            this.currentState = currentState;
            this.sequenceIndexes = new HashMap<>();
            this.elapsedTimes = new HashMap<>();
            this.currentState.getSequences().forEach((ADLSequence seq) -> this.sequenceIndexes.put(seq.name, 0));
        }

        @Override
        public SimulationState clone() throws CloneNotSupportedException {
            SimulationState clone = (SimulationState) super.clone();
            clone.sequenceIndexes = (HashMap<String, Integer>) this.sequenceIndexes.clone();
            clone.elapsedTimes = (HashMap<ADLAction, Double>) this.elapsedTimes.clone();
            return clone;
        }
    }

    @Override
    public ADLAgent clone() throws CloneNotSupportedException {
        ADLAgent agent = (ADLAgent) super.clone();
        agent.simulationState = this.simulationState.clone();
        return agent;
    }
}
