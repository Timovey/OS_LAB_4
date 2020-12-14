package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.ArrayList;
import java.io.File;

public class TreeManager {
    private final ArrayList<StoredObject> storedObjects;
    private int width = 120;
    private int height = 20;
    private int levelLength = 20;
    private int offsetDrawX = 40;
    private int offsetDrawY = 15;
    StoredObject emptyObject = new StoredObject("Empty");

    public TreeManager() {
        storedObjects = new ArrayList<>();
        StoredObject base = new Catalog("base", true, 10, 10);
        base.setLevel(0);
        storedObjects.add(base);


    }


    public void addArrayObject(Catalog parent, ArrayList<StoredObject> moveObjects) {
        int count = 0;
        int level = moveObjects.get(0).getLevel();
        for (int i = 0; i < storedObjects.size(); i++) {
            StoredObject obj = storedObjects.get(i);
            if (obj.equals(parent)) {

                for (int j = 0; j < moveObjects.size(); j++) {
                    StoredObject objMoveObject = moveObjects.get(j);
                    if (objMoveObject.isHaveChild()) {
                        Catalog catalog = new Catalog(objMoveObject);
                        if (level == objMoveObject.getLevel()) {
                            addCatalog(parent, catalog);
                            parent.addCatalog(catalog);
                        } else {
                            int b = i + count;
                            int h = j - 1;
                            StoredObject objParent = moveObjects.get(h);
                            StoredObject objParentInTree = storedObjects.get(b);
                            int levelParent = objParent.getLevel();
                            while (h >= 0 && levelParent != objMoveObject.getLevel() - 1) {
                                objParent = moveObjects.get(h);
                                objParentInTree = storedObjects.get(b);
                                levelParent = objParent.getLevel();
                                h--;
                                b--;
                            }
                            Catalog cat = (Catalog) objParentInTree;
                            addCatalog(cat, catalog);
                            cat.addCatalog(catalog);
                        }

                    } else {
                        com.company.File file = (com.company.File) objMoveObject;
                        com.company.File child = new com.company.File(file.getName(), file.getSize());
                        if (level == objMoveObject.getLevel()) {
                            addFile(parent, child);
                            parent.addFile(child);
                        } else {
                            int b = i + count;
                            int h = j - 1;
                            StoredObject objParent = moveObjects.get(h);
                            StoredObject objParentInTree = storedObjects.get(b);
                            int levelParent = objParent.getLevel();
                            while (h >= 0 && levelParent != objMoveObject.getLevel() - 1) {
                                objParent = moveObjects.get(h);
                                objParentInTree = storedObjects.get(b);
                                levelParent = objParent.getLevel();
                                h--;
                                b--;
                            }
                            Catalog cat = (Catalog) objParentInTree;
                            addFile(cat, child);
                            cat.addFile(child);
                        }

                    }
                    count++;
                }
            }
        }
    }

    public ArrayList<StoredObject> getCopyTree(StoredObject parent) {
        ArrayList<StoredObject> copyTree = new ArrayList<>();
        copyTree.add(parent);
        for (int i = 0; i < storedObjects.size(); i++) {
            StoredObject obj = storedObjects.get(i);
            if (obj.equals(parent) && (storedObjects.size() > i + 1 && storedObjects.get(i + 1).getLevel() > obj.getLevel())) {
                copyTree = addInTreeObjects(obj.getLevel(), i + 1, copyTree);
            }

        }

        return copyTree;
    }

    public ArrayList<StoredObject> addInTreeObjects(int level, int index, ArrayList<StoredObject> copyTree) {
        for (int i = index; i < storedObjects.size(); i++) {
            StoredObject obj = storedObjects.get(i);
            if (obj.getLevel() > level) {
                copyTree.add(obj);
            } else {
                return copyTree;
            }
        }
        return copyTree;
    }

    public void addCatalog(Catalog parent, Catalog catalog) {
        for (int i = 0; i < storedObjects.size(); i++) {
            StoredObject obj = storedObjects.get(i);
            if (obj.equals(parent)) {
                offset(i);
                catalog.setObject(true, obj.getStartX(), obj.getStartY() + height, obj.getLevel() + 1);
                storedObjects.add(i + 1, catalog);
                return;
            }
        }
    }

    public void offset(int index) {
        for (int i = storedObjects.size() - 1; i > index; i--) {
            StoredObject so = storedObjects.get(i);
            so.setStartPosition(height);
        }
    }

    public void addFile(Catalog parent, com.company.File file) {
        for (int i = 0; i < storedObjects.size(); i++) {
            StoredObject obj = storedObjects.get(i);
            if (obj.equals(parent)) {
                offset(i);
                file.setObject(false, obj.getStartX(), obj.getStartY() + height, obj.getLevel() + 1);
                storedObjects.add(i + 1, file);
                return;
            }
        }
    }

