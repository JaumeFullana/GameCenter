package com.cifpfbmoll.gamecenter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView dorado1=(ImageView) findViewById(R.id.dorado1);
        ImageView dorado2=(ImageView) findViewById(R.id.dorado2);
        ImageView azul1=(ImageView) findViewById(R.id.azul1);
        ImageView azul2=(ImageView) findViewById(R.id.azul2);
        ImageView rojo1=(ImageView) findViewById(R.id.rojo1);
        ImageView rojo2=(ImageView) findViewById(R.id.rojo2);
        ImageView lightning=(ImageView) findViewById(R.id.lightningBackground);
        ImageView lightningLight=(ImageView) findViewById(R.id.lightningLight);
        ImageView jackGames=(ImageView) findViewById(R.id.jackGames);

        AnimatorSet set = new AnimatorSet();

        Animator aninmatorDorado1Size1=ObjectAnimator.ofFloat(dorado1, "scaleX", 80f, 1f).setDuration(2000);
        Animator aninmatorDorado1Size2=ObjectAnimator.ofFloat(dorado1, "scaleY", 80f, 1f).setDuration(2000);
        Animator aninmatorDorado2Size1=ObjectAnimator.ofFloat(dorado2, "scaleX", 50f, 1f).setDuration(2000);
        Animator aninmatorDorado2Size2=ObjectAnimator.ofFloat(dorado2, "scaleY", 50f, 1f).setDuration(2000);
        Animator aninmatorAzul1Size1=ObjectAnimator.ofFloat(azul1, "scaleX", 50f, 1f).setDuration(2000);
        Animator aninmatorAzul1Size2=ObjectAnimator.ofFloat(azul1, "scaleY", 50f, 1f).setDuration(2000);
        Animator aninmatorRojo2Size1=ObjectAnimator.ofFloat(rojo2, "scaleX", 80f, 1f).setDuration(2000);
        Animator aninmatorRojo2Size2=ObjectAnimator.ofFloat(rojo2, "scaleY", 80f, 1f).setDuration(2000);
        Animator aninmatorRojo1Size1=ObjectAnimator.ofFloat(rojo1, "scaleX", 50f, 1f).setDuration(2000);
        Animator aninmatorRojo1Size2=ObjectAnimator.ofFloat(rojo1, "scaleY", 50f, 1f).setDuration(2000);
        Animator aninmatorAzul2Size1=ObjectAnimator.ofFloat(azul2, "scaleX", 50f, 1f).setDuration(2000);
        Animator aninmatorAzul2Size2=ObjectAnimator.ofFloat(azul2, "scaleY", 50f, 1f).setDuration(2000);

        Animator animatorDorado11 = ObjectAnimator.ofFloat(dorado1, "translationY", 0f, 710f).setDuration(500);
        Animator animatorDorado12 = ObjectAnimator.ofFloat(dorado1, "translationX", 0f, -185f).setDuration(500);

        Animator animatorAzul11 = ObjectAnimator.ofFloat(azul1, "translationX", 0f, -438f).setDuration(500);

        Animator animatorDorado21 = ObjectAnimator.ofFloat(dorado2, "translationY", 0f, 980f).setDuration(500);
        Animator animatorDorado22 = ObjectAnimator.ofFloat(dorado2, "translationX", 0f, -740f).setDuration(500);

        Animator animatorRojo1 = ObjectAnimator.ofFloat(rojo1, "translationX", 0f, 400f).setDuration(500);

        Animator animatorAzul21 = ObjectAnimator.ofFloat(azul2, "translationY", 0f, -975f).setDuration(500);
        Animator animatorAzul22 = ObjectAnimator.ofFloat(azul2, "translationX", 0f, 723f).setDuration(500);

        Animator animatorRojo21 = ObjectAnimator.ofFloat(rojo2, "translationY", 0f, -695f).setDuration(500);
        Animator animatorRojo22 = ObjectAnimator.ofFloat(rojo2, "translationX", 0f, 122f).setDuration(500);

        Animator animatorLightining1= ObjectAnimator.ofFloat(lightning, "alpha", 0f, 1f).setDuration(1);

        Animator animatorLight1= ObjectAnimator.ofFloat(lightningLight, "alpha", 0f, 1f).setDuration(1);
        Animator animatorLight2= ObjectAnimator.ofFloat(lightningLight, "alpha", 1f, 0f).setDuration(1000);

        Animator animatorAzul1Invisible=ObjectAnimator.ofFloat(azul1, "alpha", 1f, 0f).setDuration(1);
        Animator animatorAzul2Invisible=ObjectAnimator.ofFloat(azul2, "alpha", 1f, 0f).setDuration(1);
        Animator animatorDorado1Invisible= ObjectAnimator.ofFloat(dorado1, "alpha", 1f, 0f).setDuration(1);
        Animator animatorDorado2Invisible= ObjectAnimator.ofFloat(dorado2, "alpha", 1f, 0f).setDuration(1);
        Animator animatorRojo1Invisible=ObjectAnimator.ofFloat(rojo1, "alpha", 1f, 0f).setDuration(1);
        Animator animatorRojo2Invisible= ObjectAnimator.ofFloat(rojo2, "alpha", 1f, 0f).setDuration(1);

        Animator animatorJackGames= ObjectAnimator.ofFloat(jackGames, "alpha", 0f, 1f).setDuration(1000);

        set.play(aninmatorDorado1Size1).with(aninmatorDorado1Size2).with(aninmatorDorado2Size1).with(aninmatorDorado2Size2)
                .with(aninmatorAzul1Size1).with(aninmatorAzul1Size2).with(aninmatorRojo2Size1).with(aninmatorRojo2Size2)
                .with(aninmatorRojo1Size1).with(aninmatorRojo1Size2).with(aninmatorAzul2Size1).with(aninmatorAzul2Size2);

        set.play(animatorDorado11).with(animatorDorado12).with(animatorAzul11).with(animatorDorado21).with(animatorDorado22)
                .with(animatorRojo1).with(animatorAzul21).with(animatorAzul22).with(animatorRojo21).with(animatorRojo22)
                .after(aninmatorDorado1Size1);
        set.play(animatorLightining1).with(animatorLight1).after(animatorDorado11);
        set.play(animatorLight2).with(animatorAzul1Invisible).with(animatorDorado2Invisible).with(animatorAzul2Invisible)
                .with(animatorDorado1Invisible).with(animatorRojo1Invisible).with(animatorRojo2Invisible).after(animatorLight1);
        set.play(animatorJackGames).after(animatorLight2);

        Intent intent=new Intent(this, GamesListActivity.class);
        animatorJackGames.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startActivity(intent);
                finish();
            }
        });

        set.start();
    }
}