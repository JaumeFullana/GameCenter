package com.cifpfbmoll.game2048;

import android.view.MotionEvent;

import com.cifpfbmoll.game2048.Cell2048;
import com.cifpfbmoll.game2048.Game2048Activity;

import java.util.ArrayList;
import java.util.Random;

public class Table2048 {

    private Game2048Activity main;
    private Cell2048[][] cells;
    private Cell2048 [][] cellsCopy;
    private ArrayList <String> availableCells;

    public Table2048(Game2048Activity main, Cell2048 [][] cells, Cell2048 [][] cellsCopy) {
        this.main=main;
        this.cells=cells;
        this.initCells(this.cells);
        this.cellsCopy=cellsCopy;
        this.fillDisponibleCells();
    }

    public Cell2048[][] getCells() {
        return cells;
    }

    public Cell2048[][] getCellsCopy() {
        return cellsCopy;
    }

    public void setCellsCopy(Cell2048[][] cellsCopy) {
        this.cellsCopy = cellsCopy;
    }

    public ArrayList<String> getAvailableCells() {
        return availableCells;
    }

    /**
     * Initiates a cells2048 matrix.
     * @param cells matrix to be initiated.
     */
    public void initCells(Cell2048 [][] cells){
        for (int i=0; i<cells.length;i++){
            for (int j=0; j<cells[i].length;j++){
                cells[i][j]=new Cell2048(0);
            }
        }
    }

    /**
     * Method that generates a number 2 (70% posibility) or 4(30% posibility).
     * @return int 2 or 4
     */
    public int generateNumber(){
        Random r=new Random();
        int number=2;
        int probability=r.nextInt(10);
        if (probability>7){
            number=4;
        }
        return number;
    }

    /**
     * Method that fills the ArrayList availableCells with the position of the cells that aren't
     * occupied in the Matrix cells.
     */
    public void fillDisponibleCells(){
        this.availableCells = new ArrayList<>();
        for (int i=0;i<cells.length;i++){
            for (int j=0;j<cells[i].length;j++){
                if(this.cells[i][j].getValue()==0) {
                    this.availableCells.add(i + "" + j);
                }
            }
        }
    }

    /**
     * Method that assign a number in a position of the matrix cells. The String cell is the position
     * and the int number is the number to assign.
     * @param cell String with the position, charAt index 0 is the y position and charAt index 1 is the x position.
     * @param number int the value to assign in the position of the matrix.
     * @return return the position where the number is assigned.
     */
    public int[] fillCell(String cell, int number){
        int i=Character.getNumericValue(cell.charAt(0));
        int j=Character.getNumericValue(cell.charAt(1));
        this.cells[i][j].setValue(number);
        int [] pos=new int[2];
        pos[0]=i;
        pos[1]=j;
        return pos;
    }

    /**
     * Method that copies a cell2048 matrix in another matrix.
     * @param originalCells matrix to be copied
     * @param copyCells copied matrix
     */
    public void copyCells(Cell2048 [][] originalCells, Cell2048 [][] copyCells){
        for (int i=0; i<originalCells.length; i++){
            for (int j=0; j<originalCells[i].length;j++){
                copyCells[i][j]=new Cell2048(originalCells[i][j]);
            }
        }
    }

    /**
     * Checks if the the game is losed, playable or won. The return value can be -1 (lost),
     * state 0 (still playable) and state 1 (won).
     * @return an int representing the state of the game
     */
    public int checkState(){
        int state=-1;
        int i=0;
        while (i<cells.length & state!=1){
            int j=0;
            while (j<cells[i].length & state!=1){
                if(this.cells[i][j].getValue()!=0) {
                    if(this.cells[i][j].getValue()==2048){
                        state=1;
                    }
                } else{
                    state=0;
                }
                j++;
            }
            i++;
        }
        if (state==-1){
            state=checkMovements();
        }
        return state;
    }

    /**
     * Checks if there is available movements in the grid. The return value can be -1 (lost),
     * state 0 (still playable) and state 1 (won).
     * @return an int representing the state of the game
     */
    public int checkMovements(){
        int state=-1;
        int i=0;
        while (i<cells.length && state==-1){
            int j=0;
            while (j<cells[i].length && state==-1){
                if (i!=0) {
                    if(this.cells[i-1][j].getValue()==this.cells[i][j].getValue()) {
                        state = 0;
                    }
                }
                if (i!=cells.length-1){
                    if(this.cells[i+1][j].getValue()==this.cells[i][j].getValue()) {
                        state = 0;
                    }
                }
                if (j!=cells[i].length-1){
                    if(this.cells[i][j+1].getValue()==this.cells[i][j].getValue()) {
                        state = 0;
                    }
                }
                if (j!=0){
                    if(this.cells[i][j-1].getValue()==this.cells[i][j].getValue()) {
                        state = 0;
                    }
                }
                j++;
            }
            i++;
        }
        return state;
    }

