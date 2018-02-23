package ADL;

import Simulation.Entity;
import Simulation.GameManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pakapon on 3/14/2017 AD.
 */
public class ADLBaseAgent extends Entity implements Cloneable{

    public String name;

    public int life;
    public int attack;

    public String group;

    public boolean isProjectile;
    public boolean isAttacker;
    public boolean isDefender;

    public boolean isFlipper;
    public boolean isFlippable;

    public int horizontalDirection = 1;
    public int verticalDirection = 1;

    private double velocityX;
    private double velocityY;

    ADLBaseAgent(String name) {
        super();
        this.name = name;
    }

    public int getHorizontalDirection() {
        return horizontalDirection;
    }

    public int getVerticalDirection() {
        return verticalDirection;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public void setPosition(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void setSize(double width, double height){
        this.width = width;
        this.height = height;
    }

    public void setVelocity(double velocityX, double velocityY){
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public void toggleHorizontalDirection(){
        this.horizontalDirection *= -1;
    }

    public void toggleVerticalDirection(){
        this.verticalDirection *= -1;
    }

    private void flip(ADLBaseAgent baseAgent){
        if (this.isFlippable && baseAgent.isFlipper)
        {
            System.out.println("Flip: " + baseAgent.name);
            if (this.velocityX != 0)
                this.horizontalDirection *= -1;
            if (this.velocityY != 0)
                this.verticalDirection *= -1;
        }
    }

    public int compareToOnAxisX(ADLBaseAgent agent){
        if (this.x < agent.x)
        {
            return -1;
        }
        else if (this.x > agent.x)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    public int compareToOnAxisY(ADLBaseAgent agent){
        if (this.y < agent.y)
        {
            return -1;
        }
        else if (this.y > agent.y)
        {
            return  1;
        }
        else
        {
            return 0;
        }
    }

    public static List<ADLBaseAgent> agents = new ArrayList<>();

    public static ADLBaseAgent findAgent(String agentName){
        return agents.stream()
                    .filter((ADLBaseAgent agent) -> agent.name.equals(agentName))
                    .findFirst()
                    .get();
    }

    public Entity toEntity(){
        return new Entity(this.x, this.y, this.width, this.height);
    }

    public boolean isCollidedWith(ADLBaseAgent baseAgent){
        if (this instanceof ADLEnvironment && baseAgent instanceof ADLEnvironment)
            return false;

        boolean isCollided = !((this.x + (this.width / 2)) < (baseAgent.x - (baseAgent.width / 2)) ||
                (this.x - (this.width / 2)) > (baseAgent.x + (baseAgent.width / 2)) ||
                (this.y + (this.height / 2)) < (baseAgent.y - (baseAgent.height / 2)) ||
                (this.y - (this.height / 2)) > (baseAgent.y + (baseAgent.height / 2)));

        return isCollided;
    }

    public void handleOnCollidedWith(ADLBaseAgent baseAgent){
        //System.out.println(this.name + " collided with " + baseAgent.name);

        this.flip(baseAgent);
        baseAgent.flip(this);

        if (this instanceof ADLEnvironment)
        {
            if (baseAgent.isProjectile && !baseAgent.isFlippable) {
                GameManager.getInstance().getWillDestroyAgents().add(baseAgent);
            }
            else {
                baseAgent.moveAgentToBound(this);
            }
        }
        else if (baseAgent instanceof ADLEnvironment)
        {
            if (this.isProjectile && !this.isFlippable){
                GameManager.getInstance().getWillDestroyAgents().add(this);
            }
            else {
                this.moveAgentToBound(baseAgent);
            }
        }
    }

    protected void moveAgentToBound(ADLBaseAgent baseAgent){
        boolean isNotCollidedLeft = (this.x + (this.width / 2)) < (baseAgent.x - (baseAgent.width / 2));
        boolean isNotCollidedRight = (this.x - (this.width / 2)) > (baseAgent.x + (baseAgent.width / 2));
        boolean isNotCollidedBottom = (this.y + (this.height / 2)) < (baseAgent.y - (baseAgent.height / 2));
        boolean isNotCollidedTop = (this.y - (this.height / 2)) > (baseAgent.y + (baseAgent.height / 2));


        final double UNITY_BOUND_THRESHOLD = 0.015;

        if (baseAgent instanceof ADLEnvironment){
            if (((ADLEnvironment) baseAgent).isHorizontalAlignment()){
                if (!isNotCollidedTop)
                {
                    this.setPosition (this.x, (baseAgent.y + baseAgent.height / 2) + (this.height / 2) + UNITY_BOUND_THRESHOLD);
                    //System.out.println("Collided Top: " + this.name + " - X: " + this.x + " ,Y: " + this.y);
                }
                else if (!isNotCollidedBottom)
                {
                    this.setPosition(this.x, (baseAgent.y - baseAgent.height / 2) - (this.height / 2) - UNITY_BOUND_THRESHOLD);
                    //System.out.println("Collided Bottom: " + this.name + " - X: " + this.x + " ,Y: " + this.y);
                }
            }
            else
            {
                if (!isNotCollidedLeft)
                {
                    this.setPosition((baseAgent.x - baseAgent.width / 2) - (this.width / 2) - UNITY_BOUND_THRESHOLD, this.y);
                    //System.out.println("Collided Left: " + this.name + " - X: " + this.x + " ,Y: " + this.y);
                }
                else if (!isNotCollidedRight)
                {
                    this.setPosition((baseAgent.x + baseAgent.width / 2) + (this.width / 2) + UNITY_BOUND_THRESHOLD, this.y);
                    //System.out.println("Collided Right: " + this.name + " - X: " + this.x + " ,Y: " + this.y);
                }
            }
        }
        else
        {
            if (!isNotCollidedLeft)
            {
                this.setPosition((baseAgent.x - baseAgent.width / 2) - (this.width / 2) - UNITY_BOUND_THRESHOLD, this.y);
                System.out.println("Collided Left: " + this.name + " - X: " + this.x + " ,Y: " + this.y);
            }
            else if (!isNotCollidedRight)
            {
                this.setPosition((baseAgent.x + baseAgent.width / 2) + (this.width / 2) + UNITY_BOUND_THRESHOLD, this.y);
                System.out.println("Collided Right: " + this.name + " - X: " + this.x + " ,Y: " + this.y);
            }
            else if (!isNotCollidedBottom)
            {
                this.setPosition(this.x, (baseAgent.y - baseAgent.height / 2) - (this.height / 2) - UNITY_BOUND_THRESHOLD);
                System.out.println("Collided Bottom: " + this.name + " - X: " + this.x + " ,Y: " + this.y);
            }
            else if (!isNotCollidedTop)
            {
                this.setPosition (this.x, (baseAgent.y + baseAgent.height / 2) + (this.height / 2) + UNITY_BOUND_THRESHOLD);
                System.out.println("Collided Top: " + this.name + " - X: " + this.x + " ,Y: " + this.y);
            }
        }
    }

    public double getDistance(ADLBaseAgent baseAgent) {
        return Math.min(Math.abs(baseAgent.getRightMostX() - this.getLeftMostX()), Math.abs(this.getRightMostX() - baseAgent.getLeftMostX()));
    }

    public boolean isMovingAwayFrom(ADLBaseAgent baseAgent) {
        return (baseAgent.getVelocityX() < 0 && baseAgent.getRightMostX() < this.getLeftMostX()) ||
                (baseAgent.getVelocityX() >= 0 && baseAgent.getLeftMostX() > this.getRightMostX());
    }

    public boolean isEnemy() {
        return this.group.equals("Enemy");
    }

    @Override
    public ADLBaseAgent clone() throws CloneNotSupportedException {
        return (ADLBaseAgent) super.clone();
    }

    @Override
    public String toString() {
        ArrayList<String> info = new ArrayList<>();
        info.add("Name: " + name);
        info.add("Group" + group);
        info.add("X: " + x);
        info.add("Y: " + y);
        info.add("Vx: " + velocityX);
        info.add("Vy: " + velocityY);
        return String.join("\n", info);
    }
}
