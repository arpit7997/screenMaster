package com.codingblocks.screenshot;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Arpit on 13-09-2017.
 */

public class FriendlyMessage implements Parcelable {

    String photoUrl;
    String name;
    String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    protected FriendlyMessage(Parcel in) {
        photoUrl = in.readString();
        name = in.readString();
    }

    public static final Creator<FriendlyMessage> CREATOR = new Creator<FriendlyMessage>() {
        @Override
        public FriendlyMessage createFromParcel(Parcel in) {
            return new FriendlyMessage(in);
        }

        @Override
        public FriendlyMessage[] newArray(int size) {
            return new FriendlyMessage[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public FriendlyMessage(String name, String photoUrl,String uid) {

        this.photoUrl = photoUrl;
        this.name = name;
        this.uid = uid;
    }

    public FriendlyMessage(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(photoUrl);
        dest.writeString(name);
    }
}
