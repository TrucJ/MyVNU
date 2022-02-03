package com.example.myvnu.roomdatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "base", primaryKeys = {"lat", "lng"})
public class Place implements Serializable {
    @NonNull
    @ColumnInfo(name = "lat")
    public double lat;

    @NonNull
    @ColumnInfo(name = "lng")
    public double lng;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "img_path")
    public String imgPath;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "link")
    public String link;

    @ColumnInfo(name = "phone_number")
    public String phoneNumber;

    @ColumnInfo(name = "min_zoom")
    public int minZoom;

    @ColumnInfo(name = "max_zoom")
    public int maxZoom;

    @ColumnInfo(name = "tags")
    public String tags;

    @ColumnInfo(name = "intro")
    public String intro;

    public Place(){

    }

    public Place(double lat, double lng, String title, String imgPath, String description, String address, String link, String phoneNumber, int minZoom, int maxZoom, String tags, String intro) {
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
        this.tags = tags;
        this.intro = intro;
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }
}
