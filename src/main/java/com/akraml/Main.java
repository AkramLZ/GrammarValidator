package com.akraml;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        // Test all types of grammars (0 through 3)
        for (int type = 0; type <= 3; type++) {
            System.out.println("Testing grammar of specified type: " + type);
            Grammar grammar = createG(type);
            displayRules(grammar);
            int classification = classifyGrammar(grammar);
            System.out.print("Classified as: ");
            switch (classification) {
                case 3:
                    System.out.println("Type 3 (Regular) Grammar.");
                    break;
                case 1:
                    System.out.println("Type 1 (Context-Sensitive) Grammar.");
                    break;
                case 2:
                    System.out.println("Type 2 (Context-Free) Grammar.");
                    break;
                case 0:
                    System.out.println("Type 0 (Unrestricted) Grammar.");
                    break;
                default:
                    System.out.println("Unknown grammar type.");
                    break;
            }
            System.out.println("------------------------------");
        }
    }

    public static Grammar createG(int grammarType) {
        switch (grammarType) {
            case 2 -> createType2Grammar();
            case 1 -> createType1Grammar();
            case 0 -> createType0Grammar();
            default -> createType3Grammar();
        }
        return createType3Grammar();
    }

    private static Grammar createType3Grammar() {
        // Type 3: Regular grammar
        // S -> aS | T
        // T -> bT | $   (using "$" to denote ε)
        Set<String> V_T = new HashSet<>(List.of("a", "b"));
        Set<String> V_N = new HashSet<>(List.of("S", "T"));
        String S = "S";
        Map<String, List<String>> P = new HashMap<>();
        P.put("S", List.of("aS", "T"));
        P.put("T", List.of("bT", "$"));
        return new Grammar(V_T, V_N, S, P);
    }

    private static Grammar createType2Grammar() {
        // Type 2: Context-Free grammar that is not context-sensitive because of an improper ε production.
        // S -> aSb | $
        // Here S appears on the right-hand side while S -> $ exists.
        Set<String> V_T = new HashSet<>(List.of("a", "b"));
        Set<String> V_N = new HashSet<>(List.of("S"));
        String S = "S";
        Map<String, List<String>> P = new HashMap<>();
        P.put("S", List.of("aSb", "$"));
        return new Grammar(V_T, V_N, S, P);
    }

    private static Grammar createType1Grammar() {
        // Type 1: Context-Sensitive grammar (non-regular) without problematic ε.
        // S -> aSb | ab
        Set<String> V_T = new HashSet<>(List.of("a", "b"));
        Set<String> V_N = new HashSet<>(List.of("S"));
        String S = "S";
        Map<String, List<String>> P = new HashMap<>();
        P.put("S", List.of("aSb", "ab"));
        return new Grammar(V_T, V_N, S, P);
    }

    public static Grammar createType0Grammar() {
        // Type 0: Unrestricted grammar.
        // We use a multi-symbol left-hand side to force non-context-freeness.
        // S -> AB
        // AB -> BA
        // A -> a
        // B -> b
        Set<String> V_T = new HashSet<>(List.of("a", "b"));
        // Note: Even though "AB" has two characters, we include it in V_N.
        Set<String> V_N = new HashSet<>(List.of("S", "A", "B", "AB"));
        String S = "S";
        Map<String, List<String>> P = new HashMap<>();
        P.put("S", List.of("AB"));
        P.put("AB", List.of("BA"));
        P.put("A", List.of("a"));
        P.put("B", List.of("b"));
        return new Grammar(V_T, V_N, S, P);
    }

    public static void displayRules(Grammar grammar) {
        Map<String, List<String>> productions = grammar.getProductions();
        for (String lhs : productions.keySet()) {
            for (String rhs : productions.get(lhs)) {
                System.out.println(lhs + " -> " + rhs);
            }
        }
    }

    public static boolean isType3(Grammar grammar) {
        Map<String, List<String>> productions = grammar.getProductions();
        Set<String> V_T = grammar.getVT();
        Set<String> V_N = grammar.getVN();
        String startSymbol = grammar.getS();

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
                    // Productions longer than 2 symbols are not allowed in a regular grammar.
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isContextFree(Grammar grammar) {
        Map<String, List<String>> productions = grammar.getProductions();
        Set<String> V_N = grammar.getVN();
        for (String lhs : productions.keySet()) {
            if (lhs.length() != 1 || !V_N.contains(lhs)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isContextSensitive(Grammar grammar) {
        Map<String, List<String>> productions = grammar.getProductions();
        String startSymbol = grammar.getS();
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
    public static int classifyGrammar(Grammar grammar) {
        // First, if not context-free, then it's unrestricted (Type 0).
        if (!isContextFree(grammar)) {
            return 0;
        }
        // If it is context-free and satisfies regular conditions, then it is Type 3.
        if (isType3(grammar)) {
            return 3;
        }
        // If it is not regular but meets context-sensitive criteria, then it's Type 1.
        if (isContextSensitive(grammar)) {
            return 1;
        }
        // Otherwise, it remains as a context-free grammar (Type 2).
        return 2;
    }

}