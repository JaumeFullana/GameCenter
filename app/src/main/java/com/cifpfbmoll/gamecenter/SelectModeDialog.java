package com.cifpfbmoll.gamecenter;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.cifpfbmoll.game2048.Game2048Activity;
import com.cifpfbmoll.gamepegsolitaire.GamePegSolitaireActivity;

public class SelectModeDialog extends DialogFragment {

    private String game;
    private String[] modes;
    private Button buttonSelect;
    private Button buttonNext;
    private Button buttonBack;
    private ImageView previewImage;
    private TextView selectedMode;
    private int selectedModeIndex;

    public SelectModeDialog(String game, String[] modes) {
        this.game = game;
        this.modes = modes;
    }

    /**
     * Overrided method.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.select_mode_dialog, null));

        return builder.create();
    }

    /**
     * Overrided method, create all the necessary things to start the dialog.
     */
    @Override
    public void onStart() {
        super.onStart();

        this.buttonSelect = getDialog().findViewById(R.id.buttonSelectMode);
        this.buttonNext = getDialog().findViewById(R.id.buttonNext);
        this.buttonBack = getDialog().findViewById(R.id.buttonBack);
        this.selectedMode = getDialog().findViewById(R.id.textViewMode);
        this.previewImage=getDialog().findViewById(R.id.imagePreview);
        this.selectedMode.setText(this.modes[0]);
        this.selectedModeIndex=0;
        this.changePreviewImage();

        addButtonsListeners();
    }

    /**
     * Adds the listeners to the buttons.
     */
    private void addButtonsListeners() {
        this.buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (game.equals("2048")) {
                    Intent intent = new Intent(getActivity(), Game2048Activity.class);
                    intent.putExtra("MODE",selectedMode.getText().toString());
                    startActivity(intent);
                    getDialog().dismiss();
                }
                else if (game.equals("Peg Solitaire")){
                    Intent intent = new Intent(getActivity(), GamePegSolitaireActivity.class);
                    intent.putExtra("MODE",selectedMode.getText().toString());
                    startActivity(intent);
                    getDialog().dismiss();
                }
            }
        });

        this.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("hola",""+selectedModeIndex);
                selectedModeIndex = (selectedModeIndex+1)%3;
                selectedMode.setText(modes[selectedModeIndex]);
                SelectModeDialog.this.changePreviewImage();
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
                SelectModeDialog.this.changePreviewImage();
            }
        });
    }

    /**
     * Change the image showed in the dialog depending of the game and of the selected item in the
     * modes array.
     */
    public void changePreviewImage(){
        if (game.equals("2048")) {
            switch (selectedModeIndex){
                case 0:
                    previewImage.setBackgroundResource(R.drawable.preview_2048_3x3);
                    break;

                case 1:
                    previewImage.setBackgroundResource(R.drawable.preview_2048_4x4);
                    break;

                case 2:
                    previewImage.setBackgroundResource(R.drawable.preview_2048_5x5);
                    break;
            }
        }
        else if (game.equals("Peg Solitaire")){
            switch (selectedModeIndex){
                case 0:
                    previewImage.setBackgroundResource(R.drawable.preview_peg_english);
                    break;

                case 1:
                    previewImage.setBackgroundResource(R.drawable.preview_peg_german);
                    break;

                case 2:
                    previewImage.setBackgroundResource(R.drawable.preview_peg_european);
                    break;
            }
        }
    }

}
