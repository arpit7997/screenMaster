package com.codingblocks.screenshot;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by mayankchauhan on 13/09/17.
 */

public class NotificationViewHolder extends RecyclerView.ViewHolder {

    TextView nameText;
    public NotificationViewHolder(View itemView) {
        super(itemView);

        nameText = (TextView) itemView.findViewById(R.id.notification_row_name);
    }
}
