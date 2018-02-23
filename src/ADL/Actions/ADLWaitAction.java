package ADL.Actions;

import ADL.ADLAgent;
import ADL.SpannableAction;

/**
 * Created by Pakapon on 3/21/2017 AD.
 */
public class ADLWaitAction extends ADLAction implements SpannableAction{

    ADLWaitAction(String name) {
        super(name);
    }

    @Override
    public void performAction(ADLAgent agent) {
        super.performAction(agent);

//        System.out.println("Wait: " + agent.getElapsedTime(this) * 1000 + "<=" + this.getMillisecond());
    }

    private int getMillisecond(){
        return (int) this.parameters.get(0).processRPN();
    }

    @Override
    public boolean isEnd(ADLAgent agent) {
        return agent.getElapsedTime(this) * 1000 >= this.getMillisecond();
    }

}
