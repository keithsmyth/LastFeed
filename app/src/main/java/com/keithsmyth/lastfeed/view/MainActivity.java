package com.keithsmyth.lastfeed.view;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.keithsmyth.lastfeed.BuildConfig;
import com.keithsmyth.lastfeed.R;
import com.keithsmyth.lastfeed.model.Feed;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;
import static com.keithsmyth.lastfeed.view.EditFeedViewModel.LEFT;
import static com.keithsmyth.lastfeed.view.EditFeedViewModel.RIGHT;

public class MainActivity extends AppCompatActivity {

    private static final String BOTTOM_SHEET_STATE = "BOTTOM_SHEET_STATE";

    private BottomSheetBehavior<CardView> bottomSheetBehavior;
    private Adapter adapter;
    private EditFeedViewHolder editFeedViewHolder;
    private EditFeedViewBinder editFeedViewBinder;
    private FeedsViewModel feedsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .penaltyFlashScreen()
                    .build()
            );
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final View parent = findViewById(R.id.root);
        CardView addNewFeedLayout = findViewById(R.id.add_new_feed_layout);
        bottomSheetBehavior = BottomSheetBehavior.from(addNewFeedLayout);
        if (savedInstanceState != null) {
            bottomSheetBehavior.setState(savedInstanceState.getInt(BOTTOM_SHEET_STATE, STATE_HIDDEN));
        }

        editFeedViewHolder = new EditFeedViewHolder(addNewFeedLayout);
        editFeedViewBinder = new EditFeedViewBinder();

        final OnSaveFeedClickListener saveFeedClickListener = feedViewModel -> {
            bottomSheetBehavior.setState(STATE_HIDDEN);
            feedsViewModel.saveFeed();
        };

        findViewById(R.id.add_new_feed_button).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            feedsViewModel.editFeedViewModel.newFeed();
            editFeedViewBinder.bind(editFeedViewHolder, feedsViewModel.editFeedViewModel, saveFeedClickListener);
        });

        RecyclerView recycler = findViewById(R.id.feed_recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter = new Adapter());
        adapter.setEditFeedClickListener(feed -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            feedsViewModel.editFeedViewModel.editFeed(feed);
            editFeedViewBinder.bind(editFeedViewHolder, feedsViewModel.editFeedViewModel, saveFeedClickListener);
        });

        feedsViewModel = ViewModelProviders.of(this).get(FeedsViewModel.class);
        feedsViewModel.init(this);

        feedsViewModel.feeds().observe(this, feeds -> {
            if (feeds != null) {
                adapter.setFeeds(feeds);
            }
        });

        feedsViewModel.error().observe(this, throwable -> {
            if (throwable != null) {
                Snackbar.make(parent, throwable.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

        editFeedViewBinder.bind(editFeedViewHolder, feedsViewModel.editFeedViewModel, saveFeedClickListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BOTTOM_SHEET_STATE, bottomSheetBehavior.getState());
    }

    interface OnSaveFeedClickListener {
        void onSaveFeedClicked(EditFeedViewModel feedViewModel);
    }

    private interface OnEditFeedClickListener {
        void onEditFeedClicked(Feed feed);
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView feedText;

        ViewHolder(View itemView) {
            super(itemView);
            feedText = itemView.findViewById(R.id.feed_text);
        }
    }

    private static class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private static final DateFormat dateFormat = new SimpleDateFormat("EEE dd MMM hh:mm a", Locale.getDefault());
        private final List<Feed> feeds = new ArrayList<>();

        private OnEditFeedClickListener editFeedClickListener;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_adapter_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Feed feed = feeds.get(position);
            StringBuilder sb = new StringBuilder(dateFormat.format(new Date(feed.time)));
            if (feed.left == 1) {
                sb.append(" ").append(LEFT);
                if (feed.right == 2) {
                    sb.append(",").append(RIGHT);
                }
            } else if (feed.right == 1) {
                sb.append(" ").append(RIGHT);
                if (feed.left == 2) {
                    sb.append(",").append(LEFT);
                }
            }
            if (feed.snack) {
                sb.append(" ").append(holder.itemView.getContext().getString(R.string.snack));
            }
            holder.feedText.setText(sb.toString());
            holder.itemView.setOnClickListener(v -> editFeedClickListener.onEditFeedClicked(feed));
        }

        @Override
        public int getItemCount() {
            return feeds.size();
        }

        void setEditFeedClickListener(OnEditFeedClickListener editFeedClickListener) {
            this.editFeedClickListener = editFeedClickListener;
        }

        void setFeeds(List<Feed> feeds) {
            final List<Feed> oldList = this.feeds;
            final List<Feed> newList = feeds;

            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return oldList.size();
                }

                @Override
                public int getNewListSize() {
                    return newList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return oldList.get(oldItemPosition).time == newList.get(newItemPosition).time;
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
                }
            });

            this.feeds.clear();
            this.feeds.addAll(feeds);

            result.dispatchUpdatesTo(this);
        }
    }
}
