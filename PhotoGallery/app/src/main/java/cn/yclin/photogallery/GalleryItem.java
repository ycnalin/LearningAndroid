package cn.yclin.photogallery;

import android.net.Uri;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GalleryItem {
    @SerializedName("title")
    @Expose()
    private String mCaption;
    @SerializedName("id")
    @Expose()
    private String mId;
    @SerializedName("url_s")
    @Expose()
    private String mUrl;
    @SerializedName("owner")
    @Expose()
    private String mOwner;

    @Override
    public String toString() {
        return mCaption;
    }

    public void setCaption(String mCaption) {
        this.mCaption = mCaption;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getCaption() {
        return mCaption;
    }

    public String getId() {
        return mId;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getOwner() {
        return mOwner;
    }
    public void setOwner(String owner) {
        mOwner = owner;
    }

    public Uri getPhotoPageUri() {
        return Uri.parse("http://www.flickr.com/photos/")
                .buildUpon()
                .appendPath(mOwner)
                .appendPath(mId)
                .build();
    }
}
