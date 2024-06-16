package Syntac.test;

import Syntac.LLAnalyzer;
import Syntac.utils.LL.*;
import org.junit.jupiter.api.Test;

import java.util.*;

/* Syntacs:
* E -> TE1
  E1 -> +TE1|~
  T -> FT1
  T1 -> *FT1|~
  F -> (E)|id
 * */

public class UnitTest {
    Symbol start;
    Set<Symbol> terminals;
    Set<Symbol> nonTerminals;

    /**
     * The Test of the Initializer
     */
    @Test
    public void problem0(){
        Map<Symbol, Set<LinkList>> synTacs = initialize();
        LLAnalyzer aa = new LLAnalyzer(synTacs,terminals,null);
        System.out.println(synTacs);
    }

    /**
     * The Test of the Method of getFirstSet
     */
    @Test
    public void problem1(){
        Map<Symbol, Set<LinkList>> synTacs = initialize();

        LLAnalyzer aa = new LLAnalyzer(synTacs,terminals,null);

        Set<Symbol> keySet = synTacs.keySet();
        for (Symbol symbol : keySet) {
           System.out.println(symbol+":"+aa.getFirstSet(symbol));
        }
    }

    /**
     * The Test of the Method of getFOLLOWSet
     */
    @Test
    public void problem2(){
        Map<Symbol, Set<LinkList>> synTacs = initialize();
        LLAnalyzer aa = new LLAnalyzer(synTacs,terminals,null);
        aa.setStartSymbol(start);
        Set<Symbol> keySet = synTacs.keySet();
        for (Symbol symbol : keySet) {
           System.out.println(symbol+"的follow是:"+aa.getFollowSet(symbol));
        }
    }

    /**
     * The Test of the Method to get the FirstSet of a String,not a Symbol
     */
    @Test
    public void problem3_1(){
        Map<Symbol, Set<LinkList>> synTacs = initialize();
        LLAnalyzer aa = new LLAnalyzer(synTacs,terminals,nonTerminals);
        aa.setStartSymbol(start);
        Set<Symbol> keySet = synTacs.keySet();
        for (Symbol symbol : keySet) {
           for (LinkList list : synTacs.get(symbol)) {
               System.out.println(symbol+":");
               System.out.println(aa.getFirstSet_Chain(list));
           }
        }
    }

    /**
     * The Test of Map of Analyzer
     */
    @Test
    public void problem3_2(){
        Map<Symbol, Set<LinkList>> synTacs = initialize();
        LLAnalyzer aa = new LLAnalyzer(synTacs,terminals,nonTerminals);
        aa.setStartSymbol(start);
        aa.createDecisionMap();
        //aa.daMap.traverseDAMap();
        System.out.println("语法解析表为:(~即epsilon)");
        System.out.println(aa.daMap);
    }

    //测试字符串能否被正确分割
    @Test
    public void problem4_1(){
        String thisString = "id+id*(id+id)";
        System.out.println(StringSymbolTransfer.transfer(thisString).getStringVer());
    }

    //测试表达式的反向入栈操作
    @Test
    public void problem4_2(){
        Map<Symbol, Set<LinkList>> synTacs = initialize();
        LLAnalyzer aa = new LLAnalyzer(synTacs,terminals,nonTerminals);
        Deque<Symbol> ss = new LinkedList<>();
        ss.push(new EndSymbol());
        Set<LinkList> set = synTacs.get(new NonTerminal("T"));
        for (LinkList l : set) {
            aa.reversePush(ss,l);
            System.out.println(ss);
            ss.clear();
            ss.push(new EndSymbol());
        }
    }

    /**
     * The Test of Analyzation
     */
    @Test
    public void problem4_3(){
        Map<Symbol, Set<LinkList>> synTacs = initialize();
        LLAnalyzer aa = new LLAnalyzer(synTacs,terminals,nonTerminals);
        Symbol it = aa.analyze("id+id*(id+id)$",start);
    }
    /**
     * The Test of Constructing A Tree
     */
    @Test
    public void problem5(){
        Map<Symbol, Set<LinkList>> synTacs = initialize();
        LLAnalyzer aa = new LLAnalyzer(synTacs,terminals,nonTerminals);
        aa.analyzeAndConstructTree("id+id*(id+id)$",start);
    }
    /**
     * The Test of Graphic
     */
    @Test
    public void problem5_2(){
        Map<Symbol, Set<LinkList>> synTacs = initialize();
        LLAnalyzer aa = new LLAnalyzer(synTacs,terminals,nonTerminals);
        SymbolNode n = aa.analyzeAndConstructTree("id+id*(id+id)$",start);
    }

    private Map<Symbol, Set<LinkList>> initialize() {
        Symbol s0 = new NonTerminal("F");
        Symbol s1 = new NonTerminal("E1");
        Symbol s2 = new NonTerminal("E");
        Symbol s3 = new NonTerminal("T1");
        Symbol s4 = new NonTerminal("T");
        Symbol s5 = new Terminal("+");
        Symbol s6 = new Terminal("*");
        Symbol s7 = new Terminal("id");
        Symbol s8 = new Epsilon();
        Symbol s9 = new Terminal("(");
        Symbol s10 = new Terminal(")");
        start = s2;
        terminals = new HashSet<>();
        terminals.add(s5);
        terminals.add(s6);
        terminals.add(s7);
        //terminals.add(s8);不要把Epsilon当作Terminal
        terminals.add(s9);
        terminals.add(s10);
        nonTerminals = new HashSet<>();
        nonTerminals.add(s1);
        nonTerminals.add(s2);
        nonTerminals.add(s3);
        nonTerminals.add(s4);
        nonTerminals.add(s5);


        LinkedList<Symbol> lst = new LinkedList<>();
        lst.add(s4);
        lst.add(s1);
        LinkList lst1 = new LinkList(lst);
        Set<LinkList> set1 = new HashSet<>();
        set1.add(lst1);
        lst.clear();

        lst.add(s5);
        lst.add(s4);
        lst.add(s1);
        LinkList lst2 = new LinkList(lst);
        Set<LinkList> set2 = new HashSet<>();
        set2.add(lst2);
        LinkedList<Symbol> lst2_2 = new LinkedList<>();
        lst2_2.add(s8);
        set2.add(new LinkList(lst2_2));
        lst.clear();

        lst.add(s0);
        lst.add(s3);
        LinkList lst3 = new LinkList(lst);
        Set<LinkList> set3 = new HashSet<>();
        set3.add(lst3);
        lst.clear();

        lst.add(s6);
        lst.add(s0);
        lst.add(s3);
        LinkList lst4 = new LinkList(lst);
        Set<LinkList> set4 = new HashSet<>();
        set4.add(lst4);
        LinkedList<Symbol> lst4_2 = new LinkedList<>();
        lst4_2.add(s8);
        set4.add(new LinkList(lst4_2));
        lst.clear();

        lst.add(s9);
        lst.add(s2);
        lst.add(s10);
        LinkList lst5 = new LinkList(lst);
        Set<LinkList> set5 = new HashSet<>();
        set5.add(lst5);
        LinkedList<Symbol> lst5_2 = new LinkedList<>();
        lst5_2.add(s7);
        set5.add(new LinkList(lst5_2));
        lst.clear();

        Map<Symbol, Set<LinkList>> synTacs =
                new HashMap<>();
        synTacs.put(s2,set1);
        synTacs.put(s1,set2);
        synTacs.put(s4,set3);
        synTacs.put(s3,set4);
        synTacs.put(s0,set5);
        return synTacs;
    }

}
