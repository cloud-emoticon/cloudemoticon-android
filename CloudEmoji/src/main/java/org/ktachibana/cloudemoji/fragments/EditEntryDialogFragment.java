package org.ktachibana.cloudemoji.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import org.ktachibana.cloudemoji.R;

public class EditEntryDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        AlertDialog.Builder builer = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.fragment_edit_entry_dialog, null);
        // Set up edit texts
        final EditText editTextString = (EditText) rootView.findViewById(R.id.editTextString);
        final EditText editTextNote = (EditText) rootView.findViewById(R.id.editTextNote);
        // Set up dialogs
        builer.setTitle(null);
        return builer.create();
    }
}
