package Syntac;
import Syntac.utils.*;
import Syntac.utils.LL.*;

import java.util.*;

//生成第一个作业的语法的First集合,Follow集合
/**
 * 产生式:Map<Symbol, Set<LinkList>> synTacs
 * 预测表:Map<NonTerminal,Map<Symbol,Map<Symbol, Set<LinkList>>>>,封装为DAMap
* */
public class LLAnalyzer {

    public Map<Symbol, Set<LinkList>> synTacs;

    public Set<Symbol> terminals;

    public Set<Symbol> nonTerminals;

    public Symbol startSymbol;

    public DAMap daMap;

    public LLAnalyzer(Map<Symbol, Set<LinkList>> synTacs, Set<Symbol> terminals, Set<Symbol> nonTerminals) {
        this.synTacs = synTacs;
        this.terminals = terminals;
        this.nonTerminals = nonTerminals;
        terminals.add(new EndSymbol());
    }
    public void setStartSymbol(Symbol X){
        startSymbol = X;
    }

    public Set<Symbol> getFirstSet(Symbol X){
        Set<Symbol> toReturn = new HashSet<>();
        if(Terminal.class.isAssignableFrom(X.getClass())) toReturn.add(X);
        if(NonTerminal.class.isAssignableFrom(X.getClass())) {
            for (LinkList list : synTacs.get(X)) {
                Symbol head = list.getHead();
                if(head.equals(new Epsilon())){//若有X->epsilon
                    toReturn.add(new Epsilon());
                } else if (Terminal.class.isAssignableFrom(head.getClass())) {
                    //若有X->aALPHA,a.class=Terminal
                    toReturn.add(head);
                } else if (NonTerminal.class.isAssignableFrom(head.getClass())) {
                    Set<Symbol> firstY_i = getFirstSet(head);
                    toReturn.addAll(firstY_i);
                    //有epsilon
                    if(hasEpsilon(firstY_i)){
                        Symbol next = head.getNext();
                        if(next!=null){
                            toReturn.addAll(getFirstSet(next));
                        }
                    }
                }
            }
        }
        //System.out.println(X+"的First是:"+toReturn);
        return toReturn;
    }
    private boolean hasEpsilon(Set<Symbol> set){
        for (Symbol s : set) {
            if(Epsilon.class.isAssignableFrom(s.getClass()))return true;
        }
        return false;
    }
    public Set<Symbol> getFollowSet(Symbol X){
        Set<Symbol> toReturn = new HashSet<>();
        Symbol end = new EndSymbol();
        cond1(X, toReturn, end);//情况1:开始符后接美元符号
        for (Symbol key : synTacs.keySet()) {
            if(!key.equals(X)){//other -> aXb
                for (LinkList list : synTacs.get(key)) {//针对other指向的所有串
                    if(list.contains(X)){//如果有X
                        cond23(key, X, toReturn, list);//情况2和3
                    }
                }
            }
        }
        //System.out.println(X+"的FOLLOW是:"+toReturn);
        return toReturn;
    }

    private void cond1(Symbol X, Set<Symbol> toReturn, Symbol end) {
        if(X.equals(startSymbol)) toReturn.add(end);//情况1:开始符后接美元符号
    }

