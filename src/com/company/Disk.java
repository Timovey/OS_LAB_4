package com.company;

public class Disk {

    private final int segmentSize;
    private final int segmentAmount;
    private final Segment[] segmentArr;
    private final int size;
    private int freeSegment;
    Status status = new Status();

    public Disk(int size, int segmentSize) {
        this.size = size;
        this.segmentSize = segmentSize;
        segmentAmount = size / segmentSize;
        freeSegment =  segmentAmount;

        segmentArr = new Segment[segmentAmount];
        for (int i = 0; i < segmentAmount; i++) {
            segmentArr[i] = new Segment(status.free);
        }
    }

    public void deleteFreeSegment() {
        freeSegment--;
    }

    public void addFreeSegment() {
        freeSegment++;
    }
    public int getSegmentSize() {
        return segmentSize;
    }

    public Segment[] getSegmentArr() {
        return segmentArr;
    }

    public int getSegmentAmount() {
        return segmentAmount;
    }

    public int getFreeSegment() {
        return freeSegment;
    }

}
