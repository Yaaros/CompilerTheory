package Lex;

import Lex.utils.LexTree;
import Lex.utils.StateGraph;
/**输入：正则表达式（含有|,*,圆括号,+,?和字母的字符串）
 * 输出：一个图
 * 步骤：
 * 1.把正则表达式作为中缀表达式建树
 * 2.把中缀表达式从下而上转化为有向有权图
 */
public class RegexToMinDFA {
    StateGraph NFA;
    StateGraph DFA;
    StateGraph minDFA;

    LexTree tree;
    public RegexToMinDFA(String regex) {
        RegexToNFA(regex);
        NFAToDFA();
        minimizeDFA();
    }

    public StateGraph getDFA() {
        return DFA;
    }

    public StateGraph getMinDFA() {
        return minDFA;
    }

    public void printTree() {
        System.out.println("前序序列:");
        tree.preOrder();
        System.out.println("中序序列:");
        tree.inOrder();
    }

    public LexTree getTree(){
        return tree;
    }

    public StateGraph getNFA() {
        return NFA;
    }
    public void RegexToNFA(String regex){
        tree = new LexTree(regex);
        NFA = tree.postOrder();
        System.out.println("NFA=\n"+NFA);
    }
    public void NFAToDFA(){
        DFA = NFA.createDFA();
        System.out.println("DFA=\n"+DFA);
    }
    public void minimizeDFA(){
        minDFA = DFA.minimize();
        System.out.println("minDFA=\n"+DFA);
    }
}