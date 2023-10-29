package com.example.virtualeyeforblinds.progessbar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.virtualeyeforblinds.R;

public class ProgressDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        ProgressDialog progressDialog = new ProgressDialog(requireContext());

        // Set the message to be displayed in the progress dialog
        progressDialog.setMessage("Please wait as the data is being fetched");

        // Set the progress dialog to be indeterminate (circular loading indicator)
        progressDialog.setIndeterminate(true);

        // Make the progress dialog non-cancelable (user cannot dismiss it)
        progressDialog.setCancelable(false);
        //int d=R.drawable.custom_progress_bar_horizontal;
        //progressDialog.setIndeterminateDrawable(R.drawable.custom_progress_bar_horizontal);


        return progressDialog;
    }
}
