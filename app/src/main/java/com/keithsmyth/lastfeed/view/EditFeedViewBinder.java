package com.keithsmyth.lastfeed.view;

import com.keithsmyth.lastfeed.R;
import com.keithsmyth.lastfeed.view.MainActivity.OnSaveFeedClickListener;

import static com.keithsmyth.lastfeed.view.EditFeedViewModel.LEFT;
import static com.keithsmyth.lastfeed.view.EditFeedViewModel.RIGHT;

class EditFeedViewBinder {

    void bind(final EditFeedViewHolder holder,
              EditFeedViewModel model,
              final OnSaveFeedClickListener saveFeedClickListener) {
        holder.model = model;

        int cacheHour = model.hour;
        int cacheMinute = model.minute;

        TimePickerCompat.setHour(holder.timePicker, cacheHour);
        TimePickerCompat.setMinute(holder.timePicker, cacheMinute);
        holder.timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            holder.model.hour = hourOfDay;
            holder.model.minute = minute;
        });

        bindSideButtons(holder, model);

        holder.leftButton.setOnClickListener(v -> {
            holder.model.toggleSide(LEFT);
            bindSideButtons(holder, holder.model);
        });

        holder.rightButton.setOnClickListener(v -> {
            holder.model.toggleSide(RIGHT);
            bindSideButtons(holder, holder.model);
        });

        holder.snackCheckBox.setChecked(holder.model.snack);
        holder.snackCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> holder.model.snack = isChecked);

        holder.saveButton.setOnClickListener(v -> saveFeedClickListener.onSaveFeedClicked(holder.model));
    }

    private void bindSideButtons(EditFeedViewHolder holder, EditFeedViewModel model) {
        holder.leftButton.setText(holder.context.getString(R.string.left_button, model.getLeft()));
        holder.rightButton.setText(holder.context.getString(R.string.right_button, model.getRight()));
    }
}
