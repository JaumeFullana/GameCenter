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
import android.widget.Toast;

import com.cifpfbmoll.Utils.DataBaseAssistant;

public class SignUpActivity extends AppCompatActivity {

    private Button signUpButton;
    private EditText editTextUserName;
    private EditText editTextPassword;
    private EditText editTextPassword2;
    private DataBaseAssistant db;

    /**
     * Overrided method, create all the necessary things to start the game.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.signUpButton=findViewById(R.id.buttonSignUp2);
        this.editTextUserName=findViewById(R.id.editSignUpUser);
        this.editTextPassword=findViewById(R.id.editSignUpPassword);
        this.editTextPassword2=findViewById(R.id.editSignUpPassword2);
        this.db = new DataBaseAssistant(this);

        this.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

    /**
     * Method to sign up a user in the game center, the user name can't be repeated, the passwords
     * have to be equals and the user name can't be longer than 14 characters.
     */
    private void signUp() {
        if (editTextUserName.getText().toString().isEmpty() ||
                editTextPassword.getText().toString().isEmpty() ||
                editTextPassword2.getText().toString().isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
            builder.setMessage("All fields must be filled")
                    .setTitle("ERROR");
            builder.create().show();
        }
        else if (!editTextPassword.getText().toString().equals(editTextPassword2.getText().toString())){
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
            builder.setMessage("The passwords aren't equals")
                    .setTitle("ERROR");
            builder.create().show();
        }
        else if (editTextUserName.getText().toString().length()>14){
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
            builder.setMessage("The user name can't be longer than 14 characters")
                    .setTitle("ERROR");
            builder.create().show();
        } else {
            boolean inserted=db.addUser(editTextUserName.getText().toString().toLowerCase(), editTextPassword.getText().toString());
            if (inserted){
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                Toast toast = Toast.makeText(SignUpActivity.this, "Registered", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setMessage("This user alredy exist.")
                        .setTitle("ERROR");
                builder.create().show();
            }
        }
    }

    /**
     * Overrided method. Close the keyboard when another part of the screen is touched.
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}