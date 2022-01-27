package com.example.myvnu.roomdatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;

@Entity(tableName = "base", primaryKeys = {"lat", "lng"})
public class Place implements Serializable {
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

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "link")
    private String link;

    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    @ColumnInfo(name = "min_zoom")
    private int minZoom;

    @ColumnInfo(name = "max_zoom")
    private int maxZoom;

    @ColumnInfo(name = "tags")
    private ArrayList<String> tags;

    @ColumnInfo(name = "intro")
    private String intro;

    public Place(){

    }

    public Place(double lat, double lng, String title, String imgPath, String description, String address, String link, String phoneNumber, int minZoom, int maxZoom) {
        this.lat = lat;
        this.lng = lng;
        this.title = title;
        this.imgPath = imgPath;
        this.description = description;
        this.address = address;
        this.link = link;
        this.phoneNumber = phoneNumber;
        this.minZoom = minZoom;
        this.maxZoom = maxZoom;
        this.tags = null;
        this.intro = null;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getMinZoom() {
        return minZoom;
    }

    public void setMinZoom(int minZoom) {
        this.minZoom = minZoom;
    }

    public int getMaxZoom() {
        return maxZoom;
    }

    public void setMaxZoom(int maxZoom) {
        this.maxZoom = maxZoom;
    }
}
