package com.mertyilmaz.lab3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Part 3: expression evaluation with operator precedence.
 *
 * The infix token list (numbers and operators) is converted to Reverse Polish
 * Notation with the shunting-yard algorithm and then evaluated with a stack.
 * Precedence: ^ (highest, right-associative), then * and /, then + and -.
 * There is no limit on the number of operands/operators.
 */
public final class ExpressionEvaluator {

    private ExpressionEvaluator() {
    }

    public static double evaluate(List<String> infixTokens) {
        return evaluateRpn(toRpn(infixTokens));
    }

    /** Shunting-yard: infix tokens -> RPN tokens. */
    static List<String> toRpn(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Deque<String> operators = new ArrayDeque<>();

        for (String token : tokens) {
            if (isOperator(token)) {
                while (!operators.isEmpty()
                        && (precedence(operators.peek()) > precedence(token)
                            || (precedence(operators.peek()) == precedence(token)
                                && isLeftAssociative(token)))) {
                    output.add(operators.pop());
                }
                operators.push(token);
            } else {
                output.add(token); // number
            }
        }
        while (!operators.isEmpty()) {
            output.add(operators.pop());
        }
        return output;
    }

    /** Stack-based RPN evaluation. */
    static double evaluateRpn(List<String> rpn) {
        Deque<Double> stack = new ArrayDeque<>();
        for (String token : rpn) {
            if (isOperator(token)) {
                double b = stack.pop();
                double a = stack.pop();
                stack.push(CalcUtils.apply(a, b, token));
            } else {
                stack.push(Double.parseDouble(token));
            }
        }
        return stack.pop();
    }

    static boolean isOperator(String token) {
        return token.length() == 1 && "+-*/^".contains(token);
    }

    private static int precedence(String op) {
        switch (op) {
            case "^": return 3;
            case "*":
            case "/": return 2;
            default:  return 1; // + and -
        }
    }

    private static boolean isLeftAssociative(String op) {
        return !"^".equals(op); // only the power operator is right-associative
    }
}
