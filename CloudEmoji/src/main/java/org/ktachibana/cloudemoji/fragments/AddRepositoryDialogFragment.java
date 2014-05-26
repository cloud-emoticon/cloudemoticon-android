package org.ktachibana.cloudemoji.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.commons.io.FilenameUtils;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.RepositoryAddedEvent;
import org.ktachibana.cloudemoji.models.Repository;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Dialog fragment to add a repository to database
 */
public class AddRepositoryDialogFragment extends DialogFragment implements Constants {
    @InjectView(R.id.addRepositoryUrlEditText)
    EditText mUrlEditText;
    @InjectView(R.id.addRepositoryAliasEditText)
    EditText mAliasEditText;

    public AddRepositoryDialogFragment() {
        // Required empty constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Setup views
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_repository_dialog, null);
        ButterKnife.inject(this, rootView);

        // Setup dialog itself
        builder.setTitle(R.string.add_repo);
        builder.setView(rootView);

        // Setup dialog's positive button
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /**
                 * Here we do nothing because we want other code to handle the event
                 * because when this button is clicked, nothing can prevent this dialog from closing
                 * Even if we want URL validation
                 */
            }
        });

        // Setup dialog's negative button
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Hacky way to get the button
        final AlertDialog dialog = (AlertDialog) getDialog();
        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);

        // What happens if positive button is clicked
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get URL and extension
                String url = mUrlEditText.getText().toString();
                String extension = FilenameUtils.getExtension(url);
                boolean duplicateUrl = false;

                // Detect duplicate URL
                List<Repository> repositories = Repository.listAll(Repository.class);
                for (Repository repository : repositories) {
                    if (url.equals(repository.getUrl())) {
                        duplicateUrl = true;
                    }
                }

                // If duplicate, set error in URL edit text
                if (duplicateUrl) {
                    mUrlEditText.setError(getString(R.string.duplicate_url));
                }

                else {
                    // Detect correct file format
                    // TODO: support json
                    if (extension.equals("xml")) {
                        // Get alias
                        String alias = mAliasEditText.getText().toString();

                        // Create and save repository to database
                        Repository repository = new Repository(getActivity().getBaseContext(), url, alias);
                        repository.save();

                        /**
                         * Tell anybody who cares about a repository added
                         * The anybody is namely repository list fragment
                         */
                        EventBus.getDefault().post(new RepositoryAddedEvent(repository));

                        // Dialog dismissed
                        dialog.dismiss();
                    }

                    // Unsupported format, set error in URL edit text
                    else {
                        mUrlEditText.setError(getString(R.string.invalid_repo_format));
                    }
                }
            }
        });
    }
}
