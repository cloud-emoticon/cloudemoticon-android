package org.ktachibana.cloudemoji.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.models.Repository;

public class AddRepositoryDialogFragment extends DialogFragment {

    public AddRepositoryDialogFragment() {
        // Required empty constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_add_repository_dialog, null);
        final EditText urlEditText = (EditText) rootView.findViewById(R.id.addRepositoryUrlEditText);
        builder.setTitle(R.string.add_repo);
        builder.setView(rootView);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = urlEditText.getText().toString();
                String extension = FilenameUtils.getExtension(url);
                if (extension.equals("json") || extension.equals("xml")) {
                    Repository repository = new Repository(getActivity().getBaseContext(), url, DateTime.now());
                    repository.save();
                    dialog.dismiss();
                    Toast.makeText(getActivity().getBaseContext(), "SAVED", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getBaseContext(), "INVALID", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
