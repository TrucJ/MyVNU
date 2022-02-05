package com.example.myvnu.roomdatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.HashMap;

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

    @ColumnInfo(name = "icon")
    public String icon;

    @ColumnInfo(name = "img")
    public String img;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "link")
    public String link;

    @ColumnInfo(name = "phone_number")
    public String phoneNumber;

    @ColumnInfo(name = "min_zoom_x4")
    public int minZoom;

    @ColumnInfo(name = "max_zoom_x4")
    public int maxZoom;

    @ColumnInfo(name = "intro")
    public String intro;

    @ColumnInfo(name = "tags")
    public String tags;

    public Place(){

    }

    public Place(double lat, double lng, String title, String icon, String img, String description, String address, String link, String phoneNumber, int minZoom, int maxZoom, String intro, String tags) {
        this.lat = lat;
        this.lng = lng;
        this.title = title;
        this.icon = icon;
        this.img = img;
        this.description = description;
        this.address = address;
        this.link = link;
        this.phoneNumber = phoneNumber;
        this.minZoom = minZoom;
        this.maxZoom = maxZoom;
        this.intro = intro;
        this.tags = tags;
    }

    public Place(HashMap<String, Object> h){
        this.lat = (Double) h.get("lat");
        this.lng = (Double) h.get("lng");
        this.title = (String) h.get("title");
        this.img = (String) h.get("img");
        this.description = (String) h.get("description");
        this.address = (String) h.get("address");
        this.link = (String) h.get("link");
        this.phoneNumber = (String) h.get("phoneNumber");
        this.minZoom = ((Long) h.get("minZoom")).intValue();
        this.maxZoom = ((Long) h.get("maxZoom")).intValue();
        this.tags = (String) h.get("tags");
        this.intro = (String) h.get("intro");
        this.icon = (String) h.get("icon");
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}

