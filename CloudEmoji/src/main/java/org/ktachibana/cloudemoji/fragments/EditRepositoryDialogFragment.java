package org.ktachibana.cloudemoji.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.RepositoryEditedEvent;
import org.ktachibana.cloudemoji.models.Repository;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class EditRepositoryDialogFragment extends DialogFragment {
    private static final String ARG_REPOSITORY = "repository";
    @InjectView(R.id.editRepositoryAliasEditText)
    EditText mAliasEditText;
    private Repository mRepository;

    public EditRepositoryDialogFragment() {
        // Required empty public constructor
    }

    public static EditRepositoryDialogFragment newInstance(Repository repository) {
        EditRepositoryDialogFragment fragment = new EditRepositoryDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_REPOSITORY, repository);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRepository = (Repository) getArguments().getSerializable(ARG_REPOSITORY);
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Setup views
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View rootView = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_edit_repository_dialog, null);
        ButterKnife.inject(this, rootView);

        // Setup dialog itself
        builder.setTitle(getString(R.string.edit_repository));
        builder.setView(rootView);

        // Setup alias edit text
        mAliasEditText.setText(mRepository.getAlias());

        // Setup positive button
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Get the new alias and SAVE
                mRepository.setAlias(mAliasEditText.getText().toString());
                mRepository.save();

                /**
                 * Tell anybody who cares about a repository edited
                 * Namely the anybody is repository list fragment
                 */
                EventBus.getDefault().post(new RepositoryEditedEvent());
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
