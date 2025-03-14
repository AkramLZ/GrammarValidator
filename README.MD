# Language Theory - Grammar Validator
This is the source code of the 4th Semester Mid-Term Validation in the Language Theory module
for 2<sup>nd</sup> Year Bachelor degree in Computer Science students in University of Oum EL-Bouaghi.

## Validation Exercise
The objective of this part is to implement some functions on grammars. We assume that the empty word is represented by
the symbol $.
1. Propose a data structure to represent grammars.
2. Implement a function `create_G` to create a grammar **(of any type)**
3. Implement a function `Display_Rules` to display the derivation rules of the created grammar. For example, for the G Grammar:
$$G = (V_T, V_N, A, P) \text{ with: } V_T = \{a, b\}, \ V_N = \{S, T\}, \ S, \ P = \{ S \rightarrow aS \mid T, \ T \rightarrow bT \mid \epsilon \}$$
We display:
    ```
    S --> aS
    S --> T
    T --> bT
    T --> $
    ```
4. Implement a function `Type3` to check whether the grammar is type 3 or not.
5. Implement a function `Type_G` to specify the type of the created grammar (type 3, 2, 1 or 0).