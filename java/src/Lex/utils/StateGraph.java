package Lex.utils;

import java.util.*;

public class StateGraph {
    ArrayList<State> StateList = new ArrayList<>();
    HashSet<String> SymbolList;
    ArrayList<Edge> EdgeList = new ArrayList<>();
    State StartState;
    State EndState;

    public StateGraph(ArrayList<State> newStates, ArrayList<Edge> newEdges, HashSet<String> symbolList) {
        StateList = newStates;
        EdgeList = newEdges;
        SymbolList = symbolList;
        StartState = newStates.get(0);
        EndState = newStates.get(StateList.size()-1);
    }

    public State getStartState() {
        return StartState;
    }

    public State getEndState() {
        return EndState;
    }


    public String getStateList() {
        StringBuilder stb = new StringBuilder();
        for (State s : StateList) {
            stb.append(s.num).append(":").append("\n").append(s.edges).append("\n");
        }
        return stb.toString();
    }

    public String getEdgeList() {
        StringBuilder stb = new StringBuilder();
        for (Edge s : EdgeList) {
            stb.append(s).append("\n");
        }
        return stb.toString();
    }
    public HashSet<String> getSymbolList() {
        return SymbolList;
    }
    State TempState; //在combine的时候暂时用得上

    public StateGraph(HashSet<String> SymbolList) {//无状态参数 初始化
        this.StartState = new State((int)(Math.random()*100));
        this.EndState = this.StartState;
        this.StateList.add(this.StartState);
        this.SymbolList = SymbolList;
    }

    public StateGraph(HashSet<String> SymbolList,int index) {//无状态参数 初始化
        this.StartState = new State(index);
        this.EndState = this.StartState;
        this.StateList.add(this.StartState);
        this.SymbolList = SymbolList;
    }
    public StateGraph headConnect(int newStart){
        return headConnect(newStart,"~");
    }
    public StateGraph headConnect(int newStart,String symbol){
        boolean flag = false;//这个newState是不是已存在的
        State p = new State(newStart);
        this.TempState = p;
        for (State state : this.StateList) {
            if(state.getNum() == newStart){//如果有newStart存在则直接把已存在的拿出来作连接
                flag = true;
                p = state;
                break;
            }
        }
        this.EdgeList.add(new Edge(p,this.StartState,symbol));
        this.StartState = p; //这个图内部有环但可以保证首末位不连成环才可以这么写
        if(!flag){
            this.StateList.add(p);
        }
        return this;
    }
    public StateGraph tailConnect(StateGraph graph){
        State connecting_node = this.EndState;
        this.EndState = graph.EndState;
        ArrayList<Edge> existingEdges = graph.EdgeList;
        ArrayList<State> existingStates = graph.StateList;
        HashSet<Edge> removedEdges = new HashSet<>();
        existingStates.removeIf(s -> s == graph.StartState);
        for (State s : existingStates) {
            s.num--;
        }
        Iterator<Edge> iter = existingEdges.iterator();//只有迭代器遍历集合才能避免并发异常
        while (iter.hasNext()){
            Edge e = iter.next();
            if(e.start == graph.StartState){
                iter.remove();
                removedEdges.add(e);
            }
        }
        for (Edge e : existingEdges) {
            if(e.start == graph.StartState){
                existingEdges.remove(e);
                removedEdges.add(e);
            }
        }
        this.EdgeList.addAll(existingEdges);
        this.StateList.addAll(existingStates);
        for (Edge e : removedEdges) {
            String symbol = e.symbol;
            State end = e.end;
            this.EdgeList.add(new Edge(connecting_node,end,symbol));
        }
        return this;
    }
    public StateGraph tailConnect(int newEnd){
        return tailConnect(newEnd,"~");
    }
    public StateGraph tailConnect(int newEnd,String symbol){
        boolean flag = false;//这个newState是不是已存在的
        State p = new State(newEnd);
        this.TempState = p;
        for (State state : this.StateList) {
            if(state.getNum() == newEnd){//如果有newEnd存在则直接把已存在的拿出来作连接
                flag = true;
                p = state;
                break;
            }
        }
        this.EdgeList.add(new Edge(this.EndState,p,symbol));
        this.EndState = p; //这个图内部有环但可以保证首末位不连成环才可以这么写
        if(!flag){
            this.StateList.add(p);
        }
        return this;
    }


    public StateGraph combine(StateGraph g){
        int i = this.StartState.getNum()-1;
        int f = g.EndState.getNum()+1;
        return combine(g, i, f);
    }

