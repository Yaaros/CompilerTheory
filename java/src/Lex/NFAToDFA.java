package Lex;

import Lex.utils.StateGraph;
public class NFAToDFA {

    public NFAToDFA(StateGraph NFA){
        StateGraph DFA = NFA.createDFA();
    }
}
