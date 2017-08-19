package com.keithsmyth.lastfeed.view;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TimePicker;

import com.keithsmyth.lastfeed.R;

class EditFeedViewHolder {

    final Context context;
    final TimePicker timePicker;
    final Button rightButton;
    final Button leftButton;
    final CheckBox snackCheckBox;
    final Button saveButton;

    EditFeedViewModel model;

    EditFeedViewHolder(View itemView) {
        context = itemView.getContext();
        timePicker = (TimePicker) itemView.findViewById(R.id.time_picker);
        rightButton = (Button) itemView.findViewById(R.id.right_button);
        leftButton = (Button) itemView.findViewById(R.id.left_button);
        snackCheckBox = (CheckBox) itemView.findViewById(R.id.snack_checkbox);
        saveButton = (Button) itemView.findViewById(R.id.save_feed_button);
    }
}
