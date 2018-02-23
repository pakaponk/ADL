package ADL.Functions;

import ADL.ADLAgent;
import ADL.ADLBaseAgent;
import ADL.Utility;

/**
 * Created by Pakapon on 5/4/2017 AD.
 */
public class ADLIsYFunction extends ADLFunction<Boolean>{

    public ADLIsYFunction(String name) {
        super(name);
    }

    private Double getY(){
        return convertToDouble(this.parameters.get(0).processRPN());
    }

    private String getAgentName(){
        return (String) this.parameters.get(1).processRPN();
    }

    private ADLBaseAgent getBaseAgent(){
        String agentName = this.getAgentName();

        if (agentName.equals("Self")){
            return ADLAgent.currentUpdatingAgent;
        }
        else{
            return ADLBaseAgent.findAgent(agentName);
        }
    }

    @Override
    public Boolean performFunction() {
        ADLBaseAgent agent = this.getBaseAgent();

        double conditionY = this.getY();
        double nextY = agent.getY() + (agent.getVelocityY() * ADLAgent.FIXED_UPDATE_TIME);

        if ((Utility.compare(conditionY, agent.getY()) == 1 && Utility.compare(conditionY, nextY) <= 0) ||
                (Utility.compare(conditionY, agent.getY()) == -1 && Utility.compare(conditionY, nextY) >= 0))
        {
            agent.setVelocity(agent.getVelocityX(), (conditionY - agent.getY()) * 50);
            return true;
        }
        else
        {
            return Utility.compare(conditionY, agent.getY()) == 0 && Utility.compare(conditionY, nextY) == 0;
        }
    }
}
