package com.example.myvnu;

import android.content.Context;

import com.example.myvnu.roomdatabase.Place;
import com.example.myvnu.DBAction;
import com.google.android.gms.maps.model.LatLng;

import java.util.Iterator;
import java.util.List;

public class Recommendation {
    private DBAction dbAction = new DBAction();

    private double dis(LatLng latLng, Place place){
        double curLat = latLng.latitude;
        double curLng = latLng.longitude;
        double lat = place.getLat();
        double lng = place.getLng();
        return (curLat - lat)*(curLat - lat) + (curLng - lng)*(curLng - lng);
    }

    public List<Place> findPlaceWithQuery(Context context, String query, LatLng latLng){
        String[] words = query.split(" ");
        int numWords = 10;
        if(words.length < 10) numWords = words.length;
        while (numWords >= 0){
            for (int i = 0; i + numWords < words.length; i++) {
                String tag = words[i];
                for (int j = 1; j < numWords; j++){
                    tag = tag + " " + words[i+j];
                }

                if(numWords == 0) tag = " ";

                List<Place> places = dbAction.findPlaceWithTag(context, tag);

                if(places.size() > 0){
                    List<Place> res=null;
                    Place[] placesArr = places.toArray(new Place[places.size()]);

                    double curDis = 0;
                    while (true) {
                        double minDis = 1000000000;
                        Place place = null;
                        for (int j = 0; j < placesArr.length; j++) {
                            if (minDis > dis(latLng, placesArr[j]) && dis(latLng, placesArr[j]) > curDis) {
                                minDis = dis(latLng, placesArr[j]);
                                place = placesArr[i];
                            }
                        }
                        curDis = minDis;
                        if (place == null) break;
                        res.add(place);
                    }

                    return res;
                }
            }
            numWords--;
        }
        return null;
    }
    public String makeIntro(Context context, Place place){
        return place.getIntro();
    }
}
