package org.ktachibana.cloudemoji;

import android.support.v4.app.Fragment;

import com.github.mrengineer13.snackbar.SnackBar;

public class BaseFragment extends Fragment {
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
