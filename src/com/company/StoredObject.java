package com.company;

public class StoredObject {
    private String name;
    private boolean haveChild;
    private int startX;
    private int startY;
    private boolean pickInTree = false;
    private int level = 0;

    public StoredObject(StoredObject storedObject) {
        this.name = storedObject.name;
        this.haveChild = storedObject.haveChild;
    }

    public StoredObject(String name) {
        this.name = name;
    }

    public StoredObject(String name, boolean haveChild, int startX, int startY) {
        this.name = name;
        this.haveChild = haveChild;
        this.startX = startX;
        this.startY = startY;
    }

    public void setObject(boolean haveChild, int startX, int startY, int level) {
        this.haveChild = haveChild;
        this.startX = startX;
        this.startY = startY;
        this.level = level;
    }

    public boolean isHaveChild() {
        return haveChild;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public void setPickInTree(boolean pickInTree) {
        this.pickInTree = pickInTree;
    }

    public boolean getPickInTree() {
        return pickInTree;
    }

    public String getName() {return name;}

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void addLevel() {
        level++;
    }

    public void setStartPosition(int height) {
        startY += height;
    }

    @Override
    public String toString() {
        return name;
    }
}
