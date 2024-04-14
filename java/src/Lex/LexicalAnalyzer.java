package Lex;

import java.util.ArrayList;
import java.util.HashSet;

public class LexicalAnalyzer {
    public static HashSet<String> preserveWords =
            new HashSet<>();
    static {//这是静态类初始化哈希表的方法
        preserveWords.add("if");
        preserveWords.add("else");
        preserveWords.add("for");
        preserveWords.add("while");
        preserveWords.add("do");
        preserveWords.add("int");
    }
    public LexicalAnalyzer(){
        preserveWords.add("if");
        preserveWords.add("else");
        preserveWords.add("for");
        preserveWords.add("while");
        preserveWords.add("do");
        preserveWords.add("int");
    }
    public static String scan(String rawText) throws Exception {
        StringBuilder identifier;
        StringBuilder number;
        StringBuilder divideSignal;
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
                result.append('\n');
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
                        result.append("$").append(idOrNot).append("  ").append(idOrNot);
                    } else { //为纯字母标识符
                        result.append("ID").append("  ").append(idOrNot).append(" ");
                    }
                } else if (c.matches("\\d")) { //数字
                    number = new StringBuilder();
                    while ((c = arr.get(i)).matches("\\d+")) {
                        number.append(c);
                        i++;
                    }
                    i--;
                    result.append("NUM").append("  ").append(number).append(" ");
                } else if (c.equals("+")||c.equals("-")||c.equals(";")
                        ||c.equals("{")||c.equals("}")||c.equals(".")) {//单分符
                    switch (c) {
                        case "." -> {
                            divideSignal = new StringBuilder();
                            while ((c = arr.get(i)).matches("\\d")) {
                                divideSignal.append(c);
                                i++;
                            }
                            i--;
                            result.append(divideSignal).append("    ").append(divideSignal);
                        }default -> {
                            result.append(c).append("  ").append(c);
                        }
                    }
                } else if (c.matches("[<>=!]")) {//双字符
                    if(arr.get(++i).matches("=")){
                        result.append(c).append(arr.get(i)).append("    ").append(c).append(arr.get(i));
                    }else{
                        i--;
                        result.append(c).append("   ").append(c);
                    }
                } else if(c.matches("[()]")){
                    result.append(c).append("    ").append(c);
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
                                annotation.append(arr.get(i)).append("\n");
                            }else{
                                throw new Exception("找不到注释下界");
                            }
                        }
                        else{
                            throw new Exception("找不到注释下界");
                        }
                    }else{//正常的除号
                        i--;
                        result.append(c).append("   ").append(c);
                    }
                } else if (c.equals("*")) {
                    if(arr.get(++i).equals("/")){//注释
                        throw new Exception("找不到注释上界");
                    }else{
                        i--;
                        result.append(c).append("    ").append(c);
                    }
                } else{
                    throw new Exception("出错了，输入字符有误");
                }
            }
            result.append("\n");
        }
        String annotation_result = annotation.toString();
        if(annotation_result.length()>1){
            System.out.println("The annotation is "+annotation);
        }
        return result.toString();
    }
}