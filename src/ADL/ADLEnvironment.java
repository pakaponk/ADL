package ADL;

/**
 * Created by Pakapon on 4/20/2017 AD.
 */
public class ADLEnvironment extends ADLBaseAgent{

    private boolean isHorizontalAlignment;

    public ADLEnvironment(String name, double x, double y, double width, double height, boolean isHorizontalAlignment) {
        super(name);
        this.group = "Environment";
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isFlipper = true;
        this.isHorizontalAlignment = isHorizontalAlignment;
    }

    public ADLEnvironment(String name, double x, double y, double width, double height, boolean isFlipper, boolean isHorizontalAlignment){
        super(name);
        this.group = "Environment";
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isFlipper = isFlipper;
        this.isHorizontalAlignment = isHorizontalAlignment;
    }

    public boolean isHorizontalAlignment() {
        return isHorizontalAlignment;
    }

    @Override
    public ADLEnvironment clone() throws CloneNotSupportedException {
        return (ADLEnvironment) super.clone();
    }
}
