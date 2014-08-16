package org.ktachibana.cloudemoji.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.FavoriteAddedEvent;
import org.ktachibana.cloudemoji.models.Favorite;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class AddFavoriteDialogFragment extends DialogFragment implements Constants {
    @InjectView(R.id.addFavoriteEmoticonEditText)
    EditText mEmoticonEditText;
    @InjectView(R.id.addFavoriteDescriptionEditText)
    EditText mDescriptionEditText;

    public AddFavoriteDialogFragment() {
        // Required empty public constructor
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Setup views
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View rootView = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_add_favorite_dialog, null);
        ButterKnife.inject(this, rootView);

        // Setup dialog itself
        builder.setTitle(R.string.add_to_fav);
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
                final String emoticon = mEmoticonEditText.getText().toString();
                final String description = mDescriptionEditText.getText().toString();

                // Check if empty emoticon
                if (emoticon.equals("")) {
                    mEmoticonEditText.setError(getString(R.string.empty_emoticon));
                    return;
                }

                // Check favorite emoticon duplicate
                List<Favorite> favorites = Favorite.listAll(Favorite.class);
                for (Favorite favorite : favorites) {
                    if (favorite.getEmoticon().equals(emoticon)) {
                        mEmoticonEditText.setError(getString(R.string.duplicate_emoticon));
                        return;
                    }
                }

                // Else add to favorites
                Favorite favorite = new Favorite(emoticon, description);
                favorite.save();

                // Notify main activity about it
                EventBus.getDefault().post(new FavoriteAddedEvent(emoticon));

                // Notify target fragment about it
                try {
                    ((FavoriteFragment) getTargetFragment()).notifyFavoriteAdded();
                } catch (ClassCastException e) {
                    Log.e(DEBUG_TAG, e.getLocalizedMessage());
                }

                // Dismiss dialog
                dialog.dismiss();

            }
        });
    }

}
