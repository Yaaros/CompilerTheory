package Semantic;

import Lex.LexAnalyzer;
import Semantic.exception.DuplicateDefinitionException;
import Semantic.exception.UndefinedSymbolException;
import Syntac.TopToBottomParser;
import utils.Block;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


/**翻译规则
 * <declaration_stat> -> int ID {name-def function}
 * <expression_stat> -> <expression> {POP;}
 * <expression> :不完全按照那上面的写，因为我这里把什么additive_expr之类的全省略了
 * <expression> -> 判断expr_stat的儿子是不是assign，如果是，这是一个赋值语句，
 *  <assign> -> Search(thisBlock.children[0])=>address;
 *      遍历，  if(<assign>.child==<expr>):<assign>.child.child.child
 *              根据这里是PLUS,MINUS,MUL,DIV,FACTOR做不同的操作
 *              <factor:i/var>->LOADI i或者LOAD address(即 &var)
 *              STO d
 *  <bool_expr>: 1. if(Block.value!=null):输出代码Block.value,但是要把两个孩子先LOAD或者LOADI了
 *              这里假定机器在识别大于小于号的时候是栈式识别。即栈顶-1和栈顶比较
 *               2. else:如果是null，这不是一个bool_expr，看它的孩子，如果是一个factor执行load系指令
 *              否则应该是一个操作符，压栈它的两个孩子并操作之
 * <if_stat> -> 处理<bool_expr>; BRF(lab1); <compound_list>还是处理<expr_stat>(这个处理时有POP结尾);
 *              BR(lab2); lab1:\n ;else段的内容,还是一个compound_list; lab2:\n 这里其实是结束
 *
 * setLabel()方法：要有一个全局计数器，因为后面要跳转lab3,lab4,...,实质是加一句lab_n:\n
 *
 * <while_stat> ->SetLabel(curr) ;处理<bool_expr>; BRF(lab_curr++);
 *              <compound_list>还是处理<expr_stat>(这个处理时有POP结尾);
 *              BR(lab_curr--)<=这个是SET到TOP位置，也就是说while循环体结束后跳转到开头;SetLabel(curr)1
 * <for_stat> -> 先碰到的是<assign>，是初始化i的句子，处理之；SetLabel(curr);设:改变curr前curr=temp
 *               处理<bool_expr>; BRF Label_temp++ ;BR Label_temp++; SetLabel(temp++);[2,3,4]
 *               然后处理相当于i++的<expr>(含POP);  BR Label_1; SetLabel(3)
 *               然后才处理循环体<compound_list>;BR Label_4;然后才是SetLabel(2),Label_2是结束
 *
 * */

public class AsmSpawner {

    static ArrayList<String> signal_chart = new ArrayList<>();

    static StringBuilder result = new StringBuilder();

    static int LabelSetter = 1;
    static int LabelWriter = 1;

    /**
     *
     * @param path:接收一个带文件名的路径！
     * @return 树根
     * @throws Exception:词法分析和语法分析可能出现的任何错误
     */
    public static Block read(String path) throws Exception {
        Path filePath = Paths.get(path);
        String rawText = Files.readString(filePath);
        System.out.println(rawText);
        Path folderPath = filePath.getParent();//换到父路径
        LexAnalyzer.lexStreamSpawner(folderPath.toString() , rawText);
        return TopToBottomParser.read(path);
    }

    public static void main(String[] args) throws Exception {
        Block root = read("./resource/testLang.txt");
        spawn(root);
        System.out.println(result);
        String filePath = "./resource/asm.txt";
        Path path = Paths.get(filePath);
        Files.writeString(path, result);
    }

    /**
     *
     * @param path:含文件名的path
     * @throws Exception
     */

    public AsmSpawner(String path) throws Exception {
        Block root = AsmSpawner.read(path);
        spawn(root);
        System.out.println(result);
        Path path1 = Paths.get(path);
        Path folderPath = path1.getParent();
        String filePath = folderPath+"/asm.txt";
        Path path2 = Paths.get(filePath);
        Files.writeString(path2, result);
    }
    private static void spawn(Block root){
        if(root==null)return;
        switch (root.type){
                case "program_root"-> root.children.forEach(AsmSpawner::spawn);
                case "declaration_list"->root.children.forEach(AsmSpawner::declare);
                case "statement_list"-> root.children.forEach(AsmSpawner::state);
        }
    }

