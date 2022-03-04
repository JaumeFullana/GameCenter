package com.cifpfbmoll.gamecenter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cifpfbmoll.Utils.DataBaseAssistant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UserSettingsActivity extends AppCompatActivity {

    private TextView textPlayerName;
    private TextView textPlayerLevel;
    private ImageView imageProfilePicture;
    private Button buttonChangePicture;
    private Button buttonChangePassword;
    private Button buttonDeleteUser;
    private DataBaseAssistant db;
    private String userName;
    private final String [] levels= {"Rookie", "Intermediate", "Advanced", "Pro"};
    private Button buttonNext;
    private Button buttonBack;
    private TextView selectedMode;
    private int selectedModeIndex;
    private final String[] modes2048 = {"3x3","4x4","5x5"};
    private final String[] modesPegSolitaire = {"English","German","European"};
    private String[] modes;

    public DataBaseAssistant getDb() {
        return db;
    }

    public String getUserName() {
        return userName;
    }

    /**
     * Overrided method, when the buttonChangePicture is clicked and the picture from the gallery is
     * selected this method is called and set the selected picture as the profilePicture of the user.
     * That picture is stored in the database.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3 && data!=null){
            try {
                Uri selectedImage = data.getData();
                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                Bitmap bitmap = Bitmap.createScaledBitmap(originalBitmap, 174, 201, false);
                this.imageProfilePicture.setImageBitmap(bitmap);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                this.db.changeUserPicture(this.getUserName(), stream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Overrided method, create all the necessary things to start the game.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String game = getIntent().getStringExtra("game");

        if (game == null) {
            setContentView(R.layout.activity_user_settings_default);
        } else {
            initGameSettingsComponents(game);
        }

        this.textPlayerName = findViewById(R.id.textGameName);
        this.textPlayerLevel = findViewById(R.id.textRecordGameMode);
        this.imageProfilePicture = findViewById(R.id.imageProfilePicture);
        this.buttonChangePicture = findViewById(R.id.buttonChangePicture);
        this.buttonChangePassword = findViewById(R.id.buttonChangePassword);
        this.buttonDeleteUser = findViewById(R.id.buttonDeleteUser);
        this.db = new DataBaseAssistant(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        userName = preferences.getString("userName", "Wrong.User.Name");
        this.textPlayerName.setText(userName);

        byte[] picture=this.db.getUserPicture(this.userName);
        if (picture!=null && picture.length!=0) {
            this.imageProfilePicture.setImageBitmap(BitmapFactory.decodeByteArray(picture,0,picture.length));
        }

        Cursor cursor = db.getRecords(userName, null, "score", null, null);
        String level=getPlayerLevel(cursor);
        this.textPlayerLevel.setText(level);

        addButtonListeners();
    }

    /**
     * Init the components needed when the activity is started from one of the games.
     * @param game game that started the activity
     */
    private void initGameSettingsComponents(String game) {
        String mode = getIntent().getStringExtra("mode");
        setContentView(R.layout.activity_user_settings_game_mode);
        this.buttonNext = findViewById(R.id.buttonNext);
        this.buttonBack = findViewById(R.id.buttonBack);
        this.selectedMode = findViewById(R.id.textViewMode);
        if (game.equals("2048")){
            modes = modes2048;
        } else {
            modes = modesPegSolitaire;
        }

        int i=0;
        boolean found=false;
        while ( i<modes.length && !found){
            if (modes[i].equals(mode)){
                selectedModeIndex=i;
                this.selectedMode.setText(this.modes[i]);
                found = true;
            }
            i++;
        }

        this.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("hola",""+selectedModeIndex);
                selectedModeIndex = (selectedModeIndex+1)%3;
                selectedMode.setText(modes[selectedModeIndex]);
            }
        });

        this.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("hola",""+selectedModeIndex);
                selectedModeIndex = (selectedModeIndex-1)%3;
                if (selectedModeIndex<0){
                    selectedModeIndex=2;
                }
                selectedMode.setText(modes[selectedModeIndex]);
            }
        });
    }

    /**
     * Overrided method. Put an extra on the intent to be recived in a onActivityResult().
     */
    @Override
    public void onBackPressed() {
        if (this.selectedMode!=null) {
            Intent intent = new Intent();
            intent.putExtra("MODE", this.selectedMode.getText().toString());
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    /**
     * adds onClickListener to the activity buttons.
     */
    private void addButtonListeners() {
        this.buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PasswordChangerDialog(UserSettingsActivity.this)
                        .show(UserSettingsActivity.this.getSupportFragmentManager(),"CHANGE PASSWORD");
            }
        });

        this.buttonChangePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,3);
            }
        });

        this.buttonDeleteUser.setBackgroundColor(Color.RED);
        this.buttonDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDeleteUserDialog();
            }
        });
    }

    /**
     * Creates a dialog to make sure that the user really wants to delete it's user.
     */
    private void createDeleteUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserSettingsActivity.this);
        builder.setMessage("Are you sure you want to delete your user?")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.deleteUser(userName);
                        finishAffinity();
                        Intent intent=new Intent(UserSettingsActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create().show();
    }

    /**
     * Get the player level averaging the best results of each game.
     * @param cursor cursor with all the records of the player
     * @return String the player level
     */
    private String getPlayerLevel(Cursor cursor) {
        int player2048BestPuntuation=0;
        int playerPegBestPuntuation=45;
        if (cursor != null && cursor.getCount() != 0){
            if (cursor.moveToFirst()){
                do {
                    if (cursor.getString(2).equals("2048")){
                        if (player2048BestPuntuation < cursor.getInt(0)){
                            player2048BestPuntuation = cursor.getInt(0);
                        }
                    } else {
                        if (playerPegBestPuntuation > cursor.getInt(0)){
                            playerPegBestPuntuation = cursor.getInt(0);
                        }
                    }
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        int levelIndex=((get2048Level(player2048BestPuntuation)+getPegLevel(playerPegBestPuntuation))/2);
        return this.levels[levelIndex];
    }

    /**
     * get the level of the 2048 puntuation
     * @param puntuation to check and get the level from
     * @return int 2048 level
     */
    private int get2048Level(int puntuation){
        int level=0;
        if (puntuation<5001){
            level=0;
        } else if (puntuation<15001){
            level=1;
        } else if (puntuation<25001){
            level=2;
        } else {
            level=3;
        }
        return level;
    }

    /**
     * get the level of the peg solitaire puntuation
     * @param puntuation to check and get the level from
     * @return int pegSolitaire level
     */
    private int getPegLevel(int puntuation){
        int level=0;
        if (puntuation==1){
            level=3;
        } else if (puntuation<7){
            level=2;
        } else if (puntuation<15){
            level=1;
        } else {
            level=0;
        }
        return level;
    }
}

