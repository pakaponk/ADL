package ADL;

public class Utility {
    private static double EPSILON = 0.01;

    public static int compare(double d1, double d2){
        if (d1 - d2 > EPSILON)
        {
            return 1;
        }
        else if (d1 - d2 < -EPSILON)
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
}
