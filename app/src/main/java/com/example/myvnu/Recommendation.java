package com.example.myvnu;

import android.content.Context;

import com.example.myvnu.roomdatabase.Place;
import com.example.myvnu.DBAction;

import java.util.List;

public class Recommendation {
    private DBAction dbAction = new DBAction();

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
        return null;
    }
    public String makeIntro(Context context, Place place){
        return place.getIntro();
    }
}
