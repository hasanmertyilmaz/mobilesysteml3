package com.mertyilmaz.lab3;

/**
 * Shared arithmetic helpers used by both calculators.
 */
public final class CalcUtils {

    private CalcUtils() {
    }

    /**
     * Applies a binary operator. Throws ArithmeticException on division by zero
     * so the callers can show "Error" instead of crashing.
     */
    public static double apply(double a, double b, String op) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/":
                if (b == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return a / b;
            case "^": return Math.pow(a, b);
            default: throw new IllegalArgumentException("Unknown operator: " + op);
        }
    }

    /** Formats a double without the trailing ".0" for integral values. */
    public static String format(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "Error";
        }
        if (value == Math.rint(value) && Math.abs(value) < 1e15) {
            return Long.toString((long) value);
        }
        return String.valueOf(value);
    }

    /** True if the value has no fractional part (safe for base conversion). */
    public static boolean isIntegral(double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value)
                && value == Math.rint(value) && Math.abs(value) < 1e15;
    }
}
