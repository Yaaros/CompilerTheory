package Semantic.exception;

public class UndefinedSymbolException extends Exception{
    // 构造器：仅带消息参数
    public UndefinedSymbolException() {
        super("Undefined Symbol was Found.");
    }
}
