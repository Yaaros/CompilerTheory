package Lex;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class LexAnalyzer {
    public static HashSet<String> preserveWords =
            new HashSet<>();
    static {//这是静态类初始化哈希表的方法
        preserveWords.add("if");
        preserveWords.add("else");
        preserveWords.add("for");
        preserveWords.add("while");
        preserveWords.add("do");
        preserveWords.add("int");
        preserveWords.add("read");
        preserveWords.add("write");
    }
    public LexAnalyzer(){
        preserveWords.add("if");
        preserveWords.add("else");
        preserveWords.add("for");
        preserveWords.add("while");
        preserveWords.add("do");
        preserveWords.add("int");
        preserveWords.add("read");
        preserveWords.add("write");
    }
    public static String scan(String rawText) throws Exception {
        StringBuilder identifier;
        StringBuilder number;
        StringBuilder result = new StringBuilder();
        StringBuilder annotation = new StringBuilder();
        String idOrNot;
        String[] temp = rawText.split("");
        ArrayList<String> arr = new ArrayList<>();
        for(int i = 0;i<temp.length;i++){
            if(temp[i].isBlank()){
                while(i<temp.length&&temp[i].isBlank()) {
                    i++;
                }
                i--;
                arr.add(" ");
            }else{
                if(!temp[i].isEmpty()){
                    arr.add(temp[i]);
                }
            }
        }
        for(int i = 0;;i++){
            if(i >= arr.size()){
                break;
            }
            String c = arr.get(i);
            if(!c.isBlank()&&!c.isEmpty()){//非空
                if(c.matches("[a-zA-Z]")){
                    identifier = new StringBuilder();
                    while ((c=arr.get(i)).matches("[a-zA-Z]")){
                        identifier.append(c);
                        i++;
                    }
                    i--;
                    idOrNot = identifier.toString().trim();
                    if (preserveWords.contains(idOrNot)) {//为保留字
                        result.append(idOrNot.toUpperCase()).append(" ").append(idOrNot);
                    } else { //为纯字母标识符
                        result.append("ID").append(" ").append(idOrNot).append(" ");
                    }
                } else if (c.matches("\\d")) { //数字
                    number = new StringBuilder();
                    while ((c = arr.get(i)).matches("\\d+")) {
                        number.append(c);
                        i++;
                    }
                    i--;
                    result.append("NUM").append(" ").append(number).append(" ");
                } else if (c.equals("+")||c.equals("-")||c.equals(";")
                        ||c.equals("{")||c.equals("}")) {//单分符
                    switch (c) {
                        case "+"-> result.append("PLUS").append(" ").append("+");
                        case "-"-> result.append("MINUS").append(" ").append("-");
                        case "{"-> result.append("LBRACE").append(" ").append("{");
                        case "}"-> result.append("RBRACE").append(" ").append("}");
                        case ";"-> result.append("SEMICOLON").append(" ").append(";");
                    }
                } else if (c.matches("[<>=!]")) { //双字符
                    if (i + 1 < arr.size() && arr.get(i + 1).equals("=")) { // 检查是否有下一个字符，并且它是否是'='
                        switch (c) {
                            case ">" -> result.append("GE ").append(">="); // 大于等于
                            case "<" -> result.append("LE ").append("<="); // 小于等于
                            case "=" -> result.append("EQ ").append("=="); // 等于
                            case "!" -> result.append("NOTEQ ").append("!="); // 不等于
                        }
                        i++; // 跳过下一个'='字符
                    } else if (c.equals("<")&&i + 1 < arr.size() && arr.get(i + 1).equals(">")) {
                        result.append("NOTEQ ").append("<>"); // 不等于
                    } else { // 单字符
                        switch (c) {
                            case ">" -> result.append("GT ").append(">"); // 大于
                            case "<" -> result.append("LT ").append("<"); // 小于
                            case "=" -> result.append("ASSIGN ").append("="); // 赋值
                        }
                    }
                } else if(c.matches("[()]")){
                    if(c.equals("("))result.append("LPAREN").append(" ").append(c);
                    else result.append("RPAREN").append(" ").append(c);
                } else if (c.matches("/")) {
                    if(arr.get(++i).equals("*")){//注释
                        annotation.append(c);
                        annotation.append(arr.get(i));
                        while(!arr.get(++i).equals("*")){//注释内容->清空
                            annotation.append(arr.get(i));
                        }
                        if(arr.get(i).equals("*")){
                            annotation.append(arr.get(i));
                            if(arr.get(++i).equals("/")){
                                annotation.append(arr.get(i));
                            }else{
                                throw new Exception("找不到注释下界");
                            }
                        }
                        else{
                            throw new Exception("找不到注释下界");
                        }
                    }else{//正常的除号
                        i--;
                        result.append("DIVIDE").append(" ").append(c);
                    }
                } else if (c.equals("*")) {
                    if(arr.get(++i).equals("/")){//注释
                        throw new Exception("找不到注释上界");
                    }else{//乘号
                        i--;
                        result.append("MULTIPLY").append(" ").append(c);
                    }
                } else{
                    throw new Exception("出错了，输入字符有误");
                }
            }
            result.append("\n");
        }
        removeNewLines(result);
        String annotation_result = annotation.toString();
        if(annotation_result.length()>1){
            System.out.println("The annotation is "+annotation);
        }
        return result.toString();
    }

    public static void lexStreamSpawner(String path,String rawText) throws Exception {
        String input = scan(rawText);
        try (FileWriter writer = new FileWriter(path+"/LexStream.txt")) {
            writer.write(input); // 将 input 字符串写入文件
        } catch (IOException e) {
            // 处理可能发生的 IO 异常
            throw new Exception("Failed to write to LexStream.txt", e);
        }
        System.out.println("Lex analysis successful!");
    }

    private static void removeNewLines(StringBuilder stb){
        int i = 0;
        while (i < stb.length() - 1) {
            if (stb.charAt(i) != '\n'){
                i++;
            }
            else if(stb.charAt(i) == '\n' && stb.charAt(i + 1) != '\n') {
                i+=2;
            }
            else if (stb.charAt(i) == '\n' && stb.charAt(i + 1) == '\n') {
                stb.deleteCharAt(i);
            }
        }
    }
}