    private void cond23(Symbol key, Symbol X, Set<Symbol> toReturn, LinkList list) {
        Symbol curr = list.getHead();
        while(curr!=null){
            if(curr.equals(X)&&curr.getNext()!=null){//匹配到X,找X后面的符号b
                Set<Symbol> set = getFirstSet(curr.getNext());//b的first集合
                for (Symbol s : set) {
                    if(!s.equals(new Epsilon())){//不是空串
                        toReturn.add(s);
                    }
                }
                if(hasEpsilon(set)){//情况3
                    toReturn.addAll(getFollowSet(key));
                }
            }
            if(curr.equals(X)&&curr.getNext()==null){//匹配到X,但X后面不存在符号b
                toReturn.addAll(getFollowSet(key));//Problem
                break;
            }
            curr = curr.getNext();//匹配下一个字符
        }
    }
    public Set<Symbol> getFirstSet_Chain(LinkList input){
        Set<Symbol> toReturn = new HashSet<>();
        Symbol curr = input.getHead();
        if(Terminal.class.isAssignableFrom(curr.getClass())){
            toReturn.add(curr);
        }else{
            Set<Symbol> set = getFirstSet(curr);
            if(hasEpsilon(set)){
                while(hasEpsilon(set)){
                    for (Symbol symbol : set) {//非空符号写入集合
                        if(!symbol.equals(new Epsilon())){
                            set.add(symbol);
                        }
                    }
                    assert curr!=null;
                    curr = curr.getNext();
                    if(curr!=null){
                        if(Terminal.class.isAssignableFrom(curr.getClass())){
                            toReturn.add(curr);
                            return toReturn;
                        } else {
                            //else的话进行下一轮循环
                            set = getFirstSet(curr);
                        }
                    }
                }
            }else{
                toReturn.addAll(set);
            }
        }
        //System.out.println(input+"的FIRST是:"+toReturn);
        return toReturn;
    }
    public Set<Symbol> getFirstSet_Chain2(LinkList input) {
        Set<Symbol> toReturn = new HashSet<>();
        Symbol curr = input.getHead();
        if (Terminal.class.isAssignableFrom(curr.getClass())) {
            toReturn.add(curr);
            return toReturn;
        }
        Set<Symbol> set = getFirstSet(curr);
        while (curr != null) {
            for (Symbol symbol : set) {
                if (!symbol.equals(new Epsilon())) {
                    toReturn.add(symbol);
                }
            }
            if (!hasEpsilon(set)) {
                break; // 如果当前符号的 FIRST 集合没有 epsilon，停止循环
            }
            curr = curr.getNext();
            if (curr != null) {
                if (Terminal.class.isAssignableFrom(curr.getClass())) {
                    toReturn.add(curr);
                    break; // 如果下一个符号是终结符，添加它并结束循环
                } else {
                    set = getFirstSet(curr); // 更新非终结符的 FIRST 集合
                }
            } else {
                toReturn.add(new Epsilon()); // 如果所有符号都有 epsilon，添加 epsilon 到结果集
            }
        }
        return toReturn;
    }
    public DAMap createDecisionMap(){
        daMap = new DAMap(terminals);
        Set<Symbol> keySet = synTacs.keySet();
        for (Symbol A : keySet) {
            for (LinkList alpha : synTacs.get(A)) {//A->alpha是一条产生式
                Map<Symbol,LinkList> map = new HashMap<>();
                map.put(A,alpha);
                if(alpha.contains(new Epsilon())){//推空
                    Set<Symbol> follow = getFollowSet(A);
                    for (Symbol y : follow) {
                        daMap.fill((NonTerminal) A,y,map);
                    }
                }
                Set<Symbol> first = getFirstSet_Chain2(alpha);
                for (Symbol y : first) {
                    if(Terminal.class.isAssignableFrom(y.getClass())){
                        daMap.fill((NonTerminal) A,y,map);
                    }
                }
            }
        }
        return daMap;
    }
    public Symbol analyze(String input,Symbol startSymbol){
        createDecisionMap();
        LinkList SymbolList = StringSymbolTransfer.transfer(input);
        setStartSymbol(startSymbol);
        Deque<Symbol> analyzer = new LinkedList<>();
        analyzer.push(new EndSymbol());
        analyzer.push(startSymbol);
        int i = 0;
        while  (analyzer.peek()!=null&&
                !EndSymbol.class.isAssignableFrom(analyzer.peek().getClass())&&
                !EndSymbol.class.isAssignableFrom(SymbolList.getHead().getClass()))
        {
            System.out.println();
            i++;
            Symbol symbol1 = analyzer.pop();
            Symbol symbol2 = SymbolList.getHead();
            if(Terminal.class.isAssignableFrom(symbol1.getClass())){//栈顶是终结符
                if(symbol1.equals(symbol2)){//匹配操作
                    System.out.println("第"+i+"次运行,"+symbol1 + "匹配成功");
                    System.out.println("第"+i+"次的analyzer:"+analyzer);
                    System.out.println("此时的String:"+SymbolList.getStringVer());
                    SymbolList.pop();
                    continue;
                }else{//出现错误
                    handleError("终结符不匹配");
                }
            } else if (symbol1.equals(new Epsilon())) {//Epsilon


                System.out.println("第"+i+"次运行添加Epsilon");
                System.out.println("第"+i+"次的analyzer:"+analyzer);
                System.out.println("此时的String:"+SymbolList.getStringVer());
                continue;
            }
            System.out.println("symbol1="+symbol1);
            System.out.println("symbol2="+symbol2);
            //推导:
            LinkList thisList = daMap.getProduction(symbol1,symbol2).get(symbol1);
            reversePush(analyzer,thisList);

            System.out.println("处理的产生式是:"+thisList);


            System.out.println("第"+i+"次的analyzer:"+analyzer);
            System.out.println("此时的String:"+SymbolList.getStringVer());
        }
        /*我将上面的while循环的条件称之为正常情况，如果结束后，堆栈有东西而符号串只剩$
          那是正确的。但是，如果堆栈是$而符号串还有东西，此时说明分析器已经无法应用任何
          规则来继续分析了，因此应该报错。  */
        System.out.println("出大循环后的Analyzer:"+analyzer);
        if(!EndSymbol.class.isAssignableFrom(SymbolList.getHead().getClass())&&
                (analyzer.peek()==null || EndSymbol.class.isAssignableFrom(analyzer.peek().getClass()))){
            handleError("检测到额外输入或语法规则缺失");
        }else if(EndSymbol.class.isAssignableFrom(SymbolList.getHead().getClass())){
            //如果是符号串为$而堆栈不为空，尽一切可能利用推空的表达式消除堆栈中的内容
            if(analyzer.peek()==null||EndSymbol.class.isAssignableFrom(analyzer.peek().getClass())) {
                return startSymbol;//最好的情况
            }
            while (true){
                System.out.println("PEEK:"+analyzer.peek());
                if (analyzer.peek()==null||EndSymbol.class.isAssignableFrom(analyzer.peek().getClass()))
                    break;
                if (Objects.equals(analyzer.peek(), new Epsilon())) {
                    analyzer.pop();
                    continue;
                }
                Symbol it = analyzer.peek();
                try {
                    for (LinkList l : synTacs.get(it)) {
                        if(l.getHead().equals(new Epsilon())){
                            analyzer.pop();
                        }
                    }
                }catch (Exception e){
                    handleError("剩余无法推空的表达式");
                }
            }
        }
        return startSymbol;
    }


