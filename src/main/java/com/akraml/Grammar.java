package com.akraml;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public final class Grammar {

    private final Set<String> VT, VN; // Terminal & Non-Terminal symbols
    private final String S; // Start symbol
    private final Map<String, List<String>> productions; // Production Rules

    public void displayRules() {
        Map<String, List<String>> productions = getProductions();
        for (String lhs : productions.keySet()) {
            for (String rhs : productions.get(lhs)) {
                System.out.println(lhs + " -> " + rhs);
            }
        }
    }

    public boolean isType3() {
        boolean isRightLinear = true;
        boolean isLeftLinear = true;

        for (Map.Entry<String, List<String>> entry : productions.entrySet()) {
            String nonTerminal = entry.getKey();
            for (String production : entry.getValue()) {
                // Check for right-linear form: A -> aB or A -> a or A -> ε
                if (!production.matches("^[a-z]?[A-Z]?$") && !production.equals("$")) {
                    isRightLinear = false;
                }

                // Check for left-linear form: A -> Ba or A -> a or A -> ε
                if (!production.matches("^[A-Z]?[a-z]?$") && !production.equals("$")) {
                    isLeftLinear = false;
                }
            }
        }

        // The grammar is type 3 if it is either right-linear or left-linear, but not both
        return isRightLinear || isLeftLinear;
    }

    public boolean isContextFree() {
        Map<String, List<String>> productions = getProductions();
        Set<String> V_N = getVN();
        for (String lhs : productions.keySet()) {
            if (lhs.length() != 1 || !V_N.contains(lhs)) {
                return false;
            }
        }
        return true;
    }

    public boolean isContextSensitive() {
        Map<String, List<String>> productions = getProductions();
        String startSymbol = getS();
        for (String A : productions.keySet()) {
            for (String r : productions.get(A)) {
                if (r.equals("$")) {
                    if (!A.equals(startSymbol)) {
                        return false;
                    }
                    // If S -> ε exists, ensure that S does not appear on any right-hand side.
                    for (String B : productions.keySet()) {
                        for (String prod : productions.get(B)) {
                            if (prod.contains(startSymbol)) {
                                return false;
                            }
                        }
                    }
                } else {
                    if (r.length() < A.length()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Classify the grammar into one of the following types:
     *  Type 0: Unrestricted Grammar
     *  Type 1: Context-Sensitive Grammar (if no problematic ε-production exists)
     *  Type 2: Context-Free Grammar (with problematic ε-production)
     *  Type 3: Regular Grammar
     * The classification is done hierarchically.
     */
    public int classify() {
        // First, if not context-free, then it's unrestricted (Type 0).
        if (!isContextFree()) {
            return 0;
        }
        // If it is context-free and satisfies regular conditions, then it is Type 3.
        if (isType3()) {
            return 3;
        }
        // If it is not regular but meets context-sensitive criteria, then it's Type 1.
        if (isContextSensitive()) {
            return 1;
        }
        // Otherwise, it remains as a context-free grammar (Type 2).
        return 2;
    }

}
