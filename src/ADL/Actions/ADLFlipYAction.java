package ADL.Actions;

import ADL.ADLAgent;

/**
 * Created by Pakapon on 3/21/2017 AD.
 */
public class ADLFlipYAction extends ADLAction {

    ADLFlipYAction(String name) {
        super(name);
    }

    @Override
    public void performAction(ADLAgent agent) {
        super.performAction(agent);
        agent.toggleVerticalDirection();
    }
}
