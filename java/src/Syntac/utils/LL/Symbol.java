package Syntac.utils.LL;

import javax.swing.tree.TreeNode;
import java.util.List;
import java.util.Objects;

public abstract class Symbol {
    String symbol;

    Symbol next;

    TreeNode node;


    public void setNode(TreeNode node) {
        this.node = node;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Symbol symbol1)) return false;
        return symbol.equals(symbol1.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }

    public void setNext(Symbol next) {
        this.next = next;
    }

    public Symbol getNext(){
        return this.next;
    }


    @Override
    public String toString() {
        return symbol;
    }
}

class Production{
    List<Symbol> symbolList;

    public Production(List<Symbol> symbolList) {
        this.symbolList = symbolList;
    }
}