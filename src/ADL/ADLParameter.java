package ADL;

import ADL.Functions.ADLFunction;

import java.util.*;

/**
 * Created by Pakapon on 3/3/2017 AD.
 */
public class ADLParameter {

    private Queue<ADLToken> tokens;

    private Queue<ADLToken> rpnQueue;

    public ADLParameter(){
        this.tokens = new ArrayDeque<>();
    }

    public Queue<ADLToken> getTokens() {
        return tokens;
    }

    public void addToken(ADLToken token){
        this.tokens.add(token);
    }

    public void buildRPN(){
        this.rpnQueue = new ArrayDeque<>();
        Deque<ADLToken<String>> operatorStack = new ArrayDeque<>();

        while(!tokens.isEmpty())
        {
            ADLToken token = tokens.poll();

            if (token.isOperator())
            {
                if (!operatorStack.isEmpty()
                        && this.getOperatorPrecedence(operatorStack.peek().getTokenValue()) > this.getOperatorPrecedence((String) token.getTokenValue())){
                    this.rpnQueue.offer(operatorStack.pop());
                }
                operatorStack.push(token);
            }
            else
            {
                this.rpnQueue.offer(token);
            }
        }

        while(!operatorStack.isEmpty())
        {
            this.rpnQueue.offer(operatorStack.pop());
        }
    }

    public Object processRPN(){
        if (this.rpnQueue == null)
        {
            this.buildRPN();
        }

        Deque<ADLToken> resultStack = new ArrayDeque<>();
        Queue<ADLToken> processingQueue = new ArrayDeque<>(this.rpnQueue);

        while(processingQueue.size() != 0) {
            //If Front of Queue is Operand
            //Push Operand into Stack
            if (processingQueue.peek().isOperator()) {
                ADLToken operand2 = resultStack.pop();
                ADLToken operand1 = resultStack.pop();
                resultStack.push(new ADLToken<>(this.operate((String) processingQueue.poll().getTokenValue(), operand1.toDouble(), operand2.toDouble())));
            } else {
                resultStack.push(processingQueue.poll());
            }
        }

        Object tokenValue = resultStack.peek().getTokenValue();
        if (tokenValue instanceof Boolean)
        {
            return resultStack.pop().getTokenValue();
        }
        else if (tokenValue instanceof String)
        {
            return resultStack.pop().toString();
        }
        else if (tokenValue instanceof ADLFunction)
        {
            return resultStack.pop().toADLFunction().performFunction();
        }
        else if (tokenValue instanceof Integer)
        {
            return resultStack.pop().getTokenValue();
        }
        else
        {
            return resultStack.pop().toDouble();
        }
    }

    private Integer getOperatorPrecedence(String operator){
        switch (operator){
            case "||":
                return -6;
            case "&&":
                return -5;
            case "==":
            case "!=":
                return -1;
            case "<":
            case ">":
            case "<=":
            case ">=":
                return 0;
            case "<<":
            case ">>":
                return 1;
            case "+":
            case "-":
                return 2;
            case "*":
            case "/":
            case "%":
                return 3;
            default:
                return -9999;
        }
    }

    private Double operate(String operator, Double operand1, Double operand2) {
        switch (operator) {
            case "+":
                return operand1 + operand2;
            case "-":
                return operand1 - operand2;
            case "*":
                return operand1 * operand2;
            case "/":
                return operand1 / operand2;
            default:
                return operand1 + operand2;
        }
    }

    @Override
    public String toString() {
        return tokens.stream().map(ADLToken::toString).reduce((String a, String b) -> a + " " + b).orElse("");
    }
}