    public StateGraph combine(StateGraph g,int i,int f){//两个图的并
        for (State state : g.StateList) {
            boolean in = false;
            for (State state1 : this.StateList) {
                if(state==state1){
                    in = true;
                    break;
                }
            }
            if(!in){
                this.StateList.add(state);
            }
        }
        for (Edge edge : g.EdgeList) {
            boolean in = false;
            for (Edge e : this.EdgeList) {
                if(edge==e){
                    in = true;
                    break;
                }
            }
            if(!in){
                this.EdgeList.add(edge);
            }
        }
        this.headConnect(i); //i->this.start
        this.EdgeList.add(new Edge(TempState,g.StartState)); //i->g.start
        this.tailConnect(f); //i->this.end
        this.EdgeList.add(new Edge(g.EndState,TempState));  //i->g.end
        return this;
    }
    public StateGraph closure(){
        int i = this.StartState.getNum()-1;
        int f = this.EndState.getNum()+1;
        return closure(i,f);
    }
    public StateGraph positive_closure(){
        int i = this.StartState.getNum()-1;
        int f = this.EndState.getNum()+1;
        return positive_closure(i,f);
    }
    public StateGraph closure(int i,int f){
        this.positive_closure(i,f);
        this.EdgeList.add(new Edge(this.StartState,this.EndState));
        return this;
    }
    public StateGraph positive_closure(int i,int f){
        this.EdgeList.add(new Edge(this.EndState,this.StartState));
        this.headConnect(i);
        this.tailConnect(f);
        return this;
    }
    public StateGraph change_index() {
        int delta = -StartState.getNum();
        for (State s : StateList) {
            s.isChecked = false;
        }
        for (Edge e : EdgeList) {
            if(!e.start.isChecked){
                e.start.num+=delta;
                e.start.isChecked = true;
            }
            if(!e.end.isChecked){
                e.end.num+=delta;
                e.end.isChecked = true;
            }
        }
        sort(EdgeList);
        StateList.sort(Comparator.comparingInt(a -> a.num));
        for (Edge e : EdgeList) {
            e.start.edges.add(e);
        }
        return this;
    }

    private void sort(ArrayList<Edge> edgeList) {
        edgeList.sort((e1, e2) -> {
            int startComparison = Integer.compare(e1.start.num, e2.start.num);
            if (startComparison != 0) return startComparison;
            return Integer.compare(e1.end.num, e2.end.num);
        });
    }

    public int correctIndex(){
        return this.EndState.getNum() + 1;
    }