    public void reversePush(Deque<Symbol> analyzer, LinkList linkList) {
        Deque<Symbol> temp = new LinkedList<>();
        linkList.loop(temp::push);
        while(!temp.isEmpty()){
            analyzer.push(temp.pop());
        }
    }


    private void handleError(String s) {
        System.out.println("出现错误:\n"+s);
    }

    public SymbolNode analyzeAndConstructTree(String input,Symbol startSymbol){
        createDecisionMap();
        LinkList SymbolList = StringSymbolTransfer.transfer(input);
        setStartSymbol(startSymbol);
        Deque<Symbol> analyzer = new LinkedList<>();
        analyzer.push(new EndSymbol());
        analyzer.push(startSymbol);

        Deque<SymbolNode> treeStack = new LinkedList<>();
        SymbolNode startNode = new SymbolNode(startSymbol);
        treeStack.push(startNode);

        while  (analyzer.peek()!=null&&
                !EndSymbol.class.isAssignableFrom(analyzer.peek().getClass())&&
                !EndSymbol.class.isAssignableFrom(SymbolList.getHead().getClass()))
        {
            System.out.println("树栈为:"+treeStack);
            System.out.println("分析栈为:"+analyzer);
            SymbolNode peek = treeStack.peek();//先不要pop
            Symbol symbol1 = analyzer.pop();
            Symbol symbol2 = SymbolList.getHead();
            if(Terminal.class.isAssignableFrom(symbol1.getClass())){//栈顶是终结符
                if(symbol1.equals(symbol2)){//匹配操作
                    System.out.println("匹配操作"+symbol1);
                    SymbolList.pop();
                    assert peek != null;
                    //peek.addChild(new SymbolNode(symbol1));//终结符不pop ,为什么这行去掉就对了?
                    continue;
                }else{//出现错误
                    handleError("终结符不匹配");
                }
            } else if (symbol1.equals(new Epsilon())) {//Epsilon
                System.out.println("遍历到Epsilon");
                continue;
            }
            //推导:
            System.out.println("推导操作");
            LinkList thisList = daMap.getProduction(symbol1,symbol2).get(symbol1);
            reversePushForConstruct(analyzer,treeStack,thisList);
        }
        System.out.println("主循环结束处理后事");
        System.out.println("此时的分析栈:"+analyzer);
        if(!EndSymbol.class.isAssignableFrom(SymbolList.getHead().getClass())&&
                (analyzer.peek()==null || EndSymbol.class.isAssignableFrom(analyzer.peek().getClass()))){
            handleError("检测到额外输入或语法规则缺失");
        }else if(EndSymbol.class.isAssignableFrom(SymbolList.getHead().getClass())){
            //如果是符号串为$而堆栈不为空，尽一切可能利用推空的表达式消除堆栈中的内容
            if(analyzer.peek()==null||EndSymbol.class.isAssignableFrom(analyzer.peek().getClass())) {
                return startNode;//最好的情况
            }
            while (analyzer.peek() != null && !EndSymbol.class.isAssignableFrom(analyzer.peek().getClass())) {
                if (Objects.equals(analyzer.peek(), new Epsilon())) {//若为空
                    analyzer.pop();
                    treeStack.pop();
                    continue;
                }
                Symbol it = analyzer.peek();
                //System.out.println("peek:"+it);
                try {
                    for (LinkList l : synTacs.get(it)) {
                        if (l.getHead().equals(new Epsilon())) {
                            analyzer.pop();
                            //System.out.println("pop"+ treeStack.pop());
                        }
                    }
                } catch (Exception e) {
                    handleError("剩余无法推空的表达式");
                }
            }
        }
        System.out.println(treeStack);
        printTree(startNode,0);
        return startNode;
    }


