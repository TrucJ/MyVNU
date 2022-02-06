package com.example.myvnu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myvnu.roomdatabase.Place;

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
    LinearLayout upLayout;
    LinearLayout downLayout;
    Bundle data;
    Place item;
    ImageButton btnHome;
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
    public void setUpLayout(View view){
        upLayout.setVisibility(View.INVISIBLE);
        downLayout.setVisibility(View.VISIBLE);
    }
    public void setDownLayout(View view){
        downLayout.setVisibility(View.INVISIBLE);
        upLayout.setVisibility(View.VISIBLE);
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
        upLayout = (LinearLayout)findViewById(R.id.upLayout);
        btnHome = (ImageButton)findViewById(R.id.btnHomePlace);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(PlaceActivity.this, "Về trang chủ", Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(PlaceActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        downLayout = (LinearLayout) findViewById(R.id.downLayout);
        upLayout.setOnTouchListener(new OnSwipeTouchListener(PlaceActivity.this){
            public void onSwipeTop() {
                upLayout.setVisibility(View.INVISIBLE);
                downLayout.setVisibility(View.VISIBLE);
            }
            public void onSwipeRight() {
            }
            public void onSwipeLeft() {
            }
            public void onSwipeBottom() {

            }
        });
        downLayout.setOnTouchListener(new OnSwipeTouchListener(PlaceActivity.this){
            public void onSwipeTop() {
            }
            public void onSwipeRight() {
            }
            public void onSwipeLeft() {
            }
            public void onSwipeBottom() {
                downLayout.setVisibility(View.INVISIBLE);
                upLayout.setVisibility(View.VISIBLE);
            }
        });
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

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone.getText().toString()));
                startActivity(intent);
            }
        });
    }
    private void initIntent() {
        data = getIntent().getBundleExtra("data");
        if(data != null){
            item = (Place) data.getSerializable("item");
            imageView.setImageBitmap(loadBitmapFromCache(item.getImg()));
            title.setText(item.getTitle());
            desc.setText(item.getDescription());
            addr.setText(item.getAddress());
            phone.setText(item.getPhoneNumber());
            link.setText(item.getLink());
        }

    }
    public Bitmap loadBitmapFromCache(String fileName){
        try{
            Bitmap bitmap = BitmapFactory.decodeFile(getApplicationContext().getCacheDir().getAbsolutePath() + "/places/" + fileName);
            return bitmap;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}