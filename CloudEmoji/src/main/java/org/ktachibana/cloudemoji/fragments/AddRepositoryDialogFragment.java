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
    public static final String ARG_PASSED_IN_URL = "mPassedInUrl";
    @InjectView(R.id.addRepositoryUrlEditText)
    EditText mUrlEditText;
    @InjectView(R.id.addRepositoryAliasEditText)
    EditText mAliasEditText;
    private String mPassedInUrl;

    public AddRepositoryDialogFragment() {
        // Required empty constructor
    }

    public static AddRepositoryDialogFragment newInstance(String passedInUrl) {
        AddRepositoryDialogFragment fragment = new AddRepositoryDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PASSED_IN_URL, passedInUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPassedInUrl = getArguments().getString(ARG_PASSED_IN_URL);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Setup views
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View rootView = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_add_repository_dialog, null);
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


        // Setup passed in url
        mUrlEditText.setText(mPassedInUrl);

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

                // Detect duplicate URL
                List<Repository> repositories = Repository.listAll(Repository.class);
                for (Repository repository : repositories) {
                    if (url.equals(repository.getUrl())) {
                        mUrlEditText.setError(getString(R.string.duplicate_url));
                        return;
                    }
                }

                // Detect correct file format
                if (extension.equals("xml") || extension.equals("json")) {
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
        });
    }
}
