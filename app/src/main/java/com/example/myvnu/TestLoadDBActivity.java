package com.example.myvnu;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.myvnu.roomdatabase.Place;
import com.example.myvnu.roomdatabase.PlaceDatabase;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class TestLoadDBActivity extends AppCompatActivity {
    private ImageView imageViewTest;
    private Button buttonTest;

    List<Place> dataTest;
    Iterator<Place> cursor;
    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_load_db);
        initUI();
        initData();
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cursor.hasNext()){
                    String imgName = cursor.next().getImg();
                    Log.d("imgName: ", imgName);
                    bmp = loadBitmapFromAsset(imgName);
                    imageViewTest.setImageBitmap(bmp);
                }
            }
        });
    }

    private void initData() {
        dataTest = PlaceDatabase.getDatabase(this).placeDao().getAllDefaultPlaces();
        for(Place p : dataTest){
            Log.d("alo:", p.getTitle());
        }
        cursor = dataTest.iterator();
        bmp = null;
    }

    private void initUI() {
        imageViewTest = (ImageView) findViewById(R.id.imageViewTest);
        buttonTest = (Button) findViewById(R.id.buttonTest);
    }

    public Bitmap loadBitmapFromDevice(String imgName){
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        String fullPath = storageDir.getAbsolutePath() + "/" + imgName;
        Bitmap b = BitmapFactory.decodeFile(fullPath);
        return b;
    }

    public Bitmap loadBitmapFromAsset(String fileName){
        try{
            InputStream inputStream = getAssets().open(Const.ASSETS_BITMAP_FOLDER + fileName);
            Bitmap bmp = BitmapFactory.decodeStream(inputStream);
            return bmp;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}