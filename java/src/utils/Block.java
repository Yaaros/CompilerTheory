package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class Block {
    public String type;

    public List<Block> children;


    public Object value;

    public Block(String type, List<Block> children) {
        this.type = type;
        this.children = children;
        this.value = null;
    }

    public Block(String type, List<Block> children,Object value) {
        this.type = type;
        this.children = children;
        this.value = value;
    }

    @Override
    public String toString() {
        return "<"+type + (value == null ? "" : (":"+value))+">";
    }

    public void addChild(Block child){
        children.add(child);
    }

    public void visualizeBlockTree(){
        visualizeBlockTree(this);
    }

    // 新增一个可视化方法
    private void visualizeBlockTree(Block root) {
        if (root == null) {
            System.out.println("The tree is empty.");
            return;
        }
        printBlockTree(root, "");
    }

    // 递归辅助方法，用于打印每个节点
    private void printBlockTree(Block block, String indent) {
        if (block == null) {
            return;
        }
        // 打印当前节点
        System.out.println(indent + block);
        // 递归打印每个子节点，增加缩进
        if (block.children != null) {
            for (Block child : block.children) {
                printBlockTree(child, indent + "  ");
            }
        }
    }
    public void outputBlockTreeToFile(String path) {
        try {
            PrintWriter writer = new PrintWriter(new File(path+"/RecursionResult.txt"));
            outputBlockTree(this, "", writer);
            writer.close(); // 确保关闭文件
        } catch (FileNotFoundException e) {
            System.out.println("Error creating or writing to the file.");
            e.printStackTrace();
        }
    }
    public void outputBlockTreeToFile() {
        try {
            PrintWriter writer = new PrintWriter(new File("RecursionResult.txt"));
            outputBlockTree(this, "", writer);
            writer.close(); // 确保关闭文件
        } catch (FileNotFoundException e) {
            System.out.println("Error creating or writing to the file.");
            e.printStackTrace();
        }
    }

    // 新增一个递归输出方法，包含PrintWriter参数
    private void outputBlockTree(Block root, String prefix, PrintWriter writer) {
        if (root == null) {
            writer.println("The tree is empty.");
            return;
        }
        writer.println(prefix + root); // 假设Block有getName方法获取节点名称
        // 假设Block有getChildren方法返回子节点列表
        if(root.children!=null){
            for (Block child : root.children) {
                outputBlockTree(child, prefix + "  ", writer); // 为子节点增加缩进
            }
        }
    }
}