    /**
     * Gets one of the empty cells randomly.
     * @return the cell position
     */
    public String getRandomEmptyCell(){
        Random r=new Random();
        int position=r.nextInt(this.availableCells.size());
        return this.availableCells.get(position);
    }

    /**
     * Resets the oldY, oldX and joined values of all the cells of the cells matrix.
     */
    private void resetCells(){
        for (int i=0;i<cells.length;i++){
            for (int j=0; j<cells[i].length;j++){
                cells[i][j].setOldY(i);
                cells[i][j].setOldX(j);
                cells[i][j].setJoined(false);
            }
        }
    }

    /**
     * Method that check the information of the fling and moves the numbers depending of the direction
     * of that fling.
     * @param event1 where the fling starts
     * @param event2 where the fling ends
     * @param yDiff difference between the y axis in the flings start and flings end
     * @param xDiff difference between the x axis in the flings start and flings end
     * @return boolean to know if something has been moved
     */
    public boolean moveNumbers(MotionEvent event1, MotionEvent event2, double yDiff, double xDiff){
        boolean moved=false;
        int loops;
        Cell2048 [][] temporalCells=new Cell2048[this.cells.length][this.cells[0].length];
        this.copyCells(this.cells,temporalCells);
        resetCells();

        if (xDiff>yDiff) {
            if (event1.getX() > event2.getX()) {
                loops = moveNumbersToLeft();
            }
            else{
                loops = moveNumbersToRight();
            }
        }
        else {
            if (event1.getY() > event2.getY()) {
                loops = moveNumbersToTop();
            }
            else {
                loops = moveNumbersToBottom();
            }
        }
        if (loops>cells.length){
            moved=true;
            this.copyCells(temporalCells,this.cellsCopy);
        }

        return moved;
    }

    /**
     * Method to move the numbers to the bottom.
     * @return the numbers of loops done to do all the movements.
     */
    private int moveNumbersToBottom() {
        int loops=0;
        int iAux=0;
        int jAux=0;
        boolean joined;
        boolean moving;
        for (int i = 0; i < cells.length; i++) {
            moving = true;
            joined=false;
            while (moving) {
                moving = false;
                for (int j = cells[i].length-1; j > 0; j--) {
                    if (this.cells[j][i].getValue() == 0 && this.cells[j-1][i].getValue() != 0) {
                        this.cells[j][i]=new Cell2048(this.cells[j-1][i]);
                        this.cells[j-1][i]=new Cell2048(0);
                        moving = true;
                    }
                    else if (this.cells[j][i].getValue() == this.cells[j-1][i].getValue()
                            && this.cells[j][i].getValue()!=0) {
                        if ((i== iAux && j== jAux && joined) || (i== iAux && j-1== jAux && joined)) {

                        } else{
                            this.cells[j-1][i].setJoined(true);
                            this.cells[j-1][i].setOldY2(this.cells[j][i].getOldY());
                            this.cells[j-1][i].setOldX2(this.cells[j][i].getOldX());
                            this.cells[j][i]=new Cell2048(this.cells[j-1][i]);
                            this.cells[j][i].setValue(this.cells[j][i].getValue() * 2);
                            main.setScore(this.cells[j][i].getValue());
                            this.cells[j - 1][i]=new Cell2048(0);
                            moving = true;
                            joined = true;
                            iAux = i;
                            jAux = j;
                        }
                    }
                }
                loops++;
            }
        }
        return loops;
    }

