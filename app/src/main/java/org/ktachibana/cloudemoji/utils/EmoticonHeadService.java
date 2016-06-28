package org.ktachibana.cloudemoji.utils;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.ktachibana.cloudemoji.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EmoticonHeadService extends Service {
    private WindowManager windowManager;
    private View rootView;
    private boolean showing;
    @Bind(R.id.icon)
    ImageView icon;
    @Bind(R.id.window)
    FrameLayout window;

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
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        // Gesture detector for icon
        final GestureDetectorCompat detector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                params.x = (int) e2.getRawX();
                params.y = (int) e2.getRawY();
                windowManager.updateViewLayout(rootView, params);
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // When clicked, show/hide window
                if (showing) {
                    window.setVisibility(View.GONE);
                } else {
                    window.setVisibility(View.VISIBLE);
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

        // When view touched, move with finger
//        icon.setOnTouchListener(new View.OnTouchListener() {
//            private int initialX;
//            private int initialY;
//            private float initialTouchX;
//            private float initialTouchY;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        initialX = params.x;
//                        initialY = params.y;
//                        initialTouchX = event.getRawX();
//                        initialTouchY = event.getRawY();
//                        return true;
//                    case MotionEvent.ACTION_UP:
//                        return true;
//                    case MotionEvent.ACTION_MOVE:
//                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
//                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
//                        windowManager.updateViewLayout(rootView, params);
//                        return true;
//                }
//                return false;
//            }
//        });

        // Add
        windowManager.addView(rootView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (rootView != null) {
            windowManager.removeView(rootView);
        }
    }
}
