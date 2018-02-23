package ADL.Functions;

import ADL.ADLAgent;
import ADL.ADLBaseAgent;
import ADL.Utility;

/**
 * Created by Pakapon on 4/20/2017 AD.
 */
public class ADLIsXFunction extends ADLFunction<Boolean> {

    public ADLIsXFunction(String name) {
        super(name);
    }

    private Double getX(){
        return convertToDouble(this.parameters.get(0).processRPN());
    }

    private String getAgentName(){
        return this.parameters.get(1).processRPN().toString();
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

        double conditionX = this.getX();
        double nextX = agent.getX() + (agent.getVelocityX() * ADLAgent.FIXED_UPDATE_TIME);

        if ((Utility.compare(conditionX, agent.getX()) == 1 && Utility.compare(conditionX, nextX) <= 0) ||
                (Utility.compare(conditionX, agent.getX()) == -1 && Utility.compare(conditionX, nextX) >= 0))
        {
            agent.setVelocity((conditionX - agent.getX()) * 50, agent.getVelocityY());
            return true;
        }
        else
        {
            return Utility.compare(conditionX, agent.getX()) == 0 && Utility.compare(conditionX, nextX) == 0;
        }
    }
}
