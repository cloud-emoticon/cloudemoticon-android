package org.ktachibana.cloudemoji.services;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.RequiresApi;

import org.ktachibana.cloudemoji.models.disk.Favorite;
import org.ktachibana.cloudemoji.utils.CapabilityUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        for (final Favorite favorite: Favorite.listAll(Favorite.class)) {
            final String shortcut = favorite.getShortcut();
            if (shortcut == null || shortcut.length() == 0) {
                continue;
            }
            final Pattern pattern = Pattern.compile(String.format(
                    "^:%s| :%s",
                    shortcut,
                    shortcut));
            final Matcher matcher = pattern.matcher(text);
            if (!matcher.find()) {
                continue;
            }
            final boolean isStart = matcher.start() == 0;
            String replacedText;
            if (isStart) {
                replacedText = matcher.replaceFirst(favorite.getEmoticon());
            } else {
                replacedText = matcher.replaceFirst(" " + favorite.getEmoticon());
            }
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, replacedText);
            nodeInfo.performAction(
                    AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_TEXT.getId(),
                    arguments);
        }
    }

    @Override
    public void onInterrupt() {

    }
}
