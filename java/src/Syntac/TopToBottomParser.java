package Syntac;

import Syntac.utils.recursion.Lexer;
import Syntac.utils.recursion.Token;
import utils.Block;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class TopToBottomParser {
    public Lexer lexer;
    public Token currentToken;

    public TopToBottomParser(Lexer lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.nextToken();
    }

    private void advance() {
        currentToken = lexer.nextToken();
    }

    private void consume(String expectedType) throws Exception {
        if (currentToken.type.equals(expectedType)) {
            advance();
        } else {
            throw new Exception("Syntax Error: Expected " + expectedType + " but found " + currentToken.type);
        }
    }

    public Block parse() throws Exception {
        Block root = program();
        System.out.println("Syntax analysis successful!");
        return root;
    }

    private Block program() throws Exception {
        //程序总体架构，先处理声明后处理其他语句
        consume("LBRACE");
        ArrayList<Block> arr = new ArrayList<>();
        Block declarations = declaration_list();
        Block statements = statement_list();
        consume("RBRACE");
        arr.add(declarations);
        arr.add(statements);
        return new Block("program_root",arr);
    }

    private Block declaration_list() throws Exception {
        ArrayList<Block> declarations = new ArrayList<>();
        while (currentToken.type.equals("INT")) {
            declarations.add(declaration_stat());
        }
        return new Block("declaration_list",declarations);
    }

    private Block declaration_stat() throws Exception {
        consume("INT");
        String val = currentToken.value;
        consume("ID");
        consume("SEMICOLON");
        return new Block("declaration_stat",null,val);
    }

    private Block statement_list() throws Exception {
        ArrayList<Block> statements = new ArrayList<>();
        while (isStatementStart(currentToken.type)) {
            statements.add(statement());
        }
        return new Block("statement_list", statements);
    }

    private boolean isStatementStart(String type) {
        return type.equals("IF") || type.equals("WHILE") || type.equals("FOR") || type.equals("READ")
                || type.equals("WRITE") || type.equals("LBRACE") || type.equals("ID") || type.equals("SEMICOLON");
    }

    private Block statement() throws Exception {
        Block statement = switch (currentToken.type) {
            case "IF" -> if_stat();
            case "WHILE" -> while_stat();
            case "FOR" -> for_stat();
            case "READ" -> read_stat();
            case "WRITE" -> write_stat();
            case "LBRACE" -> compound_stat();
            case "ID", "SEMICOLON" -> expression_stat();
            default -> throw new Exception("Syntax Error: Unexpected token " + currentToken.type);
        };
        return statement;
    }
    private Block if_stat() throws Exception {
        consume("IF");
        consume("LPAREN");
        ArrayList<Block> list = new ArrayList<>();
        list.add(expression());
        consume("RPAREN");
        list.add(statement());
        if (currentToken.type.equals("ELSE")) {
            consume("ELSE");
            list.add(statement());
        }
        return new Block("if_stat",list);
    }

    private Block while_stat() throws Exception {
        consume("WHILE");
        consume("LPAREN");
        ArrayList<Block> list = new ArrayList<>();
        list.add(expression());

        consume("RPAREN");
        list.add(statement());
        return new Block("while_stat",list);
    }

    private Block for_stat() throws Exception {
        consume("FOR");
        consume("LPAREN");
        ArrayList<Block> list = new ArrayList<>();
        list.add(expression());
        consume("SEMICOLON");
        list.add(expression());
        consume("SEMICOLON");
        list.add(expression());
        consume("RPAREN");
        list.add(statement());
        return new Block("for_stat",list);
    }

    private Block read_stat() throws Exception {
        consume("READ");
        Block expr = expression();
        consume("SEMICOLON");
        return new Block("read_stat",List.of(expr));
    }

    private Block write_stat() throws Exception {
        consume("WRITE");
        Block expr = expression();
        consume("SEMICOLON");
        return new Block("write_stat", List.of(expr));
    }

    private Block compound_stat() throws Exception {
        consume("LBRACE");
        Block stat_list = statement_list();
        consume("RBRACE");
        return new Block("compound_list",List.of(stat_list));
    }

    private Block expression_stat() throws Exception {
        Block expressionStat;
        if (currentToken.type.equals("ID") || currentToken.type.equals("NUM") || currentToken.type.equals("LPAREN")) {
            Block expr = expression();
            consume("SEMICOLON");
            expressionStat = new Block("expression_stat", List.of(expr));
        } else {
            consume("SEMICOLON");
            expressionStat = new Block("empty_statement", null); // 对于空语句
        }
        return expressionStat;
    }

    private Block expression() throws Exception {
        Block expressionNode = new Block("expression", new ArrayList<>());
        if (currentToken.type.equals("ID")) {
            String variableName = currentToken.value;
            advance();
            if (currentToken.type.equals("ASSIGN")) {

                advance();
                Block value = expression();
                return new Block("assign",List.of(new Block("variable",null,variableName),value));
            } else{
                lexer.rewind();
                lexer.rewind();
                currentToken = lexer.nextToken();
                Block boolExpr = bool_expr();
                expressionNode.children.add(boolExpr);
            }
        } else {
            Block boolExpr = bool_expr();
            expressionNode.children.add(boolExpr);
        }
        return expressionNode;
    }

    private Block bool_expr() throws Exception {
        Block boolExpr = new Block("bool_expr", new ArrayList<>());
        Block leftAdditiveExpr = additive_expr();
        boolExpr.children.add(leftAdditiveExpr);
        if (currentToken.type.equals("GT") || currentToken.type.equals("LT") || currentToken.type.equals("GE")
                || currentToken.type.equals("LE") || currentToken.type.equals("EQ") || currentToken.type.equals("NE")) {
            boolExpr.value = currentToken.type;
            advance();
            Block rightAdditiveExpr = additive_expr();
            boolExpr.children.add(rightAdditiveExpr);
        }
        return boolExpr;
    }
    private Block additive_expr() throws Exception {
        // 从第一个term开始构建表达式
        Block left = term();

        // 处理后续的加法或减法操作
        while (currentToken.type.equals("PLUS") || currentToken.type.equals("MINUS")) {
            String operation = currentToken.type; // 保存当前操作符
            advance(); // 移动到下一个token
            Block right = term(); // 解析下一个term

            // 根据操作符创建一个新的Block来表示这个操作，并把之前的left和新的right作为子节点
            left = new Block(operation, Arrays.asList(left, right), null);
        }

        return left; // 返回构建的表达式树
    }

    private Block term() throws Exception {
        // 从第一个factor开始构建表达式
        Block left = factor();

        // 处理后续的乘法或除法操作
        while (currentToken.type.equals("MULTIPLY") || currentToken.type.equals("DIVIDE")) {
            String operation = currentToken.type; // 保存当前操作符
            advance(); // 移动到下一个token
            Block right = factor(); // 解析下一个factor

            // 根据操作符创建一个新的Block来表示这个操作，并把之前的left和新的right作为子节点
            left = new Block(operation, Arrays.asList(left, right), null);
        }

        return left; // 返回构建的表达式树
    }

    private Block factor() throws Exception {
        Block factorNode;
        System.out.println(currentToken.type);
        if (currentToken.type.equals("LPAREN")) {
            consume("LPAREN");
            Block expr = expression();
            consume("RPAREN");
            factorNode = new Block("factor", List.of(expr));
        } else if (currentToken.type.equals("ID") || currentToken.type.equals("NUM")) {
            String value = currentToken.value;
            factorNode = new Block("factor", null,value);
            advance();
        } else {
            throw new Exception("Syntax Error: Unexpected token " + currentToken.type);
        }
        return factorNode;
    }

    /**
     * 传进来的path也是含文件名的路径
     * 可以通过调用read方法来进行测试，也可以直接运行更下面的main方法进行测试
     * */
    public static Block read(String path){
        try {
            Path filePath = Paths.get(path);
            Path folderPath = filePath.getParent();
            Lexer lexer = new Lexer(folderPath+"/LexStream.txt");
            System.out.println("TokenStream:");
            System.out.println(lexer.tokens);
            TopToBottomParser parser = new TopToBottomParser(lexer);
            Block root = parser.parse();
            root.visualizeBlockTree();
            root.outputBlockTreeToFile(folderPath.toString());
            return root;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;//根本走不到这还非得写这一行什么垃圾语言
    }

    public static void main(String[] args) {
        try {
            Lexer lexer = new Lexer("tokens.txt");
            System.out.println(lexer.tokens);
            TopToBottomParser parser = new TopToBottomParser(lexer);
            Block root = parser.parse();
            root.visualizeBlockTree();
            root.outputBlockTreeToFile();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}