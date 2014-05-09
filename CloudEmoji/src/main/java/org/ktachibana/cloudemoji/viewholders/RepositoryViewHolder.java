package org.ktachibana.cloudemoji.viewholders;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.models.Repository;

import uk.co.ribot.easyadapter.ItemViewHolder;
import uk.co.ribot.easyadapter.PositionInfo;
import uk.co.ribot.easyadapter.annotations.LayoutId;
import uk.co.ribot.easyadapter.annotations.ViewId;

@LayoutId(R.layout.list_item_repository)
public class RepositoryViewHolder extends ItemViewHolder<Repository> {

    @ViewId(R.id.repositoryAliasTextView)
    TextView repositoryAliasTextView;

    @ViewId(R.id.repositoryUrlTextView)
    TextView repositoryUrlTextView;

    @ViewId(R.id.repositoryDownloadButton)
    ImageButton repositoryDownloadButton;

    @ViewId(R.id.repositoryDeleteButton)
    ImageButton repositoryDeleteButton;

    public RepositoryViewHolder(View view) {
        super(view);
    }

    @Override
    public void onSetValues(final Repository item, PositionInfo positionInfo) {
        repositoryAliasTextView.setText(item.getAlias());
        repositoryUrlTextView.setText(item.getRemoteAddress());
        repositoryDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("233", "Downloading: " + item.getRemoteAddress());
            }
        });
        repositoryDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("233", "Deleting: " + item.getRemoteAddress());
            }
        });
    }
}
