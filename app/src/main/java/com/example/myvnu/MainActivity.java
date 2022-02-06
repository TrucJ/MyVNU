package com.example.myvnu;

import androidx.annotation.NonNull;
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
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myvnu.roomdatabase.CustomPlace;
import com.example.myvnu.roomdatabase.CustomPlaceDatabase;
import com.example.myvnu.roomdatabase.Place;
import com.example.myvnu.roomdatabase.PlaceDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<CustomPlace> items;
    ConstraintLayout btnVNU;
    ConstraintLayout btnPos;
    Button btnUpdate;
    public static String IMG_PATH;
    boolean requested;

    DatabaseReference mData;
    StorageReference storageRef;
    String CACHE_DIR;
    List<Place> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA}, Const.PERMISSION_REQUEST_CODE);
        CACHE_DIR = getApplicationContext().getCacheDir().getAbsolutePath();
        IMG_PATH = CACHE_DIR + "/" + Const.IMAGE_FOLDER;

        initViews();
        initDatabase();
        makeRecycleView();
        setGoToMap();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                startActivity(intent);
            }
        });

        // UPLOAD - for the first run -> DONOT DELETE
        /*
        places = PlaceDatabase.getDatabase(this).placeDao().getAllDefaultPlaces();
        System.out.println(places.size());
        Iterator<Place> iter = places.iterator();

        while (iter.hasNext()){
            Place place = iter.next();
            String ll = Double.toString(place.getLat()) + '+' + Double.toString(place.getLng());
            String key = ll.replace('.', 'p');
            mData.child("data").child(key).setValue(place);
        }
        */
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
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new MyAdapter(this, items));
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        btnVNU = (ConstraintLayout) findViewById(R.id.btnVNU);
        btnPos = (ConstraintLayout) findViewById(R.id.btnPos);
        btnUpdate = (Button) findViewById(R.id.buttonUpdate);
    }


    private void initDatabase() {
        //DBAction dbAction = new DBAction();
        PlaceDatabase.getDatabase(this).placeDao().findPlaceWithTitle("alo");
        if(checkEmptyDB()){
            System.out.println("DB is empty");
            populateDatabaseFromAsset();
            //currentVersion = 1.0f;
        }
        else{
            System.out.println("DB is existed");
            //currentVersion = checkVersion();
        }
    }

    private boolean checkEmptyDB() {
        String versionFile = CACHE_DIR + "/places/version.txt";
        File file = new File(versionFile);
        if(file.exists())
            return false;
        else
            return true;
    }

    private void populateDatabaseFromAsset() {
        File rootPath = new File(CACHE_DIR + "/");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        File placePath = new File(CACHE_DIR + "/places");
        if(!placePath.exists()) {
            placePath.mkdirs();
        }

        String[] files = null;
        try{
            files = getAssets().list("places");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("PRINTING FILES");
        for(String filename : files) {
            System.out.println(filename);
            InputStream in = null;
            OutputStream out = null;
            try {
                in = getAssets().open("places/" + filename);
                String outDir = CACHE_DIR + "/places";
                File outFile = new File(outDir, filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
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
                        //Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
    public void go2Discover(View view){
        Intent intent = new Intent(MainActivity.this, RecommendActivity.class);
        startActivity(intent);
    }

}