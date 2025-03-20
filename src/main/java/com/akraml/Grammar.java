package com.akraml;

import lombok.Data;

import java.util.*;

/**
 * <p>Grammar class is a data structure to represent a Grammar in <b>Formal Language Theory</b>.</p>
 * <p>For more information, check out <a href="https://en.wikipedia.org/wiki/Formal_language">This Wikipedia article</a></p>
 *
 * @author Akram Louze, Moussa Tiab
 */
@Data
public final class Grammar {

    // My computer almost exploded due to infinity loop
    private static final int MAX_DERIVATION_DEPTH = 20;

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

    /**
     * Check if the current {@link Grammar} is a type 3 grammar.
     *
     * @return {@code true} If it's a type 3 grammar, {@code false} otherwise.
     * @author Akram Louze
     */
    public boolean isType3() {
        boolean isRightLinear = true;
        boolean isLeftLinear = true;

        for (Map.Entry<String, List<String>> entry : productions.entrySet()) {
            String lhs = entry.getKey().trim();
            // LHS must be exactly one non-terminal
            if (!VN.contains(lhs) || lhs.length() != 1) {
                return false;
            }
            for (String rule : entry.getValue()) {
                String rhs = rule.trim();
                // Allow empty production only if LHS equals start symbol (if applicable)
                if (rhs.equals("$")) {
                    if (!lhs.equals(S)) return false;
                    continue;
                }
                boolean right = isValidRightLinearRule(rhs);
                boolean left = isValidLeftLinearRule(rhs);
                if (!right) isRightLinear = false;
                if (!left) isLeftLinear = false;
                if (!right && !left) {
                    return false;
                }
            }
        }
        return isRightLinear || isLeftLinear;
    }

    /**
     * Just a helper function for right linear check.
     *
     * @param rhs Right-hand side.
     * @return {@code true} If the provided rule is valid, {@code false} otherwise.
     * @author Akram Louze
     */
    private boolean isValidRightLinearRule(String rhs) {
        if (rhs.length() == 1 && VT.contains(rhs)) return true;
        if (rhs.length() == 2
                && VT.contains(String.valueOf(rhs.charAt(0)))
                && VN.contains(String.valueOf(rhs.charAt(1)))) return true;
        return false;
    }

    /**
     * Same as {@link #isValidRightLinearRule(String)}, but for left linearity check.
     *
     * @author Akram Louze
     */
    private boolean isValidLeftLinearRule(String rhs) {
        if (rhs.length() == 1 && VT.contains(rhs)) return true;
        if (rhs.length() == 2
                && VN.contains(String.valueOf(rhs.charAt(0)))
                && VT.contains(String.valueOf(rhs.charAt(1)))) return true;
        return false;
    }


    /**
     * Check if the current {@link Grammar} is a type 2 grammar.
     *
     * @return {@code true} If it's a type 2 grammar, {@code false} otherwise.
     * @author Moussa Tiab
     */
    private boolean isType2() {
        for (Map.Entry<String, List<String>> entry : productions.entrySet()) {
            String nonTerminal = entry.getKey();
            for (String production : entry.getValue()) {
                // Check if the left side is a single non-terminal
                if (nonTerminal.length() != 1 || !VN.contains(nonTerminal)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check if the current {@link Grammar} is a type 3 grammar.
     *
     * @return {@code true} If it's a type 3 grammar, {@code false} otherwise.
     * @author Moussa Tiab
     */
    private boolean isType1() {
        for (Map.Entry<String, List<String>> entry : productions.entrySet()) {
            String leftSide = entry.getKey();
            String startSymbol = getS();
            for (String production : entry.getValue()) {
                if (production.equals("$")) {
                    if (!leftSide.equals(startSymbol)) return false;
                }
                // Check if |leftSide| <= |production|
                if (leftSide.length() > production.length()) {
                    return false;
                }
            }
        }
        return true;
    }

    public int classify() {
        if (isType3()) return 3;
        if (isType2()) return 2;
        if (isType1()) return 1;
        return 0;
    }

    public void derive(String target) {
        String startSymbol = getS();
        Map<String, List<String>> productions = getProductions();

        Queue<List<String>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.add(Collections.singletonList(startSymbol));

        while (!queue.isEmpty()) {
            List<String> steps = queue.poll();
            String current = steps.get(steps.size() - 1);

            // Prevent computer from doing BOOM
            if (steps.size() > MAX_DERIVATION_DEPTH) {
                System.out.println("Max depth reached. Derivation stopped.");
                return;
            }

            if (current.equals(target)) {
                System.out.println("Derivation steps:");
                steps.forEach(System.out::println);
                return;
            }

            if (visited.contains(current)) continue;
            visited.add(current);

            // Expand the current string using valid productions
            for (Map.Entry<String, List<String>> entry : productions.entrySet()) {
                String nonTerminal = entry.getKey();

                // Check all occurrences of the non-terminal in the current string
                int index = current.indexOf(nonTerminal);
                while (index != -1) {
                    for (String production : entry.getValue()) {
                        String nextStep = current.substring(0, index) + production + current.substring(index + nonTerminal.length());

                        // Only add steps that make some progress
                        if (!visited.contains(nextStep)) {
                            List<String> newSteps = new ArrayList<>(steps);
                            newSteps.add(nextStep);
                            queue.add(newSteps);
                        }
                    }
                    index = current.indexOf(nonTerminal, index + 1);
                }
            }
        }

        System.out.println("No valid derivation found for: " + target);
    }

    // Utility method to count non-terminals in a string
    private int countNonTerminals(String str, Set<String> nonTerminals) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (nonTerminals.contains(String.valueOf(c))) {
                count++;
            }
        }
        return count;
    }

}