    /**
     * Method to move the numbers to the top.
     * @return the numbers of loops done to do all the movements.
     */
    private int moveNumbersToTop() {
        int loops=0;
        int iAux=0;
        int jAux=0;
        boolean joined;
        boolean moving;
        for (int i = 0; i < cells.length; i++) {
            moving = true;
            joined=false;
            while (moving) {
                moving = false;
                for (int j = 0; j < cells[i].length-1; j++) {
                    if (this.cells[j][i].getValue() == 0 && this.cells[j+1][i].getValue() != 0) {
                        this.cells[j][i]=new Cell2048(this.cells[j+1][i]);
                        this.cells[j+1][i]=new Cell2048(0);
                        moving = true;
                    }
                    else if (this.cells[j][i].getValue() == this.cells[j+1][i].getValue()
                            && this.cells[j][i].getValue()!=0) {
                        if ((i== iAux && j== jAux && joined) || (i== iAux && j+1== jAux && joined)) {

                        } else {
                            this.cells[j+1][i].setJoined(true);
                            this.cells[j+1][i].setOldY2(this.cells[j][i].getOldY());
                            this.cells[j+1][i].setOldX2(this.cells[j][i].getOldX());
                            this.cells[j][i]=new Cell2048(this.cells[j+1][i]);
                            this.cells[j][i].setValue(this.cells[j][i].getValue() * 2);
                            main.setScore(this.cells[j][i].getValue());
                            this.cells[j + 1][i]=new Cell2048(0);
                            moving = true;
                            joined = true;
                            iAux = i;
                            jAux = j;
                        }
                    }
                }
                loops++;
            }
        }
        return loops;
    }

    /**
     * Method to move the numbers to the right.
     * @return the numbers of loops done to do all the movements.
     */
    private int moveNumbersToRight() {
        int loops=0;
        int iAux=0;
        int jAux=0;
        boolean joined;
        boolean moving;
        for (int i = 0; i < cells.length; i++) {
            moving = true;
            joined=false;
            while (moving) {
                moving = false;
                for (int j = cells[i].length-1; j > 0; j--) {
                    if (this.cells[i][j].getValue() == 0 && this.cells[i][j - 1].getValue() != 0) {
                        this.cells[i][j]=new Cell2048(this.cells[i][j - 1]);
                        this.cells[i][j - 1]=new Cell2048(0);
                        moving = true;
                    }
                    else if (this.cells[i][j].getValue() == this.cells[i][j - 1].getValue()
                            && this.cells[i][j].getValue()!=0) {
                        if ((i== iAux && j== jAux && joined) || (i== iAux && j-1== jAux && joined)) {

                        } else{
                            this.cells[i][j-1].setJoined(true);
                            this.cells[i][j-1].setOldY2(this.cells[i][j].getOldY());
                            this.cells[i][j-1].setOldX2(this.cells[i][j].getOldX());
                            this.cells[i][j]=new Cell2048(this.cells[i][j - 1]);
                            this.cells[i][j].setValue(this.cells[i][j].getValue() * 2);
                            main.setScore(this.cells[i][j].getValue());
                            this.cells[i][j - 1]=new Cell2048(0);
                            moving = true;
                            joined = true;
                            iAux = i;
                            jAux = j;
                        }
                    }
                }
                loops++;
            }
        }
        return loops;
    }

    /**
     * Method to move the numbers to the left.
     * @return the numbers of loops done to do all the movements.
     */
    private int moveNumbersToLeft() {
        int loops=0;
        int iAux=0;
        int jAux=0;
        boolean joined;
        boolean moving;
        for (int i = 0; i < cells.length; i++) {
            moving = true;
            joined = false;
            while (moving) {
                moving = false;
                for (int j = 0; j < cells[i].length-1; j++) {
                    if (this.cells[i][j].getValue() == 0 && this.cells[i][j + 1].getValue() != 0) {
                        this.cells[i][j]=new Cell2048(this.cells[i][j + 1]);
                        this.cells[i][j + 1]=new Cell2048(0);
                        moving = true;
                    }
                    else if (this.cells[i][j].getValue() == this.cells[i][j + 1].getValue()
                            && this.cells[i][j].getValue()!=0) {
                        if ((i== iAux && j== jAux && joined) || (i== iAux && j+1== jAux && joined)) {

                        }
                        else {
                            this.cells[i][j+1].setJoined(true);
                            this.cells[i][j+1].setOldY2(this.cells[i][j].getOldY());
                            this.cells[i][j+1].setOldX2(this.cells[i][j].getOldX());
                            this.cells[i][j]=new Cell2048(this.cells[i][j + 1]);
                            this.cells[i][j].setValue(this.cells[i][j].getValue() * 2);
                            main.setScore(this.cells[i][j].getValue());
                            this.cells[i][j + 1]=new Cell2048(0);
                            moving = true;
                            joined = true;
                            iAux = i;
                            jAux = j;
                        }
                    }
                }
                loops++;
            }
        }
        return loops;
    }
}
