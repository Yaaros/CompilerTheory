package Syntac.utils.LL;

import java.util.LinkedList;

public class StringSymbolTransfer {
    public static LinkList transfer(String input){
        LinkedList<Symbol> toReturn = new LinkedList<>();
        String[] process = input.split("");
        if(!process[process.length-1].equals("$")){//如果没有结束符,添加一个结束符
            String[] temp = new String[process.length+1];
            System.arraycopy(process, 0, temp, 0, process.length);
            temp[process.length] = "$";
            process = temp;
        }
        for (int i = 0; i < process.length; i++) {
            if(process[i].isBlank()||process[i].isEmpty())continue;
            if(process[i].equals("i")){
                i++;
                toReturn.add(new Terminal("id"));
            } else if (process[i].equals("$")) {
                toReturn.add(new EndSymbol());
            } else{
                toReturn.add(new Terminal(process[i]));
            }
        }
        return new LinkList(toReturn);
    }
}
