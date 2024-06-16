package Semantic.exception;

public class DuplicateDefinitionException extends Exception {
    // 构造器：仅带消息参数
    public DuplicateDefinitionException() {
        super("Duplicate Definition Exception cased by Multiple Symbol Definition");
    }
}