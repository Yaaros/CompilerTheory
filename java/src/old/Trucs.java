package old;

import java.util.*;

/** 最小子集构造法
 * 目的：把NFA转化为DFA
 * 空边转换：认为转换过程等价
 * 例：0-(null)->1,1-(null)->2,1-(null)->4,0-(null)->7
 * 则说明0、1、2、4、7是等价的
 * */
public class Trucs {
    /**
     * input的每一项的第一项指的是如果输入是null它会迁移到哪个状态
     * input:[
     * [[1,7],null,null],
     * [[2,4,6],null,null],
     * [null,[3],null],
     * [[6],null,null],
     * [null,null,[5]],
     * [[6],null,null],
     * [null,[8],null],
     * [null,null,[9]],
     * []
     * ]
     */
    int temp_state = -1;//暂存状态量
    int input_count;//输入符号的类型总数
    HashMap<Integer,String> signal_query_map = new HashMap<>();
    public Trucs(String signals){
        input_count = signals.length();
        String[] arr = signals.split("");
        int i = 0;
        for (String s : arr) {
            signal_query_map.put(i,s);
            i++;
        }
    }
    public Set<Integer> closure(ArrayList<ArrayList<Integer>[]> NFA){
        Stack<Integer> stack = new Stack<>();
        Set<Integer> Close = new HashSet<>();
        int total_state_num = NFA.size();
        for (int i = 0; i < total_state_num; i++) {
            stack.push(i);
        }
        while(!stack.isEmpty()){
            int t = stack.pop();
            Close.add(t);
            for (int u = 0; u < total_state_num; u++) {
                ArrayList<Integer> v = NFA.get(t)[0];
                if(v!=null){
                    if(NFA.get(t)[0].contains(u)){
                        if(!Close.contains(u)){
                            stack.push(u);
                        }
                    }
                }
            }
        }
        return Close;
    }
    public boolean hasUnlabeled(boolean[] state_labels){
        for (int i = 0; i < state_labels.length; i++) {
            boolean flag = state_labels[i];
            if(!flag){
                temp_state = i;
                return true;
            }
        }
        return false;
    }
    public Set<Integer> move(ArrayList<ArrayList<Integer>[]> NFA, int origin_state, int input){
        if (NFA.get(origin_state)[input] == null) {
            return null;
        }
        return new HashSet<>(NFA.get(origin_state)[input]);
    }
    public Set<Integer> create(ArrayList<ArrayList<Integer>[]> NFA){
        Set<Integer> Dstates = closure(NFA);
        boolean[] state_labels = new boolean[NFA.size()];
        while(hasUnlabeled(state_labels)){
            state_labels[temp_state] = true;
            for (int i = 0; i < input_count; i++) {
                Set<Integer> U = move(NFA,temp_state,i);
                if (U != null){
                    Dstates.addAll(U);
                }
            }
        }
        return Dstates;
    }
}
