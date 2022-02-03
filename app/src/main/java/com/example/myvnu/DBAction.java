package com.example.myvnu;

import android.content.Context;

import com.example.myvnu.roomdatabase.CustomPlace;
import com.example.myvnu.roomdatabase.CustomPlaceDatabase;
import com.example.myvnu.roomdatabase.MapInfo;
import com.example.myvnu.roomdatabase.Place;
import com.example.myvnu.roomdatabase.PlaceDatabase;

import java.util.List;

public class DBAction {
    public void insert(Context context, Place place){
        PlaceDatabase.getDatabase(context).placeDao().insert(place);
    }

    public List<Place> getAllDefaultPlaces(Context context){
        return PlaceDatabase.getDatabase(context).placeDao().getAllDefaultPlaces();
    }

    public List<String> getAllImages(Context context){
        return PlaceDatabase.getDatabase(context).placeDao().getAllImages();
    }

    public List<MapInfo> getAllDefaultMapInfo(Context context){
        return PlaceDatabase.getDatabase(context).placeDao().getAllDefaultMapInfo();
    }

    public List<Place> findPlaceWithTitle(Context context, String title){
        return PlaceDatabase.getDatabase(context).placeDao().findPlaceWithTitle(title);
    }

    public Place findPlaceWithLatLng(Context context, double lat, double lng){
        return PlaceDatabase.getDatabase(context).placeDao().findPlaceWithLatLng(lat, lng);
    }



    public void insert(Context context, CustomPlace customPlace){
        CustomPlaceDatabase.getDatabase(context).customPlaceDao().insert(customPlace);
    }

    public List<CustomPlace> getAllCustomPlaces(Context context){
        return CustomPlaceDatabase.getDatabase(context).customPlaceDao().getAllCustomPlaces();
    }

    public List<MapInfo> getAllCustomMapInfo(Context context){
        return CustomPlaceDatabase.getDatabase(context).customPlaceDao().getAllCustomMapInfo();
    }

    public List<CustomPlace> findCustomPlaceWithTitle(Context context, String title){
        return CustomPlaceDatabase.getDatabase(context).customPlaceDao().findCustomPlaceWithTilte(title);
    }

    public CustomPlace findCustomPlaceWithLatLng(Context context, double lat, double lng){
        return CustomPlaceDatabase.getDatabase(context).customPlaceDao().findCustomPlaceWithLatLng(lat, lng);
    }

    public void updateCustomPlace(Context context, CustomPlace customPlace){
        CustomPlaceDatabase.getDatabase(context).customPlaceDao().update(customPlace);
    }

    public void deleteCustomPlace(Context context, CustomPlace customPlace){
        CustomPlaceDatabase.getDatabase(context).customPlaceDao().delete(customPlace);
    }

    public void deleteAllCustomPlaces(Context context){
        CustomPlaceDatabase.getDatabase(context).customPlaceDao().deleteAll();
    }

}
