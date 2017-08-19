package com.keithsmyth.lastfeed.view;

import android.support.annotation.StringDef;

import com.keithsmyth.lastfeed.model.Feed;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class EditFeedViewModel {

    private static final int NEW_FEED = -1;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({LEFT, RIGHT})
    @interface Side {}

    static final String LEFT = "L";
    static final String RIGHT = "R";

    int hour;
    int minute;
    boolean snack;
    long oldTime;

    private final List<String> sides = new ArrayList<>();

    long getTime() {
        Calendar c = Calendar.getInstance();
        long now = c.getTimeInMillis();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        // backwards from midnight check
        if (c.getTimeInMillis() > now) {
            c.add(Calendar.DAY_OF_YEAR, -1);
        }
        return c.getTimeInMillis();
    }

    int getLeft() {
        return sides.indexOf(LEFT) + 1;
    }

    int getRight() {
        return sides.indexOf(RIGHT) + 1;
    }

    void toggleSide(@Side String side) {
        if (sides.contains(side)) {
            sides.remove(side);
        } else {
            sides.add(side);
        }
    }

    void newFeed() {
        oldTime = NEW_FEED;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        sides.clear();
        snack = false;
    }

    void editFeed(Feed feed) {
        oldTime = feed.time;

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(feed.time);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        sides.clear();
        if (feed.left == 1) {
            sides.add(LEFT);
            if (feed.right == 2) {
                sides.add(RIGHT);
            }
        } else if (feed.right == 1) {
            sides.add(RIGHT);
            if (feed.left == 2) {
                sides.add(LEFT);
            }
        }

        snack = feed.snack;
    }
}
