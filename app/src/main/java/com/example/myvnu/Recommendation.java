package com.example.myvnu;

import android.content.Context;
import android.util.Log;

import com.example.myvnu.roomdatabase.Place;
import com.example.myvnu.DBAction;
import com.google.android.gms.maps.model.LatLng;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Recommendation {
    private DBAction dbAction = new DBAction();

    private double dis(LatLng latLng, Place place){
        double curLat = latLng.latitude;
        double curLng = latLng.longitude;
        double lat = place.getLat();
        double lng = place.getLng();
        return (curLat - lat)*(curLat - lat) + (curLng - lng)*(curLng - lng);
    }

    public List<Place> findPlaceWithQuery(Context context, String query){
        String[] words = query.split(" ");
        int numWords = 10;
        if(words.length < 10) numWords = words.length;
        while (numWords >= 0){
            for (int i = 0; i + numWords < words.length; i++) {
                String tag = words[i];
                for (int j = 1; j < numWords; j++){
                    tag = tag + " " + words[i+j];
                }

                List<Place> places = dbAction.findPlaceWithTag(context, tag);

                if(places.size() > 0){
                    return places;
                }
            }
            numWords--;
        }
        List<Place>p = dbAction.findPlaceWithTitle(context,"khoa");
        return p;
    }
    public String makeIntro(Context context, Place place){
        String[] options = place.getIntro().split("#");
        return options[new Random().nextInt(options.length - 1)+1];
    }
}
