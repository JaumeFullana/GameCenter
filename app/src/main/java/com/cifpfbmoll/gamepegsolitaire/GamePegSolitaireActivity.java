package com.cifpfbmoll.gamepegsolitaire;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cifpfbmoll.Utils.DataBaseAssistant;
import com.cifpfbmoll.game2048.Game2048Activity;
import com.cifpfbmoll.gamecenter.R;
import com.cifpfbmoll.Utils.Timer;
import com.cifpfbmoll.Utils.TimerInterface;
import com.cifpfbmoll.gamecenter.RecordsListActivity;
import com.cifpfbmoll.gamecenter.Score;
import com.cifpfbmoll.gamecenter.UserSettingsActivity;

import java.text.SimpleDateFormat;

public class GamePegSolitaireActivity extends AppCompatActivity implements TimerInterface {

    private TablePegSolitaire table;
    private Button ballView;
    private Button backButton;
    private TextView ballCounter;
    private TextView timerView;
    private boolean started;
    private Timer timer;
    private String selectedMode;
    private MediaPlayer dragDropSound;

    public TablePegSolitaire getTable() {
        return table;
    }

    /**
     * Overrided method, create all the necessary things to start the game.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedMode = this.getIntent().getStringExtra("MODE");
        if (selectedMode == null){
            selectedMode = "English";
        }

        if (selectedMode.equals("English")) {
            setContentView(R.layout.activity_game_peg_solitaire_english);
            this.table = new TablePegSolitaire();
            table.initEnglishBoard();
        }
        else if (selectedMode.equals("German")) {
            setContentView(R.layout.activity_game_peg_solitaire_german);
            this.table = new TablePegSolitaire();
            table.initGermanBoard();
        }
        else if (selectedMode.equals("European")) {
            setContentView(R.layout.activity_game_peg_solitaire_european);
            this.table = new TablePegSolitaire();
            table.initEuropeanBoard();
        }

        this.dragDropSound = MediaPlayer.create(this,R.raw.drag_drop);
        this.timerView = findViewById(R.id.timerPeg);
        this.backButton = findViewById(R.id.backMovementButtonPegSolitaire);
        table.countBalls();
        this.started = false;
        this.timer = new Timer(this, true);
        ballCounter = findViewById(R.id.balls);
        ballCounter.setText(Integer.toString(this.table.getBalls()));
        this.addDragListeners(this.getTable());
        this.addTouchListeners();
    }

    /**
     * Overrided method.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_bar_options_peg_solitaire, menu);

        return true;
    }

    /**
     * Overrided method, call diferents methods depending on the item selected.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean result=true;
        switch(item.getItemId()){
            case R.id.menu_bar_2048:
                open2048();
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
     * Method that set the OnDragListener on each playable frameLayout. The method onDrag returns a
     * boolean, if it's false the ball returns to its position with a movement with physics.
     * @param table table where the game is played
     */
    public void addDragListeners(TablePegSolitaire table){
        for (int i=0;i<table.getCells().length;i++) {
            for (int j=0;j<table.getCells()[i].length;j++) {
                if(table.getCells()[i][j]!=-1) {
                    int id = this.getResources().getIdentifier("f" +i+""+j,"id",this.getPackageName());
                    FrameLayout fl = findViewById(id);
                    fl.setOnDragListener(new View.OnDragListener() {
                        @Override
                        public boolean onDrag(View view, DragEvent dragEvent) {
                            if (!started){
                                started=true;
                                timer.start();
                            }
                            boolean correct=true;
                            View v = (View) dragEvent.getLocalState();
                            if (dragEvent.getAction() == DragEvent.ACTION_DROP) {
                                correct=dropBall(v, fl);
                            } else if(dragEvent.getAction()==DragEvent.ACTION_DRAG_ENDED){
                                v.setVisibility(View.VISIBLE);
                                dragDropSound.start();
                            }
                            return correct;
                        }
                    });
                }
            }
        }
    }

