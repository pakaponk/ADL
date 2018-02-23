package ADL.Actions;

import ADL.*;

/**
 * Created by Pakapon on 3/21/2017 AD.
 */
public class ADLMoveAction extends ADLAction implements SpannableAction {

    private boolean isEnd = false;

    ADLMoveAction(String name) {
        super(name);
    }

    @Override
    public void performAction(ADLAgent agent){
        super.performAction(agent);
        agent.setVelocity(this.getVelocityX() * agent.getHorizontalDirection(),
                this.getVelocityY() * agent.getVerticalDirection());
        this.setIsEnd();
    }

    private double getVelocityX(){
        return convertToDouble(this.parameters.get(0).processRPN());
    }

    private double getVelocityY(){
        return convertToDouble(this.parameters.get(1).processRPN());
    }

    private void setIsEnd(){
        this.isEnd = (boolean) this.parameters.get(2).processRPN();
    }

    @Override
    public boolean isEnd(ADLAgent agent) {
        return this.isEnd;
    }

    public String getXEquationString(){
        return "X Equation: x + " + this.parameters.get(0).toString();
    }

    public String getYEquationString(){
        return "Y Equation: y + " + this.parameters.get(1).toString();
    }

    public double calculateEndTime(ADLBaseAgent agent){
        Object token = this.parameters.get(2).processRPN();
        return 0;
    }
}
