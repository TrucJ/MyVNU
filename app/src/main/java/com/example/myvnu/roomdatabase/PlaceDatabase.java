package com.example.myvnu.roomdatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myvnu.Const;

import java.io.File;

@Database(entities = {Place.class}, version = 1, exportSchema = false)
public abstract class PlaceDatabase extends RoomDatabase {


    private static PlaceDatabase INSTANCE;
    public abstract PlaceDao placeDao();

    public static PlaceDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (PlaceDatabase.class){
                if (INSTANCE == null){
                    // Create database
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), PlaceDatabase.class, Const.DEFAULT_DB_NAME)
                            .createFromAsset(Const.ASSETS_INIT_DATABASE)
                            .allowMainThreadQueries()
                    //        .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
