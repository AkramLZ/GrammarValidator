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


}
