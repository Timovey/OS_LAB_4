package com.company;

import javax.swing.*;
import java.awt.*;

public class TreePanel extends JPanel {
    private final TreeManager treeManager;

    public TreePanel(TreeManager treeManager) {
    this.treeManager = treeManager;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        treeManager.draw(g);
    }
}
