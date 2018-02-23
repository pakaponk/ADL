package ADL.Functions;

/**
 * Created by Pakapon on 6/1/2017 AD.
 */
public class ADLFunctionFactory {
    private static ADLFunctionFactory ourInstance = new ADLFunctionFactory();

    public static ADLFunctionFactory getInstance() {
        return ourInstance;
    }

    private ADLFunctionFactory() {
    }

    public ADLFunction createFunction(String name) throws Exception {
        switch(name){
            case "IsX":
                return new ADLIsXFunction(name);
            case "IsY":
                return new ADLIsYFunction(name);
            case "Linear":
                return new ADLLinearFunction(name);
            default:
                throw new Exception("Unable to create an unknown ADLFunction");
        }
    }
}
