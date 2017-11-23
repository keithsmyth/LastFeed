package com.keithsmyth.lastfeed.view;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.keithsmyth.lastfeed.model.Feed;
import com.keithsmyth.lastfeed.model.FeedDao;
import com.keithsmyth.lastfeed.model.FeedDatabase;
import com.keithsmyth.lastfeed.model.FeedDatabaseProvider;

import java.util.List;

import rx.Completable;
import rx.Single;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

class FeedsViewModel extends ViewModel {

    final EditFeedViewModel editFeedViewModel = new EditFeedViewModel();

    private FeedDao feedDao;

    private final MutableLiveData<List<Feed>> feeds = new MutableLiveData<>();
    private final MutableLiveData<Throwable> error = new MutableLiveData<>();
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    void init(Context context) {
        if (feedDao == null) {

            FeedDatabase db = FeedDatabaseProvider.get(context);
            feedDao = db.feedDao();

            fetchFeeds();
        }
    }

    LiveData<List<Feed>> feeds() {
        return feeds;
    }

    LiveData<Throwable> error() {
        return error;
    }

    void saveFeed() {
        saveFeed(editFeedViewModel.oldTime, editFeedViewModel.getTime(), editFeedViewModel.getLeft(), editFeedViewModel.getRight(), editFeedViewModel.snack);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        subscriptions.unsubscribe();
    }

    private void saveFeed(long oldTime, long time, int left, int right, boolean snack) {
        final Feed newFeed = new Feed(time, left, right, snack);
        Completable insertFeed = Completable.fromAction(() -> feedDao.insert(newFeed));

        if (oldTime > -1) {
            final Feed oldFeed = new Feed(oldTime);
            insertFeed = insertFeed.startWith(Completable.fromAction(() -> feedDao.delete(oldFeed)));
        }

        Subscription s = insertFeed
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .andThen(getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()))
            .subscribe(this::refreshData, this.error::postValue);
        subscriptions.add(s);
    }

    private void fetchFeeds() {
        Subscription s = getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::refreshData, this.error::postValue);
        subscriptions.add(s);
    }

    private Single<List<Feed>> getAll() {
        return Single.fromCallable(() -> feedDao.list());
    }

    private void refreshData(List<Feed> feeds) {
        this.error.postValue(null);
        this.feeds.postValue(feeds);
    }
}
