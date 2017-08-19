package com.keithsmyth.lastfeed.view;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

public class MainActivity extends AppCompatActivity implements LifecycleRegistryOwner {

    private static final String BOTTOM_SHEET_STATE = "BOTTOM_SHEET_STATE";

    private BottomSheetBehavior<CardView> bottomSheetBehavior;
    private Adapter adapter;
    private EditFeedViewHolder editFeedViewHolder;
    private EditFeedViewBinder editFeedViewBinder;
    private FeedsViewModel feedsViewModel;

    private final LifecycleRegistry registry = new LifecycleRegistry(this);

    @Override
    public LifecycleRegistry getLifecycle() {
        return registry;
    }

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
        CardView contentLayout = (CardView) findViewById(R.id.add_new_feed_layout);
        bottomSheetBehavior = BottomSheetBehavior.from(contentLayout);
        if (savedInstanceState != null) {
            bottomSheetBehavior.setState(savedInstanceState.getInt(BOTTOM_SHEET_STATE, STATE_HIDDEN));
        }

        editFeedViewHolder = new EditFeedViewHolder(findViewById(R.id.add_new_feed_layout));
        editFeedViewBinder = new EditFeedViewBinder();

        final OnSaveFeedClickListener saveFeedClickListener = new OnSaveFeedClickListener() {
            @Override
            public void onSaveFeedClicked(EditFeedViewModel feedViewModel) {
                bottomSheetBehavior.setState(STATE_HIDDEN);
                feedsViewModel.saveFeed();
            }
        };

        findViewById(R.id.add_new_feed_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                feedsViewModel.editFeedViewModel.newFeed();
                editFeedViewBinder.bind(editFeedViewHolder, feedsViewModel.editFeedViewModel, saveFeedClickListener);
            }
        });

        RecyclerView recycler = (RecyclerView) findViewById(R.id.feed_recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter = new Adapter());
        adapter.setEditFeedClickListener(new OnEditFeedClickListener() {
            @Override
            public void onEditFeedClicked(Feed feed) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                feedsViewModel.editFeedViewModel.editFeed(feed);
                editFeedViewBinder.bind(editFeedViewHolder, feedsViewModel.editFeedViewModel, saveFeedClickListener);
            }
        });

        feedsViewModel = ViewModelProviders.of(this).get(FeedsViewModel.class);
        feedsViewModel.init(this);

        feedsViewModel.feeds().observe(this, new Observer<List<Feed>>() {
            @Override
            public void onChanged(@Nullable List<Feed> feeds) {
                if (feeds != null) {
                    adapter.setFeeds(feeds);
                }
            }
        });

        feedsViewModel.error().observe(this, new Observer<Throwable>() {
            @Override
            public void onChanged(@Nullable Throwable throwable) {
                if (throwable != null) {
                    Snackbar.make(parent, throwable.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                }
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
            feedText = (TextView) itemView.findViewById(R.id.feed_text);
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
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editFeedClickListener.onEditFeedClicked(feed);
                }
            });
        }

        @Override
        public int getItemCount() {
            return feeds.size();
        }

        void setEditFeedClickListener(OnEditFeedClickListener editFeedClickListener) {
            this.editFeedClickListener = editFeedClickListener;
        }

        void setFeeds(List<Feed> feeds) {
            this.feeds.clear();
            this.feeds.addAll(feeds);
            notifyDataSetChanged();
        }
    }
}
