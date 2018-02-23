package ADL;

import java.util.ArrayList;

/**
 * Created by Pakapon on 4/19/2017 AD.
 */
public class Timeline{
    public ArrayList<TimeNode> timeNodes;

    public Timeline(){
        this.timeNodes = new ArrayList<>();
    }
}


class TimeNode{
    double startTime;
    double endTime;
    ADLParameter velocityXEquation;
    ADLParameter velocityYEquation;
    double startPositionX;
    double startPositionY;
}