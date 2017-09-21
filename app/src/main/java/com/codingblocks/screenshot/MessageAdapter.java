package com.codingblocks.screenshot;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Arpit on 13-09-2017.
 */

public class MessageAdapter extends ArrayAdapter<FriendlyMessage> {

    private OnItemClickListener clickListener;
    public MessageAdapter( Context context, int resource,List<FriendlyMessage> objects) {
        super(context, resource, objects);
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
        }

        ImageView photoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.nameTextView);

        View view = convertView;

        final FriendlyMessage message = getItem(position);

        if(message.getPhotoUrl() ==null){
            photoImageView.setImageResource(R.drawable.def);
        }else {

            Glide.with(photoImageView.getContext())
                    .load(message.getPhotoUrl())
                    .into(photoImageView);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != clickListener)
                {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("data",message);
                    clickListener.onItemClicked(bundle);
                }
            }
        });

        authorTextView.setText(message.getName());

        return convertView;
    }
}
