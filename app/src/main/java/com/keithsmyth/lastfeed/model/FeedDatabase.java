package com.keithsmyth.lastfeed.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Feed.class}, version = 3)
public abstract class FeedDatabase extends RoomDatabase {
    public abstract FeedDao feedDao();
}