    public StateGraph programIndex(){
        for (State s : StateList) {
            s.isChecked = false;
        }
        Queue<State> q = new LinkedList<>();
        int index = 0;
        q.offer(this.StartState);
        while (!q.isEmpty()){
            State s = q.poll();
            s.isChecked = true;
            s.setNum(index);
            index++;
            for(Edge e : EdgeList){
                if(e.start==s&&(!e.end.isChecked)){
                    q.offer(e.end);
                }
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return "<<<"+"StartState"+"="+StartState+","
                +"EndState"+"="+EndState+",\n"
                +"EdgeList"+"="+EdgeList+",\n"
                +"StateList"+"="+StateList+">>>\n";
    }

    //这个操作没有实现,即"?"
    public StateGraph question_operate() {
        return this;
    }

    public StateGraph createDFA() {
        for (State s : this.StateList) {
            s.isChecked = false;
        }
        String[] sys = new String[]{"A","B","C","D","E","F","G"};
        int index = 0;
        ArrayList<State> newStates = new ArrayList<>();
        ArrayList<Edge> newEdges = new ArrayList<>();
        Map<Set<State>,State> searchMap = new HashMap<>();//old & new States 对应
        Deque<Set<State>> stack = new LinkedList<>();
        HashSet<State> h = new HashSet<>();
        h.add(StateList.get(0));
        Set<State> NFAStartClosure = nullClosure(h);
        DFAState DFAStartState = new DFAState(index,sys[index]);
        newStates.add(DFAStartState);
        searchMap.put(NFAStartClosure,DFAStartState);
        stack.push(NFAStartClosure);
        while(!stack.isEmpty()){
            Set<State> currSet = stack.pop();
            State currState = searchMap.get(currSet);
            for(String symbol : SymbolList){
                if(symbol.equals("~"))continue;//skip 空边转换
                Set<State> nextSet = new HashSet<>();
                for(State state : currSet){
                    for(Edge e : state.edges){
                        if(e.symbol.equals(symbol)){
                            HashSet<State> h2 = new HashSet<>();
                            h2.add(e.end);
                            nextSet.addAll(nullClosure(h2));
                        }
                    }
                }
                if (!nextSet.isEmpty()) {
                    State dfaNextState = searchMap.get(nextSet);
                    if (dfaNextState == null) {
                        index++;
                        dfaNextState = new DFAState(index,sys[index]); // create a new State for DFA
                        newStates.add(dfaNextState);
                        searchMap.put(nextSet, dfaNextState);
                        stack.push(nextSet);
                    }
                    Edge e = new Edge(currState, dfaNextState, symbol);
                    currState.edges.add(e);
                    newEdges.add(e);
                }
            }
        }
        return new StateGraph(newStates,newEdges,SymbolList);
    }
    private Set<State> nullClosure(Set<State> initial){
        Set<State> closure = initial;
        Deque<State> stack = new LinkedList<>(closure);
        while (!stack.isEmpty()){
            State peek = stack.pop();
            for (Edge e : peek.edges) {
                if(e.symbol.equals("~")){//空边
                    if(!closure.contains(e.end)){//不在closure中
                        closure.add(e.end);
                        stack.push(e.end);
                    }
                }
            }
        }
        return closure;
    }

    public StateGraph minimize(){
        System.out.println("add前:"+this);
        addDeathState();
        System.out.println("add后:"+this);
        Map<State,Map<String,State>> transitions = createTransitions();
        Set<Set<State>> divideState = new HashSet<>();
        Set<State> unReceiver = new HashSet<>();
        Set<State> Receiver = new HashSet<>();
        Receiver.add(this.EndState);
        for (State s : this.StateList) {
            if(s!=this.EndState){
                unReceiver.add(s);
            }
        }
        divideState.add(unReceiver);
        divideState.add(Receiver);
        Set<Set<State>> nextSet = new HashSet<>();
        while(!nextSet.equals(divideState)){
            nextSet = new HashSet<>();
            for (Set<State> subSet : divideState) {
                Set<Set<State>> temp = divide(subSet,transitions);
                nextSet.addAll(temp);
            }
            boolean isAllOne = true;
            for (Set<State> stateSet : nextSet) {
                if (stateSet.size() != 1) {
                    isAllOne = false;
                    break;
                }
            }
            divideState = nextSet;
            if(isAllOne){
                break;
            }
        }
        return this;
    }

    private void addDeathState() {
        int right_num = SymbolList.size()*StateList.size();
        if(EdgeList.size()>=right_num)return;//不用加死状态
        State DEATH = new State(-1);
        Set<Edge> arr = new HashSet<>();
        for (String s : SymbolList) {
            if(!s.equals("~")){
                Edge e = new Edge(DEATH,DEATH,s);
                arr.add(e);
                EdgeList.add(e);
            }
        }
        DEATH.edges = arr;
        Map<State, Map<String, State>> transitions = createTransitions();
        for (State s : StateList) {
            if(!transitions.containsKey(s)){
                for (String a : SymbolList) {
                    Edge e = new Edge(s,DEATH,a);
                    s.edges.add(e);
                    EdgeList.add(e);
                }
            }else{
                for (String a : SymbolList) {
                    if(transitions.get(s).get(a)!=null){
                        Edge e = new Edge(s,DEATH,a);
                        s.edges.add(e);
                        EdgeList.add(e);
                    }
                }
            }
        }
    }

    private Map<State, Map<String, State>> createTransitions() {
        Map<State, Map<String, State>> toReturn = new HashMap<>();
        for (Edge e : this.EdgeList) {
            HashMap<String,State> map = new HashMap<>();
            map.put(e.symbol,e.end);
            toReturn.put(e.start,map);
        }
        return toReturn;
    }

    private Set<Set<State>> divide(Set<State> subSet, Map<State, Map<String, State>> transitions) {
        // 存储新的划分
        Set<Set<State>> newPartitions = new HashSet<>();
        Map<State,Set<State>> setTransition = new HashMap<>();
        for (State s : subSet) {
            // 检查是否已经将状态 s 分配到了某个分区
            if(!setTransition.containsKey(s)){
                // 创建新分区
                Set<State> newPartition = new HashSet<>();
                newPartition.add(s);
                setTransition.put(s,newPartition);
                for (State t : subSet) {
                    if (s!=t&&!setTransition.containsKey(t)) {//s肯定不等于t了
                        boolean allTransitionsMatch = true;
                        // 对于每个可能的输入符号，检查状态 s 和 t 的转换是否结束在相同的分区
                        for (String a : SymbolList) {
                            State sNext = transitions.get(s).get(a);
                            State tNext = transitions.get(t).get(a);
                            if(sNext!=tNext){
                                allTransitionsMatch = false;
                                break;
                            }
                        }
                        // 如果所有转换都匹配，那么将状态 t 加入到与 s 相同的分区
                        if (allTransitionsMatch) {
                            newPartition.add(t);
                            setTransition.put(t,newPartition);
                        }
                    }
                }
                // 将新分区添加到分区集合
                newPartitions.add(newPartition);
            }
        }

        return newPartitions;
    }


    private static class State{

        boolean isChecked = false;
        int num;

        Set<Edge> edges;
        public State(int num) {
            this.num = num;
            edges = new HashSet<>();
        }

        public int getNum() {
            return num;
        }

        @Override
        public String toString() {
            return "State{" +
                    "num=" + num +
                    '}';
        }

        public void setNum(int num) {
            this.num = num;
        }
    }
    private static class Edge{
        State start;
        State end;
        String symbol;

        public Edge(State start,State end,String symbol) {
            this.start = start;
            this.end = end;
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return "["+start+","+symbol+","+end+"]";
        }

        //~表示epsilon，即空连接
        public Edge(State start,State end) {
            this.start = start;
            this.end = end;
            this.symbol = "~";
        }
    }
    private static class DFAState extends State{
        String flag;
        public DFAState(int num,String flag) {
            super(num);
            this.flag = flag;
        }
    }
}
