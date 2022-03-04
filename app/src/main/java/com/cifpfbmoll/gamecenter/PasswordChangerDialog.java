package com.cifpfbmoll.gamecenter;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class PasswordChangerDialog extends DialogFragment {

    private UserSettingsActivity activity;
    private EditText editNewPassword;
    private EditText editNewPasswordRepeated;
    private Button buttonCancel;
    private Button buttonChange;

    public PasswordChangerDialog(UserSettingsActivity activity) {
        this.activity = activity;
    }

    /**
     * Overrided method.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.new_password_input_dialog, null));

        return builder.create();
    }

    /**
     * Crates a simple dialog with a title and a message
     * @param title the title of the dialog
     * @param message the message of the dialog
     */
    private void createInfoDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setTitle(title);
        builder.create().show();
    }

    /**
     * Overrided method, create all the necessary things to start the dialog.
     */
    @Override
    public void onStart() {
        super.onStart();

        this.editNewPassword = getDialog().findViewById(R.id.editNewPassword);
        this.editNewPasswordRepeated = getDialog().findViewById(R.id.editRepeatNewPassword);
        this.buttonCancel = getDialog().findViewById(R.id.buttonPasswordCancel);
        this.buttonChange = getDialog().findViewById(R.id.buttonPasswordChange);

        this.buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editNewPassword.getText().toString().equals(editNewPasswordRepeated.getText().toString())){
                    boolean changed=activity.getDb().changeUserPassword(activity.getUserName(),editNewPassword.getText().toString());
                    if (changed){
                        createInfoDialog(null, "Password succesfully changed");
                        getDialog().cancel();
                    } else {
                        createInfoDialog("ERROR", "An error has occurred");
                    }
                } else {
                    createInfoDialog("ERROR", "The passwords aren't equals");
                }
            }
        });

        this.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().cancel();
            }
        });
    }
}
