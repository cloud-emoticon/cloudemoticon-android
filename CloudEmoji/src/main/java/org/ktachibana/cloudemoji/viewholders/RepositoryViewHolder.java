package org.ktachibana.cloudemoji.viewholders;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.orm.SugarApp;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.RepositoryDeletedEvent;
import org.ktachibana.cloudemoji.events.RepositoryDownloadedEvent;
import org.ktachibana.cloudemoji.models.Repository;

import java.io.File;

import de.greenrobot.event.EventBus;
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
        repositoryUrlTextView.setText(item.getUrl());
        repositoryDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ion.with(SugarApp.getSugarContext())
                        .load(item.getUrl())
                        .write(new File(item.getFileName()))
                        .setCallback(new FutureCallback<File>() {
                            @Override
                            public void onCompleted(Exception e, File result) {
                                RepositoryDownloadedEvent event;
                                if (e == null) {
                                    event = new RepositoryDownloadedEvent(item, RepositoryDownloadedEvent.Status.SUCCESS);
                                }
                                else
                                {
                                    event = new RepositoryDownloadedEvent(item, RepositoryDownloadedEvent.Status.FAIL);
                                }
                                EventBus.getDefault().post(event);
                            }
                        });
            }
        });
        repositoryDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.delete();
                EventBus.getDefault().post(new RepositoryDeletedEvent(item));
            }
        });
    }
}
