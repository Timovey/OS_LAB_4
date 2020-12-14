package com.company;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Form {
    public JFrame frame;
    private FileController fileController;
    private TreePanel treePanel;
    private FileSystem fileSystem;
    private MemoryPanel panel;
    private TreeManager treeManager;
    private StoredObject moveObject = null;

    /**
     * Launch the application.
     */
    Form() {
        initialize();
    }

    private void initialize() {
        int size = 100;
        int maxSize = 900;
        int minSize = 10;
        int sizeSegment = 1;
        int minSizeSegment = 1;
        int widthTree = 330;
        int heightTree = 300;

        frame = new JFrame();
        frame.setTitle("Визуализация работы файловой системы");
        frame.setBounds(300, 250, 1200, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        boolean circle = true;
        while (circle) {
            try {
                size = Integer.parseInt(JOptionPane.showInputDialog(frame, "Введите значение от " + minSize + " до " + maxSize, "Размер диска", 1));
                if (size > maxSize || size < minSize) {
                    throw new Exception();
                } else {
                    circle = false;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Введены неверные данные", "Размер диска", JOptionPane.ERROR_MESSAGE);
            }
        }
        int pathSize = size / 5;
        circle = true;
        while (circle) {
            try {
                sizeSegment = Integer.parseInt(JOptionPane.showInputDialog(frame, "Введите значение от " + minSizeSegment + " до " + pathSize, "Размер сегмента диска", 1));
                if (sizeSegment > pathSize || sizeSegment < minSizeSegment) {
                    throw new Exception();
                } else {
                    circle = false;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Введены неверные данные", "Размер сегмента диска", JOptionPane.ERROR_MESSAGE);
            }
        }

        Disk disk = new Disk(size, sizeSegment);
        treeManager = new TreeManager();
        fileSystem = new FileSystem(disk, treeManager);
        fileController = new FileController(this, disk, fileSystem, treeManager);


        //добавить 0 элемент в дереве

        panel = new MemoryPanel(fileSystem);
        panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        frame.getContentPane().add(panel);
        panel.setBounds(360, 20, 500, 500);

        treePanel = new TreePanel(treeManager);
        treePanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        frame.getContentPane().add(treePanel);
        treePanel.setBounds(20, 20, 300, 300);


        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getPoint().x > treePanel.getX() && e.getPoint().y > treePanel.getY()
                        && e.getPoint().y < (treePanel.getY() + treePanel.getHeight()) && (treePanel.getX() + treePanel.getWidth()) > e.getPoint().x) {
                    moveObject = treeManager.pick(e.getPoint().x - treePanel.getX(), e.getPoint().y - treePanel.getY());
                    if (moveObject != null && !moveObject.isHaveChild()) {
                        fileSystem.pickElement((File) moveObject);
                    } else {
                        fileSystem.pickDefaultElement();
                    }
                    treePanel.repaint();
                    panel.repaint();
                }
            }

        });

        JButton addFileButton = new JButton("Добавить файл");
        addFileButton.addActionListener(e -> {
            fileController.addFile();
            panel.repaint();
            treePanel.repaint();
        });
        addFileButton.setBounds(20, heightTree + 40, 150, 50);
        frame.getContentPane().add(addFileButton);

        JButton addCatalogButton = new JButton("Добавить каталог");
        addCatalogButton.addActionListener(e -> {
            fileController.addCatalog();
            panel.repaint();
            treePanel.repaint();
        });
        addCatalogButton.setBounds(20, heightTree + 100, 150, 50);
        frame.getContentPane().add(addCatalogButton);

        JButton copyFileButton = new JButton("Скопировать");
        copyFileButton.setBounds(20, heightTree + 160, 150, 50);
        frame.getContentPane().add(copyFileButton);
        copyFileButton.addActionListener(e -> {
            fileController.copy(moveObject);
            panel.repaint();
            treePanel.repaint();
        });

        JButton deleteFileButton = new JButton("Удалить объект");
        deleteFileButton.setBounds(180, heightTree + 40, 150, 50);
        frame.getContentPane().add(deleteFileButton);
        deleteFileButton.addActionListener(e -> {
            fileController.deleteObject(moveObject);
            treePanel.repaint();
            panel.repaint();
        });

        JButton pasteFileButton = new JButton("Вставить");
        pasteFileButton.setBounds(180, heightTree + 100, 150, 50);
        frame.getContentPane().add(pasteFileButton);
        pasteFileButton.addActionListener(e -> {
            fileController.transfer(moveObject);
            panel.repaint();
            treePanel.repaint();
        });

        JButton moveFileButton = new JButton("Переместить");
        moveFileButton.setBounds(180, heightTree + 160, 150, 50);
        frame.getContentPane().add(moveFileButton);
        moveFileButton.addActionListener(e -> {
            fileController.move(moveObject);
            panel.repaint();
            treePanel.repaint();
        });


    }


}
