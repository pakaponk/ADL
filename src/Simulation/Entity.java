package Simulation;

/**
 * Created by Pakapon on 5/11/2017 AD.
 */
public class Entity {
    protected double x;
    protected double y;
    protected double width;
    protected double height;

    public Entity(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Entity() {

    }

    public double getX(){
        return this.x;
    }
    public double getY(){
        return this.y;
    }
    public double getWidth(){
        return this.width;
    }
    public double getHeight(){
        return this.height;
    }

    public double getLeftMostX(){
        return this.x - this.width / 2;
    }
    public double getRightMostX() { return this.x + this.width / 2; }
    public double getTopMostY(){
        return this.y + this.height / 2;
    }
    public double getBottomMostY() { return this.y - this.height / 2;}
}