    // 修改reversePush方法以接受SymbolNode，并且能够构建语法树
    //||(linkList.size()==1 && linkList.getHead().equals(new Epsilon()))
    public void reversePushForConstruct(Deque<Symbol> analyzerStack, Deque<SymbolNode> treeStack, LinkList linkList) {
        // 如果是空产生式，不进行处理
        if (linkList.size() == 1 && linkList.getHead().equals(new Epsilon())) {
            analyzerStack.push(linkList.getHead());
            return;
        }
        SymbolNode parent = treeStack.peek(); // 使用peek代替pop
        if (linkList.size() == 1 && Terminal.class.isAssignableFrom(linkList.getHead().getClass())) {
            // 如果产生式是一个终结符，我们不需要将它推入treeStack
            Symbol symbol = linkList.getHead();
            SymbolNode childNode = new SymbolNode(symbol);
            parent.addChild(childNode); // 直接添加终结符节点作为子节点
            analyzerStack.push(symbol); // 将终结符推入分析栈
            return;
        }

        // 对于非终结符产生式的处理
        Deque<SymbolNode> temp = new LinkedList<>();
        linkList.loop(s -> {
            SymbolNode newNode = new SymbolNode(s);
            temp.push(newNode); // 使用SymbolNode代替Symbol
            parent.addChild(newNode); // 将新节点作为子节点添加到父节点中
            System.out.println(parent + "add了:" + newNode);
        });
        while (!temp.isEmpty()) {
            SymbolNode child = temp.pop();
            analyzerStack.push(child.getSymbol());
            // 对于非终结符，我们需要同时更新分析栈和树栈
            if (!Terminal.class.isAssignableFrom(child.getSymbol().getClass())) {
                treeStack.push(child); // 推入树栈
            }
        }
    }
    public void printTree(SymbolNode node, int depth) {
        StringBuilder indent = new StringBuilder();
        indent.append("  ".repeat(Math.max(0, depth))); // 缩进，表示树的层级

        // 打印当前节点
        System.out.println(indent + (node.getSymbol() != null ?
                node.toString() : "null"));

        // 递归打印每个子节点
        for (SymbolNode child : node.getChildren()) {
            printTree(child, depth + 1);
        }
    }
}
