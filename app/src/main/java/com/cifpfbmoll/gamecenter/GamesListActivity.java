package com.cifpfbmoll.gamecenter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class GamesListActivity extends AppCompatActivity {

    /**
     * Overrided method, create all the necessary things to start the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games_list);

        String[] textList=new String[2];
        textList[0]="Peg Solitaire";
        textList[1]="2048";

        String[] descriptionList=new String[2];
        descriptionList[0]="Board game where the objective is to empty the entire board except for a solitary peg";
        descriptionList[1]="Puzzle video game where the objective is to slide numbered tiles " +
                "on a grid to combine them to create the number 2048";

        int[] imageList=new int[2];
        imageList[0]=getResources().getIdentifier("icon_peg_solitaire","drawable", getPackageName());
        imageList[1]=getResources().getIdentifier("icon_2048","drawable", getPackageName());

        RecyclerView list = findViewById(R.id.recyclerViewGames);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new CustomGamesAdapter(textList, imageList, descriptionList, this));
    }

    /**
     * Overrided method.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_bar_options, menu);

        return true;
    }

    /**
     * Overrided method, call diferents methods depending on the item selected.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean result=true;
        switch(item.getItemId()){
            case R.id.settings_menu_bar:
                this.openSettings();
                break;
            case R.id.records:
                this.openRecords();
                break;
            default:
                result=super.onOptionsItemSelected(item);
        }
        return result;
    }

    /**
     * Starts the UserSettingsActivity.
     */
    public void openSettings(){
        Intent intent=new Intent(this, UserSettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Starts the RecordsActivity.
     */
    public void openRecords(){
        Intent intent=new Intent(this, RecordsActivity.class);
        startActivity(intent);
    }
}

class CustomGamesAdapter extends RecyclerView.Adapter<CustomGamesAdapter.ViewHolder> {

    private String[] textList;
    private int[] imageList;
    private String[] descriptionList;
    private GamesListActivity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageViewCard;
        private final ConstraintLayout constraintLayout;
        private String game;

        public ViewHolder(View view) {
            super(view);

            constraintLayout = (ConstraintLayout) view.findViewById(R.id.gameConstraintLayout);
            imageViewCard = (ImageView) view.findViewById(R.id.imageViewCard);
        }

        public ImageView getImageViewCard() {
            return imageViewCard;
        }

        public ConstraintLayout getConstraintLayout() {
            return constraintLayout;
        }

        public String getGame() { return game; }

        public void setGame(String game) { this.game = game; }
    }


    public CustomGamesAdapter(String[] textList, int [] imageList, String[] descriptionList, GamesListActivity activity) {
        this.textList = textList;
        this.imageList = imageList;
        this.descriptionList = descriptionList;
        this.activity = activity;
    }

    /**
     * Overrided method. When the listView is clicked a dialog to select the game mode is opened.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.game_card_row, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.getGame().equals("2048")) {
                    new SelectModeDialog("2048",new String[]{"3x3","4x4","5x5"})
                            .show(activity.getSupportFragmentManager(),"2048");
                }
                else if (viewHolder.getGame().equals("Peg Solitaire")){
                    new SelectModeDialog("Peg Solitaire", new String[]{"English","German","European"})
                            .show(activity.getSupportFragmentManager(),"Peg Solitaire");
                }
            }
        });

        return viewHolder;
    }

    /**
     * Overrided method.
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (textList[position].equals("2048")){
            viewHolder.getImageViewCard().setImageResource(R.drawable.img2048card);
        }
        else if(textList[position].equals("Peg Solitaire")){
            viewHolder.getImageViewCard().setImageResource(R.drawable.peg_solitaire_card);
        }
        viewHolder.setGame(textList[position]);
        viewHolder.getConstraintLayout().setBackgroundResource(imageList[position]);
    }

    @Override
    /**
     * Overrided method.
     */
    public int getItemCount() {
        return textList.length;
    }
}
