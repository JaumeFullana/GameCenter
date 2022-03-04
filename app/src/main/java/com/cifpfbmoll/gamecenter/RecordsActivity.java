package com.cifpfbmoll.gamecenter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.cifpfbmoll.Utils.DataBaseAssistant;

import java.util.ArrayList;
import java.util.Collections;

public class RecordsActivity extends AppCompatActivity {

    private DataBaseAssistant db;
    private Spinner spinnerOrder;
    private Spinner spinnerSearch;
    private Spinner spinnerGame;
    private EditText editSearch;
    private Button buttonSearch;
    private String orderBy;
    private String game;
    private String searchSymbol;
    private String searchText;
    private String userName;
    private RecyclerView list;
    private ArrayList<Score> scoreList;
    private String appUser;

    /**
     * Overrided method, create all the necessary things to start the game.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        this.spinnerOrder = findViewById(R.id.spinnerOrderBy);
        this.spinnerSearch = findViewById(R.id.spinnerSearchBy);
        this.editSearch = findViewById(R.id.editSearch);
        this.buttonSearch = findViewById(R.id.buttonSearch);
        this.list = findViewById(R.id.recycleViewRecords);
        this.spinnerGame = findViewById(R.id.spinnerGame);
        this.db = new DataBaseAssistant(this);
        this.orderBy = "user_name";
        this.game = "2048";
        this.searchSymbol = null;
        this.searchText = null;
        this.userName = null;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.appUser = preferences.getString("userName", "Wrong.User.Name");

        Cursor cursor = db.getRecords(null, game, orderBy, searchSymbol, searchText);
        scoreList = getScoresList(cursor);
        addTouchHelper();
        addSpinnersListeners();
        addButtonListener();
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new CustomAdapter(scoreList, this));
    }

    /**
     * Add listener to the search button.
     */
    private void addButtonListener() {
        this.buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText=spinnerSearch.getSelectedItem().toString();
                if (searchText.equals("Score less than")){
                    searchSymbol="<";
                    searchText=editSearch.getText().toString();
                    userName=null;
                }
                else if (searchText.equals("Score more than")){
                    searchSymbol=">";
                    searchText=editSearch.getText().toString();
                    userName=null;
                } else {
                    userName=editSearch.getText().toString();
                    searchText=null;
                }
                updateRecycleView();
            }
        });
    }

    /**
     * Add listeners to the spinners.
     */
    private void addSpinnersListeners() {
        this.spinnerOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                orderBy = adapterView.getItemAtPosition(pos).toString();
                if (orderBy.equals("Name")){
                    orderBy = "user_name";
                }
                updateRecycleView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        this.spinnerGame.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                game = adapterView.getItemAtPosition(pos).toString();
                updateRecycleView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Creates a ItemTocuhHelper and attaches it to the recycleView.
     */
    private void addTouchHelper() {
        ItemTouchHelper touchHelper=new ItemTouchHelper(new ItemTouchHelper.SimpleCallback
                (0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (((CustomAdapter.ViewHolder)viewHolder).getTextUser().getText().toString().equals(appUser)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecordsActivity.this);
                    builder.setMessage("Do you want to delete this record?")
                            .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    db.deleteScore(appUser,
                                        ((CustomAdapter.ViewHolder)viewHolder).getTextRecord().getText().toString(),
                                            ((CustomAdapter.ViewHolder)viewHolder).getTextTime().getText().toString());
                                    updateRecycleView();
                                }
                            })
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    updateRecycleView();
                                }
                            });
                    builder.create().show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecordsActivity.this);
                    builder.setMessage("You can't delete other users' scores")
                                .setTitle("ERROR");
                    builder.create().show();
                    updateRecycleView();
                }
            }
        });

        touchHelper.attachToRecyclerView(list);
    }

    /**
     * Updates de recycle view and notify the update.
     */
    private void updateRecycleView() {
        Cursor cursor = db.getRecords(userName, game, orderBy, searchSymbol, searchText);
        scoreList = getScoresList(cursor);
        ((CustomAdapter) list.getAdapter()).setScoreList(scoreList);
        list.getAdapter().notifyDataSetChanged();
    }

    /**
     * Creates an ArrayList of scores from a cursos sended from the database.
     * @param cursor Cursor returned by the database
     * @return an ArrayList of scores
     */
    @NonNull
    private ArrayList<Score> getScoresList(Cursor cursor) {
        ArrayList<Score> scoreList = new ArrayList<>();

        if (cursor != null && cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    int score = cursor.getInt(0);
                    String time = cursor.getString(1);
                    String game = cursor.getString(2);
                    String mode = cursor.getString(3);
                    String user_name = cursor.getString(4);
                    scoreList.add(new Score(score, time, game, mode, user_name));
                } while (cursor.moveToNext());
            }
        }
        return scoreList;
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

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private ArrayList<Score> scoreList;
    private RecordsActivity activity;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ConstraintLayout constraintLayout;
        private final TextView textMode;
        private final TextView textUser;
        private final TextView textRecord;
        private final TextView textTime;

        public ViewHolder(View view) {
            super(view);

            this.constraintLayout=view.findViewById(R.id.gameConstraintLayoutRecords);
            this.textMode=view.findViewById(R.id.textGameMode);
            this.textUser=view.findViewById(R.id.textRecordPlayer);
            this.textRecord=view.findViewById(R.id.textRecordPuntuation);
            this.textTime=view.findViewById(R.id.textRecordTime);
        }

        public ConstraintLayout getConstraintLayout() {
            return constraintLayout;
        }

        public TextView getTextMode() {
            return textMode;
        }

        public TextView getTextUser() {
            return textUser;
        }

        public TextView getTextRecord() {
            return textRecord;
        }

        public TextView getTextTime() {
            return textTime;
        }
    }


    public CustomAdapter(ArrayList<Score> scoreList, RecordsActivity activity) {
        this.scoreList = scoreList;
        this.activity = activity;
    }

    public void setScoreList(ArrayList<Score> scoreList) {
        this.scoreList = scoreList;
    }

    /**
     * Overried method.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.record_row, viewGroup, false);

        return new ViewHolder(view);
    }

    /**
     * Overrided mehtod.
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        viewHolder.getTextMode().setText(scoreList.get(position).getMode());
        viewHolder.getTextRecord().setText(Integer.toString(scoreList.get(position).getScore()));
        viewHolder.getTextTime().setText(scoreList.get(position).getTime());
        viewHolder.getTextUser().setText(scoreList.get(position).getUser_name());
        if (scoreList.get(position).getGame().equals("2048")){
            viewHolder.getConstraintLayout().setBackgroundResource(R.drawable.icon_2048_small);
        }
        else if(scoreList.get(position).getGame().equals("Peg Solitaire")){
            viewHolder.getConstraintLayout().setBackgroundResource(R.drawable.icon_peg_solitaire_small);
        }

    }

    /**
     * Overrided method.
     */
    @Override
    public int getItemCount() {
        return scoreList.size();
    }
}