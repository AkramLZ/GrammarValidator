package com.akraml;

import java.util.*;

/**
 * The main bootstrap to run the program of Grammar Validation.
 *
 * @author Akram Louze, Tiab Moussa
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Input terminals
        System.out.print("Enter terminal symbols don't forget the space between each terminal symbol: ");
        Set<String> terminals = new HashSet<>(Arrays.asList(scanner.nextLine().split("\\s+")));
        // Input non-terminals
        System.out.print("Enter non-terminal symbols (separated by spaces, e.g., S A B): ");
        Set<String> nonTerminals = new HashSet<>(Arrays.asList(scanner.nextLine().split("\\s+")));
        // Input start symbol
        System.out.print("Enter the start symbol (must be one of the non-terminals): ");
        String startSymbol = scanner.nextLine();
        // Input production rules
        System.out.println("Enter production rules (one at a time, in the format 'A -> aA', type 'done' to finish):");
        Map<String, List<String>> productions = new HashMap<>();
        while (true) {
            System.out.print("Enter rule: ");
            String ruleInput = scanner.nextLine();
            if (ruleInput.equalsIgnoreCase("done")) {
                break;
            }
            String[] parts = ruleInput.trim().split("->");
            String nonTerminal = parts[0].trim();
            String[] prd = parts[1].trim().split("\\|");
            productions.putIfAbsent(nonTerminal, new ArrayList<>());
            productions.get(nonTerminal).addAll(Arrays.asList(prd));
        }
        Grammar grammar = createG(terminals, nonTerminals, startSymbol, productions);
        grammar.displayRules();
        int type = grammar.classify();
        if (type == 3) {
            System.out.println("It's type 3");
        }
        else {
            System.out.println("It's not type 3 :)");
            System.out.println("Grammar type: " + type);
        }

        // Generate grammar derivation now.
        // Also, it looks like the derivation rules means only grammar rules, but I will
        // just do it as what I understood.
        System.out.print("Enter value to find derivation steps for: ");
        String target = scanner.nextLine();
        grammar.derive(target);
    }

    public static Grammar createG(Set<String> VT,
                                  Set<String> VN,
                                  String S,
                                  Map<String, List<String>> productions) {
        return new Grammar(VT, VN, S, productions);
    }

}