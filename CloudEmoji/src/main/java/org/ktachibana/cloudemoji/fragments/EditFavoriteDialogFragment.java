package org.ktachibana.cloudemoji.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.FavoriteEditedEvent;
import org.ktachibana.cloudemoji.models.Favorite;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class EditFavoriteDialogFragment extends DialogFragment {
    private static final String ARG_FAVORITE = "favorite";
    @InjectView(R.id.editFavoriteEditText)
    EditText mFavoriteEditText;
    private Favorite mFavorite;

    public EditFavoriteDialogFragment() {
        // Required empty public constructor
    }

    public static EditFavoriteDialogFragment newInstance(Favorite favorite) {
        EditFavoriteDialogFragment fragment = new EditFavoriteDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FAVORITE, favorite);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFavorite = (Favorite) getArguments().getSerializable(ARG_FAVORITE);
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Setup views
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View rootView = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_edit_favorite_dialog, null);
        ButterKnife.inject(this, rootView);

        // Setup dialog itself
        builder.setTitle(getString(R.string.edit_favorite));
        builder.setView(rootView);

        // Setup alias edit text
        mFavoriteEditText.setText(mFavorite.getDescription());

        // Setup positive button
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Get the new favorite and SAVE
                mFavorite.setDescription(mFavoriteEditText.getText().toString());
                mFavorite.save();

                /**
                 * Tell anybody who cares about a repository edited
                 * Namely the anybody is repository list fragment
                 */
                EventBus.getDefault().post(new FavoriteEditedEvent());
            }
        });

        // Setup negative button
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        return builder.create();
    }
}
