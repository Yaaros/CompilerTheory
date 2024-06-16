package Lex.test;

import Lex.RegexToMinDFA;
import Lex.utils.TreeVisualize;

public class TreeTest {
    //请注意，Java单元测试中使用java swing会瞬间消失
    public static void main(String[] args) {
        RegexToMinDFA dfa = new RegexToMinDFA("ba|(a|bb)a*b");
        TreeVisualize tv = new TreeVisualize(dfa.getTree());
    }
}