    public void deleteObject(StoredObject storedObject) {
        for (int i = 0; i < storedObjects.size(); i++) {
            StoredObject obj = storedObjects.get(i);
            if (obj.equals(storedObject)) {
                if (!obj.isHaveChild() || (obj.isHaveChild() && i + 1 == storedObjects.size()) ||
                        (obj.isHaveChild() && i + 1 < storedObjects.size() && storedObjects.get(i + 1).getLevel() <= obj.getLevel())) {
                    storedObjects.remove(storedObject);
                    offsetDrawObject(i, 1);
                } else {
                    int level = storedObject.getLevel();
                    int countDeletedObject = clearBounds(level, i);
                    storedObjects.remove(storedObject);
                    offsetDrawObject(i, countDeletedObject + 1);
                }
            }
        }
    }

    public void offsetDrawObject(int index, int countDeletedObject) {
        for (int i = index; i < storedObjects.size(); i++) {
            StoredObject obj = storedObjects.get(i);
            int posY = obj.getStartY();
            obj.setStartY(posY - countDeletedObject * height);
        }
    }

    public int clearBounds(int level, int index) {
        int count = 0;
        int i = index + 1;
        while (i < storedObjects.size()) {
            StoredObject obj = storedObjects.get(i);
            if (obj.getLevel() > level) {
                storedObjects.remove(i);
                count++;
            } else {
                return count;
            }
        }
        return 0;
    }

    public StoredObject pick(int xPanel, int yPanel) {
        int x = xPanel - 8;
        int y = yPanel - 30;
        for (StoredObject obj : storedObjects) {
            obj.setPickInTree(false);
        }
        for (StoredObject obj : storedObjects) {
            if (obj.getStartX() < x && obj.getStartY() < y && x < (obj.getStartX() + width + 200) && y < (obj.getStartY() + height)) {
                obj.setPickInTree(true);
                return obj;
            }
        }
        return null;
    }


    public StoredObject getPickObject() {
        for (StoredObject obj : storedObjects) {
            if (obj.getPickInTree()) {
                return obj;
            }
        }
        return null;
    }

    public Catalog getParentPickObject(StoredObject storedObject) {
        for (int i = 0; i < storedObjects.size(); i++) {
            StoredObject obj = storedObjects.get(i);
            if (obj.equals(storedObject)) {
                int level = obj.getLevel();
                int index = i;
                while (index >= 0) {
                    StoredObject parent = storedObjects.get(index);
                    index--;
                    if (parent.getLevel() + 1 == level) {
                        return (Catalog) parent;
                    }
                }
            }
        }

        return null;
    }

    public void draw(Graphics g) {
        try {
            Image imgCatalogPick = ImageIO.read(new File("images/CatalogPick.png"));
            Image imgTrianglePick = ImageIO.read(new File("images/TrianglePick.png"));
            Image imgCatalogNotPick = ImageIO.read(new File("images/CatalogNotPick.png"));
            Image imgTriangleNotPick = ImageIO.read(new File("images/TriangleNotPick.png"));
            Image imgFilePick = ImageIO.read(new File("images/FilePick.png"));
            Image imgFileNotPick = ImageIO.read(new File("images/FileNotPick.png"));

            for (StoredObject obj : storedObjects) {
                if (obj.getPickInTree()) {
                    g.setColor(new Color(156, 90, 205));
                    g.fillRect(obj.getStartX() + obj.getLevel() * levelLength, obj.getStartY(), width, height);

                    if (obj.isHaveChild()) {
                        g.drawImage(imgCatalogPick, obj.getStartX() + obj.getLevel() * levelLength + 20, obj.getStartY(), height, height, null);
                        g.drawImage(imgTrianglePick, obj.getStartX() + obj.getLevel() * levelLength, obj.getStartY(), height, height, null);
                    } else {
                        g.drawImage(imgFilePick, obj.getStartX() + obj.getLevel() * levelLength + 20, obj.getStartY(), height, height, null);
                    }

                } else {
                    g.setColor(new Color(102, 189, 205));
                    g.fillRect(obj.getStartX() + obj.getLevel() * levelLength, obj.getStartY(), width, height);

                    if (obj.isHaveChild()) {
                        g.drawImage(imgCatalogNotPick, obj.getStartX() + obj.getLevel() * levelLength + 20, obj.getStartY(), height, height, null);
                        g.drawImage(imgTriangleNotPick, obj.getStartX() + obj.getLevel() * levelLength, obj.getStartY(), height, height, null);
                    } else {
                        g.drawImage(imgFileNotPick, obj.getStartX() + obj.getLevel() * levelLength + 20, obj.getStartY(), height, height, null);
                    }

                }


                g.setColor(Color.BLACK);
                g.drawString(obj.toString(), obj.getLevel() * levelLength + obj.getStartX() + offsetDrawX, obj.getStartY() + offsetDrawY);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}


