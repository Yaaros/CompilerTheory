package Lex.test;

import Lex.RegexToMinDFA;
import Lex.utils.LexTree;
import Lex.utils.StateGraph;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.List;

public class RegexTest {
    /**
     * 差什么：
     * 闭包是错的，并是对的
     * 层级之间的叠加有问题
     */
    @Test
    public void test0_treeTest(){
        LexTree t = new LexTree("(a|b)*ab");
        System.out.println(t.symbolList);
        System.out.println("前序遍历:");
        t.preOrder();
        System.out.println("中序遍历:");
        t.inOrder();
    }
    @Test
    public void test1_stateGraphTest(){
        List<String> lst = List.of(new String[]{"a","b"});
        HashSet<String> set = new HashSet<>(lst);
        StateGraph s = new StateGraph(set);
    }
    @Test
    public void test2_machineTest(){
        LexTree t = new LexTree("(a|b)*ab");
        StateGraph result = t.postOrder();
        System.out.println(result);
        System.out.println(result.getEndState()==result.getStartState());//IMPORTANT
    }
    @Test
    public void test3_NFATest(){
        new RegexToMinDFA("(a|b)*ab");
    }
    @Test
    public void test4_NFAToDFATest(){
        RegexToMinDFA dfa = new RegexToMinDFA("(a|b)*ab");
    }
    @Test
    public void test5_NFAToDFATest2(){
        RegexToMinDFA dfa = new RegexToMinDFA("ba|(a|bb)a*b");
        dfa.printTree();
    }
}
