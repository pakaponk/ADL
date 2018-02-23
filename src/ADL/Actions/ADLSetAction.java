package ADL.Actions;

import ADL.ADLAgent;

/**
 * Created by Pakapon on 3/21/2017 AD.
 */
public class ADLSetAction extends ADLAction {

    ADLSetAction(String name) {
        super(name);
    }

    public String getPropertyName(){
        return (String) this.parameters.get(0).processRPN();
    }

    public Object getPropertyValue(){
        return this.parameters.get(1).processRPN();
    }

    @Override
    public void performAction(ADLAgent agent) {
        super.performAction(agent);
        switch(this.getPropertyName()){
            case "lifePoint":
                agent.life = (int) this.getPropertyValue();
                break;
            case "attack":
                agent.attack = (int) this.getPropertyValue();
                break;
            case "group":
                agent.group = this.getPropertyValue().toString();
                break;
            case "isProjectile":
                agent.isProjectile = (boolean) this.getPropertyValue();
                break;
            case "isAttacker":
                agent.isAttacker = (boolean) this.getPropertyValue();
                break;
            case "isDefender":
                agent.isDefender = (boolean) this.getPropertyValue();
                break;
            case "isFlipper":
                agent.isFlipper = (boolean) this.getPropertyValue();
                break;
            case "isFlippable":
                agent.isFlippable = (boolean) this.getPropertyValue();
                break;
            case "horizontalDirection":
                agent.horizontalDirection = (int) this.getPropertyValue();
                break;
            case "verticalDirection":
                agent.verticalDirection = (int) this.getPropertyValue();
                break;
            case "x":
                agent.setPosition((double) this.getPropertyValue(), agent.getY());
                break;
            case "y":
                agent.setPosition(agent.getX() , (double) this.getPropertyValue());
                break;
            case "width":
                agent.setSize((double) this.getPropertyValue(), agent.getHeight());
                break;
            case "height":
                agent.setSize(agent.getWidth(), (double) this.getPropertyValue());
                break;
            default:
                break;

        }
    }
}
