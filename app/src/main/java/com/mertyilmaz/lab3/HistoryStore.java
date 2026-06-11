package com.mertyilmaz.lab3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * In-memory history of all operations performed since app launch.
 * One entry per line, e.g. "2 + 2 = 4".
 */
public final class HistoryStore {

    private static final List<String> ENTRIES = new ArrayList<>();

    private HistoryStore() {
    }

    public static void add(String entry) {
        ENTRIES.add(entry);
    }

    public static void addOperation(double a, String op, double b, double result) {
        add(CalcUtils.format(a) + " " + op + " " + CalcUtils.format(b)
                + " = " + CalcUtils.format(result));
    }

    public static List<String> getEntries() {
        return Collections.unmodifiableList(ENTRIES);
    }
}
