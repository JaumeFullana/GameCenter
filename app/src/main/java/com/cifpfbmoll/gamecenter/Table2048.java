package com.cifpfbmoll.gamecenter;

import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Random;

public class Table2048 {

    private Game2048Activity main;
    private Cell2048 [][] cells;
    private ArrayList <String> disponibleCells;

    public Table2048(Game2048Activity main, Cell2048 [][] cells) {
        this.main=main;
        this.cells=cells;
        this.initCells();
        this.fillDisponibleCells();
    }

    public Cell2048[][] getCells() {
        return cells;
    }

    public void setCells(Cell2048[][] cells) {
        this.cells = cells;
    }

    public Game2048Activity getMain() {
        return main;
    }

    public void setMain(Game2048Activity main) {
        this.main = main;
    }

    public ArrayList<String> getDisponibleCells() {
        return disponibleCells;
    }

    public void setDisponibleCells(ArrayList<String> disponibleCells) {
        this.disponibleCells = disponibleCells;
    }

    private void initCells(){
        for (int i=0; i<this.cells.length;i++){
            for (int j=0; j<this.cells[i].length;j++){
                this.cells[i][j]=new Cell2048(0);
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
     * Method that fills the ArrayList disponibleCells with the position of the cells that aren't
     * occupied in the Matrix cells.
     */
    public void fillDisponibleCells(){
        this.setDisponibleCells(new ArrayList<String>());
        for (int i=0;i<4;i++){
            for (int j=0;j<4;j++){
                if(this.getCells()[i][j].getValue()==0) {
                    this.getDisponibleCells().add(i + "" + j);
                }
            }
        }
    }

    /**
     * Method that assign a number in a position of the matrix cells. The String cell is the position
     * and the int number is the number to assign.
     * @param cell String with the position, charAt index 0 is the y position and charAt index 1 is the x position.
     * @param number int the value to assign in the position of the matrix.
     */
    public int[] fillCell(String cell, int number){
        int i=Character.getNumericValue(cell.charAt(0));
        int j=Character.getNumericValue(cell.charAt(1));
        this.getCells()[i][j].setValue(number);
        int [] pos=new int[2];
        pos[0]=i;
        pos[1]=j;
        return pos;
    }

    public int checkState(){
        //state -1 lose, state 0 still playing, state 1 win
        int state=-1;
        int i=0;
        while (i<4 & state==-1){
            int j=0;
            while (j<4 & state==-1){
                if(this.getCells()[i][j].getValue()!=0) {
                    if(this.getCells()[i][j].getValue()==2048){
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

    public int checkMovements(){
        //state -1 lose, state 0 still playing
        //s'ha de diferenciar si va cap adalt o cap abaix dreta o esquerra
        int state=-1;
        int i=0;
        while (i<4 && state==-1){
            int j=0;
            while (j<4 && state==-1){
                if (i!=0) {
                    if(this.getCells()[i-1][j].getValue()==this.getCells()[i][j].getValue()) {
                        state = 0;
                    }
                }
                if (i!=3){
                    if(this.getCells()[i+1][j].getValue()==this.getCells()[i][j].getValue()) {
                        state = 0;
                    }
                }
                if (j!=3){
                    if(this.getCells()[i][j+1].getValue()==this.getCells()[i][j].getValue()) {
                        state = 0;
                    }
                }
                if (j!=0){
                    if(this.getCells()[i][j-1].getValue()==this.getCells()[i][j].getValue()) {
                        state = 0;
                    }
                }
                j++;
            }
            i++;
        }
        return state;
    }

    public String getRandomEmptyCell(){
        Random r=new Random();
        int position=r.nextInt(this.getDisponibleCells().size());
        String cell=this.getDisponibleCells().get(position);
        return cell;
    }

    private void resetCells(){
        for (int i=0;i<cells.length;i++){
            for (int j=0; j<cells[i].length;j++){
                cells[i][j].setOldY(i);
                cells[i][j].setOldX(j);
                cells[i][j].setJoined(false);
            }
        }
    }

    public boolean moveNumbers(MotionEvent event1, MotionEvent event2, double yDiff, double xDiff){
        boolean moved=false;
        int loops=0;
        boolean moving;
        int iAux=0;
        int jAux=0;
        boolean joined;

        resetCells();

        if (xDiff>yDiff) {
            //left
            if (event1.getX() > event2.getX()) {
                for (int i = 0; i < 4; i++) {
                    moving = true;
                    joined = false;
                    while (moving) {
                        moving = false;
                        for (int j = 0; j < 3; j++) {
                            if (this.getCells()[i][j].getValue() == 0 && this.getCells()[i][j + 1].getValue() != 0) {
                                this.getCells()[i][j]=new Cell2048(this.getCells()[i][j + 1]);
                                this.getCells()[i][j + 1]=new Cell2048(0);
                                moving = true;
                            }
                            else if (this.getCells()[i][j].getValue() == this.getCells()[i][j + 1].getValue()
                                    && this.getCells()[i][j].getValue()!=0) {
                                if ((i==iAux && j==jAux && joined) || (i==iAux && j+1==jAux && joined)) {

                                }
                                else {
                                    this.getCells()[i][j+1].setJoined(true);
                                    this.getCells()[i][j+1].setOldY2(this.getCells()[i][j].getOldY());
                                    this.getCells()[i][j+1].setOldX2(this.getCells()[i][j].getOldX());
                                    this.getCells()[i][j]=new Cell2048(this.getCells()[i][j + 1]);
                                    this.getCells()[i][j].setValue(this.getCells()[i][j].getValue() * 2);
                                    main.setPuntuacion(this.getCells()[i][j].getValue());
                                    this.getCells()[i][j + 1]=new Cell2048(0);
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
            }
            //right
            else{
                for (int i = 0; i < 4; i++) {
                    moving = true;
                    joined=false;
                    while (moving) {
                        moving = false;
                        for (int j = 3; j > 0; j--) {
                            if (this.getCells()[i][j].getValue() == 0 && this.getCells()[i][j - 1].getValue() != 0) {
                                this.getCells()[i][j]=new Cell2048(this.getCells()[i][j - 1]);
                                this.getCells()[i][j - 1]=new Cell2048(0);
                                moving = true;
                            }
                            else if (this.getCells()[i][j].getValue() == this.getCells()[i][j - 1].getValue()
                                    && this.getCells()[i][j].getValue()!=0) {
                                if ((i==iAux && j==jAux && joined) || (i==iAux && j-1==jAux && joined)) {

                                } else{
                                    this.getCells()[i][j-1].setJoined(true);
                                    this.getCells()[i][j-1].setOldY2(this.getCells()[i][j].getOldY());
                                    this.getCells()[i][j-1].setOldX2(this.getCells()[i][j].getOldX());
                                    this.getCells()[i][j]=new Cell2048(this.getCells()[i][j - 1]);
                                    this.getCells()[i][j].setValue(this.getCells()[i][j].getValue() * 2);
                                    main.setPuntuacion(this.getCells()[i][j].getValue());
                                    this.getCells()[i][j - 1]=new Cell2048(0);
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
            }
        }
        else {
            //up
            if (event1.getY() > event2.getY()) {
                for (int i = 0; i < 4; i++) {
                    moving = true;
                    joined=false;
                    while (moving) {
                        moving = false;
                        for (int j = 0; j < 3; j++) {
                            if (this.getCells()[j][i].getValue() == 0 && this.getCells()[j+1][i].getValue() != 0) {
                                this.getCells()[j][i]=new Cell2048(this.getCells()[j+1][i]);
                                this.getCells()[j+1][i]=new Cell2048(0);
                                moving = true;
                            }
                            else if (this.getCells()[j][i].getValue() == this.getCells()[j+1][i].getValue()
                                    && this.getCells()[j][i].getValue()!=0) {
                                if ((i==iAux && j==jAux && joined) || (i==iAux && j+1==jAux && joined)) {

                                } else {
                                    this.getCells()[j+1][i].setJoined(true);
                                    this.getCells()[j+1][i].setOldY2(this.getCells()[j][i].getOldY());
                                    this.getCells()[j+1][i].setOldX2(this.getCells()[j][i].getOldX());
                                    this.getCells()[j][i]=new Cell2048(this.getCells()[j+1][i]);
                                    this.getCells()[j][i].setValue(this.getCells()[j][i].getValue() * 2);
                                    main.setPuntuacion(this.getCells()[j][i].getValue());
                                    this.getCells()[j + 1][i]=new Cell2048(0);
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
            }
            //down
            else {
                for (int i = 0; i < 4; i++) {
                    moving = true;
                    joined=false;
                    while (moving) {
                        moving = false;
                        for (int j = 3; j > 0; j--) {
                            if (this.getCells()[j][i].getValue() == 0 && this.getCells()[j-1][i].getValue() != 0) {
                                this.getCells()[j][i]=new Cell2048(this.getCells()[j-1][i]);
                                this.getCells()[j-1][i]=new Cell2048(0);
                                moving = true;
                            }
                            else if (this.getCells()[j][i].getValue() == this.getCells()[j-1][i].getValue()
                                    && this.getCells()[j][i].getValue()!=0) {
                                if ((i==iAux && j==jAux && joined) || (i==iAux && j-1==jAux && joined)) {

                                } else{
                                    this.getCells()[j-1][i].setJoined(true);
                                    this.getCells()[j-1][i].setOldY2(this.getCells()[j][i].getOldY());
                                    this.getCells()[j-1][i].setOldX2(this.getCells()[j][i].getOldX());
                                    this.getCells()[j][i]=new Cell2048(this.getCells()[j-1][i]);
                                    this.getCells()[j][i].setValue(this.getCells()[j][i].getValue() * 2);
                                    main.setPuntuacion(this.getCells()[j][i].getValue());
                                    this.getCells()[j - 1][i]=new Cell2048(0);
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
            }
        }
        if (loops>4){
            moved=true;
        }
        //devuelve booleano para saber si se ha movido algo o no ja que si nada se ha movido no se
        //va a generar un numero nuevo ni ara falta refrescar la interfaz grafica
        return moved;
    }
}
