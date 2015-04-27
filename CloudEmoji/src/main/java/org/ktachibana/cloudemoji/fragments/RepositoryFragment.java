package org.ktachibana.cloudemoji.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;

import org.apache.commons.io.FilenameUtils;
import org.ktachibana.cloudemoji.BaseFragment;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.RepositoryListViewAdapter;
import org.ktachibana.cloudemoji.events.NetworkUnavailableEvent;
import org.ktachibana.cloudemoji.events.RepositoryBeginEditingEvent;
import org.ktachibana.cloudemoji.events.RepositoryDownloadFailedEvent;
import org.ktachibana.cloudemoji.events.RepositoryDownloadedEvent;
import org.ktachibana.cloudemoji.events.RepositoryExportedEvent;
import org.ktachibana.cloudemoji.events.RepositoryInvalidFormatEvent;
import org.ktachibana.cloudemoji.models.Repository;
import org.ktachibana.cloudemoji.utils.MultiInputMaterialDialogBuilder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class RepositoryFragment extends BaseFragment implements Constants {
    @InjectView(R.id.repositoryListView)
    ListView mRepositoryListView;

    @InjectView(R.id.emptyView)
    RelativeLayout mRepositoryEmptyView;

    @InjectView(R.id.emptyViewTextView)
    TextView mEmptyViewTextView;

    @InjectView(R.id.fab)
    FloatingActionButton mFab;

    private RepositoryListViewAdapter mAdapter;

    public RepositoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Setup views
        View rootView = inflater.inflate(R.layout.fragment_repository, container, false);
        ButterKnife.inject(this, rootView);

        // Setup contents
        mRepositoryListView.setEmptyView(mRepositoryEmptyView);
        mEmptyViewTextView.setText(getString(R.string.no_repo_prompt));
        this.mAdapter = new RepositoryListViewAdapter(getActivity());
        mRepositoryListView.setAdapter(mAdapter);
        mFab.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_fab_create));
        mFab.attachToListView(mRepositoryListView);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAddRepositoryDialog("");
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void popupAddRepositoryDialog(String passedInUrl) {
        /**
         AddRepositoryDialogFragment fragment = AddRepositoryDialogFragment.newInstance(passedInUrl);
         fragment.show(getFragmentManager(), "add_repository");
         **/
        new MultiInputMaterialDialogBuilder(getActivity())
                .addInput(passedInUrl, getString(R.string.repo_url), new MultiInputMaterialDialogBuilder.InputValidator() {
                    @Override
                    public CharSequence validate(CharSequence input) {
                        // Get URL and extension
                        String url = input.toString();
                        String extension = FilenameUtils.getExtension(url);

                        // Detect duplicate URL
                        List<Repository> repositories = Repository.listAll(Repository.class);
                        for (Repository repository : repositories) {
                            if (url.equals(repository.getUrl())) {
                                return getString(R.string.duplicate_url);
                            }
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

    /**
     * Listens for repository downloaded from Internet, namely from repository list view adapter
     *
     * @param event repository downloaded from Internet event
     */
    public void onEvent(RepositoryDownloadedEvent event) {
        showSnackBar(event.getRepository().getAlias() + " " + getString(R.string.downloaded));
        mAdapter.updateRepositories();
    }

    /**
     * Listens for repository download from Internet failed, namely from repository list view adapter
     *
     * @param event repository download failed from Internet event
     */
    public void onEvent(RepositoryDownloadFailedEvent event) {
        showSnackBar(event.getThrowable().getLocalizedMessage());
    }

    /**
     * Listens for repository being edited, namely from repository list view adapter
     *
     * @param event repository being edited event
     */
    public void onEvent(RepositoryBeginEditingEvent event) {
        /**
         EditRepositoryDialogFragment fragment
         = EditRepositoryDialogFragment.newInstance(event.getRepository());
         fragment.show(getFragmentManager(), "edit_repository");
         **/
        final Repository repository = event.getRepository();
        new MultiInputMaterialDialogBuilder(getActivity())
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

    public void onEvent(NetworkUnavailableEvent event) {
        showSnackBar(R.string.bad_conn);
    }

    public void onEvent(RepositoryExportedEvent exportedEvent) {
        showSnackBar(exportedEvent.getPath());
    }

    public void onEvent(RepositoryInvalidFormatEvent event) {
        showSnackBar(getString(R.string.invalid_repo_format) + event.getType());
    }
}