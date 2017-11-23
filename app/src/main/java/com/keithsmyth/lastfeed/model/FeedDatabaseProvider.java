package com.keithsmyth.lastfeed.model;

import android.arch.persistence.room.Room;
import android.content.Context;

public class FeedDatabaseProvider {

    public static FeedDatabase get(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), FeedDatabase.class, "feed.db")
            .build();
    }
}
