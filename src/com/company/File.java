package com.company;

public class File extends StoredObject {
    private int size;
    private INode firstNode;


    public File (StoredObject storedObject, int size) {
        super(storedObject);
        this.size = size;
    }
    public File(String name, int size) {
        super(name);
        this.size = size;
    }


    public int getSize() {
        return size;
    }

    public String getName() {
        return super.toString();
    }
    public void setSize(int size) {
        this.size = size;
    }

    public INode getFirstNode() {
        return firstNode;
    }

    public void setFirstNode(INode firstNode) {
        this.firstNode = firstNode;
    }
}
