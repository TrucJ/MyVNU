package com.example.myvnu.roomdatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CustomPlaceDao {

    // Conveniences methods
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(CustomPlace customPlace);

    @Delete
    void delete(CustomPlace customPlace);

    @Update
    void update(CustomPlace... customPlaces);


    // Query methods
    @Query("SELECT * FROM custom ORDER BY title ASC")
    List<CustomPlace> getAllCustomPlaces();

    @Query("SELECT * FROM custom WHERE LOWER(title) like '%' || LOWER(:title) || '%'")
    List<CustomPlace> findCustomPlaceWithTilte(String title);

    @Query("SELECT * FROM custom WHERE lat = :lat AND lng = :lng")
    CustomPlace findCustomPlaceWithLatLng(double lat, double lng);

    @Query("SELECT lat, lng, title, img_path FROM custom")
    List<MapInfo> getAllCustomMapInfo();

    @Query("DELETE FROM custom")
    void deleteAll();



}
