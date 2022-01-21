package com.cifpfbmoll.gamecenter;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;

public class GamePegSolitaireActivity extends AppCompatActivity {

    private TablePegSolitaire table;
    private boolean timerRunning;
    private boolean timerStarted;
    private long time;

    public TablePegSolitaire getTable() {
        return table;
    }

    public void setTable(TablePegSolitaire table) {
        this.table = table;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_peg_solitaire);
        TablePegSolitaire table=new TablePegSolitaire();
        table.initEnglishBoard();
        table.countBalls();
        this.table=table;
        this.timerRunning=true;
        this.timerStarted=false;
        TextView ballCounter=(TextView)findViewById(R.id.balls);
        ballCounter.setText(Integer.toString(this.table.getBalls()));
        this.addDragListeners(this.getTable());
        this.addTouchListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_bar_options_peg_solitaire, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean result=true;
        switch(item.getItemId()){
            case R.id.menu_bar_2048:
                open2048();
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

    /**
     * Method that set the OnDragListener on each playable frameLayout. The method onDrag returns a
     * boolean, if it's false the ball returns to its position with a movement with physics.
     * @param table table where the game is played
     */
    public void addDragListeners(TablePegSolitaire table){
        for (int i=0;i<table.getCells().length;i++) {
            for (int j=0;j<table.getCells()[i].length;j++) {
                int id = this.getResources().getIdentifier("f" +i+""+j,"id",this.getPackageName());
                if(table.getCells()[i][j]!=-1) {
                    FrameLayout fl = findViewById(id);
                    fl.setOnDragListener(new View.OnDragListener() {
                        @Override
                        public boolean onDrag(View view, DragEvent dragEvent) {
                            boolean correct=true;
                            if (!timerStarted){
                                long startTime=System.currentTimeMillis();
                                TextView timer=(TextView)findViewById(R.id.timer);
                                timerStarted=true;
                                Thread thread=new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        time=0;
                                        while (timerRunning){
                                            time=updateTimer(startTime, timer);
                                            try {
                                                Thread.sleep(31);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                                thread.start();
                            }
                            //get the view I saved on the local state when drag started(is a button)
                            View v = (View) dragEvent.getLocalState();
                            if (dragEvent.getAction() == DragEvent.ACTION_DROP) {
                                //when the ball is droped correctly(a listener catch the event))
                                correct=dropBall(v, fl);
                            } else if(dragEvent.getAction()==DragEvent.ACTION_DRAG_ENDED){
                                //when the drag ends, this always happens.
                                v.setVisibility(View.VISIBLE);
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
        for (int i=1;i<33;i++){
            int id = this.getResources().getIdentifier("b" +i,"id",this.getPackageName());
            Button b32=findViewById(id);
            b32.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
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
            TextView ballCounter=(TextView)findViewById(R.id.balls);
            ballCounter.setText(Integer.toString(this.table.getBalls()));
            fParent.removeView(v);
            fl.addView(v);
            FrameLayout intermediateFrame=this.getFrameLayout(ballPositionToDelete[0],ballPositionToDelete[1]);
            intermediateFrame.removeView(intermediateFrame.getChildAt(0));
            int state=this.getTable().isFinished();
            if (state!=0){
                this.timerRunning=false;
                this.announceResult(state);
            }
        }
        return correct;
    }

    //no comment because we arent gona show the result in a toast, we are gonna change this method logic
    public void announceResult(int state){
        String resultado="";
        if (state==1){
            resultado="Has ganado!";
        } else{
            resultado="Has perdido!";
        }
        SimpleDateFormat sdf=new SimpleDateFormat("mm:ss:SSS");
        Toast toast=Toast.makeText(this,resultado+" tiempo: "+sdf.format(time),Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Method that strat the drag action. Create a DragShadow that is copy of the button that the method
     * is dragging. Then start the drag and set the button that is moving to inivisible.
     * @param view
     */
    public void dragBall(View view){
        //ToneGenerator tg=new ToneGenerator(AudioManager.STREAM_MUSIC,100);
        //tg.startTone(ToneGenerator.TONE_SUP_ERROR,200);

        View.DragShadowBuilder shadow= new View.DragShadowBuilder(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.startDragAndDrop(null,shadow,view,View.DRAG_FLAG_OPAQUE);
        } else{
            view.startDrag(null,shadow,view,0);
        }
        view.setVisibility(View.INVISIBLE);
    }

    /**
     * Method that recives a frame layout from the gridLayout that represents the board of the peg
     * solitaire and returns its position.
     * @param fl FrameLayout
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

    public long updateTimer(long starTime, TextView timer){
        long currentTime=System.currentTimeMillis()-starTime;
        SimpleDateFormat sdf=new SimpleDateFormat("mm:ss:SSS");
        runOnUiThread(new Runnable() {
            public void run() {
                timer.setText(sdf.format(currentTime));
            }
        });
        return currentTime;
    }

    public void openRecords(View view){
        Intent intent=new Intent(this, RecordsPegSolitaireActivity.class);
        startActivity(intent);
    }

    public void open2048(){
        Intent intent=new Intent(this, Game2048Activity.class);
        startActivity(intent);
    }
}