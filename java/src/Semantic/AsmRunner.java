package Semantic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class AsmRunner {
    public AsmRunner(String path) throws Exception {
        new AsmSpawner(path);
        ArrayList<String> signal_chart = AsmSpawner.signal_chart;
        System.out.println("signal_chart="+signal_chart);
        Path filePath = Paths.get(path);
        Path folderPath = filePath.getParent();
        ArrayList<String> asm_code = read(folderPath+"/asm.txt");
        run(asm_code,signal_chart);
    }

    public static void main(String[] args) throws Exception {
        AsmSpawner.main(args);
        ArrayList<String> signal_chart = AsmSpawner.signal_chart;
        System.out.println("signal_chart="+signal_chart);
        ArrayList<String> asm_code = read("./resource/asm.txt");
        run(asm_code,signal_chart);
    }
    public static ArrayList<String> read(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        return new ArrayList<>(lines);
    }
    public static void run(ArrayList<String> asm_code, ArrayList<String> signal_chart){
        HashMap<String,Integer> code_address = new HashMap<>();
        HashMap<Integer,Integer>  var_val = new HashMap<>();//变量表
        for (int i = 0; i < signal_chart.size(); i++) {
            var_val.put(i,0);
        }
        LinkedList<Integer> stack = new LinkedList<>();
        for (int i = 0; i < asm_code.size(); i++) {//编制索引
            String e = asm_code.get(i);
            if(e.matches("Label\\d+:")){
                e = e.substring(0,e.length()-1);
                code_address.put(e,i);
            }
        }
        System.out.println("索引:"+code_address);
        boolean flag = false;
        for (int i = 0; i < asm_code.size(); i++) {
            String command = asm_code.get(i);
            if(!command.matches("Label\\d+:")){
                String[] operation = command.split(" ");
                System.out.println(operation[1]);
                switch (operation[1]){
                    case "IN" -> in(stack);
                    case "OUT" -> out(stack);
                    case "POP" -> stack.pop();
                    // 有操作数的指令，需要传递额外的参数
                    case "STO" -> sto(stack, operation[2], var_val); // STO需要更新var_val中变量的值
                    case "LOAD" -> load(stack, operation[2], var_val); // LOAD需要从var_val获取变量的值
                    case "LOADI" -> loadi(stack, operation[2]); // LOADI直接将立即数压入栈中
                    case "ADD" -> add(stack);
                    case "SUB" -> sub(stack);
                    case "MUL" -> mul(stack);
                    case "DIV" -> div(stack);
                    case "GT" -> {
                        int right = stack.pop();
                        int left = stack.pop();
                        flag = left>right;
                    }
                    case "GE" -> {
                        int right = stack.pop();
                        int left = stack.pop();
                        flag = left>=right;
                    }
                    case "LT" ->  {
                        int right = stack.pop();
                        int left = stack.pop();
                        flag = left<right;
                    }
                    case "LE" -> {
                        int right = stack.pop();
                        int left = stack.pop();
                        flag = left<=right;
                    }
                    case "EQ" ->  {
                        int right = stack.pop();
                        int left = stack.pop();
                        flag = left==right;
                    }
                    case "NE" -> {
                        int right = stack.pop();
                        int left = stack.pop();
                        flag = left!=right;
                    }
                    // 跳转指令，需要传递code_address来确定跳转的目标地址
                    case "BR" -> //无条件跳转
                            i = code_address.get(operation[2]);
                    case "BRF" -> {
                        if (!flag){
                            i = code_address.get(operation[2]);
                        }
                    }
                }
            }
        }
    }

    private static void add(LinkedList<Integer> stack) {
        int right = stack.pop();
        int left = stack.pop();
        stack.push(left + right);
    }

    private static void sub(LinkedList<Integer> stack) {
        int right = stack.pop();
        int left = stack.pop();
        stack.push(left - right);
    }

    private static void mul(LinkedList<Integer> stack) {
        int right = stack.pop();
        int left = stack.pop();
        stack.push(left * right);
    }

    private static void div(LinkedList<Integer> stack) {
        int right = stack.pop();
        int left = stack.pop();
        if (right != 0) {
            stack.push(left / right);
        } else {
            // 处理除数为0的情况
            System.out.println("Error: Division by zero.");
        }
    }

    private static void loadi(LinkedList<Integer> stack, String num) {
        stack.push(Integer.valueOf(num));
    }

    private static void load(LinkedList<Integer> stack, String var, HashMap<Integer,Integer> var_val) {
        stack.push(var_val.get(Integer.parseInt(var)));
    }

    private static void sto(LinkedList<Integer> stack, String address, HashMap<Integer,Integer> var_val) {
        if(!stack.isEmpty()) {
            var_val.put(Integer.valueOf(address),stack.peek());
        }
    }

    private static void out(LinkedList<Integer> stack) {
        System.out.println(stack.peek());
    }

    private static void in(LinkedList<Integer> stack) {
        System.out.println("请输入一个整数:");
        Scanner sc = new Scanner(System.in);
        stack.push(sc.nextInt());
    }
}
