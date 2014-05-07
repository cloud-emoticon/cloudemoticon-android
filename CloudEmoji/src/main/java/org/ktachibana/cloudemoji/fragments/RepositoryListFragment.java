package org.ktachibana.cloudemoji.fragments;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.models.Repository;

import java.util.ArrayList;
import java.util.List;

public class RepositoryListFragment extends Fragment {

    public RepositoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_repository_list, container, false);
        ListView repositoryListView = (ListView) rootView.findViewById(R.id.repositoryListView);
        repositoryListView.setEmptyView(rootView.findViewById(R.id.repositoryEmptyView));
        List<Repository> repositories = Repository.listAll(Repository.class);
        repositoryListView.setAdapter(generateAdapter(repositories));
        return rootView;
    }

    private FunDapter<Repository> generateAdapter(final List<Repository> repositories) {
        BindDictionary<Repository> dictionary = new BindDictionary<Repository>();
        dictionary.addStringField(R.id.repositoryURLTextView,
                new StringExtractor<Repository>() {
                    @Override
                    public String getStringValue(Repository repository, int i) {
                        return repository.getRemoteAddress();
                    }
                });
        dictionary.addStringField(R.id.repositoryLastUpdateTextView,
                new StringExtractor<Repository>() {
                    @Override
                    public String getStringValue(Repository repository, int i) {
                        return repository.getLastUpdate().toString();
                    }
                });
        ArrayList<Repository> whyArrayListAsInterface = new ArrayList<Repository>();
        for (Repository repository : repositories) {
            whyArrayListAsInterface.add(repository);
        }
        return new FunDapter<Repository>(getActivity(), whyArrayListAsInterface, R.layout.list_item_repository, dictionary);
    }
}
