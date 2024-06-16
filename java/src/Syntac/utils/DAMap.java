package Syntac.utils;

import Syntac.utils.LL.LinkList;
import Syntac.utils.LL.NonTerminal;
import Syntac.utils.LL.Symbol;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DAMap {
    Map<NonTerminal,Map<Symbol, Map<Symbol, LinkList>>> innerMap;

    Set<Symbol> axis_y;
    public DAMap(Set<Symbol> Terminals) {
        innerMap = new HashMap<>();
        axis_y = Terminals;
    }

    public void fill(NonTerminal x, Symbol y, Map<Symbol, LinkList> content){
        // 首先检查innerMap中是否已经包含了对应的非终结符x
        if (!innerMap.containsKey(x)) {
            // 如果不存在，则创建一个新的HashMap并放入innerMap中
            innerMap.put(x, new HashMap<>());
        }
        // 获取x对应的映射
        Map<Symbol, Map<Symbol, LinkList>> xMap = innerMap.get(x);
        // 直接将y和content映射添加到这个映射中
        // 这样，对于同一个x，可以累积多个y和它们对应的content
        xMap.put(y, content);

    }
    public Map<NonTerminal,Map<Symbol, Map<Symbol, LinkList>>> get(){
        return innerMap;
    }
    public Map<Symbol, LinkList> getProduction(Symbol X,Symbol Y){
        return innerMap.get((NonTerminal) X).get(Y);
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        // Header with "NonTerminals" followed by all terminals
        builder.append(String.format("%-16s","NonTerminals"));
        for (Symbol terminal : axis_y) {
            builder.append("\t").append(String.format("%-16s", terminal));
        }
        builder.append("\n");

        // Rows for each non-terminal
        for (NonTerminal nonTerminal : innerMap.keySet()) {
            builder.append(String.format("%-16s", nonTerminal.toString()));

            for (Symbol terminal : axis_y) {
                builder.append("\t");
                Map<Symbol, Map<Symbol, LinkList>> symbolMap = innerMap.get(nonTerminal);
                if (symbolMap != null && symbolMap.containsKey(terminal)) {
                    Map<Symbol, LinkList> linkMap = symbolMap.get(terminal);
                    // Assuming we only want to display the first entry from the LinkList
                    if (!linkMap.isEmpty()) {
                        Map.Entry<Symbol, LinkList> firstEntry = linkMap.entrySet().iterator().next();
                        Symbol key = firstEntry.getKey();
                        LinkList value = firstEntry.getValue();
                        builder.append(String.format("%-16s", key.toString()+"->"+(value.toString())));
                    } else {
                        builder.append(String.format("%-16s","Error"));
                    }
                } else {
                    builder.append(String.format("%-16s","Error"));
                }
            }
            builder.append("\n");
        }

        return builder.toString();
    }
    public void traverseDAMap() {
        // 遍历innerMap的每个非终结符（NonTerminal）键
        for (NonTerminal nonTerminal : innerMap.keySet()) {
            System.out.println("非终结符: " + nonTerminal);

            // 获取当前非终结符对应的映射（Map<Symbol, Map<Symbol, LinkList>>）
            Map<Symbol, Map<Symbol, LinkList>> symbolMap = innerMap.get(nonTerminal);

            // 遍历该映射的每个终结符（Symbol）键
            for (Symbol terminal : symbolMap.keySet()) {
                System.out.println("\t终结符: " + terminal);

                // 获取当前终结符对应的产生式集合（Map<Symbol, LinkList>）
                Map<Symbol, LinkList> productionMap = symbolMap.get(terminal);

                // 遍历产生式集合
                for (Map.Entry<Symbol, LinkList> entry : productionMap.entrySet()) {
                    Symbol left = entry.getKey(); // 产生式左部的符号
                    LinkList right = entry.getValue(); // 产生式右部的链表

                    System.out.println("\t\t产生式: " + left + " -> " + right);
                }
            }
        }
    }
}
