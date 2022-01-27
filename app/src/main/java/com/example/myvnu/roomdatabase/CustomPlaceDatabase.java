package com.example.myvnu.roomdatabase;

import android.content.Context;
import android.util.Log;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myvnu.Const;

@Database(entities = {CustomPlace.class}, version = 1, exportSchema = false)
public abstract class CustomPlaceDatabase extends RoomDatabase {

    private static CustomPlaceDatabase INSTANCE;
    public abstract CustomPlaceDao customPlaceDao();

    public static CustomPlaceDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (CustomPlaceDatabase.class){
                if (INSTANCE == null){
                    // Create database
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), CustomPlaceDatabase.class, Const.CUSTOM_DB_NAME)
                            .allowMainThreadQueries()
                            .build();

                }
            }
        }
        return INSTANCE;
    }



}
