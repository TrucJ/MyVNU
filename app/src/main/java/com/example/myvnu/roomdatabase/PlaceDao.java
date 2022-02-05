package com.example.myvnu.roomdatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Place place);

    @Query("SELECT * FROM base")
    List<Place> getAllDefaultPlaces();

    @Query("SELECT img FROM base")
    List<String> getAllImages();

    @Query("SELECT icon FROM base")
    List<String> getAllIcons();

    @Query("SELECT intro FROM base")
    List<String> getAllIntros();

    @Query("SELECT tags FROM base")
    List<String> getAllTags();

    @Query("SELECT lat, lng, title, img FROM base")
    List<MapInfo> getAllDefaultMapInfo();

    @Query("SELECT * FROM base WHERE LOWER(title) like '%' || LOWER(:title) || '%'")
    List<Place> findPlaceWithTitle(String title);

    @Query("SELECT * FROM base WHERE lat = :lat AND lng = :lng")
    Place findPlaceWithLatLng(double lat, double lng);
}
