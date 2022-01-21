package com.cifpfbmoll.gamecenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;

import java.util.ArrayList;

//Hacer clase cell para almacenar el numero, color de celda y posicion. Ver que metodos se tiene que pasar
// a este. Pasar la matriz i lista de int a matriz i lista de cell
public class Game2048Activity extends AppCompatActivity implements GestureDetector.OnGestureListener{

    private Table2048 table;
    private GestureDetectorCompat detector;
    private Animator2048 animator;

    public Table2048 getTable() {
        return table;
    }

    public void setTable(Table2048 table) {
        this.table = table;
    }

    public GestureDetectorCompat getDetector() {
        return detector;
    }

    public Animator2048 getAnimator() {
        return animator;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_2048);
        detector=new GestureDetectorCompat(this, this);
        table=new Table2048(this, new Cell2048[4][4]);
        animator=new Animator2048(this);
        this.assignNumber();
        this.table.fillDisponibleCells();
        this.assignNumber();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_bar_options_2048, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean result=true;
        switch(item.getItemId()){
            case R.id.peg_solitaire_menu_bar:
                openPegSolitaire();
                break;
            case R.id.settings_menu_bar:

                break;
            case R.id.help_menu_bar:

                break;
            default:
                result=super.onOptionsItemSelected(item);
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.getDetector().onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public void assignNumber(){
        if(this.getTable().getDisponibleCells().size()!=0){
            String cell=this.getTable().getRandomEmptyCell();
            int number=this.getTable().generateNumber();
            int [] pos=this.getTable().fillCell(cell, number);
            animatedPaintNumber(pos);
        }
    }


    public void animatedMovement(ArrayList<Animator> anims) {
        for (int i=0; i<anims.size();i++){
            anims.get(i).start();
        }
    }

    public void animatedPaintNumber(int[] pos) {
        Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                TextView square=paintNumber(pos[0], pos[1]);
                runCreateAnimation(square);
            }
        };
        handler.postDelayed(runnable,150);
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        double xDifference=Math.abs(event1.getX()-event2.getX());
        double yDifference=Math.abs(event1.getY()-event2.getY());
        if(xDifference/2>yDifference || yDifference/2>xDifference){
            boolean moved=this.getTable().moveNumbers(event1, event2, yDifference, xDifference);
            if(moved) {
                this.getTable().fillDisponibleCells();
                ArrayList<Animator> anims=animator.animateMovememnts(this.table.getCells());
                anims.get(anims.size()-1).addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        paintNumbers();
                        assignNumber();
                        int state = getTable().checkState();
                        if (state != 0) {
                            announceResult(state);
                            saveRecord();
                        }
                    }
                });
                this.animatedMovement(anims);

            }
        }
        return true;
    }

    //no comment because we arent gonna show the result in a toast, we are gonna change this method logic
    public void announceResult(int state){
        String resultado="";
        if (state==1){
            resultado="Has ganado!";
        } else{
            resultado="Has perdido!";
        }
        Toast toast=Toast.makeText(this,resultado,Toast.LENGTH_LONG);
        toast.show();
    }

    private void saveRecord() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("PegSolitaire"+this.getPackageName(),Context.MODE_PRIVATE);
        int puntuacion=Integer.parseInt(((TextView)findViewById(R.id.puntuacion)).getText().toString());
        int i=1;
        boolean posicionado=false;
        int recordAntiguo=0;
        while (i<6) {
            int record=sharedPreferences.getInt("record"+i,00000);
            if (posicionado){
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putInt("record"+i,recordAntiguo);
                editor.commit();
                recordAntiguo=record;
            }
            if (!posicionado && puntuacion>record){
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putInt("record"+i,puntuacion);
                posicionado=true;
                editor.commit();
                recordAntiguo=record;
            }
            i++;
        }
    }

    public void paintNumbers(){
        for (int i=0; i<4; i++){
            for (int j=0; j<4; j++){
                paintNumber(i, j);
            }
        }
    }

    private TextView paintNumber(int i, int j) {
        int number=this.table.getCells()[i][j].getValue();
        int id=this.getResources().getIdentifier("p"+ i +""+ j,"id",this.getPackageName());
        TextView square=(TextView)findViewById(id);
        if (number!=0){
            square.setText(String.valueOf(number));
            int colorId=this.getResources().getIdentifier("color"+number,"color",this.getPackageName());
            square.setBackgroundColor(ContextCompat.getColor(this, colorId));

        } else {
            square.setText("");
            square.setBackgroundColor(ContextCompat.getColor(this, R.color.gridBackground));
        }
        return square;
    }

    private void runCreateAnimation(TextView view) {
        Animator a = ObjectAnimator.ofFloat(view,"alpha", 0.33f, 1f).setDuration(400);
        a.start();
    }

    public void setPuntuacion(int numero){
        TextView puntuacion=this.findViewById(R.id.puntuacion);
        int puntuacionActual=Integer.parseInt(puntuacion.getText().toString())+numero;
        puntuacion.setText(Integer.toString(puntuacionActual));
    }

    public void openRecords(View view){
        Intent intent=new Intent(this, Records2048Activity.class);
        startActivity(intent);
    }

    public void openPegSolitaire(){
        Intent intent=new Intent(this, GamePegSolitaireActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }
}