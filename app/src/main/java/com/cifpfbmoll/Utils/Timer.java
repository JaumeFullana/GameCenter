package com.cifpfbmoll.Utils;

public class Timer implements Runnable{

    private boolean timerStarted;
    private boolean timerRunning;
    private boolean timerPaused;
    private long startTime;
    private long time;
    private long timeSaved;
    private TimerInterface main;
    private boolean restartingAtEnd;

    public Timer(TimerInterface main, boolean restartingAtEnd) {
        this.main=main;
        this.timerStarted=false;
        this.restartingAtEnd=restartingAtEnd;
        timeSaved=0;
    }

    public long getTime() {
        return time;
    }

    /**
     * Starts the timer. If this timer wasn't used before, starts the thread where the timer runs.
     */
    public void start(){
        this.timerPaused=false;
        this.timerRunning=true;
        if (!timerStarted) {
            new Thread(this).start();
            this.timerStarted=true;
        }
    }

    /**
     * Pause the timer.
     */
    public void pause(){
        timerPaused=true;
        timeSaved=time;
    }

    /**
     * Resume the timer, used after a pause.
     */
    public void resume(){
        timerPaused=false;
        startTime=System.currentTimeMillis();
    }

    /**
     * Stops the timer, not allowing to resume. You have to call start again to run this timer agin.
     */
    public void stop(){
        timerPaused=true;
        timerRunning=false;
    }

    /**
     * Overrided method. Method were the main logic of the timer happens.
     */
    @Override
    public void run() {
        time=0;
        startTime=System.currentTimeMillis();
        while (timerRunning){
            if (!timerPaused) {
                time = (System.currentTimeMillis() + timeSaved) - startTime ;
                main.onTimeUpdated(time);
                if (timerPaused){
                    timeSaved=time;
                }
            }
            try {
                Thread.sleep(31);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (restartingAtEnd){
            time = 0;
            main.onTimeUpdated(time);
        }
    }
}
