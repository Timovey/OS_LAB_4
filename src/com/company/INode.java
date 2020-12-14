package com.company;

import java.util.ArrayList;

public class INode {
    private int[] elements;
    private int size;
    private INode next = null;

    public INode() {
        size = 0;
        elements = new int[50];
    }

    public void add(int num) {
        if(size >= elements.length) {
            next = new INode();
            next.add(num);
        }
        else {
            elements[size] = num;
            size++;
        }
    }

    public int delete() {
        if(size >= elements.length && next != null) {
            if(next.size == 1) {
                int replace = next.get(0);
                next = null;
                return  replace;
            }
            return next.delete();
        }
        size--;
        return elements[size + 1];
    }

    public int getFirst() {
        return elements[0];
    }
    public void setElements(int[] elements) {
        this.elements = elements;
    }
    public int getSize() {
        return size;
    }
    public int get(int index) {
        if(index < size) {
            return elements[index];
        }
        else if(index >= size && next != null && index - next.getSize() > 0) {
           return next.get(index - elements.length);
        }
        else {
            return -1;
        }
    }
}
