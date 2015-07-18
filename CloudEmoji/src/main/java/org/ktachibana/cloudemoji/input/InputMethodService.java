package org.ktachibana.cloudemoji.input;

import android.os.Build;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import com.astuetz.PagerSlidingTabStrip;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.EntryAddedToHistoryEvent;
import org.ktachibana.cloudemoji.events.FavoriteAddedEvent;
import org.ktachibana.cloudemoji.events.FavoriteDeletedEvent;
import org.ktachibana.cloudemoji.models.disk.Repository;
import org.ktachibana.cloudemoji.models.memory.Entry;
import org.ktachibana.cloudemoji.models.memory.Source;
import org.ktachibana.cloudemoji.parsing.SourceParsingException;
import org.ktachibana.cloudemoji.parsing.SourceReader;
import org.ktachibana.cloudemoji.utils.SourceInMemoryCache;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;


public class InputMethodService extends android.inputmethodservice.InputMethodService implements InputMethodPagerAdapter.OnInputCompletedListener {
    private final static String TAG = "InputMethodService";
    @InjectView(R.id.pager)
    protected ViewPager mPager;
    @InjectView(R.id.tabs)
    protected PagerSlidingTabStrip mTabs;

    private SourceInMemoryCache mCache;
    private EventBus BUS;
    private boolean inputMethodRecovered;
    private InputMethodPagerAdapter mPageAdapter;

    @Override
    public void onCreate() {
        this.setTheme(R.style.AppTheme);
        super.onCreate();
        BUS = EventBus.getDefault();
        BUS.register(this);
        mCache = this.initializeCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BUS.unregister(this);
    }

    private SourceInMemoryCache initializeCache() {
        SourceInMemoryCache cache = new SourceInMemoryCache();

        /**
         // Put favorites
         Source favoritesSource = FavoritesHelper.getFavoritesAsSource();
         cache.put(-1, favoritesSource);
         **/

        // Put all available repositories
        List<Repository> allRepositories = Repository.listAll(Repository.class);
        for (Repository repository : allRepositories) {
            if (repository.isAvailable()) {
                try {
                    long id = repository.getId();
                    Source source =
                            new SourceReader().readSourceFromDatabaseId(repository.getAlias(), id);
                    cache.put(id, source);
                } catch (SourceParsingException e) {
                    // showSnackBar(getString(R.string.invalid_repo_format) + e.getFormatType().toString());
                } catch (Exception e) {
                    //\\Log.e(DEBUG_TAG, e.getLocalizedMessage());
                }
            }
        }

        return cache;
    }

    @Subscribe
    public void handle(FavoriteAddedEvent event) {
        mPageAdapter.updateView(true);
    }

    @Subscribe
    public void handle(FavoriteDeletedEvent event) {
        mPageAdapter.updateView(false);
    }


    private void swithInputMethod() {
        if (inputMethodRecovered) return;
        inputMethodRecovered = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            try {
                InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
                final IBinder token = this.getWindow().getWindow().getAttributes().token;
                imm.switchToLastInputMethod(token);
            } catch (Throwable t) { // java.lang.NoSuchMethodError if API_level<11
                Log.e(TAG, "cannot set the previous input method:");
                t.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateInputView() {
        View rootView = getLayoutInflater().inflate(R.layout.fragment_base_tabs_pager, null, false);
        ButterKnife.inject(this, rootView);
        mPageAdapter = new InputMethodPagerAdapter(this, mCache);
        mPageAdapter.setOnInputCompletedListener(this);
        mPager.setAdapter(mPageAdapter);
        mTabs.setViewPager(mPager);
        inputMethodRecovered = false;
        return rootView;
    }


    @Override
    public void onWindowHidden() {
        super.onWindowHidden();
        swithInputMethod();
    }

    @Override
    public void onInputCompleted(Entry entry) {
        InputConnection ic = getCurrentInputConnection();
        ic.commitText(entry.getEmoticon(), 1);
        BUS.post(new EntryAddedToHistoryEvent(entry));
        InputMethodService.this.hideWindow();
        swithInputMethod();
    }
}
