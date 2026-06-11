package com.mertyilmaz.lab3;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Part 3: calculator with expression logic. The whole expression is built
 * first and evaluated only when "=" is pressed, WITH operator precedence
 * (shunting-yard -> RPN -> stack evaluation in ExpressionEvaluator):
 * 2 + 2 * 2 = 6.
 *
 * There is no limit on the number of operands/operators. Two operators in a
 * row replace each other; a trailing operator is dropped on "=".
 */
public class ExpressionActivity extends AppCompatActivity {

    private static final int MAX_INPUT_DIGITS = 12;

    private TextView textDisplay;

    private final List<String> tokens = new ArrayList<>(); // numbers and operators
    private final StringBuilder current = new StringBuilder();
    private double lastResult = 0;
    private boolean justEvaluated = false;
    private boolean errorState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expression);

        textDisplay = findViewById(R.id.textDisplay);

        int[] digitIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};
        for (int i = 0; i < digitIds.length; i++) {
            final String digit = String.valueOf(i);
            findViewById(digitIds[i]).setOnClickListener(v -> onDigit(digit));
        }

        findViewById(R.id.btnPlus).setOnClickListener(v -> onOperator("+"));
        findViewById(R.id.btnMinus).setOnClickListener(v -> onOperator("-"));
        findViewById(R.id.btnMul).setOnClickListener(v -> onOperator("*"));
        findViewById(R.id.btnDiv).setOnClickListener(v -> onOperator("/"));
        findViewById(R.id.btnPow).setOnClickListener(v -> onOperator("^")); // "num" button
        findViewById(R.id.btnEq).setOnClickListener(v -> onEquals());
        findViewById(R.id.btnClear).setOnClickListener(v -> onClear());

        render();
    }

    private void onDigit(String digit) {
        if (errorState) {
            return;
        }
        if (justEvaluated) {
            tokens.clear();
            current.setLength(0);
            justEvaluated = false;
        }
        if (current.length() < MAX_INPUT_DIGITS) {
            current.append(digit);
        }
        render();
    }

    private void onOperator(String op) {
        if (errorState) {
            return;
        }
        if (justEvaluated) {
            // Continue building a new expression from the previous result.
            tokens.clear();
            tokens.add(CalcUtils.format(lastResult));
            tokens.add(op);
            current.setLength(0);
            justEvaluated = false;
        } else if (current.length() > 0) {
            tokens.add(current.toString());
            current.setLength(0);
            tokens.add(op);
        } else if (!tokens.isEmpty()
                && ExpressionEvaluator.isOperator(tokens.get(tokens.size() - 1))) {
            tokens.set(tokens.size() - 1, op); // replace operator
        } else if (tokens.isEmpty()) {
            tokens.add("0"); // operator first: start from 0 (same rule as Part 1)
            tokens.add(op);
        } else {
            tokens.add(op);
        }
        render();
    }

    private void onEquals() {
        if (errorState) {
            return;
        }
        List<String> expression = new ArrayList<>(tokens);
        if (current.length() > 0) {
            expression.add(current.toString());
        }
        if (!expression.isEmpty()
                && ExpressionEvaluator.isOperator(expression.get(expression.size() - 1))) {
            expression.remove(expression.size() - 1); // drop trailing operator
        }
        if (expression.isEmpty()) {
            return;
        }

        double result;
        try {
            result = ExpressionEvaluator.evaluate(expression);
        } catch (ArithmeticException e) {
            showError();
            return;
        }

        HistoryStore.add(String.join(" ", expression) + " = " + CalcUtils.format(result));
        lastResult = result;
        tokens.clear();
        current.setLength(0);
        justEvaluated = true;
        textDisplay.setText(CalcUtils.format(result));
    }

    private void onClear() {
        tokens.clear();
        current.setLength(0);
        justEvaluated = false;
        errorState = false;
        render();
    }

    private void showError() {
        tokens.clear();
        current.setLength(0);
        errorState = true;
        textDisplay.setText(getString(R.string.display_error));
    }

    /** Shows the expression as typed, e.g. "2 + 2 * 2". */
    private void render() {
        StringBuilder text = new StringBuilder();
        for (String token : tokens) {
            text.append(token).append(' ');
        }
        text.append(current);
        String s = text.toString().trim();
        textDisplay.setText(s.isEmpty() ? "0" : s);
    }
}
