package com.example.myvnu;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myvnu.roomdatabase.CustomPlace;
import com.example.myvnu.roomdatabase.CustomPlaceDatabase;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class AddNewPlaceActivity extends AppCompatActivity {

    private ImageView imageView;
    private EditText edtTitle;
    private EditText edtDescription;
    private EditText edtAddress;
    private EditText edtPhoneNumber;
    private Button btnSave;
    private Button btnCancel;
    private Bitmap bmp;
    private double lat;
    private double lng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_place);
        initUI();
        receiveImage();
        imageView.setImageBitmap(bmp);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng currentLL = new LatLng(1,1);
                saveToDatabase();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAddNew();
            }
        });
    }


    private void initUI() {
        imageView = (ImageView) findViewById(R.id.imageViewNewPlace);
        edtTitle = (EditText) findViewById(R.id.editTextNewTitle);
        edtDescription = (EditText) findViewById(R.id.editTextNewDescription);
        edtAddress = (EditText) findViewById(R.id.editTextNewAddress);
        edtPhoneNumber = (EditText) findViewById(R.id.editTextNewContact);
        btnSave = (Button) findViewById(R.id.buttonSaveNewPlace);
        btnCancel = (Button) findViewById(R.id.buttonCancelNewPlace);
    }


    private void saveToDatabase() {
        Random r = new Random();

        String title = edtTitle.getText().toString().trim();
        String imgName = title + "_" + Integer.toString(r.nextInt(1000)) + Const.DEFAULT_IMG_EXTENSION;
        String description = edtDescription.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String phoneNumber = edtPhoneNumber.getText().toString().trim();
        String checkInDate = getCurrentTime();

        // Save image to img dir
        saveImg(imgName);

        // update new record to the database
        CustomPlace customPlace = new CustomPlace(lat, lng, title, imgName, description, address, phoneNumber,checkInDate);
        CustomPlaceDatabase.getDatabase(this).customPlaceDao().insert(customPlace);
        //Toast.makeText(this, "Database Updated!", Toast.LENGTH_SHORT).show();
        returnToMapActivity();
    }


    private void cancelAddNew() {
        returnToMapActivity();
    }

    private void receiveImage(){
        Bundle bundle = getIntent().getBundleExtra("BUNDLE");

        String bmpFile = bundle.getString("CNTN19");
        lat = bundle.getDouble("lat");
        lng = bundle.getDouble("lng");
        if(bmpFile != null){
            bmp = BitmapFactory.decodeFile(bmpFile);
            if(bmp.getWidth() > bmp.getHeight()){
                Matrix matrix = new Matrix();
                matrix.postRotate(90f);
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            }
        }
    }
    private void returnToMapActivity() {
        Intent intent = new Intent(AddNewPlaceActivity.this, MapsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putDouble("lat", lat);
        bundle.putDouble("lng", lng);
        intent.putExtra("data", bundle);
        startActivity(intent);
    }

    private String getCurrentTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // MM means month, mm means minute
        Date date = new Date();
        return sdf.format(date).toString();
    }

    private void checkAndCreateDir(String path){
        File file = new File(path);
        if(!file.exists())
            file.mkdir();
    }




    private void saveImg(String imgName){
        File storageDir = getApplicationContext().getCacheDir();
        String path = storageDir.getAbsolutePath() + "/" + Const.IMAGE_FOLDER;
        File file = new File(path);
        if(!file.exists())
            file.mkdir();

        File dest = new File(path  + imgName);
        try {
            FileOutputStream out = new FileOutputStream(dest);
            bmp.compress(Bitmap.CompressFormat.JPEG, Const.JPEG_QUALITY_PERCENT, out);
            out.flush();
            out.close();
            Toast.makeText(this, "Image saved!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Save image failed!", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Const.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    private Bitmap loadBitmapFromDevice(String imgName){
        String fullPath = getApplicationContext().getCacheDir().getAbsolutePath() + "/" + Const.IMAGE_FOLDER + imgName;
        Log.d("img folder: ", fullPath);
        try {
            Bitmap b = BitmapFactory.decodeFile(fullPath);
            return b;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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

