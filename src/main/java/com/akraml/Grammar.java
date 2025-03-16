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
        Map<String, List<String>> productions = getProductions();
        Set<String> V_T = getVT();
        Set<String> V_N = getVN();
        String startSymbol = getS();

        for (String A : productions.keySet()) {
            for (String r : productions.get(A)) {
                if (r.equals("$")) {
                    // Only the start symbol may have an ε-production.
                    if (!A.equals(startSymbol)) {
                        return false;
                    }
                } else if (r.length() == 1) {
                    if (!V_T.contains(r)) {
                        return false;
                    }
                } else if (r.length() == 2) {
                    String first = r.substring(0, 1);
                    String second = r.substring(1);
                    if (!V_T.contains(first) || !V_N.contains(second)) {
                        return false;
                    }
                } else {
                    // Productions longer than 2 symbols are not allowed in a regular 
                    return false;
                }
            }
        }
        return true;
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
    public int classifyGrammar() {
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
