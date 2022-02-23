package com.cifpfbmoll.game2048;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;

import com.cifpfbmoll.gamepegsolitaire.GamePegSolitaireActivity;
import com.cifpfbmoll.gamecenter.R;
import com.cifpfbmoll.Utils.Timer;
import com.cifpfbmoll.Utils.TimerInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

//Hacer clase cell para almacenar el numero, color de celda y posicion. Ver que metodos se tiene que pasar
// a este. Pasar la matriz i lista de int a matriz i lista de cell
public class Game2048Activity extends AppCompatActivity implements GestureDetector.OnGestureListener,
        TimerInterface {

    private Table2048 table;
    private GestureDetectorCompat detector;
    private Animator2048 animator;
    private GridLayout gridLayout;
    private int previousScore;
    private TextView score;
    private boolean started;
    private TextView timerView;
    private Timer timer;
    private int selectedMode;
    private Button backButton;

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

        selectedMode = 3;

        if (selectedMode == 4) {
            setContentView(R.layout.activity_game_2048_4x4);
            table = new Table2048(this, new Cell2048[4][4], new Cell2048[4][4]);
        }
        else if (selectedMode == 5) {
            setContentView(R.layout.activity_game_2048_5x5);
            table = new Table2048(this, new Cell2048[5][5], new Cell2048[5][5]);
        }
        else if (selectedMode == 3) {
            setContentView(R.layout.activity_game_2048_3x3);
            table = new Table2048(this, new Cell2048[3][3], new Cell2048[3][3]);
        }

        this.detector = new GestureDetectorCompat(this, this);
        this.backButton = ((Button)findViewById(R.id.backMovementButton2048));
        this.gridLayout = (GridLayout)findViewById(R.id.gridLayout2048);
        this.animator = new Animator2048(this, selectedMode);
        this.timerView = (TextView)findViewById(R.id.timer2048);
        this.started=false;
        this.timer=new Timer(this, true);
        this.assignNumber();
        this.table.fillDisponibleCells();
        this.assignNumber();
        this.score =this.findViewById(R.id.puntuacion);
        this.previousScore =0;
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
                finish();
                break;
            case R.id.settings_menu_bar:

                break;
            case R.id.help_menu_bar:

                break;
            case R.id.records:
                openRecords();
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
        if(this.getTable().getAvailableCells().size()!=0){
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
        this.animator.setGridHeight(gridLayout.getHeight());
        this.animator.setGridWidth(gridLayout.getWidth());
        double xDifference=Math.abs(event1.getX()-event2.getX());
        double yDifference=Math.abs(event1.getY()-event2.getY());
        if(xDifference/2>yDifference || yDifference/2>xDifference){
            boolean moved=this.getTable().moveNumbers(event1, event2, yDifference, xDifference);
            if(moved) {
                if (!this.started){
                    timer.start();
                    this.started=true;
                }
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
                            timer.pause();
                            backButton.setEnabled(false);
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
        SimpleDateFormat sdf=new SimpleDateFormat("mm:ss:SSS");
        Toast toast=Toast.makeText(this,resultado+" tiempo: "+sdf.format(this.timer.getTime()),Toast.LENGTH_LONG);
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
        for (int i=0; i<table.getCells().length; i++){
            for (int j=0; j<table.getCells()[i].length; j++){
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
            if (number>=1024){
                square.setTextSize(TypedValue.COMPLEX_UNIT_SP,28);
            }else{
                square.setTextSize(TypedValue.COMPLEX_UNIT_SP,40);
            }

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

    public void setScore(int numero){
        score =this.findViewById(R.id.puntuacion);
        this.previousScore =Integer.parseInt(score.getText().toString());
        int puntuacionActual=this.previousScore +numero;
        score.setText(Integer.toString(puntuacionActual));
    }

    public void openRecords(){
        Intent intent=new Intent(this, Records2048Activity.class);
        startActivity(intent);
    }

    public void openPegSolitaire(){
        Intent intent=new Intent(this, GamePegSolitaireActivity.class);
        startActivity(intent);
    }

    public void backMovement(View view){
        if (this.getTable().getCellsCopy()!=this.getTable().getCells()  &&
            this.getTable().getCellsCopy()[0][0]!=null) {
            this.getTable().copyCells(this.getTable().getCellsCopy(), this.getTable().getCells());
            this.paintNumbers();
            this.backButton.setEnabled(false);
            score.setText(Integer.toString(this.previousScore));
        }
    }

    public void restartGame(View view){

        this.getTable().initCells(this.getTable().getCells());
        if (selectedMode == 4) {
            this.getTable().setCellsCopy(new Cell2048[4][4]);
        } else if (selectedMode == 5){
            this.getTable().setCellsCopy(new Cell2048[5][5]);
        } else if (selectedMode == 3){
            this.getTable().setCellsCopy(new Cell2048[3][3]);
        }
        this.timerView=(TextView)findViewById(R.id.timer2048);
        this.started=false;
        this.timer.stop();
        this.timer=new Timer(this, true);
        this.previousScore = 0;
        this.score.setText("0");
        this.getTable().fillDisponibleCells();
        this.assignNumber();
        this.table.fillDisponibleCells();
        this.assignNumber();
        this.paintNumbers();
        this.backButton.setEnabled(true);

        /*
        Intent intent = new Intent(this, Game2048Activity.class);
        this.startActivity(intent);
        this.finish();
        */

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

    @Override
    public void onTimeUpdated(long time) {
        this.updateTimer(time);
    }

    public void updateTimer(Long currentTime){
        SimpleDateFormat sdf=new SimpleDateFormat("mm:ss:SSS");
        runOnUiThread(new Runnable() {
            public void run() {
                timerView.setText(sdf.format(currentTime));
            }
        });
    }
}