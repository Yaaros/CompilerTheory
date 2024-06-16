package Syntac.utils.LL;


import java.util.ArrayList;
import java.util.List;

public class SymbolNode {
    private final Symbol symbol;
    private final List<SymbolNode> children;

    public SymbolNode(Symbol symbol) {
        this.symbol = symbol;
        this.children = new ArrayList<>();
    }

    public void addChild(SymbolNode child) {
        children.add(child);
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public List<SymbolNode> getChildren() {
        return children;
    }

    public boolean equals(Symbol symbol) {
        return this.symbol.equals(symbol);
    }

    @Override
    public String toString() {
        return symbol.symbol;
    }
}