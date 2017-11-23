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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Feed feed = (Feed) o;

        if (time != feed.time) return false;
        if (left != feed.left) return false;
        if (right != feed.right) return false;
        return snack == feed.snack;
    }

    @Override
    public int hashCode() {
        int result = (int) (time ^ (time >>> 32));
        result = 31 * result + left;
        result = 31 * result + right;
        result = 31 * result + (snack ? 1 : 0);
        return result;
    }
}
