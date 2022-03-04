package com.cifpfbmoll.gamecenter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class RecordActivity extends AppCompatActivity {

    private ImageView textGameName;
    private ImageView imageGame;
    private TextView textUserName;
    private TextView textScore;
    private TextView textTime;

    /**
     * Overrided method. Overrided method, create all the necessary things to start the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        this.textGameName = findViewById(R.id.textGameName);
        this.imageGame = findViewById(R.id.imageGamePicture);
        this.textUserName = findViewById(R.id.textRecordUserName);
        this.textScore = findViewById(R.id.textPuntuationRecordActivity);
        this.textTime = findViewById(R.id.textRecordActivityTime);

        Score score = (Score)getIntent().getSerializableExtra("SCORE");

        if (score.getGame().equals("2048")){
            this.textGameName.setBackgroundResource(R.drawable.img2048big);
        } else {
            this.textGameName.setBackgroundResource(R.drawable.peg_solitaire);
        }

        this.imageGame.setImageBitmap(score.getGamePicture());
        this.textUserName.setText("User name:   "+score.getUser_name());
        this.textScore.setText("Score:   "+Integer.toString(score.getScore()));
        this.textTime.setText("Time:   "+score.getTime());
    }
}