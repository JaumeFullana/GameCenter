package com.cifpfbmoll.gamecenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class Score implements Serializable {

    private int score;
    private String time;
    private String game;
    private String mode;
    private String user_name;
    private byte[] gamePicture;

    public Score(int score, String time, String game, String mode, String user_name, Bitmap gamePicture) {
        this.score = score;
        this.time = time;
        this.game = game;
        this.mode = mode;
        this.user_name = user_name;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        gamePicture.compress(Bitmap.CompressFormat.PNG, 0, stream);
        this.gamePicture = stream.toByteArray();
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

    public Bitmap getGamePicture() {
        return BitmapFactory.decodeByteArray(gamePicture,0,gamePicture.length);
    }
}
