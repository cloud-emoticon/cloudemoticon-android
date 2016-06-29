package org.ktachibana.cloudemoji.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.ktachibana.cloudemoji.R;
import org.ktachibana.cloudemoji.adapters.FavoriteListViewAdapter;
import org.ktachibana.cloudemoji.models.disk.Favorite;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EmoticonHeadService extends Service {
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private View rootView;

    private boolean showing;
    @Bind(R.id.icon)
    ImageView icon;

    @Bind(R.id.prompt)
    TextView prompt;

    List<Favorite> favorites;
    Adapter adapter;
    @Bind(R.id.list)
    ListView list;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        rootView = LayoutInflater.from(this).inflate(R.layout.view_emoticon_head, null);
        ButterKnife.bind(this, rootView);
        showing = true;

        // Window params
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        // Add
        windowManager.addView(rootView, params);

        // Gesture detector for icon
        final GestureDetectorCompat detector = new GestureDetectorCompat(EmoticonHeadService.this, new GestureDetector.SimpleOnGestureListener() {
            private float diffX;
            private float diffY;

            @Override
            public boolean onDown(MotionEvent e) {
                diffX = e.getRawX() - params.x;
                diffY = e.getRawY() - params.y;
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                favorites = Favorite.listAll(Favorite.class);
                adapter.clear();
                for (Favorite f : favorites) {
                    adapter.add(f);
                }
                adapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                // When scrolled, move the whole view
                params.x = (int) (e2.getRawX() - diffX);
                params.y = (int) (e2.getRawY() - diffY);
                windowManager.updateViewLayout(rootView, params);
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // When clicked, show/hide window
                if (showing) {
                    list.setVisibility(View.GONE);
                    prompt.setVisibility(View.GONE);
                } else {
                    list.setVisibility(View.VISIBLE);
                    prompt.setVisibility(View.VISIBLE);
                }
                showing = !showing;
                return true;
            }
        });
        icon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });

        // List
        favorites = Favorite.listAll(Favorite.class);
        adapter = new Adapter(this, favorites);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String emoticon = favorites.get(position).getEmoticon();
                CopyUtils.copyToClipboard(EmoticonHeadService.this, emoticon);

                Toast.makeText(EmoticonHeadService.this, emoticon + "\n" + getString(R.string.copied), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (rootView != null) {
            windowManager.removeView(rootView);
        }
    }

    private class Adapter extends ArrayAdapter<Favorite> {
        public Adapter(Context context, List<Favorite> favorites) {
            super(context, 0, favorites);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Favorite favorite = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_double_line_emoticon, parent, false);
            }
            TextView emoticon = (TextView) convertView.findViewById(R.id.emoticonTextView);
            TextView description = (TextView) convertView.findViewById(R.id.descriptionTextView);
            emoticon.setText(favorite.getEmoticon());
            description.setText(favorite.getDescription());
            emoticon.setTextColor(getResources().getColor(android.R.color.black));
            description.setTextColor(getResources().getColor(android.R.color.black));
            return convertView;
        }
    }
}
