package org.ktachibana.cloudemoji.services;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import org.ktachibana.cloudemoji.utils.CapabilityUtils;

import java.util.List;

public class MyAccessibilityService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (!CapabilityUtils.accessibilitySetTextAvailable()) {
            return;
        }

        final int eventType = accessibilityEvent.getEventType();

        if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            handleViewTextChangedEvent(accessibilityEvent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void handleViewTextChangedEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo nodeInfo = event.getSource();
        if (nodeInfo == null) {
            return;
        }
        List<CharSequence> texts = event.getText();
        if (texts.size() == 0) {
            return;
        }
        CharSequence text = texts.get(0);
        if (text == null || text.length() == 0) {
            return;
        }
        Log.d("MyAccessibilityService", "Retrieved text: " + text);
        if (":kt".contentEquals(text)) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "ლ(╹◡╹ლ)");
            nodeInfo.performAction(
                    AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_TEXT.getId(),
                    arguments);
        }
    }

    @Override
    public void onInterrupt() {

    }
}
