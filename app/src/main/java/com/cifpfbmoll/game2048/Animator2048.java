package com.cifpfbmoll.game2048;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.widget.TextView;

import java.util.ArrayList;

public class Animator2048 {

    private Game2048Activity game2048;
    private int gridWidth;
    private int gridHeight;
    private float movementDistance;
    private int movementDivisor;
    private final int MOVEMENT_DURATION = 150;
    private final int JOIN_DURATION = 75;

    public Animator2048(Game2048Activity game2048, int tableSize) {
        this.game2048 = game2048;
        this.movementDivisor = tableSize * 2;
    }

    public void setGridWidth(int gridWidth) {
        this.gridWidth = gridWidth;
    }

    public void setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
    }

    /**
     * Method that loops through the cells matrix looking for every cell that has been moved in the
     * last movement and give them an animation to go from the old position the new one.
     * @param cells game cells matrix
     * @return ArrayList < Animator > to start each animator.
     */
    public ArrayList <Animator> animateMovememnts(Cell2048 [][] cells){

        ArrayList <Animator> anims = new ArrayList<>();

        if (this.movementDistance != ((this.gridWidth + this.gridHeight) / movementDivisor)) {
            this.movementDistance = ((this.gridWidth + this.gridHeight) / movementDivisor);
        }

        for (int i=0; i< cells.length; i++){
            for (int j=0; j<cells[i].length; j++){
                if(cells[i][j].getValue()!=0){
                    TextView textView = getTextView(cells[i][j].getOldY(), cells[i][j].getOldX());
                    textView.setElevation(1f);
                    animateCellMovement(cells, anims, i, j, textView);
                }
            }
        }
        return anims;
    }

    /**
     * Method that give one or more animations to a textView. The animations can be a movement
     * animation, the view is moved from one point to another, or a joined animation, represents when
     * two views are joined, the view that keeps the position with the new value is resized a bit bigger
     * during 75 miliseconds and then resized to its original size.
     * @param cells game cells matrix
     * @param anims Animators ArrayList
     * @param i index of a matrix row
     * @param j index of a matrix column
     * @param textView textView to animate
     */
    private void animateCellMovement(Cell2048[][] cells, ArrayList<Animator> anims, int i, int j, TextView textView) {
        if (cells[i][j].getOldY() == i){
            simpleMovementAnimation( anims, j - cells[i][j].getOldX(), textView, "translationX");
            if (cells[i][j].isJoined()){

                TextView textView2 = getTextView(cells[i][j].getOldY2(), cells[i][j].getOldX2());
                textView2.setElevation(1f);
                Animator anim2Extra = simpleMovementAnimation(anims, j - cells[i][j].getOldX2(), textView2, "translationX");
                TextView textViewJoin = getTextView(i, j);
                joinAnimation(anim2Extra, textViewJoin);
            }
        }
        else {
            simpleMovementAnimation(anims, i - cells[i][j].getOldY(), textView, "translationY");
            if (cells[i][j].isJoined()){

                TextView textView2 = getTextView(cells[i][j].getOldY2(), cells[i][j].getOldX2());
                textView2.setElevation(1f);
                Animator anim2Extra = simpleMovementAnimation( anims, i - cells[i][j].getOldY2(), textView2, "translationY");
                TextView textViewJoin = getTextView(i, j);
                joinAnimation(anim2Extra, textViewJoin);
            }
        }
    }

    /**
     * Get a textView, that is a cell of the 2048 table, giving its position in the table
     * @param y row number where is placed the textView
     * @param x column number where is placed the textView
     * @return TextView the textView
     */
    private TextView getTextView(int y, int x) {
        int id=game2048.getResources().getIdentifier("p"+ y+""
                + x,"id",game2048.getPackageName());
        return (TextView) game2048.findViewById(id);
    }

    /**
     * Method that add the join animation to the textView passed by parameter.
     * @param anim2Extra Animator to set the join animation when this animatior ends
     * @param textViewJoin View to animate
     */
    private void joinAnimation(Animator anim2Extra, TextView textViewJoin) {
        Animator animJoin1 = ObjectAnimator.ofFloat(textViewJoin,"scaleX",1f,1.2f).setDuration(JOIN_DURATION);
        Animator animJoin2 = ObjectAnimator.ofFloat(textViewJoin,"scaleY",1f,1.2f).setDuration(JOIN_DURATION);
        Animator animJoin3 = ObjectAnimator.ofFloat(textViewJoin,"scaleX",1.2f,1f).setDuration(JOIN_DURATION);
        Animator animJoin4 = ObjectAnimator.ofFloat(textViewJoin,"scaleY",1.2f,1f).setDuration(JOIN_DURATION);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.play(animJoin1).with(animJoin2).before(animJoin3);
        animatorSet.play(animJoin3).with(animJoin4);
        anim2Extra.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animatorSet.start();
            }
        });
    }

    /**
     *  Give to the text view passed by parameter a movement animation.
     * @param anims Animators ArrayList
     * @param textView textView to animate
     * @param movement the distance of the movement
     * @param translationType the animation value, can be translationX or translationY
     * @return anim2 animator that we may use in another method
     */
    private Animator simpleMovementAnimation(ArrayList<Animator> anims, int movement, TextView textView, String translationType) {
        Animator anim = ObjectAnimator.ofFloat(textView, translationType,0f,movementDistance * movement).setDuration(MOVEMENT_DURATION);
        Animator anim2 = ObjectAnimator.ofFloat(textView, translationType,0f,0f).setDuration(0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                anim2.start();
                textView.setElevation(0f);
            }
        });
        anims.add(anim);
        return anim2;
    }
}
