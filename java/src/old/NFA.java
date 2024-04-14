package old;

import java.util.*;

public class NFA {

    // 定义NFA状态转移函数
    private static Map<Integer, Map<Character, Set<Integer>>> nfaTransitions = new HashMap<>();

    // 定义DFA状态转移函数
    private static Map<Set<Integer>, Map<Character, Set<Integer>>> dfaTransitions = new HashMap<>();

    // 定义NFA的初始状态
    private static int nfaInitialState;

    // 定义NFA的终态集合
    private static Set<Integer> nfaFinalStates = new HashSet<>();

    // 定义DFA的初始状态
    private static Set<Integer> dfaInitialState = new HashSet<>();

    // 定义DFA的终态集合
    private static Set<Set<Integer>> dfaFinalStates = new HashSet<>();

    // 添加NFA状态转移
    private static void addNFATransition(int from, char symbol, int to) {
        nfaTransitions.computeIfAbsent(from, k -> new HashMap<>());
        nfaTransitions.get(from).computeIfAbsent(symbol, k -> new HashSet<>());
        nfaTransitions.get(from).get(symbol).add(to);
    }

    // 添加NFA的终态
    private static void addNFAFinalState(int state) {
        nfaFinalStates.add(state);
    }

    // 计算ε-闭包
    private static Set<Integer> epsilonClosure(Set<Integer> states) {
        Set<Integer> closure = new HashSet<>(states);
        Deque<Integer> stack = new LinkedList<>(states);
        while (!stack.isEmpty()) {
            int state = stack.pop();
            if (nfaTransitions.containsKey(state) && nfaTransitions.get(state).containsKey('ε')) {
                for (int nextState : nfaTransitions.get(state).get('ε')) {
                    if (!closure.contains(nextState)) {
                        closure.add(nextState);
                        stack.push(nextState);
                    }
                }
            }
        }
        /*Queue<Integer> queue = new LinkedList<>(states);
        while (!queue.isEmpty()) {
            int state = queue.poll();
            if (nfaTransitions.containsKey(state) && nfaTransitions.get(state).containsKey('ε')) {
                for (int nextState : nfaTransitions.get(state).get('ε')) {
                    if (!closure.contains(nextState)) {
                        closure.add(nextState);
                        queue.add(nextState);
                    }
                }
            }
        }*/
        return closure;
    }

    // 构造DFA转移表
    private static void constructDFATransitions() {
        Queue<Set<Integer>> queue = new LinkedList<>();
        Map<Set<Integer>, Integer> dfaStateIndices = new HashMap<>();
        queue.add(epsilonClosure(dfaInitialState));
        dfaStateIndices.put(epsilonClosure(dfaInitialState), 0);
        while (!queue.isEmpty()) {
            Set<Integer> dfaState = queue.poll();
            for (char symbol : new char[]{'a', 'b'}) {
                Set<Integer> nextStates = new HashSet<>();
                for (int nfaState : dfaState) {
                    if (nfaTransitions.containsKey(nfaState) && nfaTransitions.get(nfaState).containsKey(symbol)) {
                        nextStates.addAll(nfaTransitions.get(nfaState).get(symbol));
                    }
                }
                Set<Integer> nextStateClosure = epsilonClosure(nextStates);
                if (!nextStateClosure.isEmpty() && !dfaStateIndices.containsKey(nextStateClosure)) {
                    queue.add(nextStateClosure);
                    dfaStateIndices.put(nextStateClosure, dfaStateIndices.size());
                }
                dfaTransitions.computeIfAbsent(dfaState, k -> new HashMap<>());
                dfaTransitions.get(dfaState).put(symbol, nextStateClosure);
            }
        }
        // 确定DFA的终态
        for (Set<Integer> dfaState : dfaTransitions.keySet()) {
            if (!Collections.disjoint(dfaState, nfaFinalStates)) {
                dfaFinalStates.add(dfaState);
            }
        }
    }

    public static void main(String[] args) {
        // 添加NFA的状态转移
        addNFATransition(0, 'ε', 1);
        addNFATransition(0, 'ε', 7);
        addNFATransition(1, 'ε', 2);
        addNFATransition(1, 'ε', 4);
        addNFATransition(1, 'ε', 6);
        addNFATransition(2, 'a', 3);
        addNFATransition(4, 'b', 5);
        addNFATransition(3, 'ε', 6);
        addNFATransition(5, 'ε', 6);
        addNFATransition(7, 'a', 8);
        addNFATransition(8, 'b', 9);


        // 设置NFA的初始状态
        nfaInitialState = 0;

        // 添加NFA的终态
        addNFAFinalState(9);

        // 设置DFA的初始状态
        dfaInitialState.add(0);

        // 构造DFA转移表
        constructDFATransitions();

        // 打印DFA状态转移函数
        for (Set<Integer> fromState : dfaTransitions.keySet()) {
            for (char symbol : dfaTransitions.get(fromState).keySet()) {
                Set<Integer> toState = dfaTransitions.get(fromState).get(symbol);
                System.out.println("Transition from " + fromState + " on symbol " + symbol + " to " + toState);
            }
        }

        // 打印DFA终态
        System.out.println("DFA final states: " + dfaFinalStates);
    }
}