    /**
     * Method that set the onTouchListener on every ball (button) of the layout.
     */
    public void addTouchListeners(){
        for (int i=1;i<this.table.getHolesNumber();i++){
            int id = this.getResources().getIdentifier("b" +i,"id",this.getPackageName());
            Button b32=findViewById(id);
            b32.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    dragDropSound.start();
                    dragBall(view);
                    return true;
                }
            });
        }
    }

    /**
     * Method that drop the ball in the new position if is posible. Method that check if the ball can
     * be moved from the old position to the new position(calling the checkMovement Method), then, if
     * the result of the checkMovement is a valid position the method change the positions and delete
     * the ball in the middle of the two and it check if the player can still make movements. If the player
     * can't do any movement (the game is over) the method calls the announceResult method.
     * @param v view that is the ball (button) that is been moved
     * @param fl frameLayout where the ball is gonna be placed (if is posible)
     * @return correct, a boolean, if the movement is done returns true, if not returns false
     */
    public boolean dropBall(View v, FrameLayout fl) {
        boolean correct=false;
        int [] newPosition=this.getFrameLayoutPosition(fl);
        FrameLayout fParent = (FrameLayout) v.getParent();
        int[] oldPosition = this.getFrameLayoutPosition(fParent);
        int[] ballPositionToDelete=this.getTable().checkMovement(oldPosition,newPosition);
        if(ballPositionToDelete[0]!=-1) {
            correct=true;
            this.getTable().moveBall(oldPosition, newPosition, ballPositionToDelete);
            ballCounter.setText(Integer.toString(this.table.getBalls()));
            fParent.removeView(v);
            fl.addView(v);
            FrameLayout intermediateFrame=this.getFrameLayout(ballPositionToDelete[0],ballPositionToDelete[1]);
            this.ballView=(Button)intermediateFrame.getChildAt(0);
            intermediateFrame.removeView(intermediateFrame.getChildAt(0));
            int state=this.getTable().isFinished();
            if (state!=0){
                this.timer.pause();
                this.backButton.setEnabled(false);
                v.setVisibility(View.VISIBLE);
                this.saveRecord();
                this.announceResult(state);
            }
        }

        return correct;
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
     * Method that starts the drag action. Create a DragShadow that is copy of the button that the method
     * is dragging. Then start the drag and set the button that is moving to inivisible.
     * @param view the ball view, to create the shadow of the drag from it and to send to the new position.
     */
    public void dragBall(View view){
        View.DragShadowBuilder shadow = new View.DragShadowBuilder(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.startDragAndDrop(null,shadow,view,View.DRAG_FLAG_OPAQUE);
        } else{
            view.startDrag(null,shadow,view,0);
        }
        view.setVisibility(View.INVISIBLE);
    }

    /**
     * Method that recives a frame layout from the gridLayout that represents a cell of the board of the peg
     * solitaire and returns its position.
     * @param fl FrameLayout cell of the board
     * @return the position of the FrameLayout
     */
    public int[] getFrameLayoutPosition(FrameLayout fl){
        String name=fl.getResources().getResourceName(fl.getId());
        int y=Character.getNumericValue(name.charAt(name.length()-2));
        int x=Character.getNumericValue(name.charAt(name.length()-1));
        int [] position=new int [2];
        position[0]=y;
        position[1]=x;
        return position;
    }

    /**
     * Method that return the frameLayout placed in the y,x position of the gridLayout.
     * @param y int y position of the frameLayout
     * @param x int x position of the frameLayout
     * @return the FrameLayout in position y,x
     */
    public FrameLayout getFrameLayout(int y, int x){
        int id=this.getResources().getIdentifier("f"+y+""+x,"id",this.getPackageName());
        return findViewById(id);
    }

    /**
     * takes an screenshot of a view and saves it in a bitmap.
     * @param view to take the photo
     * @return bitmap the screenshot
     */
    private Bitmap screenShot(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
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

    /**
     * Starts the RecordsActivity.
     */
    public void openRecords(){
        Intent intent=new Intent(this, RecordsListActivity.class);
        startActivity(intent);
    }

    /**
     * Starts the Game2048Activity.
     */
    public void open2048(){
        Intent intent=new Intent(this, Game2048Activity.class);
        startActivity(intent);
    }

    /**
     * Starts the UserSettingsActivity.
     */
    private void openSettings() {
        Intent intent=new Intent(this, UserSettingsActivity.class);
        intent.putExtra("game","Peg Solitaire");
        intent.putExtra("mode", selectedMode);
        startActivityForResult(intent, 1);
    }

    /**
     * Saves the result of the game in the data base.
     */
    private void saveRecord() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = preferences.getString("userName", "Wrong.User.Name");
        int puntuacion=Integer.parseInt(ballCounter.getText().toString());
        String time = timerView.getText().toString();
        Bitmap screenShot = this.screenShot(findViewById(R.id.gridLayoutPegSolitaire));
        DataBaseAssistant db=new DataBaseAssistant(this);
        db.addScore(new Score(puntuacion, time, "Peg Solitaire", selectedMode, userName, screenShot));
    }

    /**
     * Method that returns the board to the state it was before the last movement
     * @param view view that should be passed to allow to call this method from a button in a hardcoded way
     */
    public void backMovement(View view){
        if(this.getTable().getCellDeleted()!=null) {
            this.getTable().redoMovement();
            FrameLayout frameMovingBall = this.getFrameLayout(this.getTable().getCellAfterMovement()[0], this.getTable().getCellAfterMovement()[1]);
            FrameLayout frameRecivingBall = this.getFrameLayout(this.getTable().getCellBeforeMovement()[0], this.getTable().getCellBeforeMovement()[1]);
            FrameLayout frameRecreatingBall = this.getFrameLayout(this.getTable().getCellDeleted()[0], this.getTable().getCellDeleted()[1]);
            Button button = (Button) frameMovingBall.getChildAt(0);
            frameMovingBall.removeView(button);
            frameRecivingBall.addView(button);
            frameRecreatingBall.addView(this.ballView);
            ballCounter.setText(Integer.toString(this.table.getBalls()));
            this.backButton.setEnabled(false);
        }
    }

    /**
     * Method that restarts the game.
     * @param view view that should be passed to allow to call this method from a button in a hardcoded way
     */
    public void restartGame(View view){
        if (selectedMode == null || selectedMode.equals("English")) {
            setContentView(R.layout.activity_game_peg_solitaire_english);
            this.table = new TablePegSolitaire();
            table.initEnglishBoard();
        }
        else if (selectedMode.equals("German")) {
            setContentView(R.layout.activity_game_peg_solitaire_german);
            this.table = new TablePegSolitaire();
            table.initGermanBoard();
        }
        else if (selectedMode.equals("European")) {
            setContentView(R.layout.activity_game_peg_solitaire_european);
            this.table = new TablePegSolitaire();
            table.initEuropeanBoard();
        }
        this.backButton = findViewById(R.id.backMovementButtonPegSolitaire);
        table.countBalls();
        this.timerView = findViewById(R.id.timerPeg);
        this.started=false;
        this.timer.stop();
        this.timer = new Timer(this,true);
        ballCounter = findViewById(R.id.balls);
        ballCounter.setText(Integer.toString(this.table.getBalls()));
        this.addDragListeners(this.getTable());
        this.addTouchListeners();
    }

    /**
     * Overried method. Method that is called by the Timer class everytime the timer is updated.
     * @param time new time
     */
    @Override
    public void onTimeUpdated(long time) {
        this.updateTimer(time);
    }

}