package ADL;

import ADL.Actions.ADLAction;
import ADL.Actions.ADLActionFactory;
import ADL.Functions.ADLFunction;
import ADL.Functions.ADLFunctionFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Pakapon on 3/3/2017 AD.
 */
public class ADLScriptFactory {
    private static ADLScriptFactory ourInstance = new ADLScriptFactory();

    public static ADLScriptFactory getInstance() {
        return ourInstance;
    }

    private static final int STATE_NAME = 0;
    private static final int STATE_STATE = 1;
    private static final int STATE_SEQUENCE = 2;
    private static final int STATE_ACTION = 3;
    private static final int STATE_PARAMETER = 4;

    private static final Pattern REGEX_NAME_PATTERN = Pattern.compile("\\.(?<Name>[A-Z][\\w-]*)\\{");
    private static final Pattern REGEX_STATE_PATTERN = Pattern.compile("\\.(?<State>[\\w]+)\\{|(?<EndName>})");
    private static final Pattern REGEX_SEQUENCE_PATTERN = Pattern.compile("\\.(?<Sequence>[\\w]+)\\{|(?<EndState>})");
    private static final Pattern REGEX_ACTION_PATTERN = Pattern.compile("(?<Action>[A-Z][\\w]*)\\(|(?<EndSequence>})");
    private static final Pattern REGEX_PARAMETER_PATTERN = Pattern.compile("'(?<String>[^']*)'|\"(?<String2>[^\"]*)\"|(?<Double>-?[\\d]+\\.[\\d]+)|(?<PI>PI)|(?<Int>-?[\\d]+)|(?<Comma>,)|(?<EndAction>\\);)|(?<Function>[A-Z][\\w]*)\\(|(?<EndIfBracket>\\)\\{)|(?<EndBracket>\\))|(?<OrOperator>\\|\\|)|(?<AndOperator>&&)|(?<PlusOperator>\\+)|(?<MinusOpaerator>-)|(?<MultiplyOperator>\\*)|(?<DivideOperator>/)|(?<ModOperator>%)|(?<StartBracket>\\()|(?<True>true)|(?<False>false)|(?<MoreThanOrEqual>>=)|(?<LessThanOrEqual><=)|(?<MoreThan>>)|(?<LessThan><)|(?<Equal>==)|(?<NotEqual>!=)");

    private int currentState = 0;
    private Matcher matcher;

    private boolean isDebug = false;

    private ADLScript primaryAgentScript;

    private ADLScript compilingScript;
    private ADLState compilingState;
    private ADLSequence compilingSequence;
    private ADLAction compilingAction;
    private ADLFunction compilingFunction;
    private ADLParameter compilingParameter;

    private Deque<ADLFunction> functionStack;


    private ADLScriptFactory() {
    }

    private void initializeFactory(String script){
        primaryAgentScript = null;
        compilingScript = null;
        compilingState = null;
        compilingSequence = null;
        compilingAction = null;
        currentState = STATE_NAME;
        matcher = REGEX_NAME_PATTERN.matcher(script);

        functionStack = new ArrayDeque<>();
    }

    public ADLScript createADLScript(String script) throws Exception {
        this.initializeFactory(script);

        while(matcher.find()){
            switch(currentState){
                case STATE_NAME:
                    parseStateName();
                    break;
                case STATE_STATE:
                    parseStateState();
                    break;
                case STATE_SEQUENCE:
                    parseStateSequence();
                    break;
                case STATE_ACTION:
                    parseStateAction();
                    break;
                case STATE_PARAMETER:
                    parseStateParameter();
                    break;
                default:
                    throw new Exception("Unknown compiling state:" + currentState);
            }
        }

        return primaryAgentScript;
    }

    private void parseStateName(){
        if (isGroupPresented("Name")){
            String agentName = matcher.group("Name");
            currentState = STATE_STATE;
            matcher.usePattern(REGEX_STATE_PATTERN);

            ADLScript script = new ADLScript(agentName);
            compilingScript = script;

            if (primaryAgentScript == null)
                primaryAgentScript = script;
            else
                primaryAgentScript.getSubAgentScripts().put(agentName, script);

            log("Enemy name: " + agentName);
        }
    }

    private void parseStateState(){
        if (isGroupPresented("State")){
            String stateName = matcher.group("State");

            ADLState state = stateName.equals("init") || stateName.equals("des") ?
                    new ADLInstantState(stateName) : new ADLState(stateName);

            compilingState = state;
            compilingScript.getStates().put(stateName, state);

            if (stateName.equals("init") || stateName.equals("des"))
            {
                ADLSequence sequence = new ADLSequence("seq");
                compilingSequence = sequence;
                state.sequences.add(sequence);

                currentState = STATE_ACTION;
                matcher.usePattern(REGEX_ACTION_PATTERN);

                log("\tSpecial State: " + stateName);
            }
            else
            {
                currentState = STATE_SEQUENCE;
                matcher.usePattern(REGEX_SEQUENCE_PATTERN);

                log("\tState: " + stateName);
            }
        }
        else if (isGroupPresented("EndName")){
            currentState = STATE_NAME;
            matcher.usePattern(REGEX_NAME_PATTERN);

            log("End");
        }
    }

