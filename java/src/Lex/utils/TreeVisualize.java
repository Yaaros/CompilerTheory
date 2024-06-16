package Lex.utils;

import javax.swing.*;
import java.awt.*;

public class TreeVisualize{

    public TreeVisualize(LexTree tree){
        LexTree.Node root = tree.root;
        JFrame frame = new JFrame("Tree Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new TreePanel(root));
        frame.setSize(400, 400);
        frame.setVisible(true);
    }
    static class TreePanel extends JPanel {
        protected LexTree.Node root;
        private final int NODE_WIDTH = 20;
        private final int NODE_HEIGHT = 20;
        private final int LEVEL_GAP = 30;

        public TreePanel(LexTree.Node root) {
            this.root = root;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // 绘制树
            drawTree(g, root, getWidth() / 2, NODE_HEIGHT, getWidth() / 4);
        }

        private void drawTree(Graphics g, LexTree.Node node, int x, int y, int gap) {
            if (node == null) {
                return;
            }

            // Set color to black for the border of the node
            g.setColor(Color.BLACK);
            // 绘制节点边框
            g.drawOval(x - NODE_WIDTH / 2, y - NODE_HEIGHT / 2, NODE_WIDTH, NODE_HEIGHT);

            // Set color to white for the node fill
            g.setColor(Color.WHITE);
            // 填充节点
            g.fillOval(x - NODE_WIDTH / 2 + 1, y - NODE_HEIGHT / 2 + 1, NODE_WIDTH - 2, NODE_HEIGHT - 2);

            // Set color back to black for the text and lines
            g.setColor(Color.BLACK);
            // 绘制节点值
            g.drawString(node.symbol, x - (g.getFontMetrics().stringWidth(node.symbol) / 2),
                    y + (g.getFontMetrics().getHeight() / 4));

            // 绘制左子树
            if (node.left != null) {
                g.drawLine(x, y + NODE_HEIGHT / 2, x - gap, y + LEVEL_GAP);
                drawTree(g, node.left, x - gap, y + LEVEL_GAP, gap / 2);
            }

            // 绘制右子树
            if (node.right != null) {
                g.drawLine(x, y + NODE_HEIGHT / 2, x + gap, y + LEVEL_GAP);
                drawTree(g, node.right, x + gap, y + LEVEL_GAP, gap / 2);
            }
        }
    }
}
