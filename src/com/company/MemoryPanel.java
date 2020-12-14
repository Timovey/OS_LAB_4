package com.company;

import javax.swing.*;
import java.awt.*;

public class MemoryPanel extends JPanel {
    private final FileSystem fileSystem;

    public MemoryPanel(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        fileSystem.draw(g);
    }
}
