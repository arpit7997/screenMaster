package com.codingblocks.screenshot;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

/**
 * Created by mayankchauhan on 13/09/17.
 */

public class NotificationAdapter extends FirebaseRecyclerAdapter<String,NotificationViewHolder> {
    /**
     * @param modelClass      Firebase will marshall the data at a location into
     *                        an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list.
     *                        You will be responsible for populating an instance of the corresponding
     *                        view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location,
     *                        using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public NotificationAdapter(Class<String> modelClass, int modelLayout, Class<NotificationViewHolder> viewHolderClass, Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    @Override
    protected void populateViewHolder(NotificationViewHolder viewHolder, String model, int position) {

        viewHolder.nameText.setText(model.trim() +" has taken screenshot");
    }
}
