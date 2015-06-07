package org.ktachibana.cloudemoji;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.mrengineer13.snackbar.SnackBar;

import org.ktachibana.cloudemoji.events.EmptyEvent;

import de.greenrobot.event.EventBus;

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

    public void onEvent(EmptyEvent event) {

    }

    protected void showSnackBar(String message) {
        new SnackBar.Builder(getActivity().getApplicationContext(), getView())
                .withMessage(message)
                .withDuration(SnackBar.SHORT_SNACK)
                .show();
    }

    protected void showSnackBar(int resId) {
        showSnackBar(getString(resId));
    }
}
