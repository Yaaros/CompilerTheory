package Lex.utils;

import java.util.*;

public class LexTree {
    public Node root;

    public HashSet<String> symbolList = new HashSet<>();

    public int index;
    public LexTree(String regex) {
        String[] arr = regex.split("");
        root = read(arr,0,arr.length);
    }
    public void preOrder(){
        preOrder(root);
    }
    public void preOrder(Node n){
        if(n==null){
            return;
        }
        System.out.println(n);
        preOrder(n.left);
        preOrder(n.right);
    }
    public void inOrder() {
        inOrder(root);
    }
    public void inOrder(Node n){
        if(n==null){
            return;
        }
        inOrder(n.left);
        System.out.println(n);
        inOrder(n.right);
    }
    public StateGraph postOrder() {
        Deque<Node> stack = new LinkedList<>();
        Deque<StateGraph> states = new LinkedList<>();
        Node curr = root;
        Node lastVisited = null;
        index = 1;
        while (curr != null || !stack.isEmpty()) {
            if (curr != null) {
                stack.push(curr);
                curr = curr.left;
            } else {
                Node peekNode = stack.peek();
                if (peekNode.right != null && lastVisited != peekNode.right) {
                    curr = peekNode.right;
                } else {
                    stack.pop();
                    StateGraph currGraph = getCurrGraph(states, peekNode);
                    if(currGraph!=null){
                        states.push(currGraph);
                    }
                    lastVisited = peekNode;
                }
            }
            if(states.peek()!=null){
                index = states.peek().correctIndex();
            }
        }
        assert states.peek() != null;
        states.peek().change_index();
        return states.peek();
    }


    private StateGraph getCurrGraph(Deque<StateGraph> states, Node peekNode) {
        StateGraph currGraph = new StateGraph(symbolList,index);
        switch (peekNode.symbol) {//此symbol非彼symbol
            case "~" -> {//连接
                currGraph = states.pop();
                StateGraph lastGraph = states.pop();
                lastGraph.tailConnect(currGraph);
                currGraph = lastGraph;
                index--;
            }//连接
            case "|" -> { //并
                StateGraph rightGraph = states.pop();
                StateGraph leftGraph = states.pop();
                leftGraph.combine(rightGraph);
                currGraph = leftGraph;
            }
            case "^" ->{//单目操作符
                currGraph = states.pop();
                String op = peekNode.right.symbol;
                switch (op){
                    case "*"-> currGraph.closure();
                    case "+"-> currGraph.positive_closure();
                    case "?"-> currGraph.question_operate();
                }
                states.push(currGraph);
            }
            case "*","+","?"-> currGraph = null;//不添加新的图
            default -> { //字母也要直接进行连接
                currGraph.tailConnect(++index, peekNode.symbol);
                index++;
            }
        }
        return currGraph;
    }

    private Node read(String[] arr,int start,int end) {
        Deque<Node> stack = new LinkedList<>();
        Node curr;
        for (int i = start; i < end; i++) {
            String c = arr[i];
            curr = new Node(c);
            if(c.isEmpty()||arr[i].isBlank()){
                continue;
            }
            if(stack.peek()==null) {
                if(!arr[i].equals("(")){
                    stack.push(curr);
                    continue;
                }
            }
            if(c.matches("[a-zA-Z]")){//处理字母
                symbolList.add(c);
                if (isOperator(stack)){//栈顶是操作符
                    Node op = stack.pop();
                    if(op.isLeaf){
                        op.left = stack.pop();
                        op.right = curr;
                        op.isLeaf = false;
                        stack.push(op);
                    }else{
                        Node now = new Node("~",op,curr);
                        stack.push(now);
                    }

                } else if(stack.peek().symbol.matches("[a-zA-Z]")){//栈顶是字母
                    Node now = new Node("~",stack.pop(),curr);
                    stack.push(now);
                }
            } else if (c.matches("[|~^]")) {//处理双目操作符,^标识单目操作符
                stack.push(curr);
            } else if (c.matches("[*+?]")) {//处理单目操作符
                Node p = stack.pop();
                stack.push(new Node("^",p,curr));
            } else if (c.equals("(")) {
                ArrayList<String> newArrT = new ArrayList<>();
                int start1 = i+1;
                while(i<arr.length&&!arr[i].equals(")")){
                    i++;
                }
                int end1 = i;
                if(end1>start1){
                    Node n = read(arr,start1,end1);
                    n.isLeaf = false;
                    stack.push(n);
                }
            }
        }
        return stack.pop();
    }

    private boolean isOperator(Deque<Node> stack) {
        Node peek = stack.peek();
        if(peek!=null){
            return peek.symbol.matches("[*|+?~^]");
        }
        return false;
    }

    private static class Node{
        String symbol;
        Node left;
        Node right;

        @Override
        public String toString() {
            return "Node{" +
                    "symbol='" + symbol + '\'' +
                    ", isLeaf=" + isLeaf +
                    '}';
        }

        boolean isLeaf = true; //如果不是叶子，运算符则代表了一个式子

        public Node(String symbol) {
            this.symbol = symbol;
        }

        public Node(String symbol, Node left, Node right) {
            this.symbol = symbol;
            this.left = left;
            this.right = right;
            this.isLeaf = false;
        }

        public Node(String symbol, Node left) {
            this.symbol = symbol;
            this.left = left;
            this.right = null;
            this.isLeaf = false;
        }
    }
}
