package com.keithsmyth.lastfeed.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FeedDao {

    @Query("Select * From Feed Order by time DESC")
    List<Feed> list();

    @Insert
    void insert(Feed feed);

    @Delete
    void delete(Feed feed);
}
