package old;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UnitTest {
    @Test
    public void test0_Scanner(){
        String text;
        Scanner sc = new Scanner(System.in);
        StringBuilder stb = new StringBuilder();
        while(true){
            System.out.println("---------------------");
            text = sc.next();
            if(text.matches("\\d+")){
                System.out.println(text);
            } else if (text.matches("y")) {
                System.out.println("yyy");
                break;
            } else if (text.matches("yu")) {
                System.out.println("syuu");
            } else {
                stb.append(text).append(" ");
            }
        }
        System.out.println(stb);
    }
    @Test
    public void test_1sout(){
        System.out.println("1\n2");
    }
    @Test
    public void test_2nfa(){
        Trucs nfa = new Trucs("sab");
        ArrayList<ArrayList<Integer>[]> NFA  = new ArrayList<>();
        ArrayList<Integer>[] a1 = new ArrayList[]{new ArrayList<>(List.of(1,7)),null,null};
        ArrayList<Integer>[] a2 = new ArrayList[]{new ArrayList<>(List.of(2,4,6)),null,null};
        ArrayList<Integer>[] a3 = new ArrayList[]{null,new ArrayList<>(List.of(3)),null};
        ArrayList<Integer>[] a4 = new ArrayList[]{new ArrayList<>(List.of(6)),null,null};
        ArrayList<Integer>[] a5 = new ArrayList[]{null,null,new ArrayList<>(List.of(5))};
        ArrayList<Integer>[] a6 = new ArrayList[]{new ArrayList<>(List.of(6)),null,null};
        ArrayList<Integer>[] a7 = new ArrayList[]{null,new ArrayList<>(List.of(8)),null};
        ArrayList<Integer>[] a8 = new ArrayList[]{null,null,new ArrayList<>(List.of(9))};
        ArrayList<Integer>[] a9 = new ArrayList[]{null,null,null};
        NFA.add(a1);
        NFA.add(a2);
        NFA.add(a3);
        NFA.add(a4);
        NFA.add(a5);
        NFA.add(a6);
        NFA.add(a7);
        NFA.add(a8);
        NFA.add(a9);
        System.out.println(nfa.input_count);
        System.out.println(nfa.signal_query_map);
        System.out.println(nfa.closure(NFA));
        System.out.println(nfa.create(NFA));
    }
}
