package com.keithsmyth.lastfeed.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Feed {
    @PrimaryKey
    public final long time;

    public final int left;

    public final int right;

    public final boolean snack;

    public Feed(long time, int left, int right, boolean snack) {
        this.time = time;
        this.left = left;
        this.right = right;
        this.snack = snack;
    }

    @Ignore
    public Feed(long time) {
        this.time = time;
        left = 0;
        right = 0;
        snack = false;
    }
}
