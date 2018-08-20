package cn.yclin.photogallery;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhotoPages {
    public static PhotoPages sPhotoPages;
    PhotoCollection photos;

    public static PhotoPages get(){
        if(sPhotoPages == null){
            sPhotoPages = new PhotoPages();
        }
        return sPhotoPages;
    }

    private PhotoPages(){ }

    public int getTotalPage() {
        return photos.pages;
    }

    public int getItemsPerPage() {
        return photos.perpage;
    }

    public List<GalleryItem> getPhotoList() {
        return photos.photoList;
    }
}

class PhotoCollection {
    int page;
    int pages;
    int perpage;
    int total;

    @SerializedName("photo")
    List<GalleryItem> photoList;

    List<GalleryItem> getPhotoList() {
        return photoList;
    }
}
