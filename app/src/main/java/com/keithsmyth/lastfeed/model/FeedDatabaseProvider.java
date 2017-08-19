package com.keithsmyth.lastfeed.model;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

public class FeedDatabaseProvider {
    private static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE FeedTemp (" +
                "time INTEGER PRIMARY KEY," +
                "left INTEGER," +
                "right INTEGER," +
                "snack BIT)");
            db.execSQL("INSERT INTO FeedTemp " +
                "SELECT time,left,right,note='Snack' " +
                "FROM Feed");
            db.execSQL("DROP TABLE Feed");
            db.execSQL("ALTER TABLE FeedTemp RENAME TO Feed");
        }
    };

    public static FeedDatabase get(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), FeedDatabase.class, "feed.db")
            .addMigrations(MIGRATION_2_3)
            .build();
    }
}
