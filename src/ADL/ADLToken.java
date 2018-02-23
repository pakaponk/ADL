package ADL;

import ADL.Functions.ADLFunction;

import java.util.Arrays;

/**
 * Created by Pakapon on 3/3/2017 AD.
 */
public class ADLToken<T> {

    private static final String[] OPERATOR_LIST = {"+", "-", "*", "/"};

    private T token;

    public T getTokenValue() {
        return token;
    }

    public void setToken(T token) {
        this.token = token;
    }

    public ADLToken(T token) {
        this.token = token;
    }

    public Double toDouble(){
        if (token instanceof Double)
        {
            return (Double) token;
        }
        else if (token instanceof Integer)
        {
            return ((Integer) token).doubleValue();
        }
        else if (token instanceof Boolean)
        {
            return (Boolean) token ? 1.0 : 0.0;
        }
        else if (token instanceof ADLFunction)
        {
            return (Double) ((ADLFunction) token).performFunction();
        }
        else
        {
            return Double.parseDouble(token.toString());
        }
    }

    ADLFunction toADLFunction(){
        return ((ADLFunction) token);
    }

    boolean isOperator(){
        return token instanceof String &&
                Arrays.stream(OPERATOR_LIST).anyMatch(operator -> operator.equals(token));
    }

    @Override
    public String toString() {
        return this.token.toString();
    }
}
