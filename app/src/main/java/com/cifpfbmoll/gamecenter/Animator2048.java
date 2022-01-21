package com.cifpfbmoll.gamecenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.widget.TextView;

import java.util.ArrayList;

public class Animator2048 {


    private Game2048Activity game2048;
    private final float movementDistance=243f;
    private final int movementDuration=150;
    private final int joinDuration=75;


    public Animator2048(Game2048Activity game2048) {
        this.game2048 = game2048;
    }

    //metode que recorr sa matriu on hi ha ses anitgues posicions des objectes i crea s'animacio
    //refactoritza
    public ArrayList <Animator> animateMovememnts(Cell2048 [][] cells){

        ArrayList <Animator> anims=new ArrayList<>();

        for (int i=0; i< cells.length;i++){
            for (int j=0; j<cells[i].length;j++){

                if(cells[i][j].getValue()!=0){
                    int id=game2048.getResources().getIdentifier("p"+cells[i][j].getOldY()+""
                            +cells[i][j].getOldX(),"id",game2048.getPackageName());
                    TextView textView=(TextView) game2048.findViewById(id);
                    textView.setElevation(1f);

                    if (cells[i][j].getOldY()==i){
                        int movement=j-cells[i][j].getOldX();
                        Animator anim= ObjectAnimator.ofFloat(textView,"translationX",0f,movementDistance*movement).setDuration(movementDuration);
                        Animator anim2= ObjectAnimator.ofFloat(textView,"translationX",0f,0f).setDuration(0);
                        anim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                anim2.start();
                                textView.setElevation(0f);
                            }
                        });
                        anims.add(anim);

                        if (cells[i][j].isJoined()){
                            int id2=game2048.getResources().getIdentifier("p"+cells[i][j].getOldY2()+""
                                    +cells[i][j].getOldX2(),"id",game2048.getPackageName());
                            TextView textView2 =(TextView) game2048.findViewById(id2);
                            textView2.setElevation(1f);
                            int movementExtra = j-cells[i][j].getOldX2();
                            Animator animExtra = ObjectAnimator.ofFloat(textView2,"translationX",0f,movementDistance*movementExtra).setDuration(movementDuration);
                            Animator anim2Extra = ObjectAnimator.ofFloat(textView2,"translationX",0f,0f).setDuration(0);
                            animExtra.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    anim2Extra.start();
                                    textView2.setElevation(0f);
                                }
                            });
                            int idJoin=game2048.getResources().getIdentifier("p"+i+""
                                    +j,"id",game2048.getPackageName());
                            TextView textViewJoin =(TextView) game2048.findViewById(idJoin);
                            Animator animJoin1 = ObjectAnimator.ofFloat(textViewJoin,"scaleX",1f,1.2f).setDuration(joinDuration);
                            Animator animJoin2 = ObjectAnimator.ofFloat(textViewJoin,"scaleY",1f,1.2f).setDuration(joinDuration);
                            Animator animJoin3 = ObjectAnimator.ofFloat(textViewJoin,"scaleX",1.2f,1f).setDuration(joinDuration);
                            Animator animJoin4 = ObjectAnimator.ofFloat(textViewJoin,"scaleY",1.2f,1f).setDuration(joinDuration);
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
                            anims.add(animExtra);
                        }
                    }
                    else {
                        int movement=i-cells[i][j].getOldY();
                        Animator anim= ObjectAnimator.ofFloat(textView,"translationY",0f,movementDistance*movement).setDuration(movementDuration);
                        Animator anim2= ObjectAnimator.ofFloat(textView,"translationY",0f,0f).setDuration(0);
                        anim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                anim2.start();
                                textView.setElevation(0f);
                            }
                        });
                        anims.add(anim);

                        if (cells[i][j].isJoined()){
                            int id2=game2048.getResources().getIdentifier("p"+cells[i][j].getOldY2()+""
                                    +cells[i][j].getOldX2(),"id",game2048.getPackageName());
                            TextView textView2=(TextView) game2048.findViewById(id2);
                            textView2.setElevation(1f);
                            int movementExtra=i-cells[i][j].getOldY2();
                            Animator animExtra= ObjectAnimator.ofFloat(textView2,"translationY",0f,movementDistance*movementExtra).setDuration(movementDuration);
                            Animator anim2Extra= ObjectAnimator.ofFloat(textView2,"translationY",0f,0f).setDuration(0);
                            animExtra.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    anim2Extra.start();
                                    textView2.setElevation(0f);
                                }
                            });
                            int idJoin=game2048.getResources().getIdentifier("p"+i+""
                                    +j,"id",game2048.getPackageName());
                            TextView textViewJoin =(TextView) game2048.findViewById(idJoin);
                            Animator animJoin1 = ObjectAnimator.ofFloat(textViewJoin,"scaleX",1f,1.2f).setDuration(joinDuration);
                            Animator animJoin2 = ObjectAnimator.ofFloat(textViewJoin,"scaleY",1f,1.2f).setDuration(joinDuration);
                            Animator animJoin3 = ObjectAnimator.ofFloat(textViewJoin,"scaleX",1.2f,1f).setDuration(joinDuration);
                            Animator animJoin4 = ObjectAnimator.ofFloat(textViewJoin,"scaleY",1.2f,1f).setDuration(joinDuration);
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
                            anims.add(animExtra);
                        }
                    }
                }
            }
        }
        return anims;
    }
}
