package org.ktachibana.cloudemoji.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.linearlistview.LinearListView;
import com.orm.SugarApp;

import org.apache.commons.io.IOUtils;
import org.ktachibana.cloudemoji.Constants;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.LeftDrawerListItem;
import org.ktachibana.cloudemoji.adapters.LeftDrawerListViewAdapter;
import org.ktachibana.cloudemoji.events.CategoryClickedEvent;
import org.ktachibana.cloudemoji.events.LocalRepositoryClickedEvent;
import org.ktachibana.cloudemoji.events.RemoteRepositoryParsedEvent;
import org.ktachibana.cloudemoji.helpers.SourceXmlParser;
import org.ktachibana.cloudemoji.models.Category;
import org.ktachibana.cloudemoji.models.Repository;
import org.ktachibana.cloudemoji.models.Source;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class LeftDrawerFragment extends Fragment implements Constants {
    private static final String PARSE_ON_FIRST_TIME_ID_TAG = "firstTime";
    @InjectView(R.id.leftDrawerSourceListView)
    LinearListView mSourceListView;
    @InjectView(R.id.leftDrawerCategoryListView)
    LinearListView mCategoryListView;
    private long mParseOnFirstTimeId;

    public LeftDrawerFragment() {
        // Required public constructor
    }

    public static LeftDrawerFragment newInstance(long id) {
        LeftDrawerFragment fragment = new LeftDrawerFragment();
        Bundle args = new Bundle();
        args.putLong(PARSE_ON_FIRST_TIME_ID_TAG, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParseOnFirstTimeId = getArguments().getLong(PARSE_ON_FIRST_TIME_ID_TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Setup views
        View rootView = inflater.inflate(R.layout.fragment_left_drawer, container, false);
        ButterKnife.inject(this, rootView);

        // Setup source list view
        final List<LeftDrawerListItem> sourceListItems = getSourceListItems();
        mSourceListView.setAdapter(new LeftDrawerListViewAdapter(sourceListItems, getActivity()));
        mSourceListView.setOnItemClickListener(new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView linearListView, View view, int i, long l) {
                long id = sourceListItems.get(i).getId();

                /**
                 * If it is a local repository clicked
                 * Clean categories and notify anyone who cares
                 * Namely the anyone is main activity
                 */
                if (id < 0) {
                    mCategoryListView.setAdapter(null);
                    EventBus.getDefault().post(new LocalRepositoryClickedEvent(id));
                }

                /**
                 * Else it is a remote repository clicked
                 * Parse the file according to id and fill categories
                 * Notify anyone who cares
                 * Namely the anyone is main activity
                 */
                else {
                    EventBus.getDefault()
                            .post(new RemoteRepositoryParsedEvent(setupCategoryListView(id), id));
                }
            }
        });

        // Setup first time category list view if it is valid remote repository
        if (mParseOnFirstTimeId >= 0) {
            if (Repository.findById(Repository.class, mParseOnFirstTimeId) != null) {
                setupCategoryListView(mParseOnFirstTimeId);
            }
        }
        return rootView;
    }

    private Source setupCategoryListView(long repositoryId) {
        Source source = readSourceFromFile(repositoryId);
        if (source != null) {
            mCategoryListView.setAdapter(
                    new LeftDrawerListViewAdapter(
                            getCategoryListItems(source), getActivity())
            );
            mCategoryListView.setOnItemClickListener(
                    new LinearListView.OnItemClickListener() {
                        @Override
                        public void onItemClick(LinearListView linearListView, View view, int i, long l) {
                            EventBus.getDefault().post(new CategoryClickedEvent(i));
                        }
                    }
            );
        }
        return source;
    }

    private Source readSourceFromFile(long id) {
        Source source = null;

        // Get repository file name
        String fileName = Repository.findById(Repository.class, id).getFileName();

        // Read the file from file system
        File file = new File(SugarApp.getSugarContext().getFilesDir(), fileName);

        // Read it
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            try {
                // Parse source from file
                source = new SourceXmlParser().parse(fileReader);
            }

            // Parser error
            catch (XmlPullParserException e) {
                Toast.makeText(getActivity(), getString(R.string.invalid_repo_format), Toast.LENGTH_SHORT)
                        .show();
            } catch (IOException e) {
                Log.e(DEBUG_TAG, e.getLocalizedMessage());
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            IOUtils.closeQuietly(fileReader);
        }

        return source;
    }


    private List<LeftDrawerListItem> getSourceListItems() {
        List<LeftDrawerListItem> items = new ArrayList<LeftDrawerListItem>();

        // Add local favorite and history
        items.add(
                new LeftDrawerListItem(
                        getString(R.string.fav), R.drawable.ic_favorite, LIST_ITEM_FAVORITE_ID)
        );
        items.add(
                new LeftDrawerListItem(
                        getString(R.string.history), R.drawable.ic_history, LIST_ITEM_HISTORY_ID)
        );

        // Add remote repositories
        List<Repository> repositories = Repository.listAll(Repository.class);
        for (Repository repository : repositories) {
            if (repository.isAvailable()) {
                items.add(
                        new LeftDrawerListItem(
                                repository.getAlias(), R.drawable.ic_repository, repository.getId())
                );
            }
        }

        return items;
    }

    private List<LeftDrawerListItem> getCategoryListItems(Source source) {
        List<LeftDrawerListItem> items = new ArrayList<LeftDrawerListItem>();

        for (Category category : source.getCategories()) {
            items.add(new LeftDrawerListItem(category.getName(), R.drawable.ic_category, 0));
        }

        return items;
    }
}
