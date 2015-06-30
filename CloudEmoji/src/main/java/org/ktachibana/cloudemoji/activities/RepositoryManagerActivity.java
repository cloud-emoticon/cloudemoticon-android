package org.ktachibana.cloudemoji.activities;


import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;

import org.apache.commons.io.FilenameUtils;
import org.ktachibana.cloudemoji.BaseActivity;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.RepositoryListViewAdapter;
import org.ktachibana.cloudemoji.events.NetworkUnavailableEvent;
import org.ktachibana.cloudemoji.events.RepositoryBeginEditingEvent;
import org.ktachibana.cloudemoji.events.RepositoryDownloadFailedEvent;
import org.ktachibana.cloudemoji.events.RepositoryDownloadedEvent;
import org.ktachibana.cloudemoji.events.RepositoryExportedEvent;
import org.ktachibana.cloudemoji.events.RepositoryInvalidFormatEvent;
import org.ktachibana.cloudemoji.models.disk.Repository;
import org.ktachibana.cloudemoji.utils.MultiInputMaterialDialogBuilder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.Subscribe;

public class RepositoryManagerActivity extends BaseActivity implements Constants {
    @InjectView(R.id.repositoryListView)
    ListView mRepositoryListView;

    @InjectView(R.id.emptyView)
    RelativeLayout mRepositoryEmptyView;

    @InjectView(R.id.emptyViewTextView)
    TextView mEmptyViewTextView;

    @InjectView(R.id.fab)
    FloatingActionButton mFab;

    private RepositoryListViewAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_repository_manager);
        ButterKnife.inject(this);

        // Setup contents
        mRepositoryListView.setEmptyView(mRepositoryEmptyView);
        mEmptyViewTextView.setText(getString(R.string.no_repo_prompt));
        this.mAdapter = new RepositoryListViewAdapter(RepositoryManagerActivity.this);
        mRepositoryListView.setAdapter(mAdapter);
        mFab.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_fab_create));
        mFab.attachToListView(mRepositoryListView);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAddRepositoryDialog("");
            }
        });
    }

    public void popupAddRepositoryDialog(String passedInUrl) {
        /**
         AddRepositoryDialogFragment fragment = AddRepositoryDialogFragment.newInstance(passedInUrl);
         fragment.show(getFragmentManager(), "add_repository");
         **/
        new MultiInputMaterialDialogBuilder(RepositoryManagerActivity.this)
                .addInput(passedInUrl, getString(R.string.repo_url), new MultiInputMaterialDialogBuilder.InputValidator() {
                    @Override
                    public CharSequence validate(CharSequence input) {
                        // Get URL and extension
                        String url = input.toString();
                        String extension = FilenameUtils.getExtension(url);

                        // Detect duplicate URL
                        if (Repository.hasDuplicateUrl(url)) {
                            return getString(R.string.duplicate_url);
                        }

                        // Detect incorrect file format
                        if (!(extension.equals("xml") || extension.equals("json"))) {
                            return getString(R.string.invalid_repo_format);
                        } else {
                            return null;
                        }
                    }
                })
                .addInput(null, getString(R.string.alias))
                .inputs(new MultiInputMaterialDialogBuilder.InputsCallback() {
                    @Override
                    public void onInputs(MaterialDialog dialog, List<CharSequence> inputs, boolean allInputsValidated) {
                        if (allInputsValidated) {
                            String url = inputs.get(0).toString();
                            String alias = inputs.get(1).toString();

                            // Create and save repository to database
                            Repository repository = new Repository(url, alias);
                            repository.save();

                            mAdapter.updateRepositories();
                        }
                    }
                })
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .title(R.string.add_repo)
                .show();
    }

    @Subscribe
    public void handle(RepositoryDownloadedEvent event) {
        showSnackBar(event.getRepository().getAlias() + " " + getString(R.string.downloaded));
        mAdapter.updateRepositories();
    }

    @Subscribe
    public void handle(RepositoryDownloadFailedEvent event) {
        showSnackBar(event.getThrowable().getLocalizedMessage());
    }

    @Subscribe
    public void handle(RepositoryBeginEditingEvent event) {
        final Repository repository = event.getRepository();
        new MultiInputMaterialDialogBuilder(RepositoryManagerActivity.this)
                .addInput(repository.getAlias(), getString(R.string.alias))
                .inputs(new MultiInputMaterialDialogBuilder.InputsCallback() {
                    @Override
                    public void onInputs(MaterialDialog dialog, List<CharSequence> inputs, boolean allInputsValidated) {
                        String alias = inputs.get(0).toString();

                        // Get the new alias and SAVE
                        repository.setAlias(alias);
                        repository.save();

                        mAdapter.updateRepositories();
                    }
                })
                .title(R.string.edit_repository)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .show();
    }

    @Subscribe
    public void handle(NetworkUnavailableEvent event) {
        showSnackBar(R.string.bad_conn);
    }

    @Subscribe
    public void handle(RepositoryExportedEvent exportedEvent) {
        showSnackBar(exportedEvent.getPath());
    }

    @Subscribe
    public void handle(RepositoryInvalidFormatEvent event) {
        showSnackBar(getString(R.string.invalid_repo_format) + event.getType());
    }
}