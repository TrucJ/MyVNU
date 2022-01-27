package com.example.myvnu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.myvnu.roomdatabase.CustomPlace;
import com.example.myvnu.roomdatabase.Place;

import java.io.File;
import java.io.InputStream;

public class PlaceActivity extends AppCompatActivity {
    ImageView imageView;
    EditText title;
    EditText desc;
    EditText addr;
    EditText phone;
    EditText link;
    Button btnView;
    ImageView img;
    Bundle data;
    Place item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        initView();
        initIntent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initIntent();
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.imagePlace) ;
        btnView = (Button)findViewById(R.id.btnViewPlace);
        title = (EditText) findViewById(R.id.titlePlace);
        desc = (EditText) findViewById(R.id.descPlace);
        addr = (EditText) findViewById(R.id.addrPlace);
        phone = (EditText) findViewById(R.id.phonePlace);
        link = (EditText) findViewById(R.id.linkPlace);
        img = (ImageView)findViewById(R.id.imagePlace);
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaceActivity.this, MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", item.getLat());
                bundle.putDouble("lng", item.getLng());
                intent.putExtra("data", bundle);
                startActivity(intent);
            }
        });
    }
    private void initIntent() {
        data = getIntent().getBundleExtra("data");
        if(data != null){
            item = (Place) data.getSerializable("item");
            imageView.setImageBitmap(loadBitmapFromAsset(item.getImgPath()));
            title.setText(item.getTitle());
            desc.setText(item.getDescription());
            addr.setText(item.getAddress());
            phone.setText(item.getPhoneNumber());
            link.setText(item.getLink());
        }

    }
    private Bitmap loadBitmapFromAsset(String fileName){
        try{
            InputStream inputStream = getAssets().open(Const.ASSETS_BITMAP_FOLDER + fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}