package com.keithsmyth.lastfeed.view;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.TimePicker;

import com.keithsmyth.lastfeed.R;
import com.keithsmyth.lastfeed.view.MainActivity.OnSaveFeedClickListener;

import static com.keithsmyth.lastfeed.view.EditFeedViewModel.LEFT;
import static com.keithsmyth.lastfeed.view.EditFeedViewModel.RIGHT;

class EditFeedViewBinder {

    void bind(final EditFeedViewHolder holder,
              EditFeedViewModel model,
              final OnSaveFeedClickListener saveFeedClickListener) {
        holder.model = model;

        TimePickerCompat.setHour(holder.timePicker, model.hour);
        TimePickerCompat.setMinute(holder.timePicker, model.minute);
        holder.timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                holder.model.hour = hourOfDay;
                holder.model.minute = minute;
            }
        });

        bindSideButtons(holder, model);

        holder.leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.model.toggleSide(LEFT);
                bindSideButtons(holder, holder.model);
            }
        });

        holder.rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.model.toggleSide(RIGHT);
                bindSideButtons(holder, holder.model);
            }
        });

        holder.snackCheckBox.setChecked(holder.model.snack);
        holder.snackCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                holder.model.snack = isChecked;
            }
        });

        holder.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFeedClickListener.onSaveFeedClicked(holder.model);
            }
        });
    }

    private void bindSideButtons(EditFeedViewHolder holder, EditFeedViewModel model) {
        holder.leftButton.setText(holder.context.getString(R.string.left_button, model.getLeft()));
        holder.rightButton.setText(holder.context.getString(R.string.right_button, model.getRight()));
    }
}
