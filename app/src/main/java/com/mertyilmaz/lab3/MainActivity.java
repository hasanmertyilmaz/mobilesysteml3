package com.mertyilmaz.lab3;

/*
 * Name: Hasan Yilmaz
 * Student ID: 56505
 * Lab: 3
 * Course: Introduction to Mobile Systems
 */

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Part 1: basic calculator with IMMEDIATE (sequential) evaluation — operations
 * are applied left-to-right in the order they are entered, without precedence:
 * 2 + 2 * 2 = (2 + 2) * 2 = 8.
 *
 * Chosen rule (documented): pressing an operator without entering a number
 * first uses the value currently shown in the display (initially 0); pressing
 * two operators in a row simply replaces the pending operator.
 *
 * Part 2 extras on this screen: BIN/OCT/HEX conversion of the current result
 * (integer values only — otherwise Error), History screen, and graphical
 * buttons (custom backgrounds that change color when pressed).
 */
public class MainActivity extends AppCompatActivity {

    private static final int MAX_INPUT_DIGITS = 12;

    private TextView textDisplay;

    private Double accumulator = null;   // previous result, null if none
    private String pendingOp = null;     // +, -, *, /, ^
    private final StringBuilder input = new StringBuilder();
    private boolean justEvaluated = false;
    private boolean errorState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        findViewById(R.id.btnBin).setOnClickListener(v -> onBase(2, "BIN"));
        findViewById(R.id.btnOct).setOnClickListener(v -> onBase(8, "OCT"));
        findViewById(R.id.btnHex).setOnClickListener(v -> onBase(16, "HEX"));
        findViewById(R.id.btnHistory).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));
        findViewById(R.id.btnExpr).setOnClickListener(v ->
                startActivity(new Intent(this, ExpressionActivity.class)));

        show("0");
    }

    private void onDigit(String digit) {
        if (errorState) {
            return; // only C resets after Error
        }
        if (justEvaluated) {
            // A digit after "=" starts a completely new calculation.
            accumulator = null;
            pendingOp = null;
            input.setLength(0);
            justEvaluated = false;
        }
        if (input.length() < MAX_INPUT_DIGITS) {
            input.append(digit);
        }
        show(input.toString());
    }

    private void onOperator(String op) {
        if (errorState) {
            return;
        }
        if (justEvaluated) {
            // An operator after "=" continues from the previous result.
            justEvaluated = false;
            pendingOp = op;
            input.setLength(0);
            return;
        }
        if (input.length() == 0) {
            // No new number: use the displayed value (0 at start) or just
            // replace the pending operator.
            if (accumulator == null) {
                accumulator = 0.0;
            }
            pendingOp = op;
            return;
        }

        double value = Double.parseDouble(input.toString());
        if (accumulator == null || pendingOp == null) {
            accumulator = value;
        } else {
            // Immediate evaluation of the pending operation (left-to-right).
            try {
                double result = CalcUtils.apply(accumulator, value, pendingOp);
                HistoryStore.addOperation(accumulator, pendingOp, value, result);
                accumulator = result;
            } catch (ArithmeticException e) {
                showError();
                return;
            }
        }
        input.setLength(0);
        pendingOp = op;
        show(CalcUtils.format(accumulator));
    }

    private void onEquals() {
        if (errorState) {
            return;
        }
        if (pendingOp != null && input.length() > 0) {
            double value = Double.parseDouble(input.toString());
            double base = accumulator == null ? 0 : accumulator;
            try {
                double result = CalcUtils.apply(base, value, pendingOp);
                HistoryStore.addOperation(base, pendingOp, value, result);
                accumulator = result;
            } catch (ArithmeticException e) {
                showError();
                return;
            }
            pendingOp = null;
            input.setLength(0);
            justEvaluated = true;
            show(CalcUtils.format(accumulator));
        } else if (pendingOp == null && input.length() > 0) {
            accumulator = Double.parseDouble(input.toString());
            input.setLength(0);
            justEvaluated = true;
            show(CalcUtils.format(accumulator));
        }
        // "5 + =" (operator without second number) is ignored.
    }

    /** Part 2.2: shows the current displayed value in the given base. */
    private void onBase(int base, String label) {
        if (errorState) {
            return;
        }
        double value;
        if (input.length() > 0) {
            value = Double.parseDouble(input.toString());
        } else if (accumulator != null) {
            value = accumulator;
        } else {
            value = 0;
        }
        if (!CalcUtils.isIntegral(value)) {
            // Documented rule: conversion of a non-integer value shows Error.
            showError();
            return;
        }
        long longValue = (long) value;
        String converted = (longValue < 0 ? "-" : "")
                + Long.toString(Math.abs(longValue), base).toUpperCase();
        HistoryStore.add(CalcUtils.format(value) + " = " + label + " " + converted);

        accumulator = value;
        input.setLength(0);
        pendingOp = null;
        justEvaluated = true; // digit starts fresh, operator continues from value
        show(label + ": " + converted);
    }

    private void onClear() {
        accumulator = null;
        pendingOp = null;
        input.setLength(0);
        justEvaluated = false;
        errorState = false;
        show("0");
    }

    private void showError() {
        accumulator = null;
        pendingOp = null;
        input.setLength(0);
        errorState = true;
        show(getString(R.string.display_error));
    }

    private void show(String text) {
        textDisplay.setText(text);
    }
}
