package org.ktachibana.cloudemoji.ime;

import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.events.FavoriteAddedEvent;
import org.ktachibana.cloudemoji.events.FavoriteDeletedEvent;
import org.ktachibana.cloudemoji.models.disk.Favorite;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MyInputMethodService extends InputMethodService {
    @Bind(R.id.imeButton1)
    Button button1;

    @Bind(R.id.imeButton2)
    Button button2;

    @Bind(R.id.imeButton3)
    Button button3;

    @Bind(R.id.imeButton4)
    Button button4;

    @Bind(R.id.imeButton5)
    Button button5;

    @Bind(R.id.imeButton6)
    Button button6;

    List<Button> mButtons;

    protected EventBus mBus;

    @Override
    public void onCreate() {
        super.onCreate();
        mBus = EventBus.getDefault();
        mBus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBus.unregister(this);
    }

    @Subscribe
    public void handle(FavoriteAddedEvent event) {
        updateInputView();
    }

    @Subscribe
    public void handle(FavoriteDeletedEvent event) {
        updateInputView();
    }

    @Override
    public View onCreateInputView() {
        View rootView = getLayoutInflater().inflate(R.layout.view_ime_view, null, false);
        ButterKnife.bind(this, rootView);
        this.mButtons = Arrays.asList(button1, button2, button3, button4, button5, button6);

        updateInputView();

        return rootView;
    }

    private void updateInputView() {
        List<Favorite> favorites = Favorite.listAll(Favorite.class);

        for (int i = 0; i < Math.min(favorites.size(), mButtons.size()); ++i) {
            Button b = mButtons.get(i);
            Favorite f = favorites.get(i);
            b.setText(f.getEmoticon());
        }

        if (mButtons.size() > favorites.size()) {
            for (int j = 0; j < (mButtons.size() - favorites.size()); ++j) {
                Button b = mButtons.get(favorites.size() + j);
                b.setText(R.string.ime_no_favorite);
                b.setEnabled(false);
            }
        }
    }
}
