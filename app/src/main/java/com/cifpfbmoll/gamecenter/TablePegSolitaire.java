package com.cifpfbmoll.gamecenter;

public class TablePegSolitaire {

    //-1 not available, 0 occupied, 1 free
    private int [][] cells;
    private int balls;

    public TablePegSolitaire() {
        this.balls=0;
    }

    public int[][] getCells() {
        return cells;
    }

    public void setCells(int[][] cells) {
        this.cells = cells;
    }

    public int getBalls() {
        return balls;
    }

    /**
     * Initialitzes the matrix and set it like an english board peg solitaire.
     */
    public void initEnglishBoard(){
        this.cells=new int [7][7];
        this.cells[3][3]=1;
        for (int i=0;i<7;i++){
            if (i==2){
                i=5;
            }
            this.cells[i][0] = -1;
            this.cells[i][1] = -1;
            this.cells[i][5] = -1;
            this.cells[i][6] = -1;
        }
    }

    /**
     * Counts the balls of the board
     */
    public void countBalls(){
        for (int i=0; i<this.cells.length;i++){
            for (int j=0; j<this.cells[i].length;j++){
                if (this.cells[i][j]==0){
                    balls++;
                }
            }
        }
    }

    /**
     * Method that change the state of the matrix positions that are participating in the movement.
     * Set the old poisition(where the drag started) and the ballToDelete position (position between
     * old position and new position) to 1(free) and the new position to 0(occupied). After that,
     * the method substract 1 from int balls.
     * @param oldPosition position where the drag started
     * @param newPosition position where the ball is droped
     * @param ballToDelete poisiton of the ball which is in the middle of the two other positions
     */
    public void moveBall(int [] oldPosition, int [] newPosition, int [] ballToDelete){
        this.getCells()[oldPosition[0]][oldPosition[1]] = 1;
        this.getCells()[newPosition[0]][newPosition[1]] = 0;
        this.getCells()[ballToDelete[0]][ballToDelete[1]]= 1;
        this.balls--;
    }

    /**
     * Check if the movement is possible. The method receive two int Arrays, one with the position where
     * is the ball and another with the position where the ball wants to go. If the moviment is posible
     * the method returns an int array with the position of the ball which is in the middle of the two position.
     * If the moviment isn't posible it returns the int array with a -1 in index 0.
     * @param oldPosition position where the ball is
     * @param newPosition position where the ball wants to go
     * @return An int array with the position of the ball in the middle of oldPosition and newPoisiton
     * or an int array with a -1 in the index 0(if the movement is not posible).
     */
    public int [] checkMovement(int [] oldPosition, int [] newPosition){
        int [] positionToDelete=new int[2];
        positionToDelete[0]=-1;
        if(this.getCells()[newPosition[0]][newPosition[1]]==1){
            if (newPosition[0]==oldPosition[0]-2 || newPosition[0]==oldPosition[0]+2){
                if(newPosition[1]==oldPosition[1] && this.getCells()[(newPosition[0]+oldPosition[0])/2][newPosition[1]]==0){
                    positionToDelete[0]=(newPosition[0]+oldPosition[0])/2;
                    positionToDelete[1]=newPosition[1];
                }
            } else if (newPosition[1]==oldPosition[1]-2 || newPosition[1]==oldPosition[1]+2){
                if(newPosition[0]==oldPosition[0] && this.getCells()[newPosition[0]][(newPosition[1]+oldPosition[1])/2]==0){
                    positionToDelete[0]=newPosition[0];
                    positionToDelete[1]=(newPosition[1]+oldPosition[1])/2;
                }
            }
        }
        return positionToDelete;
    }

    /**
     * Method that check if the game is over. Returns -1 if is finished and the player has lost,
     *  0 if the game is still playable or 1 if the game is finished and the played wined.
     * @return int -1 finished and lost, 0 still playable, 1 finished and wined
     */
    public int isFinished(){
        //0 still playable, -1 finished lose, 1 finished win
        int state=-1;
        int ballsCounter=0;
        int [] midlePosition;
        int [] realPosition=new int [2];
        int [] posiblePosition=new int [2];
        int i=0;
        while ( i<this.getCells().length && state!=0){
            int j=0;
            while ( j<this.getCells()[i].length && state!=0){
                if (this.getCells()[i][j]==0){
                    ballsCounter++;
                    realPosition[0]=i;
                    realPosition[1]=j;
                    int k=0;
                    while (k<4 && state!=0){
                        if (k==0){
                            posiblePosition[0]=i+2;
                            posiblePosition[1]=j;
                        } else if(k==1){
                            posiblePosition[0]=i-2;
                            posiblePosition[1]=j;
                        } else if(k==2){
                            posiblePosition[0]=i;
                            posiblePosition[1]=j+2;
                        } else{
                            posiblePosition[0]=i;
                            posiblePosition[1]=j-2;
                        }
                        //Check if the index aren't out of bounds
                        if (posiblePosition[0]<this.getCells().length && posiblePosition[0]>-1
                            && posiblePosition[1]<this.getCells()[i].length && posiblePosition[1]>-1) {

                            midlePosition = this.checkMovement(realPosition, posiblePosition);
                            if(midlePosition[0]!=-1){
                                state=0;
                            }
                        }
                        k++;
                    }
                }
                j++;
            }
            i++;
        }
        if (ballsCounter==1 && state!=0){
            state=1;
        }
        return state;
    }
}
