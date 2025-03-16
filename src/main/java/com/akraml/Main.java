package com.akraml;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        // Test all types of grammars (0 through 3)
        for (int type = 0; type <= 3; type++) {
            System.out.println("Testing grammar of specified type: " + type);
            Grammar grammar = createG(type);
            grammar.displayRules();
            int classification = grammar.classifyGrammar();
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

}