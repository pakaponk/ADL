package ADL;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Created by Pakapon on 5/11/2017 AD.
 */
public class ADLAgentFactory {
    private static ADLAgentFactory ourInstance = new ADLAgentFactory();

    public static ADLAgentFactory getInstance() {
        return ourInstance;
    }

    private ADLAgentFactory() {
    }

    private String getScriptContent(String pathToScript) throws IOException {
        Optional<String> scriptText = Files.readAllLines(Paths.get("src/IceMan.txt")).stream().reduce((prev, curr) -> prev + curr);
        return scriptText.get();
    }

    public ADLAgent createADLAgent(String pathToScript) throws Exception {
        String scriptContent = getScriptContent(pathToScript);

        Optional<ADLScript> agentScript = Optional.ofNullable(ADLScriptFactory.getInstance().createADLScript(scriptContent));

        if (agentScript.isPresent()){
            return this.createADLAgent(agentScript.get());
        }
        else
        {
            return null;
        }
    }

    public ADLAgent createADLAgent(ADLScript script){
        return new ADLAgent(script);
    }
}
