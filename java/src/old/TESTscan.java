package old;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class TESTscan {

/*    public HashSet<String> preserveWords =
            new HashSet<>(List.of(new String[]{"if", "else", "for", "while", "do", "int"}));*/
    public static HashMap<String,String > preserveWords =
            new HashMap<>();
    static {//这是静态类初始化哈希表的方法
        preserveWords.put("if","$if");
        preserveWords.put("else","$else");
        preserveWords.put("for","$for");
        preserveWords.put("while","$while");
        preserveWords.put("do","$do");
        preserveWords.put("int","$int");
    }
    public TESTscan(){
        preserveWords.put("if","$if");
        preserveWords.put("else","$else");
        preserveWords.put("for","$for");
        preserveWords.put("while","$while");
        preserveWords.put("do","$do");
        preserveWords.put("int","$int");
    }
    public static String scan(String rawText) throws Exception {
        StringBuilder identifier;
        StringBuilder number;
        StringBuilder divideSignal;
        StringBuilder result = new StringBuilder();
        String[] arr = rawText.split("");
 /*       ArrayList<String> arr1 = new ArrayList<>();
        for (String s : arr) {
            if(!s.isBlank()){
               arr1.add(s);
            }
        }*/
        for (int i = 0; ; i++) {
            if(i == arr.length-1){
                result.append(arr[i]).append("\n");
                break;
            }
            String c = arr[i];
            /*if ((c.isEmpty() || c.isBlank()) && i == arr.length - 1) {
                break;
            }*/
            if ((c.isEmpty() || c.isBlank())) {
                continue;
            } else {
                if(c.equals("]")||c.equals("}")||c.equals("/")||c.equals(";")){
                    result.append('\n');
                }
                if (c.matches("[a-zA-Z]+")) {//为纯字母
                    identifier = new StringBuilder();
                    while ((c = arr[i]).matches("[a-zA-Z]+")) {
                        identifier.append(c);
                        i++;
                    }
                    i--;
                    //System.out.println(i+"   "+"identifier = "+identifier);
                    if (preserveWords.containsKey(identifier.toString().trim())) {//为保留字
                        result.append(preserveWords.get(identifier.toString().trim())).append(" ");
                    } else {
                        result.append("ID").append(" ").append(identifier).append(" ");
                    }
                } else {
                    if (c.matches("\\d+")) {//为数字
                        number = new StringBuilder();
                        while ((c = arr[i]).matches("\\d+")) {
                            number.append(c);
                            i++;
                        }
                        i--;
                        result.append("NUM").append(" ").append(number).append(" ");
                    } else{
                        if(c.equals("+")||c.equals("-")){//单字符
                            result.append(c).append(" ");
                        } else if (c.equals(";")) {
                            result.append(c).append("\n");
                        } else if(c.matches(".")){
                            divideSignal = new StringBuilder();
                            while ((c = arr[i]).matches(".")) {
                                divideSignal.append(c);
                                i++;
                            }
                            i--;
                            result.append(divideSignal).append(" ");
                        } else if (c.matches("[<>=!]")) {//双字符
                            if(arr[++i].matches("=")){
                                result.append(c).append(arr[i]).append(" ");
                            }else{
                                i--;
                                result.append(c).append(" ");
                            }
                        } else if(c.matches("[({)}]")){
                            result.append(c);
                        } else if (c.matches("/")) {
                            if(arr[++i].matches("[*]")){//注释
                                result.append(c).append(arr[i]);
                            }else{
                                i--;
                                result.append(c).append(" ");
                            }
                        } else if (c.matches("[*]")) {
                            if(arr[++i].matches("[/]")){//注释
                                result.append(c).append(arr[i]);
                            }else{
                                i--;
                                result.append(c).append(" ");
                            }
                        } else{
                            throw new Exception("自己查查你的低能错误.");
                        }
                    }
                }
            }
        }
        return result.toString();
    }
    public TESTscan(String originPath,String aimPath) throws Exception{
        File f1 = new File(originPath);
        File f2 = new File(aimPath);
        try (BufferedReader reader = new BufferedReader(new FileReader(f1))) {
            StringBuilder sb = new StringBuilder();
            String line;
            // 逐行读取文本内容并追加到 StringBuilder 中
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator()); // 添加换行符（可选）
            }

            String content = sb.toString(); // 将 StringBuilder 转换为字符串
            String result = scan(content);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(f2))) {
                writer.write(result);
                System.out.println("写入成功！");
            } catch (IOException e) {
                System.err.println("写入文件时出错：" + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("读取文件时出错：" + e.getMessage());
        }

    }
}
