package ADL.Actions;

import ADL.ADLAgent;
import ADL.ADLPlayer;
import ADL.ADLScript;
import ADL.ADLToken;
import Simulation.GameManager;

/**
 * Created by Pakapon on 3/21/2017 AD.
 */
public class ADLSpawnAction extends ADLAction{

    ADLSpawnAction(String name) {
        super(name);
    }

    @Override
    public void performAction(ADLAgent agent) {
        super.performAction(agent);

        String agentName = this.getAgentName();
        double subAgentX = this.getPositionX();
        double subAgentY = this.getPositionY();
        double subAgentWidth = this.getWidth();
        double subAgentHeight = this.getHeight();
        String spawnDirectionString = this.getSpawnDirectionString();
        int spawnDirection;

        switch (spawnDirectionString){
            case "TowardPlayer":
                ADLPlayer player = GameManager.getInstance().getPlayer();
                if (player.getX() + (player.getWidth() / 2) <= agent.getX()){
                    spawnDirection = -1;
                }
                else{
                    spawnDirection = 1;
                }
                break;
            case "TowardLeft":
                spawnDirection = -1;
                break;
            case "TowardRight":
                spawnDirection = 1;
                break;
            default:
                spawnDirection = agent.horizontalDirection;
                break;
        }

        ADLScript agentScript = agent.getScript().getSubAgentScripts().get(agentName);
        ADLAgent subAgent = new ADLAgent(agentScript);

        subAgent.setPosition(agent.getX() + subAgentX * spawnDirection, agent.getY() + subAgentY);
        subAgent.setSize(subAgentWidth, subAgentHeight);
        subAgent.horizontalDirection = spawnDirection;

        GameManager.getInstance().getWillSpawnAgents().add(subAgent);
    }

    private String getAgentName() { return this.parameters.get(0).processRPN().toString(); }

    private double getPositionX(){
        return new ADLToken(this.parameters.get(1).processRPN()).toDouble();
    }

    private double getPositionY(){
        return new ADLToken(this.parameters.get(2).processRPN()).toDouble();
    }

    private double getWidth(){
        return (double) this.parameters.get(3).processRPN();
    }

    private double getHeight(){
        return (double) this.parameters.get(4).processRPN();
    }

    private String getSpawnDirectionString(){
        try{
            return this.parameters.get(5).processRPN().toString();
        } catch (ArrayIndexOutOfBoundsException e){
            return "TowardSelf";
        }
    }
}
