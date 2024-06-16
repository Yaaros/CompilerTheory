package Syntac.utils.recursion;

public class Token {
    public String type;
    public String value;

    @Override
    public String toString() {
        return "<"+type+">: "+value;
    }

    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }
}