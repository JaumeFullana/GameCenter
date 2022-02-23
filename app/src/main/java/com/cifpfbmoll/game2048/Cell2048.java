package com.cifpfbmoll.game2048;

public class Cell2048 {

    private int value;
    private int oldY;
    private int oldX;
    private boolean joined;
    private int oldY2;
    private int oldX2;

    public Cell2048(int value) {
        this.value = value;
    }

    public Cell2048(Cell2048 cell) {
        this.value = cell.value;
        this.oldY = cell.oldY;
        this.oldX = cell.oldX;
        this.joined = cell.joined;
        this.oldY2 =cell.oldY2;
        this.oldX2 = cell.oldX2;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getOldY() {
        return oldY;
    }

    public void setOldY(int oldY) {
        this.oldY = oldY;
    }

    public int getOldX() {
        return oldX;
    }

    public void setOldX(int oldX) {
        this.oldX = oldX;
    }

    public boolean isJoined() {
        return joined;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

    public int getOldY2() {
        return oldY2;
    }

    public void setOldY2(int oldY2) {
        this.oldY2 = oldY2;
    }

    public int getOldX2() {
        return oldX2;
    }

    public void setOldX2(int oldX2) {
        this.oldX2 = oldX2;
    }
}
