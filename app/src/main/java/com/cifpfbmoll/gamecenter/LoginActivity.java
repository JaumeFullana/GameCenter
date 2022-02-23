package com.cifpfbmoll.gamecenter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.cifpfbmoll.Utils.DataBaseAssistant;

public class LoginActivity extends AppCompatActivity {

    private Button buttonLogin;
    private Button buttonSignUp;
    private EditText userName;
    private EditText userPassword;
    private DataBaseAssistant db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.buttonLogin = findViewById(R.id.buttonLogin);
        this.buttonSignUp = findViewById(R.id.buttonSignUp);
        this.userName = findViewById(R.id.editTextUser);
        this.userPassword = findViewById(R.id.editTextPassword);
        this.db = new DataBaseAssistant(this);

        this.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean found=db.selectUser(userName.getText().toString(), userPassword.getText().toString());
                if (found) {
                    Intent intent = new Intent(LoginActivity.this, GamesListActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("The user name or password isn't correct")
                            .setTitle("ERROR");
                    builder.create().show();
                }
            }
        });

        this.buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}