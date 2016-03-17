package org.ktachibana.cloudemoji;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import org.ktachibana.cloudemoji.events.EmptyEvent;
import org.ktachibana.cloudemoji.events.ShowSnackBarOnBaseActivityEvent;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * Base fragment for all fragments to extend
 * It includes snack bar, and event bus
 */
public class BaseFragment extends Fragment {
    protected EventBus mBus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBus = EventBus.getDefault();
        mBus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBus.unregister(this);
    }

    @Subscribe
    public void handle(EmptyEvent event) {

    }

    protected void showSnackBar(String message) {
        mBus.post(new ShowSnackBarOnBaseActivityEvent(message));
    }

    protected void showSnackBar(@StringRes int resId) {
        mBus.post(new ShowSnackBarOnBaseActivityEvent(getString(resId)));
    }
}
