package com.cifpfbmoll.gamecenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Records2048Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records_2048);

        SharedPreferences sharedPreferences = this.getSharedPreferences("PegSolitaire"+this.getPackageName(),Context.MODE_PRIVATE);
        setRecordValues(sharedPreferences);
    }

    public void setRecordValues(SharedPreferences sharedPreferences) {
        for (int i=1;i<6;i++){
            int record= sharedPreferences.getInt("record"+i,00000);
            int id = this.getResources().getIdentifier("record" +i+"Text","id",this.getPackageName());
            TextView view=(TextView)findViewById(id);
            view.setText(i+". "+record);
        }
    }

}