    private void parseStateSequence(){
        if (isGroupPresented("Sequence")) {
            String sequenceName = matcher.group("Sequence");

            ADLSequence sequence = new ADLSequence(sequenceName);
            compilingSequence = sequence;
            compilingState.sequences.add(sequence);

            currentState = STATE_ACTION;
            matcher.usePattern(REGEX_ACTION_PATTERN);

            log("\t\tSequence: " + sequenceName);
        }
        else if (isGroupPresented("EndState")){
            currentState = STATE_STATE;
            matcher.usePattern(REGEX_STATE_PATTERN);

            log("\tEnd");
        }
    }

    private void parseStateAction() throws Exception {
        if (isGroupPresented("Action")){
            String actionName = matcher.group("Action");

            ADLAction action = ADLActionFactory.getInstance().createAction(actionName);
            ADLParameter parameter = new ADLParameter();
            compilingAction = action;
            compilingParameter = parameter;
            compilingSequence.actions.add(action);
            compilingAction.getParameters().add(parameter);

            currentState = STATE_PARAMETER;
            matcher.usePattern(REGEX_PARAMETER_PATTERN);

            log("\t\t\tAction: " + actionName);
            log("\t\t\t\tParameter #" + compilingAction.getParameters().size());
        }
        else if(isGroupPresented("EndSequence")){
            if (compilingState.name.equals("init") || compilingState.name.equals("desc"))
            {
                currentState = STATE_STATE;
                matcher.usePattern(REGEX_STATE_PATTERN);

                log("\tEnd");
            }
            else
            {
                currentState = STATE_SEQUENCE;
                matcher.usePattern(REGEX_SEQUENCE_PATTERN);

                log("\t\tEnd");
            }
        }
    }

    private void parseStateParameter(){
        if (isGroupPresented("EndAction"))
        {
            currentState = STATE_ACTION;
            matcher.usePattern(REGEX_ACTION_PATTERN);

            log("\t\t\tEnd");
        }
        else if(isGroupPresented("EndBracket"))
        {
            if (functionStack.isEmpty())
            {

            }
            else
            {
                functionStack.pop();
                if (!functionStack.isEmpty())
                {
                    compilingFunction = functionStack.peek();
                    compilingParameter = compilingFunction.getLatestParameter();
                }
                else
                {
                    compilingParameter = compilingAction.getParameters().get(compilingAction.getParameters().size() - 1);
                }
            }
        }
        else if(isGroupPresented("Function"))
        {
            String tabIndent = "\t\t\t\t\t";
            for(int i = 0;i < functionStack.size();i++)
            {
                tabIndent += "\t";
            }

            String functionName = matcher.group("Function");
            ADLFunction function = null;
            try {
                function = ADLFunctionFactory.getInstance().createFunction(functionName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            compilingParameter.addToken(new ADLToken<>(function));

            ADLParameter parameter = new ADLParameter();
            compilingFunction = function;
            compilingParameter = parameter;
            compilingFunction.addParameter(parameter);
            functionStack.push(function);

            log(tabIndent + "Token: " + functionName + "()");
        }
        else if (isGroupPresented("Comma"))
        {
            ADLParameter parameter = new ADLParameter();
            if (functionStack.isEmpty())
            {
                compilingAction.getParameters().add(parameter);
                log("\t\t\t\tParameter #" + compilingAction.getParameters().size());
            }
            else
            {
                compilingFunction.addParameter(parameter);
            }
            compilingParameter = parameter;
        }
        else
        {
            if (isGroupPresented("Double"))
            {
                compilingParameter.addToken(new ADLToken<>(Double.parseDouble(matcher.group("Double"))));
            }
            else if(isGroupPresented("Int"))
            {
                compilingParameter.addToken(new ADLToken<>(Integer.parseInt(matcher.group("Int"))));
            }
            else if(isGroupPresented("PI"))
            {
                compilingParameter.addToken(new ADLToken<>(Math.PI));
            }
            else if(isGroupPresented("String"))
            {
                compilingParameter.addToken(new ADLToken<>(matcher.group("String")));
            }
            else if(isGroupPresented("String2"))
            {
                compilingParameter.addToken(new ADLToken<>(matcher.group("String2")));
            }
            else if(isGroupPresented("True"))
            {
                compilingParameter.addToken(new ADLToken<>(true));
            }
            else if(isGroupPresented("False"))
            {
                compilingParameter.addToken(new ADLToken<>(false));
            }
            else
            {
                //Operator Token
                compilingParameter.addToken(new ADLToken<>(matcher.group()));
            }

            String tabIndent = "\t\t\t\t\t";
            for(int i = 0;i < functionStack.size();i++)
            {
                tabIndent += "\t";
            }
            log(tabIndent + "Token #" + compilingParameter.getTokens().size() + ": " + compilingParameter.getTokens().peek().toString());
        }
    }

    private void log(String message){
        if (isDebug)
            System.out.println(message);
    }

    private boolean isGroupPresented(String groupName){
        return matcher.group(groupName) != null;
    }
}
