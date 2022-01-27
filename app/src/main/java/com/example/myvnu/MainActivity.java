package com.example.myvnu;

import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myvnu.roomdatabase.CustomPlace;
import com.example.myvnu.roomdatabase.CustomPlaceDatabase;
import com.example.myvnu.roomdatabase.Place;
import com.example.myvnu.roomdatabase.PlaceDao;
import com.example.myvnu.roomdatabase.PlaceDatabase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<CustomPlace> items;
    ConstraintLayout btnVNU;
    ConstraintLayout btnPos;
    public static String imgPath;
    boolean requested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                Const.PERMISSION_REQUEST_CODE);
        imgPath = getApplicationContext().getCacheDir().getAbsolutePath() + "/" + Const.IMAGE_FOLDER;

        initDB();
        initAll();
        List<Place> places = PlaceDatabase.getDatabase(this).placeDao().getAllDefaultPlaces();
        makeRecycleView();
        setGoToMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeRecycleView();
    }

    private void setGoToMap() {
        btnVNU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", 10.8693508);
                bundle.putDouble("lng", 106.7962367);
                intent.putExtra("data", bundle);
                startActivity(intent);
            }
        });
        btnPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();
        makeRecycleView();
    }

    private void makeRecycleView() {
        items = CustomPlaceDatabase.getDatabase(this).customPlaceDao().getAllCustomPlaces();
        if (items.size() == 0) {
            Toast.makeText(this, "Hãy khám phá những địa điểm mới\n và thêm vào bộ sưu tập của bạn.", Toast.LENGTH_SHORT).show();
        }
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new MyAdapter(this, items));
    }

    private void initAll() {
        recyclerView = findViewById(R.id.recyclerView);
        btnVNU = (ConstraintLayout) findViewById(R.id.btnVNU);
        btnPos = (ConstraintLayout) findViewById(R.id.btnPos);

    }

    private void initDB() {
        DBAction dbAction = new DBAction();
    }

    public Bitmap loadBitmapFromAsset(String fileName) {
        try {
            InputStream inputStream = getAssets().open(fileName);
            Bitmap bmp = BitmapFactory.decodeStream(inputStream);
            return bmp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Const.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                        // perform action when allow permission success
                    } else {
                        Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }


}


