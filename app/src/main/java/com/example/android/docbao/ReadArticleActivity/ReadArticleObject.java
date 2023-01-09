package com.example.android.docbao.ReadArticleActivity;

import android.os.Parcel;
import android.os.Parcelable;



public class ReadArticleObject implements Parcelable {
    public int id;
    public String title;
    public String link;

    public ReadArticleObject(int id, String title, String link) {
        this.id=id;
        this.title = title;
        this.link = link;
    }

    protected ReadArticleObject(Parcel in) {
        id=in.readInt();
        title = in.readString();
        link = in.readString();
    }

    public static final Creator<ReadArticleObject> CREATOR = new Creator<ReadArticleObject>() {
        @Override
        public ReadArticleObject createFromParcel(Parcel in) {
            return new ReadArticleObject(in);
        }

        @Override
        public ReadArticleObject[] newArray(int size) {
            return new ReadArticleObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(link);
    }
}