    /**
     *
     * @param root :一定是一个declaration_stat
     */
    private static void declare(Block root) {
        if(root==null)return;
        try {
            defName(root.value);
        } catch (DuplicateDefinitionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param root :一定是一个statement
     * 扔进switch里
     */
    private static void state(Block root){
        if(root==null)return;
        try{
            switch (root.type){
                case "expression_stat"->expr_stat(root);
                case "if_stat"-> ifProcess(root);
                case "while_stat"-> whileProcess(root);
                case "for_stat"-> forProcess(root);
                case "write_stat"-> writeProcess(root);
                case "read_stat"-> readProcess(root);
            }
        }catch (UndefinedSymbolException e){
            throw new RuntimeException(e);
        }
    }

    private static void readProcess(Block root) throws UndefinedSymbolException {
        Object var = root.children.get(0).children.get(0).children.get(0).value;
        int address = LOOKUP((String)var);
        result.append(" IN\n");
        result.append(" STO ").append(address).append("\n");
        result.append(" POP").append("\n");
    }

    private static void writeProcess(Block root) throws UndefinedSymbolException {
        Object var = root.children.get(0).children.get(0).children.get(0).value;
        int address = LOOKUP((String)var);
        result.append(" LOAD ").append(address).append("\n");
        result.append(" OUT\n");
        result.append(" POP").append("\n");
    }


    private static void defName(Object value) throws DuplicateDefinitionException {
        String s = (String) value;
        for (String o : signal_chart) {
            if(o.equals(s)){
                throw new DuplicateDefinitionException();
            }
        }
        signal_chart.add(s);
    }
    private static void ifProcess(Block root) throws UndefinedSymbolException {
        List<Block> children = root.children;//[bool,compound,compound]
        real_bool_expression(children.get(0));//bool
        addBRF();
        for (int i = 1; i < children.size(); i++) {
            Block statement_list = children.get(i).children.get(0);
            //这里一定是expr_stat
            for (Block expr : statement_list.children) {
                expr_stat(expr);
            }
            if(i<children.size()-1){
                addBR();
            }
            setLabel(LabelSetter);
            LabelSetter++;
        }
    }

    /**
     * 特点：compound_list只有一个
     * @param root
     */
    private static void whileProcess(Block root) throws UndefinedSymbolException {
        List<Block> children = root.children;//[bool,compound,compound]
        real_bool_expression(children.get(0));//bool
        addBRF();
        List<Block> statement_list = children.get(1).children.get(0).children;
        for (Block expr : statement_list) {
            expr_stat(expr);
        }
        addBR(LabelWriter-2);
        setLabel(LabelSetter);
        LabelSetter++;
    }

    private static void forProcess(Block root) throws UndefinedSymbolException {
        List<Block> children = root.children;
        Block assign = children.get(0);
        assign(assign);
        int TEMP = LabelSetter;
        int TEMP2 = TEMP;
        setLabel(TEMP);
        TEMP+=3;
        real_bool_expression(children.get(1));
        LabelWriter++;
        addBRF();
        addBR();
        int TEMP3 = LabelWriter;
        LabelWriter = TEMP2;
        setLabel(TEMP);
        TEMP--;
        assign(children.get(2));
        addBR();
        setLabel(TEMP);
        LabelWriter = --TEMP3;//
        List<Block> statement_list = children.get(3).children.get(0).children;
        for (Block expr_stat : statement_list) {
            expr_stat(expr_stat);
        }
        LabelWriter=++TEMP3;
        addBR();
        setLabel(--TEMP);
        LabelSetter = LabelWriter;
    }

    /**
     *
     * @param block:传进来的一定是bool_expr上一层的节点
     */
    private static void real_bool_expression(Block block) {
        Block opBlock = block.children.get(0);
        Object op = opBlock.value;
        Block left = opBlock.children.get(0);
        Block right = opBlock.children.get(1);
        factor(left.value);
        factor(right.value);
        addCMP(op);

    }

    private static void addCMP(Object op) {
        result.append(" ").append(op).append("\n");
    }
    private static void factor(Block factor) {
        factor(factor.value);
    }
    private static void factor(Object factor) {
        try {
            String operand = (String) factor;
            if(operand.matches("\\d+"))result.append(" LOADI ").append(operand).append("\n");
            else {
                int address = LOOKUP(operand);
                result.append(" LOAD ").append(address).append("\n");
            }
        }
        catch (UndefinedSymbolException e){
            throw new RuntimeException(e);
        }
    }

    private static int LOOKUP(String operand) throws UndefinedSymbolException {
        int address = signal_chart.indexOf(operand);
        if(address == -1)throw new UndefinedSymbolException();
        return address;
    }

    private static void addBRF() {
        result.append(" BRF Label").append(LabelWriter++).append("\n");
    }
    private static void addBR(){
        result.append(" BR Label").append(LabelWriter++).append("\n");
    }
    private static void addBR(int num){
        result.append(" BR Label").append(num).append("\n");
    }
    private static void expr_stat(Block expr) throws UndefinedSymbolException {
        Block assign = expr.children.get(0);
        assign(assign);
    }

    private static void assign(Block assign) throws UndefinedSymbolException {
        Block leftVar = assign.children.get(0);
        String storeOp = "\n";
        if(leftVar!=null){
            int address = LOOKUP((String) leftVar.value);
            storeOp = " STO "+address;
        }
        expr(assign.children.get(1));//<expr> in
        result.append(storeOp).append("\n");
        result.append(" POP").append("\n");
    }

    private static void expr(Block expr) {

            if(expr.children.get(0).children.get(0).type.equals("factor")){
                factor(expr.children.get(0).children.get(0));
            }else{
                Block op = expr.children.get(0).children.get(0);
                Deque<Block> stack = new LinkedList<>();
                op.children.forEach(stack::push);
                stack.forEach(AsmSpawner::factor);
                switch (op.type){//这东西最好用枚举
                    case "PLUS"->result.append(" ADD").append("\n");
                    case "MINUS"->result.append(" SUB").append("\n");
                    case "MULTIPLY"->result.append(" MUL").append("\n");
                    case "DIVIDE"->result.append(" DIV").append("\n");
            }
        }
    }
    private static void setLabel(int temp) {
        result.append("Label").append(temp).append(":").append("\n");
        //这里暂时不++
    }

}
