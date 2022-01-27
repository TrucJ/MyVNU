package com.example.myvnu.roomdatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;


import java.io.Serializable;

public class MapInfo implements Serializable {
    @NonNull
    @ColumnInfo(name = "lat")
    private double lat;

    @NonNull
    @ColumnInfo(name = "lng")
    private double lng;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "img_path")
    private String imgPath;

    public MapInfo(double lat, double lng, String title, String imgPath) {
        this.lat = lat;
        this.lng = lng;
        this.title = title;
        this.imgPath = imgPath;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
