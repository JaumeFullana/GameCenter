package com.cifpfbmoll.gamecenter;

public class Score {

    private int score;
    private String time;
    private String game;
    private String mode;
    private String user_name;

    public Score(int score, String time, String game, String mode, String user_name) {
        this.score = score;
        this.time = time;
        this.game = game;
        this.mode = mode;
        this.user_name = user_name;
    }

    public int getScore() {
        return score;
    }

    public String getTime() {
        return time;
    }

    public String getMode() {
        return mode;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getGame() {
        return game;
    }
}
