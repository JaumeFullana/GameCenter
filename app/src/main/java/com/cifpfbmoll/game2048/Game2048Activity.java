package com.cifpfbmoll.game2048;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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

import com.cifpfbmoll.Utils.DataBaseAssistant;
import com.cifpfbmoll.gamecenter.RecordsActivity;
import com.cifpfbmoll.gamecenter.Score;
import com.cifpfbmoll.gamecenter.UserSettingsActivity;
import com.cifpfbmoll.gamepegsolitaire.GamePegSolitaireActivity;
import com.cifpfbmoll.gamecenter.R;
import com.cifpfbmoll.Utils.Timer;
import com.cifpfbmoll.Utils.TimerInterface;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class Game2048Activity extends AppCompatActivity implements GestureDetector.OnGestureListener, TimerInterface {

    private Table2048 table;
    private GestureDetectorCompat detector;
    private Animator2048 animator;
    private GridLayout gridLayout;
    private int previousScore;
    private TextView score;
    private boolean started;
    private TextView timerView;
    private Timer timer;
    private String selectedMode;
    private int intSelectedMode;
    private Button backButton;
    private boolean playing;
    private MediaPlayer flingSound;


    /**
     * Overrided method, create all the necessary things to start the game.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedMode = this.getIntent().getStringExtra("MODE");

        if (selectedMode == null){
            selectedMode="4x4";
        }

        if (selectedMode.equals("4x4")) {
            setContentView(R.layout.activity_game_2048_4x4);
            table = new Table2048(this, new Cell2048[4][4], new Cell2048[4][4]);
            intSelectedMode=4;
        }
        else if (selectedMode.equals("5x5")) {
            setContentView(R.layout.activity_game_2048_5x5);
            table = new Table2048(this, new Cell2048[5][5], new Cell2048[5][5]);
            intSelectedMode=5;
        }
        else if (selectedMode.equals("3x3")) {
            setContentView(R.layout.activity_game_2048_3x3);
            table = new Table2048(this, new Cell2048[3][3], new Cell2048[3][3]);
            intSelectedMode=3;
        }

        this.flingSound=MediaPlayer.create(this, R.raw.fling);
        this.playing=true;
        this.detector = new GestureDetectorCompat(this, this);
        this.backButton = ((Button)findViewById(R.id.backMovementButton2048));
        this.gridLayout = (GridLayout)findViewById(R.id.gridLayout2048);
        this.animator = new Animator2048(this, intSelectedMode);
        this.timerView = (TextView)findViewById(R.id.timer2048);
        this.started=false;
        this.timer=new Timer(this, true);
        this.assignNumber();
        this.table.fillDisponibleCells();
        this.assignNumber();
        this.score =this.findViewById(R.id.puntuacion);
        this.previousScore =0;
    }


    /**
     * Overrided method.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_bar_options_2048, menu);
        return true;
    }


    /**
     * Overrided method, call diferents methods depending on the item selected.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean result=true;
        switch(item.getItemId()){
            case R.id.peg_solitaire_menu_bar:
                openPegSolitaire();
                finish();
                break;
            case R.id.settings_menu_bar:
                openSettings();
                break;
            case R.id.records:
                openRecords();
                break;
            default:
                result=super.onOptionsItemSelected(item);
        }
        return result;
    }

    /**
     * Overrided method.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * Overrided method. Restarts the game if the game mode was changed in the settings screen.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String mode = data.getStringExtra("MODE");
                if (mode != null && !mode.equals(this.selectedMode)) {
                    selectedMode = mode;
                    this.restartGame(null);
                }
            }
        }
    }

    /**
     * if there is available cells this method generates a new number in a random cell.
     */
    public void assignNumber(){
        if(this.table.getAvailableCells().size()!=0){
            String cell=this.table.getRandomEmptyCell();
            int number=this.table.generateNumber();
            int [] pos=this.table.fillCell(cell, number);
            animatedPaintNumber(pos);
        }
    }

    /**
     * Animates the movements of the cells that are being moved in this fling.
     * @param anims ArrayList of the animators that has to been started
     */
    public void animatedMovement(ArrayList<Animator> anims) {
        for (int i=0; i<anims.size();i++){
            anims.get(i).start();
        }
    }

    /**
     * Call the method that starts the animation that is used when a new number appears in the grid.
     * The call is done in a new thread and is delayed 150 miliseconds.
     * @param pos position where the new number appears
     */
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


    /**
     * Overrided method. Method called when a fling is done in the phone screen. Checks if the match
     * is still playable, if the movement is correct and if some cells are moving. Then, if everything
     * is ok the cells are moved.
     */
    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        if (this.playing) {
            this.animator.setGridHeight(gridLayout.getHeight());
            this.animator.setGridWidth(gridLayout.getWidth());
            double xDifference = Math.abs(event1.getX() - event2.getX());
            double yDifference = Math.abs(event1.getY() - event2.getY());
            if (xDifference / 2 > yDifference || yDifference / 2 > xDifference) {
                boolean moved = this.table.moveNumbers(event1, event2, yDifference, xDifference);
                if (moved) {
                    this.flingSound.start();
                    if (!this.started) {
                        timer.start();
                        this.started = true;
                    }
                    this.table.fillDisponibleCells();
                    ArrayList<Animator> anims = animator.animateMovememnts(this.table.getCells());
                    anims.get(anims.size() - 1).addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            paintNumbers();
                            assignNumber();
                            checkIfEnded();
                        }
                    });
                    this.animatedMovement(anims);
                }
            }
        }
        return this.playing;
    }

    /**
     * Check if the game is ended. If is ended stops all the things that should be stoped.
     */
    private void checkIfEnded() {
        int state = table.checkState();
        if (state != 0) {
            timer.pause();
            backButton.setEnabled(false);
            announceResult(state);
            saveRecord();
            playing=false;
        }
    }

    /**
     * Announce the result of the game in a toast.
     * @param state int tells if the game is lost or won
     */
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

    /**
     * Saves the result of the game in the data base.
     */
    private void saveRecord() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = preferences.getString("userName", "Wrong.User.Name");
        int puntuation = Integer.parseInt(score.getText().toString());
        String time = timerView.getText().toString();
        DataBaseAssistant db = new DataBaseAssistant(this);
        db.addScore(new Score(puntuation, time, "2048", selectedMode, userName));
    }

    /**
     * repaints the grid to show the result of the cells movements done in the fling.
     */
    public void paintNumbers(){
        for (int i=0; i<table.getCells().length; i++){
            for (int j=0; j<table.getCells()[i].length; j++){
                paintNumber(i, j);
            }
        }
    }

    /**
     * Paint a number in a specific position of the grid.
     * @param i row where the number is gonna be painted
     * @param j column where the number is gonna be painted
     * @return the textView where the number is painted
     */
    private TextView paintNumber(int i, int j) {
        int number=this.table.getCells()[i][j].getValue();
        int id=this.getResources().getIdentifier("p"+ i +""+ j,"id",this.getPackageName());
        TextView square=(TextView)findViewById(id);
        if (number!=0){
            square.setText(String.valueOf(number));
            int colorId=this.getResources().getIdentifier("color"+number,"color",this.getPackageName());
            square.setBackgroundColor(ContextCompat.getColor(this, colorId));
            if (this.selectedMode.equals("4x4")){
                if (number >= 1024) {
                    square.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                }
                else if (number >= 128) {
                    square.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                }
                else {
                    square.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
                }
            } else if (this.selectedMode.equals("5x5")){
                if (number >= 1024) {
                    square.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                }
                else if (number >= 128) {
                    square.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                }
                else {
                    square.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                }
            } else{
                if (number >= 1024) {
                    square.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                }
                else if (number >= 128) {
                    square.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                }
                else {
                    square.setTextSize(TypedValue.COMPLEX_UNIT_SP, 42);
                }
            }
        } else {
            square.setText("");
            square.setBackgroundColor(ContextCompat.getColor(this, R.color.gridBackground));
        }
        return square;
    }

    /**
     * Method that creates and starts the animation used when a new number is generated in the grid.
     * @param view textView to be animated
     */
    private void runCreateAnimation(TextView view) {
        Animator a = ObjectAnimator.ofFloat(view,"alpha", 0.33f, 1f).setDuration(400);
        a.start();
    }

    /**
     * Sums a new number to the actual puntuation.
     * @param numero number to be summed
     */
    public void setScore(int numero){
        score =this.findViewById(R.id.puntuacion);
        this.previousScore = Integer.parseInt(score.getText().toString());
        int puntuacionActual=this.previousScore + numero;
        score.setText(Integer.toString(puntuacionActual));
    }

    /**
     * Starts the RecordActivity.
     */
    public void openRecords(){
        Intent intent=new Intent(this, RecordsActivity.class);
        startActivity(intent);
    }

    /**
     * Starts the GamePegSolitaireActivity
     */
    public void openPegSolitaire(){
        Intent intent=new Intent(this, GamePegSolitaireActivity.class);
        startActivity(intent);
    }

    /**
     * Starts the UserSettingsActivity.
     */
    private void openSettings() {
        Intent intent=new Intent(this, UserSettingsActivity.class);
        intent.putExtra("game","2048");
        intent.putExtra("mode", selectedMode);
        startActivityForResult(intent, 1);
    }

    /**
     * Method that returns the grid to the state it was before the last movement
     * @param view view that should be passed to allow to call this method from a button in a hardcoded way
     */
    public void backMovement(View view){
        if (this.table.getCellsCopy()!=this.table.getCells()  &&
            this.table.getCellsCopy()[0][0]!=null) {
            this.table.copyCells(this.table.getCellsCopy(), this.table.getCells());
            this.paintNumbers();
            this.backButton.setEnabled(false);
            score.setText(Integer.toString(this.previousScore));
        }
    }

    /**
     * Method that restarts the game.
     * @param view view that should be passed to allow to call this method from a button in a hardcoded way
     */
    public void restartGame(View view){

        if (selectedMode == null){
            selectedMode="4x4";
        }

        if (selectedMode.equals("4x4")) {
            setContentView(R.layout.activity_game_2048_4x4);
            table = new Table2048(this, new Cell2048[4][4], new Cell2048[4][4]);
            intSelectedMode=4;
        }
        else if (selectedMode.equals("5x5")) {
            setContentView(R.layout.activity_game_2048_5x5);
            table = new Table2048(this, new Cell2048[5][5], new Cell2048[5][5]);
            intSelectedMode=5;
        }
        else if (selectedMode.equals("3x3")) {
            setContentView(R.layout.activity_game_2048_3x3);
            table = new Table2048(this, new Cell2048[3][3], new Cell2048[3][3]);
            intSelectedMode=3;
        }

        this.table.initCells(this.table.getCells());
        this.playing=true;
        this.timerView=(TextView)findViewById(R.id.timer2048);
        this.started=false;
        this.timer.stop();
        this.timer=new Timer(this, true);
        this.previousScore = 0;
        this.score.setText("0");
        this.table.fillDisponibleCells();
        this.assignNumber();
        this.table.fillDisponibleCells();
        this.assignNumber();
        this.paintNumbers();
        this.backButton.setEnabled(true);
        this.animator = new Animator2048(this, intSelectedMode);
    }

    /**
     * Overried method. Method that is called by the Timer class everytime the timer is updated.
     * @param time new time
     */
    @Override
    public void onTimeUpdated(long time) {
        this.updateTimer(time);
    }

    /**
     * Change the text of the timerView.
     * @param currentTime value to be setted as the text of the view, after being formatted.
     */
    public void updateTimer(Long currentTime){
        SimpleDateFormat sdf=new SimpleDateFormat("mm:ss:SSS");
        runOnUiThread(new Runnable() {
            public void run() {
                timerView.setText(sdf.format(currentTime));
            }
        });
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