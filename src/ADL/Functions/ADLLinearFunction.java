package ADL.Functions;

/**
 * Created by Pakapon on 3/3/2017 AD.
 */
public class ADLLinearFunction extends ADLFunction<Double>{

    public ADLLinearFunction(String name) {
        super(name);
    }

    private double getSlope(){
        return (double) this.parameters.get(0).processRPN();
    }

    private double getConstant(){
        return (double) this.parameters.get(1).processRPN();
    }

    @Override
    public Double performFunction() {
        return this.performFunction(this.getElapsedTime());
    }

    private Double performFunction(double elapsedTime){
        return (this.getSlope() * elapsedTime) + this.getConstant();
    }

    @Override
    public String toString() {
        Double slope = this.getSlope();
        double constant = this.getConstant();
        return "Linear Function: " + (Double.compare(slope, 0.0) == 0 ? slope.toString() + "x + " : "") + constant;
    }
}
