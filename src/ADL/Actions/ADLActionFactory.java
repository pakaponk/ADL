package ADL.Actions;

/**
 * Created by Pakapon on 3/21/2017 AD.
 */
public class ADLActionFactory {
    private static ADLActionFactory ourInstance = new ADLActionFactory();

    public static ADLActionFactory getInstance() {
        return ourInstance;
    }

    private ADLActionFactory() {
    }

    public ADLAction createAction(String name) throws Exception {
        switch(name){
            case "Move":
                return new ADLMoveAction(name);
            case "Spawn":
                return new ADLSpawnAction(name);
            case "Wait":
                return new ADLWaitAction(name);
            case "Set":
                return new ADLSetAction(name);
            case "FlipX":
                return new ADLFlipXAction(name);
            case "FlipY":
                return new ADLFlipYAction(name);
            default:
                throw new Exception("Unable to create Unknown Action: " + name);
        }
    }
}
