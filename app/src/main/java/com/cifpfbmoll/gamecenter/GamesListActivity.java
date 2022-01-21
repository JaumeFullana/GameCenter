package com.cifpfbmoll.gamecenter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

public class GamesListActivity extends AppCompatActivity {

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

        RecyclerView list=(RecyclerView)findViewById(R.id.recyclerViewGames);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new CustomAdapter(textList, imageList, descriptionList));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_bar_options, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean result=true;
        switch(item.getItemId()){
            case R.id.settings_menu_bar:

                break;
            case R.id.help_menu_bar:

                break;
            default:
                result=super.onOptionsItemSelected(item);
        }
        return result;
    }
}

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private String[] textList;
    private int[] imageList;
    private String[] descriptionList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageViewCard;
        private final TextView descriptionViewCard;
        private final ConstraintLayout constraintLayout;
        private String game;

        public ViewHolder(View view) {
            super(view);

            constraintLayout = (ConstraintLayout) view.findViewById(R.id.gameConstraintLayout);
            imageViewCard = (ImageView) view.findViewById(R.id.imageViewCard);
            descriptionViewCard = (TextView) view.findViewById(R.id.descriptionCardView);
        }

        public ImageView getImageViewCard() {
            return imageViewCard;
        }

        public ConstraintLayout getConstraintLayout() {
            return constraintLayout;
        }

        public TextView getDescriptionViewCard() { return descriptionViewCard; }

        public String getGame() { return game; }

        public void setGame(String game) { this.game = game; }
    }


    public CustomAdapter(String[] textList, int [] imageList, String[] descriptionList) {
        this.textList = textList;
        this.imageList = imageList;
        this.descriptionList = descriptionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.game_card_row, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);

        //mirar de crear es listener a onCreate, li posam a sa recycle view directement. Pensa que
        // es onItemTouchListener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=null;

                if (viewHolder.getGame().equals("2048")) {
                    intent = new Intent(((GamesListActivity) view.getContext()), Game2048Activity.class);
                }
                else if (viewHolder.getGame().equals("Peg Solitaire")){
                    intent = new Intent(((GamesListActivity) view.getContext()), GamePegSolitaireActivity.class);
                }

                ((GamesListActivity)view.getContext()).startActivity(intent);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (textList[position]=="2048"){
            viewHolder.getImageViewCard().setImageResource(R.drawable.img2048card);
        }
        else if(textList[position]=="Peg Solitaire"){
            viewHolder.getImageViewCard().setImageResource(R.drawable.peg_solitaire_card);
        }
        viewHolder.setGame(textList[position]);
        viewHolder.getConstraintLayout().setBackgroundResource(imageList[position]);
        //viewHolder.getDescriptionViewCard().setText(descriptionList[position]);
    }

    @Override
    public int getItemCount() {
        return textList.length;
    }
}
