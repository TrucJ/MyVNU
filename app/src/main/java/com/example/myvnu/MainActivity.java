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
    double currentVersion, latestVersion;
    String CACHE_DIR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                Const.PERMISSION_REQUEST_CODE);
        CACHE_DIR = getApplicationContext().getCacheDir().getAbsolutePath();
        IMG_PATH = CACHE_DIR + "/" + Const.IMAGE_FOLDER;

        initAll();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("assets.zip");
        System.out.println(storageRef.toString());

        /*
        final File localFile = new File(rootPath,"update.zip");
        System.out.println(localFile.toString());

        UpdateDatabase.unzip(getApplicationContext().getCacheDir().getAbsolutePath() + "/"+"update.zip", getApplicationContext().getCacheDir().getAbsolutePath() + "/");

        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ",";local tem file created  created " +localFile.toString());
                //  updateDb(timestamp,localFile.toString(),position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ",";local tem file not created  created " +exception.toString());
            }
        });
         */
        // init Firebase DB ref and get the latest version
        mData = FirebaseDatabase.getInstance().getReference();
        mData.child("version").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                latestVersion = (Double) snapshot.getValue();
                System.out.println("Latest version = " + latestVersion);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        initDB();
        //upload();

        //List<Place> places = PlaceDatabase.getDatabase(MainActivity.this).placeDao().getAllPlaces();
        makeRecycleView();
        setGoToMap();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDatabase();
            }
        });


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
        btnUpdate = (Button) findViewById(R.id.buttonUpdate);

    }

    private void initDB() {
        DBAction dbAction = new DBAction();
        if(checkEmptyDB()){
            System.out.println("DB is empty");
            populateDB();
            currentVersion = 1.0f;
        }
        else{
            System.out.println("DB is existed");
            currentVersion = checkVersion();
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

    private double checkVersion() {
        File versionFile = new File( CACHE_DIR + "/places/version.txt");
        String v;
        try (Scanner input = new Scanner(versionFile)) {
            v = input.nextLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0.9f;
        }
        System.out.println("Local version = " + v);
        System.out.println("Cloud version = " + latestVersion);
        return Double.parseDouble(v);
    }

    private void populateDB() {
        File rootPath = new File(CACHE_DIR + "/");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
        /*
        File dbPath = new File(getApplicationContext().getCacheDir().getAbsolutePath() + "/database");
        if(!dbPath.exists()) {
            dbPath.mkdirs();
        }
        */
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

    public void upload(){
        DBAction dbAction = new DBAction();
        List<Place> places = dbAction.getAllDefaultPlaces(MainActivity.this);
        System.out.println("Size of places " + Integer.toString(places.size()));
        //mData.child("data")
    }

    public void updateDatabase(){
        System.out.println("CurrenVersion: " + Double.toString(currentVersion));
        System.out.println("LatestVersion: " + Double.toString(latestVersion));
        if(currentVersion == latestVersion){
            Toast.makeText(this, "Đây là phiên bản mới nhất.", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Đang cập nhật lên phiên bản " + Double.toString(latestVersion) + "\nVui lòng đợi.", Toast.LENGTH_SHORT).show();
            DBAction dbAction = new DBAction();
            writeVersionToTextFile(latestVersion);
            currentVersion = latestVersion;
            if(true) return;

            // Tạo cơ sở dữ liệu mới nhất
            mData.child("data").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    ArrayList<Place> places = (ArrayList<Place>) task.getResult().getValue();
                    for(int i = 0; i < places.size(); i++){
                        Place place = places.get(i);

                        dbAction.insert(MainActivity.this, place);
                    }
                }
            });

            // Lấy danh sách file ảnh mới nhất
            ArrayList<String> latestImages = new ArrayList<String>();
            latestImages.addAll(dbAction.getAllImages(MainActivity.this));

            // Lấy danh sách file ảnh local hiện tại
            ArrayList<String> currentImages = new ArrayList<String>();
            String imgPath = getApplicationContext().getCacheDir().getAbsolutePath() + "/places";
            File directory = new File(imgPath);
            File[] files = directory.listFiles();
            for(int i = 0; i < files.length; i++){
                currentImages.add(files[i].getName());
            }

            // Lấy danh sách file ảnh cần tải bổ sung
            ArrayList<String> newImages = new ArrayList<String>();
            for(int i = 0; i < latestImages.size(); i++){
                if(!currentImages.contains(latestImages.get(i)))
                    newImages.add(latestImages.get(i));
            }

            // Tải xuống các file ảnh bổ sung
            for(int i = 0; i < newImages.size(); i++){
                downloadImage(newImages.get(i));
            }

            // Cập nhật xong
            Toast.makeText(this, "Đã cập nhật xong!.", Toast.LENGTH_SHORT).show();

        }
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

    public void downloadImage(String filename) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("images/" + filename);
        StorageReference islandRef = storageRef;

        File rootPath = new File(Environment.getExternalStorageDirectory(), "places");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath,filename);

        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ",";local tem file created  created " +localFile.toString());
                //  updateDb(timestamp,localFile.toString(),position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ",";local tem file not created  created " +exception.toString());
            }
        });
    }

    public void downloadUpdatePackage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("assets.zip");
        StorageReference islandRef = storageRef;

        File rootPath = new File(Environment.getExternalStorageDirectory(), "");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath,"update.zip");

        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ",";local tem file created  created " +localFile.toString());
                //  updateDb(timestamp,localFile.toString(),position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ",";local tem file not created  created " +exception.toString());
            }
        });
    }

    public void unzip(String _zipFile, String _targetLocation) {
        System.out.println("Unzipping");
        try {
            FileInputStream fin = new FileInputStream(_zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            System.out.println(fin.toString());
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {

                //create dir if required while unzipping
                if (ze.isDirectory()) {
                    dirChecker(ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(_targetLocation + ze.getName());
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        fout.write(c);
                    }

                    zin.closeEntry();
                    fout.close();
                }

            }
            zin.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void dirChecker(String filepath) {
        File file = new File(filepath);
        if (file.exists()) {
            //Do something
        }

        else {
            file.mkdirs();
        }
    }

    public void writeVersionToTextFile(double _version){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(CACHE_DIR + "/places/version.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.println(Double.toString(_version));
        writer.close();
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