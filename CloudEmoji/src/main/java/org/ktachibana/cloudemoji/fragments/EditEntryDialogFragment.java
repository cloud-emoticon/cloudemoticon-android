package org.ktachibana.cloudemoji.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.databases.FavoritesDataSource;
import org.ktachibana.cloudemoji.helpers.RepoXmlParser;
import org.ktachibana.cloudemoji.interfaces.OnFavoritesDatabaseOperationsListener;

public class EditEntryDialogFragment extends DialogFragment {

    private RepoXmlParser.Entry oldEntry;
    private boolean createNewEntry;

    public static final String OLD_ENTRY_KEY = "oldEntry";
    public static final String NEW_ENTRY_KEY = "newEntry";

    public static EditEntryDialogFragment createInstance(RepoXmlParser.Entry oldEntry) {
        EditEntryDialogFragment fragment = new EditEntryDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(OLD_ENTRY_KEY, oldEntry);
        fragment.setArguments(args);
        return fragment;
    }

    public EditEntryDialogFragment() {
        // Required empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        if (getArguments() != null) {
            oldEntry = (RepoXmlParser.Entry) getArguments().getSerializable(OLD_ENTRY_KEY);
            createNewEntry = (oldEntry == null);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.fragment_edit_entry_dialog, null);
        // Set up edit texts
        final EditText editTextString = (EditText) rootView.findViewById(R.id.editTextString);
        final EditText editTextNote = (EditText) rootView.findViewById(R.id.editTextNote);
        if (!createNewEntry) {
            editTextString.setText(oldEntry.string);
            editTextString.setEnabled(false);
            editTextNote.setText(oldEntry.note);
        }
        // Set up dialogs
        if (createNewEntry) {
            builder.setTitle(R.string.add_entry);
        } else {
            builder.setTitle(R.string.edit_entry);
        }
        builder.setView(rootView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String string = editTextString.getText().toString();
                        String note = editTextNote.getText().toString();
                        RepoXmlParser.Entry newEntry = FavoritesDataSource.createEntry(string, note);
                        Intent intent = new Intent();
                        intent.putExtra(NEW_ENTRY_KEY, newEntry);
                        if (createNewEntry) {
                            getTargetFragment().onActivityResult(getTargetRequestCode(), FavoritesFragment.ADD_REQUEST_CODE, intent);
                        } else {
                            intent.putExtra(OLD_ENTRY_KEY, oldEntry);
                            getTargetFragment().onActivityResult(getTargetRequestCode(), FavoritesFragment.EDIT_REQUEST_CODE, intent);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditEntryDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}